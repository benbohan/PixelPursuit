package game.cosmetics;

/**
 * Runner color descriptor:
 *  - Encapsulates a runner color's ID, display name, and gold cost.
 *  - Central table (ALL) is used by UI code to populate color grids + prices.
 *  - Allows simple lookup by ID so game logic stays decoupled from UI wiring.
 */
public class ColorInfo {

    // ---------- FIELDS ----------

    public final int id;        // PlayerCosmetics.COLOR_*
    public final String name;
    public final int goldCost;  // cost in gold

    // ---------- CONSTRUCTORS ----------

    // ColorInfo - Simple data holder for one runner color entry
    public ColorInfo(int id, String name, int goldCost) {
        this.id = id;
        this.name = name;
        this.goldCost = goldCost;
    }

    // ---------- COLOR TABLE ----------

    // ALL - Master list of all runner colors and their gold costs
    // Adjust prices however you like.
    public static final ColorInfo[] ALL = {
        new ColorInfo(PlayerCosmetics.COLOR_RED,          "Red",         100),
        new ColorInfo(PlayerCosmetics.COLOR_ORANGE,       "Orange",      100),
        new ColorInfo(PlayerCosmetics.COLOR_YELLOW,       "Yellow",      100),
        new ColorInfo(PlayerCosmetics.COLOR_DARK_GREEN,   "Dark Green",  100),
        new ColorInfo(PlayerCosmetics.COLOR_GREEN,        "Green",       100),
        new ColorInfo(PlayerCosmetics.COLOR_LIME,         "Lime",        100),
        new ColorInfo(PlayerCosmetics.COLOR_DARK_BLUE,    "Dark Blue",   100),
        new ColorInfo(PlayerCosmetics.COLOR_TEAL,         "Teal",        100),
        new ColorInfo(PlayerCosmetics.COLOR_LIGHT_BLUE,   "Light Blue",  100),
        new ColorInfo(PlayerCosmetics.COLOR_PURPLE,       "Purple",      100),
        new ColorInfo(PlayerCosmetics.COLOR_LAVENDER,     "Lavender",    100),
        new ColorInfo(PlayerCosmetics.COLOR_PINK,         "Pink",        100),
        new ColorInfo(PlayerCosmetics.COLOR_BLACK,        "Black",       100),
        new ColorInfo(PlayerCosmetics.COLOR_DARK_GRAY,    "Dark Gray",   100),
        new ColorInfo(PlayerCosmetics.COLOR_DEFAULT_GRAY, "Default",       0)  // free
    };

    // ---------- LOOKUP HELPERS ----------

    // findById - Returns the ColorInfo with the given ID, or null if not found
    public static ColorInfo findById(int id) {
        for (ColorInfo c : ALL) {
            if (c.id == id) return c;
        }
        return null;
    }
}
