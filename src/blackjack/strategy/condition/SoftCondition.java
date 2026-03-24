package blackjack.strategy.condition;

import blackjack.engine.Card;
import blackjack.engine.Hand;
import blackjack.engine.Rank;

import java.util.HashSet;
import java.util.Set;

//TODO: Replace Rank with card value

public class SoftCondition implements PlayerCondition {
    private final Set<Rank> otherRanks;

    public SoftCondition(Rank otherRank) {
        this.otherRanks = Set.of(otherRank);
    }

    public SoftCondition(Set<Rank> otherRanks) {
        this.otherRanks = new HashSet<>(otherRanks);
    }

    @Override
    public boolean matches(Hand hand) {
        if (hand.size() != 2) {
            return false;
        }

        Card c1 = hand.getCard(0);
        Card c2 = hand.getCard(1);
        boolean firstAce = c1.getRank() == Rank.ACE;
        boolean secondAce = c2.getRank() == Rank.ACE;
        if (firstAce == secondAce) {
            return false; // either no ace or two aces (pair case)
        }

        Rank other = firstAce ? c2.getRank() : c1.getRank();
        if (!otherRanks.contains(other)) {
            return false;
        }

        int raw = 11 + rankValue(other);
        return raw <= 21 && hand.getBestValue() == raw;
    }

    private int rankValue(Rank rank) {
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
