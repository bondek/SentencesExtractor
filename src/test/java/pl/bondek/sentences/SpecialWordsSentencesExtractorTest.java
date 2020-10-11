package pl.bondek.sentences;

import junit.framework.TestCase;
import org.custommonkey.xmlunit.XMLAssert;
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
public class SpecialWordsSentencesExtractorTest extends TestCase {
    private String inputFile;
    private String outputFile;
    private String expectedFile;

    public SpecialWordsSentencesExtractorTest(String inputFile, String outputFile, String expectedFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.expectedFile = expectedFile;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> sourceFiles() {
        return Arrays.asList(new Object[][] {
            { "reference-input.txt", "special-words-output.xml", "special-words-expected.xml" }
        });
    }

    @Test
    public void testExtractSentences() throws IOException, SAXException {
        Files.createDirectories(Paths.get("target/test"));
        String outputFilePath = "target/test/" + outputFile;

        InputStream inputFileStream = this.getClass().getResourceAsStream("/" + inputFile);
        OutputStream outputFileStream = new FileOutputStream(outputFilePath);

        SentencesExtractorArgs args = new SentencesExtractorArgs(SentencesWriterFactory.XML, "src/test/resources/special-words-test.txt");
        SentencesExtractorApp app = new SentencesExtractorApp(args, inputFileStream, outputFileStream);
        app.extractSentences();

        InputStream expectedFileStream = this.getClass().getResourceAsStream("/" + expectedFile);
        XMLAssert.assertXMLEqual(new InputStreamReader(expectedFileStream), new FileReader(outputFilePath));
    }

}
