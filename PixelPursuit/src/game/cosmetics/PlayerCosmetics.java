package game.cosmetics;

import java.awt.Color;
import java.awt.image.BufferedImage;

import game.account.Account;
import game.ui.theme.UiColors;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;

/**
 * Player cosmetics system:
 *  - Defines IDs for colors, cosmetics, and multipliers stored in Account.unlocks.
 *  - Loads cosmetic images and offset hints for drawing over the runner.
 *  - Provides helpers to unlock, equip, and query colors, cosmetics, and multipliers.
 */
public class PlayerCosmetics {

    // ---------- COLOR IDS ----------

    public static final int COLOR_RED           = 0;
    public static final int COLOR_ORANGE        = 1;
    public static final int COLOR_YELLOW        = 2;
    public static final int COLOR_DARK_GREEN    = 3;
    public static final int COLOR_GREEN         = 4;
    public static final int COLOR_LIME          = 5;
    public static final int COLOR_DARK_BLUE     = 6;
    public static final int COLOR_TEAL          = 7;
    public static final int COLOR_LIGHT_BLUE    = 8;
    public static final int COLOR_PURPLE        = 9;
    public static final int COLOR_LAVENDER      = 10;
    public static final int COLOR_PINK          = 11;
    public static final int COLOR_BLACK         = 12;
    public static final int COLOR_DARK_GRAY     = 13;
    public static final int COLOR_DEFAULT_GRAY  = 14;

    // ---------- COSMETIC IDS ----------

    public static final int COSMETIC_NONE           = 15;
    public static final int COSMETIC_BEANIE         = 16;
    public static final int COSMETIC_CROWN          = 17;
    public static final int COSMETIC_TOP_HAT        = 18;
    public static final int COSMETIC_SHADES         = 19;
    public static final int COSMETIC_HEADPHONES     = 20;
    // 21 unused
    public static final int COSMETIC_CAT_PET        = 22;
    public static final int COSMETIC_DOG_PET        = 23;
    public static final int COSMETIC_GOLD_SHADES    = 24;
    public static final int COSMETIC_GOLD_HAT       = 25;
    public static final int COSMETIC_DIAMOND_SHADES = 26;
    public static final int COSMETIC_DIAMOND_HAT    = 27;

    // ---------- MULTIPLIER IDS ----------

    public static final int MULTIPLIER_2X  = 28;
    public static final int MULTIPLIER_3X  = 29;
    public static final int MULTIPLIER_5X  = 30;
    public static final int MULTIPLIER_10X = 31;

    // ---------- COLOR TABLE ----------

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

    public static final Color DEFAULT_COLOR = UiColors.PLAYER_DEFAULT_GRAY;

    // ---------- COSMETIC SPRITES & OFFSETS ----------

    private static final BufferedImage[] COSMETIC_SPRITES = new BufferedImage[32];
    private static final int[] COSMETIC_OFFSET_X = new int[32];
    private static final int[] COSMETIC_OFFSET_Y = new int[32];

    static {
        COSMETIC_SPRITES[COSMETIC_BEANIE]         = loadCosmetic("beanie.png");
        COSMETIC_SPRITES[COSMETIC_CROWN]          = loadCosmetic("crown.png");
        COSMETIC_SPRITES[COSMETIC_TOP_HAT]        = loadCosmetic("topHat.png");
        COSMETIC_SPRITES[COSMETIC_SHADES]         = loadCosmetic("shades.png");
        COSMETIC_SPRITES[COSMETIC_HEADPHONES]     = loadCosmetic("headphones.png");
        COSMETIC_SPRITES[COSMETIC_CAT_PET]        = loadCosmetic("catPet.png");
        COSMETIC_SPRITES[COSMETIC_DOG_PET]        = loadCosmetic("dogPet.png");
        COSMETIC_SPRITES[COSMETIC_GOLD_SHADES]    = loadCosmetic("goldShades.png");
        COSMETIC_SPRITES[COSMETIC_GOLD_HAT]       = loadCosmetic("goldHat.png");
        COSMETIC_SPRITES[COSMETIC_DIAMOND_SHADES] = loadCosmetic("diamondShades.png");
        COSMETIC_SPRITES[COSMETIC_DIAMOND_HAT]    = loadCosmetic("diamondHat.png");

        setCosmeticOffset(COSMETIC_BEANIE,          0, -8);
        setCosmeticOffset(COSMETIC_CROWN,           0, -10);
        setCosmeticOffset(COSMETIC_TOP_HAT,         0, -12);
        setCosmeticOffset(COSMETIC_HEADPHONES,      0, -6);
        setCosmeticOffset(COSMETIC_GOLD_HAT,        0, -10);
        setCosmeticOffset(COSMETIC_DIAMOND_HAT,     0, -10);

        setCosmeticOffset(COSMETIC_SHADES,          0, -2);
        setCosmeticOffset(COSMETIC_GOLD_SHADES,     0, -2);
        setCosmeticOffset(COSMETIC_DIAMOND_SHADES,  0, -2);

        setCosmeticOffset(COSMETIC_CAT_PET,        +10, +10);
        setCosmeticOffset(COSMETIC_DOG_PET,        +10, +10);
    }

