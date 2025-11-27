package game.cosmetics;

import java.awt.Color;

import game.account.Account;
import game.ui.theme.UiColors;

public class PlayerCosmetics {

    // ---------- COLOR IDS ----------

    // Bit positions for unlockable runner colors (0-13)
    public static final int COLOR_RED        = 0;
    public static final int COLOR_ORANGE     = 1;
    public static final int COLOR_YELLOW     = 2;
    public static final int COLOR_DARK_GREEN = 3;
    public static final int COLOR_GREEN      = 4;
    public static final int COLOR_LIME       = 5;
    public static final int COLOR_DARK_BLUE  = 6;
    public static final int COLOR_TEAL       = 7;
    public static final int COLOR_LIGHT_BLUE = 8;
    public static final int COLOR_PURPLE     = 9;
    public static final int COLOR_LAVENDER   = 10;
    public static final int COLOR_PINK       = 11;
    public static final int COLOR_BLACK      = 12;
    public static final int COLOR_DARK_GRAY  = 13;

    // Default runner color (available even if no cosmetics unlocked)
    public static final Color DEFAULT_COLOR = UiColors.PLAYER_DEFAULT;

    // Map color IDs to actual Colors
    private static final Color[] COLOR_TABLE = {
        UiColors.PLAYER_RED,
        UiColors.PLAYER_ORANGE,
        UiColors.PLAYER_YELLOW,
        UiColors.PLAYER_DARK_GREEN,
        UiColors.PLAYER_GREEN,
        UiColors.PLAYER_LIME,
        UiColors.PLAYER_DARK_BLUE,
        UiColors.PLAYER_TEAL,
        UiColors.PLAYER_LIGHT_BLUE,
        UiColors.PLAYER_PURPLE,
        UiColors.PLAYER_LAVENDER,
        UiColors.PLAYER_PINK,
        UiColors.PLAYER_BLACK,
        UiColors.PLAYER_DARK_GRAY
    };


    // ---------- UNLOCK BITMASK HELPERS ----------

    // Check if a color is unlocked for this account
    public static boolean hasColor(Account acc, int colorId) {
        if (acc == null) return false;
        long mask = acc.getUnlocks();
        long bit  = 1L << colorId;
        return (mask & bit) != 0L;
    }

    // Unlock a color for this account
    public static void unlockColor(Account acc, int colorId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask |= (1L << colorId);
        acc.setUnlocks(mask);
    }

    // Lock / remove a color from this account
    public static void lockColor(Account acc, int colorId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask &= ~(1L << colorId);
        acc.setUnlocks(mask);
    }


    // ---------- EQUIPPED COLOR ----------

    // Equip a color (only if unlocked)
    public static void equipColor(Account acc, int colorId) {
        if (acc == null) return;
        if (!hasColor(acc, colorId)) return;
        acc.setColor(colorId);
    }

    // Get Color for the currently equipped color on this account
    public static Color getRunnerColor(Account acc) {
        if (acc == null) return DEFAULT_COLOR;
        int colorId = (int) acc.getColor();
        if (!hasColor(acc, colorId)) return DEFAULT_COLOR;
        return getColor(colorId);
    }


    // ---------- COLOR LOOKUP ----------

    // Get Color for a given colorId (no account check)
    public static Color getColor(int colorId) {
        if (colorId < 0 || colorId >= COLOR_TABLE.length) {
            return DEFAULT_COLOR;
        }
        return COLOR_TABLE[colorId];
    }

    // Get runner color for this account and colorId (falls back to default if locked)
    public static Color getRunnerColor(Account acc, int colorId) {
        if (acc == null) return DEFAULT_COLOR;
        if (!hasColor(acc, colorId)) return DEFAULT_COLOR;
        return getColor(colorId);
    }
}
