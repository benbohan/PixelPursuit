package game.ui.windows;

import game.account.Account;
import game.cosmetics.PlayerCosmetics;
import game.ui.WindowManager;
import game.ui.components.controls.RoundedHoverButton;
import game.ui.theme.GameFonts;
import game.ui.theme.UiColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Large customization window with colors, cosmetics, and multipliers.
 */
public class CustomizeWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final int CARD_ARC = 32;
    private static final int COLOR_BUTTON_SIZE = 70;
    private static final int TILE_SIZE = 72;

    private JLabel colorSelectedLabel;
    private JLabel cosmeticSelectedLabel;
    private JLabel multiplierSelectedLabel;

    private final Account account;
    private final WindowManager windowManager;

    private final JButton[] colorButtons      = new JButton[15]; // color IDs 0–14
    private final JButton[] cosmeticButtons   = new JButton[12];
    private final JButton[] multiplierButtons = new JButton[4];

    public CustomizeWindow(WindowManager windowManager, Account account) {
        super("Customize");
        this.account = account;
        this.windowManager = windowManager;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // ----- Window size -----
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width  = (int) (screen.width * 0.8);
        int height = (int) (screen.height * 0.75);
        setSize(width, height);
        setLocationRelativeTo(null);

        // ----- Root background -----
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(new Color(0, 0, 0)); // dark overlay
        setContentPane(root);

        // ----- Inner rounded "card" panel -----
        RoundedCardPanel card = new RoundedCardPanel(CARD_ARC);
        card.setBackground(new Color(245, 245, 245));
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 32, 20, 32));

        int cardWidth  = (int) (width  * 0.7);
        int cardHeight = (int) (height * 0.75);
        Dimension cardSize = new Dimension(cardWidth, cardHeight);
        card.setPreferredSize(cardSize);
        card.setMinimumSize(cardSize);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx   = 0;
        gbc.gridy   = 0;
        gbc.fill    = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.anchor  = GridBagConstraints.CENTER;
        root.add(card, gbc);

        // ===== LEFT: COLORS =====
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel colorsLabel = new JLabel("COLORS");
        colorsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorsLabel.setFont(GameFonts.get(32f, Font.BOLD));
        colorsLabel.setForeground(Color.BLACK);

        colorSelectedLabel = new JLabel("Selected: -");
        colorSelectedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorSelectedLabel.setFont(GameFonts.get(16f, Font.PLAIN));
        colorSelectedLabel.setForeground(Color.DARK_GRAY);

        JPanel colorsGrid = new JPanel(new GridLayout(5, 3, 16, 16));
        colorsGrid.setOpaque(false);
        colorsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        colorsGrid.setBorder(new EmptyBorder(8, 0, 0, 0));

        // Add 5x3 color buttons using your IDs + UiColors
        addColorButton(colorsGrid, PlayerCosmetics.COLOR_RED,          UiColors.PLAYER_RED);
        addColorButton(colorsGrid, PlayerCosmetics.COLOR_ORANGE,       UiColors.PLAYER_ORANGE);
        addColorButton(colorsGrid, PlayerCosmetics.COLOR_YELLOW,       UiColors.PLAYER_YELLOW);

        addColorButton(colorsGrid, PlayerCosmetics.COLOR_DARK_GREEN,   UiColors.PLAYER_DARK_GREEN);
        addColorButton(colorsGrid, PlayerCosmetics.COLOR_GREEN,        UiColors.PLAYER_GREEN);
        addColorButton(colorsGrid, PlayerCosmetics.COLOR_LIME,         UiColors.PLAYER_LIME);

        addColorButton(colorsGrid, PlayerCosmetics.COLOR_DARK_BLUE,    UiColors.PLAYER_DARK_BLUE);
        addColorButton(colorsGrid, PlayerCosmetics.COLOR_TEAL,         UiColors.PLAYER_TEAL);
        addColorButton(colorsGrid, PlayerCosmetics.COLOR_LIGHT_BLUE,   UiColors.PLAYER_LIGHT_BLUE);

        addColorButton(colorsGrid, PlayerCosmetics.COLOR_PURPLE,       UiColors.PLAYER_PURPLE);
        addColorButton(colorsGrid, PlayerCosmetics.COLOR_LAVENDER,     UiColors.PLAYER_LAVENDER);
        addColorButton(colorsGrid, PlayerCosmetics.COLOR_PINK,         UiColors.PLAYER_PINK);

        addColorButton(colorsGrid, PlayerCosmetics.COLOR_BLACK,        UiColors.PLAYER_BLACK);
        addColorButton(colorsGrid, PlayerCosmetics.COLOR_DARK_GRAY,    UiColors.PLAYER_DARK_GRAY);
        addColorButton(colorsGrid, PlayerCosmetics.COLOR_DEFAULT_GRAY, UiColors.PLAYER_DEFAULT_GRAY);

        leftPanel.add(colorsLabel);
        leftPanel.add(colorSelectedLabel);
        leftPanel.add(colorsGrid);

        // ===== RIGHT: COSMETICS + MULTIPLIERS =====
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        // Cosmetics title
        JLabel cosmeticsLabel = new JLabel("COSMETICS");
        cosmeticsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cosmeticsLabel.setFont(GameFonts.get(32f, Font.BOLD));
        cosmeticsLabel.setForeground(Color.BLACK);

        cosmeticSelectedLabel = new JLabel("Selected: -");
        cosmeticSelectedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cosmeticSelectedLabel.setFont(GameFonts.get(16f, Font.PLAIN));
        cosmeticSelectedLabel.setForeground(Color.DARK_GRAY);

        JPanel cosmeticsGrid = new JPanel(new GridLayout(3, 4, 16, 16));
        cosmeticsGrid.setOpaque(false);
        cosmeticsGrid.setBorder(new EmptyBorder(8, 0, 0, 0));
        cosmeticsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 3x4 cosmetic tiles
        for (int i = 0; i < 12; i++) {
            JButton tile = createCosmeticTile(i);
            cosmeticsGrid.add(tile);
        }

        // Multipliers title
        JLabel multipliersLabel = new JLabel("MULTIPLIERS");
        multipliersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        multipliersLabel.setFont(GameFonts.get(32f, Font.BOLD));
        multipliersLabel.setForeground(Color.BLACK);

        multiplierSelectedLabel = new JLabel("Selected: -");
        multiplierSelectedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        multiplierSelectedLabel.setFont(GameFonts.get(16f, Font.PLAIN));
        multiplierSelectedLabel.setForeground(Color.DARK_GRAY);

        JPanel multipliersRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 12));
        multipliersRow.setOpaque(false);
        multipliersRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] multLabels = { "2x", "3x", "5x", "10x" };
        for (int i = 0; i < multLabels.length; i++) {
            JButton tile = createMultiplierTile(multLabels[i], i);
            multipliersRow.add(tile);
        }

        rightPanel.add(cosmeticsLabel);
        rightPanel.add(cosmeticSelectedLabel);
        rightPanel.add(cosmeticsGrid);
        rightPanel.add(Box.createVerticalStrut(12));
        rightPanel.add(multipliersLabel);
        rightPanel.add(multiplierSelectedLabel);
        rightPanel.add(multipliersRow);

        // ----- Place left & right into card -----
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints cgbc = new GridBagConstraints();
        cgbc.gridy   = 0;
        cgbc.fill    = GridBagConstraints.NONE;
        cgbc.weighty = 0.0;
        cgbc.anchor  = GridBagConstraints.NORTH;

        cgbc.gridx   = 0;
        cgbc.weightx = 0.0;
        center.add(leftPanel, cgbc);

        cgbc.gridx = 1;
        center.add(Box.createHorizontalStrut(40)); // spacer

        cgbc.gridx = 2;
        center.add(rightPanel, cgbc);

        card.add(center, BorderLayout.CENTER);

        // ----- Bottom full-width Back button -----
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
        bottom.setBorder(new EmptyBorder(16, 0, 0, 0));

        RoundedHoverButton backButton = new RoundedHoverButton("Back");
        int backHeight = 60;
        Dimension backSize = new Dimension(320, backHeight);
        backButton.setPreferredSize(backSize);
        backButton.setMinimumSize(backSize);
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, backHeight));
        backButton.setFont(GameFonts.get(22f, Font.BOLD));

        bottom.add(Box.createHorizontalGlue());
        bottom.add(backButton);
        bottom.add(Box.createHorizontalGlue());

        card.add(bottom, BorderLayout.SOUTH);

        backButton.addActionListener(e -> dispose()); // close customize, main menu stays

        // highlight currently equipped color (if any)
        if (account != null) {
            int equipped = account.getColor();
            if (equipped >= 0 && equipped < colorButtons.length && colorButtons[equipped] != null) {
                setSelectedColorBorder(equipped);
                colorSelectedLabel.setText("Selected: " + getColorName(equipped));
            }
        }

        setVisible(true);
    }

    // Fallback constructor for testing without WindowManager
    public CustomizeWindow(Account account) {
        this(null, account);
    }

    // ---------- helpers ----------

    private void addColorButton(JPanel grid, int colorId, Color color) {
        ColorTileButton btn = new ColorTileButton(color);
        Dimension d = new Dimension(COLOR_BUTTON_SIZE, COLOR_BUTTON_SIZE);
        btn.setPreferredSize(d);
        btn.setMinimumSize(d);
        btn.setMaximumSize(d);

        if (colorId >= 0 && colorId < colorButtons.length) {
            colorButtons[colorId] = btn;
        }

        btn.addActionListener(e -> {
            if (account != null) {
                PlayerCosmetics.unlockColor(account, colorId);
                PlayerCosmetics.equipColor(account, colorId);
                setSelectedColorBorder(colorId);
            }
            if (colorSelectedLabel != null) {
                colorSelectedLabel.setText("Selected: " + getColorName(colorId));
            }
            Toolkit.getDefaultToolkit().beep();
        });

        grid.add(btn);
    }

    private String getColorName(int id) {
        switch (id) {
            case PlayerCosmetics.COLOR_RED:          return "Red";
            case PlayerCosmetics.COLOR_ORANGE:       return "Orange";
            case PlayerCosmetics.COLOR_YELLOW:       return "Yellow";
            case PlayerCosmetics.COLOR_DARK_GREEN:   return "Dark Green";
            case PlayerCosmetics.COLOR_GREEN:        return "Green";
            case PlayerCosmetics.COLOR_LIME:         return "Lime";
            case PlayerCosmetics.COLOR_DARK_BLUE:    return "Dark Blue";
            case PlayerCosmetics.COLOR_TEAL:         return "Teal";
            case PlayerCosmetics.COLOR_LIGHT_BLUE:   return "Light Blue";
            case PlayerCosmetics.COLOR_PURPLE:       return "Purple";
            case PlayerCosmetics.COLOR_LAVENDER:     return "Lavender";
            case PlayerCosmetics.COLOR_PINK:         return "Pink";
            case PlayerCosmetics.COLOR_BLACK:        return "Black";
            case PlayerCosmetics.COLOR_DARK_GRAY:    return "Dark Gray";
            case PlayerCosmetics.COLOR_DEFAULT_GRAY: return "Default";
            default: return "Unknown";
        }
    }

    private void setSelectedColorBorder(int colorId) {
        for (int i = 0; i < colorButtons.length; i++) {
            if (colorButtons[i] instanceof ColorTileButton) {
                ((ColorTileButton) colorButtons[i]).setSelected(i == colorId);
            }
        }
    }

    private JButton createCosmeticTile(int index) {
        GrayTileButton btn = new GrayTileButton("");
        Dimension d = new Dimension(TILE_SIZE, TILE_SIZE);
        btn.setPreferredSize(d);
        btn.setMinimumSize(d);
        btn.setMaximumSize(d);

        cosmeticButtons[index] = btn;

        btn.addActionListener(e -> {
            setSelectedCosmeticBorder(index);
            if (cosmeticSelectedLabel != null) {
                cosmeticSelectedLabel.setText("Selected: Cosmetic " + (index + 1));
            }
            // TODO: hook into PlayerCosmetics.equipCosmetic(account, index) if you add it
            Toolkit.getDefaultToolkit().beep();
        });

        return btn;
    }

    private void setSelectedCosmeticBorder(int index) {
        for (int i = 0; i < cosmeticButtons.length; i++) {
            if (cosmeticButtons[i] instanceof GrayTileButton) {
                ((GrayTileButton) cosmeticButtons[i]).setSelected(i == index);
            }
        }
    }

    private JButton createMultiplierTile(String text, int index) {
        GrayTileButton btn = new GrayTileButton(text);
        Dimension d = new Dimension(TILE_SIZE, TILE_SIZE);
        btn.setPreferredSize(d);
        btn.setMinimumSize(d);
        btn.setMaximumSize(d);

        multiplierButtons[index] = btn;

        btn.addActionListener(e -> {
            setSelectedMultiplierBorder(index);
            if (multiplierSelectedLabel != null) {
                multiplierSelectedLabel.setText("Selected: " + text);
            }
            // TODO: hook into PlayerCosmetics.equipMultiplier(account, index) if you add it
            Toolkit.getDefaultToolkit().beep();
        });

        return btn;
    }

    private void setSelectedMultiplierBorder(int index) {
        for (int i = 0; i < multiplierButtons.length; i++) {
            if (multiplierButtons[i] instanceof GrayTileButton) {
                ((GrayTileButton) multiplierButtons[i]).setSelected(i == index);
            }
        }
    }

    // ---------- inner UI classes ----------

    /** White/gray rounded card, like the big panel in the mockup. */
    private static class RoundedCardPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final int arc;

        RoundedCardPanel(int arc) {
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            Shape round = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), arc, arc);
            g2.setColor(getBackground());
            g2.fill(round);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Rounded square color tile, with thicker border when selected. */
    private static class ColorTileButton extends JButton {
        private static final long serialVersionUID = 1L;
        private boolean selected = false;
        private final Color fill;

        ColorTileButton(Color fill) {
            this.fill = fill;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 26;
            int w = getWidth();
            int h = getHeight();

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

            g2.setStroke(new BasicStroke(selected ? 4f : 2.5f));
            g2.setColor(selected ? Color.WHITE : new Color(230, 230, 230));
            g2.drawRoundRect(2, 2, w - 5, h - 5, arc, arc);

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) { /* no-op */ }
    }

    /** Gray rounded tile used for cosmetics and multipliers. */
    private static class GrayTileButton extends JButton {
        private static final long serialVersionUID = 1L;
        private boolean selected = false;

        GrayTileButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setForeground(new Color(255, 255, 255, text.isEmpty() ? 0 : 220));
            setFont(GameFonts.get(18f, Font.BOLD));
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 26;
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(210, 210, 210));
            g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

            g2.setColor(selected ? Color.WHITE : new Color(220, 220, 220));
            g2.setStroke(new BasicStroke(selected ? 3f : 2f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);

            // draw text centered
            String text = getText();
            if (text != null && !text.isEmpty()) {
                FontMetrics fm = g2.getFontMetrics(getFont());
                int tw = fm.stringWidth(text);
                int th = fm.getAscent();
                int tx = (w - tw) / 2;
                int ty = (h + th) / 2 - fm.getDescent();
                g2.setColor(getForeground());
                g2.drawString(text, tx, ty);
            }

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) { /* no-op */ }
    }
}
