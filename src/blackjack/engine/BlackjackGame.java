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

        List<PlayerHandSnapshot> finalPlayerHands = playPlayerHands(initialPlayerHand, dealerUpcard, strategy);
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

    private List<PlayerHandSnapshot> playPlayerHands(Hand initialHand, Card dealerUpcard, Strategy strategy) {
        List<PlayerHandSnapshot> result = new ArrayList<>();

        firstAction = strategy.decide(initialHand, dealerUpcard);
        if (firstAction == Action.SPLIT && initialHand.isPair()) {
            HandState h1 = new HandState();
            HandState h2 = new HandState();
            h1.hand.addCard(initialHand.getCard(0));
            h2.hand.addCard(initialHand.getCard(1));

            h1.hand.addCard(deck.draw());
            h2.hand.addCard(deck.draw());

            result.add(toSnapshot(playSingleHand(h1, dealerUpcard, strategy)));
            result.add(toSnapshot(playSingleHand(h2, dealerUpcard, strategy)));
            return result;
        }

        HandState singleHand = new HandState();
        singleHand.hand = initialHand;
        result.add(toSnapshot(playSingleHand(singleHand, dealerUpcard, strategy)));
        return result;
    }

    private HandState playSingleHand(HandState handState, Card dealerUpcard, Strategy strategy) {
        while (true) {
            if (handState.hand.getBestValue() > 21) {
                return handState;
            }

            Action action = strategy.decide(handState.hand, dealerUpcard);
            switch (action) {
                case STAND -> {
                    return handState;
                }
                case HIT -> handState.hand.addCard(deck.draw());
                case DOUBLE -> {
                    handState.betMultiplier *= 2;
                    handState.hand.addCard(deck.draw());
                    return handState;
                }
                case SPLIT -> {
                    //TODO: consider recursive splitting
                    //No recursive splitting. If a split is requested here, treat as HIT.
                    handState.hand.addCard(deck.draw());
                }
            }
        }
    }

    private PlayerHandSnapshot toSnapshot(HandState handState) {
        return new PlayerHandSnapshot(handState.hand, handState.betMultiplier);
    }

    private void playDealer() {
        while (dealerHand.getBestValue() < 17) {
            dealerHand.addCard(deck.draw());
        }
    }

    private static class HandState {
        private Hand hand = new Hand();
        private int betMultiplier = 1;
    }
}

