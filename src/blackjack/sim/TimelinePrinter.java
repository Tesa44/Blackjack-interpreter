package blackjack.sim;

import blackjack.engine.Result;
import blackjack.engine.RoundResult;

import java.io.PrintStream;
import java.util.List;

public class TimelinePrinter {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private final PrintStream out;

    public TimelinePrinter() {
        this(System.out);
    }

    public TimelinePrinter(PrintStream out) {
        this.out = out;
    }

    public void print(List<RoundResult> results) {
        if (results.isEmpty()) {
            out.println("No games matched the timeline query.");
            return;
        }

        StringBuilder timeline = new StringBuilder();
        for (RoundResult result : results) {
            timeline.append(formatResult(result.getResult()));
        }

        out.println("Timeline:");
        out.println(timeline);
        out.println("Legend: " + ANSI_GREEN + "w" + ANSI_RESET + "=win, "
                + ANSI_RED + "l" + ANSI_RESET + "=lose, "
                + ANSI_YELLOW + "d" + ANSI_RESET + "=draw");
    }

    private String formatResult(Result result) {
        return switch (result) {
            case PLAYER_WIN -> ANSI_GREEN + "w" + ANSI_RESET;
            case DEALER_WIN -> ANSI_RED + "l" + ANSI_RESET;
            case PUSH -> ANSI_YELLOW + "d" + ANSI_RESET;
        };
    }
}
