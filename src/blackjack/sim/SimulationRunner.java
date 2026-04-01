package blackjack.sim;

import blackjack.engine.BlackjackGame;
import blackjack.engine.Result;
import blackjack.engine.RoundResult;
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
        int balance = config.getInitialBalance();
        result.setInitialBalance(config.getInitialBalance());
        result.setBetPerGame(config.getBetPerGame());
        result.addBalanceSnapshot(balance);

        if (shouldUseUntilMode()) {
            validateUntilMode();
            while (shouldContinueUntil(balance)) {
                balance = playRound(result, balance);
            }
        } else {
            for (int i = 0; i < config.getRounds(); i++) {
                balance = playRound(result, balance);
            }
        }

        result.setFinalBalance(balance);
        return result;
    }

    private boolean shouldUseUntilMode() {
        return config.isRunUntilBroke() || config.getTargetBalance() != null;
    }

    private void validateUntilMode() {
        if (config.getBetPerGame() <= 0) {
            throw new IllegalArgumentException("Bet per game must be greater than 0 for bankroll-based simulation.");
        }
    }

    private boolean shouldContinueUntil(int balance) {
        if (config.getTargetBalance() != null) {
            return balance > 0 && balance < config.getTargetBalance();
        }
        return balance > 0;
    }

    private int playRound(SimulationResult result, int balance) {
            RoundResult roundResult = game.playRound(strategy);
            result.addRoundResult(roundResult);
            balance += roundResult.getNetBetUnits() * config.getBetPerGame();
            result.addBalanceSnapshot(balance);
            switch (roundResult.getResult()) {
                case PLAYER_WIN -> result.setPlayerWins(result.getPlayerWins() + 1);
                case DEALER_WIN -> result.setDealerWins(result.getDealerWins() + 1);
                case PUSH -> result.setPushes(result.getPushes() + 1);
            }
            return balance;
    }
}

