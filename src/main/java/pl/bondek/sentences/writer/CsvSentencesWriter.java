package pl.bondek.sentences.writer;

import com.opencsv.CSVWriter;
import org.apache.commons.io.IOUtils;
import pl.bondek.sentences.Sentence;

import java.io.*;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CsvSentencesWriter implements SentencesWriter {

    private OutputStream os;
    private CSVWriter writer;
    private File tempFile;
    private int lineNo = 1;
    private int maxColumns = 0;

    public CsvSentencesWriter(OutputStream os) {
        this.os = os;
        try {
            tempFile = Files.createTempFile("Sentences-", ".tmp").toFile();
            tempFile.deleteOnExit();

            FileWriter tempFileWriter = new FileWriter(tempFile);
            writer = new CSVWriter(tempFileWriter);
        } catch (IOException ex) {
            try {
                os.close();
            } catch (IOException exIn) {
                throw new SentencesWriterException(exIn);
            } finally {
                if (tempFile != null) {
                    tempFile.delete();
                }
            }
            throw new SentencesWriterException(ex);
        }
    }

    public synchronized void writeSentence(Sentence sentence) {
        List<String> lineAsList = new LinkedList<>();
        lineAsList.add("Sentence " + lineNo++);
        lineAsList.addAll(sentence.getWords().stream().map(s -> " " + s).collect(Collectors.toList()));

        if (sentence.getWords().size() > maxColumns) {
            maxColumns = sentence.getWords().size();
        }

        String[] line = lineAsList.toArray(new String[] {});
        writer.writeNext(line, false);
    }

    public synchronized void close() throws IOException {
        if (writer != null) {
            writer.close();

            addCsvHeaderToStream();

            try (InputStream is = new FileInputStream(tempFile)) {
                IOUtils.copy(is, os);
            }

            os.close();
            tempFile.delete();
        }

        writer = null;
    }

    private void addCsvHeaderToStream() throws IOException {
        for (int i = 1; i < maxColumns + 1; i++) {
            os.write(("," + " Word " + i).getBytes());
        }

        if (maxColumns > 0) {
            os.write("\n".getBytes());
        }
    }

}
