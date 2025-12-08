package game.ui.windows;

import game.account.Account;
import game.ui.WindowManager;
import game.ui.components.controls.*;
import game.ui.components.panels.*;
import game.ui.theme.*;
import game.settings.*;
import game.cosmetics.MultiplierInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * MainMenuWindow - Full-screen main menu with background art, loot HUD, and menu buttons.
 */
public class MainMenuWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    // ---------- FIELDS ----------

    private final WindowManager windowManager;
    private Account currentAccount;
    private LootDisplayPanel lootDisplay;

    private int screenWidth;
    private int screenHeight;
    private int buttonHeight;

    private int rowWidth;
    private int buttonGap;
    private int halfButtonWidth;
    private int fullButtonWidth;

    // ---------- CONSTRUCTORS ----------

    // MainMenuWindow - Constructs the main menu for the given account
    public MainMenuWindow(WindowManager windowManager, Account account) {
        super("Pixel Pursuit - Main Menu");
        this.windowManager = windowManager;
        this.currentAccount = account;
        initUI();
    }

    // ---------- UI SETUP ----------

    // initUI - Builds the background, loot HUD, menu buttons, and actions
    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        // screen sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth  = screenSize.width;
        screenHeight = screenSize.height;

        Dimension fieldSize = new Dimension(screenWidth / 4, screenHeight / 16);
        buttonHeight        = (int) (fieldSize.height * 0.8);

        int labelGap   = Math.max(8, screenHeight / 120);
        buttonGap      = Math.max(10, screenHeight / 80);
        int labelWidth = screenWidth / 8;

        // layout math
        rowWidth = labelWidth + labelGap + fieldSize.width;

        float idleScale = RoundedHoverButton.IDLE_SCALE; // 0.9f
        halfButtonWidth = Math.round((rowWidth - buttonGap) / (2 * idleScale));
        fullButtonWidth = (int) Math.round(rowWidth * 1.175);

        // ---------- BACKGROUND ----------

        BackgroundPanel mainPanel = new BackgroundPanel("/game/resources/images/menuBackground.png");
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // ---------- LOOT DISPLAY (TOP BAR) ----------

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        lootDisplay = new LootDisplayPanel(
                currentAccount.getVaultGold(),
                currentAccount.getVaultDiamonds()
        );

        double multValue = 1.0;
        String difficultyName = "Unknown";

        if (currentAccount != null) {
            int multIndex = currentAccount.getMultiplier();
            multValue = MultiplierInfo.getValueForIndex(multIndex);
        }

        Difficulty diff = GameConfig.getCurrentDifficulty();
        if (diff != null) {
            difficultyName = diff.getDisplayName();
        }

        lootDisplay.setMultiplierAndDifficulty(multValue, difficultyName);

        JPanel rightBox = new JPanel();
        rightBox.setOpaque(false);
        rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.X_AXIS));
        rightBox.add(Box.createHorizontalGlue());
        rightBox.add(lootDisplay);

        topBar.add(rightBox, BorderLayout.EAST);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // ---------- CENTER MENU PANEL ----------

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        int gapRow = screenHeight / 90;

        // Row 1: Play (full)
        RoundedHoverButton playButton = RoundedHoverButton.createMenuButton(
                "Play", fullButtonWidth, buttonHeight, screenHeight);
        menuPanel.add(playButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, gapRow)));

        // Row 2: Customize / Difficulty
        RoundedHoverButton customizeButton = RoundedHoverButton.createMenuButton(
                "Customize", halfButtonWidth, buttonHeight, screenHeight);
        RoundedHoverButton settingsButton = RoundedHoverButton.createMenuButton(
                "Difficulty", halfButtonWidth, buttonHeight, screenHeight);
        JPanel row2 = RoundedHoverButton.createButtonRow(customizeButton, settingsButton, buttonGap);
        menuPanel.add(row2);
        menuPanel.add(Box.createRigidArea(new Dimension(0, gapRow)));

        // Row 3: Leaderboard / Sign Out
        RoundedHoverButton leaderboardButton = RoundedHoverButton.createMenuButton(
                "Leaderboard", halfButtonWidth, buttonHeight, screenHeight);
        RoundedHoverButton signOutButton = RoundedHoverButton.createMenuButton(
                "Sign Out", halfButtonWidth, buttonHeight, screenHeight);
        JPanel row3 = RoundedHoverButton.createButtonRow(leaderboardButton, signOutButton, buttonGap);
        menuPanel.add(row3);
        menuPanel.add(Box.createRigidArea(new Dimension(0, gapRow)));

        // Row 4: Quit (full)
        RoundedHoverButton quitButton = RoundedHoverButton.createMenuButton(
                "Quit", fullButtonWidth, buttonHeight, screenHeight);
        menuPanel.add(quitButton);

        // username label
        if (currentAccount != null) {
            menuPanel.add(Box.createRigidArea(new Dimension(0, screenHeight / 120)));
            JLabel userLabel = new JLabel("User: " + currentAccount.getUsername());
            userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            int userFontSize = Math.max(14, Math.min(screenHeight / 45, 24));
            userLabel.setFont(GameFonts.get((float) userFontSize, Font.PLAIN));
            userLabel.setForeground(Color.WHITE);
            menuPanel.add(userLabel);
        }

        // center menu on background
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(menuPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // ---------- BUTTON ACTIONS ----------

        // quitButton - Exits the game (via WindowManager if present)
        quitButton.addActionListener(e -> {
            if (windowManager != null) {
                windowManager.exitGame();
            } else {
                System.exit(0);
            }
        });

        // signOutButton - Returns to login and clears current account
        signOutButton.addActionListener(e -> {
            windowManager.setCurrentAccount(null);
            windowManager.showLoginWindow();
        });

        // playButton - Starts a new game if an account is loaded
        playButton.addActionListener(e -> {
            if (currentAccount == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No account loaded. Please log in first.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (windowManager != null) {
                windowManager.showGameWindow();
            } else {
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    new GameWindow(windowManager, currentAccount);
                });
            }
        });

        // customizeButton - Opens the customize window
        customizeButton.addActionListener(e -> {
            if (windowManager != null) {
                windowManager.showCustomizeWindow();
            } else {
                new CustomizeWindow(currentAccount);
            }
        });

        // settingsButton - Opens the difficulty window
        settingsButton.addActionListener(e -> {
            new DifficultyWindow(windowManager);
        });

        // leaderboardButton - Opens the leaderboard window or shows info if unavailable
        leaderboardButton.addActionListener(e -> {
            if (windowManager != null) {
                windowManager.showLeaderboardWindow();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Leaderboard unavailable (no WindowManager wired).",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        setVisible(true);
    }

    // ---------- LOOT REFRESH ----------

    // refreshLootDisplay - Syncs loot HUD with vault + multiplier + difficulty
    public void refreshLootDisplay() {
        if (lootDisplay == null || currentAccount == null) {
            return;
        }

        lootDisplay.updateLoot(
                currentAccount.getVaultGold(),
                currentAccount.getVaultDiamonds()
        );

        double multValue = 1.0;
        int multIndex = currentAccount.getMultiplier();
        multValue = MultiplierInfo.getValueForIndex(multIndex);

        String difficultyName = "Unknown";
        Difficulty diff = GameConfig.getCurrentDifficulty();
        if (diff != null) {
            difficultyName = diff.getDisplayName();
        }

        lootDisplay.setMultiplierAndDifficulty(multValue, difficultyName);
    }
}
