package game.cosmetics;

import java.awt.Color;

/**
 * Stores cosmetic choices for the player.
 * For now: a single global runner color.
 * Later we can make this per-account + persisted.
 */
public class PlayerCosmetics {

    // Default: white runner
    private static Color runnerColor = Color.WHITE;

    public static Color getRunnerColor() {
        return runnerColor;
    }

    public static void setRunnerColor(Color color) {
        if (color != null) {
            runnerColor = color;
        }
    }
}
