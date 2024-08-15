package oh_heaven.game;

import ch.aplu.jcardgame.*;

public class Player {

    private NPC npcType;
    private int playerNum;
    private Hand currentHand;
    private Oh_Heaven.Suit currentTrump;
    private Oh_Heaven.Suit currentLead;
    private boolean isHuman = false;
    private Card[] tableCards = new Card[Oh_Heaven.nbPlayers];
    private int[] allPlayerTricks = new int[Oh_Heaven.nbPlayers];
    private int[] allPlayerScores = new int[Oh_Heaven.nbPlayers];
    private int[] allPlayerBids = new int[Oh_Heaven.nbPlayers];

    public Player(NPC npcType, int playerNum) {
        if (npcType != null) {
            this.npcType = npcType;
        } else {
            isHuman = true;
        }
        this.playerNum = playerNum;
    }

    public Hand getCurrentHand() {
        return currentHand;
    }

    public Oh_Heaven.Suit getLeadSuit() {
        return currentLead;
    }

    public Oh_Heaven.Suit getTrumpSuit() {
        return currentTrump;
    }

    public int getPlayerTricks() { return allPlayerTricks[playerNum]; }

    public int getPlayerBid() { return allPlayerBids[playerNum]; }

    public int getPlayerScore() { return allPlayerScores[playerNum]; }

    public Card[] getTableCards() { return tableCards; }

    public void setCurrentHand(Hand hand) {
        currentHand = hand;
    }

    public void setLeadSuit(Oh_Heaven.Suit lead) {
        currentLead = lead;
    }

    public void setTrumpSuit(Oh_Heaven.Suit trump) {
        currentTrump = trump;
    }

    public void setBid(int bid, int playerIndex) { allPlayerBids[playerIndex] = bid;
    }

    public boolean isHuman() {
        return isHuman;
    }



    public void addTableCard(Card card, int nextPlayerIndex) {
        tableCards[nextPlayerIndex] = card;
    }

    public void clearTableCards() {
        for (int i = 0; i < Oh_Heaven.nbPlayers; i++) {
            tableCards[i] = null;
        }
    }

    public void initPlayerTricks() {
        for (int i=0; i < Oh_Heaven.nbPlayers; i++) {
            allPlayerTricks[i] = 0;
        }
    }

    public void initPlayerScores() {
        for (int i=0; i < Oh_Heaven.nbPlayers; i++) {
            allPlayerScores[i] = 0;
        }
    }

    public void incrementTricks(int nextPlayerIndex) {
        allPlayerTricks[nextPlayerIndex]++;
    }



    public void updateAllPlayerScores() {
        for (int i = 0; i < Oh_Heaven.nbPlayers; i++) {
            allPlayerScores[i] += allPlayerTricks[i];
            if (allPlayerTricks[i] == allPlayerBids[i]) allPlayerScores[i] += Oh_Heaven.madeBidBonus;
        }
    }

    public Card chooseCard() {
        return npcType.chooseCard(this);
    }
}
