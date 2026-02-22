import java.io.*;
import java.net.Socket;
import java.util.Base64;
import javax.crypto.SecretKey;

public class ClientHandler extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private SecretKey aesKey;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void sendEncrypted(String msg) {
        try {
            out.println(CryptoUtils.aesEncrypt(msg, aesKey));
        } catch (Exception ignored) {}
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 1. Send RSA public key
            out.println(Base64.getEncoder().encodeToString(
                    ChatServer.rsaKeyPair.getPublic().getEncoded()));

            // 2. Receive AES key
            byte[] encAES = Base64.getDecoder().decode(in.readLine());
            aesKey = CryptoUtils.decodeAESKey(
                    CryptoUtils.rsaDecrypt(encAES,
                            ChatServer.rsaKeyPair.getPrivate()));

            // 3. Receive username
            username = CryptoUtils.aesDecrypt(in.readLine(), aesKey);
            ChatServer.addClient(username, this);

            ChatServer.broadcast("[SERVER]: " + username + " joined", this);

            String enc;
            while ((enc = in.readLine()) != null) {
                String msg = CryptoUtils.aesDecrypt(enc, aesKey);

                if (msg.equalsIgnoreCase("/exit")) break;

                if (msg.startsWith("/msg ")) {
                    handlePrivate(msg);
                } else {
                    ChatServer.broadcast("[" + username + "]: " + msg, this);
                }
            }

        } catch (Exception ignored) {
        } finally {
            ChatServer.removeClient(username);
            ChatServer.broadcast("[SERVER]: " + username + " left", this);
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private void handlePrivate(String msg) {
        String[] p = msg.split(" ", 3);
        if (p.length < 3) return;

        ClientHandler target = ChatServer.getClient(p[1]);
        if (target != null) {
            target.sendEncrypted("[Private from " + username + "]: " + p[2]);
            sendEncrypted("[Private to " + p[1] + "]: " + p[2]);
        } else {
            sendEncrypted("[SERVER]: User not found");
        }
    }
}
