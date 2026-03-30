import blackjack.engine.BlackjackGame;
import blackjack.engine.Deck;
import blackjack.engine.RoundResult;
import blackjack.sim.SimulationConfig;
import blackjack.sim.SimulationResult;
import blackjack.sim.SimulationRunner;
import blackjack.strategy.BasicStrategyConfig;
import blackjack.strategy.Strategy;

import java.util.List;

public class BooleanVisitor extends ExprParserBaseVisitor<String> {
    private SimulationResult lastSimulationResult;

    @Override
    public String visitProgram(ExprParser.ProgramContext ctx) {
        StringBuilder output = new StringBuilder();
        List<ExprParser.StatContext> statements = ctx.stat();
        for (int i = 0; i < statements.size(); i++) {
            if (i > 0) {
                output.append(System.lineSeparator());
            }
            output.append(visit(statements.get(i)));
        }
        return output.toString();
    }

    @Override
    public String visitStat(ExprParser.StatContext ctx) {
        if (ctx.SIMULATE() != null) {
            int rounds = Integer.parseInt(ctx.expr().INT().getText());

            SimulationConfig config = new SimulationConfig();
            config.setRounds(rounds);

            Deck deck = new Deck();
            BlackjackGame game = new BlackjackGame(deck);
            Strategy strategy = BasicStrategyConfig.create();
            SimulationRunner runner = new SimulationRunner(config, game, strategy);
            lastSimulationResult = runner.run();

            return "Simulated " + rounds + " rounds -> "
                    + "playerWins=" + lastSimulationResult.getPlayerWins() + ", "
                    + "dealerWins=" + lastSimulationResult.getDealerWins() + ", "
                    + "pushes=" + lastSimulationResult.getPushes();
        }

        if (lastSimulationResult == null) {
            return "No simulation results available. Run 'simulate ... rounds;' first.";
        }

        return filterRounds(ctx.expr());
    }

    private String filterRounds(ExprParser.ExprContext exprContext) {
        int targetTotal = Integer.parseInt(exprContext.INT().getText());
        String propertyName = exprContext.property().getText();

        StringBuilder output = new StringBuilder();
        int matches = 0;

        List<RoundResult> roundResults = lastSimulationResult.getRoundResults();
        for (int i = 0; i < roundResults.size(); i++) {
            RoundResult roundResult = roundResults.get(i);
            if (!matches(roundResult, propertyName, targetTotal)) {
                continue;
            }

            if (matches == 0) {
                output.append("Filtered results for ")
                        .append(propertyName)
                        .append(" = ")
                        .append(targetTotal)
                        .append(":")
                        .append(System.lineSeparator());
            } else {
                output.append(System.lineSeparator());
            }

            output.append("Game #").append(i + 1).append(System.lineSeparator());
            output.append(roundResult.describeRoundSummary());
            matches++;
        }

        if (matches == 0) {
            return "No games matched " + propertyName + " = " + targetTotal + ".";
        }

        output.append(System.lineSeparator())
                .append("Matched games: ")
                .append(matches);
        return output.toString();
    }

    private boolean matches(RoundResult roundResult, String propertyName, int targetTotal) {
        return switch (propertyName) {
            case "player.total" -> roundResult.hasPlayerTotal(targetTotal);
            case "dealer.total" -> roundResult.getDealerValue() == targetTotal;
            default -> false;
        };
    }
}
