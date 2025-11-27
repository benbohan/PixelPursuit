package game.scoring;

import game.settings.Difficulty;
import game.settings.GameConfig;

/**
 * Represents the overall score multiplier applied at the end of a run.
 *
 * Currently multiplier depends on:
 *  - whether the runner escaped or died, and
 *  - the selected difficulty.
 *
 * Death always gives 0x. Escaping on EASY gives 1x, escaping on HARD
 * gives 2x (using the constants in GameConfig).
 */
public final class Multiplier {

    private final double value;

    public Multiplier(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public int asInt() {
        return (int) Math.round(value);
    }

    /** Factory: pick a multiplier based on difficulty + outcome. */
    public static Multiplier forOutcome(Difficulty difficulty, boolean escaped) {
        if (!escaped) {
            // Caught by a chaser
            return new Multiplier(GameConfig.MULTIPLIER_DEATH);
        }

        if (difficulty == Difficulty.HARD) {
            return new Multiplier(GameConfig.MULTIPLIER_HARD);
        } else {
            // EASY (and any future default difficulties)
            return new Multiplier(GameConfig.MULTIPLIER_NORMAL);
        }
    }
}
