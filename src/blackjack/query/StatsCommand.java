package blackjack.query;

import blackjack.engine.RoundResult;
import blackjack.sim.GroupedStatisticsPrinter;
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

    public StatsCommand(
            Filter filter,
            StatisticsCalculator statisticsCalculator,
            StatisticsPrinter statisticsPrinter,
            GroupedStatisticsPrinter groupedStatisticsPrinter
    ) {
        this.filter = filter;
        this.statisticsCalculator = statisticsCalculator;
        this.statisticsPrinter = statisticsPrinter;
        this.groupedStatisticsPrinter = groupedStatisticsPrinter;
    }

    public void execute(List<RoundResult> allResults) {
        List<RoundResult> filtered = filter.apply(allResults);
        Statistics stats = statisticsCalculator.calculate(filtered);
        statisticsPrinter.print(stats);
    }

    public void execute(List<RoundResult> allResults, GroupByClassifier classifier) {
        List<RoundResult> filtered = filter.apply(allResults);
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
