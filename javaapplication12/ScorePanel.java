package javaapplication12;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScorePanel extends JPanel {
    private Map<String, Integer> playerScores = new HashMap<>();
    private JPanel scoresContainer;

    public ScorePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Create title
        JLabel titleLabel = new JLabel("Player Scores", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(titleLabel, BorderLayout.NORTH);

        // Create scores container
        scoresContainer = new JPanel();
        scoresContainer.setLayout(new BoxLayout(scoresContainer, BoxLayout.Y_AXIS));
        add(scoresContainer, BorderLayout.CENTER);
    }

    public void updateScore(int score) {
        // This method is called when the local player's score changes
        System.out.println("SCORE:" + score);
    }

    public void updatePlayerScore(int score) {
        // This method is called when the local player's score changes
        System.out.println("SCORE:" + score);
    }

    public void updateScores(String scoresData) {
        // Parse scores data (format: "SCORES:player1=score1,player2=score2,...")
        playerScores.clear();
        if (scoresData.startsWith("SCORES:")) {
            String[] scores = scoresData.substring(7).split(",");
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
        }
        
        // Update the display
        updateScoreDisplay();
    }

    private void updateScoreDisplay() {
        scoresContainer.removeAll();
        
        // Sort players by score
        List<Map.Entry<String, Integer>> sortedScores = new ArrayList<>(playerScores.entrySet());
        Collections.sort(sortedScores, Map.Entry.<String, Integer>comparingByValue().reversed());
        
        // Add each player's score
        for (Map.Entry<String, Integer> entry : sortedScores) {
            JLabel scoreLabel = new JLabel(
                String.format("Player %s: %d pts", entry.getKey(), entry.getValue()),
                SwingConstants.LEFT
            );
            scoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            scoreLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
            scoresContainer.add(scoreLabel);
        }
        
        // Refresh the panel
        scoresContainer.revalidate();
        scoresContainer.repaint();
    }
}
