package blackjack.strategy;

import blackjack.engine.Card;
import blackjack.engine.Hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Strategy {
    private final List<Rule> rules;

    public Strategy(List<Rule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    public List<Rule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public Action decide(Hand playerHand, Card dealerCard) {
        for (Rule rule : rules) {
            if (rule.matches(playerHand, dealerCard)) {
                return rule.getAction();
            }
        }
        return Action.HIT; // Was HIT before
    }

    @Override
    public String toString() {

        return super.toString();
    }
}
