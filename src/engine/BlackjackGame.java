package engine;

/**
 * Core Blackjack game engine.
 * For the first version, this implements a very simple set of rules:
 * - Single deck shoe.
 * - Player hits until 17 or more.
 * - Dealer hits until 17 or more.
 * - No splitting, doubling, or betting yet.
 */
public class BlackjackGame {

    private final Deck deck;
    private final Hand playerHand = new Hand();
    private final Hand dealerHand = new Hand();

    private RoundResult lastResult;

    public BlackjackGame(Deck deck) {
        this.deck = deck;
    }

    public Deck getDeck() {
        return deck;
    }

    public Hand getPlayerHand() {
        return playerHand;
    }

    public Hand getDealerHand() {
        return dealerHand;
    }

    public RoundResult getLastResult() {
        return lastResult;
    }

    /**
     * Plays a complete round of Blackjack using a fixed simple strategy:
     * - Player hits until hand value >= 17.
     * - Dealer hits until hand value >= 17.
     * Returns the outcome of the round.
     */
    public RoundResult playSimpleRound() {
        startRound();
        playPlayer();
        playDealer();
        lastResult = evaluate();
        return lastResult;
    }

    private void startRound() {
        playerHand.clear();
        dealerHand.clear();

        // initial deal: two cards each, player first
        playerHand.addCard(deck.draw());
        dealerHand.addCard(deck.draw());
        playerHand.addCard(deck.draw());
        dealerHand.addCard(deck.draw());
    }

    private void playPlayer() {
        while (playerHand.getBestValue() < 17) {
            playerHand.addCard(deck.draw());
        }
    }

    private void playDealer() {
        while (dealerHand.getBestValue() < 17) {
            dealerHand.addCard(deck.draw());
        }
    }

    private RoundResult evaluate() {
        int playerValue = playerHand.getBestValue();
        int dealerValue = dealerHand.getBestValue();

        boolean playerBust = playerValue > 21;
        boolean dealerBust = dealerValue > 21;

        if (playerBust && dealerBust) {
            return RoundResult.PUSH;
        } else if (playerBust) {
            return RoundResult.DEALER_WIN;
        } else if (dealerBust) {
            return RoundResult.PLAYER_WIN;
        }

        if (playerValue > dealerValue) {
            return RoundResult.PLAYER_WIN;
        } else if (dealerValue > playerValue) {
            return RoundResult.DEALER_WIN;
        } else {
            return RoundResult.PUSH;
        }
    }
}

