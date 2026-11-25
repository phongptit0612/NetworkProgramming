package vn.edu.network.common.analysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CharacterFrequency {
    private CharacterFrequency() { }

    /** Case-sensitive Unicode code points; ignores whitespace; ties retain first-seen order. */
    public static Result analyze(String text) {
        Map<Integer, Integer> counts = new LinkedHashMap<>();
        text.codePoints()
                .filter(cp -> !Character.isWhitespace(cp) && !Character.isSpaceChar(cp))
                .forEach(cp -> counts.merge(cp, 1, Integer::sum));
        int max = counts.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        List<String> characters = new ArrayList<>();
        counts.forEach((cp, count) -> {
            if (count == max) {
                characters.add(new String(Character.toChars(cp)));
            }
        });
        return new Result(characters, max);
    }

    public record Result(List<String> characters, int count) {
        public Result {
            characters = List.copyOf(characters);
        }
    }
}
