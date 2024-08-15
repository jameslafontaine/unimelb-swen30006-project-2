package oh_heaven.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class Oh_Heaven extends CardGame {
	
  public enum Suit
  {
    SPADES, HEARTS, DIAMONDS, CLUBS
  }

  public enum Rank
  {
    // Reverse order of rank importance (see rankGreater() below)
	// Order of cards is tied to card images
	ACE, KING, QUEEN, JACK, TEN, NINE, EIGHT, SEVEN, SIX, FIVE, FOUR, THREE, TWO
  }
  
  // return random Enum value
  public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
      int x = random.nextInt(clazz.getEnumConstants().length);
      return clazz.getEnumConstants()[x];
  }

  // return random Card from Hand
  public static Card randomCard(Hand hand){
      int x = random.nextInt(hand.getNumberOfCards());
      return hand.get(x);
  }
 
  // return random Card from ArrayList
  public static Card randomCard(ArrayList<Card> list){
      int x = random.nextInt(list.size());
      return list.get(x);
  }

  private static int seed;
  private static Random random = new Random();
	 
  private final String version = "1.0";
  public static int nbPlayers = 4;
  public static int nbRounds = 3;
  public static final int madeBidBonus = 10;
  private static Player[] players = new Player[nbPlayers];

  private final Location[] scoreLocations = {
			  new Location(575, 675),
			  new Location(25, 575),
			  new Location(575, 25),
			  // new Location(650, 575)
			  new Location(575, 575)
	  };
  private Actor[] scoreActors = {null, null, null, null };
  private final Location textLocation = new Location(350, 450);

  private Round currentRound;


  public void setStatus(String string) { setStatusText(string); }

    private static final int INIT_TRICKS = 0;
    private static final int INIT_BIDS = 0;


    Font bigFont = new Font("Serif", Font.BOLD, 36);

    public static Random getRandom() {
        return random;
    }

    public static void setSeed(int seedNum) {
        seed = seedNum;
        random = new Random(seed);
    }

    public static void setNbRounds(int rounds) {
        nbRounds = rounds;
    }

    public static void addPlayer(int playerIndex, Player player) {
        players[playerIndex] = player;
    }

    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            players[i].initPlayerScores();
            String text = "[" + String.valueOf( players[i].getPlayerScore()) + "]" + String.valueOf(INIT_TRICKS) + "/" + String.valueOf(INIT_BIDS);
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }
    }

    public void updateScore(int player, int[] tricks, int[] bids) {
	    removeActor(scoreActors[player]);
	    String text = "[" + String.valueOf(players[player].getPlayerScore()) + "]" + String.valueOf(players[player].getPlayerTricks()) + "/" + String.valueOf(players[player].getPlayerBid());
	    scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
	    addActor(scoreActors[player], scoreLocations[player]);
    }

  public Oh_Heaven(Properties properties)
  {
	super(700, 700, 30);
    setTitle("Oh_Heaven (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
    setStatusText("Initializing...");
    PropertiesLoader.loadProperties(properties);
    initScore();
    for (int i=0; i < nbRounds; i++) {
      currentRound = new Round(this, players, random);
      currentRound.handleRound();
    }
    for (int i=0; i <nbPlayers; i++) updateScore(i, currentRound.getTricks(), currentRound.getBids());
    int maxScore = 0;
    for (int i = 0; i <nbPlayers; i++) if (players[i].getPlayerScore() > maxScore) maxScore = players[i].getPlayerScore();
    Set <Integer> winners = new HashSet<Integer>();
    for (int i = 0; i <nbPlayers; i++) if (players[i].getPlayerScore() == maxScore) winners.add(i);
    String winText;
    if (winners.size() == 1) {
    	winText = "Game over. Winner is player: " +
    			winners.iterator().next();
    }
    else {
    	winText = "Game Over. Drawn winners are players: " +
    			String.join(", ", winners.stream().map(String::valueOf).collect(Collectors.toSet()));
    }
    addActor(new Actor("sprites/gameover.gif"), textLocation);
    setStatusText(winText);
    refresh();
  }

  public static void main(String[] args)
  {
	// System.out.println("Working Directory = " + System.getProperty("user.dir"));
	final Properties properties;
	if (args == null || args.length == 0) {
        properties = PropertiesLoader.loadPropertiesFile(null);
	} else {
	     properties = PropertiesLoader.loadPropertiesFile(args[0]);
	}
    new Oh_Heaven(properties);
  }
}
