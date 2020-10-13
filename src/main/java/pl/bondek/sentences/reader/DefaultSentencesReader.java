package pl.bondek.sentences.reader;

import pl.bondek.sentences.Sentence;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DefaultSentencesReader implements SentencesReader {

    public static final Comparator<String> WORDS_COMPARATOR = new WordsComparator();

    private final List<String> specialWords;
    private final Tokenizer tokenizer;
    private final Scanner scanner;

    private List<Token> bufferedTokens = Collections.emptyList();

    public DefaultSentencesReader(InputStream is) {
        this(is, Collections.emptyList());
    }

    public DefaultSentencesReader(InputStream is, List<String> specialWords) {
        this.scanner = new Scanner(new BufferedReader(new InputStreamReader(is)));
        this.tokenizer = new Tokenizer();
        this.specialWords = specialWords;
    }

    private List<Token> nextTokens() {
        if (bufferedTokens.isEmpty()) {
            return tokenizer.tokenize(scanner.nextLine());
        }

        List<Token> tokens = bufferedTokens;
        bufferedTokens = Collections.emptyList();
        return tokens;
    }

    @Override
    public Stream<Sentence> sentencesStream() {
        Spliterator<Sentence> spliterator = Spliterators.spliterator(
                this, Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL);
        Stream<Sentence> stream = StreamSupport.stream(spliterator, false);

        return stream;
    }

    @Override
    public synchronized boolean hasNext() {
        return !bufferedTokens.isEmpty() || scanner.hasNextLine();
    }

    @Override
    public synchronized Sentence next() {
        List<String> sentenceWords = new LinkedList<>();
        String lastToken = "";

        while (hasNext()) {
            List<Token> tokens = nextTokens();
            int idx = 1;
            for (Token token : tokens) {
                if (token.isWord()) {
                    sentenceWords.add(token.getText());
                } else {
                    String specialToken = lastToken + token.getText();
                    if (isSpecialWord(specialToken)) {
                        sentenceWords.set(sentenceWords.size() - 1, specialToken);
                    } else if (token.isSentenceEnd() && !sentenceWords.isEmpty()) {
                        Sentence sentence = createSentence(sentenceWords);
                        bufferedTokens = tokens.subList(idx, tokens.size());
                        return sentence;
                    }
                }
                lastToken = token.getText();
                idx++;
            }
        }

        if (!sentenceWords.isEmpty()) {
            Sentence sentence = createSentence(sentenceWords);
            return sentence;
        }

        throw new NoSuchElementException("End of source reached.");
    }

    private boolean isSpecialWord(String scannedWord) {
        return specialWords.contains(scannedWord.toLowerCase());
    }

    private Sentence createSentence(List<String> sentenceWords) {
        sentenceWords.sort(WORDS_COMPARATOR);
        return new Sentence(sentenceWords);
    }

    @Override
    public void close() throws IOException {
        scanner.close();
    }

    /**
     * Default ignore case string comparator with opposite order of of the same words in different case.
     */
    public static class WordsComparator implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            int result = s1.compareToIgnoreCase(s2);
            if (result == 0) {
                result = -s1.compareTo(s2);
            }
            return result;
        }
    }
}
