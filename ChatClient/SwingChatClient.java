import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.swing.*;

public class SwingChatClient extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private SecretKey aesKey;

    public SwingChatClient() {
        setTitle("Secure Chat Client");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        connectToServer();
    }

    private void initUI() {
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(inputField, BorderLayout.CENTER);
        bottom.add(sendButton, BorderLayout.EAST);

        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
    }

    private void connectToServer() {
        try {
            socket = new Socket("192.168.1.14", 5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // ---------- RSA HANDSHAKE ----------
            byte[] pubKeyBytes = Base64.getDecoder().decode(in.readLine());
            PublicKey serverPublicKey = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(pubKeyBytes));

            aesKey = CryptoUtils.generateAESKey();

            out.println(Base64.getEncoder().encodeToString(
                    CryptoUtils.rsaEncrypt(aesKey.getEncoded(), serverPublicKey)));

            // ---------- USERNAME ----------
            String username = JOptionPane.showInputDialog(this, "Enter username:");
            out.println(CryptoUtils.aesEncrypt(username, aesKey));

            chatArea.append("Connected as " + username + "\n");

            // ---------- LISTENER THREAD ----------
            new Thread(this::listenForMessages).start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Connection failed",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listenForMessages() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                String decrypted = CryptoUtils.aesDecrypt(msg, aesKey);
                SwingUtilities.invokeLater(() ->
                        chatArea.append(decrypted + "\n")
                );
            }
        } catch (Exception e) {
            chatArea.append("Disconnected from server\n");
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        try {
            out.println(CryptoUtils.aesEncrypt(text, aesKey));
            inputField.setText("");

            if (text.equalsIgnoreCase("/exit")) {
                socket.close();
                dispose();
            }

        } catch (Exception e) {
            chatArea.append("Error sending message\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new SwingChatClient().setVisible(true)
        );
    }
}
