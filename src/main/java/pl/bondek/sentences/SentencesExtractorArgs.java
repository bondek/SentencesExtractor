package pl.bondek.sentences;

public class SentencesExtractorArgs {

    private final String outputType;
    private final String specialWordsPath;

    public SentencesExtractorArgs(String outputType, String specialWordsPath) {
        this.outputType = outputType;
        this.specialWordsPath = specialWordsPath;
    }

    public String getOutputType() {
        return outputType;
    }

    public String getSpecialWordsPath() {
        return specialWordsPath;
    }

}
