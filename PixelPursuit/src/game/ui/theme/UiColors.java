package game.ui.theme;

import java.awt.Color;

/**
 * UiColors - Shared color palette for the UI and game world.
 *  - Central place for dark theme, text, and accent colors.
 *  - Also defines maze, chaser, and player colors used by rendering.
 */
public class UiColors {

    // ---------- CORE THEME COLORS ----------

    public static final Color BACKGROUND_DARK  = new Color(18, 18, 18);
    public static final Color BACKGROUND_PANEL = new Color(30, 30, 30);

    public static final Color TEXT_PRIMARY   = new Color(235, 235, 235);
    public static final Color TEXT_SECONDARY = new Color(180, 180, 180);
    public static final Color TEXT_DISABLED  = new Color(120, 120, 120);

    public static final Color ACCENT_PRIMARY = new Color(0, 200, 150);
    public static final Color ACCENT_HOVER   = new Color(0, 230, 180);
    public static final Color ACCENT_PRESSED = new Color(0, 160, 120);

    public static final Color BORDER_LIGHT   = new Color(80, 80, 80);
    public static final Color BORDER_STRONG  = new Color(120, 120, 120);

    // ---------- MAZE / GAME WORLD COLORS ----------

    public static final Color MAZE_FLOOR     = new Color(45, 45, 45);
    public static final Color MAZE_WALL      = new Color(30, 30, 30);
    public static final Color MAZE_GRID      = new Color(60, 60, 60);
    public static final Color MAZE_ENTRANCE  = new Color(126, 217, 87);
    public static final Color MAZE_EXIT      = new Color(220, 70, 70);
    public static final Color CHASER_DEFAULT = new Color(255, 49, 49);

    // ---------- PLAYER COLORS ----------

    public static final Color PLAYER_RED        = new Color(255, 49, 49);
    public static final Color PLAYER_ORANGE     = new Color(255, 117, 31);
    public static final Color PLAYER_YELLOW     = new Color(255, 210, 31);

    public static final Color PLAYER_DARK_GREEN = new Color(0, 132, 68);
    public static final Color PLAYER_GREEN      = new Color(76, 166, 38);
    public static final Color PLAYER_LIME       = new Color(75, 207, 28);

    public static final Color PLAYER_DARK_BLUE  = new Color(1, 69, 159);
    public static final Color PLAYER_TEAL       = new Color(0, 151, 178);
    public static final Color PLAYER_LIGHT_BLUE = new Color(153, 197, 255);

    public static final Color PLAYER_PURPLE     = new Color(140, 82, 255);
    public static final Color PLAYER_LAVENDER   = new Color(203, 108, 230);
    public static final Color PLAYER_PINK       = new Color(255, 102, 196);

    public static final Color PLAYER_BLACK        = new Color(0, 0, 0);
    public static final Color PLAYER_DARK_GRAY    = new Color(89, 88, 88);
    public static final Color PLAYER_DEFAULT_GRAY = new Color(217, 217, 217);
}
