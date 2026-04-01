package blackjack.sim;

import blackjack.strategy.Action;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class Statistics {
    private final int totalGames;
    private final int playerWins;
    private final int dealerWins;
    private final int draws;
    private final int playerBusts;
    private final int dealerBusts;
    private final Map<Action, Integer> actionCounts;
    private final Map<Action, Integer> actionWins;

    public Statistics(
            int totalGames,
            int playerWins,
            int dealerWins,
            int draws,
            int playerBusts,
            int dealerBusts,
            Map<Action, Integer> actionCounts,
            Map<Action, Integer> actionWins
    ) {
        this.totalGames = totalGames;
        this.playerWins = playerWins;
        this.dealerWins = dealerWins;
        this.draws = draws;
        this.playerBusts = playerBusts;
        this.dealerBusts = dealerBusts;
        this.actionCounts = Collections.unmodifiableMap(new EnumMap<>(actionCounts));
        this.actionWins = Collections.unmodifiableMap(new EnumMap<>(actionWins));
    }

    public int getTotalGames() {
        return totalGames;
    }

    public int getPlayerWins() {
        return playerWins;
    }

    public int getDealerWins() {
        return dealerWins;
    }

    public int getDraws() {
        return draws;
    }

    public int getPlayerBusts() {
        return playerBusts;
    }

    public int getDealerBusts() {
        return dealerBusts;
    }

    public Map<Action, Integer> getActionCounts() {
        return actionCounts;
    }

    public Map<Action, Integer> getActionWins() {
        return actionWins;
    }

    public double getPlayerWinRate() {
        return rate(playerWins);
    }

    public double getDealerWinRate() {
        return rate(dealerWins);
    }

    public double getDrawRate() {
        return rate(draws);
    }

    private double rate(int count) {
        if (totalGames == 0) {
            return 0.0;
        }
        return (double) count / totalGames;
    }
}
