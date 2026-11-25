package vn.edu.network.server;

import vn.edu.network.common.protocol.AnalysisRequest;
import vn.edu.network.common.protocol.AnalysisResponse;
import vn.edu.network.common.protocol.Protocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class TcpServer implements AutoCloseable {
    private final ServerSocket listener;
    private final AnalysisService service = new AnalysisService();
    private final ExecutorService workers = new ThreadPoolExecutor(16, 16, 0L,
            TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(32));
    private final Set<Socket> clients = ConcurrentHashMap.newKeySet();
    private volatile boolean closed;

    public TcpServer(String host, int port) throws IOException {
        listener = new ServerSocket();
        try {
            listener.bind(new InetSocketAddress(host, port));
        } catch (IOException | RuntimeException e) {
            listener.close();
            throw e;
        }
    }

    public int port() {
        return listener.getLocalPort();
    }

    public void serve() throws IOException {
        while (!closed) {
            try {
                Socket socket = listener.accept();
                clients.add(socket);
                try {
                    if (closed) {
                        release(socket);
                        break;
                    }
                    socket.setSoTimeout(300_000);
                    workers.execute(() -> handle(socket));
                } catch (RejectedExecutionException | IOException e) {
                    release(socket);
                    System.err.println("Khong the tiep nhan client: " + e.getMessage());
                }
            } catch (SocketException e) {
                if (!closed) {
                    throw e;
                }
            }
        }
    }

    private void handle(Socket socket) {
        try (socket;
             DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
            while (!closed) {
                AnalysisRequest request = Protocol.readRequest(in);
                AnalysisResponse response;
                try {
                    response = service.analyze(request);
                } catch (IllegalArgumentException e) {
                    response = AnalysisResponse.error(e.getMessage());
                }
                Protocol.writeResponse(out, response);
            }
        } catch (EOFException e) {
            // Client closed the connection.
        } catch (IOException e) {
            if (!closed) {
                System.err.println("Ket noi " + socket.getRemoteSocketAddress() + ": " + e.getMessage());
            }
        } finally {
            clients.remove(socket);
        }
    }

    private void release(Socket socket) {
        clients.remove(socket);
        try {
            socket.close();
        } catch (IOException ignored) { }
    }

    @Override
    public void close() throws IOException {
        closed = true;
        try {
            listener.close();
        } finally {
            clients.forEach(this::release);
            workers.shutdownNow();
        }
    }
}
