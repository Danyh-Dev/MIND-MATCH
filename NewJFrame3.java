package javaapplication12;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;

public class NewJFrame3 extends javax.swing.JFrame {
    private Timer imageTimer;
    private Timer answerTimer;
    private int imageTimeLeft = 15; // 15 seconds for image viewing
    private int answerTimeLeft = 15; // 15 seconds for answering
    private JPanel questionPanel;
    private ButtonGroup buttonGroup1;
    private JLabel jLabel1, jLabel2, jLabel3;
    private JRadioButton jRadioButton1, jRadioButton2, jRadioButton3;
    private JButton jButton1;
    private JLabel timerLabel;
    private ScorePanel scorePanel; 
    private PrintWriter out;
    private Socket socket;
    private BufferedReader in;
    private boolean answerSubmitted = false;
    private int playerScore = 0;
    
    // Correct answer is option 1 (jRadioButton1)
    private static final String CORRECT_ANSWER = "1";
    
    public NewJFrame3(Socket socket, PrintWriter out, BufferedReader in) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        
        // Initialize timer label
        timerLabel = new JLabel("Time left: " + imageTimeLeft + "s");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Initialize score panel
        scorePanel = new ScorePanel();
        
        initComponents();
        setupUI();
        startImageTimer();
        hideQuestionAndOptions();
        startMessageListener();
        
