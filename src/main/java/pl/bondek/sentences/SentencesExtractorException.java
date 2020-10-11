package pl.bondek.sentences;

public class SentencesExtractorException extends RuntimeException {

    public SentencesExtractorException(String message) {
        super(message);
    }

    public SentencesExtractorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SentencesExtractorException(Throwable cause) {
        super(cause);
    }

}
