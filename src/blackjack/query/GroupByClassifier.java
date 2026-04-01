package blackjack.query;

import blackjack.engine.RoundResult;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class GroupByClassifier {
    private final List<String> properties;

    public GroupByClassifier(List<String> properties) {
        this.properties = List.copyOf(properties);
    }

    public List<String> getProperties() {
        return properties;
    }

    public GroupKey classify(RoundResult result) {
        StringJoiner joiner = new StringJoiner(", ");
        List<Comparable<?>> sortValues = new ArrayList<>();
        for (String property : properties) {
            PropertyValue propertyValue = describeProperty(property, result);
            joiner.add(propertyValue.label());
            sortValues.add(propertyValue.sortValue());
        }
        return new GroupKey(joiner.toString(), sortValues);
    }

    private PropertyValue describeProperty(String property, RoundResult result) {
        return switch (property) {
            case "dealer.upcard", "dealer.Upcard", "dealer.init", "dealer.Init" ->
                    new PropertyValue("Dealer: " + result.getDealerUpcardValue(), result.getDealerUpcardValue());
            case "dealer.total" -> new PropertyValue("Dealer total: " + result.getDealerValue(), result.getDealerValue());
            case "player.initialTotal", "player.InitialTotal", "player.init", "player.Init" ->
                    new PropertyValue("Player initial: " + result.getPlayerInitialTotal(), result.getPlayerInitialTotal());
            case "action" -> new PropertyValue("Action: " + result.getAction(), result.getAction().ordinal());
            case "player.isPair", "player.IsPair" -> new PropertyValue("Player pair: " + result.hasPlayerPair(), result.hasPlayerPair());
            case "player.isSoft", "player.IsSoft" -> new PropertyValue("Player soft: " + result.hasPlayerSoftHand(), result.hasPlayerSoftHand());
            default -> throw new IllegalArgumentException("Unsupported group by property: " + property);
        };
    }

    private record PropertyValue(String label, Comparable<?> sortValue) {
    }

    public record GroupKey(String label, List<Comparable<?>> sortValues) implements Comparable<GroupKey> {
        @Override
        public int compareTo(GroupKey other) {
            int max = Math.min(sortValues.size(), other.sortValues.size());
            for (int i = 0; i < max; i++) {
                @SuppressWarnings("unchecked")
                Comparable<Object> left = (Comparable<Object>) sortValues.get(i);
                Object right = other.sortValues.get(i);
                int comparison = left.compareTo(right);
                if (comparison != 0) {
                    return comparison;
                }
            }
            if (sortValues.size() != other.sortValues.size()) {
                return Integer.compare(sortValues.size(), other.sortValues.size());
            }
            return label.compareTo(other.label);
        }
    }
}
