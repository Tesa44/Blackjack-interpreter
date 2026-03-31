package blackjack.engine;

import blackjack.strategy.Action;
import blackjack.strategy.Strategy;

import java.util.ArrayList;
import java.util.List;


//TODO: Fix results when player's action is SPLIT.
// We should delete Final result and add separated results


public class BlackjackGame {
    private final Deck deck;
    private final Hand dealerHand = new Hand();
    private Action firstAction = null;

    public BlackjackGame(Deck deck) {
        this.deck = deck;
    }

    public RoundResult playRound(Strategy strategy) {
        Hand initialPlayerHand = new Hand();
        startRound(initialPlayerHand);

        Card dealerUpcard = dealerHand.getCard(0);
        int dealerUpcardValue = dealerUpcard.getValue();
        int playerInitialTotal = initialPlayerHand.getBestValue();

        List<Hand> finalPlayerHands = playPlayerHands(initialPlayerHand, dealerUpcard, strategy);
        playDealer();

        return new RoundResult(dealerHand, dealerUpcardValue, playerInitialTotal, finalPlayerHands, firstAction);
    }

    private void startRound(Hand playerHand) {
        playerHand.clear();
        dealerHand.clear();

        playerHand.addCard(deck.draw());
        dealerHand.addCard(deck.draw());
        playerHand.addCard(deck.draw());
        dealerHand.addCard(deck.draw());
    }

    private List<Hand> playPlayerHands(Hand initialHand, Card dealerUpcard, Strategy strategy) {
        List<Hand> result = new ArrayList<>();

        firstAction = strategy.decide(initialHand, dealerUpcard);
        if (firstAction == Action.SPLIT && initialHand.isPair()) {
            Hand h1 = new Hand();
            Hand h2 = new Hand();
            h1.addCard(initialHand.getCard(0));
            h2.addCard(initialHand.getCard(1));

            h1.addCard(deck.draw());
            h2.addCard(deck.draw());

            result.add(playSingleHand(h1, dealerUpcard, strategy));
            result.add(playSingleHand(h2, dealerUpcard, strategy));
            return result;
        }

        result.add(playSingleHand(initialHand, dealerUpcard, strategy));
        return result;
    }

    private Hand playSingleHand(Hand hand, Card dealerUpcard, Strategy strategy) {
        while (true) {
            if (hand.getBestValue() > 21) {
                return hand;
            }

            Action action = strategy.decide(hand, dealerUpcard);
            switch (action) {
                case STAND -> {
                    return hand;
                }
                case HIT -> hand.addCard(deck.draw());
                case DOUBLE -> {
                    // TODO: add betting/money impact for DOUBLE once bankroll logic exists
                    hand.addCard(deck.draw());
                    return hand;
                }
                case SPLIT -> {
                    //TODO: consider recursive splitting
                    //No recursive splitting. If a split is requested here, treat as HIT.
                    hand.addCard(deck.draw());
                }
            }
        }
    }

    private void playDealer() {
        while (dealerHand.getBestValue() < 17) {
            dealerHand.addCard(deck.draw());
        }
    }
}

