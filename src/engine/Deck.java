package engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents one or more shuffled decks of cards.
 * Shuffling and draw rules will be added later.
 */
public class Deck {

    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        refill();
    }

    public Card draw() {
        if (cards.isEmpty()) {
            // automatically refill and shuffle when the shoe is exhausted
            refill();
        }
        return cards.remove(cards.size() - 1);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    private void refill() {
        cards.clear();
        // populate with a single standard 52-card deck
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        shuffle();
    }
}

