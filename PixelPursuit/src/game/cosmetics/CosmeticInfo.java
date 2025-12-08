package game.cosmetics;

/**
 * Cosmetic descriptor:
 *  - Stores id, display name, icon file, and gold cost.
 *  - Central catalog (ALL) is used by UI and gameplay code.
 */
public class CosmeticInfo {

    // ---------- FIELDS ----------

    public final int id;
    public final String name;
    public final String iconFile;   // PNG name in /game/resources/images/
    public final int goldCost;

    // ---------- CONSTRUCTORS ----------

    // CosmeticInfo - Simple data holder for one cosmetic entry
    public CosmeticInfo(int id, String name, String iconFile, int goldCost) {
        this.id = id;
        this.name = name;
        this.iconFile = iconFile;
        this.goldCost = goldCost;
    }

    // ---------- CATALOG ----------

    public static final CosmeticInfo[] ALL = {
        new CosmeticInfo(PlayerCosmetics.COSMETIC_NONE,
                "None", null, 0),

        new CosmeticInfo(PlayerCosmetics.COSMETIC_BEANIE,
                "Beanie", "beanie.png", 250),

        new CosmeticInfo(PlayerCosmetics.COSMETIC_CROWN,
                "Crown", "crown.png", 250),

        new CosmeticInfo(PlayerCosmetics.COSMETIC_TOP_HAT,
                "Top Hat", "topHat.png", 250),

        new CosmeticInfo(PlayerCosmetics.COSMETIC_SHADES,
                "Shades", "shades.png", 500),

        new CosmeticInfo(PlayerCosmetics.COSMETIC_HEADPHONES,
                "Headphones", "headphones.png", 500),

        new CosmeticInfo(PlayerCosmetics.COSMETIC_CAT_PET,
                "Cat Pet", "catPet.png", 1000),

        new CosmeticInfo(PlayerCosmetics.COSMETIC_DOG_PET,
                "Dog Pet", "dogPet.png", 1000),

        new CosmeticInfo(PlayerCosmetics.COSMETIC_GOLD_SHADES,
                "Gold Shades", "goldShades.png", 10000),

        new CosmeticInfo(PlayerCosmetics.COSMETIC_GOLD_HAT,
                "Gold Hat", "goldHat.png", 10000),

        new CosmeticInfo(PlayerCosmetics.COSMETIC_DIAMOND_SHADES,
                "Diamond Shades", "diamondShades.png", 10000),

        new CosmeticInfo(PlayerCosmetics.COSMETIC_DIAMOND_HAT,
                "Diamond Hat", "diamondHat.png", 10000)
    };

    // ---------- HELPERS ----------

    // findById - Returns cosmetic info for a given id, or null if not found
    public static CosmeticInfo findById(int id) {
        for (CosmeticInfo c : ALL) {
            if (c.id == id) return c;
        }
        return null;
    }

    // indexOfId - Returns the index in ALL for this id, or -1 if not found
    public static int indexOfId(int id) {
        for (int i = 0; i < ALL.length; i++) {
            if (ALL[i].id == id) return i;
        }
        return -1;
    }
}
