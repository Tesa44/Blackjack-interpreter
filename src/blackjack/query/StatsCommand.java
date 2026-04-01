package blackjack.query;

import blackjack.engine.RoundResult;
import blackjack.sim.GroupedStatisticsPrinter;
import blackjack.sim.StreakStatistics;
import blackjack.sim.StreakStatisticsCalculator;
import blackjack.sim.StreakStatisticsPrinter;
import blackjack.sim.Statistics;
import blackjack.sim.StatisticsCalculator;
import blackjack.sim.StatisticsPrinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatsCommand {
    private final Filter filter;
    private final StatisticsCalculator statisticsCalculator;
    private final StatisticsPrinter statisticsPrinter;
    private final GroupedStatisticsPrinter groupedStatisticsPrinter;
    private final StreakStatisticsCalculator streakStatisticsCalculator;
    private final StreakStatisticsPrinter streakStatisticsPrinter;

    public StatsCommand(
            Filter filter,
            StatisticsCalculator statisticsCalculator,
            StatisticsPrinter statisticsPrinter,
            GroupedStatisticsPrinter groupedStatisticsPrinter,
            StreakStatisticsCalculator streakStatisticsCalculator,
            StreakStatisticsPrinter streakStatisticsPrinter
    ) {
        this.filter = filter;
        this.statisticsCalculator = statisticsCalculator;
        this.statisticsPrinter = statisticsPrinter;
        this.groupedStatisticsPrinter = groupedStatisticsPrinter;
        this.streakStatisticsCalculator = streakStatisticsCalculator;
        this.streakStatisticsPrinter = streakStatisticsPrinter;
    }

    public void execute(List<RoundResult> allResults) {
        List<RoundResult> filtered = filter.apply(allResults);
        Statistics stats = statisticsCalculator.calculate(filtered);
        statisticsPrinter.print(stats);
    }

    public void execute(List<RoundResult> allResults, GroupByClassifier classifier) {
        List<RoundResult> filtered = filter.apply(allResults);
        if (classifier.isPlayerStreakGrouping()) {
            StreakStatistics streakStatistics = streakStatisticsCalculator.calculatePlayerStreaks(filtered);
            streakStatisticsPrinter.print("Player", streakStatistics);
            return;
        }
        if (classifier.isDealerStreakGrouping()) {
            StreakStatistics streakStatistics = streakStatisticsCalculator.calculateDealerStreaks(filtered);
            streakStatisticsPrinter.print("Dealer", streakStatistics);
            return;
        }

        Map<GroupByClassifier.GroupKey, List<RoundResult>> groupedResults = new TreeMap<>();

        for (RoundResult result : filtered) {
            GroupByClassifier.GroupKey groupKey = classifier.classify(result);
            groupedResults.computeIfAbsent(groupKey, ignored -> new ArrayList<>()).add(result);
        }

        Map<GroupByClassifier.GroupKey, Statistics> groupedStatistics = new TreeMap<>();
        for (Map.Entry<GroupByClassifier.GroupKey, List<RoundResult>> entry : groupedResults.entrySet()) {
            groupedStatistics.put(entry.getKey(), statisticsCalculator.calculate(entry.getValue()));
        }

        groupedStatisticsPrinter.print(groupedStatistics);
    }
}
