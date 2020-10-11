package pl.bondek.sentences.writer;

import com.opencsv.CSVWriter;
import pl.bondek.sentences.Sentence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CsvSentencesWriter implements SentencesWriter {

    public static final int COPY_BUFFER_SIZE = 1024;

    private OutputStream os;
    private CSVWriter writer;
    private Path tempFilePath;
    private int lineNo = 1;
    private int maxColumns = 0;

    public CsvSentencesWriter(OutputStream os) {
        this.os = os;
        try {
            tempFilePath = Files.createTempFile("Sentences-", ".tmp");
            tempFilePath.toFile().deleteOnExit();
            FileWriter tempFileWriter = new FileWriter(tempFilePath.toFile());
            writer = new CSVWriter(tempFileWriter);
        } catch (IOException ex) {
            try {
                os.close();
            } catch (IOException exIn) {
                throw new SentencesWriterException(exIn);
            }
            throw new SentencesWriterException(ex);
        }
    }

    public synchronized void writeSentence(Sentence sentence) {
        List<String> lineAsList = new LinkedList<>();
        lineAsList.add("Sentence " + lineNo++);
        lineAsList.addAll(sentence.getWords());

        if (sentence.getWords().size() > maxColumns) {
            maxColumns = sentence.getWords().size();
        }

        String[] line = lineAsList.toArray(new String[] {});
        writer.writeNext(line);
    }

    public synchronized void close() throws IOException {
        if (writer != null) {
            writer.close();

            os.write(",".getBytes());

            for (int i = 1; i < maxColumns + 1; i++) {
                os.write(("," + "Word " + i).getBytes());
            }

            try (InputStream is = new FileInputStream(tempFilePath.toFile())) {
                copyStream(is, os);
            }

            os.close();
        }

        writer = null;
    }

    private void copyStream(InputStream is, OutputStream os) throws IOException {
        int n;
        byte[] buffer = new byte[COPY_BUFFER_SIZE];
        while ((n = is.read(buffer)) > -1) {
            os.write(buffer, 0, n);
        }
    }
}
