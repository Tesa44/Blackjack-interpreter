package blackjack.engine;

import blackjack.strategy.Action;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Predicate;

public class RoundResult {
        public final Hand dealerHand;
        private final int dealerValue;
        private final int dealerUpcardValue;
        private final int playerInitialTotal;
        public final Map<Hand, Integer> playerHandsWithBestValues;
        private final List<Integer> playerHandBetMultipliers;
        public final Result result;
        public Action action;

        public RoundResult(Hand dealerHand, int dealerUpcardValue, int playerInitialTotal, List<PlayerHandSnapshot> playerHands, Action action) {
            this.dealerHand = new Hand(dealerHand);
            this.dealerValue = this.dealerHand.getBestValue();
            this.dealerUpcardValue = dealerUpcardValue;
            this.playerInitialTotal = playerInitialTotal;
            this.playerHandsWithBestValues = new LinkedHashMap<>();
            this.playerHandBetMultipliers = new ArrayList<>();
            capturePlayerHands(playerHands);
            this.action = action;
            this.result = evaluateRound();
        }

        private void capturePlayerHands(List<PlayerHandSnapshot> playerHands) {
            for (PlayerHandSnapshot handSnapshotSource : playerHands) {
                Hand handSnapshot = handSnapshotSource.getHand();
                playerHandsWithBestValues.put(handSnapshot, handSnapshot.getBestValue());
                playerHandBetMultipliers.add(handSnapshotSource.getBetMultiplier());
            }
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

    public int getDealerUpcardValue() {
        return dealerUpcardValue;
    }

    public Map<Hand, Integer> getPlayerHandsWithBestValues() {
        return playerHandsWithBestValues;
    }

    public int getPlayerInitialTotal() {
        return playerInitialTotal;
    }

    public Action getAction() {
        return action;
    }

    public boolean hasPlayerTotal(int total) {
        return playerHandsWithBestValues.values().stream().anyMatch(value -> value == total);
    }

    public boolean hasPlayerTotal(String operator, int total) {
        return playerHandsWithBestValues.values().stream()
                .anyMatch(value -> compare(value, operator, total));
    }

    public boolean hasPlayerTotalInRange(int minTotal, int maxTotal) {
        return playerHandsWithBestValues.values().stream()
                .anyMatch(value -> value >= minTotal && value <= maxTotal);
    }

    public boolean hasPlayerCard(Rank rank) {
        return playerHandsWithBestValues.keySet().stream()
                .anyMatch(hand -> handContainsRank(hand, rank));
    }

    public boolean hasDealerCard(Rank rank) {
        return handContainsRank(dealerHand, rank);
    }

    public boolean hasPlayerPair() {
        return anyPlayerHandMatches(Hand::isPair);
    }

    public boolean hasPlayerSoftHand() {
        return anyPlayerHandMatches(Hand::isSoft);
    }

    public int getNetBetUnits() {
        int netUnits = 0;
        boolean dealerBust = dealerValue > 21;

        int handIndex = 0;
        for (Map.Entry<Hand, Integer> entry : playerHandsWithBestValues.entrySet()) {
            int playerValue = entry.getValue();
            int betMultiplier = playerHandBetMultipliers.get(handIndex);
            Result handResult = evaluateSingle(playerValue, dealerValue, dealerBust);

            if (handResult == Result.PLAYER_WIN) {
                netUnits += betMultiplier;
            } else if (handResult == Result.DEALER_WIN) {
                netUnits -= betMultiplier;
            }
            handIndex++;
        }

        return netUnits;
    }

    private boolean compare(int actualValue, String operator, int targetValue) {
        return switch (operator) {
            case "=" -> actualValue == targetValue;
            case ">" -> actualValue > targetValue;
            case "<" -> actualValue < targetValue;
            case ">=" -> actualValue >= targetValue;
            case "<=" -> actualValue <= targetValue;
            default -> throw new IllegalArgumentException("Unsupported comparison operator: " + operator);
        };
    }

    private boolean handContainsRank(Hand hand, Rank rank) {
        return hand.getCards().stream().anyMatch(card -> card.getRank() == rank);
    }

    private boolean anyPlayerHandMatches(Predicate<Hand> predicate) {
        return playerHandsWithBestValues.keySet().stream().anyMatch(predicate);
    }

    public String getPlayerTotalsSummary() {
        StringJoiner joiner = new StringJoiner(", ");
        for (int playerValue : playerHandsWithBestValues.values()) {
            joiner.add(String.valueOf(playerValue));
        }
        return joiner.toString();
    }
}

