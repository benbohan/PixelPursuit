package game.cosmetics;

import java.awt.Color;

import game.account.Account;
import game.ui.theme.UiColors;

public class PlayerCosmetics {

    // ---------- COLOR IDS ----------

    // Bit positions for unlockable runner colors (0-14)
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
    public static final int COLOR_DEFAULT_GRAY = 14;
    
    // ---------- COSMETIC IDS ----------
    
    // Bit positions for unlockable runner cosmetics (15-27)
    public static final int COSMETIC_TOP_HAT     = 15;
    public static final int COSMETIC_BEANIE      = 16;
    public static final int COSMETIC_CROWN       = 17;
    public static final int COSMETIC_HEADBAND    = 18;
    public static final int COSMETIC_SHADES      = 19;
    public static final int COSMETIC_HEADPHONES  = 20;
    public static final int COSMETIC_SANTA_HAT   = 21;
    public static final int COSMETIC_CAT_PET     = 22;
    public static final int COSMETIC_DOG_PET     = 23;
    
    public static final int COSMETIC_GOLD_SHADES = 24;
    public static final int COSMETIC_DIAMOND_SHADES = 25;
    public static final int COSMETIC_GOLD_HAT = 26;
    public static final int COSMETIC_DIAMOND_HAT = 27;
    
    // ---------- MULTIPLIER IDS ----------
    
    public static final int MULTIPLIER_2X = 28;
    public static final int MULTIPLIER_5X = 29;
    public static final int MULTIPLIER_10X = 30;
    

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
        UiColors.PLAYER_DARK_GRAY,
        UiColors.PLAYER_DEFAULT_GRAY
    };
    
    // Default runner color used when no valid or unlocked color
    public static final Color DEFAULT_COLOR = UiColors.PLAYER_DEFAULT_GRAY;


    // ---------- COLOR UNLOCK BITMASK HELPERS ----------

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
    
    // ---------- COSMETIC UNLOCK BITMASK HELPERS ----------

    // Check if a cosmetic is unlocked for this account
    public static boolean hasCosmetic(Account acc, int cosmeticId) {
        if (acc == null) return false;
        long mask = acc.getUnlocks();
        long bit  = 1L << cosmeticId;   // cosmeticId is already 15–27
        return (mask & bit) != 0L;
    }

    // Unlock a cosmetic for this account
    public static void unlockCosmetic(Account acc, int cosmeticId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask |= (1L << cosmeticId);
        acc.setUnlocks(mask);
    }

    // Lock / remove a cosmetic from this account
    public static void lockCosmetic(Account acc, int cosmeticId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask &= ~(1L << cosmeticId);
        acc.setUnlocks(mask);
    }
 
    // ---------- MULTIPLIER UNLOCK BITMASK HELPERS ----------

    public static boolean hasMultiplier(Account acc, int multiplierId) {
        if (acc == null) return false;
        long mask = acc.getUnlocks();
        long bit  = 1L << multiplierId;   // multiplierId is 28–30
        return (mask & bit) != 0L;
    }

    public static void unlockMultiplier(Account acc, int multiplierId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask |= (1L << multiplierId);
        acc.setUnlocks(mask);
    }

    public static void lockMultiplier(Account acc, int multiplierId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask &= ~(1L << multiplierId);
        acc.setUnlocks(mask);
    }

    // Reference unlocks in Multiplier.java
    public static boolean has2x(Account acc)  { return hasMultiplier(acc, MULTIPLIER_2X); }
    public static boolean has5x(Account acc)  { return hasMultiplier(acc, MULTIPLIER_5X); }
    public static boolean has10x(Account acc) { return hasMultiplier(acc, MULTIPLIER_10X); }



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
    
    // ---------- EQUIPPED COSMETIC ----------

    // Equip a cosmetic (only if unlocked)
    public static void equipCosmetic(Account acc, int cosmeticId) {
        if (acc == null) return;
        if (!hasCosmetic(acc, cosmeticId)) return;
        acc.setCosmetic(cosmeticId);
    }

    // Get currently equipped cosmetic ID
    // Returns -1 if none or if the equipped one is no longer unlocked
    public static int getEquippedCosmetic(Account acc) {
        if (acc == null) return -1;
        int id = (int) acc.getCosmetic();
        return hasCosmetic(acc, id) ? id : -1;
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
