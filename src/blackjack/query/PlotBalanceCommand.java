package blackjack.query;

import blackjack.sim.BalancePlotter;
import blackjack.sim.SimulationResult;

public class PlotBalanceCommand {
    private final BalancePlotter balancePlotter;

    public PlotBalanceCommand(BalancePlotter balancePlotter) {
        this.balancePlotter = balancePlotter;
    }

    public String execute(SimulationResult simulationResult) {
        return balancePlotter.plot(simulationResult);
    }
}
