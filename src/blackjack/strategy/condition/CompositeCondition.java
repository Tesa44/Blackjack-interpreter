package blackjack.strategy.condition;

import blackjack.engine.Hand;

import java.util.ArrayList;
import java.util.List;

//TODO: Adapt this class for DealerCondition

public class CompositeCondition implements PlayerCondition {
    private final List<PlayerCondition> conditions;

    public CompositeCondition(List<PlayerCondition> conditions) {
        this.conditions = new ArrayList<>(conditions);
    }

    @Override
    public boolean matches(Hand hand) {
        for (PlayerCondition condition : conditions) {
            if (condition.matches(hand)) {
                return true;
            }
        }
        return false;
    }
}
