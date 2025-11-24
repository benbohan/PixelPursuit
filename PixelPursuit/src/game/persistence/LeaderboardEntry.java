package game.persistence;

public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    private final String username;
    private final double bestTimeSeconds;

    public LeaderboardEntry(String username, double bestTimeSeconds) {
        this.username = username;
        this.bestTimeSeconds = bestTimeSeconds;
    }

    public String getUsername() {
        return username;
    }

    public double getBestTimeSeconds() {
        return bestTimeSeconds;
    }

    @Override
    public int compareTo(LeaderboardEntry other) {
        // sort DESC by best time (highest first)
        return Double.compare(other.bestTimeSeconds, this.bestTimeSeconds);
    }
}