    // setCosmeticOffset - Stores pixel offset for a cosmetic sprite id
    private static void setCosmeticOffset(int id, int dx, int dy) {
        if (id < 0 || id >= COSMETIC_OFFSET_X.length) return;
        COSMETIC_OFFSET_X[id] = dx;
        COSMETIC_OFFSET_Y[id] = dy;
    }

    // loadCosmetic - Loads a cosmetic PNG from the resources folder
    private static BufferedImage loadCosmetic(String fileName) {
        try {
            URL url = PlayerCosmetics.class.getResource("/game/resources/images/" + fileName);
            if (url == null) {
                System.err.println("PlayerCosmetics: cosmetic image not found: " + fileName);
                return null;
            }
            return ImageIO.read(url);
        } catch (IOException e) {
            System.err.println("PlayerCosmetics: error loading cosmetic: " + fileName);
            e.printStackTrace();
            return null;
        }
    }

    // getCosmeticImage - Returns the sprite for a cosmetic id, or null if missing
    public static BufferedImage getCosmeticImage(int cosmeticId) {
        if (cosmeticId < 0 || cosmeticId >= COSMETIC_SPRITES.length) return null;
        return COSMETIC_SPRITES[cosmeticId];
    }

    // getEquippedCosmeticImage - Returns sprite for the account's equipped cosmetic
    public static BufferedImage getEquippedCosmeticImage(Account acc) {
        int id = getEquippedCosmetic(acc);
        if (id == -1) return null;
        return getCosmeticImage(id);
    }

    // getCosmeticOffsetX - Returns x-offset for a cosmetic id
    public static int getCosmeticOffsetX(int cosmeticId) {
        if (cosmeticId < 0 || cosmeticId >= COSMETIC_OFFSET_X.length) return 0;
        return COSMETIC_OFFSET_X[cosmeticId];
    }

    // getCosmeticOffsetY - Returns y-offset for a cosmetic id
    public static int getCosmeticOffsetY(int cosmeticId) {
        if (cosmeticId < 0 || cosmeticId >= COSMETIC_OFFSET_Y.length) return 0;
        return COSMETIC_OFFSET_Y[cosmeticId];
    }

    // ---------- COLOR UNLOCK BITMASK HELPERS ----------

    // hasColor - Returns true if a color bit is set on this account
    public static boolean hasColor(Account acc, int colorId) {
        if (acc == null) return false;
        long mask = acc.getUnlocks();
        long bit  = 1L << colorId;
        return (mask & bit) != 0L;
    }

    // unlockColor - Sets the bit for a color on this account
    public static void unlockColor(Account acc, int colorId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask |= (1L << colorId);
        acc.setUnlocks(mask);
    }

    // lockColor - Clears the bit for a color on this account
    public static void lockColor(Account acc, int colorId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask &= ~(1L << colorId);
        acc.setUnlocks(mask);
    }

    // ---------- COSMETIC UNLOCK BITMASK HELPERS ----------

    // hasCosmetic - Returns true if a cosmetic bit is set on this account
    public static boolean hasCosmetic(Account acc, int cosmeticId) {
        if (acc == null) return false;
        long mask = acc.getUnlocks();
        long bit  = 1L << cosmeticId;
        return (mask & bit) != 0L;
    }

    // unlockCosmetic - Sets the bit for a cosmetic on this account
    public static void unlockCosmetic(Account acc, int cosmeticId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask |= (1L << cosmeticId);
        acc.setUnlocks(mask);
    }

