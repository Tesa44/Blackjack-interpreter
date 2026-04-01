package blackjack.sim;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Map;

public class StreakStatisticsPrinter {
    private final PrintStream out;

    public StreakStatisticsPrinter() {
        this(System.out);
    }

    public StreakStatisticsPrinter(PrintStream out) {
        this.out = out;
    }

    public void print(String sideLabel, StreakStatistics statistics) {
        if (statistics.getStreakCounts().isEmpty()) {
            out.println("No " + sideLabel.toLowerCase(Locale.US) + " streaks found.");
            return;
        }

        out.println(sideLabel + " streaks:");
        for (Map.Entry<Integer, Integer> entry : statistics.getStreakCounts().entrySet()) {
            int streakLength = entry.getKey();
            int count = entry.getValue();
            out.println(
                    "Streak " + streakLength
                            + ": " + count
                            + " times (" + formatPercentage(statistics.getRate(streakLength)) + ")"
            );
        }
    }

    private String formatPercentage(double value) {
        return String.format(Locale.US, "%.1f%%", value * 100);
    }
}
