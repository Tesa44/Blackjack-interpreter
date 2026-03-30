package blackjack.engine;

import blackjack.strategy.Action;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class RoundResult {
        public final Hand dealerHand;
        private final int dealerValue;
        public final Map<Hand, Integer> playerHandsWithBestValues;
        public final Result result;
        public Action action;

        public RoundResult(Hand dealerHand, List<Hand> playerHands, Action action) {
            this.dealerHand = new Hand(dealerHand);
            this.dealerValue = this.dealerHand.getBestValue();
            this.playerHandsWithBestValues = calculateBestPlayerValues(playerHands);
            this.action = action;
            this.result = evaluateRound();
        }

        private Map<Hand, Integer> calculateBestPlayerValues(List<Hand> playerHands) {
            Map<Hand, Integer> initPlayerHandsWithBestValues = new LinkedHashMap<>();
            for (Hand hand : playerHands) {
                Hand handSnapshot = new Hand(hand);
                initPlayerHandsWithBestValues.put(handSnapshot, handSnapshot.getBestValue());
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
        System.out.println(describeRoundSummary());
    }

    public String describeRoundSummary() {
        boolean dealerBust = dealerValue > 21;
        StringBuilder summary = new StringBuilder();
        summary.append("Dealer final hand: ")
                .append(formatHand(dealerHand))
                .append(" (")
                .append(dealerValue)
                .append(")")
                .append(System.lineSeparator());

        if (playerHandsWithBestValues == null || playerHandsWithBestValues.isEmpty()) {
            summary.append("Player final hand: <none>").append(System.lineSeparator());
            summary.append("Result: ").append(result);
            return summary.toString();
        }
        if (playerHandsWithBestValues.size() == 1) {
            Map.Entry<Hand, Integer> entry = playerHandsWithBestValues.entrySet().iterator().next();
            summary.append("Player final hand: ")
                    .append(formatHand(entry.getKey()))
                    .append(" (")
                    .append(entry.getValue())
                    .append(")")
                    .append(System.lineSeparator());
            summary.append("Result: ").append(result);
            return summary.toString();
        }

        int i = 1;
        for (Map.Entry<Hand,Integer> entry : playerHandsWithBestValues.entrySet()) {
            try {
                Hand hand = entry.getKey();
                int playerBestValue = entry.getValue();
                Result handResult = evaluateSingle(playerBestValue, dealerValue, dealerBust);
                summary.append("Player hand #")
                        .append(i)
                        .append(": ")
                        .append(formatHand(hand))
                        .append(" (")
                        .append(playerBestValue)
                        .append(") -> ")
                        .append(handResult)
                        .append(System.lineSeparator());
            } catch (RuntimeException e) {
                summary.append("Player hand #")
                        .append(i)
                        .append(": could not print/evaluate hand (")
                        .append(e.getMessage())
                        .append(")")
                        .append(System.lineSeparator());
            }
            i++;
        }

        summary.append("Final result: ").append(result);
        return summary.toString();
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

    public int getDealerValue() {
        return dealerValue;
    }

    public Map<Hand, Integer> getPlayerHandsWithBestValues() {
        return playerHandsWithBestValues;
    }

    public Action getAction() {
        return action;
    }

    public boolean hasPlayerTotal(int total) {
        return playerHandsWithBestValues.values().stream().anyMatch(value -> value == total);
    }

    public String getPlayerTotalsSummary() {
        StringJoiner joiner = new StringJoiner(", ");
        for (int playerValue : playerHandsWithBestValues.values()) {
            joiner.add(String.valueOf(playerValue));
        }
        return joiner.toString();
    }
}

