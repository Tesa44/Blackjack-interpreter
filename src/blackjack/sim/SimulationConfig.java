package blackjack.sim;

public class SimulationConfig {
    private int rounds;
    private int initialBalance;
    private int betPerGame = 1;

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
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
}

