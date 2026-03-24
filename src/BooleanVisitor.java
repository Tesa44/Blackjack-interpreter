import blackjack.engine.BlackjackGame;
import blackjack.engine.Deck;
import blackjack.sim.SimulationConfig;
import blackjack.sim.SimulationResult;
import blackjack.sim.SimulationRunner;
import blackjack.strategy.BasicStrategyConfig;
import blackjack.strategy.Strategy;

public class BooleanVisitor extends ExprParserBaseVisitor<String> {

    @Override
    public String visitProgram(ExprParser.ProgramContext ctx) {
        // delegate to the single statement inside the program
        return visit(ctx.stat());
    }

    @Override
    public String visitStat(ExprParser.StatContext ctx) {
        // Current grammar: 'simulate' expr 'rounds' ';'
        // expr is just an INT, representing number of rounds
        int rounds = Integer.parseInt(ctx.expr().getText());

        SimulationConfig config = new SimulationConfig();
        config.setRounds(rounds);

        Deck deck = new Deck();
        BlackjackGame game = new BlackjackGame(deck);
        Strategy strategy = BasicStrategyConfig.create();
        SimulationRunner runner = new SimulationRunner(config, game, strategy);
        SimulationResult result = runner.run();

        return "Simulated " + rounds + " rounds -> "
                + "playerWins=" + result.getPlayerWins() + ", "
                + "dealerWins=" + result.getDealerWins() + ", "
                + "pushes=" + result.getPushes();
    }
}
