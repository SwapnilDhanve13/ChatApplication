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
    private DataInputStream infile;
    private DataOutputStream outfile;
    private String name; // name field

    public ChatClient(String serverAddress, int serverPort, Consumer<String> onMessageReceived, Consumer<String[]> onUserListReceived) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.infile = new DataInputStream(socket.getInputStream());
        this.outfile = new DataOutputStream(socket.getOutputStream());
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
    
    public void sendFile(File file) throws IOException {
        // Notify the server that a file transfer is starting
        outfile.writeUTF("FILE_TRANSFER: " + file.getName());
        outfile.writeUTF(" ");
        outfile.flush(); // Ensure the message is sent immediately

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outfile.write(buffer, 0, bytesRead);
                outfile.flush(); // Send the bytes to the server
            }
            System.out.println("File sent successfully: " + file.getName());
        } catch (IOException e) {
            System.err.println("Failed to send file: " + e.getMessage());
            throw e; // Rethrow to allow external handling if needed
        }
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