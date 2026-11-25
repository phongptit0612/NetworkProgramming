package vn.edu.network.client;

import vn.edu.network.common.crypto.VigenereCipher;
import vn.edu.network.common.protocol.AnalysisRequest;
import vn.edu.network.common.protocol.AnalysisResponse;
import vn.edu.network.common.protocol.Protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/** Reusable by a future Swing UI; call networking methods from a background thread. */
public final class AnalysisClient implements AutoCloseable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public AnalysisClient(String host, int port) throws IOException {
        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port), 5_000);
            socket.setSoTimeout(15_000);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException | RuntimeException e) {
            socket.close();
            throw e;
        }
    }

    public synchronized AnalysisResponse analyze(String plaintext, String key) throws IOException {
        String ciphertext = VigenereCipher.encrypt(plaintext, key);
        Protocol.writeRequest(out, new AnalysisRequest(ciphertext, key));
        return Protocol.readResponse(in);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
