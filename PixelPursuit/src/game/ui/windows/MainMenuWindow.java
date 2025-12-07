package game.ui.windows;

import game.account.Account;
import game.ui.WindowManager;
import game.ui.components.controls.*;
import game.ui.components.panels.*;
import game.ui.theme.*;
import game.settings.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainMenuWindow extends JFrame {

    private static final long serialVersionUID = 1L;

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

    // -------- Constructors --------

    public MainMenuWindow(WindowManager windowManager, Account account) {
        super("Pixel Pursuit - Main Menu");
        this.windowManager = windowManager;
        this.currentAccount = account;
        initUI();
    }

    // -------- UI setup --------

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        // Screen Setup
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth  = screenSize.width;
        screenHeight = screenSize.height;

        Dimension fieldSize = new Dimension(screenWidth / 4, screenHeight / 16);
        buttonHeight        = (int) (fieldSize.height * 0.8);

        int labelGap  = Math.max(8, screenHeight / 120);
        buttonGap     = Math.max(10, screenHeight / 80);
        int labelWidth = screenWidth / 8;

        // Menu Setup
        rowWidth = labelWidth + labelGap + fieldSize.width;

        // Half buttons
        float idleScale = RoundedHoverButton.IDLE_SCALE; // 0.9f
        halfButtonWidth = Math.round((rowWidth - buttonGap) / (2 * idleScale));

        // Full-width buttons
        fullButtonWidth = (int) Math.round(rowWidth * 1.175);

        // ---------- BACKGROUND ----------
        BackgroundPanel mainPanel = new BackgroundPanel("/game/resources/images/menuBackground.png");
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

     // ---------- LOOT DISPLAY ----------
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        lootDisplay = new LootDisplayPanel(
                currentAccount.getVaultGold(),
                currentAccount.getVaultDiamonds()
        );

        // ---- NEW: Multiplier + Difficulty line ----
        double multValue = 1.0;
        String difficultyName = "Unknown";

        // Get equipped multiplier from the Account (0–3 → 2x, 3x, 5x, 10x)
        if (currentAccount != null) {
            int multIndex = currentAccount.getMultiplier();
            switch (multIndex) {
                case 0: multValue = 2.0;  break;
                case 1: multValue = 3.0;  break;
                case 2: multValue = 5.0;  break;
                case 3: multValue = 10.0; break;
                default: multValue = 1.0; // fallback
            }
        }

        // Get difficulty display name from GameConfig
        Difficulty diff = GameConfig.getCurrentDifficulty();
        if (diff != null) {
            difficultyName = diff.getDisplayName();
        }

        // Push into the HUD second line
        lootDisplay.setMultiplierAndDifficulty(multValue, difficultyName);
        // -----------------------------------

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

        int gapRow   = screenHeight / 90;

        // Row 1: Play (full)
        RoundedHoverButton playButton = RoundedHoverButton.createMenuButton(
                "Play", fullButtonWidth, buttonHeight, screenHeight);
        menuPanel.add(playButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, gapRow)));

        // Row 2: Customize (left) / Settings (right)
        RoundedHoverButton customizeButton = RoundedHoverButton.createMenuButton(
                "Customize", halfButtonWidth, buttonHeight, screenHeight);
        RoundedHoverButton settingsButton = RoundedHoverButton.createMenuButton(
                "Difficulty", halfButtonWidth, buttonHeight, screenHeight);
        JPanel row2 = RoundedHoverButton.createButtonRow(customizeButton, settingsButton, buttonGap);
        menuPanel.add(row2);
        menuPanel.add(Box.createRigidArea(new Dimension(0, gapRow)));

        // Row 3: Leaderboard (left) / Sign Out (right)
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

        // Show username under menu
        if (currentAccount != null) {
            menuPanel.add(Box.createRigidArea(new Dimension(0, screenHeight / 120)));
            JLabel userLabel = new JLabel("User: " + currentAccount.getUsername());
            userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            int userFontSize = Math.max(14, Math.min(screenHeight / 45, 24));
            userLabel.setFont(GameFonts.get((float) userFontSize, Font.PLAIN));
            userLabel.setForeground(Color.WHITE);
            menuPanel.add(userLabel);
        }

        // Center the menu panel on the background
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(menuPanel);
        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // ---------- BUTTON ACTIONS ----------

        // Quit
        quitButton.addActionListener(e -> {
            if (windowManager != null) {
                windowManager.exitGame();
            } else {
                System.exit(0);
            }
        });

        // Sign Out
        signOutButton.addActionListener(e -> {
            windowManager.setCurrentAccount(null);
            windowManager.showLoginWindow();
        });

        // Play
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
                // fallback for standalone testing
                SwingUtilities.invokeLater(() -> {
                    dispose(); // close menu while in game
                    new GameWindow(windowManager, currentAccount);
                });
            }
        });

        // Customize
        customizeButton.addActionListener(e -> {
            if (windowManager != null) {
                windowManager.showCustomizeWindow();
            } else {
                new CustomizeWindow(currentAccount);
            }
        });

        // Settings
        settingsButton.addActionListener(e -> {
            new DifficultyWindow(windowManager);
        });
        
        // Leaderboard
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

    // -------- Loot refresh for WindowManager --------

    public void refreshLootDisplay() {
        if (lootDisplay == null || currentAccount == null) {
            return;
        }

        // 1) Update vault gold / diamonds
        lootDisplay.updateLoot(
                currentAccount.getVaultGold(),
                currentAccount.getVaultDiamonds()
        );

        // 2) Recompute multiplier value from the equipped index on the account
        double multValue = 1.0;
        int multIndex = currentAccount.getMultiplier();
        switch (multIndex) {
            case 0: multValue = 2.0;  break;
            case 1: multValue = 3.0;  break;
            case 2: multValue = 5.0;  break;
            case 3: multValue = 10.0; break;
            default: multValue = 1.0; // fallback
        }

        // 3) Re-read difficulty name from GameConfig
        String difficultyName = "Unknown";
        Difficulty diff = GameConfig.getCurrentDifficulty();
        if (diff != null) {
            difficultyName = diff.getDisplayName();
        }

        // 4) Push second-line text into the HUD
        lootDisplay.setMultiplierAndDifficulty(multValue, difficultyName);
    }
}
