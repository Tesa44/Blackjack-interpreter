package blackjack.engine;

import java.util.List;

public class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(this.getRank()).append(" of ").append(this.getSuit());
        sb.append("]");
        return sb.toString();
    }
}

