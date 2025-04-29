package framework;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.ComputerPlayer;
import model.Player;

public class NimoSettings extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int TOTAL_PLAYERS = 4;
    
    private JSpinner botCountSpinner;
    private JPanel playerNamesPanel;
    private JTextField[] playerNameFields;
    
    public NimoSettings(NimoWindow frame) {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 40, 20, 40));

        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Bot count selection panel
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        spinnerPanel.setOpaque(false);
        
        JLabel botLabel = new JLabel("Number of Bots:");
        botLabel.setForeground(Color.WHITE);
        spinnerPanel.add(botLabel);
        
        SpinnerNumberModel botModel = new SpinnerNumberModel(0, 0, TOTAL_PLAYERS-1, 1);
        botCountSpinner = new JSpinner(botModel);
        botCountSpinner.addChangeListener(e -> updatePlayerFields());
        spinnerPanel.add(botCountSpinner);
        
        contentPanel.add(spinnerPanel);
        
        // Player names panel
        playerNamesPanel = new JPanel();
        playerNamesPanel.setOpaque(false);
        
        // Initialize player name fields
        playerNameFields = new JTextField[TOTAL_PLAYERS];
        for (int i = 0; i < TOTAL_PLAYERS; i++) {
            playerNameFields[i] = new JTextField("Player " + (i + 1), 15);
        }
        
        contentPanel.add(playerNamesPanel);
        updatePlayerFields();
        
        // Navigation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        buttonPanel.add(new NimoButton("Back", Color.ORANGE, 15, e -> frame.setHomeView()));
        buttonPanel.add(new NimoButton("Start Game", Color.ORANGE, 15, e -> startGame(frame)));
        
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updatePlayerFields() {
        int botCount = (int) botCountSpinner.getValue();
        int humanCount = TOTAL_PLAYERS - botCount;

        playerNamesPanel.removeAll();
        playerNamesPanel.setLayout(new GridLayout(0, 2, 10, 10));

        // Create or update text fields for human players
        for (int i = 0; i < humanCount; i++) {
            JLabel label = new JLabel("Human Player " + (i + 1) + ":");
            label.setForeground(Color.WHITE);
            playerNamesPanel.add(label);
            playerNamesPanel.add(playerNameFields[i]);
        }

        // Show bot labels (no input fields needed)
        for (int i = 0; i < botCount; i++) {
            JLabel botLabel = new JLabel("Bot " + (i + 1) + ":");
            botLabel.setForeground(Color.WHITE);
            JLabel botName = new JLabel("Bot " + (i + 1));
            botName.setForeground(Color.GRAY);
            playerNamesPanel.add(botLabel);
            playerNamesPanel.add(botName);
        }

        playerNamesPanel.revalidate();
        playerNamesPanel.repaint();
    }

    private void startGame(NimoWindow frame) {
        int botCount = (int) botCountSpinner.getValue();
        int humanCount = TOTAL_PLAYERS - botCount;
        
        NimoGame game = new NimoGame(frame);
        Player[] players = new Player[TOTAL_PLAYERS];
        
        // Create human players
        for (int i = 0; i < humanCount; i++) {
            String name = playerNameFields[i].getText().trim();
            if (name.isEmpty()) name = "Player " + (i + 1);
            players[i] = new Player(game, getPlayerPosition(i), true);
            players[i].setName(name);
        }
        
        // Create bot players
        for (int i = humanCount; i < TOTAL_PLAYERS; i++) {
            players[i] = new ComputerPlayer(game, getPlayerPosition(i));
            players[i].setName("Bot " + (i - humanCount + 1));
        }
        
        game.setupPlayers(players);
        frame.setBoardGame(game);
    }
    
    private char getPlayerPosition(int index) {
        char[] positions = {'B', 'L', 'U', 'R'};
        return positions[index % TOTAL_PLAYERS];
    }
}