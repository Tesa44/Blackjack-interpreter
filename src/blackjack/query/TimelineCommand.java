package blackjack.query;

import blackjack.engine.RoundResult;
import blackjack.sim.TimelinePrinter;

import java.util.List;

public class TimelineCommand {
    private final Filter filter;
    private final TimelinePrinter timelinePrinter;

    public TimelineCommand(Filter filter, TimelinePrinter timelinePrinter) {
        this.filter = filter;
        this.timelinePrinter = timelinePrinter;
    }

    public void execute(List<RoundResult> allResults) {
        List<RoundResult> filtered = filter.apply(allResults);
        timelinePrinter.print(filtered);
    }
}
