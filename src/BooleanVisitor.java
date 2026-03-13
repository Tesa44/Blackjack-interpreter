import engine.BlackjackGame;
import engine.Deck;
import sim.SimulationConfig;
import sim.SimulationResult;
import sim.SimulationRunner;

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
        SimulationRunner runner = new SimulationRunner(config, game);
        SimulationResult result = runner.run();

        return "Simulated " + rounds + " rounds -> "
                + "playerWins=" + result.getPlayerWins() + ", "
                + "dealerWins=" + result.getDealerWins() + ", "
                + "pushes=" + result.getPushes();
    }
}
