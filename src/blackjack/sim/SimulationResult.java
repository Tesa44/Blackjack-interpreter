package blackjack.sim;

import blackjack.engine.RoundResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulationResult {
    private int playerWins;
    private int dealerWins;
    private int pushes;
    private final List<RoundResult> roundResults = new ArrayList<>();

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

    public void addRoundResult(RoundResult roundResult) {
        roundResults.add(roundResult);
    }

    public List<RoundResult> getRoundResults() {
        return Collections.unmodifiableList(roundResults);
    }
}

