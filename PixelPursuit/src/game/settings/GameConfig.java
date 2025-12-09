package game.settings;

/**
 * GameConfig - Central place for tunable gameplay constants and magic numbers.
 * Change values here instead of hunting through multiple classes.
 */
public final class GameConfig {

    // ---------- DIFFICULTY STATE ----------

    private static Difficulty currentDifficulty = Difficulty.EASY;

    private GameConfig() {
        // no instances
    }

    // getCurrentDifficulty - Returns the active difficulty used for new runs
    public static Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    // setCurrentDifficulty - Updates the global difficulty (must not be null)
    public static void setCurrentDifficulty(Difficulty difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("difficulty cannot be null");
        }
        currentDifficulty = difficulty;
    }

    // ---------- MAZE LAYOUT ----------

    public static final int MAZE_WIDTH  = 36;
    public static final int MAZE_HEIGHT = 18;

    // ---------- MOVEMENT & TIMING ----------

    // Survival gold: how often and how much
    public static final int RUNNER_MOVE_INTERVAL_MS      = 300;
    public static final double CHASER_MOVE_INTERVAL_SEC  = 0.6;
    public static final double GOLD_SPAWN_INTERVAL_SEC   = 5.0;
    public static final double SURVIVAL_GOLD_INTERVAL_SEC = 10.0;
    public static final int SURVIVAL_GOLD_PER_TICK       = 1;

    // ---------- SCORING / MULTIPLIERS ----------

    public static final double MULTIPLIER_DEATH  = 0.0;
    public static final double MULTIPLIER_NORMAL = 1.0;
    public static final double MULTIPLIER_HARD   = 2.0;

    public static final int DIAMOND_GOLD_VALUE   = 10;

    // ---------- LEADERBOARD ----------

    public static final int LEADERBOARD_MAX_ENTRIES = 10;

    // ---------- DIFFICULTY HELPERS ----------

    // getChaserCountForCurrentDifficulty - Returns chaser count based on difficulty
    public static int getChaserCountForCurrentDifficulty() {
        Difficulty d = getCurrentDifficulty();
        switch (d) {
            case HARD:
                //return 3;   // HARD: 2 chasers
                return 2;
            case EASY:
            default:
                return 1;
        }
    }

    // getDetectionRadiusForCurrentDifficulty - Returns chaser detection radius
    public static int getDetectionRadiusForCurrentDifficulty() {
        Difficulty d = getCurrentDifficulty();
        switch (d) {
            case HARD:
                return 11;
            case EASY:
            default:
                return 7;
        }
    }

    // getGoldSpawnIntervalForCurrentDifficulty - Returns gold spawn interval in seconds
    public static double getGoldSpawnIntervalForCurrentDifficulty() {
        Difficulty d = getCurrentDifficulty();
        switch (d) {
            case HARD:
                return 5.0;
            case EASY:
            default:
                return 4.0;
        }
    }

    // getDiamondChanceForCurrentDifficulty - Returns diamond spawn chance (0–1)
    public static double getDiamondChanceForCurrentDifficulty() {
        Difficulty d = getCurrentDifficulty();
        switch (d) {
            case HARD:
                return 0.20;
            case EASY:
            default:
                return 0.10;
        }
    }
}
