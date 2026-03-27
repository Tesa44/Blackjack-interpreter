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
            this.playerHandsWithBestValues = calculateBestPlayerValues(playerHands);
            this.action = action;
            this.result = evaluateRound();
        }

        private Map<Hand, Integer> calculateBestPlayerValues(List<Hand> playerHands) {
            Map<Hand, Integer> initPlayerHandsWithBestValues = new HashMap<>();
            for (Hand hand : playerHands) {
                initPlayerHandsWithBestValues.put(hand, hand.getBestValue());
            }
            return initPlayerHandsWithBestValues;
        }

    private Result evaluateRound() {
        boolean dealerBust = dealerValue > 21;

        boolean anyPlayerWin = false;
        boolean anyDealerWin = false;

        for (int playerValue : playerHandsWithBestValues.values()) {
            Result r = evaluateSingle(playerValue, dealerValue, dealerBust);
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

    private Result evaluateSingle(int playerValue, int dealerValue, boolean dealerBust) {
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

    public void printRoundSummary() {
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

        int i = 1;
        for (Map.Entry<Hand,Integer> entry : playerHandsWithBestValues.entrySet()) {
            try {
                Hand hand = entry.getKey();
                int playerBestValue = entry.getValue();
                Result handResult = evaluateSingle(playerBestValue, dealerValue, dealerBust);
                System.out.println(
                        "Player hand #" + i + ": " + formatHand(hand) + " (" + playerBestValue + ") -> " + handResult
                );
            } catch (RuntimeException e) {
                System.out.println("Player hand #" + i + ": could not print/evaluate hand (" + e.getMessage() + ")");
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


    public Result getResult() {
        return result;
    }
}

