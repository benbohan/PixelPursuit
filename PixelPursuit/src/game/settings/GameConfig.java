package game.settings;

/**
 * Central place for all game tunables and magic numbers.
 * Change values here instead of hunting through multiple classes.
 */
public final class GameConfig {

    private GameConfig() {
        // no instances
    }

    // ----- Maze layout -----

    // Logical maze dimensions in cells
    // (you widened it by 4 columns: 32 -> 36)
    public static final int MAZE_WIDTH  = 36;
    public static final int MAZE_HEIGHT = 18;

    // ----- Movement & timing -----

    // Runner glide step in ms (Swing Timer in GamePanel)
    public static final int RUNNER_MOVE_INTERVAL_MS = 120;

    // Chaser step interval in seconds (used in Session)
    public static final double CHASER_MOVE_INTERVAL_SEC = 0.24;

    // Random gold spawn interval in seconds
    public static final double GOLD_SPAWN_INTERVAL_SEC = 1.5;

    // Survival gold: how often and how much
    public static final double SURVIVAL_GOLD_INTERVAL_SEC = 1.0;
    public static final int SURVIVAL_GOLD_PER_TICK = 1;

    // ----- Scoring / multipliers -----

    // For now we only use 0x (death) and 1x (escape),
    // but this is ready for 2x/3x on harder AIs later.
    public static final double MULTIPLIER_DEATH  = 0.0;
    public static final double MULTIPLIER_NORMAL = 1.0;
    public static final double MULTIPLIER_HARD   = 2.0;

    // ----- Leaderboard -----

    public static final int LEADERBOARD_MAX_ENTRIES = 10;
}
