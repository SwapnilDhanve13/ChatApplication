package com.swapnil.chatapplication;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>()); // Use synchronized list for thread safety

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server started. Waiting for clients...");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket);

            // Spawn a new thread for each client
            ClientHandler clientThread = new ClientHandler(clientSocket, clients);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private List<ClientHandler> clients;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, List<ClientHandler> clients) throws IOException {
        this.clientSocket = socket;
        this.clients = clients;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                // Log received messages
                System.out.println("Received: " + inputLine);

                // Broadcast message to all clients
                synchronized (clients) {
                    for (ClientHandler aClient : clients) {
                        aClient.out.println(inputLine);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            // Clean up resources and remove the client from the list
            try {
                clients.remove(this);
                System.out.println("Client disconnected: " + clientSocket);

                // Notify remaining clients about the user departure
                synchronized (clients) {
                    for (ClientHandler aClient : clients) {
                        aClient.out.println("A user has disconnected.");
                    }
                }

                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
