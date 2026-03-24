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

    public boolean matches(Card dealerCard) {
        int value = upcardValue(dealerCard.getRank());
        return value >= minInclusive && value <= maxInclusive;
    }

    private int upcardValue(Rank rank) {
        return switch (rank) {
            case TWO -> 2;
            case THREE -> 3;
            case FOUR -> 4;
            case FIVE -> 5;
            case SIX -> 6;
            case SEVEN -> 7;
            case EIGHT -> 8;
            case NINE -> 9;
            case TEN, JACK, QUEEN, KING -> 10;
            case ACE -> 11;
        };
    }
}
