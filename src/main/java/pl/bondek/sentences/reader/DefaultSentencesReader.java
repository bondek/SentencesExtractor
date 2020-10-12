package pl.bondek.sentences.reader;

import pl.bondek.sentences.Sentence;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DefaultSentencesReader implements SentencesReader {

    public static final String SENTENCES_DELIMITER = "[.?!]";
    public static final Pattern SENTENCES_PATTERN = Pattern.compile(SENTENCES_DELIMITER);
    public static final String WHITESPACE_WORDS_DELIMITER = "\\s+";
    public static final String WORDS_DELIMITER = "[^\\pL\\p{Mn}\\p{Nd}\\p{Pc}'’]+";
    public static final Comparator<String> WORDS_COMPARATOR = new WordsComparator();

    private final List<String> specialWords;
    private final Scanner scanner;

    private String bufferedWord;

    public DefaultSentencesReader(InputStream is) {
        this(is, Collections.emptyList());
    }

    public DefaultSentencesReader(InputStream is, List<String> specialWords) {
        this.scanner = new Scanner(new BufferedReader(new InputStreamReader(is)));
        this.scanner.useDelimiter(WHITESPACE_WORDS_DELIMITER);
        this.specialWords = specialWords;
    }

    private String nextWord() {
        if (bufferedWord != null) {
            return bufferedWord;
        }

        return scanner.next();
    }

    @Override
    public Stream<Sentence> sentencesStream() {
        Spliterator<Sentence> spliterator = Spliterators.spliterator(
                this, Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.NONNULL);
        Stream<Sentence> stream = StreamSupport.stream(spliterator, false);

        return stream;
    }

    @Override
    public boolean hasNext() {
        return bufferedWord != null || scanner.hasNext();
    }

    @Override
    public Sentence next() {
        List<String> sentenceWords = new LinkedList<>();

        while (hasNext()) {
            String scannedWord = nextWord();
            if (isSpecialWord(scannedWord)) {
                sentenceWords.add(scannedWord);
            } else {
                Matcher m = SENTENCES_PATTERN.matcher(scannedWord);
                if (m.find()) {
                    if (m.start() != 0) {
                        String charsToSentenceEnd = scannedWord.substring(0, m.start());
                        List<String> wordsToSentenceEnd = splitIntoWords(charsToSentenceEnd);
                        sentenceWords.addAll(wordsToSentenceEnd);
                    }

                    scannedWord = scannedWord.substring(m.end());

                    if (!scannedWord.isEmpty()) {
                        bufferedWord = scannedWord;
                    }

                    return createSentence(sentenceWords);
                } else {
                    List<String> wordsToSentenceEnd = splitIntoWords(scannedWord);
                    sentenceWords.addAll(wordsToSentenceEnd);
                }
            }
        }

        if (!sentenceWords.isEmpty()) {
            return createSentence(sentenceWords);
        }

        throw new NoSuchElementException("End of source reached.");
    }

    private List<String> splitIntoWords(String scannedWord) {
        return Arrays.asList(scannedWord.split(WORDS_DELIMITER))
                .stream()
                .filter(s -> !s.isEmpty())
                .map(CharactersMappingUtils::replace)
                .collect(Collectors.toList());
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
