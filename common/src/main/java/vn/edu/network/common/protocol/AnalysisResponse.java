package vn.edu.network.common.protocol;

import java.util.List;

public record AnalysisResponse(boolean success, String message, List<String> characters, int count) {
    public AnalysisResponse {
        characters = List.copyOf(characters);
    }

    public static AnalysisResponse error(String message) {
        return new AnalysisResponse(false, message, List.of(), 0);
    }
}
