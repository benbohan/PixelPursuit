package game.scoring;

import game.settings.Difficulty;

/**
 * SessionResult:
 *  - Holds all scoring and outcome data for a single run.
 *  - Used by end-of-run dialogs and the leaderboard.
 */
public final class SessionResult {

    // ---------- FIELDS ----------

    private final double timeSeconds;

    private final int timeGold;          // from surviving
    private final int pickupGold;        // from pickups
    private final int baseGold;          // timeGold + pickupGold
    private final int finalGold;         // baseGold * multiplier
    private final int diamondsCollected; // raw diamonds picked up
    private final int finalDiamonds;     // diamondsCollected * multiplier

    private final Multiplier multiplier;
    private final Difficulty difficulty;
    private final boolean escaped;

    // ---------- CONSTRUCTORS ----------

    // SessionResult - Stores all numeric results for one completed run
    public SessionResult(double timeSeconds,
                         int timeGold,
                         int pickupGold,
                         int baseGold,
                         int finalGold,
                         int diamondsCollected,
                         int finalDiamonds,
                         Multiplier multiplier,
                         Difficulty difficulty,
                         boolean escaped) {

        this.timeSeconds       = timeSeconds;
        this.timeGold          = timeGold;
        this.pickupGold        = pickupGold;
        this.baseGold          = baseGold;
        this.finalGold         = finalGold;
        this.diamondsCollected = diamondsCollected;
        this.finalDiamonds     = finalDiamonds;
        this.multiplier        = multiplier;
        this.difficulty        = difficulty;
        this.escaped           = escaped;
    }

    // ---------- ACCESSORS ----------

    // getTimeSeconds - Returns total time survived in seconds
    public double getTimeSeconds() {
        return timeSeconds;
    }

    // getTimeGold - Returns gold earned from time survived
    public int getTimeGold() {
        return timeGold;
    }

    // getPickupGold - Returns gold collected from pickups
    public int getPickupGold() {
        return pickupGold;
    }

    // getBaseGold - Returns timeGold + pickupGold before multipliers
    public int getBaseGold() {
        return baseGold;
    }

    // getFinalGold - Returns final gold after multiplier
    public int getFinalGold() {
        return finalGold;
    }

    // getDiamondsCollected - Returns raw diamonds picked up during the run
    public int getDiamondsCollected() {
        return diamondsCollected;
    }

    // getFinalDiamonds - Returns final diamonds after multiplier
    public int getFinalDiamonds() {
        return finalDiamonds;
    }

    // getMultiplier - Returns the Multiplier object used for this run
    public Multiplier getMultiplier() {
        return multiplier;
    }

    // getDifficulty - Returns the Difficulty used for this run
    public Difficulty getDifficulty() {
        return difficulty;
    }

    // hasEscaped - Returns true if the player escaped, false if they died
    public boolean hasEscaped() {
        return escaped;
    }
}
