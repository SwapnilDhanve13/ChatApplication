package com.swapnil.chatapplication;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<String> onMessageReceived;
    private Consumer<String[]> onUserListReceived;
    private String name; // name field

    public ChatClient(String serverAddress, int serverPort, Consumer<String> onMessageReceived, Consumer<String[]> onUserListReceived) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.onMessageReceived = onMessageReceived;
        this.onUserListReceived = onUserListReceived;
    }

    public void setUserName(String name) {
        this.name = name; // Setting the name when it's provided
        out.println("USER_NAME:" + name); // Sending user name to the server
    }

    public void sendMessage(String msg) {
        out.println(msg); // Sending a message to the server
    }
    
    public void sendUserName(String name) {
        out.println("USER_NAME:" + name); // Sending user name to the server
    }

    public void disconnect() {
        try {
            out.println("USER_LEFT:" + name); // Notifying the server that the user is leaving
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread.currentThread().interrupt();
    }

    public void startClient() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("USER_LIST:")) {
                        String[] users = line.substring(10).split(",");
                        onUserListReceived.accept(users); // Updating user list on client
                    } else {
                        onMessageReceived.accept(line); // Displaying messages
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}