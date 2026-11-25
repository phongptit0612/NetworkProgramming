package vn.edu.network.server;

import org.junit.jupiter.api.Test;
import vn.edu.network.common.crypto.VigenereCipher;
import vn.edu.network.common.protocol.AnalysisRequest;
import vn.edu.network.common.protocol.AnalysisResponse;
import vn.edu.network.common.protocol.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TcpServerTest {
    @Test
    void servesMultipleClientsAndRecoversAfterInvalidKey() throws Exception {
        var executor = Executors.newSingleThreadExecutor();
        try (var server = new TcpServer("127.0.0.1", 0)) {
            var running = executor.submit(() -> {
                server.serve();
                return null;
            });
            try (var first = new Socket("127.0.0.1", server.port());
                 var second = new Socket("127.0.0.1", server.port())) {
                first.setSoTimeout(3_000);
                second.setSoTimeout(3_000);
                assertFalse(send(first, new AnalysisRequest("ABC", "")).success());
                var response = send(first, new AnalysisRequest(VigenereCipher.encrypt("BANANA", "KEY"), "KEY"));
                assertTrue(response.success());
                assertEquals(List.of("A"), response.characters());
                assertEquals(3, response.count());
                var tie = send(second, new AnalysisRequest(VigenereCipher.encrypt("ABBA", "KEY"), "KEY"));
                assertEquals(List.of("A", "B"), tie.characters());
                assertEquals(2, tie.count());
                var empty = send(first, new AnalysisRequest("   ", "KEY"));
                assertEquals(List.of(), empty.characters());
                assertEquals(0, empty.count());
                // Shutdown also closes connected clients and unblocks the accept loop.
                server.close();
                running.get(3, TimeUnit.SECONDS);
            }
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(3, TimeUnit.SECONDS));
        }
    }

    private AnalysisResponse send(Socket socket, AnalysisRequest request) throws Exception {
        Protocol.writeRequest(new DataOutputStream(socket.getOutputStream()), request);
        return Protocol.readResponse(new DataInputStream(socket.getInputStream()));
    }
}
