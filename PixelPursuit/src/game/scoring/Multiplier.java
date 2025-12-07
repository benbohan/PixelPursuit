package game.scoring;

import game.settings.Difficulty;
import game.settings.GameConfig;

/**
 * Represents the overall score multiplier applied at the end of a run.
 *
 * Now multiplier depends on:
 *  - whether the runner escaped or died
 *  - the selected difficulty (Easy / Hard)
 *  - the equipped account multiplier (2x, 3x, 5x, 10x)
 *
 * Rules:
 *  - If the player dies, the multiplier is always 0 (DEATH).
 *  - If they escape on EASY, base = MULTIPLIER_NORMAL (1.0).
 *  - If they escape on HARD, base = MULTIPLIER_HARD (2.0).
 *  - Overall multiplier = base * accountMultiplierValue.
 */
public final class Multiplier {

    private final double value;

    public Multiplier(double value) {
        this.value = value;
    }

    /** Final numeric multiplier (e.g., 0.0, 2.0, 6.0, 10.0, 20.0, ...) */
    public double getValue() {
        return value;
    }

    /** Rounded integer version, useful for UI text ("× 6"). */
    public int asInt() {
        return (int) Math.round(value);
    }

    // --------------------------------------------------------------------
    //  FACTORIES
    // --------------------------------------------------------------------

    /**
     * Legacy factory: only depends on difficulty + outcome.
     * Equivalent to using an "account multiplier" of 1.0 (no extra bonus).
     *
     * Death → 0x
     * Easy → 1x
     * Hard → 2x
     */
    public static Multiplier forOutcome(Difficulty difficulty, boolean escaped) {
        return forOutcome(difficulty, escaped, -1);  // -1 => accountMult = 1.0
    }

    /**
     * New factory: includes the equipped account multiplier.
     *
     * @param difficulty   current game difficulty
     * @param escaped      true if player escaped, false if died
     * @param accountMultIndex value stored in Account.getMultiplier()
     *                          0 → 2x, 1 → 3x, 2 → 5x, 3 → 10x (default 1x)
     */
    public static Multiplier forOutcome(Difficulty difficulty,
                                        boolean escaped,
                                        int accountMultIndex) {
        // If the player died, multiplier is always 0, regardless of everything else.
        if (!escaped) {
            return new Multiplier(GameConfig.MULTIPLIER_DEATH); // 0.0
        }

        // Base multiplier from difficulty
        double base;
        if (difficulty == Difficulty.HARD) {
            base = GameConfig.MULTIPLIER_HARD;   // 2.0
        } else {
            base = GameConfig.MULTIPLIER_NORMAL; // 1.0 (Easy / default)
        }

        // Extra multiplier from the equipped account multiplier
        double accountMult = accountMultiplierValue(accountMultIndex);

        // Final combined multiplier for scoring and UI
        return new Multiplier(base * accountMult);
    }

    // --------------------------------------------------------------------
    //  INTERNAL HELPER
    // --------------------------------------------------------------------

    /**
     * Maps the Account.multiplier field to a numeric value.
     *
     * Expected mapping (CustomizeWindow currently uses index 0–3):
     *   0 → 2x
     *   1 → 3x
     *   2 → 5x
     *   3 → 10x
     * Any other / unknown index → 1x (no bonus).
     */
    private static double accountMultiplierValue(int index) {
        switch (index) {
            case 0: return 2.0;
            case 1: return 3.0;
            case 2: return 5.0;
            case 3: return 10.0;
            default:
                // -1 or anything unexpected → treat as "no extra multiplier"
                return 1.0;
        }
    }
}
