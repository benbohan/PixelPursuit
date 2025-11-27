package game.ui.windows;

import game.account.Account;
import game.cosmetics.PlayerCosmetics;
import game.ui.theme.GameFonts;
import game.ui.theme.UiColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Small window to customize the runner's color.
 */
public class CustomizeWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Account account;

    public CustomizeWindow(Account account) {
        super("Customize - Runner Color");
        this.account = account;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // --- Main panel ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
        mainPanel.setOpaque(true);
        mainPanel.setBackground(new Color(30, 30, 30));  // dark cave-like

        JLabel titleLabel = new JLabel("Customize Runner");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(GameFonts.get(24f, Font.BOLD));

        JLabel subtitle = new JLabel("Choose your runner color:");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setFont(GameFonts.get(18f, Font.PLAIN));

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // --- Row of color buttons ---
        JPanel colorRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        colorRow.setOpaque(false);

        // Map buttons to your PlayerCosmetics color IDs + UiColors palette
        addColorButton(colorRow, PlayerCosmetics.COLOR_RED,        UiColors.PLAYER_RED);
        addColorButton(colorRow, PlayerCosmetics.COLOR_ORANGE,     UiColors.PLAYER_ORANGE);
        addColorButton(colorRow, PlayerCosmetics.COLOR_YELLOW,     UiColors.PLAYER_YELLOW);
        addColorButton(colorRow, PlayerCosmetics.COLOR_DARK_GREEN, UiColors.PLAYER_DARK_GREEN);
        addColorButton(colorRow, PlayerCosmetics.COLOR_DARK_BLUE,  UiColors.PLAYER_DARK_BLUE);
        addColorButton(colorRow, PlayerCosmetics.COLOR_PURPLE,     UiColors.PLAYER_PURPLE);

        mainPanel.add(colorRow);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setFocusPainted(false);
        closeButton.setFont(GameFonts.get(18f, Font.BOLD));
        closeButton.addActionListener(e -> dispose());

        mainPanel.add(closeButton);

        setContentPane(mainPanel);
        pack();
        setSize(Math.max(getWidth(), 420), Math.max(getHeight(), 260));
        setLocationRelativeTo(null); // center on screen
        setVisible(true);
    }

    /** Helper to create a square color button that equips a runner color. */
    private void addColorButton(JPanel parent, int colorId, Color color) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(48, 48));
        btn.setMinimumSize(new Dimension(48, 48));
        btn.setMaximumSize(new Dimension(48, 48));

        btn.setBackground(color);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        btn.setFocusPainted(false);

        btn.addActionListener(e -> {
            if (account != null) {
                // Unlock this color (if not already) and equip it
                PlayerCosmetics.unlockColor(account, colorId);
                PlayerCosmetics.equipColor(account, colorId);
            }
            Toolkit.getDefaultToolkit().beep();
        });

        parent.add(btn);
    }
}
