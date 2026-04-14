package blackjack.sim;

import blackjack.engine.RoundResult;
import blackjack.strategy.Action;

import java.util.ArrayList;
import java.util.List;

public class FrontendExportData {
    private Summary summary;
    private final List<ShowResult> showResults = new ArrayList<>();
    private final List<StatsResult> statsResults = new ArrayList<>();
    private final List<TimelineResult> timelineResults = new ArrayList<>();
    private PlotResult plotResult;

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public List<ShowResult> getShowResults() {
        return showResults;
    }

    public List<StatsResult> getStatsResults() {
        return statsResults;
    }

    public List<TimelineResult> getTimelineResults() {
        return timelineResults;
    }

    public PlotResult getPlotResult() {
        return plotResult;
    }

    public void setPlotResult(PlotResult plotResult) {
        this.plotResult = plotResult;
    }

    public static class Summary {
        private final String simulationMode;
        private final int roundsPlayed;
        private final int playerWins;
        private final int dealerWins;
        private final int pushes;
        private final int initialBalance;
        private final int betPerGame;
        private final int finalBalance;

        public Summary(
                String simulationMode,
                int roundsPlayed,
                int playerWins,
                int dealerWins,
                int pushes,
                int initialBalance,
                int betPerGame,
                int finalBalance
        ) {
            this.simulationMode = simulationMode;
            this.roundsPlayed = roundsPlayed;
            this.playerWins = playerWins;
            this.dealerWins = dealerWins;
            this.pushes = pushes;
            this.initialBalance = initialBalance;
            this.betPerGame = betPerGame;
            this.finalBalance = finalBalance;
        }

        public String getSimulationMode() {
            return simulationMode;
        }

        public int getRoundsPlayed() {
            return roundsPlayed;
        }

        public int getPlayerWins() {
            return playerWins;
        }

        public int getDealerWins() {
            return dealerWins;
        }

        public int getPushes() {
            return pushes;
        }

        public int getInitialBalance() {
            return initialBalance;
        }

        public int getBetPerGame() {
            return betPerGame;
        }

        public int getFinalBalance() {
            return finalBalance;
        }
    }

    public static class RoundView {
        private final int roundNumber;
        private final RoundResult roundResult;

        public RoundView(int roundNumber, RoundResult roundResult) {
            this.roundNumber = roundNumber;
            this.roundResult = roundResult;
        }

        public int getRoundNumber() {
            return roundNumber;
        }

        public RoundResult getRoundResult() {
            return roundResult;
        }
    }

    public static class ShowResult {
        private final String filter;
        private final List<RoundView> rounds;

        public ShowResult(String filter, List<RoundView> rounds) {
            this.filter = filter;
            this.rounds = rounds;
        }

        public String getFilter() {
            return filter;
        }

        public List<RoundView> getRounds() {
            return rounds;
        }
    }

    public static class StatsResult {
        private final String filter;
        private final List<String> groupBy;
        private final SummaryStats summary;
        private final List<GroupedStatsEntry> groupedEntries;
        private final StreakStats streakStats;

        public StatsResult(
                String filter,
                List<String> groupBy,
                SummaryStats summary,
                List<GroupedStatsEntry> groupedEntries,
                StreakStats streakStats
        ) {
            this.filter = filter;
            this.groupBy = groupBy;
            this.summary = summary;
            this.groupedEntries = groupedEntries;
            this.streakStats = streakStats;
        }

        public String getFilter() {
            return filter;
        }

        public List<String> getGroupBy() {
            return groupBy;
        }

        public SummaryStats getSummary() {
            return summary;
        }

        public List<GroupedStatsEntry> getGroupedEntries() {
            return groupedEntries;
        }

        public StreakStats getStreakStats() {
            return streakStats;
        }
    }

    public static class TimelineResult {
        private final String filter;
        private final List<RoundView> rounds;

        public TimelineResult(String filter, List<RoundView> rounds) {
            this.filter = filter;
            this.rounds = rounds;
        }

        public String getFilter() {
            return filter;
        }

        public List<RoundView> getRounds() {
            return rounds;
        }
    }

    public static class PlotResult {
        private final List<Integer> balanceHistory;

        public PlotResult(List<Integer> balanceHistory) {
            this.balanceHistory = balanceHistory;
        }

        public List<Integer> getBalanceHistory() {
            return balanceHistory;
        }
    }

    public static class SummaryStats {
        private final int totalGames;
        private final int playerWins;
        private final int dealerWins;
        private final int draws;
        private final double playerWinRate;
        private final double dealerWinRate;
        private final double drawRate;
        private final double playerBustRate;
        private final double dealerBustRate;
        private final List<ActionStats> actionStats;

        public SummaryStats(
                int totalGames,
                int playerWins,
                int dealerWins,
                int draws,
                double playerWinRate,
                double dealerWinRate,
                double drawRate,
                double playerBustRate,
                double dealerBustRate,
                List<ActionStats> actionStats
        ) {
            this.totalGames = totalGames;
            this.playerWins = playerWins;
            this.dealerWins = dealerWins;
            this.draws = draws;
            this.playerWinRate = playerWinRate;
            this.dealerWinRate = dealerWinRate;
            this.drawRate = drawRate;
            this.playerBustRate = playerBustRate;
            this.dealerBustRate = dealerBustRate;
            this.actionStats = actionStats;
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

        public double getPlayerWinRate() {
            return playerWinRate;
        }

        public double getDealerWinRate() {
            return dealerWinRate;
        }

        public double getDrawRate() {
            return drawRate;
        }

        public double getPlayerBustRate() {
            return playerBustRate;
        }

        public double getDealerBustRate() {
            return dealerBustRate;
        }

        public List<ActionStats> getActionStats() {
            return actionStats;
        }
    }

    public static class ActionStats {
        private final String action;
        private final int count;
        private final int wins;
        private final double winRate;

        public ActionStats(String action, int count, int wins, double winRate) {
            this.action = action;
            this.count = count;
            this.wins = wins;
            this.winRate = winRate;
        }

        public String getAction() {
            return action;
        }

        public int getCount() {
            return count;
        }

        public int getWins() {
            return wins;
        }

        public double getWinRate() {
            return winRate;
        }
    }

    public static class GroupedStatsEntry {
        private final String label;
        private final int games;
        private final double winRate;
        private final double loseRate;

        public GroupedStatsEntry(String label, int games, double winRate, double loseRate) {
            this.label = label;
            this.games = games;
            this.winRate = winRate;
            this.loseRate = loseRate;
        }

        public String getLabel() {
            return label;
        }

        public int getGames() {
            return games;
        }

        public double getWinRate() {
            return winRate;
        }

        public double getLoseRate() {
            return loseRate;
        }
    }

    public static class StreakStats {
        private final String sideLabel;
        private final int totalStreaks;
        private final List<StreakEntry> entries;

        public StreakStats(String sideLabel, int totalStreaks, List<StreakEntry> entries) {
            this.sideLabel = sideLabel;
            this.totalStreaks = totalStreaks;
            this.entries = entries;
        }

        public String getSideLabel() {
            return sideLabel;
        }

        public int getTotalStreaks() {
            return totalStreaks;
        }

        public List<StreakEntry> getEntries() {
            return entries;
        }
    }

    public static class StreakEntry {
        private final int length;
        private final int count;
        private final double percentage;

        public StreakEntry(int length, int count, double percentage) {
            this.length = length;
            this.count = count;
            this.percentage = percentage;
        }

        public int getLength() {
            return length;
        }

        public int getCount() {
            return count;
        }

        public double getPercentage() {
            return percentage;
        }
    }
}
