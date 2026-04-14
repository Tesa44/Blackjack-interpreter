import blackjack.engine.RoundResult;
import blackjack.query.GroupByClassifier;
import blackjack.sim.FrontendExportData;
import blackjack.sim.StreakStatistics;
import blackjack.sim.StreakStatisticsCalculator;
import blackjack.sim.Statistics;
import blackjack.sim.StatisticsCalculator;
import blackjack.strategy.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatsExportFactory {
    private final StatisticsCalculator statisticsCalculator = new StatisticsCalculator();
    private final StreakStatisticsCalculator streakStatisticsCalculator = new StreakStatisticsCalculator();

    public FrontendExportData.StatsResult create(
            String filterDescription,
            List<String> groupBy,
            List<RoundResult> filteredResults
    ) {
        GroupByClassifier classifier = groupBy.isEmpty() ? null : new GroupByClassifier(groupBy);

        FrontendExportData.SummaryStats summary = null;
        List<FrontendExportData.GroupedStatsEntry> groupedEntries = null;
        FrontendExportData.StreakStats streakStats = null;

        if (classifier == null) {
            summary = toSummaryStats(statisticsCalculator.calculate(filteredResults));
        } else if (classifier.isPlayerStreakGrouping()) {
            streakStats = toStreakStats("Player", streakStatisticsCalculator.calculatePlayerStreaks(filteredResults));
        } else if (classifier.isDealerStreakGrouping()) {
            streakStats = toStreakStats("Dealer", streakStatisticsCalculator.calculateDealerStreaks(filteredResults));
        } else {
            groupedEntries = buildGroupedStats(classifier, filteredResults);
        }

        return new FrontendExportData.StatsResult(
                filterDescription,
                groupBy.isEmpty() ? Collections.emptyList() : new ArrayList<>(groupBy),
                summary,
                groupedEntries,
                streakStats
        );
    }

    private FrontendExportData.SummaryStats toSummaryStats(Statistics statistics) {
        int totalGames = statistics.getTotalGames();
        List<FrontendExportData.ActionStats> actionStats = new ArrayList<>();
        for (Action action : Action.values()) {
            int count = statistics.getActionCounts().getOrDefault(action, 0);
            int wins = statistics.getActionWins().getOrDefault(action, 0);
            double winRate = count == 0 ? 0.0 : (double) wins / count;
            actionStats.add(new FrontendExportData.ActionStats(action.name(), count, wins, winRate));
        }

        return new FrontendExportData.SummaryStats(
                totalGames,
                statistics.getPlayerWins(),
                statistics.getDealerWins(),
                statistics.getDraws(),
                statistics.getPlayerWinRate(),
                statistics.getDealerWinRate(),
                statistics.getDrawRate(),
                rate(statistics.getPlayerBusts(), totalGames),
                rate(statistics.getDealerBusts(), totalGames),
                actionStats
        );
    }

    private List<FrontendExportData.GroupedStatsEntry> buildGroupedStats(
            GroupByClassifier classifier,
            List<RoundResult> filteredResults
    ) {
        Map<GroupByClassifier.GroupKey, List<RoundResult>> groupedResults = new TreeMap<>();
        for (RoundResult result : filteredResults) {
            GroupByClassifier.GroupKey groupKey = classifier.classify(result);
            groupedResults.computeIfAbsent(groupKey, ignored -> new ArrayList<>()).add(result);
        }

        List<FrontendExportData.GroupedStatsEntry> groupedEntries = new ArrayList<>();
        for (Map.Entry<GroupByClassifier.GroupKey, List<RoundResult>> entry : groupedResults.entrySet()) {
            Statistics statistics = statisticsCalculator.calculate(entry.getValue());
            groupedEntries.add(
                    new FrontendExportData.GroupedStatsEntry(
                            entry.getKey().label(),
                            statistics.getTotalGames(),
                            statistics.getPlayerWinRate(),
                            statistics.getDealerWinRate()
                    )
            );
        }
        return groupedEntries;
    }

    private FrontendExportData.StreakStats toStreakStats(String sideLabel, StreakStatistics streakStatistics) {
        List<FrontendExportData.StreakEntry> entries = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : streakStatistics.getStreakCounts().entrySet()) {
            entries.add(
                    new FrontendExportData.StreakEntry(
                            entry.getKey(),
                            entry.getValue(),
                            streakStatistics.getRate(entry.getKey())
                    )
            );
        }
        return new FrontendExportData.StreakStats(sideLabel, streakStatistics.getTotalStreaks(), entries);
    }

    private double rate(int count, int total) {
        if (total == 0) {
            return 0.0;
        }
        return (double) count / total;
    }
}
