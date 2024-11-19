package com.swapnil.chatapplication;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClientGUI extends JFrame {
    private JTextArea messageArea;
    private JTextField textField;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JButton exitButton, emojiPanelButton, fileButton;
    private JPanel emojiPanel;
    private ChatClient client;
    private String name;

    public ChatClientGUI() {
        super("Chat Application");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Layout setup
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane pane = new JScrollPane(userList);
        pane.setSize(150, 250);
        add(pane, BorderLayout.EAST);

        textField = new JTextField();
        textField.addActionListener(e -> {
            String input = textField.getText().trim();

            // Sending the input directly if it's a private message
            if (input.startsWith("/msg ")) {
                client.sendMessage(input);
            } else {
                // Formatting and sending regular chat messages
                String message = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + name + ": " + input;
                client.sendMessage(message);
            }

            textField.setText("");
        });

        // Initializing the emoji panel and buttons
        emojiPanel = new JPanel(new GridLayout(8, 2));
        emojiPanel.add(createEmojiButton("😊"));
        emojiPanel.add(createEmojiButton("😂"));
        emojiPanel.add(createEmojiButton("😒"));
        emojiPanel.add(createEmojiButton("❤️"));
        emojiPanel.add(createEmojiButton("🔥️"));
        emojiPanel.add(createEmojiButton("😍️"));
        emojiPanel.add(createEmojiButton("👌"));
        emojiPanel.add(createEmojiButton("👍"));
        emojiPanel.add(createEmojiButton("😱"));
        

        emojiPanel.setVisible(false);
        add(emojiPanel, BorderLayout.WEST); // Added to the main frame, not in the bottom panel

        emojiPanelButton = new JButton("Emojis");
        emojiPanelButton.addActionListener(e -> emojiPanel.setVisible(!emojiPanel.isVisible()));
        
        fileButton = new JButton("Send File");
        fileButton.addActionListener(e -> sendFile());

        // Initializing the exit button
        exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            client.disconnect(); // Notify the server and close connection
            System.exit(0);
        });
        
        JPanel lPanel = new JPanel(new BorderLayout());
        lPanel.add(fileButton,BorderLayout.EAST);
        lPanel.add(exitButton,BorderLayout.WEST);

        // Bottom panel setup
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(emojiPanelButton, BorderLayout.WEST);
        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(lPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Initialize client
        try {
            // Request the user's name
            name = JOptionPane.showInputDialog(this, "Enter your name:", "Name Entry", JOptionPane.PLAIN_MESSAGE);

            // If the user input is null or empty, ask again or close the application
            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name cannot be empty. Exiting.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            this.setTitle("Chat Application - " + name);
            client = new ChatClient("127.0.0.1", 5000, this::onMessageReceived, this::onUserListReceived);
            client.sendUserName(name);
            client.startClient();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the server", "Connection error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                client.sendFile(selectedFile);
                messageArea.append("File sent: " + selectedFile.getName() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error sending file", "File Transfer Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createEmojiButton(String emoji) {
        JButton button = new JButton(emoji);
        button.addActionListener(e -> textField.setText(textField.getText() + emoji));
        return button;
    }

    private void onMessageReceived(String message) {
        SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
    }

    private void onUserListReceived(String[] users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String user : users) {
                userListModel.addElement(user);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientGUI().setVisible(true));
    }
}
