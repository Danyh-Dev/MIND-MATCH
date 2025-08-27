/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication12;
import java.io.*;
import java.net.*;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class ClientThread implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GameClientGUI gui;
    private volatile boolean running = true;

    public ClientThread(Socket socket, BufferedReader in, PrintWriter out, GameClientGUI gui) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            if (running) {
                handleError("Lost connection to server: " + e.getMessage());
            }
        }
    }

    private void processMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (message.startsWith("PLAYERS:")) {
                    // Handle player list update
                    String[] players = message.substring(8).split(",");
                    StringBuilder playerList = new StringBuilder();
                    for (String player : players) {
                        playerList.append(player).append("\n");
                    }
                    // Update UI components here
                } else if (message.equals("START_GAME")) {
                    // Handle game start
                    startGame();
                } else if (message.startsWith("ERROR:")) {
                    // Handle error messages
                    handleError(message.substring(6));
                }
            } catch (Exception e) {
                handleError("Error processing message: " + e.getMessage());
            }
        });
    }

    private void startGame() {
        // Game start logic here
    }

    private void handleError(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, 
                errorMessage, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        });
    }

    public void stop() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
