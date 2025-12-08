package game.ui.theme;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/**
 * GameFonts - Loads and provides the shared pixel font for the UI.
 *  - Attempts to load PressStart2P from resources at startup.
 *  - Falls back to a standard SansSerif font if loading fails.
 */
public class GameFonts {

    // ---------- FONTS ----------

    private static Font baseFont;

    static {
        try {
            InputStream is = GameFonts.class.getResourceAsStream(
                    "/game/resources/font/PressStart2P-Regular.ttf");
            if (is == null) {
                throw new RuntimeException(
                        "PressStart2P-Regular.ttf not found in /game/resources/");
            }

            baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .registerFont(baseFont);
        } catch (Exception e) {
            e.printStackTrace();
            baseFont = new Font("SansSerif", Font.PLAIN, 20);
        }
    }

    // get - Returns the base font at the given size
    public static Font get(float size) {
        return baseFont.deriveFont(size);
    }

    // get - Returns the base font with the given style and size
    public static Font get(float size, int style) {
        return baseFont.deriveFont(style, size);
    }
}
