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

    private RoundResult lastResult;

    public BlackjackGame(Deck deck) {
        this.deck = deck;
    }

    public Deck getDeck() {
        return deck;
    }

    public Hand getDealerHand() {
        return dealerHand;
    }

    public RoundResult getLastResult() {
        return lastResult;
    }

    public RoundResult playRound(Strategy strategy) {
        Hand initialPlayerHand = new Hand();
        startRound(initialPlayerHand);

        Card dealerUpcard = dealerHand.getCard(0);
        System.out.println("=== Round Summary ===");
        System.out.println("Player sees " + dealerUpcard.toString() + " dealer's card");
        System.out.println("Player cards" + formatHand(initialPlayerHand));
        List<Hand> finalPlayerHands = playPlayerHands(initialPlayerHand, dealerUpcard, strategy);
        playDealer();

        lastResult = evaluateMany(finalPlayerHands);
        printRoundSummary(finalPlayerHands, lastResult, strategy);
        return lastResult;
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

        Action first = strategy.decide(initialHand, dealerUpcard);
        System.out.println("ACTION: " + first.name());
        if (first == Action.SPLIT && initialHand.isPair()) {
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
            System.out.println("ACTION: " + action.name());
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

    private RoundResult evaluateMany(List<Hand> playerHands) {
        int dealerValue = dealerHand.getBestValue();
        boolean dealerBust = dealerValue > 21;

        boolean anyPlayerWin = false;
        boolean anyDealerWin = false;

        for (Hand playerHand : playerHands) {
            RoundResult r = evaluateSingle(playerHand, dealerValue, dealerBust);
            if (r == RoundResult.PLAYER_WIN) {
                anyPlayerWin = true;
            } else if (r == RoundResult.DEALER_WIN) {
                anyDealerWin = true;
            }
        }

        if (anyPlayerWin && !anyDealerWin) {
            return RoundResult.PLAYER_WIN;
        }
        if (anyDealerWin && !anyPlayerWin) {
            return RoundResult.DEALER_WIN;
        }
        return RoundResult.PUSH;
    }

    private RoundResult evaluateSingle(Hand playerHand, int dealerValue, boolean dealerBust) {
        int playerValue = playerHand.getBestValue();
        boolean playerBust = playerValue > 21;

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

    private void printRoundSummary(List<Hand> playerHands, RoundResult finalResult, Strategy strategy) {
        int dealerValue = dealerHand.getBestValue();
        boolean dealerBust = dealerValue > 21;

        System.out.println("Dealer final hand: " + formatHand(dealerHand) + " (" + dealerValue + ")");

        if (playerHands == null || playerHands.isEmpty()) {
            System.out.println("Player final hand: <none>");
            System.out.println("Result: " + finalResult);
            return;
        }
        if (playerHands.size() == 1) {
            Hand playerHand = playerHands.get(0);
            RoundResult handResult = evaluateSingle(playerHand, dealerValue, dealerBust);
            System.out.println("Player final hand: " + formatHand(playerHand) + " (" + playerHand.getBestValue() + ")");
            System.out.println("Result: " + handResult);
            return;
        }

        for (int i = 0; i < playerHands.size(); i++) {
            try {
                Hand hand = playerHands.get(i);
                RoundResult handResult = evaluateSingle(hand, dealerValue, dealerBust);
                System.out.println(
                        "Player hand #" + (i + 1) + ": " + formatHand(hand) + " (" + hand.getBestValue() + ") -> " + handResult
                );
            } catch (RuntimeException e) {
                System.out.println("Player hand #" + (i + 1) + ": could not print/evaluate hand (" + e.getMessage() + ")");
            }
        }

        System.out.println("Final result: " + finalResult);
    }

    private String formatHand(Hand hand) {
        StringBuilder sb = new StringBuilder("[");
        List<Card> cards = hand.getCards();

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            sb.append(card.getRank()).append(" of ").append(card.getSuit());
            if (i < cards.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");
        return sb.toString();
    }
}

