package blackjack.sim;

import blackjack.engine.RoundResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulationResult {
    private int playerWins;
    private int dealerWins;
    private int pushes;
    private int initialBalance;
    private int betPerGame;
    private int finalBalance;
    private final List<RoundResult> roundResults = new ArrayList<>();
    private final List<Integer> balanceHistory = new ArrayList<>();

    public int getPlayerWins() {
        return playerWins;
    }

    public void setPlayerWins(int playerWins) {
        this.playerWins = playerWins;
    }

    public int getDealerWins() {
        return dealerWins;
    }

    public void setDealerWins(int dealerWins) {
        this.dealerWins = dealerWins;
    }

    public int getPushes() {
        return pushes;
    }

    public void setPushes(int pushes) {
        this.pushes = pushes;
    }

    public int getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(int initialBalance) {
        this.initialBalance = initialBalance;
    }

    public int getBetPerGame() {
        return betPerGame;
    }

    public void setBetPerGame(int betPerGame) {
        this.betPerGame = betPerGame;
    }

    public int getFinalBalance() {
        return finalBalance;
    }

    public void setFinalBalance(int finalBalance) {
        this.finalBalance = finalBalance;
    }

    public void addRoundResult(RoundResult roundResult) {
        roundResults.add(roundResult);
    }

    public List<RoundResult> getRoundResults() {
        return Collections.unmodifiableList(roundResults);
    }

    public void addBalanceSnapshot(int balance) {
        balanceHistory.add(balance);
    }

    public List<Integer> getBalanceHistory() {
        return Collections.unmodifiableList(balanceHistory);
    }
}

