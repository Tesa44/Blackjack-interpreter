package blackjack.engine;

import blackjack.strategy.Action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundResult {
        public final Hand dealerHand;
        private final int dealerValue;
        public final Map<Hand, Integer> playerHandsWithBestValues;
        public final Result result;
        public Action action;

        public RoundResult(Hand dealerHand, List<Hand> playerHands, Action action) {
            this.dealerHand = dealerHand;
            this.dealerValue = dealerHand.getBestValue();
            this.result = evaluateRound();
            this.playerHandsWithBestValues = calculateBestPlayerValues(playerHands);
            this.action = action;

        }

        private Map<Hand, Integer> calculateBestPlayerValues(List<Hand> playerHands) {
            Map<Hand, Integer> initPlayerHandsWithBestValues = new HashMap<>();
            for (Hand hand : playerHands) {
                initPlayerHandsWithBestValues.put(hand, hand.getBestValue());
            }
            return initPlayerHandsWithBestValues;
        }

    public Result evaluateRound() {
        boolean dealerBust = dealerValue > 21;

        boolean anyPlayerWin = false;
        boolean anyDealerWin = false;

        for (Hand playerHand : playerHandsWithBestValues.keySet()) {
            Result r = evaluateSingle(playerHand, dealerValue, dealerBust);
            if (r == Result.PLAYER_WIN) {
                anyPlayerWin = true;
            } else if (r == Result.DEALER_WIN) {
                anyDealerWin = true;
            }
        }

        if (anyPlayerWin && !anyDealerWin) {
            return Result.PLAYER_WIN;
        }
        if (anyDealerWin && !anyPlayerWin) {
            return Result.DEALER_WIN;
        }
        return Result.PUSH;
    }

    private Result evaluateSingle(Hand playerHand, int dealerValue, boolean dealerBust) {
        int playerValue = playerHand.getBestValue();
        boolean playerBust = playerValue > 21;

        if (playerBust && dealerBust) {
            return Result.PUSH;
        } else if (playerBust) {
            return Result.DEALER_WIN;
        } else if (dealerBust) {
            return Result.PLAYER_WIN;
        }

        if (playerValue > dealerValue) {
            return Result.PLAYER_WIN;
        } else if (dealerValue > playerValue) {
            return Result.DEALER_WIN;
        } else {
            return Result.PUSH;
        }
    }

    private void printRoundSummary() {
        boolean dealerBust = dealerValue > 21;

        System.out.println("Dealer final hand: " + formatHand(dealerHand) + " (" + dealerValue + ")");

        if (playerHandsWithBestValues == null || playerHandsWithBestValues.isEmpty()) {
            System.out.println("Player final hand: <none>");
            System.out.println("Result: " + result);
            return;
        }
        if (playerHandsWithBestValues.size() == 1) {
            Map.Entry<Hand, Integer> entry = playerHandsWithBestValues.entrySet().iterator().next();
            System.out.println("Player final hand: " + formatHand(entry.getKey()) + " (" + entry.getValue() + ")");
            System.out.println("Result: " + result);
            return;
        }

        for (int i = 0; i < playerHandsWithBestValues.size(); i++) {
            try {
                Hand hand = playerHands.get(i);
                Result handResult = evaluateSingle(hand, dealerValue, dealerBust);
                System.out.println(
                        "Player hand #" + (i + 1) + ": " + formatHand(hand) + " (" + hand.getBestValue() + ") -> " + handResult
                );
            } catch (RuntimeException e) {
                System.out.println("Player hand #" + (i + 1) + ": could not print/evaluate hand (" + e.getMessage() + ")");
            }
        }

        System.out.println("Final result: " + result);
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

