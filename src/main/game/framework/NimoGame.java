package framework;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import model.Player;

public class NimoGame extends JPanel {
    private static final long serialVersionUID = 1L;
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 600;
    
    private NimoWindow window;
    private Player[] players;
    private int currentPlayerIndex;
    private CardView topCard;
    private boolean reversed;
    private static DeckView gameDeck;
    private DeckView binDeck;
    private JLabel playTurn;
    private int nextRank = 1;
    
    public NimoGame(NimoWindow window) {
        setOpaque(false);
        this.window = window;
        this.setLayout(null);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        
        // Initialize game components
        this.reversed = false;
        this.currentPlayerIndex = 0;
        
        // Setup game deck
        this.gameDeck = new DeckView(this, 0); // type 0 for draw pile
        
        // Setup discard pile
        this.binDeck = new DeckView(this, 1); // type 1 for discard pile
        
        // Get initial top card
        initializeTopCard();
        
        // Setup turn label
        playTurn = new JLabel("");
        playTurn.setBounds(120, 0, 100, 100);
        playTurn.setForeground(Color.WHITE);
        
        // Add components
        add(gameDeck);
        add(binDeck);
        add(playTurn);
    }

	public static boolean canStackCards(CardView card1, CardView card2) {
	    if (card1 == null || card2 == null) {
	        return true;
	    }

	    // Check if cards match by:
	    // 1. Same number
	    if (card1.getNb() == card2.getNb() && card1.getNb() != -1) {
	        return true;
	    }
	    
	    // 2. Same color
	    if (card1.getColor() == card2.getColor()) {
	        return true;
	    }
	    
	    // 3. Wild cards can be stacked on anything
	    if (card1.isWild() || card2.isWild()) {
	        return true;
	    }
	    
	    // 4. Same special card type
	    if (card1.getType().equals(card2.getType()) && !card1.getType().equals(CardView.TYPE_NUMBER)) {
	        return true;
	    }
	    
	    return false;
	}

	private void initializeTopCard() {
        topCard = gameDeck.getAndDelLastCard();
        while (topCard != null && topCard.isSpecialCard()) {
            gameDeck.addCard(topCard);
            topCard = gameDeck.getAndDelLastCard();
        }
        if (topCard != null) {
            binDeck.addCard(topCard);
        }
    }
    
    public void setupPlayers(Player[] players) {
        if (players == null) return;
        this.players = players;
        for (Player p : players) {
            this.add(p.getDeck());
        }
        updateTurnLabel();
    }

    public void dropCard() {
        Player currentPlayer = getActualPlayer();
        if (currentPlayer != null && !currentPlayer.isAI()) {
            drawCardForPlayer(currentPlayer);
            nextTurn();
        }
    }

    private void drawCardForPlayer(Player player) {
        CardView card = gameDeck.getAndDelLastCard();
        if (card != null) {
            player.getDeck().addCard(card);
        }
    }
    
    public boolean isValidPlay(CardView card, CardView topCard) {
        if (card == null || topCard == null) {
            return false;
        }
        
        return card.getColor() == topCard.getColor() || 
               card.getNb() == topCard.getNb() ||
               card.isWild();
    }
    
    public boolean checkCards(CardView card1, CardView card2) {
        return isValidPlay(card1, card2);
    }
    
    public void handleCardPlay(Player player, CardView card) {
        if (player != getActualPlayer() || !isValidPlay(card, topCard)) {
            return;
        }

        CardView[] selectedCards = player.getDeck().getUpCards();
        if (selectedCards == null || selectedCards.length == 0) {
            return;
        }

        // Check if cards can be stacked
        boolean validStack = true;
        for (int i = 1; i < selectedCards.length; i++) {
            if (!canStackCards(selectedCards[0], selectedCards[i])) {
                validStack = false;
                break;
            }
        }

        if (!validStack) {
            return;
        }

        // Play all selected cards
        for (CardView selectedCard : selectedCards) {
            if (player.playCard(selectedCard)) {
                topCard = selectedCard;
                handleSpecialCard(selectedCard);
            }
        }
        
        nextTurn();
        updateUI();
    }
    
    private void handleSpecialCard(CardView card) {
        String type = card.getType();
        switch (type) {
            case CardView.TYPE_REVERSE:
                reversed = !reversed;
                break;
            case CardView.TYPE_SKIP:
                nextTurn(); // Skip next player
                break;
            case CardView.TYPE_DRAW_TWO:
                Player nextPlayer = getNextPlayer();
                nextPlayer.drawCard();
                nextPlayer.drawCard();
                break;
            case CardView.TYPE_WILD_DRAW_FOUR:
                nextPlayer = getNextPlayer();
                for (int i = 0; i < 4; i++) {
                    nextPlayer.drawCard();
                }
                handleWildCard(card);
                break;
            case CardView.TYPE_WILD:
                handleWildCard(card);
                break;
        }
    }
    
    private void handleWildCard(CardView card) {
        String[] colors = {"Red", "Blue", "Green", "Yellow"};
        String choice = (String) JOptionPane.showInputDialog(
            this,
            "Choose a color:",
            "Wild Card Color Selection",
            JOptionPane.QUESTION_MESSAGE,
            null,
            colors,
            colors[0]
        );
        
        if (choice != null) {
            card.setColor(choice.charAt(0));
        }
    }
    
    private void nextTurn() {
        if (reversed) {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.length) % players.length;
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        }
        updateTurnLabel();
    }
    
    private void updateTurnLabel() {
        Player current = getActualPlayer();
        if (current != null) {
            playTurn.setText(current.getName() + "'s Turn");
        }
    }
    
    public Player getActualPlayer() {
        return players != null && players.length > 0 ? 
            players[currentPlayerIndex] : null;
    }
    
    public Player getNextPlayer() {
        if (players == null || players.length == 0) return null;
        int nextIndex = reversed ? 
            (currentPlayerIndex - 1 + players.length) % players.length :
            (currentPlayerIndex + 1) % players.length;
        return players[nextIndex];
    }
    
    public DeckView getBinDeck() {
        return binDeck;
    }
    
    public DeckView getGameDeck() {
        return gameDeck;
    }
    
    public int getNextRank() {
        return nextRank++;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Additional custom painting if needed
    }

}