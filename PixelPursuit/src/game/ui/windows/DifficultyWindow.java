package game.ui.windows;

import game.settings.Difficulty;
import game.settings.GameConfig;
import game.ui.WindowManager;
import game.ui.components.controls.RoundedHoverButton;
import game.ui.theme.GameFonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Difficulty/settings window:
 *  - Uses the dark run-summary theme with centered text and buttons.
 *  - Lets the player choose a difficulty (affects gold payout + game pacing).
 *  - Updates GameConfig and optionally refreshes main-menu loot display.
 */
public class DifficultyWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    // ---------- CONSTANTS ----------

    private static final Color BG_COLOR = new Color(30, 30, 30);
    private static final Dimension DIFF_BUTTON_SIZE = new Dimension(260, 48);
    private static final Dimension BACK_BUTTON_SIZE = new Dimension(200, 48);

    // ---------- CONSTRUCTORS ----------

    // DifficultyWindow - Builds the difficulty selection UI and wires callbacks
    public DifficultyWindow(WindowManager windowManager) {
        super("Settings");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel content = createDarkContentPanel();
        setContentPane(content);

        Difficulty[] options = Difficulty.values();

        // ----- Title area -----
        JLabel title    = createLabel("Settings",           28f, Font.BOLD,  Color.WHITE);
        JLabel subtitle = createLabel("Select difficulty",  18f, Font.PLAIN, Color.WHITE);
        JLabel hint     = createLabel("Harder runs pay out more final gold.",
                                      14f, Font.PLAIN, Color.WHITE);

        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 4)));
        content.add(hint);
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        // ----- Difficulty buttons -----
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JLabel statusLabel = createLabel(" ", 14f, Font.PLAIN, new Color(150, 255, 150));

        for (Difficulty d : options) {
            RoundedHoverButton diffButton = new RoundedHoverButton(d.getDisplayName());
            diffButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            diffButton.setFont(GameFonts.get(18f, Font.BOLD));
            diffButton.setMaximumSize(DIFF_BUTTON_SIZE);

            diffButton.addActionListener(e -> {
                GameConfig.setCurrentDifficulty(d);
                statusLabel.setText("Difficulty set to " + d.getDisplayName());
                if (windowManager != null) {
                    windowManager.refreshMainMenuLoot();
                }
            });

            buttonsPanel.add(diffButton);
            buttonsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        content.add(buttonsPanel);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(statusLabel);
        content.add(Box.createRigidArea(new Dimension(0, 18)));

        // ----- Back button -----
        RoundedHoverButton backButton = new RoundedHoverButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(GameFonts.get(18f, Font.BOLD));
        backButton.setMaximumSize(BACK_BUTTON_SIZE);
        backButton.addActionListener(e -> dispose());

        content.add(backButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ---------- SMALL HELPERS ----------

    // createDarkContentPanel - Root dark panel with padding and vertical layout
    private JPanel createDarkContentPanel() {
        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(20, 40, 24, 40));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(true);
        content.setBackground(BG_COLOR);
        return content;
    }

    // createLabel - Convenience factory for centered labels with font + color
    private JLabel createLabel(String text, float size, int style, Color color) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(GameFonts.get(size, style));
        label.setForeground(color);
        return label;
    }
}
