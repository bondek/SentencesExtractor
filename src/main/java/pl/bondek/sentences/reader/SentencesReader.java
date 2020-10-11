package pl.bondek.sentences.reader;

import pl.bondek.sentences.Sentence;

import java.io.Closeable;
import java.util.Iterator;
import java.util.stream.Stream;

public interface SentencesReader extends Iterator<Sentence>, Closeable {
    Stream<Sentence> sentencesStream();
}
