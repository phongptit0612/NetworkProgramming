package vn.edu.network.common;

import org.junit.jupiter.api.Test;
import vn.edu.network.common.analysis.CharacterFrequency;
import vn.edu.network.common.crypto.VigenereCipher;
import vn.edu.network.common.protocol.AnalysisRequest;
import vn.edu.network.common.protocol.Protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoreTest {
    @Test
    void matchesKnownVigenereVector() {
        assertEquals("LXFOPVEFRNHR", VigenereCipher.encrypt("ATTACKATDAWN", "LEMON"));
        assertEquals("ATTACKATDAWN", VigenereCipher.decrypt("LXFOPVEFRNHR", "LEMON"));
        assertEquals("LELKRY", VigenereCipher.encrypt("BANANA", "KEY"));
    }

    @Test
    void preservesCaseUnicodeAndPunctuation() {
        String text = "Xin chào, Việt Nam! 😀";
        assertEquals(text, VigenereCipher.decrypt(VigenereCipher.encrypt(text, "Key"), "Key"));
        assertEquals("B-b!", VigenereCipher.encrypt("A-a!", "B"));
    }

    @Test
    void rejectsEmptyOrNonAlphabeticKeys() {
        for (String key : new String[] {"", "  ", "K3Y", "khóa"}) {
            assertThrows(IllegalArgumentException.class, () -> VigenereCipher.encrypt("ABC", key));
        }
    }

    @Test
    void countsTiesAndUnicodeWithoutCountingWhitespace() {
        assertEquals(new CharacterFrequency.Result(List.of("A"), 3), CharacterFrequency.analyze("BANANA"));
        assertEquals(new CharacterFrequency.Result(List.of("A", "B"), 2), CharacterFrequency.analyze("AB BA"));
        assertEquals(new CharacterFrequency.Result(List.of("😀"), 2), CharacterFrequency.analyze("😀 á 😀"));
        assertEquals(new CharacterFrequency.Result(List.of(), 0), CharacterFrequency.analyze(" \t\n\u00a0"));
        assertEquals(new CharacterFrequency.Result(List.of("a"), 2), CharacterFrequency.analyze("aAa"));
    }

    @Test
    void preservesMessageBoundariesAndUtf8BeyondWriteUtfLimit() throws IOException {
        var bytes = new ByteArrayOutputStream();
        var out = new DataOutputStream(bytes);
        var first = new AnalysisRequest("á".repeat(40_000), "KEY");
        var second = new AnalysisRequest("LELKRY", "KEY");
        Protocol.writeRequest(out, first);
        Protocol.writeRequest(out, second);
        var in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        assertEquals(first, Protocol.readRequest(in));
        assertEquals(second, Protocol.readRequest(in));
        assertEquals(0, in.available());
    }

    @Test
    void rejectsMalformedAndTruncatedFrames() throws IOException {
        for (int length : new int[] {-1, Protocol.MAX_FIELD_BYTES + 1, 10}) {
            var bytes = new ByteArrayOutputStream();
            var out = new DataOutputStream(bytes);
            out.writeInt(Protocol.VERSION);
            out.writeInt(length);
            var in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));
            assertThrows(IOException.class, () -> Protocol.readRequest(in));
        }
    }
}
