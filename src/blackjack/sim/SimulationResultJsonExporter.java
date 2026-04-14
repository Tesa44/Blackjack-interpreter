package blackjack.sim;

import blackjack.engine.Card;
import blackjack.engine.Hand;
import blackjack.engine.RoundResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class SimulationResultJsonExporter {
    private static final Path OUTPUT_PATH = Path.of("out", "frontend", "latest-simulation.json");

    public String export(FrontendExportData exportData) {
        try {
            Files.createDirectories(OUTPUT_PATH.getParent());
            Files.writeString(OUTPUT_PATH, toJson(exportData), StandardCharsets.UTF_8);
            return OUTPUT_PATH.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not save simulation JSON export.", e);
        }
    }

    private String toJson(FrontendExportData exportData) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        appendSummary(json, exportData.getSummary());

        if (!exportData.getShowResults().isEmpty()) {
            json.append(",\n");
            appendShowResults(json, exportData.getShowResults());
        }

        if (!exportData.getStatsResults().isEmpty()) {
            json.append(",\n");
            appendStatsResults(json, exportData.getStatsResults());
        }

        if (!exportData.getTimelineResults().isEmpty()) {
            json.append(",\n");
            appendTimelineResults(json, exportData.getTimelineResults());
        }

        if (exportData.getPlotResult() != null) {
            json.append(",\n");
            appendPlotResult(json, exportData.getPlotResult());
        }

        json.append("\n}");
        return json.toString();
    }

    private void appendSummary(StringBuilder json, FrontendExportData.Summary summary) {
        json.append("  \"summary\": {\n");
        appendJsonField(json, 4, "simulationMode", summary.getSimulationMode(), true);
        appendJsonField(json, 4, "roundsPlayed", summary.getRoundsPlayed(), true);
        appendJsonField(json, 4, "playerWins", summary.getPlayerWins(), true);
        appendJsonField(json, 4, "dealerWins", summary.getDealerWins(), true);
        appendJsonField(json, 4, "pushes", summary.getPushes(), true);
        appendJsonField(json, 4, "initialBalance", summary.getInitialBalance(), true);
        appendJsonField(json, 4, "betPerGame", summary.getBetPerGame(), true);
        appendJsonField(json, 4, "finalBalance", summary.getFinalBalance(), false);
        json.append("  }");
    }

    private void appendShowResults(StringBuilder json, List<FrontendExportData.ShowResult> showResults) {
        json.append("  \"showResults\": [");
        if (!showResults.isEmpty()) {
            json.append("\n");
        }

        for (int i = 0; i < showResults.size(); i++) {
            FrontendExportData.ShowResult showResult = showResults.get(i);
            json.append("    {\n");
            appendJsonField(json, 6, "filter", showResult.getFilter(), true);
            json.append("      \"rounds\": ");
            appendRoundViews(json, showResult.getRounds());
            json.append("\n");
            json.append("    }");
            if (i < showResults.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]");
    }

    private void appendStatsResults(StringBuilder json, List<FrontendExportData.StatsResult> statsResults) {
        json.append("  \"statsResults\": [");
        if (!statsResults.isEmpty()) {
            json.append("\n");
        }

        for (int i = 0; i < statsResults.size(); i++) {
            FrontendExportData.StatsResult statsResult = statsResults.get(i);
            json.append("    {\n");
            appendJsonField(json, 6, "filter", statsResult.getFilter(), true);
            json.append("      \"groupBy\": ");
            appendStringArray(json, statsResult.getGroupBy());
            json.append(",\n");
            json.append("      \"summary\": ");
            appendSummaryStats(json, statsResult.getSummary());
            json.append(",\n");
            json.append("      \"groupedEntries\": ");
            appendGroupedStatsEntries(json, statsResult.getGroupedEntries());
            json.append(",\n");
            json.append("      \"streakStats\": ");
            appendStreakStats(json, statsResult.getStreakStats());
            json.append("\n");
            json.append("    }");
            if (i < statsResults.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]");
    }

    private void appendTimelineResults(StringBuilder json, List<FrontendExportData.TimelineResult> timelineResults) {
        json.append("  \"timelineResults\": [");
        if (!timelineResults.isEmpty()) {
            json.append("\n");
        }

        for (int i = 0; i < timelineResults.size(); i++) {
            FrontendExportData.TimelineResult timelineResult = timelineResults.get(i);
            json.append("    {\n");
            appendJsonField(json, 6, "filter", timelineResult.getFilter(), true);
            json.append("      \"rounds\": ");
            appendTimelineRounds(json, timelineResult.getRounds());
            json.append("\n");
            json.append("    }");
            if (i < timelineResults.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("  ]");
    }

    private void appendPlotResult(StringBuilder json, FrontendExportData.PlotResult plotResult) {
        json.append("  \"plot\": {\n");
        json.append("    \"balanceHistory\": ");
        appendIntegerArray(json, plotResult.getBalanceHistory());
        json.append("\n");
        json.append("  }");
    }

    private void appendRoundViews(StringBuilder json, List<FrontendExportData.RoundView> roundViews) {
        json.append("[");
        if (!roundViews.isEmpty()) {
            json.append("\n");
        }

        for (int i = 0; i < roundViews.size(); i++) {
            FrontendExportData.RoundView roundView = roundViews.get(i);
            appendRoundDetail(json, roundView);
            if (i < roundViews.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("      ]");
    }

    private void appendTimelineRounds(StringBuilder json, List<FrontendExportData.RoundView> roundViews) {
        json.append("[");
        if (!roundViews.isEmpty()) {
            json.append("\n");
        }

        for (int i = 0; i < roundViews.size(); i++) {
            FrontendExportData.RoundView roundView = roundViews.get(i);
            RoundResult roundResult = roundView.getRoundResult();
            json.append("        {\n");
            appendJsonField(json, 10, "roundNumber", roundView.getRoundNumber(), true);
            appendJsonField(json, 10, "result", roundResult.getResult().name(), true);
            appendJsonField(json, 10, "action", roundResult.getAction().name(), true);
            appendJsonField(json, 10, "netBetUnits", roundResult.getNetBetUnits(), false);
            json.append("        }");
            if (i < roundViews.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("      ]");
    }

    private void appendSummaryStats(StringBuilder json, FrontendExportData.SummaryStats summaryStats) {
        if (summaryStats == null) {
            json.append("null");
            return;
        }

        json.append("{\n");
        appendJsonField(json, 8, "totalGames", summaryStats.getTotalGames(), true);
        appendJsonField(json, 8, "playerWins", summaryStats.getPlayerWins(), true);
        appendJsonField(json, 8, "dealerWins", summaryStats.getDealerWins(), true);
        appendJsonField(json, 8, "draws", summaryStats.getDraws(), true);
        appendJsonField(json, 8, "playerWinRate", summaryStats.getPlayerWinRate(), true);
        appendJsonField(json, 8, "dealerWinRate", summaryStats.getDealerWinRate(), true);
        appendJsonField(json, 8, "drawRate", summaryStats.getDrawRate(), true);
        appendJsonField(json, 8, "playerBustRate", summaryStats.getPlayerBustRate(), true);
        appendJsonField(json, 8, "dealerBustRate", summaryStats.getDealerBustRate(), true);
        json.append("        \"actionStats\": ");
        appendActionStats(json, summaryStats.getActionStats());
        json.append("\n");
        json.append("      }");
    }

    private void appendActionStats(StringBuilder json, List<FrontendExportData.ActionStats> actionStats) {
        json.append("[");
        if (!actionStats.isEmpty()) {
            json.append("\n");
        }

        for (int i = 0; i < actionStats.size(); i++) {
            FrontendExportData.ActionStats stats = actionStats.get(i);
            json.append("          {\n");
            appendJsonField(json, 12, "action", stats.getAction(), true);
            appendJsonField(json, 12, "count", stats.getCount(), true);
            appendJsonField(json, 12, "wins", stats.getWins(), true);
            appendJsonField(json, 12, "winRate", stats.getWinRate(), false);
            json.append("          }");
            if (i < actionStats.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("        ]");
    }

    private void appendGroupedStatsEntries(StringBuilder json, List<FrontendExportData.GroupedStatsEntry> groupedEntries) {
        if (groupedEntries == null) {
            json.append("null");
            return;
        }

        json.append("[");
        if (!groupedEntries.isEmpty()) {
            json.append("\n");
        }

        for (int i = 0; i < groupedEntries.size(); i++) {
            FrontendExportData.GroupedStatsEntry entry = groupedEntries.get(i);
            json.append("        {\n");
            appendJsonField(json, 10, "label", entry.getLabel(), true);
            appendJsonField(json, 10, "games", entry.getGames(), true);
            appendJsonField(json, 10, "winRate", entry.getWinRate(), true);
            appendJsonField(json, 10, "loseRate", entry.getLoseRate(), false);
            json.append("        }");
            if (i < groupedEntries.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("      ]");
    }

    private void appendStreakStats(StringBuilder json, FrontendExportData.StreakStats streakStats) {
        if (streakStats == null) {
            json.append("null");
            return;
        }

        json.append("{\n");
        appendJsonField(json, 8, "sideLabel", streakStats.getSideLabel(), true);
        appendJsonField(json, 8, "totalStreaks", streakStats.getTotalStreaks(), true);
        json.append("        \"entries\": ");
        appendStreakEntries(json, streakStats.getEntries());
        json.append("\n");
        json.append("      }");
    }

    private void appendStreakEntries(StringBuilder json, List<FrontendExportData.StreakEntry> entries) {
        json.append("[");
        if (!entries.isEmpty()) {
            json.append("\n");
        }

        for (int i = 0; i < entries.size(); i++) {
            FrontendExportData.StreakEntry entry = entries.get(i);
            json.append("          {\n");
            appendJsonField(json, 12, "length", entry.getLength(), true);
            appendJsonField(json, 12, "count", entry.getCount(), true);
            appendJsonField(json, 12, "percentage", entry.getPercentage(), false);
            json.append("          }");
            if (i < entries.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }

        json.append("        ]");
    }

    private void appendRoundDetail(StringBuilder json, FrontendExportData.RoundView roundView) {
        RoundResult roundResult = roundView.getRoundResult();
        json.append("        {\n");
        appendJsonField(json, 10, "roundNumber", roundView.getRoundNumber(), true);
        appendJsonField(json, 10, "result", roundResult.getResult().name(), true);
        appendJsonField(json, 10, "action", roundResult.getAction().name(), true);
        appendJsonField(json, 10, "dealerTotal", roundResult.getDealerValue(), true);
        appendJsonField(json, 10, "dealerUpcard", roundResult.getDealerUpcardValue(), true);
        appendJsonField(json, 10, "playerInitialTotal", roundResult.getPlayerInitialTotal(), true);
        appendJsonField(json, 10, "netBetUnits", roundResult.getNetBetUnits(), true);

        json.append("          \"dealerCards\": ");
        appendCards(json, roundResult.dealerHand);
        json.append(",\n");

        json.append("          \"playerHands\": ");
        appendPlayerHands(json, roundResult);
        json.append("\n");
        json.append("        }");
    }

    private void appendPlayerHands(StringBuilder json, RoundResult roundResult) {
        json.append("[");
        if (!roundResult.getPlayerHandsWithBestValues().isEmpty()) {
            json.append("\n");
        }

        int handIndex = 0;
        for (Map.Entry<Hand, Integer> entry : roundResult.getPlayerHandsWithBestValues().entrySet()) {
            Hand hand = entry.getKey();
            List<Integer> betMultipliers = roundResult.getPlayerHandBetMultipliers();

            json.append("            {\n");
            appendJsonField(json, 14, "bestValue", entry.getValue(), true);
            appendJsonField(json, 14, "betMultiplier", betMultipliers.get(handIndex), true);
            appendJsonField(json, 14, "isPair", hand.isPair(), true);
            appendJsonField(json, 14, "isSoft", hand.isSoft(), true);
            json.append("              \"cards\": ");
            appendCards(json, hand);
            json.append("\n");
            json.append("            }");
            if (handIndex < roundResult.getPlayerHandsWithBestValues().size() - 1) {
                json.append(",");
            }
            json.append("\n");
            handIndex++;
        }

        json.append("          ]");
    }

    private void appendCards(StringBuilder json, Hand hand) {
        json.append("[");
        List<Card> cards = hand.getCards();
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (i > 0) {
                json.append(", ");
            }
            json.append("{")
                    .append("\"rank\": \"").append(escape(card.getRank().name())).append("\", ")
                    .append("\"suit\": \"").append(escape(card.getSuit().name())).append("\", ")
                    .append("\"value\": ").append(card.getValue())
                    .append("}");
        }
        json.append("]");
    }

    private void appendStringArray(StringBuilder json, List<String> values) {
        json.append("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                json.append(", ");
            }
            json.append("\"").append(escape(values.get(i))).append("\"");
        }
        json.append("]");
    }

    private void appendIntegerArray(StringBuilder json, List<Integer> values) {
        json.append("[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                json.append(", ");
            }
            json.append(values.get(i));
        }
        json.append("]");
    }

    private void appendJsonField(StringBuilder json, int indent, String name, String value, boolean withComma) {
        indent(json, indent);
        json.append("\"").append(name).append("\": \"").append(escape(value)).append("\"");
        if (withComma) {
            json.append(",");
        }
        json.append("\n");
    }

    private void appendJsonField(StringBuilder json, int indent, String name, int value, boolean withComma) {
        indent(json, indent);
        json.append("\"").append(name).append("\": ").append(value);
        if (withComma) {
            json.append(",");
        }
        json.append("\n");
    }

    private void appendJsonField(StringBuilder json, int indent, String name, double value, boolean withComma) {
        indent(json, indent);
        json.append("\"").append(name).append("\": ").append(Double.toString(value));
        if (withComma) {
            json.append(",");
        }
        json.append("\n");
    }

    private void appendJsonField(StringBuilder json, int indent, String name, boolean value, boolean withComma) {
        indent(json, indent);
        json.append("\"").append(name).append("\": ").append(value);
        if (withComma) {
            json.append(",");
        }
        json.append("\n");
    }

    private void indent(StringBuilder json, int indent) {
        json.append(" ".repeat(Math.max(0, indent)));
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
