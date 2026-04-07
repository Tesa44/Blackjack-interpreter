package blackjack.sim;

import blackjack.engine.RoundResult;

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
        private final String text;

        public StatsResult(String filter, List<String> groupBy, String text) {
            this.filter = filter;
            this.groupBy = groupBy;
            this.text = text;
        }

        public String getFilter() {
            return filter;
        }

        public List<String> getGroupBy() {
            return groupBy;
        }

        public String getText() {
            return text;
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
}
