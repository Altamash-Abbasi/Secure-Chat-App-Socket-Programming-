import java.net.*;
import java.security.KeyPair;
import java.util.*;

public class ChatServer {

    public static KeyPair rsaKeyPair;
    private static final Map<String, ClientHandler> clients =
            Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) throws Exception {
        rsaKeyPair = CryptoUtils.generateRSAKeyPair();
        System.out.println("Secure Chat Server started");

        ServerSocket serverSocket = new ServerSocket(5000);

        while (true) {
            Socket socket = serverSocket.accept();
            new ClientHandler(socket).start(); // NO WAITING
        }
    }

    public static void addClient(String username, ClientHandler ch) {
        clients.put(username, ch);
    }

    public static void removeClient(String username) {
        clients.remove(username);
    }

    public static ClientHandler getClient(String user) {
        return clients.get(user);
    }

    public static void broadcast(String msg, ClientHandler sender) {
        synchronized (clients) {
            for (ClientHandler c : clients.values()) {
                if (c != sender) {
                    c.sendEncrypted(msg);
                }
            }
        }
    }
}
