package pl.bondek.sentences;

import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xml.sax.SAXException;
import pl.bondek.sentences.writer.SentencesWriterFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CsvSentencesExtractorTest extends TestCase {
    private String inputFile;
    private String outputFile;
    private String expectedFile;

    public CsvSentencesExtractorTest(String inputFile, String outputFile, String expectedFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.expectedFile = expectedFile;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> sourceFiles() {
        return Arrays.asList(new Object[][] {
            { "reference-input.txt", "reference-output.csv", "reference-expected.csv"},
            { "empty-input.txt", "empty-output.csv", "empty-expected.csv" },
            { "no-final-dot-input.txt", "no-final-dot-output.csv", "no-final-dot-expected.csv" }
        });
    }

    @Test
    public void testExtractSentences() throws IOException, SAXException {
        Files.createDirectories(Paths.get("target/test"));
        String outputFilePath = "target/test/" + outputFile;

        InputStream inputFileStream = this.getClass().getResourceAsStream("/" + inputFile);
        OutputStream outputFileStream = new FileOutputStream(outputFilePath);

        SentencesExtractorArgs args = new SentencesExtractorArgs(SentencesWriterFactory.CSV, "src/main/resources/special-words.txt");
        SentencesExtractorApp app = new SentencesExtractorApp(args, inputFileStream, outputFileStream);
        app.extractSentences();

        assertEquals(FileUtils.readLines(new File("src/test/resources/" + expectedFile), "UTF-8"),
                FileUtils.readLines(new File(outputFilePath), "UTF-8"));
    }

}
