package game.cosmetics;

/**
 * Multiplier descriptor:
 *  - Stores index, unlock bit, numeric value, label text, and diamond cost.
 *  - Central catalog (ALL) is used by UI and gameplay code.
 */
public class MultiplierInfo {

    // ---------- FIELDS ----------

    public final int index;
    public final int bitIndex;
    public final int value;
    public final String label;
    public final int diamondCost;

    // ---------- CONSTRUCTORS ----------

    // MultiplierInfo - Simple data holder for one multiplier entry
    public MultiplierInfo(int index, int bitIndex, int value, String label, int diamondCost) {
        this.index = index;
        this.bitIndex = bitIndex;
        this.value = value;
        this.label = label;
        this.diamondCost = diamondCost;
    }

    // ---------- MULTIPLIER CATALOG ----------

    // ALL - Master list of all multipliers
    public static final MultiplierInfo[] ALL = {
        new MultiplierInfo(0, PlayerCosmetics.MULTIPLIER_2X,   2,  "2x",   10),
        new MultiplierInfo(1, PlayerCosmetics.MULTIPLIER_3X,   3,  "3x",  250),
        new MultiplierInfo(2, PlayerCosmetics.MULTIPLIER_5X,   5,  "5x",  500),
        new MultiplierInfo(3, PlayerCosmetics.MULTIPLIER_10X, 10, "10x", 1000)
    };

    // ---------- LOOKUPS ----------

    // byIndex - Returns multiplier info for an Account.multiplier index, or null if not found
    public static MultiplierInfo byIndex(int index) {
        for (MultiplierInfo m : ALL) {
            if (m.index == index) return m;
        }
        return null;
    }

    // byBitIndex - Returns multiplier info for an unlock bit index, or null if not found
    public static MultiplierInfo byBitIndex(int bitIndex) {
        for (MultiplierInfo m : ALL) {
            if (m.bitIndex == bitIndex) return m;
        }
        return null;
    }

    // getValueForIndex - Returns numeric value (2,3,5,10) for an index, or 1 if invalid
    public static int getValueForIndex(int index) {
        MultiplierInfo m = byIndex(index);
        return (m != null) ? m.value : 1;
    }

    // getLabelForIndex - Returns label ("2x", "3x", ...) for an index, or "1x" if invalid
    public static String getLabelForIndex(int index) {
        MultiplierInfo m = byIndex(index);
        return (m != null) ? m.label : "1x";
    }

    // getCostForIndex - Returns diamond cost for an index, or 0 if invalid
    public static int getCostForIndex(int index) {
        MultiplierInfo m = byIndex(index);
        return (m != null) ? m.diamondCost : 0;
    }
    
    // ---------- ACCOUNT-LEVEL HELPERS (treat invalid index as 1x) ----------

    /**
     * For Account.multiplier:
     *  - index in [0,3]  -> use shop multiplier (2x,3x,5x,10x)
     *  - anything else   -> treat as base 1x
     */
    public static int getValueForAccountIndex(int accountIndex) {
        if (accountIndex < 0) return 1;              // base 1x
        return getValueForIndex(accountIndex);       // 0..3 -> 2,3,5,10; others -> 1
    }

    public static String getLabelForAccountIndex(int accountIndex) {
        if (accountIndex < 0) return "1x";           // base 1x
        return getLabelForIndex(accountIndex);
    }
}
