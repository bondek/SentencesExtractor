package pl.bondek.sentences.writer;

import pl.bondek.sentences.Sentence;

import java.io.Closeable;

public interface SentencesWriter extends Closeable {

    void writeSentence(Sentence s);

}
