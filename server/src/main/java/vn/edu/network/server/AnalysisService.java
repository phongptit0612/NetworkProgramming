package vn.edu.network.server;

import vn.edu.network.common.analysis.CharacterFrequency;
import vn.edu.network.common.crypto.VigenereCipher;
import vn.edu.network.common.protocol.AnalysisRequest;
import vn.edu.network.common.protocol.AnalysisResponse;

/** Application logic is separate from sockets so a JDBC repository can be added here later. */
public final class AnalysisService {
    public AnalysisResponse analyze(AnalysisRequest request) {
        String plaintext = VigenereCipher.decrypt(request.ciphertext(), request.key());
        CharacterFrequency.Result result = CharacterFrequency.analyze(plaintext);
        return new AnalysisResponse(true, "Da giai ma va thong ke.", result.characters(), result.count());
    }
}
