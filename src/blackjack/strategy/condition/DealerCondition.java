package blackjack.strategy.condition;

import blackjack.engine.Card;
import blackjack.engine.Rank;

public class DealerCondition {
    private final int minInclusive;
    private final int maxInclusive;

    public DealerCondition(int minInclusive, int maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }
    public DealerCondition(int inclusive) {
        this.minInclusive = inclusive;
        this.maxInclusive = inclusive;
    }

    public boolean matches(Card dealerCard) {
        int value = dealerCard.getValue();
        return value >= minInclusive && value <= maxInclusive;
    }

}
