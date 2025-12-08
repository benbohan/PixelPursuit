package game.settings;

/**
 * Difficulty:
 *  - Represents overall difficulty for a run of Pixel Pursuit.
 *  - Currently supports EASY and HARD but can be extended later.
 */
public enum Difficulty {

    // ---------- VALUES ----------

    EASY("Easy"),
    HARD("Hard");

    // ---------- FIELDS ----------

    private final String displayName;

    // ---------- CONSTRUCTORS ----------

    // Difficulty - Stores a human-readable display name
    Difficulty(String displayName) {
        this.displayName = displayName;
    }

    // ---------- ACCESSORS ----------

    // getDisplayName - Returns the human-readable name for this difficulty
    public String getDisplayName() {
        return displayName;
    }

    // toString - Returns the display name (used by UI / debugging)
    @Override
    public String toString() {
        return displayName;
    }
}
