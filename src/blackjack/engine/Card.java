package blackjack.engine;

import java.util.List;

public class Card {
    private final Suit suit;
    private final Rank rank;
    private final int value;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        this.value = calculateValue();
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int getValue() { return value; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append(this.getRank()).append(" of ").append(this.getSuit());
        sb.append("]");
        return sb.toString();
    }

    private int calculateValue() {
        return switch (this.rank) {
            case TWO -> 2;
            case THREE -> 3;
            case FOUR -> 4;
            case FIVE -> 5;
            case SIX -> 6;
            case SEVEN -> 7;
            case EIGHT -> 8;
            case NINE -> 9;
            case TEN, JACK, QUEEN, KING -> 10;
            case ACE -> 11;
        };
    }
}

