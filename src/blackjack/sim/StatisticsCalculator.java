package blackjack.sim;

import blackjack.engine.Result;
import blackjack.engine.RoundResult;
import blackjack.strategy.Action;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class StatisticsCalculator {

    public Statistics calculate(List<RoundResult> results) {
        int playerWins = 0;
        int dealerWins = 0;
        int draws = 0;
        int playerBusts = 0;
        int dealerBusts = 0;
        Map<Action, Integer> actionCounts = initializeActionMap();
        Map<Action, Integer> actionWins = initializeActionMap();

        for (RoundResult result : results) {
            Result roundOutcome = result.getResult();
            if (roundOutcome == Result.PLAYER_WIN) {
                playerWins++;
            } else if (roundOutcome == Result.DEALER_WIN) {
                dealerWins++;
            } else {
                draws++;
            }

            if (isPlayerBust(result)) {
                playerBusts++;
            }
            if (result.getDealerValue() > 21) {
                dealerBusts++;
            }

            Action action = result.getAction();
            if (action != null) {
                actionCounts.merge(action, 1, Integer::sum);
                if (roundOutcome == Result.PLAYER_WIN) {
                    actionWins.merge(action, 1, Integer::sum);
                }
            }
        }

        return new Statistics(
                results.size(),
                playerWins,
                dealerWins,
                draws,
                playerBusts,
                dealerBusts,
                actionCounts,
                actionWins
        );
    }

    private boolean isPlayerBust(RoundResult result) {
        return result.getPlayerHandsWithBestValues().values().stream()
                .anyMatch(playerValue -> playerValue > 21);
    }

    private Map<Action, Integer> initializeActionMap() {
        Map<Action, Integer> actionMap = new EnumMap<>(Action.class);
        for (Action action : Action.values()) {
            actionMap.put(action, 0);
        }
        return actionMap;
    }
}
