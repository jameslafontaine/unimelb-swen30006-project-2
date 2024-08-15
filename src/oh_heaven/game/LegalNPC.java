package oh_heaven.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class LegalNPC implements NPC {

    public Card chooseCard(Player player) { // GamePane gp input as argument, move code for what happens on the game into this class

        boolean hasLeadSuit = false;
        // check if the player can play a lead suit card
        for (Card card : player.getCurrentHand().getCardList()) {
            if (card.getSuit() == player.getLeadSuit()) {
                hasLeadSuit = true;
            }
        }

        if (hasLeadSuit) {
            // if the lead player is playing a card then any card is legal
            if (player.getLeadSuit() == null) {
                return Oh_Heaven.randomCard(player.getCurrentHand());
                // otherwise, the following player must play a card in the lead suit
            } else {
                while (true) {
                    Card candidateCard = Oh_Heaven.randomCard(player.getCurrentHand());

                    if (candidateCard.getSuit() == player.getLeadSuit()) {
                        return candidateCard;
                    }
                }
            }
        } else
            // the following player is allowed to play any other card if they don't have a card in the lead suit
            return Oh_Heaven.randomCard(player.getCurrentHand());
    }

    public String getNPCName() {
        return "legal";
    }
}
