package blackjack.query;

import blackjack.engine.RoundResult;
import blackjack.sim.Statistics;
import blackjack.sim.StatisticsCalculator;
import blackjack.sim.StatisticsPrinter;

import java.util.List;

public class StatsCommand {
    private final Filter filter;
    private final StatisticsCalculator statisticsCalculator;
    private final StatisticsPrinter statisticsPrinter;

    public StatsCommand(Filter filter, StatisticsCalculator statisticsCalculator, StatisticsPrinter statisticsPrinter) {
        this.filter = filter;
        this.statisticsCalculator = statisticsCalculator;
        this.statisticsPrinter = statisticsPrinter;
    }

    public void execute(List<RoundResult> allResults) {
        List<RoundResult> filtered = filter.apply(allResults);
        Statistics stats = statisticsCalculator.calculate(filtered);
        statisticsPrinter.print(stats);
    }
}
