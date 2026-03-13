package engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a hand of cards for a player or dealer.
 * Scoring rules will be added later.
 */
public class Hand {

    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public void clear() {
        cards.clear();
    }

    /**
     * Computes the best Blackjack value for this hand,
     * treating aces as 1 or 11 as appropriate.
     */
    public int getBestValue() {
        int sum = 0;
        int aces = 0;

        for (Card card : cards) {
            switch (card.getRank()) {
                case TWO:
                    sum += 2;
                    break;
                case THREE:
                    sum += 3;
                    break;
                case FOUR:
                    sum += 4;
                    break;
                case FIVE:
                    sum += 5;
                    break;
                case SIX:
                    sum += 6;
                    break;
                case SEVEN:
                    sum += 7;
                    break;
                case EIGHT:
                    sum += 8;
                    break;
                case NINE:
                    sum += 9;
                    break;
                case TEN:
                case JACK:
                case QUEEN:
                case KING:
                    sum += 10;
                    break;
                case ACE:
                    sum += 11;
                    aces++;
                    break;
            }
        }

        // Downgrade aces from 11 to 1 while we're busting
        while (sum > 21 && aces > 0) {
            sum -= 10;
            aces--;
        }

        return sum;
    }
}

