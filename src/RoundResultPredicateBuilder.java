import blackjack.engine.RoundResult;
import blackjack.engine.Rank;
import blackjack.strategy.Action;

import java.util.function.Predicate;

public class RoundResultPredicateBuilder {
    public Predicate<RoundResult> comparison(String propertyName, String operator, String targetValue) {
        if ("action".equals(propertyName)) {
            Action expectedAction = Action.valueOf(targetValue);
            return roundResult -> matchesAction(roundResult, operator, expectedAction);
        }

        if ("true".equalsIgnoreCase(targetValue) || "false".equalsIgnoreCase(targetValue)) {
            boolean targetBoolean = Boolean.parseBoolean(targetValue);
            return roundResult -> matchesBoolean(roundResult, propertyName, operator, targetBoolean);
        }

        int targetTotal = Integer.parseInt(targetValue);
        return roundResult -> matches(roundResult, propertyName, operator, targetTotal);
    }

    public Predicate<RoundResult> range(String propertyName, int minTotal, int maxTotal) {
        if (minTotal > maxTotal) {
            throw new IllegalArgumentException("Invalid range in query DSL: min is greater than max");
        }

        return roundResult -> matchesRange(roundResult, propertyName, minTotal, maxTotal);
    }

    public Predicate<RoundResult> contains(String propertyName, Rank rank) {
        return roundResult -> matchesContains(roundResult, propertyName, rank);
    }

    private boolean matches(RoundResult roundResult, String propertyName, String operator, int targetTotal) {
        return switch (propertyName) {
            case "player.total" -> roundResult.hasPlayerTotal(operator, targetTotal);
            case "player.initialTotal", "player.InitialTotal", "player.init", "player.Init" ->
                    compare(roundResult.getPlayerInitialTotal(), operator, targetTotal);
            case "dealer.total" -> compare(roundResult.getDealerValue(), operator, targetTotal);
            case "dealer.upcard", "dealer.Upcard", "dealer.init", "dealer.Init" ->
                    compare(roundResult.getDealerUpcardValue(), operator, targetTotal);
            default -> false;
        };
    }

    private boolean matchesBoolean(RoundResult roundResult, String propertyName, String operator, boolean expectedValue) {
        if (!"=".equals(operator)) {
            throw new IllegalArgumentException("Boolean properties only support '=' comparisons: " + propertyName);
        }

        return switch (propertyName) {
            case "player.isPair", "player.IsPair" -> roundResult.hasPlayerPair() == expectedValue;
            case "player.isSoft", "player.IsSoft" -> roundResult.hasPlayerSoftHand() == expectedValue;
            default -> false;
        };
    }

    private boolean matchesAction(RoundResult roundResult, String operator, Action expectedAction) {
        if (!"=".equals(operator)) {
            throw new IllegalArgumentException("Action filters only support '=' comparisons.");
        }

        return roundResult.getAction() == expectedAction;
    }

    private boolean matchesRange(RoundResult roundResult, String propertyName, int minTotal, int maxTotal) {
        return switch (propertyName) {
            case "player.total" -> roundResult.hasPlayerTotalInRange(minTotal, maxTotal);
            case "player.initialTotal", "player.InitialTotal", "player.init", "player.Init" ->
                    roundResult.getPlayerInitialTotal() >= minTotal && roundResult.getPlayerInitialTotal() <= maxTotal;
            case "dealer.total" -> roundResult.getDealerValue() >= minTotal && roundResult.getDealerValue() <= maxTotal;
            case "dealer.upcard", "dealer.Upcard", "dealer.init", "dealer.Init" ->
                    roundResult.getDealerUpcardValue() >= minTotal && roundResult.getDealerUpcardValue() <= maxTotal;
            default -> false;
        };
    }

    private boolean matchesContains(RoundResult roundResult, String propertyName, Rank rank) {
        return switch (propertyName) {
            case "player.cards" -> roundResult.hasPlayerCard(rank);
            case "dealer.cards" -> roundResult.hasDealerCard(rank);
            default -> false;
        };
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
}
