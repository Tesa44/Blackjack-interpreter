package blackjack.sim;

import blackjack.engine.BlackjackGame;
import blackjack.engine.Result;
import blackjack.strategy.Strategy;

public class SimulationRunner {
    private final SimulationConfig config;
    private final BlackjackGame game;
    private final Strategy strategy;

    public SimulationRunner(SimulationConfig config, BlackjackGame game, Strategy strategy) {
        this.config = config;
        this.game = game;
        this.strategy = strategy;
    }

    public SimulationResult run() {
        SimulationResult result = new SimulationResult();
        for (int i = 0; i < config.getRounds(); i++) {
            Result roundResult = game.playRound(strategy);
            switch (roundResult) {
                case PLAYER_WIN -> result.setPlayerWins(result.getPlayerWins() + 1);
                case DEALER_WIN -> result.setDealerWins(result.getDealerWins() + 1);
                case PUSH -> result.setPushes(result.getPushes() + 1);
            }
        }
        return result;
    }
}

