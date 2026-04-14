import blackjack.engine.BlackjackGame;
import blackjack.engine.Deck;
import blackjack.sim.FrontendExportData;
import blackjack.sim.SimulationConfig;
import blackjack.sim.SimulationResult;
import blackjack.sim.SimulationRunner;
import blackjack.strategy.Strategy;

import java.util.ArrayList;

public class SimulationFacade {
    public SimulationResult simulateRounds(int rounds, int initialBalance, int betPerGame, Strategy strategy) {
        SimulationConfig config = new SimulationConfig();
        config.setRounds(rounds);
        config.setInitialBalance(initialBalance);
        config.setBetPerGame(betPerGame);
        return run(config, strategy);
    }

    public SimulationResult simulateUntilTarget(int targetBalance, int initialBalance, int betPerGame, Strategy strategy) {
        SimulationConfig config = new SimulationConfig();
        config.setInitialBalance(initialBalance);
        config.setBetPerGame(betPerGame);
        config.setTargetBalance(targetBalance);
        return run(config, strategy);
    }

    public SimulationResult simulateUntilBroke(int initialBalance, int betPerGame, Strategy strategy) {
        SimulationConfig config = new SimulationConfig();
        config.setInitialBalance(initialBalance);
        config.setBetPerGame(betPerGame);
        config.setRunUntilBroke(true);
        return run(config, strategy);
    }

    public FrontendExportData createExportData(String simulationMode, SimulationResult simulationResult) {
        FrontendExportData exportData = new FrontendExportData();
        exportData.setSummary(
                new FrontendExportData.Summary(
                        simulationMode,
                        simulationResult.getRoundResults().size(),
                        simulationResult.getPlayerWins(),
                        simulationResult.getDealerWins(),
                        simulationResult.getPushes(),
                        simulationResult.getInitialBalance(),
                        simulationResult.getBetPerGame(),
                        simulationResult.getFinalBalance()
                )
        );
        return exportData;
    }

    public FrontendExportData.PlotResult createPlotResult(SimulationResult simulationResult) {
        return new FrontendExportData.PlotResult(new ArrayList<>(simulationResult.getBalanceHistory()));
    }

    private SimulationResult run(SimulationConfig config, Strategy strategy) {
        Deck deck = new Deck();
        BlackjackGame game = new BlackjackGame(deck);
        SimulationRunner runner = new SimulationRunner(config, game, strategy);
        return runner.run();
    }
}
