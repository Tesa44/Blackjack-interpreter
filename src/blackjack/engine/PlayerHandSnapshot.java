package blackjack.engine;

public class PlayerHandSnapshot {
    private final Hand hand;
    private final int betMultiplier;

    public PlayerHandSnapshot(Hand hand, int betMultiplier) {
        this.hand = new Hand(hand);
        this.betMultiplier = betMultiplier;
    }

    public Hand getHand() {
        return new Hand(hand);
    }

    public int getBetMultiplier() {
        return betMultiplier;
    }
}
