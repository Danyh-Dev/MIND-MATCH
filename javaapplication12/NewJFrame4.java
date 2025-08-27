package javaapplication12;

import javax.swing.*;
import java.awt.*;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewJFrame4 extends javax.swing.JFrame {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JPanel mainPanel;
    private JPanel scoresPanel;
    private JButton exitButton;
    private Map<String, Integer> playerScores = new HashMap<>();

    public NewJFrame4(Socket socket, PrintWriter out) {
        this.socket = socket;
        this.out = out;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Initialize the frame
        initComponents();
        
        // Make frame visible before requesting scores
        setVisible(true);
        
        // Request scores multiple times to ensure we get them
        requestScoresMultipleTimes();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quiz Game - Final Results");
        setPreferredSize(new Dimension(800, 600));
        setResizable(false);

        // Create main panel with padding
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(new Color(255, 240, 245)); // Light pink background

        // Create scores panel
        scoresPanel = new JPanel();
        scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.Y_AXIS));
        scoresPanel.setBackground(new Color(255, 228, 225)); // Misty rose background
        scoresPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 2)); // Orange border
        
        // Add a loading message initially
        JLabel loadingLabel = new JLabel("Loading final results...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loadingLabel.setForeground(new Color(139, 69, 19)); // Brown text
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoresPanel.add(loadingLabel);

        // Add scores panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(scoresPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(255, 240, 245)); // Light pink background
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(255, 182, 193)); // Pink background
        
        exitButton = new JButton("Exit Game");
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        exitButton.setBackground(new Color(255, 140, 0)); // Orange
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        exitButton.setPreferredSize(new Dimension(150, 40));
        exitButton.addActionListener(e -> exitGame());
        buttonPanel.add(exitButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        getContentPane().setBackground(new Color(255, 240, 245)); // Light pink background
        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private void requestScoresMultipleTimes() {
        new Thread(() -> {
            try {
                // Request scores multiple times with delays
                for (int i = 0; i < 5; i++) {
                    out.println("GET_SCORES");
                    Thread.sleep(1000); // Wait 1 second between requests
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // Start listening for score updates
        startScoreListener();
    }

    private void startScoreListener() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("SCORES:")) {
                        final String scoreData = message;
                        SwingUtilities.invokeLater(() -> updateScoresDisplay(scoreData.substring(7)));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void requestScores() {
        out.println("GET_SCORES");
        
        new Thread(() -> {
            try {
                String message;
                // Keep trying to get scores for a few seconds to ensure we get the final scores
                int attempts = 0;
                while (attempts < 5) {
                    message = in.readLine();
                    if (message != null && message.startsWith("SCORES:")) {
                        final String scoreData = message;
                        SwingUtilities.invokeLater(() -> updateScoresDisplay(scoreData.substring(7)));
                        Thread.sleep(500); // Wait a bit between attempts
                    }
                    attempts++;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateScoresDisplay(String scoresData) {
        scoresPanel.removeAll();
        playerScores.clear();

        // Parse scores
        String[] scores = scoresData.split(",");
        for (String score : scores) {
            if (!score.isEmpty()) {
                String[] parts = score.split("=");
                if (parts.length == 2) {
                    try {
                        playerScores.put(parts[0], Integer.parseInt(parts[1]));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid score format: " + score);
                    }
                }
            }
        }

        // Sort players by score
        List<Map.Entry<String, Integer>> sortedScores = 
            new ArrayList<>(playerScores.entrySet());
        Collections.sort(sortedScores, 
            (a, b) -> b.getValue().compareTo(a.getValue()));

        // Create a title panel with game over message
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        
        JLabel gameOverLabel = new JLabel(" Game Over! ");
        gameOverLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(gameOverLabel);
        titlePanel.add(Box.createVerticalStrut(20));
        
        scoresPanel.add(titlePanel);

        if (sortedScores.isEmpty()) {
            JLabel noPlayersLabel = new JLabel("No players in the game!", SwingConstants.CENTER);
            noPlayersLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
            noPlayersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoresPanel.add(noPlayersLabel);
        } else {
            // Find winners (players with highest score)
            int highestScore = sortedScores.get(0).getValue();
            List<String> winners = new ArrayList<>();
            
            for (Map.Entry<String, Integer> entry : sortedScores) {
                if (entry.getValue() == highestScore) {
                    winners.add(entry.getKey());
                } else {
                    break;
                }
            }

            // Display winner(s)
            JPanel winnerPanel = new JPanel();
            winnerPanel.setLayout(new BoxLayout(winnerPanel, BoxLayout.Y_AXIS));
            
            if (winners.size() > 1) {
                JLabel tieLabel = new JLabel(" It's a Tie! ");
                tieLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
                tieLabel.setForeground(new Color(218, 165, 32)); // Gold color
                tieLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                winnerPanel.add(tieLabel);
                
                JLabel winnersLabel = new JLabel("Winners: " + String.join(" & ", winners));
                winnersLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                winnersLabel.setForeground(new Color(218, 165, 32));
                winnersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                winnerPanel.add(winnersLabel);
                
                JLabel scoreLabel = new JLabel("Score: " + highestScore + " points");
                scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                winnerPanel.add(scoreLabel);
            } else if (highestScore > 0) {
                JLabel winnerLabel = new JLabel(" Winner: Player " + winners.get(0) + " ");
                winnerLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
                winnerLabel.setForeground(new Color(218, 165, 32));
                winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                winnerPanel.add(winnerLabel);
                
                JLabel scoreLabel = new JLabel("Score: " + highestScore + " points");
                scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                winnerPanel.add(scoreLabel);
            } else {
                JLabel noWinnerLabel = new JLabel("No winner - all scores are 0!");
                noWinnerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 24));
                noWinnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                winnerPanel.add(noWinnerLabel);
            }
            
            winnerPanel.add(Box.createVerticalStrut(30));
            scoresPanel.add(winnerPanel);

            // Add "Final Scores" header
            JLabel finalScoresLabel = new JLabel("Final Scores:");
            finalScoresLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            finalScoresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            scoresPanel.add(finalScoresLabel);
            scoresPanel.add(Box.createVerticalStrut(20));

            // Display all scores
            for (int i = 0; i < sortedScores.size(); i++) {
                Map.Entry<String, Integer> entry = sortedScores.get(i);
                
                JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
                scoreRow.setMaximumSize(new Dimension(400, 50));
                
                // Add medal for top 3
                String prefix = "";
                if (i == 0) prefix = "";
                else if (i == 1) prefix = "";
                else if (i == 2) prefix = "";
                
                JLabel rankLabel = new JLabel(prefix + " #" + (i + 1) + "  ");
                rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                scoreRow.add(rankLabel);
                
                JLabel playerLabel = new JLabel(String.format("Player %s: %d points", 
                    entry.getKey(), entry.getValue()));
                playerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                scoreRow.add(playerLabel);
                
                scoresPanel.add(scoreRow);
                scoresPanel.add(Box.createVerticalStrut(10));
            }
        }

        scoresPanel.revalidate();
        scoresPanel.repaint();
    }

    private void exitGame() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
