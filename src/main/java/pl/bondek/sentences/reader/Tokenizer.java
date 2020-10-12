package pl.bondek.sentences.reader;

import java.util.*;

public class Tokenizer {

    private static final List<Character> SENTENCE_END = Arrays.asList('.', '?', '!');

    private boolean isWhitespace(char c) {
        return Character.isWhitespace(c) || Character.getType(c) == 12;
    }

    private boolean isSentenceEnd(char c) {
        return SENTENCE_END.contains(c);
    }

    private boolean isWordChar(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '\'' || c == '’';
    }

    public List<Token> tokenize(String s) {
        List<Token> tokens = new ArrayList<>();
        StringBuffer wordBuffer = new StringBuffer();

        for (char c : s.toCharArray()) {
            if (isWhitespace(c)) {
                tokenFromBuffer(tokens, wordBuffer);
            } else if (isWordChar(c)) {
                wordBuffer.append(CharactersMappingUtils.map(c));
            } else if (isSentenceEnd(c)) {
                tokenFromBuffer(tokens, wordBuffer);
                tokens.add(new Token(String.valueOf(c), Token.Type.SENTENCE_END));
            } else {
                tokenFromBuffer(tokens, wordBuffer);
                tokens.add(new Token(String.valueOf(c), Token.Type.OTHER));
            }
        }

        return tokens;
    }

    private void tokenFromBuffer(List<Token> tokens, StringBuffer wordBuffer) {
        if (wordBuffer.length() > 0) {
            tokens.add(new Token(wordBuffer.toString(), Token.Type.WORD));
            wordBuffer.setLength(0);
        }
    }
}
