package oh_heaven.game;

import ch.aplu.jcardgame.Card;

public class SmartNPC implements NPC {
    private static final int CARD_NOT_FOUND = -1;
    private static final int WORST_RANK = 13;
    private static final int BEST_RANK = -1;
    private int selectedCardIndex;
    private int lowestRankID;
    private int highestRankID;
    private Card selected;

    public Card chooseCard(Player player) {

        selectedCardIndex = CARD_NOT_FOUND;
        lowestRankID = WORST_RANK;
        highestRankID = BEST_RANK;
        selected = null;
        // the player is trying to win this trick to reach the bid / maximise their score if they surpassed the bid
        if (player.getPlayerTricks() != player.getPlayerBid()) {
            // the lead player will play their highest card if trying to win
            if (player.getLeadSuit() == null) {
                findBestCard(player);
                return player.getCurrentHand().getCardList().get(selectedCardIndex);
                // otherwise, the following player will play their best card in the lead suit, or trump suit if they don't
                // have a card in the lead suit. However, the player will check if their best cards have already lost, and thus
                // just accept the loss and play their worst card
            } else {
                // find the highest lead suit card if the following player has one and play it if it isn't guaranteed to lose or it is their only lead suit card
                findBestCardSuited(player, player.getLeadSuit());
                if ((selected != null && isWinning(selected, player))
                        || (selected != null && player.getCurrentHand().getNumberOfCardsWithSuit(player.getLeadSuit()) == 1)) {
                    return selected;
                    // otherwise, play the worst lead suit card
                } else {
                    findWorstCardSuited(player, player.getLeadSuit());
                    if (selected != null) {
                        return selected;
                    }
                    // otherwise, play the best trump suit card if it is winning
                    findBestCardSuited(player, player.getTrumpSuit());
                    if (selected != null && isWinning(selected, player)){
                        return selected;
                    } else {
                        // lastly, play the lowest card as the following player has no chance to win this round
                        findWorstCard(player);
                        return selected;
                    }
                }
            }
            // the player is trying to lose this trick as they have already reached the bid
        } else {
            // the lead player will play their lowest card, preferably not from the trump suit
            if (player.getLeadSuit() == null) {
                findWorstNonTrumpCard(player);
                if (selected != null) {
                    return selected;
                } else {
                    // otherwise find the lowest trump suit card and play it
                    findWorstCard(player);
                    return selected;
                }
            // otherwise the following player will play the worst card in the lead suit, non-trump suit, or trump suit respectively
            } else {
                // find the lowest lead suit card if the player has one and play it
                findWorstCardSuited(player, player.getLeadSuit());
                if (selected != null) {
                    return selected;
                    // else find the lowest non-trump suit card the player has and play it
                } else {
                    findWorstNonTrumpCard(player);
                    if (selected != null) {
                        return selected;
                    } else
                        // play the worst trump suit card as this the player's only option
                        findWorstCardSuited(player, player.getTrumpSuit());
                        return selected;
                }
            }
        }
    }

    public void findBestCard(Player player) {
        int i = 0;
        for (Card card : player.getCurrentHand().getCardList()) {
            if (card.getRankId() < lowestRankID) {
                lowestRankID = card.getRankId();
                selectedCardIndex = i;
            }
            i++;
        }
    }

    public void findBestCardSuited(Player player, Oh_Heaven.Suit suit) {
        int i = 0;
        for (Card card : player.getCurrentHand().getCardList()) {
            if (card.getRankId() < lowestRankID && card.getSuit() == suit) {
                lowestRankID = card.getRankId();
                selectedCardIndex = i;
            }
            i++;
        }
        if (selectedCardIndex != CARD_NOT_FOUND){
            selected = player.getCurrentHand().getCardList().get(selectedCardIndex);
        }
    }

    public void findWorstCard(Player player){
        int i = 0;
        for (Card card : player.getCurrentHand().getCardList()) {
            if (card.getRankId() > highestRankID) {
                highestRankID = card.getRankId();
                selectedCardIndex = i;
            }
            i++;
        }
        if (selectedCardIndex != CARD_NOT_FOUND){
            selected = player.getCurrentHand().getCardList().get(selectedCardIndex);
        }
    }

    public void findWorstCardSuited(Player player, Oh_Heaven.Suit suit) {
        int i = 0;
        for (Card card : player.getCurrentHand().getCardList()) {
            if (card.getRankId() > highestRankID && card.getSuit() == suit) {
                highestRankID = card.getRankId();
                selectedCardIndex = i;
            }
            i++;
        }
        if (selectedCardIndex != CARD_NOT_FOUND){
            selected = player.getCurrentHand().getCardList().get(selectedCardIndex);
        }
    }

    public void findWorstNonTrumpCard(Player player) {
        int i = 0;
        for (Card card : player.getCurrentHand().getCardList()) {
            if (card.getRankId() > highestRankID && card.getSuit() != player.getTrumpSuit()) {
                highestRankID = card.getRankId();
                selectedCardIndex = i;
            }
            i++;
        }
        if (selectedCardIndex != CARD_NOT_FOUND){
            selected = player.getCurrentHand().getCardList().get(selectedCardIndex);
        }
    }

    private boolean isWinning(Card selected, Player player) {
        Card[] tableCards = player.getTableCards();

        for (Card tableCard : tableCards) {
            if (tableCard == null){
                continue;
            }
            // check if there are trump cards in play and the current player isn't playing a trump card
            if (tableCard.getSuit() == player.getTrumpSuit() && selected.getSuit() != player.getTrumpSuit()) {
                return false;
                // check if the current player's trump card is not the best trump card
            } else if (tableCard.getSuit() == player.getTrumpSuit() && tableCard.getRankId() < selected.getRankId()) {
                return false;
                // check if the current player's lead suit card is not the best lead suit card
            } else if (tableCard.getSuit() == player.getLeadSuit() && selected.getSuit() == player.getLeadSuit()
                    && tableCard.getRankId() < selected.getRankId()) {
                return false;
            }
        }
        return true;
    }
    public String getNPCName() {
        return "smart";
    }
}