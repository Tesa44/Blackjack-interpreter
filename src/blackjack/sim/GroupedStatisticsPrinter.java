package blackjack.sim;

import blackjack.query.GroupByClassifier;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Map;

public class GroupedStatisticsPrinter {
    private final PrintStream out;

    public GroupedStatisticsPrinter() {
        this(System.out);
    }

    public GroupedStatisticsPrinter(PrintStream out) {
        this.out = out;
    }

    public void print(Map<GroupByClassifier.GroupKey, Statistics> groupedStatistics) {
        if (groupedStatistics.isEmpty()) {
            out.println("No games matched the stats query.");
            return;
        }

        for (Map.Entry<GroupByClassifier.GroupKey, Statistics> entry : groupedStatistics.entrySet()) {
            Statistics stats = entry.getValue();
            out.println(
                    entry.getKey().label()
                            + " -> Games: " + stats.getTotalGames()
                            + " | Win: " + formatPercentage(stats.getPlayerWinRate())
                            + " | Lose: " + formatPercentage(stats.getDealerWinRate())
            );
        }
    }

    private String formatPercentage(double value) {
        return String.format(Locale.US, "%.1f%%", value * 100);
    }
}
