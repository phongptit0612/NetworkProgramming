package vn.edu.network.client;

import vn.edu.network.common.crypto.VigenereCipher;
import vn.edu.network.common.protocol.AnalysisResponse;

import java.util.Scanner;

public final class ClientMain {
    private ClientMain() { }

    public static void main(String[] args) {
        try {
            if (args.length > 2) {
                throw new IllegalArgumentException("Cach chay: java -jar client.jar [host] [port]");
            }
            String host = args.length >= 1 ? args[0] : "127.0.0.1";
            int port = args.length >= 2 ? Integer.parseInt(args[1]) : 9999;
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Port phai trong khoang 1-65535.");
            }
            try (AnalysisClient client = new AnalysisClient(host, port);
                 Scanner scanner = new Scanner(System.in)) {
                System.out.printf("Da ket noi %s:%d. Nhap /quit de thoat.%n", host, port);
                while (true) {
                    System.out.print("Van ban: ");
                    if (!scanner.hasNextLine()) {
                        break;
                    }
                    String plaintext = scanner.nextLine();
                    if (plaintext.equals("/quit")) {
                        break;
                    }
                    System.out.print("Khoa (A-Z): ");
                    if (!scanner.hasNextLine()) {
                        break;
                    }
                    String key = scanner.nextLine();
                    try {
                        System.out.println("Ban ma: " + VigenereCipher.encrypt(plaintext, key));
                        AnalysisResponse response = client.analyze(plaintext, key);
                        if (response.success()) {
                            System.out.printf("Ky tu nhieu nhat: %s | So lan: %d%n",
                                    response.characters(), response.count());
                        } else {
                            System.err.println("Server: " + response.message());
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Loi client (hay kiem tra server, IP va port): " + e.getMessage());
            System.exit(1);
        }
    }
}
