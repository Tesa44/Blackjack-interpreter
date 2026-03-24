package blackjack.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public int size() {
        return cards.size();
    }

    public Card getCard(int index) {
        return cards.get(index);
    }

    public void clear() {
        cards.clear();
    }

    public boolean isPair() {
        return cards.size() == 2 && cards.get(0).getRank() == cards.get(1).getRank();
    }

    public int getBestValue() {
        int sum = 0;
        int aces = 0;

        for (Card card : cards) {
            switch (card.getRank()) {
                case TWO -> sum += 2;
                case THREE -> sum += 3;
                case FOUR -> sum += 4;
                case FIVE -> sum += 5;
                case SIX -> sum += 6;
                case SEVEN -> sum += 7;
                case EIGHT -> sum += 8;
                case NINE -> sum += 9;
                case TEN, JACK, QUEEN, KING -> sum += 10;
                case ACE -> {
                    sum += 11;
                    aces++;
                }
            }
        }

        while (sum > 21 && aces > 0) {
            sum -= 10;
            aces--;
        }

        return sum;
    }
}

