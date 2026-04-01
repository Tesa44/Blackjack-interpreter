package blackjack.sim;

import blackjack.strategy.Action;

import java.io.PrintStream;
import java.util.Locale;

public class StatisticsPrinter {
    private final PrintStream out;

    public StatisticsPrinter() {
        this(System.out);
    }

    public StatisticsPrinter(PrintStream out) {
        this.out = out;
    }

    public void print(Statistics stats) {
        int totalGames = stats.getTotalGames();

        out.println("Total games: " + totalGames);
        out.println("Player wins: " + stats.getPlayerWins() + " (" + formatPercentage(stats.getPlayerWinRate()) + ")");
        out.println("Dealer wins: " + stats.getDealerWins() + " (" + formatPercentage(stats.getDealerWinRate()) + ")");
        out.println("Draws: " + stats.getDraws() + " (" + formatPercentage(stats.getDrawRate()) + ")");
        out.println();
        out.println("Player bust rate: " + formatPercentage(rate(stats.getPlayerBusts(), totalGames)));
        out.println("Dealer bust rate: " + formatPercentage(rate(stats.getDealerBusts(), totalGames)));
        out.println();
        out.println("Action stats:");

        for (Action action : Action.values()) {
            int actionCount = stats.getActionCounts().getOrDefault(action, 0);
            int actionWins = stats.getActionWins().getOrDefault(action, 0);
            double actionWinRate = rate(actionWins, actionCount);

            out.println(
                    action + " -> " + actionCount
                            + " (wins: " + actionWins
                            + ", " + formatPercentage(actionWinRate) + ")"
            );
        }
    }

    private double rate(int value, int total) {
        if (total == 0) {
            return 0.0;
        }
        return (double) value / total;
    }

    private String formatPercentage(double value) {
        return String.format(Locale.US, "%.1f%%", value * 100);
    }
}
