package game.scoring;

import game.settings.Difficulty;

/**
 * Collection of the results of a single run.
 *
 * This keeps all the numbers that the end-of-run dialog and
 * Leaderboard might use.
 */
public final class SessionResult {

    private final double timeSeconds;

    private final int timeGold;     // from surviving
    private final int pickupGold;   // from pickups
    private final int baseGold;     // timeGold + pickupGold
    private final int finalGold;    // baseGold * multiplier

    private final Multiplier multiplier;
    private final Difficulty difficulty;
    private final boolean escaped;

    public SessionResult(double timeSeconds,
                         int timeGold,
                         int pickupGold,
                         int baseGold,
                         int finalGold,
                         Multiplier multiplier,
                         Difficulty difficulty,
                         boolean escaped) {

        this.timeSeconds = timeSeconds;
        this.timeGold = timeGold;
        this.pickupGold = pickupGold;
        this.baseGold = baseGold;
        this.finalGold = finalGold;
        this.multiplier = multiplier;
        this.difficulty = difficulty;
        this.escaped = escaped;
    }

    public double getTimeSeconds() {
        return timeSeconds;
    }

    public int getTimeGold() {
        return timeGold;
    }

    public int getPickupGold() {
        return pickupGold;
    }

    public int getBaseGold() {
        return baseGold;
    }

    public int getFinalGold() {
        return finalGold;
    }

    public Multiplier getMultiplier() {
        return multiplier;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public boolean hasEscaped() {
        return escaped;
    }
}
