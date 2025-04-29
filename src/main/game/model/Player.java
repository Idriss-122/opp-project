package model;

import framework.CardView;
import framework.DeckView;
import framework.NimoGame;

public class Player {
    protected static int nextNb = 1;
    protected NimoGame board;
    protected DeckView deck;
    protected String name = "Player";
    protected int rank = -1;
    protected char position;

    public Player(NimoGame board, char position, boolean visible) {
        this.board = board;
        this.position = position;
        this.name += " " + (nextNb++);

        // Initialize player's hand
        CardView[] cards = DeckView.getSomeCards(7, board.getGameDeck(), visible);
        this.deck = new DeckView(7, cards, position, visible);
    }

    public Player(String s, boolean b) {

    }

    public boolean canPlayCard(CardView card) {
        if (hasFinished() || card == null) {
            return false;
        }

        CardView topCard = board.getBinDeck().getFirstCard();
        return board.isValidPlay(card, topCard);
    }

    public boolean playCard(CardView card) {
        if (!canPlayCard(card)) {
            return false;
        }

        // Remove card from player's hand
        deck.delCard(card);
        // Add card to discard pile
        board.getBinDeck().addCard(card);

        // Check if player has won
        if (deck.getCards().length == 0) {
            setFinish(board.getNextRank());
        }

        return true;
    }

    public void drawCard() {
        CardView card = board.getGameDeck().getAndDelLastCard();
        if (card != null) {
            deck.addCard(card);
        }
    }

    public boolean canPlay() {
        if (hasFinished())
            return false;
        CardView binCard = board.getBinDeck().getFirstCard();
        CardView[] cards = deck.getCards();
        if (cards == null || cards.length == 0)
            return false;
        for (CardView c : cards)
            if (board.checkCards(c, binCard))
                return true;
        return false;
    }

    public boolean selectCard(CardView card) {
        if (!canPlayCard(card)) {
            return false;
        }
        
        card.setUp(true);
        deck.setupDeck();
        return true;
    }

    public void deselectAllCards() {
        CardView[] cards = deck.getCards();
        for (CardView card : cards) {
            card.setUp(false);
        }
        deck.setupDeck();
    }

    public DeckView getDeck() {
        return this.deck;
    }

    public NimoGame getBoard() {
        return board;
    }

    public boolean hasFinished() {
        return rank != -1;
    }

    public boolean isPlayer() {
        return getClass().equals(Player.class);
    }

    public boolean isAI() {
        return (this instanceof ComputerPlayer);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setFinish(int rank) {
        setRank(rank);
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public static void resetPlayerNumbers() {
        nextNb = 1;
    }
}
