package blackjack.strategy;

import blackjack.engine.Card;
import blackjack.engine.Hand;
import blackjack.strategy.condition.DealerCondition;
import blackjack.strategy.condition.PlayerCondition;

public class Rule {
    private final PlayerCondition playerCondition;
    private final DealerCondition dealerCondition;
    private final Action action;

    public Rule(PlayerCondition playerCondition, DealerCondition dealerCondition, Action action) {
        this.playerCondition = playerCondition;
        this.dealerCondition = dealerCondition;
        this.action = action;
    }

    public boolean matches(Hand playerHand, Card dealerCard) {
        return playerCondition.matches(playerHand) && dealerCondition.matches(dealerCard);
    }

    public Action getAction() {
        return action;
    }
}
