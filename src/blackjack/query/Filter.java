package blackjack.query;

import blackjack.engine.RoundResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Filter {
    private final Predicate<RoundResult> condition;

    public Filter(Predicate<RoundResult> condition) {
        this.condition = condition;
    }

    public List<RoundResult> apply(List<RoundResult> allResults) {
        List<RoundResult> filteredResults = new ArrayList<>();
        for (RoundResult result : allResults) {
            if (condition.test(result)) {
                filteredResults.add(result);
            }
        }
        return filteredResults;
    }
}
