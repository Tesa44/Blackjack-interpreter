package blackjack.strategy.condition;

import blackjack.engine.Hand;

public class TotalCondition implements PlayerCondition {
    private final int minInclusive;
    private final int maxInclusive;

    public TotalCondition(int minInclusive, int maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    @Override
    public boolean matches(Hand hand) {
        int total = hand.getBestValue();
        return total >= minInclusive && total <= maxInclusive;
    }
}
