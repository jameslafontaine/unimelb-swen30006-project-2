package oh_heaven.game;

import ch.aplu.jcardgame.Card;

public interface NPC {
    Card chooseCard(Player player);  // houses the logic in which an NPC determines which card a player will play
                                     // (potentially using game information stored in the player instance)
    String getNPCName(); // return a String which matches the String used to specify this NPC type in the properties file
}
