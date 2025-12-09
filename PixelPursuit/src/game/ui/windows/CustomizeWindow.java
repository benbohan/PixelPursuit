package game.ui.windows;

import game.account.Account;
import game.cosmetics.PlayerCosmetics;
import game.cosmetics.CosmeticInfo;
import game.cosmetics.ColorInfo;
import game.cosmetics.MultiplierInfo;
import game.ui.WindowManager;
import game.ui.components.controls.RoundedHoverButton;
import game.ui.components.controls.ColorTileButton;
import game.ui.components.controls.TileButton;
import game.ui.theme.GameFonts;
import game.ui.theme.UiColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * CustomizeWindow:
 *  - Dark-theme window for choosing runner color, cosmetic, and multiplier.
 *  - Handles unlock flow using gold/diamonds and updates the Account via WindowManager.
 */
public class CustomizeWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final int COLOR_BUTTON_WIDTH  = 70;
    private static final int COLOR_BUTTON_HEIGHT = 70;
    private static final int TILE_SIZE           = 72;

    private JLabel colorSelectedLabel;
    private JLabel cosmeticSelectedLabel;
    private JLabel multiplierSelectedLabel;

    private final Account account;
    private final WindowManager windowManager;

    private final JButton[] colorButtons      = new JButton[15];
    private final JButton[] cosmeticButtons   = new JButton[CosmeticInfo.ALL.length];
    private final JButton[] multiplierButtons = new JButton[MultiplierInfo.ALL.length];

    // CustomizeWindow - Builds the customization window for the given account
    public CustomizeWindow(WindowManager windowManager, Account account) {
        super("Customize");
        this.account = account;
        this.windowManager = windowManager;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        Color bgColor = new Color(30, 30, 30);

        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(20, 40, 24, 40));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(true);
        content.setBackground(bgColor);
        
        JScrollPane scrollPane = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(null);
        scrollPane.setOpaque(true);
        scrollPane.setBackground(bgColor);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(bgColor);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smoother wheel scrolling

        setContentPane(scrollPane);

        JLabel titleLabel = new JLabel("Customize Runner");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(GameFonts.get(28f, Font.BOLD));
        titleLabel.setForeground(Color.WHITE);

        content.add(titleLabel);
        content.add(Box.createRigidArea(new Dimension(0, 12)));

        // ---------- COLORS SECTION ----------

        JLabel colorsLabel = sectionLabel("Colors");
        colorSelectedLabel = detailLabel("Selected: -");

        JPanel colorsGrid = new JPanel(new GridLayout(5, 3, 12, 12));
        colorsGrid.setOpaque(false);
        colorsGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        colorsGrid.setBorder(new EmptyBorder(0, 32, 0, 32));

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

        content.add(colorsLabel);
        content.add(colorSelectedLabel);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(colorsGrid);
        content.add(Box.createRigidArea(new Dimension(0, 18)));
        content.add(separatorLabel());

        // ---------- COSMETICS SECTION ----------

        content.add(Box.createRigidArea(new Dimension(0, 18)));

        JLabel cosmeticsLabel = sectionLabel("Cosmetics");
        cosmeticSelectedLabel = detailLabel("Selected: -");

        JPanel cosmeticsGrid = new JPanel(new GridLayout(3, 4, 12, 12));
        cosmeticsGrid.setOpaque(false);
        cosmeticsGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        cosmeticsGrid.setBorder(new EmptyBorder(0, 32, 0, 32));

        for (int i = 0; i < CosmeticInfo.ALL.length; i++) {
            JPanel cell = createCosmeticCell(i);
            cosmeticsGrid.add(cell);
        }

        content.add(cosmeticsLabel);
        content.add(cosmeticSelectedLabel);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(cosmeticsGrid);
        content.add(Box.createRigidArea(new Dimension(0, 18)));
        content.add(separatorLabel());

        // ---------- MULTIPLIERS SECTION ----------

        content.add(Box.createRigidArea(new Dimension(0, 18)));

        JLabel multipliersLabel = sectionLabel("Multipliers");
        multiplierSelectedLabel = detailLabel("Selected: -");

        JPanel multipliersRow = new JPanel(new GridLayout(1, 4, 12, 12));
        multipliersRow.setOpaque(false);
        multipliersRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        multipliersRow.setBorder(new EmptyBorder(0, 32, 0, 32));

        for (MultiplierInfo info : MultiplierInfo.ALL) {
            JPanel cell = createMultiplierCell(info);
            multipliersRow.add(cell);
        }

        content.add(multipliersLabel);
        content.add(multiplierSelectedLabel);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(multipliersRow);

        // ---------- BACK BUTTON ----------

        content.add(Box.createRigidArea(new Dimension(0, 24)));

        RoundedHoverButton backButton = new RoundedHoverButton("Back");
        Dimension buttonSize = new Dimension(320, 60);
        backButton.setPreferredSize(buttonSize);
        backButton.setMinimumSize(buttonSize);
        backButton.setMaximumSize(buttonSize);
        backButton.setFont(GameFonts.get(22f, Font.BOLD));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(backButton);

        backButton.addActionListener(e -> {
            if (windowManager != null) {
                windowManager.updateAccount(account);
            }
            dispose();
        });

        pack();

        Dimension size = getSize();
        size.width = Math.max(size.width, 700);
        size.height = Math.min(size.height, 720);
        setSize(size);

        setLocationRelativeTo(null);

        // ---------- INITIAL SELECTION HIGHLIGHT ----------

        if (account != null) {
            int equippedColor = account.getColor();
            if (equippedColor >= 0 && equippedColor < colorButtons.length
                    && colorButtons[equippedColor] != null) {
                setSelectedColorBorder(equippedColor);
                ColorInfo cInfo = ColorInfo.findById(equippedColor);
                if (cInfo != null) {
                    colorSelectedLabel.setText("Selected: " + cInfo.name);
                }
            }

            int equippedCosmeticId = account.getCosmetic();
            int equippedCosmeticIndex = CosmeticInfo.indexOfId(equippedCosmeticId);
            if (equippedCosmeticIndex >= 0
                    && equippedCosmeticIndex < cosmeticButtons.length
                    && cosmeticButtons[equippedCosmeticIndex] != null) {
                setSelectedCosmeticBorder(equippedCosmeticIndex);
                CosmeticInfo info = CosmeticInfo.ALL[equippedCosmeticIndex];
                cosmeticSelectedLabel.setText("Selected: " + info.name);
            }

            int equippedMult = account.getMultiplier();
            MultiplierInfo mInfo = MultiplierInfo.byIndex(equippedMult);
            if (mInfo != null
                    && equippedMult >= 0 && equippedMult < multiplierButtons.length
                    && multiplierButtons[equippedMult] != null) {
                setSelectedMultiplierBorder(equippedMult);
                multiplierSelectedLabel.setText("Selected: " + mInfo.label);
            }
        }

        setVisible(true);
    }

    // CustomizeWindow - Convenience constructor for testing without a WindowManager
    public CustomizeWindow(Account account) {
        this(null, account);
    }

    // ---------- LABEL HELPERS ----------

    // sectionLabel - Creates an uppercase section header label
    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(GameFonts.get(22f, Font.BOLD));
        label.setForeground(Color.WHITE);
        return label;
    }

    // detailLabel - Creates a smaller secondary label under a section title
    private JLabel detailLabel(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(GameFonts.get(16f, Font.PLAIN));
        label.setForeground(Color.WHITE);
        return label;
    }

    // separatorLabel - Creates a simple dashed separator label
    private JLabel separatorLabel() {
        JLabel sep = new JLabel("------------------------------");
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        sep.setFont(GameFonts.get(18f, Font.PLAIN));
        sep.setForeground(Color.WHITE);
        return sep;
    }

    // ---------- COLOR HELPERS ----------

    // addColorButton - Adds a color tile+price cell to the grid and wires purchase/equip logic
    private void addColorButton(JPanel grid, int colorId, Color color) {
        ColorTileButton btn = new ColorTileButton(color);
        Dimension d = new Dimension(COLOR_BUTTON_WIDTH, COLOR_BUTTON_HEIGHT);
        btn.setPreferredSize(d);
        btn.setMinimumSize(d);
        btn.setMaximumSize(d);

        if (colorId >= 0 && colorId < colorButtons.length) {
            colorButtons[colorId] = btn;
        }

        ColorInfo info = ColorInfo.findById(colorId);
        int cost = (info != null) ? info.goldCost : 0;
        String itemName = (info != null) ? info.name : "Unknown";

        JPanel priceRow = new JPanel();
        priceRow.setOpaque(false);
        priceRow.setLayout(new BoxLayout(priceRow, BoxLayout.X_AXIS));

        Icon goldIcon = loadCosmeticIcon("gold.png", 36);
        JLabel goldLabel = new JLabel(goldIcon);

        boolean ownedAtOpen = isUnlockedBit(colorId) || cost == 0;
        String priceText = ownedAtOpen ? "Owned" : String.valueOf(cost);

        JLabel priceLabel = new JLabel(priceText);
        priceLabel.setFont(GameFonts.get(12f, Font.PLAIN));
        priceLabel.setForeground(Color.WHITE);

        priceRow.add(Box.createHorizontalGlue());
        priceRow.add(goldLabel);
        priceRow.add(Box.createRigidArea(new Dimension(4, 0)));
        priceRow.add(priceLabel);
        priceRow.add(Box.createHorizontalGlue());

        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        cell.add(btn);
        cell.add(Box.createRigidArea(new Dimension(0, 4)));
        cell.add(priceRow);

        btn.setLocked(!ownedAtOpen);

        btn.addActionListener(e -> {
            boolean ownedNow = isUnlockedBit(colorId) || cost == 0;

            if (ownedNow) {
                if (account != null) {
                    account.setColor(colorId);
                    if (windowManager != null) {
                        windowManager.updateAccount(account);
                    }
                }
                setSelectedColorBorder(colorId);
                if (colorSelectedLabel != null && info != null) {
                    colorSelectedLabel.setText("Selected: " + info.name);
                }
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            if (confirmAndSpendGold(itemName, cost)) {
                unlockBit(colorId);
                if (account != null) {
                    account.setColor(colorId);
                    if (windowManager != null) {
                        windowManager.updateAccount(account);
                    }
                }
                setSelectedColorBorder(colorId);
                if (colorSelectedLabel != null && info != null) {
                    colorSelectedLabel.setText("Selected: " + info.name);
                }
                priceLabel.setText("Owned");
                btn.setLocked(false);
                Toolkit.getDefaultToolkit().beep();
            }
        });

        grid.add(cell);
    }

    // setSelectedColorBorder - Highlights the currently selected color tile
    private void setSelectedColorBorder(int colorId) {
        for (int i = 0; i < colorButtons.length; i++) {
            if (colorButtons[i] instanceof ColorTileButton) {
                ((ColorTileButton) colorButtons[i]).setSelected(i == colorId);
            }
        }
    }

    // ---------- COSMETIC HELPERS ----------

    // createCosmeticCell - Builds a cosmetic tile+price cell with purchase/equip logic
    private JPanel createCosmeticCell(int index) {
        CosmeticInfo info = CosmeticInfo.ALL[index];

        TileButton btn = new TileButton("");
        Dimension d = new Dimension(TILE_SIZE, TILE_SIZE);
        btn.setPreferredSize(d);
        btn.setMinimumSize(d);
        btn.setMaximumSize(d);

        Icon icon = loadCosmeticIcon(info.iconFile, TILE_SIZE);
        if (icon != null) {
            btn.setIcon(icon);
        } else if (index == 0) {
            btn.setText("None");
        }

        cosmeticButtons[index] = btn;

        JPanel priceRow = new JPanel();
        priceRow.setOpaque(false);
        priceRow.setLayout(new BoxLayout(priceRow, BoxLayout.X_AXIS));

        Icon goldIcon = loadCosmeticIcon("gold.png", 36);
        JLabel goldLabel = new JLabel(goldIcon);

        boolean ownedAtOpen = isUnlockedBit(info.id) || info.goldCost == 0;
        String priceText = ownedAtOpen ? "Owned" : String.valueOf(info.goldCost);

        JLabel priceLabel = new JLabel(priceText);
        priceLabel.setFont(GameFonts.get(12f, Font.PLAIN));
        priceLabel.setForeground(Color.WHITE);

        priceRow.add(Box.createHorizontalGlue());
        priceRow.add(goldLabel);
        priceRow.add(Box.createRigidArea(new Dimension(4, 0)));
        priceRow.add(priceLabel);
        priceRow.add(Box.createHorizontalGlue());

        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        cell.add(btn);
        cell.add(Box.createRigidArea(new Dimension(0, 4)));
        cell.add(priceRow);

        btn.setLocked(!ownedAtOpen);

        btn.addActionListener(e -> {
            boolean ownedNow = isUnlockedBit(info.id) || info.goldCost == 0;

            if (ownedNow) {
                if (account != null) {
                    account.setCosmetic(info.id);
                    if (windowManager != null) {
                        windowManager.updateAccount(account);
                    }
                }
                setSelectedCosmeticBorder(index);
                if (cosmeticSelectedLabel != null) {
                    cosmeticSelectedLabel.setText("Selected: " + info.name);
                }
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            if (confirmAndSpendGold(info.name, info.goldCost)) {
                unlockBit(info.id);
                if (account != null) {
                    account.setCosmetic(info.id);
                    if (windowManager != null) {
                        windowManager.updateAccount(account);
                    }
                }
                setSelectedCosmeticBorder(index);
                if (cosmeticSelectedLabel != null) {
                    cosmeticSelectedLabel.setText("Selected: " + info.name);
                }
                priceLabel.setText("Owned");
                btn.setLocked(false);
                Toolkit.getDefaultToolkit().beep();
            }
        });

        return cell;
    }

    // loadCosmeticIcon - Loads and scales a cosmetic icon by filename
    private static Icon loadCosmeticIcon(String fileName, int targetSize) {
        if (fileName == null) {
            return null;
        }
        java.net.URL url = CustomizeWindow.class.getResource(
                "/game/resources/images/" + fileName);
        if (url == null) {
            System.err.println("Missing cosmetic image: " + fileName);
            return null;
        }
        ImageIcon raw = new ImageIcon(url);
        Image scaled = raw.getImage()
                .getScaledInstance(targetSize - 12, targetSize - 12, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // setSelectedCosmeticBorder - Highlights the currently selected cosmetic tile
    private void setSelectedCosmeticBorder(int index) {
        for (int i = 0; i < cosmeticButtons.length; i++) {
            if (cosmeticButtons[i] instanceof TileButton) {
                ((TileButton) cosmeticButtons[i]).setSelected(i == index);
            }
        }
    }

    // ---------- MULTIPLIER HELPERS ----------

    // createMultiplierCell - Builds a multiplier tile+price cell with purchase/equip logic
    private JPanel createMultiplierCell(MultiplierInfo info) {
        TileButton btn = new TileButton(info.label);
        Dimension d = new Dimension(TILE_SIZE, TILE_SIZE);
        btn.setPreferredSize(d);
        btn.setMinimumSize(d);
        btn.setMaximumSize(d);

        btn.setForeground(UiColors.PLAYER_BLACK);
        int index = info.index;
        int bitIndex = info.bitIndex;
        int cost = info.diamondCost;

        multiplierButtons[index] = btn;

        JPanel priceRow = new JPanel();
        priceRow.setOpaque(false);
        priceRow.setLayout(new BoxLayout(priceRow, BoxLayout.X_AXIS));

        Icon diamondIcon = loadCosmeticIcon("diamond.png", 36);
        JLabel diamondLabel = new JLabel(diamondIcon);

        boolean ownedAtOpen = isUnlockedBit(bitIndex) || cost == 0;
        String priceText = ownedAtOpen ? "Owned" : String.valueOf(cost);

        JLabel priceLabel = new JLabel(priceText);
        priceLabel.setFont(GameFonts.get(12f, Font.PLAIN));
        priceLabel.setForeground(Color.WHITE);

        priceRow.add(Box.createHorizontalGlue());
        priceRow.add(diamondLabel);
        priceRow.add(Box.createRigidArea(new Dimension(4, 0)));
        priceRow.add(priceLabel);
        priceRow.add(Box.createHorizontalGlue());

        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        cell.add(btn);
        cell.add(Box.createRigidArea(new Dimension(0, 4)));
        cell.add(priceRow);

        btn.setLocked(!ownedAtOpen);

        btn.addActionListener(e -> {
            boolean ownedNow = isUnlockedBit(bitIndex) || cost == 0;

            if (ownedNow) {
                if (account != null) {
                    account.setMultiplier(index);
                    if (windowManager != null) {
                        windowManager.updateAccount(account);
                    }
                }
                setSelectedMultiplierBorder(index);
                if (multiplierSelectedLabel != null) {
                    multiplierSelectedLabel.setText("Selected: " + info.label);
                }
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            if (confirmAndSpendDiamonds(info.label, cost)) {
                unlockBit(bitIndex);
                if (account != null) {
                    account.setMultiplier(index);
                    if (windowManager != null) {
                        windowManager.updateAccount(account);
                    }
                }
                setSelectedMultiplierBorder(index);
                if (multiplierSelectedLabel != null) {
                    multiplierSelectedLabel.setText("Selected: " + info.label);
                }
                priceLabel.setText("Owned");
                btn.setLocked(false);
                Toolkit.getDefaultToolkit().beep();
            }
        });

        return cell;
    }

    // setSelectedMultiplierBorder - Highlights the currently selected multiplier tile
    private void setSelectedMultiplierBorder(int index) {
        for (int i = 0; i < multiplierButtons.length; i++) {
            if (multiplierButtons[i] instanceof TileButton) {
                ((TileButton) multiplierButtons[i]).setSelected(i == index);
            }
        }
    }

    // ---------- PURCHASE HELPERS ----------

    // confirmAndSpendGold - Shows a dialog to buy an item with gold; returns true if purchased
    private boolean confirmAndSpendGold(String itemName, int cost) {
        if (account == null || cost <= 0) {
            return true;
        }

        final boolean[] purchased = { false };
        int current = account.getVaultGold();

        JDialog dialog = new JDialog(this, "Unlock", true);
        Color bgColor = new Color(30, 30, 30);

        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(20, 40, 24, 40));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(true);
        content.setBackground(bgColor);

        JLabel title = new JLabel("Unlock " + itemName + "?");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(GameFonts.get(24f, Font.BOLD));
        title.setForeground(Color.WHITE);

        JPanel costRow = new JPanel();
        costRow.setOpaque(false);
        costRow.setLayout(new BoxLayout(costRow, BoxLayout.X_AXIS));

        JLabel iconLabel = new JLabel(loadCosmeticIcon("gold.png", 40));
        JLabel costLabel = new JLabel("Cost: " + cost + " gold");
        costLabel.setFont(GameFonts.get(18f, Font.PLAIN));
        costLabel.setForeground(Color.WHITE);

        costRow.add(iconLabel);
        costRow.add(Box.createRigidArea(new Dimension(10, 0)));
        costRow.add(costLabel);

        JLabel balanceLabel = new JLabel("You have: " + current + " gold");
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceLabel.setFont(GameFonts.get(16f, Font.PLAIN));
        balanceLabel.setForeground(Color.WHITE);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setFont(GameFonts.get(14f, Font.PLAIN));
        errorLabel.setForeground(new Color(255, 80, 80));

        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 12)));
        costRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(costRow);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(balanceLabel);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(errorLabel);
        content.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel buttonsRow = new JPanel();
        buttonsRow.setOpaque(false);
        buttonsRow.setLayout(new BoxLayout(buttonsRow, BoxLayout.X_AXIS));

        RoundedHoverButton buyButton = new RoundedHoverButton("Buy");
        RoundedHoverButton backButton = new RoundedHoverButton("Back");

        Dimension btnSize = new Dimension(140, 48);
        buyButton.setPreferredSize(btnSize);
        buyButton.setMinimumSize(btnSize);
        buyButton.setMaximumSize(btnSize);
        buyButton.setFont(GameFonts.get(18f, Font.BOLD));

        backButton.setPreferredSize(btnSize);
        backButton.setMinimumSize(btnSize);
        backButton.setMaximumSize(btnSize);
        backButton.setFont(GameFonts.get(18f, Font.BOLD));

        buttonsRow.add(Box.createHorizontalGlue());
        buttonsRow.add(buyButton);
        buttonsRow.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonsRow.add(backButton);
        buttonsRow.add(Box.createHorizontalGlue());

        content.add(buttonsRow);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        buyButton.addActionListener(e -> {
            int gold = account.getVaultGold();
            if (gold < cost) {
                errorLabel.setText("Not enough gold.");
                return;
            }

            account.setVaultGold(gold - cost);
            if (windowManager != null) {
                windowManager.updateAccount(account);
            }
            purchased[0] = true;
            dialog.dispose();
        });

        backButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
        return purchased[0];
    }

    // confirmAndSpendDiamonds - Shows a dialog to buy an item with diamonds; returns true if purchased
    private boolean confirmAndSpendDiamonds(String itemName, int cost) {
        if (account == null || cost <= 0) {
            return true;
        }

        final boolean[] purchased = { false };
        int current = account.getVaultDiamonds();

        JDialog dialog = new JDialog(this, "Unlock", true);
        Color bgColor = new Color(30, 30, 30);

        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(20, 40, 24, 40));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(true);
        content.setBackground(bgColor);

        JLabel title = new JLabel("Unlock " + itemName + "?");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(GameFonts.get(24f, Font.BOLD));
        title.setForeground(Color.WHITE);

        JPanel costRow = new JPanel();
        costRow.setOpaque(false);
        costRow.setLayout(new BoxLayout(costRow, BoxLayout.X_AXIS));

        JLabel iconLabel = new JLabel(loadCosmeticIcon("diamond.png", 40));
        JLabel costLabel = new JLabel("Cost: " + cost + " diamonds");
        costLabel.setFont(GameFonts.get(18f, Font.PLAIN));
        costLabel.setForeground(Color.WHITE);

        costRow.add(iconLabel);
        costRow.add(Box.createRigidArea(new Dimension(10, 0)));
        costRow.add(costLabel);

        JLabel balanceLabel = new JLabel("You have: " + current + " diamonds");
        balanceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        balanceLabel.setFont(GameFonts.get(16f, Font.PLAIN));
        balanceLabel.setForeground(Color.WHITE);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setFont(GameFonts.get(14f, Font.PLAIN));
        errorLabel.setForeground(new Color(255, 80, 80));

        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 12)));
        costRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(costRow);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(balanceLabel);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(errorLabel);
        content.add(Box.createRigidArea(new Dimension(0, 18)));

        JPanel buttonsRow = new JPanel();
        buttonsRow.setOpaque(false);
        buttonsRow.setLayout(new BoxLayout(buttonsRow, BoxLayout.X_AXIS));

        RoundedHoverButton buyButton = new RoundedHoverButton("Buy");
        RoundedHoverButton backButton = new RoundedHoverButton("Back");

        Dimension btnSize = new Dimension(140, 48);
        buyButton.setPreferredSize(btnSize);
        buyButton.setMinimumSize(btnSize);
        buyButton.setMaximumSize(btnSize);
        buyButton.setFont(GameFonts.get(18f, Font.BOLD));

        backButton.setPreferredSize(btnSize);
        backButton.setMinimumSize(btnSize);
        backButton.setMaximumSize(btnSize);
        backButton.setFont(GameFonts.get(18f, Font.BOLD));

        buttonsRow.add(Box.createHorizontalGlue());
        buttonsRow.add(buyButton);
        buttonsRow.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonsRow.add(backButton);
        buttonsRow.add(Box.createHorizontalGlue());

        content.add(buttonsRow);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        buyButton.addActionListener(e -> {
            int diamonds = account.getVaultDiamonds();
            if (diamonds < cost) {
                errorLabel.setText("Not enough diamonds.");
                return;
            }

            account.setVaultDiamonds(diamonds - cost);
            if (windowManager != null) {
                windowManager.updateAccount(account);
            }
            purchased[0] = true;
            dialog.dispose();
        });

        backButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
        return purchased[0];
    }

    // ---------- UNLOCK HELPERS ----------

    // isUnlockedBit - Returns true if a given unlock bit is set on the account
    private boolean isUnlockedBit(int bitIndex) {
        if (account == null || bitIndex < 0 || bitIndex >= 63) {
            return false;
        }
        long mask = account.getUnlocks();
        return (mask & (1L << bitIndex)) != 0L;
    }

    // unlockBit - Sets an unlock bit on the account
    private void unlockBit(int bitIndex) {
        if (account == null || bitIndex < 0 || bitIndex >= 63) {
            return;
        }
        long mask = account.getUnlocks();
        mask |= (1L << bitIndex);
        account.setUnlocks(mask);
    }
}
