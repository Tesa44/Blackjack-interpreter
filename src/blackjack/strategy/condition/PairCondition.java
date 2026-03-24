package blackjack.strategy.condition;

import blackjack.engine.Hand;
import blackjack.engine.Rank;

public class PairCondition implements PlayerCondition {
    private final Rank pairRank;

    public PairCondition(Rank pairRank) {
        this.pairRank = pairRank;
    }

    @Override
    public boolean matches(Hand hand) {
        return hand.isPair() && hand.getCard(0).getRank() == pairRank;
    }
}
