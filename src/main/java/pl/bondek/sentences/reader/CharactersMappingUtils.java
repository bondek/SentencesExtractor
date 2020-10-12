package pl.bondek.sentences.reader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CharactersMappingUtils {
    public static final Map<Character, Character> charactersMap = Collections.unmodifiableMap(new HashMap<Character, Character>() {{
        put('’', '\'');
    }});

    public static Character map(Character c) {
        return charactersMap.getOrDefault(c, c);
    }

    public static String replace(String str) {
        String output = str;
        for (Map.Entry<Character, Character> entry : charactersMap.entrySet()) {
            output = output.replace(entry.getKey(), entry.getValue());
        }
        return output;
    }
}
