package pl.bondek.sentences.writer;

public class SentencesWriterException extends RuntimeException {

    public SentencesWriterException(String message) {
        super(message);
    }

    public SentencesWriterException(String message, Throwable cause) {
        super(message, cause);
    }

    public SentencesWriterException(Throwable cause) {
        super(cause);
    }

}
