package pl.bondek.sentences.writer;

import org.apache.commons.text.StringEscapeUtils;
import pl.bondek.sentences.Sentence;

import javax.xml.stream.*;
import java.io.IOException;
import java.io.OutputStream;

public class XmlSentencesWriter implements SentencesWriter {

    private XMLEventWriter writer;
    private XMLEventFactory eventFactory;

    public XmlSentencesWriter(OutputStream os) {
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
            outputFactory.setProperty("escapeCharacters", false);

            eventFactory = XMLEventFactory.newInstance();
            writer = outputFactory.createXMLEventWriter(os);

            writer.add(eventFactory.createStartDocument("UTF-8", "1.0", true));
            writer.add(eventFactory.createCharacters("\n"));
            writer.add(eventFactory.createStartElement("", "", "text"));
            writer.add(eventFactory.createCharacters("\n"));
        } catch (XMLStreamException ex) {
            throw new SentencesWriterException(ex);
        }
    }

    @Override
    public synchronized void writeSentence(Sentence sentence) {
        try {
            writer.add(eventFactory.createStartElement("", "", "sentence"));
            sentence.getWords().forEach((word) -> {
                try {
                    writer.add(eventFactory.createStartElement("", "", "word"));
                    writer.add(eventFactory.createCharacters(StringEscapeUtils.escapeXml11(word)));
                    writer.add(eventFactory.createEndElement("", "", "word"));
                } catch (XMLStreamException ex) {
                    throw new SentencesWriterException(ex);
                }
            });
            writer.add(eventFactory.createEndElement("", "", "sentence"));
            writer.add(eventFactory.createCharacters("\n"));
        } catch (XMLStreamException ex) {
            throw new SentencesWriterException(ex);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (writer != null) {
            try {
                writer.add(eventFactory.createEndElement("", "", "text"));
                writer.add(eventFactory.createEndDocument());
                writer.close();
                writer = null;
            } catch (XMLStreamException ex) {
                throw new SentencesWriterException(ex);
            }
        }
    }

}
