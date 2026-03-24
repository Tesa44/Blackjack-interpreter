package blackjack.strategy;

import blackjack.engine.Rank;
import blackjack.strategy.condition.CompositeCondition;
import blackjack.strategy.condition.DealerCondition;
import blackjack.strategy.condition.PairCondition;
import blackjack.strategy.condition.PlayerCondition;
import blackjack.strategy.condition.SoftCondition;
import blackjack.strategy.condition.TotalCondition;

import java.util.List;
import java.util.SortedMap;

public class BasicStrategyConfig {
    public static Strategy create() {
        return new Strategy(List.of(

// ==========================PAIRS================================================
                // A + A vs anything -> SPLIT
                new Rule(
                        new PairCondition(11),
                        new DealerCondition(2, 11),
                        Action.SPLIT
                ),
                // Pair of 8s vs dealer 2–6 -> SPLIT
                new Rule(
                        new PairCondition(8),
                        new DealerCondition(2, 11),
                        Action.SPLIT
                ),
                new Rule(
                        new PairCondition(10),
                        new DealerCondition(2, 11),
                        Action.STAND
                ),
                new Rule(
                        new PairCondition(5),
                        new DealerCondition(2, 11),
                        Action.DOUBLE
                ),
                new Rule(
                        new CompositeCondition(List.of(
                            new PairCondition(2),
                            new PairCondition(3)
                        )),
                        new DealerCondition(2, 7),
                        Action.SPLIT
                ),
                new Rule(
                        new PairCondition(4),
                        new DealerCondition(5, 6),
                        Action.SPLIT
                ),
                new Rule(
                        new PairCondition(6),
                        new DealerCondition(2, 6),
                        Action.SPLIT
                ),
                new Rule(
                        new PairCondition(7),
                        new DealerCondition(2, 7),
                        Action.SPLIT
                ),
                new Rule(
                        new PairCondition(9),
                        new DealerCondition(2, 9),
                        Action.SPLIT
                ),
//========================SOFT HANDS============================================
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
                        new CompositeCondition(List.of(
                                new SoftCondition(Rank.FOUR),
                                new SoftCondition(Rank.FIVE)
                        )),
                        new DealerCondition(4, 6),
                        Action.DOUBLE
                ),
                new Rule(
                        new SoftCondition(Rank.SIX),
                        new DealerCondition(3, 6),
                        Action.DOUBLE
                ),
                new Rule(
                        new SoftCondition(Rank.SEVEN),
                        new DealerCondition(3, 6),
                        Action.DOUBLE
                ),
                // ACE + 2-6 vs anything -> HIT
                new Rule(
                        new CompositeCondition(List.of(
                                new SoftCondition(Rank.TWO),
                                new SoftCondition(Rank.THREE),
                                new SoftCondition(Rank.FOUR),
                                new SoftCondition(Rank.FIVE)
                        )),
                        new DealerCondition(2, 11),
                        Action.HIT
                ),
//===========================HARD HANDS===========================================
                new Rule(
                        new TotalCondition(11),
                        new DealerCondition(2,10),
                        Action.DOUBLE
                ),

                new Rule(
                        new TotalCondition(10),
                        new DealerCondition(2,9),
                        Action.DOUBLE
                ),

                new Rule(
                        new TotalCondition(9),
                        new DealerCondition(3,6),
                        Action.DOUBLE
                ),
//=============================BASIC========================================
                new Rule(
                        new TotalCondition(12,16),
                        new DealerCondition(2,6),
                        Action.STAND
                ),
                new Rule(
                        new TotalCondition(12,16),
                        new DealerCondition(7,11),
                        Action.HIT
                ),
                new Rule(
                        new TotalCondition(17,21),
                        new DealerCondition(2,11),
                        Action.STAND
                )
        ));
    }
}
