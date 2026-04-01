package blackjack.sim;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class StreakStatistics {
    private final int totalStreaks;
    private final Map<Integer, Integer> streakCounts;

    public StreakStatistics(int totalStreaks, Map<Integer, Integer> streakCounts) {
        this.totalStreaks = totalStreaks;
        this.streakCounts = Collections.unmodifiableMap(new TreeMap<>(streakCounts));
    }

    public int getTotalStreaks() {
        return totalStreaks;
    }

    public Map<Integer, Integer> getStreakCounts() {
        return streakCounts;
    }

    public double getRate(int streakLength) {
        if (totalStreaks == 0) {
            return 0.0;
        }
        return (double) streakCounts.getOrDefault(streakLength, 0) / totalStreaks;
    }
}
