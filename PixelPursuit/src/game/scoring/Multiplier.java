package game.scoring;

import game.cosmetics.MultiplierInfo;
import game.settings.Difficulty;
import game.settings.GameConfig;

/**
 * Multiplier:
 *  - Wraps the final score multiplier applied at the end of a run.
 *  - Depends on death/escape, difficulty, and the equipped account multiplier.
 *  - Death → 0x, Easy escape → 1x, Hard escape → 2x, then scaled by account multiplier.
 */
public final class Multiplier {

    // ---------- FIELDS ----------

    private final double value;

    // ---------- CONSTRUCTORS ----------

    // Multiplier - Simple wrapper for a numeric multiplier value
    public Multiplier(double value) {
        this.value = value;
    }

    // ---------- ACCESSORS ----------

    // getValue - Returns the raw multiplier value (e.g., 0.0, 2.0, 6.0, 20.0)
    public double getValue() {
        return value;
    }

    // asInt - Returns the rounded integer value for UI text (e.g., "× 6")
    public int asInt() {
        return (int) Math.round(value);
    }

    // ---------- FACTORIES ----------

    // forOutcome - Legacy factory using only difficulty + escaped flag (no account bonus)
    public static Multiplier forOutcome(Difficulty difficulty, boolean escaped) {
        return forOutcome(difficulty, escaped, -1);  // -1 → account multiplier treated as 1x
    }

    // forOutcome - Full factory including equipped account multiplier index
    public static Multiplier forOutcome(Difficulty difficulty,
                                        boolean escaped,
                                        int accountMultIndex) {
        // Death always yields 0x multiplier
        if (!escaped) {
            return new Multiplier(GameConfig.MULTIPLIER_DEATH);
        }

        // Base multiplier from difficulty
        double base;
        if (difficulty == Difficulty.HARD) {
            base = GameConfig.MULTIPLIER_HARD;   // e.g., 2.0
        } else {
            base = GameConfig.MULTIPLIER_NORMAL; // e.g., 1.0
        }

        // Extra factor from the equipped account multiplier
        double accountMult = accountMultiplierValue(accountMultIndex);

        // Final combined multiplier
        return new Multiplier(base * accountMult);
    }

    // ---------- INTERNAL HELPERS ----------

    // accountMultiplierValue - Maps Account.multiplier index to numeric value, defaults to 1x
    private static double accountMultiplierValue(int index) {
        // MultiplierInfo.getValueForIndex: 0→2, 1→3, 2→5, 3→10, else 1
        if (index < 0) {
            return 1.0;
        }
        return (double) MultiplierInfo.getValueForIndex(index);
    }
}
