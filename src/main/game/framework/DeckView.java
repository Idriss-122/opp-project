package framework;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class DeckView extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private List<CardView> cards = new ArrayList<>();
	private char position = 'B';
	private int type = -1;
	private NimoGame board;
	private int lastLength = 0;
	
	public DeckView(NimoGame board, int type) // If type == 0 alors c la pioche. Sinon c la poubelle.
	{
		super();
		this.board = board;
		this.type = type;
		this.setOpaque(false);
		position = 0;
		if(type == 0)
			setupGameDeck();
		else if(type == 1)
			setupBin();
	}
	
	public DeckView(int nb, CardView[] gameDeck)
	{
		super();
		this.setOpaque(false);
		cards = toList(gameDeck);
		this.lastLength = cards.size();
		setupDeck();		
	}
	
	public DeckView(int nb, CardView[] gameDeck, char position, boolean visible)
	{
		super();
		this.setOpaque(false);
		this.position = position;
		cards = toList(gameDeck);
		this.lastLength = cards.size();
		setupDeck();		
	}

	public void setupGameDeck()
	{
		this.setLayout(null);
		int w = CardView.WIDTH;
		int h = CardView.HEIGHT;
		cards = toList(scrambleDeck(getDefaultDeck()));
		
		this.setBounds(300, NimoGame.HEIGHT/2 - h/2, w+30, h);
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				board.dropCard();
			}
		});
	}
	
	public void setupBin()
	{
		this.setLayout(null);
		int w = CardView.WIDTH;
		int h = CardView.HEIGHT;
		
		this.setBounds(NimoGame.WIDTH/2 + 30 + 50, NimoGame.HEIGHT/2 - h/2, w+60, h);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		int x = 0;
		if(type == 0)
		{
			for(int i=0;i<3;i++)
			{
				g.drawImage(CardView.getBackCard(), x, 0, CardView.WIDTH, CardView.HEIGHT, null);
				x += 10;
			}
		}
		else if(type == 1 && cards.size() > 0)
		{
			for(int i=3;i>=0;i--)
			{
				if(cards.size() > i)
				{
					g.drawImage(cards.get(i).getImage(), x, 0, CardView.WIDTH, CardView.HEIGHT, null);
					x += 20;
				}
			}
			
		}
	}
	
	public void setupDeck()
	{
		this.removeAll();
		for(int i=0;i<cards.size();i++)
		{
			cards.get(i).setPosition(position);
			this.add(cards.get(i));
		}
		refreshDeck();
	}
	
	public void refreshDeck()
	{
		this.setLayout(null);
		int w = CardView.WIDTH;
		int h = CardView.HEIGHT;
		int x = 0, y = 0, space = 20;
		
		switch(position)
		{
		case 'U':
			this.setBounds(300, 20, getCoeff(cards.size(), 0)*cards.size()+w, h+CardView.COEFF_UP);
			for(CardView card : cards)
			{
				if(card.isUp())
				{
					card.setLocation(x, y+CardView.COEFF_UP);
				}
				else
					card.setLocation(x, 0);
				x += getCoeff(cards.size(), 0);
			}	
			break;
		case 'L':
			this.setBounds(20, h, h+CardView.COEFF_UP, getCoeff(cards.size(), 1)*cards.size()+h);
			for(CardView card : cards)
			{
				if(card.isUp())
				{
					card.setLocation(CardView.COEFF_UP, y);
				}
				else
					card.setLocation(0, y);
				y += getCoeff(cards.size(), 1);
			}	
			break;
		case 'R':
			this.setBounds(NimoGame.WIDTH-h-CardView.COEFF_UP-space, h, h+CardView.COEFF_UP, getCoeff(cards.size(), 1)*cards.size()+h);
			for(CardView card : cards)
			{
				if(card.isUp())
				{
					card.setLocation(0, y);
				}
				else
					card.setLocation(CardView.COEFF_UP, y);
				y += getCoeff(cards.size(), 1);
			}
			break;
		default:
			this.setBounds(300, NimoGame.HEIGHT-h-h/3-2*space, getCoeff(cards.size(), 0)*cards.size()+w, h+h/3);
			for(CardView card : cards)
			{
				card.setLocation(x, CardView.COEFF_UP);
				x += getCoeff(cards.size(), 0);
			}	
			break;
		}
		repaint();
	}
	
	public static int getCoeff(int n, int pos) // n : nombre de cartes.
	{
		int w;
		if(pos == 0)
			w = 14*CardView.WIDTH/3;
		else
			w = 7*CardView.HEIGHT/4;
		int k = CardView.WIDTH;
		int temp = n*k;
		while(temp >= w)
		{
			k -= 1;
			temp -= n;
		}
		return k;
	}
	
	public static CardView[] getDefaultDeck()
	{
		CardView[] cards = new CardView[52]; // 56 if +4 cards
		String[] colors = new String[] {"red", "yellow", "green", "blue"};
		String[] specials = new String[] {"sens", "forbidden", "+2", "+4", "colorChanger"};
		
		for(int j=0;j<4;j++)
		{
			for(int i=0;i<10;i++)
			{
				cards[j*10+i] = new CardView(NimoWindow.getImage("cards/"+colors[j]+"_"+i+".png"), colors[j].charAt(0), i);
			}
		}
		
		for(int j=0;j<4;j++)
		{
			for(int i=0;i<3;i++)
			{
				cards[40+j*3+i] = new CardView(NimoWindow.getImage("cards/"+colors[j]+"_"+specials[i]+".png"),
									specials[i], colors[j].charAt(0));
			}
		}
		
		return cards;
	}
	
	public static CardView[] scrambleDeck(CardView[] deck)
	{
		List<CardView> list = toList(deck);
		int n = 0;
		for(int i=0;list.size()>0;i++)
		{
			n = genAleaNb(list.size());
			deck[i] = list.get(n);
			list.remove(n);
		}
		
		return deck;
	}
	
	public static CardView[] getSomeCards(int nb, DeckView deck, boolean visible)
	{
		CardView[] deckTab = deck.getCards();
		
		if(deckTab == null)
			return null;
		else if(deckTab.length < nb)
			return null;
		
		CardView[] cardTab = new CardView[nb];
		
		for(int i=0;i<nb;i++)
		{
			cardTab[i] = deckTab[i];
			cardTab[i].setCardVisible(visible);
			deck.delCard(deckTab[i]);
		}
		
		return cardTab;
	}
	
	public static List<CardView> genDeck(int nb, CardView[] deck, boolean visible)
	{
		List<CardView> list = new ArrayList<>();
		int n = 0;
		int k = 0;
		while(k < nb)
		{
			n = genAleaNb(deck.length);
			try {
			if(deck[n] == null)
				continue;
			}catch(Exception e) {
				continue;
			}
			deck[n].setCardVisible(visible);
			list.add(deck[n]);
			deck[n] = null;
			k++;
		}
		return list;
	}
	
	public void delCard(CardView card)
	{
		if (cards.remove(card)) {
			setupDeck();
			repaint();
		}
	}
	
	public static int genAleaNb(int n)
	{
		
		return (int)(Math.random() * n);
	}
	
	public int getLength() 
	{
		return cards.size();
	}
	
	public CardView[] getCards()
	{
		return cards.toArray(new CardView[0]);
	}
	
	public static List<CardView> toList(CardView[] tab)
	{
		List<CardView> list = new ArrayList<>();
		for(CardView c : tab)
		{
			list.add(c);
		}
		return list;
	}
	
	public CardView getAndDelLastCard()
	{
		if(cards.size() == 0)
			return null;
		CardView card = cards.get(cards.size()-1);
		cards.remove(cards.size()-1);
		if(cards.size() == 0 && type == 0) // type = 0 -> gameDeck (pioche)
			cards = toList(scrambleDeck(getDefaultDeck()));
		return card;
	}
	
	public void addCard(CardView card)
	{
		if (card != null) {
			cards.add(0, card);
			setupDeck();
			repaint();
		}
	}
	
	public CardView[] getAndDelUpCards()
	{
		List<CardView> list = new ArrayList<>();
		CardView[] tab;
		for(int i=0;i<cards.size();i++)
		{
			if(cards.get(i).isUp())
			{
				list.add(cards.get(i));
				cards.remove(i);
				i--;
			}
		}
		//putCardsDown();
		setupDeck();
		repaint();
		
		if(list.size() == 0)
			return null;
		
		tab = new CardView[list.size()];
		
		for(int i=0;i<list.size();i++)
		{
			tab[i] = list.get(i);
		}
		
		return tab;
	}
	
	public void delUpCards()
	{
		for(int i=0;i<cards.size();i++)
		{
			if(cards.get(i).isUp())
			{
				cards.remove(i);
				i--;
			}
		}
		//putCardsDown();
		setupDeck();
		repaint();
	}
	
	public CardView[] getUpCards()
	{
		List<CardView> list = new ArrayList<>();
		CardView[] tab;
		for(int i=0;i<cards.size();i++)
			if(cards.get(i).isUp())
				list.add(cards.get(i));

		repaint();
		
		if(list.size() == 0)
			return null;
		
		tab = new CardView[list.size()];
		
		for(int i=0;i<list.size();i++)
		{
			tab[i] = list.get(i);
		}
		
		return tab;
	}
	
	public void putCardsDown()
	{
		for(int i=0;i<cards.size();i++)
			if(cards.get(i).isUp())
				cards.get(i).setUp(false);
	}
	
	public CardView getFirstCard()
	{
		if(cards.size() == 0)
			return null;
		return cards.get(0);
	}
	
	public static CardView getRandomCard()
	{
		CardView[] tab = getDefaultDeck();
		return tab[genAleaNb(tab.length)];
	}

	public void setLastLength(int val)
	{
		this.lastLength = val;
	}
	
	public int getLastLength() 
	{
		return lastLength;
	}

	public void refreshLastLength() 
	{
		this.lastLength = getLength();
	}

	
}
