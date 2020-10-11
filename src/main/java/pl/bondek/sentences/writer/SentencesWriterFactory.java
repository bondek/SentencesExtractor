package pl.bondek.sentences.writer;

import java.io.OutputStream;

public class SentencesWriterFactory {

    public static final String CSV = "CSV";
    public static final String XML = "XML";

    public SentencesWriter createWriter(String type, OutputStream os) {
        switch (type) {
            case CSV:
                return new CsvSentencesWriter(os);
            case XML:
                return new XmlSentencesWriter(os);
            default:
                throw new IllegalArgumentException("Unrecognized writer type: " + type);
        }
    }

}
