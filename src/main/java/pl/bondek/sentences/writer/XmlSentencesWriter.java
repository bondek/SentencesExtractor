package pl.bondek.sentences.writer;

import org.apache.commons.text.StringEscapeUtils;
import pl.bondek.sentences.Sentence;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;

public class XmlSentencesWriter implements SentencesWriter {

    private XMLStreamWriter writer;
    private OutputStream os;

    public XmlSentencesWriter(OutputStream os) {
        this.os = os;
        try {
            XMLOutputFactory outputFactory = XMLOutputFactory.newFactory();
            outputFactory.setProperty("escapeCharacters", false);
            writer = outputFactory.createXMLStreamWriter(os);
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n");
            writer.writeStartElement("text");
            writer.writeCharacters("\n");
        } catch (XMLStreamException ex) {
            throw new SentencesWriterException(ex);
        }
    }

    @Override
    public synchronized void writeSentence(Sentence sentence) {
        try {
            writer.writeStartElement("sentence");
            sentence.getWords().forEach((word) -> {
                try {
                    writer.writeStartElement("word");
                    writer.writeCharacters(StringEscapeUtils.escapeXml11(word));
                    writer.writeEndElement();
                } catch (XMLStreamException ex) {
                    throw new SentencesWriterException(ex);
                }
            });
            writer.writeEndElement();
            writer.writeCharacters("\n");
        } catch (XMLStreamException ex) {
            throw new SentencesWriterException(ex);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        if (writer != null) {
            try {
                writer.writeEndElement();
                writer.writeEndDocument();
                writer.close();
                writer = null;
            } catch (XMLStreamException ex) {
                throw new SentencesWriterException(ex);
            }
        }
    }

}
