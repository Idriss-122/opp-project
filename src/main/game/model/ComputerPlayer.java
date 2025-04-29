package model;
import java.util.ArrayList;
import java.util.List;
import framework.CardView;
import framework.NimoGame;
public class ComputerPlayer extends Player
{
	public static int sleepTime = 1000;
	public ComputerPlayer(NimoGame board, char position)
	{
		super(board, position, false);
		this.name = "Bot "+(nextNb-1);
	}
	
	public boolean play()
	{
		CardView[] cards = this.getDeck().getCards();
		CardView binCard = this.getBoard().getBinDeck().getFirstCard();
		
		// Find a single playable card
		for(int i=0; i<cards.length; i++)
		{
			if(this.getBoard().checkCards(cards[i], binCard))
			{
				// Select only this card
				cards[i].setUp(true);
				this.getDeck().setupDeck();
				return true;
			}
		}
		
		// No playable card found
		return false;
	}
	
	public static List<CardView> getStackCards(int cardIndex, CardView[] cards)
	{
		List<CardView> list = new ArrayList<>();
		list.add(cards[cardIndex]);
		
		for(int j=0;j<cards.length;j++)
		{
			if(NimoGame.canStackCards(cards[cardIndex], cards[j]))
				if(cardIndex != j)
					list.add(cards[j]);
		}	
		
		return list;
	}
}