package pl.bondek.sentences.reader;

public class Token {
    enum Type {
        WORD,
        SENTENCE_END,
        OTHER
    }

    private final String text;
    private final Type type;

    public Token(String text, Type type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public Type getType() {
        return type;
    }

    public boolean isWord() {
        return Type.WORD == type;
    }

    public boolean isSentenceEnd() {
        return Type.SENTENCE_END == type;
    }

    public boolean isOther() {
        return Type.OTHER == type;
    }

}
