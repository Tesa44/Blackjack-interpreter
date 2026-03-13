package sim;

import engine.BlackjackGame;
import engine.RoundResult;

/**
 * Coordinates running many rounds of Blackjack driven by a DSL script.
 */
public class SimulationRunner {

    private final SimulationConfig config;
    private final BlackjackGame game;

    public SimulationRunner(SimulationConfig config, BlackjackGame game) {
        this.config = config;
        this.game = game;
    }

    public SimulationResult run() {
        SimulationResult result = new SimulationResult();
        for (int i = 0; i < config.getRounds(); i++) {
            RoundResult roundResult = game.playSimpleRound();
            switch (roundResult) {
                case PLAYER_WIN:
                    result.setPlayerWins(result.getPlayerWins() + 1);
                    break;
                case DEALER_WIN:
                    result.setDealerWins(result.getDealerWins() + 1);
                    break;
                case PUSH:
                    result.setPushes(result.getPushes() + 1);
                    break;
            }
        }

        return result;
    }
}

