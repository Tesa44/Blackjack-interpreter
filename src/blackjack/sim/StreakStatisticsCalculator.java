package blackjack.sim;

import blackjack.engine.Result;
import blackjack.engine.RoundResult;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StreakStatisticsCalculator {

    public StreakStatistics calculatePlayerStreaks(List<RoundResult> results) {
        return calculate(results, Result.PLAYER_WIN);
    }

    public StreakStatistics calculateDealerStreaks(List<RoundResult> results) {
        return calculate(results, Result.DEALER_WIN);
    }

    private StreakStatistics calculate(List<RoundResult> results, Result targetResult) {
        Map<Integer, Integer> streakCounts = new TreeMap<>();
        int currentStreak = 0;
        int totalStreaks = 0;

        for (RoundResult result : results) {
            if (result.getResult() == targetResult) {
                currentStreak++;
                continue;
            }

            if (currentStreak > 0) {
                streakCounts.merge(currentStreak, 1, Integer::sum);
                totalStreaks++;
                currentStreak = 0;
            }
        }

        if (currentStreak > 0) {
            streakCounts.merge(currentStreak, 1, Integer::sum);
            totalStreaks++;
        }

        return new StreakStatistics(totalStreaks, streakCounts);
    }
}
