
package javaapplication12;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
    private static final int PORT = 8687;
    private static final int MAX_GAME_PLAYERS = 4;
    private Timer startGameTimer;
    private boolean gameStarted = false; // To prevent multiple starts


    private static ArrayList<ServerThread> connectedClients = new ArrayList<>();
    private static ArrayList<ServerThread> gamePlayers = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is running...");
        
         Server server = new Server();
        
        while (true) {
            Socket clientSocket = serverSocket.accept(); // Accepting Connections
        ServerThread clientThread = new ServerThread(clientSocket, connectedClients, gamePlayers, server);
        new Thread(clientThread).start(); // Start the thread (but don't add it yet)
}
    }
    
    public void checkGameStartCondition() {
        synchronized (gamePlayers) {
            System.out.println("Checking game start condition. Players: " + gamePlayers.size() + ", Game started: " + gameStarted);
            
            if (gamePlayers.size() == 2 && startGameTimer == null && !gameStarted) {
                // Start a 30-second timer when there are 2 players
                System.out.println("2 players connected. Starting timer...");
                startGameTimer = new Timer();
                startGameTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (gamePlayers) {
                            if (!gameStarted) {
                                System.out.println("Game is starting after timer.");
                                gameStarted = true;
                                startGame(); // Start the game after 30 seconds
                            }
                        }
                    }
                }, 30000); // 30 seconds
            }

            if (gamePlayers.size() == 4 && !gameStarted) {
                // If 4 players connect, cancel the timer and start the game
                if (startGameTimer != null) {
                    System.out.println("4 players connected. Cancelling timer.");
                    startGameTimer.cancel();
                    startGameTimer = null;
                }
                System.out.println("Starting game with 4 players.");
                gameStarted = true;
                startGame();
            }
            
            // Reset game if all players leave
            if (gamePlayers.size() == 0) {
                System.out.println("All players left. Resetting game state.");
                gameStarted = false;
                if (startGameTimer != null) {
                    startGameTimer.cancel();
                    startGameTimer = null;
                }
            }
        }
    }
    
    private void startGame() {
       System.out.println("Starting game! Notifying all players...");
        for (ServerThread player : gamePlayers) {
            player.startGame(); // Notify clients to switch to the game frame
        }
    }
    
    
    
    
}


