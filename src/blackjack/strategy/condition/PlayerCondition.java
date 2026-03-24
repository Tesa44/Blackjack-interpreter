package blackjack.strategy.condition;

import blackjack.engine.Hand;

public interface PlayerCondition {
    boolean matches(Hand hand);
}
