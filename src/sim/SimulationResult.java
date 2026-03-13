package sim;

/**
 * Aggregated results of a Blackjack simulation run.
 */
public class SimulationResult {

    private int playerWins;
    private int dealerWins;
    private int pushes;

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
}

