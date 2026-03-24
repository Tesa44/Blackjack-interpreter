package blackjack.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        refill();
    }

    public Card draw() {
        if (cards.isEmpty()) {
            refill();
        }
        return cards.remove(cards.size() - 1);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    private void refill() {
        cards.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        shuffle();
    }
}

