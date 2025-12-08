package game.ui.components.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import game.settings.GameConfig;
import game.ui.theme.GameFonts;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * LootDisplayPanel - Loot HUD component used in two contexts:
 *
 * In MainMenuWindow (vault view):
 *   [vaultGold.png]    : 12345    [vaultDiamond.png] : 12345
 *
 * In GameWindow (in-run loot HUD):
 *   [gold.png]         : 12345    [diamond.png]      : 12345   [time.png] : 1:23
 *
 * All icons are located in /game/resources/images/.
 *
 * In MAIN_MENU mode, the amounts represent vault totals.
 * In GAME mode, the amounts represent in-run loot.
 *
 * NEW: second line shows multiplier + difficulty, e.g.
 *   "Multiplier: 4    Difficulty: Hard"
 * where 4 = (equipped multiplier) × (mode multiplier).
 */
public class LootDisplayPanel extends JPanel {

    // ---------- FIELDS ----------

    private static final long serialVersionUID = 1L;

    public enum Context {
        MAIN_MENU,   // vaultGold / vaultDiamond, no time
        GAME         // gold / diamond / time
    }

    private final Context context;

    private JLabel goldAmountLabel;     // vault gold in MAIN_MENU, run gold in GAME
    private JLabel diamondAmountLabel;  // vault diamonds in MAIN_MENU, run diamonds in GAME
    private JLabel timeLabel;           // only used in GAME
    private JLabel infoLabel;           // "Multiplier: X    Difficulty: Y"

    // ---------- CONSTRUCTORS ----------

    // LootDisplayPanel - Main-menu constructor (vault HUD)
    public LootDisplayPanel(int vaultGold, int vaultDiamonds) {
        this(Context.MAIN_MENU, vaultGold, vaultDiamonds, 0.0);
    }

    // LootDisplayPanel - Game HUD constructor (in-run loot + time)
    public LootDisplayPanel(int gold, int diamonds, double seconds) {
        this(Context.GAME, gold, diamonds, seconds);
    }

    // LootDisplayPanel - Internal constructor used by the public ones
    private LootDisplayPanel(Context context, int goldAmount, int diamondAmount, double seconds) {
        this.context = context;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        float fontSize = Math.max(18f, Math.min(screenHeight / 40f, 28f));

        String goldIconPath;
        String diamondIconPath;
        boolean showTime;

        if (context == Context.MAIN_MENU) {
            goldIconPath = "/game/resources/images/vaultGold.png";
            diamondIconPath = "/game/resources/images/vaultDiamond.png";
            showTime = false;
        } else {
            goldIconPath = "/game/resources/images/gold.png";
            diamondIconPath = "/game/resources/images/diamond.png";
            showTime = true;
        }

        setBorder(new EmptyBorder(10, 10, 10, 20));

        // ---------- FIRST ROW: icons + amounts ----------

        JPanel row1 = new JPanel();
        row1.setOpaque(false);
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));

        JLabel goldIconLabel = new JLabel(loadIcon(goldIconPath, 32));
        goldAmountLabel = new JLabel(": " + goldAmount);
        goldAmountLabel.setFont(GameFonts.get(fontSize, Font.BOLD));
        goldAmountLabel.setForeground(Color.WHITE);

        JLabel diamondIconLabel = new JLabel(loadIcon(diamondIconPath, 32));
        diamondAmountLabel = new JLabel(": " + diamondAmount);
        diamondAmountLabel.setFont(GameFonts.get(fontSize, Font.BOLD));
        diamondAmountLabel.setForeground(Color.WHITE);

        row1.add(goldIconLabel);
        row1.add(Box.createRigidArea(new Dimension(6, 0)));
        row1.add(goldAmountLabel);

        row1.add(Box.createRigidArea(new Dimension(18, 0)));

        row1.add(diamondIconLabel);
        row1.add(Box.createRigidArea(new Dimension(6, 0)));
        row1.add(diamondAmountLabel);

        if (showTime) {
            JLabel timeIconLabel = new JLabel(loadIcon("/game/resources/images/time.png", 32));
            timeLabel = new JLabel(formatTime(seconds));
            timeLabel.setFont(GameFonts.get(fontSize, Font.BOLD));
            timeLabel.setForeground(Color.WHITE);

            row1.add(Box.createRigidArea(new Dimension(18, 0)));
            row1.add(timeIconLabel);
            row1.add(Box.createRigidArea(new Dimension(6, 0)));
            row1.add(timeLabel);
        }

        row1.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(row1);

        // ---------- SECOND ROW: "Multiplier / Difficulty" ----------

        add(Box.createRigidArea(new Dimension(0, 2)));

        infoLabel = new JLabel("Multiplier: -    Difficulty: -");
        float infoFontSize = Math.max(14f, fontSize * 0.8f);
        infoLabel.setFont(GameFonts.get(infoFontSize, Font.PLAIN));
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(infoLabel);
    }

    // ---------- ICON HELPERS ----------

    // loadIcon - Loads and scales an icon from resources
    private ImageIcon loadIcon(String path, int size) {
        java.net.URL url = LootDisplayPanel.class.getResource(path);
        if (url == null) {
            return new ImageIcon(new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB));
        }
        ImageIcon icon = new ImageIcon(url);
        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // ---------- UPDATE METHODS ----------

    // setGoldAmount - Updates displayed gold (vault or run)
    public void setGoldAmount(int amount) {
        if (goldAmountLabel != null) {
            goldAmountLabel.setText(": " + amount);
        }
    }

    // setDiamondAmount - Updates displayed diamonds (vault or run)
    public void setDiamondAmount(int amount) {
        if (diamondAmountLabel != null) {
            diamondAmountLabel.setText(": " + amount);
        }
    }

    // setAmounts - Updates both gold and diamond amounts
    public void setAmounts(int goldAmount, int diamondAmount) {
        setGoldAmount(goldAmount);
        setDiamondAmount(diamondAmount);
    }

    // setTime - Updates the time label from seconds (GAME context only)
    public void setTime(double seconds) {
        if (timeLabel != null && context == Context.GAME) {
            timeLabel.setText(formatTime(seconds));
        }
    }

    // formatTime - Formats seconds as "Time: mm:ss"
    private String formatTime(double seconds) {
        int total = (int) Math.floor(seconds);
        int mins = total / 60;
        int secs = total % 60;
        return String.format("Time: %02d:%02d", mins, secs);
    }

    // updateLoot - MAIN_MENU helper to refresh vault values
    public void updateLoot(int vaultGold, int vaultDiamonds) {
        setGoldAmount(vaultGold);
        setDiamondAmount(vaultDiamonds);
    }

    // setMultiplierAndDifficulty - Updates second line with multiplier + difficulty
    public void setMultiplierAndDifficulty(double multiplierValue, String difficultyName) {
        if (infoLabel == null) return;

        if (difficultyName == null || difficultyName.trim().isEmpty()) {
            difficultyName = "-";
        }

        double modeMult = GameConfig.MULTIPLIER_NORMAL;
        String lower = difficultyName.toLowerCase();
        if (lower.contains("hard")) {
            modeMult = GameConfig.MULTIPLIER_HARD;
        }

        double total = multiplierValue * modeMult;

        int asInt = (int) Math.round(total);
        String multStr = (Math.abs(total - asInt) < 1e-6)
                ? String.valueOf(asInt)
                : String.valueOf(total);

        infoLabel.setText("Multiplier: " + multStr + "    Difficulty: " + difficultyName);
    }
}
