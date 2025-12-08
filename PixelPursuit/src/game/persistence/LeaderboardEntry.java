package game.persistence;

/**
 * Single leaderboard entry:
 *  - Stores a username and their best run time in seconds.
 *  - Comparable so entries can be sorted by best time in descending order.
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry> {

    // ---------- FIELDS ----------

    private final String username;
    private final double bestTimeSeconds;

    // ---------- CONSTRUCTORS ----------

    // LeaderboardEntry - Creates an entry with a username and best time
    public LeaderboardEntry(String username, double bestTimeSeconds) {
        this.username = username;
        this.bestTimeSeconds = bestTimeSeconds;
    }

    // ---------- ACCESSORS ----------

    // getUsername - Returns the username for this entry
    public String getUsername() {
        return username;
    }

    // getBestTimeSeconds - Returns the best time in seconds for this entry
    public double getBestTimeSeconds() {
        return bestTimeSeconds;
    }

    // ---------- COMPARISON ----------

    // compareTo - Sorts entries by best time in descending order (highest first)
    @Override
    public int compareTo(LeaderboardEntry other) {
        return Double.compare(other.bestTimeSeconds, this.bestTimeSeconds);
    }
}
