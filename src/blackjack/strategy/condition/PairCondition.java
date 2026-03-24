package blackjack.strategy.condition;

import blackjack.engine.Hand;
import blackjack.engine.Rank;

public class PairCondition implements PlayerCondition {
//    private final Rank pairRank;
    private final int pairValue;

//    public PairCondition(Rank pairRank) {
//        this.pairRank = pairRank;
//    }
    public PairCondition(int pairValue) {
        this.pairValue = pairValue;
    }

    @Override
    public boolean matches(Hand hand) {
        return hand.isPair() && hand.getCard(0).getValue() == pairValue;
    }
}
