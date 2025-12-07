package game.ui.components.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import game.ui.theme.GameFonts;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Loot HUD component used in two contexts:
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
 */
public class LootDisplayPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public enum Context {
        MAIN_MENU,   // vaultGold / vaultDiamond, no time
        GAME         // gold / diamond / time
    }

    private final Context context;

    private JLabel goldAmountLabel;     // vault gold in MAIN_MENU, run gold in GAME
    private JLabel diamondAmountLabel;  // vault diamonds in MAIN_MENU, run diamonds in GAME
    private JLabel timeLabel;           // only used in GAME

    /**
     * Main-menu constructor (vault HUD).
     * Shows vault gold + vault diamonds.
     */
    public LootDisplayPanel(int vaultGold, int vaultDiamonds) {
        this(Context.MAIN_MENU, vaultGold, vaultDiamonds, 0.0);
    }

    /**
     * Game HUD constructor (in-run loot + time).
     * Shows gold + diamonds + time.
     */
    public LootDisplayPanel(int gold, int diamonds, double seconds) {
        this(Context.GAME, gold, diamonds, seconds);
    }

    /**
     * Internal constructor used by the public ones.
     */
    private LootDisplayPanel(Context context, int goldAmount, int diamondAmount, double seconds) {
        this.context = context;

        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        float fontSize = Math.max(18f, Math.min(screenHeight / 40f, 28f));

        // Decide which icons to use based on context
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

        // --- Gold icon + text ---
        JLabel goldIconLabel = new JLabel(loadIcon(goldIconPath, 32));
        goldAmountLabel = new JLabel(": " + goldAmount);
        goldAmountLabel.setFont(GameFonts.get(fontSize, Font.BOLD));
        goldAmountLabel.setForeground(Color.WHITE);

        // --- Diamond icon + text ---
        JLabel diamondIconLabel = new JLabel(loadIcon(diamondIconPath, 32));
        diamondAmountLabel = new JLabel(": " + diamondAmount);
        diamondAmountLabel.setFont(GameFonts.get(fontSize, Font.BOLD));
        diamondAmountLabel.setForeground(Color.WHITE);

        // --- Time label (GAME only) ---
        if (showTime) {
            JLabel timeIconLabel = new JLabel(loadIcon("/game/resources/images/time.png", 32));
            timeLabel = new JLabel(formatTime(seconds));
            timeLabel.setFont(GameFonts.get(fontSize, Font.BOLD));
            timeLabel.setForeground(Color.WHITE);

            // Slight padding so it's not glued to the edges
            setBorder(new EmptyBorder(10, 10, 10, 20));

            // Layout: [gold icon] : ###   gap   [diamond icon] : ###   gap   [time icon] mm:ss
            add(goldIconLabel);
            add(Box.createRigidArea(new Dimension(6, 0)));
            add(goldAmountLabel);

            add(Box.createRigidArea(new Dimension(18, 0))); // gap between gold and diamond

            add(diamondIconLabel);
            add(Box.createRigidArea(new Dimension(6, 0)));
            add(diamondAmountLabel);

            add(Box.createRigidArea(new Dimension(18, 0))); // gap before time

            add(timeIconLabel);
            add(Box.createRigidArea(new Dimension(6, 0)));
            add(timeLabel);
        } else {
            // MAIN_MENU: just vault gold + vault diamonds, no time
            setBorder(new EmptyBorder(10, 10, 10, 20));

            // Layout: [vaultGold icon] : ###   gap   [vaultDiamond icon] : ### 
            add(goldIconLabel);
            add(Box.createRigidArea(new Dimension(6, 0)));
            add(goldAmountLabel);

            add(Box.createRigidArea(new Dimension(18, 0))); // gap between gold and diamond

            add(diamondIconLabel);
            add(Box.createRigidArea(new Dimension(6, 0)));
            add(diamondAmountLabel);
        }
    }

    /** Helper to load and scale an icon from resources. */
    private ImageIcon loadIcon(String path, int size) {
        java.net.URL url = LootDisplayPanel.class.getResource(path);
        if (url == null) {
            // Fallback: empty icon if not found
            return new ImageIcon(new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB));
        }
        ImageIcon icon = new ImageIcon(url);
        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // --------- Update Methods ---------

    /** Update gold amount (vault gold in MAIN_MENU, run gold in GAME). */
    public void setGoldAmount(int amount) {
        if (goldAmountLabel != null) {
            goldAmountLabel.setText(": " + amount);
        }
    }

    /** Update diamond amount (vault diamonds in MAIN_MENU, run diamonds in GAME). */
    public void setDiamondAmount(int amount) {
        if (diamondAmountLabel != null) {
            diamondAmountLabel.setText(": " + amount);
        }
    }

    /** Convenience: update both gold and diamond amounts at once. */
    public void setAmounts(int goldAmount, int diamondAmount) {
        setGoldAmount(goldAmount);
        setDiamondAmount(diamondAmount);
    }

    /** Update the time label from seconds (GAME context only). */
    public void setTime(double seconds) {
        if (timeLabel != null && context == Context.GAME) {
            timeLabel.setText(formatTime(seconds));
        }
    }

    /** Formats seconds as "Time: mm:ss". */
    private String formatTime(double seconds) {
        int total = (int) Math.floor(seconds);
        int mins = total / 60;
        int secs = total % 60;
        return String.format("Time: %02d:%02d", mins, secs);
    }
    
    // NEW: call this when the account changes (MAIN_MENU vault values)
    public void updateLoot(int vaultGold, int vaultDiamonds) {
        setGoldAmount(vaultGold);
        setDiamondAmount(vaultDiamonds);
    }
}
