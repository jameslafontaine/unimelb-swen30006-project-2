package oh_heaven.game;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.Random;

public class Round {

    final String trumpImage[] = {"bigspade.gif","bigheart.gif","bigdiamond.gif","bigclub.gif"};

    private static Random random = Oh_Heaven.getRandom();

    private final int handWidth = 400;
    private final int trickWidth = 40;
    private final Deck deck = new Deck(Oh_Heaven.Suit.values(), Oh_Heaven.Rank.values(), "cover");
    private final int nbPlayers = Oh_Heaven.nbPlayers;
    private final Location trickLocation = new Location(350, 350);

    private Oh_Heaven ohHeaven;
    private Card selected;
    Hand trick;
    int winner;
    Card winningCard;
    Oh_Heaven.Suit lead;
    int nextPlayerIndex;
    private final int thinkingTime = 2000;
    private Hand[] hands;
    private Location hideLocation = new Location(-500, - 500);
    private Location trumpsActorLocation = new Location(50, 50);

    private static boolean enforceRules = false;
    private static int nbStartCards = 13;



    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };


    private int[] tricks = new int[nbPlayers];
    private int[] bids = new int[nbPlayers];
    private Player[] players;

    public Round(Oh_Heaven ohHeaven, Player[] players, Random random) {
        this.ohHeaven = ohHeaven;
        this.players = players;
        this.random = random;
    }




    private void dealingOut(Hand[] hands, int nbPlayers, int nbCardsPerPlayer) {
        Hand pack = deck.toHand(false);
        // pack.setView(Oh_Heaven.this, new RowLayout(hideLocation, 0));
        for (int i = 0; i < nbCardsPerPlayer; i++) {
            for (int j=0; j < nbPlayers; j++) {
                if (pack.isEmpty()) return;
                Card dealt = Oh_Heaven.randomCard(pack);
                // System.out.println("Cards = " + dealt);
                dealt.removeFromHand(false);
                hands[j].insert(dealt, false);
                // dealt.transfer(hands[j], true);
            }
        }
    }

    public boolean rankGreater(Card card1, Card card2) {
        return card1.getRankId() < card2.getRankId(); // Warning: Reverse rank order of cards (see comment on enum)
    }

    public int[] getTricks() {
        return tricks;
    }

    public int[] getBids() {
        return bids;
    }

    private int getNextPlayerIndex() { return nextPlayerIndex; }

    public static void setNbStartCards(int startCards) {
        nbStartCards = startCards;
    }

    public static void setEnforceRules(boolean state) {
        enforceRules = state;
    }

    public void handleRound() {
        initTricks();
        initRound();
        playRound();
        // update every player's personal record of scores
        for (Player player: players) {
            player.updateAllPlayerScores();
        }
    }

    private void initTricks() {
        for (int i = 0; i < nbPlayers; i++) {
            tricks[i] = 0;
            players[i].initPlayerTricks();
        }
    }

    private void initBids(Oh_Heaven.Suit trumps, int nextPlayer) {
        int total = 0;
        for (int i = nextPlayer; i < nextPlayer + nbPlayers; i++) {
            int iP = i % nbPlayers;
            bids[iP] = nbStartCards / 4 + random.nextInt(2);
            total += bids[iP];
        }
        if (total == nbStartCards) {  // Force last bid so not every bid possible
            int iP = (nextPlayer + nbPlayers) % nbPlayers;
            if (bids[iP] == 0) {
                bids[iP] = 1;
            } else {
                bids[iP] += random.nextBoolean() ? -1 : 1;
            }
        }
        // store the players bids in their respective instance
        for (Player player: players) {
            for (int i = 0; i < nbPlayers; i++) {
                player.setBid(bids[i], i);
            }
        }

        // for (int i = 0; i < nbPlayers; i++) {
        // 	 bids[i] = nbStartCards / 4 + 1;
        //  }
    }


    private void initRound() {
        hands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(deck);
        }
        dealingOut(hands, nbPlayers, nbStartCards);
        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, true);
        }
        // Set up human player(s) for interaction
        for (int i=0; i < players.length; i++) {
            if (players[i].isHuman()) {
                CardListener cardListener = new CardAdapter()  // Human Player plays card
                {
                    public void leftDoubleClicked(Card card) { selected = card; hands[getNextPlayerIndex()].setTouchEnabled(false); }
                };
                hands[i].addCardListener(cardListener);
            }
        }

        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            // layouts[i].setStepDelay(10);
            hands[i].setView(ohHeaven, layouts[i]);
            hands[i].setTargetArea(new TargetArea(trickLocation));
            hands[i].draw();
        }
