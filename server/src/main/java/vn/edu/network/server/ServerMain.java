package vn.edu.network.server;

public final class ServerMain {
    private ServerMain() { }

    public static void main(String[] args) {
        try {
            if (args.length > 2) {
                throw new IllegalArgumentException("Cach chay: java -jar server.jar [port] [bind-host]");
            }
            int port = args.length >= 1 ? Integer.parseInt(args[0]) : 9999;
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Port phai trong khoang 1-65535.");
            }
            String host = args.length >= 2 ? args[1] : "0.0.0.0";
            try (TcpServer server = new TcpServer(host, port)) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        server.close();
                    } catch (Exception ignored) { }
                }));
                System.out.printf("Server TCP dang nghe tai %s:%d. Ctrl+C de dung.%n", host, server.port());
                server.serve();
            }
        } catch (Exception e) {
            System.err.println("Khong the chay server: " + e.getMessage());
            System.exit(1);
        }
    }
}
