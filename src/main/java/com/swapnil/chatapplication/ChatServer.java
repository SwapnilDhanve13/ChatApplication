package com.swapnil.chatapplication;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static List<ClientHandler> clients = new ArrayList<>();
    private static Map<String, ClientHandler> userMap = new HashMap<>(); // Map for username-to-handler mapping

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server started. Waiting for clients...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler clientThread = new ClientHandler(clientSocket, clients, userMap);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }
    }

    public static synchronized void broadcastUserList() {
        String userList = String.join(",", userMap.keySet());
        for (ClientHandler client : clients) {
            client.sendUserList(userList);
        }
    }

    public static synchronized void addUser(String userName, ClientHandler clientHandler) {
        userMap.put(userName, clientHandler);
    }

    public static synchronized void removeUser(String userName) {
        userMap.remove(userName);
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private List<ClientHandler> clients;
    private Map<String, ClientHandler> userMap;
    private PrintWriter out;
    private BufferedReader in;
    private String userName;

    public ClientHandler(Socket socket, List<ClientHandler> clients, Map<String, ClientHandler> userMap) throws IOException {
        this.clientSocket = socket;
        this.clients = clients;
        this.userMap = userMap;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("USER_NAME:")) {
                    userName = inputLine.substring(10);
                    System.out.println(userName + " has joined the chat.");
                    ChatServer.addUser(userName, this);
                    broadcast(userName + " has joined the chat.");
                    ChatServer.broadcastUserList();
                } else if (inputLine.startsWith("USER_LEFT:")) {
                    handleUserLeft();
                    break;
                } else if (inputLine.startsWith("/msg ")) {
                    System.out.println("privately");
                    handlePrivateMessage(inputLine);
                } else {
                    System.out.println("msg");
                    broadcast(inputLine);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void handleUserLeft() throws IOException {
        System.out.println(userName + " has left the chat.");
        clients.remove(this);
        ChatServer.removeUser(userName);
        broadcast(userName + " has left the chat.");
        ChatServer.broadcastUserList();
    }

    private void handlePrivateMessage(String inputLine) {
        String[] parts = inputLine.split(" ", 3);
        if (parts.length >= 3) {
            String targetUser = parts[1];
            String message = parts[2];
            ClientHandler targetHandler = userMap.get(targetUser);
            if (targetHandler != null) {
                targetHandler.out.println("[Private] " + userName + ": " + message);
                out.println("[Private to " + targetUser + "] " + message); // Acknowledge sender
            } else {
                out.println("User " + targetUser + " not found.");
            }
        } else {
            out.println("Invalid private message format. Use /msg [username] [message].");
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.out.println(message);
        }
    }

    public void sendUserList(String userList) {
        out.println("USER_LIST:" + userList);
    }

    private void cleanup() {
        try {
            if (userName != null) {
                ChatServer.removeUser(userName);
                ChatServer.broadcastUserList();
            }
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}