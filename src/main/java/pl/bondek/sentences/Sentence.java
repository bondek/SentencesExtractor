package pl.bondek.sentences;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Sentence domain class containing list of words.
 *
 * Order of words is important and it does matter for hashCode() and equals() methods.
 */
public class Sentence {

    private final List<String> words;

    public Sentence(List<String> words) {
        this.words = Collections.unmodifiableList(words);
    }

    public List<String> getWords() {
        return words;
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public int length() {
        return words.size();
    }

    /**
     * Two sentences are equal when they contain the same words in the same order.
     * In this application sentences are build from sorted list of words.
     *
     * @param o other object to compare
     * @return true if other object is equal and false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sentence sentence = (Sentence) o;
        return words.equals(sentence.words);
    }

    /**
     * Hashcode is generated based on each word hash code.
     * Hashcode is different if sentence have the same words in different order.
     * @return hash code generated from words list content
     */
    @Override
    public int hashCode() {
        return Objects.hash(words);
    }
}
