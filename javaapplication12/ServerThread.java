
package javaapplication12;

import java.io.*;
import java.net.*;
import java.util.*;

class ServerThread implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String playerName;
    private ArrayList<ServerThread> connectedClients;
    private ArrayList<ServerThread> gamePlayers;
    private Server server;
    private static Map<String, Integer> playerScores = new HashMap<>(); // Make scores static to persist across rounds

    public ServerThread(Socket socket, ArrayList<ServerThread> connectedClients, ArrayList<ServerThread> gamePlayers, Server server) throws IOException {
        this.socket = socket;
        this.connectedClients = connectedClients;
        this.gamePlayers = gamePlayers;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // First get the player's name
            this.playerName = in.readLine();
            
            // Initialize player's score if not exists
            synchronized (playerScores) {
                if (!playerScores.containsKey(playerName)) {
                    playerScores.put(playerName, 0);
                }
            }
            
            // Add the player to connected clients and update all clients
            synchronized (connectedClients) {
                connectedClients.add(this);
                broadcastMessage(playerName + " is now connected.");
                broadcastConnectedPlayers();
            }
            
            System.out.println(playerName + " has connected with score: " + playerScores.get(playerName));
            
            // Initial score broadcast
            broadcastScores();

            // Handle messages
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received message from " + playerName + ": " + message);
                if (message.equals("Play Game")) {
                    handlePlayGame();
                } else if (message.startsWith("ANSWER:")) {
                    handleAnswer(message.substring(7));
                } else if (message.equals("GET_SCORES")) {
                    broadcastScores(); // Send scores to all clients
                }
            }
        } catch (IOException e) {
            System.err.println("Error for client " + playerName + ": " + e.getMessage());
        } finally {
            disconnectClient();
        }
    }

    private void handlePlayGame() {
        synchronized (gamePlayers) {
            if (!gamePlayers.contains(this)) {
                gamePlayers.add(this);
                System.out.println("Player " + playerName + " joined the game. Total players: " + gamePlayers.size());
                // First update the game players list
                updateGamePlayersList();
                // Then update the connected players list to keep both in sync
                broadcastConnectedPlayers();
                broadcastScores();
                server.checkGameStartCondition();
            }
        }
    }

    private void handleAnswer(String answer) {
        System.out.println("Player " + playerName + " answered: " + answer);
        synchronized (playerScores) {
            if (answer.equals("1")) {  // Correct answer is always option 1
                int currentScore = playerScores.get(playerName);
                currentScore += 10;
                playerScores.put(playerName, currentScore);
                System.out.println("Player " + playerName + " score updated to: " + currentScore);
            }
            // Broadcast scores immediately after updating
            broadcastScores();
        }
    }

    private void disconnectClient() {
        try {
            // First remove from game players if they're in the game
            synchronized (gamePlayers) {
                if (gamePlayers.remove(this)) {
                    System.out.println(playerName + " removed from game players.");
                    updateGamePlayersList();
                    server.checkGameStartCondition();
                }
            }

            // Then remove from connected clients
            synchronized (connectedClients) {
                if (connectedClients.remove(this)) {
                    if (playerName != null) {
                        System.out.println(playerName + " has disconnected.");
                        broadcastMessage(playerName + " has disconnected.");
                        
                        // Remove player's score when they disconnect
                        synchronized (playerScores) {
                            playerScores.remove(playerName);
                        }
                        
                        // Broadcast updated lists and scores
                        broadcastConnectedPlayers();
                        broadcastScores();
                    }
                }
            }

            // Close the socket
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Socket closed for " + (playerName != null ? playerName : "unknown player"));
            }
            
            // Close streams
            if (in != null) in.close();
            if (out != null) out.close();
            
        } catch (IOException e) {
            System.err.println("Error disconnecting client: " + e.getMessage());
        }
    }

    private void broadcastMessage(String message) {
        synchronized (connectedClients) {
            for (ServerThread client : connectedClients) {
                client.out.println(message);
            }
        }
    }

    private void broadcastConnectedPlayers() {
        synchronized (connectedClients) {
            StringBuilder playerList = new StringBuilder("PLAYERS:");
            for (ServerThread client : connectedClients) {
                if (gamePlayers.contains(client)) {
                    playerList.append(client.playerName).append(" (In Game),");
                } else {
                    playerList.append(client.playerName).append(",");
                }
            }
            String message = playerList.toString();
            System.out.println("Broadcasting connected players: " + message);
            for (ServerThread client : connectedClients) {
                client.out.println(message);
                client.out.flush();
            }
        }
    }

    private void updateGamePlayersList() {
        synchronized (connectedClients) {
            broadcastConnectedPlayers(); // Just reuse the same list with game status
        }
    }

    private void broadcastScores() {
        StringBuilder scoresMessage = new StringBuilder("SCORES:");
        synchronized (playerScores) {
            synchronized (gamePlayers) {
                // Only include scores for players who are currently in the game
                for (ServerThread player : gamePlayers) {
                    String playerName = player.getPlayerName();
                    Integer score = playerScores.get(playerName);
                    if (score != null) {
                        scoresMessage.append(playerName)
                                   .append("=")
                                   .append(score)
                                   .append(",");
                    }
                }
            }
        }
        String finalMessage = scoresMessage.toString();
        System.out.println("Broadcasting scores: " + finalMessage);
        
        // Broadcast to ALL connected clients, not just game players
        synchronized (connectedClients) {
            for (ServerThread client : connectedClients) {
                client.out.println(finalMessage);
                client.out.flush(); // Ensure message is sent immediately
            }
        }
    }

    public void startGame() {
        out.println("START_GAME");
    }

    public String getPlayerName() {
        return playerName;
    }
}
