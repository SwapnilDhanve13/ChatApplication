package com.swapnil.chatapplication;

/**
 *
 * @author Swapnil
 */
public class ChatApplication {
    public static void main(String[] args) {
        // Launch the ChatClientGUI
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ChatClientGUI().setVisible(true);
        });
    }
}
