package blackjack.engine;

import blackjack.strategy.Action;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public List<Integer> getPlayerHandBetMultipliers() {
        return Collections.unmodifiableList(playerHandBetMultipliers);
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

}

