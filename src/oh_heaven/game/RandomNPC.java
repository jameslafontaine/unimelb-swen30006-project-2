package oh_heaven.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class RandomNPC implements NPC {

    public Card chooseCard(Player player) {
        return Oh_Heaven.randomCard(player.getCurrentHand());
    }

    public String getNPCName() {
        return "random";
    }
}
