package pl.bondek.sentences;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bondek.sentences.reader.DefaultSentencesReader;
import pl.bondek.sentences.reader.SentencesReader;
import pl.bondek.sentences.writer.SentencesWriter;
import pl.bondek.sentences.writer.SentencesWriterFactory;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SentencesExtractorApp {

    private static final Logger logger = LoggerFactory.getLogger(SentencesExtractorApp.class);

    private final SentencesExtractorArgs extractorArgs;
    private final InputStream is;
    private final OutputStream os;

    public SentencesExtractorApp(SentencesExtractorArgs extractorArgs, InputStream is, OutputStream os) {
        this.extractorArgs = extractorArgs;
        this.is = is;
        this.os = os;
    }

    public void extractSentences() {
        Stream<Sentence> sentencesStream = readSentencesStream();
        writeSentencesStream(sentencesStream);

        logger.info("Sentences extracted and written to stdout successfully.");
    }

    private void writeSentencesStream(Stream<Sentence> sentencesStream) {
        SentencesWriterFactory writersFactory = new SentencesWriterFactory();
        try (SentencesWriter writer = writersFactory.createWriter(extractorArgs.getOutputType(), os)) {
            sentencesStream.forEach(writer::writeSentence);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new SentencesExtractorException(ex);
        }
    }

    private Stream<Sentence> readSentencesStream() {
        try {
            List<String> specialWords = readSpecialWords();

            logger.info("Reading text from stdin and writing to stdout.");

            SentencesReader sentencesReader = new DefaultSentencesReader(is, specialWords);
            Stream<Sentence> sentencesStream = sentencesReader.sentencesStream();
            return sentencesStream;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new SentencesExtractorException(ex);
        }
    }

    private List<String> readSpecialWords() {
        try {
            InputStream specialWordsIs;
            if (extractorArgs.getSpecialWordsPath() == null || extractorArgs.getSpecialWordsPath().isEmpty()) {
                logger.info("Reading special words from: classpath:special-words.txt");
                specialWordsIs = this.getClass().getClassLoader().getResourceAsStream("special-words.txt");
            } else {
                logger.info("Reading special words from: file:{}", extractorArgs.getSpecialWordsPath());
                specialWordsIs = new BufferedInputStream(new FileInputStream(extractorArgs.getSpecialWordsPath()));
            }

            List<String> specialWords = new BufferedReader(new InputStreamReader(specialWordsIs)).lines()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            return specialWords;
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw new SentencesExtractorException(ex);
        }
    }

    public static void main(String[] args) {
        SentencesExtractorArgs extractorArgs = parseArgs(args);

        SentencesExtractorApp app = new SentencesExtractorApp(extractorArgs, System.in, System.out);
        app.extractSentences();
    }

    private static SentencesExtractorArgs parseArgs(String[] args) {
        Options options = new Options();

        Option output = new Option("o", "output-format", true, "output format type (XML [default], CSV)");
        output.setRequired(false);
        options.addOption(output);

        Option specialWords = new Option("s", "special-words-file", true, "path to special words file");
        specialWords.setRequired(false);
        options.addOption(specialWords);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException ex) {
            logger.error(ex.getMessage(), ex);
            formatter.printHelp("SentencesExtractor", options);
            System.exit(1);
            return null;
        }

        String outputFormat = cmd.getOptionValue('o', "XML");
        String specialWordsPath = cmd.getOptionValue('s', "");
        SentencesExtractorArgs extractorArgs = new SentencesExtractorArgs(outputFormat, specialWordsPath);
        return extractorArgs;
    }

}
