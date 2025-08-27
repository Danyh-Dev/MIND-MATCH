package javaapplication12;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class GameClientGUI extends JFrame {
    private JTextField nameField;
    private JTextArea connectedPlayersArea;
    private JTextArea waitingPlayersArea;
    private JButton connectButton;
    private JButton playButton;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private boolean isInGame = false;
    private JPanel cardPanel;  // Add this as a class field at the top

    public GameClientGUI() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("MindMatch");
        setPreferredSize(new Dimension(800, 600));
        setResizable(false);

        // Create main panel with background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    ImageIcon icon = new ImageIcon(getClass().getResource("/images/theme.JPG"));
                    Image img = icon.getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    e.printStackTrace();
                    setBackground(new Color(255, 240, 245));
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Card layout panel for our three frames
        cardPanel = new JPanel();  // Store reference to cardPanel
        cardPanel.setLayout(new CardLayout());
        cardPanel.setOpaque(false);

        // First Frame - Name Entry
        JPanel nameFrame = createNameEntryFrame();
        
        // Second Frame - Connected Clients
        JPanel connectedFrame = createConnectedClientsFrame();
        
        // Third Frame - Waiting Room
        JPanel waitingFrame = createWaitingRoomFrame();

        // Add frames to card layout
        cardPanel.add(nameFrame, "NameEntry");
        cardPanel.add(connectedFrame, "ConnectedClients");
        cardPanel.add(waitingFrame, "WaitingRoom");

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createNameEntryFrame() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(255, 240, 245, 200));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 182, 193, 200));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel welcomeLabel = new JLabel("Welcome to MindMatch!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(139, 69, 19));
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(new Color(255, 228, 225, 200));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0), 2),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(new Color(139, 69, 19));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setMaximumSize(new Dimension(200, 30));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        connectButton = new JButton("Connect");
        connectButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        connectButton.setBackground(new Color(255, 140, 0));
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);
        connectButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectButton.addActionListener(e -> connectToServer());

        inputPanel.add(nameLabel);
        inputPanel.add(Box.createVerticalStrut(20));
        inputPanel.add(nameField);
        inputPanel.add(Box.createVerticalStrut(30));
        inputPanel.add(connectButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createConnectedClientsFrame() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(255, 240, 245, 200));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 182, 193, 200));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel connectedLabel = new JLabel("Connected Players", SwingConstants.CENTER);
        connectedLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        connectedLabel.setForeground(new Color(139, 69, 19));
        headerPanel.add(connectedLabel, BorderLayout.CENTER);

        // Players Panel
        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        playersPanel.setBackground(new Color(255, 228, 225, 200));
        playersPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        connectedPlayersArea = new JTextArea(10, 30);
        connectedPlayersArea.setEditable(false);
        connectedPlayersArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        connectedPlayersArea.setBackground(new Color(255, 245, 238));
        JScrollPane scrollPane = new JScrollPane(connectedPlayersArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        playButton = new JButton("Play Game");
        playButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        playButton.setBackground(new Color(255, 140, 0));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        playButton.addActionListener(e -> joinGame());

        playersPanel.add(scrollPane);
        playersPanel.add(Box.createVerticalStrut(30));
        playersPanel.add(playButton);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(playersPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createWaitingRoomFrame() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(new Color(255, 240, 245, 200));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 182, 193, 200));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel waitingLabel = new JLabel("Waiting Room", SwingConstants.CENTER);
        waitingLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        waitingLabel.setForeground(new Color(139, 69, 19));
        headerPanel.add(waitingLabel, BorderLayout.CENTER);

        // Waiting Players Panel
        JPanel waitingPanel = new JPanel();
        waitingPanel.setLayout(new BoxLayout(waitingPanel, BoxLayout.Y_AXIS));
        waitingPanel.setBackground(new Color(255, 228, 225, 200));
        waitingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        waitingPlayersArea = new JTextArea(10, 30);
        waitingPlayersArea.setEditable(false);
        waitingPlayersArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        waitingPlayersArea.setBackground(new Color(255, 245, 238));
        JScrollPane scrollPane = new JScrollPane(waitingPlayersArea);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoLabel = new JLabel("Game will start when 4 players join", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        infoLabel.setForeground(new Color(139, 69, 19));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        waitingPanel.add(scrollPane);
        waitingPanel.add(Box.createVerticalStrut(20));
        waitingPanel.add(infoLabel);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(waitingPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 8687);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name.");
                return;
            }
            
            out.println(playerName);
            nameField.setEnabled(false);
            
            // Switch to connected clients frame
            ((CardLayout) cardPanel.getLayout()).show(cardPanel, "ConnectedClients");
            
            // Start listening for server messages
            startMessageListener();
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Could not connect to server: " + ex.getMessage(), 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void joinGame() {
        System.out.println("Joining game...");
        out.println("Play Game");
        
        // Switch to waiting room frame
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "WaitingRoom");
        
        // Update the button state
        playButton.setEnabled(false);
        playButton.setText("Waiting for players...");
    }

    private void startMessageListener() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    if (message.startsWith("PLAYERS:")) {
                        String[] players = message.substring(8).split(",");
                        updatePlayerList(players);
                    } else if (message.equals("START_GAME") || message.equals("Game Starting")) {
                        handleGameStart();
                    }
                }
            } catch (IOException e) {
                handleConnectionError("Error reading from server: " + e.getMessage());
            }
        }).start();
    }

    private void updatePlayerList(String[] players) {
        SwingUtilities.invokeLater(() -> {
            // Update connected players frame
            StringBuilder connectedList = new StringBuilder("Players in Lobby:\n\n");
            int totalPlayers = 0;
            for (String player : players) {
                if (player != null && !player.trim().isEmpty()) {
                    totalPlayers++;
                    String displayName = player.replace(" (In Game)", "");
                    connectedList.append("• ").append(displayName).append("\n");
                }
            }
            connectedPlayersArea.setText(connectedList.toString() + "\n" + totalPlayers + " players connected");

            // Update waiting room frame
            StringBuilder waitingList = new StringBuilder("Players Ready:\n\n");
            int readyPlayers = 0;
            for (String player : players) {
                if (player != null && !player.trim().isEmpty() && player.endsWith(" (In Game)")) {
                    readyPlayers++;
                    String displayName = player.replace(" (In Game)", "");
                    waitingList.append("• ").append(displayName).append("\n");
                }
            }
            waitingList.append("\n").append(readyPlayers).append("/4 players ready");
            waitingPlayersArea.setText(waitingList.toString());
        });
    }

    private void handleGameStart() {
        SwingUtilities.invokeLater(() -> {
            NewJFrame1 gameFrame = new NewJFrame1(socket, out, in);
            gameFrame.setVisible(true);
            this.setVisible(false);
        });
    }

    private void handleConnectionError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GameClientGUI().setVisible(true);
        });
    }
}

class TransparentPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}