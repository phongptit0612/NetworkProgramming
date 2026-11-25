package vn.edu.network.common.protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Length-prefixed UTF-8 fields: TCP reads may split or combine messages. */
public final class Protocol {
    public static final int VERSION = 1;
    public static final int MAX_FIELD_BYTES = 1_048_576;
    public static final int MAX_KEY_BYTES = 1_024;
    private static final int MAX_WINNERS = MAX_FIELD_BYTES;

    private Protocol() { }

    public static void writeRequest(DataOutputStream out, AnalysisRequest request) throws IOException {
        // Validate all fields before writing any bytes to keep the connection usable on input errors.
        byte[] ciphertext = encode(request.ciphertext(), MAX_FIELD_BYTES);
        byte[] key = encode(request.key(), MAX_KEY_BYTES);
        out.writeInt(VERSION);
        writeBytes(out, ciphertext);
        writeBytes(out, key);
        out.flush();
    }

    public static AnalysisRequest readRequest(DataInputStream in) throws IOException {
        checkVersion(in.readInt());
        return new AnalysisRequest(readString(in, MAX_FIELD_BYTES), readString(in, MAX_KEY_BYTES));
    }

    public static void writeResponse(DataOutputStream out, AnalysisResponse response) throws IOException {
        out.writeInt(VERSION);
        out.writeBoolean(response.success());
        writeBytes(out, encode(response.message(), MAX_FIELD_BYTES));
        out.writeInt(response.count());
        out.writeInt(response.characters().size());
        for (String character : response.characters()) {
            writeBytes(out, encode(character, 4));
        }
        out.flush();
    }

    public static AnalysisResponse readResponse(DataInputStream in) throws IOException {
        checkVersion(in.readInt());
        boolean success = in.readBoolean();
        String message = readString(in, MAX_FIELD_BYTES);
        int count = in.readInt();
        int size = in.readInt();
        if (count < 0 || size < 0 || size > MAX_WINNERS) {
            throw new IOException("Ket qua thong ke khong hop le.");
        }
        List<String> characters = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            characters.add(readString(in, 4));
        }
        return new AnalysisResponse(success, message, characters, count);
    }

    private static byte[] encode(String value, int limit) throws IOException {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > limit) {
            throw new IOException("Du lieu vuot gioi han " + limit + " bytes.");
        }
        return bytes;
    }

    private static void writeBytes(DataOutputStream out, byte[] bytes) throws IOException {
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    private static String readString(DataInputStream in, int limit) throws IOException {
        int length = in.readInt();
        if (length < 0 || length > limit) {
            throw new IOException("Do dai truong du lieu khong hop le: " + length);
        }
        byte[] bytes = in.readNBytes(length);
        if (bytes.length != length) {
            throw new EOFException("Ket noi dong khi chua nhan du du lieu.");
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static void checkVersion(int version) throws IOException {
        if (version != VERSION) {
            throw new IOException("Phien ban giao thuc khong duoc ho tro: " + version);
        }
    }
}