        // Request initial scores
        out.println("GET_SCORES");
    }

    private void startMessageListener() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("SCORES:")) {
                        final String scoreData = message;
                        SwingUtilities.invokeLater(() -> {
                            scorePanel.updateScores(scoreData);
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initComponents() {
        buttonGroup1 = new ButtonGroup();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(255, 240, 245)); // Light pink background

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.setBackground(new Color(255, 182, 193)); // Pink
        
        jLabel2 = new javax.swing.JLabel("Focus on all the elements in the picture");
        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        jLabel2.setForeground(new Color(139, 69, 19)); // Brown text
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(jLabel2, BorderLayout.CENTER);
        
        // Add timer to the left
        timerLabel.setForeground(new Color(139, 69, 19)); // Brown text
        headerPanel.add(timerLabel, BorderLayout.WEST);
        
        // Add score panel to the right
        headerPanel.add(scorePanel, BorderLayout.EAST);
        
        // Create image panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(255, 140, 0), 2)); // Orange border
        imagePanel.setBackground(new Color(255, 240, 245)); // Light pink background
        jLabel1 = new javax.swing.JLabel();
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.add(jLabel1, BorderLayout.CENTER);
        
        // Create question panel
        questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        questionPanel.setBackground(new Color(255, 228, 225)); // Misty rose background
        
        jLabel3 = new javax.swing.JLabel("How many triangles were on the image?");
        jLabel3.setFont(new Font("Segoe UI", Font.BOLD, 18));
        jLabel3.setForeground(new Color(139, 69, 19)); // Brown text
        jLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create radio buttons
        buttonGroup1 = new javax.swing.ButtonGroup();
        jRadioButton1 = new javax.swing.JRadioButton("3 triangles");
        jRadioButton2 = new javax.swing.JRadioButton("4 triangles");
        jRadioButton3 = new javax.swing.JRadioButton("5 triangles");
        
        // Style radio buttons
        Font optionFont = new Font("Segoe UI", Font.PLAIN, 16);
        for (JRadioButton btn : new JRadioButton[]{jRadioButton1, jRadioButton2, jRadioButton3}) {
            btn.setFont(optionFont);
            btn.setForeground(new Color(139, 69, 19)); // Brown text
            btn.setBackground(new Color(255, 228, 225)); // Misty rose background
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            buttonGroup1.add(btn);
        }
        
        // Create submit button
        jButton1 = new javax.swing.JButton("Submit Answer");
        jButton1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jButton1.setBackground(new Color(255, 140, 0)); // Orange
        jButton1.setForeground(Color.WHITE);
        jButton1.setFocusPainted(false);
        jButton1.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        jButton1.setAlignmentX(Component.CENTER_ALIGNMENT);
        jButton1.addActionListener(evt -> jButton1ActionPerformed(evt));
        
        // Add components to question panel
        questionPanel.add(jLabel3);
        questionPanel.add(Box.createVerticalStrut(20));
        questionPanel.add(jRadioButton1);
        questionPanel.add(Box.createVerticalStrut(10));
        questionPanel.add(jRadioButton2);
        questionPanel.add(Box.createVerticalStrut(10));
        questionPanel.add(jRadioButton3);
        questionPanel.add(Box.createVerticalStrut(30));
        questionPanel.add(jButton1);
        
        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(imagePanel, BorderLayout.CENTER);
        mainPanel.add(questionPanel, BorderLayout.SOUTH);
        
        // Set frame properties
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quiz Game - Round 3");
        setPreferredSize(new Dimension(800, 800));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().setBackground(new Color(255, 240, 245)); // Light pink background
        pack();
        setLocationRelativeTo(null);
    }

    private void setupUI() {
        // Apply theme to frame
        QuizTheme.styleFrame(this);
        
        // Style components
        QuizTheme.styleButton(jButton1);
        QuizTheme.styleRadioButton(jRadioButton1);
        QuizTheme.styleRadioButton(jRadioButton2);
        QuizTheme.styleRadioButton(jRadioButton3);
        
        // Load the image
        try {
            String imagePath = "src/images/TRIANGLES.jpg";
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imagePath);
                Image img = icon.getImage();
                Image newImg = img.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                jLabel1.setIcon(new ImageIcon(newImg));
            } else {
                System.err.println("Image not found at: " + imagePath);
                jLabel1.setText("Image not found");
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            jLabel1.setText("Error loading image");
            e.printStackTrace();
        }
    }

    private void startImageTimer() {
        imageTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageTimeLeft--;
                timerLabel.setText("Image time left: " + imageTimeLeft + "s");
                
                if (imageTimeLeft <= 0) {
                    imageTimer.stop();
                    // Hide the image and show questions
                    jLabel1.setVisible(false);
                    jLabel2.setVisible(false);
                    questionPanel.setVisible(true);
                    startAnswerTimer();
                }
            }
        });
        imageTimer.start();
    }

    private void startAnswerTimer() {
        timerLabel.setText("Answer time left: " + answerTimeLeft + "s");
        answerTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                answerTimeLeft--;
                timerLabel.setText("Answer time left: " + answerTimeLeft + "s");
                
                if (answerTimeLeft <= 0) {
                    answerTimer.stop();
                    // If no answer selected, submit default answer (0)
                    if (!jRadioButton1.isSelected() && !jRadioButton2.isSelected() && !jRadioButton3.isSelected()) {
                        out.println("ANSWER:0");
                    }
                    // Move to NewJFrame4
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        try {
                            System.out.println("Creating NewJFrame4");
                            NewJFrame4 frame4 = new NewJFrame4(socket, out);
                            frame4.setVisible(true);
                        } catch (Exception ex) {
                            System.err.println("Error creating next frame: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    });
                }
            }
        });
        answerTimer.start();
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
        if (!answerSubmitted) {
            checkAnswer();
            answerSubmitted = true;
            // Disable the submit button after submission
            jButton1.setEnabled(false);
        }
    }

    private void hideQuestionAndOptions() {
        // Initially hide the question panel
        questionPanel.setVisible(false);
    }

    private void checkAnswer() {
        if (!answerSubmitted) {
            String selectedOption = getSelectedAnswer();
            out.println("ANSWER:" + selectedOption);
            
            // Update score if correct answer is selected
            if (selectedOption.equals(CORRECT_ANSWER)) {
                playerScore += 10;
                out.println("SCORE:" + playerScore);
            }
            
            answerSubmitted = true;
            jButton1.setEnabled(false);
        }
    }

    private String getSelectedAnswer() {
        if (jRadioButton1.isSelected()) {
            return "1";
        } else if (jRadioButton2.isSelected()) {
            return "2";
        } else if (jRadioButton3.isSelected()) {
            return "3";
        } else {
            return "0";
        }
    }
}
