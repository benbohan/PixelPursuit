package game.ui.theme;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

public class GameFonts {

    private static Font baseFont;

    static {
        try {
            // Load the TTF from resources
            InputStream is = GameFonts.class.getResourceAsStream("/game/resources/font/PressStart2P-Regular.ttf");
            if (is == null) {
                throw new RuntimeException("PressStart2P-Regular.ttf not found in /game/resources/");
            }

            baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(baseFont);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback so the game still runs
            baseFont = new Font("SansSerif", Font.PLAIN, 20);
        }
    }

    public static Font get(float size) {
        return baseFont.deriveFont(size);
    }

    public static Font get(float size, int style) {
        return baseFont.deriveFont(style, size);
    }
}
