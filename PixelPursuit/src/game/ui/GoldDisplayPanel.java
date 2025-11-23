package game.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * HUD component showing vault total, current run gold, and time:
 *
 * [vault icon] : 12345    [gold icon] : 250    Time: 01:23
 *
 * vault.png and gold.png should live in /game/resources/.
 */
public class GoldDisplayPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel vaultTextLabel;
    private JLabel goldTextLabel;
    private JLabel timeLabel;

    public GoldDisplayPanel(int vaultAmount, int goldAmount) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
        float fontSize = Math.max(18f, Math.min(screenHeight / 40f, 28f));

        // --- Vault icon + text ---
        JLabel vaultIconLabel = new JLabel(loadIcon("/game/resources/vault.png", 32));
        vaultTextLabel = new JLabel(": " + vaultAmount);
        vaultTextLabel.setFont(GameFonts.get(fontSize, Font.BOLD));
        vaultTextLabel.setForeground(Color.WHITE);

        // --- Gold icon + text ---
        JLabel goldIconLabel = new JLabel(loadIcon("/game/resources/gold.png", 32));
        goldTextLabel = new JLabel(": " + goldAmount);
        goldTextLabel.setFont(GameFonts.get(fontSize, Font.BOLD));
        goldTextLabel.setForeground(Color.WHITE);

        // --- Time label ---
        timeLabel = new JLabel("Time: 00:00");
        timeLabel.setFont(GameFonts.get(fontSize, Font.BOLD));
        timeLabel.setForeground(Color.WHITE);

        // Slight padding so it's not glued to the edges
        setBorder(new EmptyBorder(10, 10, 10, 20));

        // Layout: [vault icon] : ###    gap    [gold icon] : ###    gap    Time: mm:ss
        add(vaultIconLabel);
        add(Box.createRigidArea(new Dimension(6, 0)));
        add(vaultTextLabel);

        add(Box.createRigidArea(new Dimension(18, 0))); // gap between vault and gold

        add(goldIconLabel);
        add(Box.createRigidArea(new Dimension(6, 0)));
        add(goldTextLabel);

        add(Box.createRigidArea(new Dimension(18, 0))); // gap before time

        add(timeLabel);
    }

    /** Helper to load and scale an icon from resources. */
    private ImageIcon loadIcon(String path, int size) {
        java.net.URL url = GoldDisplayPanel.class.getResource(path);
        if (url == null) {
            // Fallback: empty icon if not found
            return new ImageIcon(new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB));
        }
        ImageIcon icon = new ImageIcon(url);
        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /** Update vault amount (banked gold). */
    public void setVaultAmount(int vaultAmount) {
        vaultTextLabel.setText(": " + vaultAmount);
    }

    /** Update current run gold (at-risk gold). */
    public void setGoldAmount(int goldAmount) {
        goldTextLabel.setText(": " + goldAmount);
    }

    /** Convenience if you want to update both at once. */
    public void setAmounts(int vaultAmount, int goldAmount) {
        setVaultAmount(vaultAmount);
        setGoldAmount(goldAmount);
    }

    /** Update the time label from seconds. */
    public void setTime(double seconds) {
        int total = (int) Math.floor(seconds);
        int mins = total / 60;
        int secs = total % 60;
        timeLabel.setText(String.format("Time: %02d:%02d", mins, secs));
    }
}
