package com.swapnil.chatapplication;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

/**
 *
 * @author Swapnil
 */
public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<String> onMessageReceived;

    public ChatClient(String serverAddress, int serverPort, Consumer<String> onMessageReceived) throws IOException {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.onMessageReceived = onMessageReceived;
            System.out.println("Connected to the server at " + serverAddress + ":" + serverPort);
        } catch (IOException e) {
            System.err.println("Failed to connect to the server: " + e.getMessage());
            throw e; // Re-throw to indicate failure to the caller
        }
    }

    public void sendMessage(String msg) {
        if (socket != null && !socket.isClosed()) {
            out.println(msg);
        } else {
            System.err.println("Unable to send message. Connection is closed.");
        }
    }

    public void startClient() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    onMessageReceived.accept(line);
                }
            } catch (IOException e) {
                System.err.println("Connection lost: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }).start();
    }

    public void closeConnection() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Connection to the server closed.");
            }
        } catch (IOException e) {
            System.err.println("Error closing the connection: " + e.getMessage());
        }
    }
}
