package blackjack.strategy;

import blackjack.engine.Rank;
import blackjack.strategy.condition.CompositeCondition;
import blackjack.strategy.condition.DealerCondition;
import blackjack.strategy.condition.PairCondition;
import blackjack.strategy.condition.PlayerCondition;
import blackjack.strategy.condition.SoftCondition;
import blackjack.strategy.condition.TotalCondition;

import java.util.List;

public class BasicStrategyConfig {
    public static Strategy create() {
        return new Strategy(List.of(
                new Rule(
                        new TotalCondition(17,21),
                        new DealerCondition(2,11),
                        Action.STAND
                ),
                // Pair of 8s vs dealer 2–6 -> SPLIT
                new Rule(
                        new PairCondition(Rank.EIGHT),
                        new DealerCondition(2, 6),
                        Action.SPLIT
                ),
                // Total 12–16 vs dealer 2–6 -> STAND
                new Rule(
                        new TotalCondition(12, 16),
                        new DealerCondition(2, 6),
                        Action.STAND
                ),
                // A+2 or A+3 vs dealer 5–6 -> DOUBLE
                new Rule(
                        new CompositeCondition(List.of(
                                new SoftCondition(Rank.TWO),
                                new SoftCondition(Rank.THREE)
                        )),
                        new DealerCondition(5, 6),
                        Action.DOUBLE
                ),
                new Rule(
                        new TotalCondition(12,16),
                        new DealerCondition(7,11),
                        Action.HIT
                )

        ));
    }
}