//	    for (int i = 1; i < nbPlayers; i++) // This code can be used to visually hide the cards in a hand (make them face down)
//	      hands[i].setVerso(true);			// You do not need to use or change this code.
        // End graphics
    }

    private void selectCard(Player nextPlayer) {
        if (nextPlayer.isHuman()) {  // Select lead depending on player type
            hands[nextPlayerIndex].setTouchEnabled(true);
            ohHeaven.setStatus("Player " + nextPlayerIndex + " double-click on card to follow.");
            while (null == selected) ohHeaven.delay(100);
        } else {
            ohHeaven.setStatusText("Player " + nextPlayerIndex + " thinking...");
            Oh_Heaven.delay(thinkingTime);
            selected = nextPlayer.chooseCard();
            for (Player player : players) {
                player.addTableCard(selected, nextPlayerIndex);
            }
        }
    }

    private void playRound() {
        final Oh_Heaven.Suit trumps = Oh_Heaven.randomEnum(Oh_Heaven.Suit.class);
        final Actor trumpsActor = new Actor("sprites/"+trumpImage[trumps.ordinal()]);
        for (Player player: players) {
            player.setTrumpSuit(trumps);
        }
        // Display trump suit (trump suit now selected when the variable is declared i.e. when the round is created)
        ohHeaven.addActor(trumpsActor, trumpsActorLocation);
        // End trump suit
        nextPlayerIndex = random.nextInt(nbPlayers); // randomly select player to lead for this round
        initBids(trumps, nextPlayerIndex);
        // initScore();
        for (int i = 0; i < nbPlayers; i++) ohHeaven.updateScore(i, tricks, bids);
        for (int i = 0; i < nbStartCards; i++) {
            for (Player player: players) {
                player.setLeadSuit(null);
                player.clearTableCards();
            }
            lead = null;
            trick = new Hand(deck);
            selected = null;
            Player nextPlayer = players[nextPlayerIndex];
            nextPlayer.setCurrentHand(hands[nextPlayerIndex]);
            // if (false) {
            selectCard(nextPlayer);
            // Lead with selected card
            trick.setView(ohHeaven, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
            trick.draw();
            selected.setVerso(false);
            // No restrictions on the card being lead
            lead = (Oh_Heaven.Suit) selected.getSuit();
            for (Player player: players) {
                player.setLeadSuit(lead);
            }
            selected.transfer(trick, true); // transfer to trick (includes graphic effect)
            winner = nextPlayerIndex;
            winningCard = selected;
            // End Lead
            for (int j = 1; j < nbPlayers; j++) {
                if (++nextPlayerIndex >= nbPlayers) nextPlayerIndex = 0;  // From last back to first
                nextPlayer = players[nextPlayerIndex];
                nextPlayer.setCurrentHand(hands[nextPlayerIndex]);
                selected = null;
                // if (false) {
                selectCard(nextPlayer);
                // Follow with selected card
                trick.setView(ohHeaven, new RowLayout(trickLocation, (trick.getNumberOfCards()+2)*trickWidth));
                trick.draw();
                selected.setVerso(false);  // In case it is upside down
                // Check: Following card must follow suit if possible
                if (selected.getSuit() != lead && hands[nextPlayerIndex].getNumberOfCardsWithSuit(lead) > 0) {
                    // Rule violation
                    String violation = "Follow rule broken by player " + nextPlayerIndex + " attempting to play " + selected;
                    System.out.println(violation);
                    if (enforceRules)
                        try {
                            throw(new BrokeRuleException(violation));
                        } catch (BrokeRuleException e) {
                            e.printStackTrace();
                            System.out.println("A cheating player spoiled the game!");
                            System.exit(0);
                        }
                }
                // player.storeInformation()
                // End Check
                selected.transfer(trick, true); // transfer to trick (includes graphic effect)

                System.out.println("winning: " + winningCard);
                System.out.println(" played: " + selected);
                // System.out.println("winning: suit = " + winningCard.getSuit() + ", rank = " + (13 - winningCard.getRankId()));
                // System.out.println(" played: suit = " +    selected.getSuit() + ", rank = " + (13 -    selected.getRankId()));
                if ( // beat current winner with higher card
                        (selected.getSuit() == winningCard.getSuit() && rankGreater(selected, winningCard)) ||
                                // trumped when non-trump was winning
                                (selected.getSuit() == trumps && winningCard.getSuit() != trumps)) {
                    System.out.println("NEW WINNER");
                    winner = nextPlayerIndex;
                    winningCard = selected;
                }
                // End Follow
            }
            ohHeaven.delay(600);
            trick.setView(ohHeaven, new RowLayout(hideLocation, 0));
            trick.draw();
            nextPlayerIndex = winner;
            ohHeaven.setStatusText("Player " + nextPlayerIndex + " wins trick.");
            // update the next
            tricks[nextPlayerIndex]++;
            for (Player player: players){
                player.incrementTricks(nextPlayerIndex);

            }
            ohHeaven.updateScore(nextPlayerIndex, tricks, bids);
        }
        ohHeaven.removeActor(trumpsActor);
    }
}