    // lockCosmetic - Clears the bit for a cosmetic on this account
    public static void lockCosmetic(Account acc, int cosmeticId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask &= ~(1L << cosmeticId);
        acc.setUnlocks(mask);
    }

    // ---------- MULTIPLIER UNLOCK BITMASK HELPERS ----------

    // hasMultiplier - Returns true if a multiplier bit is set on this account
    public static boolean hasMultiplier(Account acc, int multiplierId) {
        if (acc == null) return false;
        long mask = acc.getUnlocks();
        long bit  = 1L << multiplierId;
        return (mask & bit) != 0L;
    }

    // unlockMultiplier - Sets the bit for a multiplier on this account
    public static void unlockMultiplier(Account acc, int multiplierId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask |= (1L << multiplierId);
        acc.setUnlocks(mask);
    }

    // lockMultiplier - Clears the bit for a multiplier on this account
    public static void lockMultiplier(Account acc, int multiplierId) {
        if (acc == null) return;
        long mask = acc.getUnlocks();
        mask &= ~(1L << multiplierId);
        acc.setUnlocks(mask);
    }

    // has2x - Convenience check for 2x multiplier bit
    public static boolean has2x(Account acc)  { return hasMultiplier(acc, MULTIPLIER_2X); }

    // has3x - Convenience check for 3x multiplier bit
    public static boolean has3x(Account acc)  { return hasMultiplier(acc, MULTIPLIER_3X); }

    // has5x - Convenience check for 5x multiplier bit
    public static boolean has5x(Account acc)  { return hasMultiplier(acc, MULTIPLIER_5X); }

    // has10x - Convenience check for 10x multiplier bit
    public static boolean has10x(Account acc) { return hasMultiplier(acc, MULTIPLIER_10X); }

    // ---------- EQUIPPED COLOR ----------

    // equipColor - Equips a color only if it is unlocked
    public static void equipColor(Account acc, int colorId) {
        if (acc == null) return;
        if (!hasColor(acc, colorId)) return;
        acc.setColor(colorId);
    }

    // getRunnerColor - Returns the equipped runner color or default if invalid
    public static Color getRunnerColor(Account acc) {
        if (acc == null) return DEFAULT_COLOR;
        int colorId = acc.getColor();
        if (!hasColor(acc, colorId)) return DEFAULT_COLOR;
        return getColor(colorId);
    }

    // ---------- EQUIPPED COSMETIC ----------

    // equipCosmetic - Equips a cosmetic only if it is unlocked
    public static void equipCosmetic(Account acc, int cosmeticId) {
        if (acc == null) return;
        if (!hasCosmetic(acc, cosmeticId)) return;
        acc.setCosmetic(cosmeticId);
    }

    // getEquippedCosmetic - Returns equipped cosmetic id or -1 if none/unlocked
    public static int getEquippedCosmetic(Account acc) {
        if (acc == null) return -1;
        int id = acc.getCosmetic();
        return hasCosmetic(acc, id) ? id : -1;
    }

    // ---------- EQUIPPED MULTIPLIER ----------

    // getEquippedMultiplierId - Returns equipped multiplier id or -1 if none/unlocked
    public static int getEquippedMultiplierId(Account acc) {
        if (acc == null) return -1;
        int id = acc.getMultiplier();
        return hasMultiplier(acc, id) ? id : -1;
    }

    // getEquippedMultiplierValue - Returns numeric multiplier value or 1 if none
    public static int getEquippedMultiplierValue(Account acc) {
        int id = getEquippedMultiplierId(acc);
        switch (id) {
            case MULTIPLIER_2X:  return 2;
            case MULTIPLIER_3X:  return 3;
            case MULTIPLIER_5X:  return 5;
            case MULTIPLIER_10X: return 10;
            default:             return 1;
        }
    }

    // ---------- COLOR LOOKUP ----------

    // getColor - Returns Color for a colorId or DEFAULT_COLOR if out of range
    public static Color getColor(int colorId) {
        if (colorId < 0 || colorId >= COLOR_TABLE.length) {
            return DEFAULT_COLOR;
        }
        return COLOR_TABLE[colorId];
    }

    // getRunnerColor - Returns Color for this account + colorId, or default if locked
    public static Color getRunnerColor(Account acc, int colorId) {
        if (acc == null) return DEFAULT_COLOR;
        if (!hasColor(acc, colorId)) return DEFAULT_COLOR;
        return getColor(colorId);
    }
}
