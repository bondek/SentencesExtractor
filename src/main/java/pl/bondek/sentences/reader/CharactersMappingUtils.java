package pl.bondek.sentences.reader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CharactersMappingUtils {
    public static final Map<String, String> charactersMap = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("’", "'");
    }});

    public static String replace(String str) {
        String output = str;
        for (Map.Entry<String, String> entry : charactersMap.entrySet()) {
            output = output.replace(entry.getKey(), entry.getValue());
        }
        return output;
    }
}
