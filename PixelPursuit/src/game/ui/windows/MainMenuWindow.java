package game.ui.windows;

import game.account.*;
import game.cosmetics.*;
import game.persistence.*;
import game.ui.*;
import game.ui.components.controls.RoundedHoverButton;
import game.ui.components.panels.BackgroundPanel;
import game.ui.components.panels.GoldDisplayPanel;
import game.ui.theme.GameFonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main menu window for Pixel Pursuit.
 */
public class MainMenuWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private Account currentAccount;

    private int screenWidth;
    private int screenHeight;
    private int buttonHeight;

    private int rowWidth;
    private int buttonGap;
    private int halfButtonWidth;
    private int fullButtonWidth;

    // -------- constructors --------

    public MainMenuWindow(Account account) {
        super("Pixel Pursuit - Main Menu");
        this.currentAccount = account;
        initUI();
    }

    // keep no-arg for quick testing
    public MainMenuWindow() {
        this(null);
    }

    // -------- UI setup --------

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth  = screenSize.width;
        screenHeight = screenSize.height;

        // Reuse similar sizing logic as LogInWindow
        Dimension fieldSize = new Dimension(screenWidth / 4, screenHeight / 16);
        buttonHeight        = (int) (fieldSize.height * 0.8);

        int labelGap  = Math.max(8, screenHeight / 120);
        buttonGap     = Math.max(10, screenHeight / 80);
        int labelWidth = screenWidth / 8;

        // Same "row width" idea as username + field row
        rowWidth = labelWidth + labelGap + fieldSize.width;

        // Half buttons: same physical size as login/create, accounting for 0.9 idle scale
        float idleScale = RoundedHoverButton.IDLE_SCALE; // 0.9f
        halfButtonWidth = Math.round((rowWidth - buttonGap) / (2 * idleScale));

        // Full-width buttons: same vibe as login's Exit (a bit longer)
        fullButtonWidth = (int) Math.round(rowWidth * 1.175);

        // ---------- BACKGROUND ----------
        BackgroundPanel mainPanel = new BackgroundPanel("/game/resources/images/menuBackground.png");
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

     // ---------- TOP BAR (gold/vault display in top-right) ----------
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        int freeGold  = 0;  // current run / at-risk gold
        int vaultGold = 0;  // banked gold
        if (currentAccount != null) {
            freeGold  = currentAccount.getFreeGold();
            vaultGold = currentAccount.getVaultGold();
        }

        // HUD: [vault icon] : vaultGold    [gold icon] : freeGold
        GoldDisplayPanel goldDisplay = new GoldDisplayPanel(vaultGold, freeGold);

        JPanel rightBox = new JPanel();
        rightBox.setOpaque(false);
        rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.X_AXIS));
        rightBox.add(Box.createHorizontalGlue());
        rightBox.add(goldDisplay);

        topBar.add(rightBox, BorderLayout.EAST);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // ---------- CENTER MENU PANEL ----------
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        int gapRow   = screenHeight / 90;

        // Row 1: Play (full)
        RoundedHoverButton playButton = createFullButton("Play");
        menuPanel.add(playButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, gapRow)));

        // Row 2: Customize (left) / Shop (right)
        RoundedHoverButton customizeButton = createHalfButton("Customize");
        RoundedHoverButton shopButton      = createHalfButton("Shop");
        JPanel row2 = createHalfRow(customizeButton, shopButton);
        menuPanel.add(row2);
        menuPanel.add(Box.createRigidArea(new Dimension(0, gapRow)));

        // Row 3: Settings (left) / Leaderboard (right)
        RoundedHoverButton settingsButton    = createHalfButton("Settings");
        RoundedHoverButton leaderboardButton = createHalfButton("Leaderboard");
        JPanel row3 = createHalfRow(settingsButton, leaderboardButton);
        menuPanel.add(row3);
        menuPanel.add(Box.createRigidArea(new Dimension(0, gapRow)));

        // Row 4: Blank (left) / Sign Out (right)
        JPanel row4 = new JPanel();
        row4.setOpaque(false);
        row4.setLayout(new BoxLayout(row4, BoxLayout.X_AXIS));
        row4.setAlignmentX(Component.CENTER_ALIGNMENT);

        // left "blank" space same size as half button
        Component blankFiller = Box.createRigidArea(new Dimension(halfButtonWidth, buttonHeight));
        RoundedHoverButton signOutButton = createHalfButton("Sign Out");

        row4.add(blankFiller);
        row4.add(Box.createRigidArea(new Dimension(buttonGap, 0)));
        row4.add(signOutButton);

        menuPanel.add(row4);
        menuPanel.add(Box.createRigidArea(new Dimension(0, gapRow)));

        // Row 5: Quit (full)
        RoundedHoverButton quitButton = createFullButton("Quit");
        menuPanel.add(quitButton);

        // Optional: show username under the menu
        if (currentAccount != null) {
            menuPanel.add(Box.createRigidArea(new Dimension(0, screenHeight / 120)));
            JLabel userLabel = new JLabel("Logged in as: " + currentAccount.getUsername());
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

        quitButton.addActionListener(e -> System.exit(0));

        signOutButton.addActionListener(e -> {
            // Back to login screen
            SwingUtilities.invokeLater(() -> {
                dispose();
                new LogInWindow();
            });
        });

        playButton.addActionListener(e -> {
            if (currentAccount != null) {
                // Open game with this account
                SwingUtilities.invokeLater(() -> {
                    dispose(); // close menu while in game
                    new GameWindow(currentAccount);
                });
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No account loaded. Please log in first.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        customizeButton.addActionListener(e -> new CustomizeWindow());
        
        shopButton.addActionListener(e -> JOptionPane.showMessageDialog(
                this, "Shop clicked (hook up shop screen)", "Info",
                JOptionPane.INFORMATION_MESSAGE
        ));

        settingsButton.addActionListener(e -> JOptionPane.showMessageDialog(
                this, "Settings clicked (hook up settings screen)", "Info",
                JOptionPane.INFORMATION_MESSAGE
        ));

        leaderboardButton.addActionListener(e -> showLeaderboardDialog());

        setVisible(true);
    }

    // -------- helper methods for rows/buttons --------

    private JPanel createHalfRow(RoundedHoverButton left, RoundedHoverButton right) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        row.add(left);
        row.add(Box.createRigidArea(new Dimension(buttonGap, 0)));
        row.add(right);

        return row;
    }

    private RoundedHoverButton createHalfButton(String text) {
        RoundedHoverButton button = new RoundedHoverButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension size = new Dimension(halfButtonWidth, buttonHeight);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);

        int fontSize = Math.max(18, Math.min(screenHeight / 34, 30));
        button.setFont(GameFonts.get((float) fontSize, Font.BOLD));
        return button;
    }

    private RoundedHoverButton createFullButton(String text) {
        RoundedHoverButton button = new RoundedHoverButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension size = new Dimension(fullButtonWidth, buttonHeight);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);

        int fontSize = Math.max(18, Math.min(screenHeight / 34, 30));
        button.setFont(GameFonts.get((float) fontSize, Font.BOLD));
        return button;
    }
    
    private void showLeaderboardDialog() {
        AccountManager am = new AccountManager(); // loads accounts.txt
        Leaderboard lb = new Leaderboard();

        java.util.List<LeaderboardEntry> entries =
                lb.buildFromAccounts(am.getAllAccounts(), 10);

        JDialog dialog = new JDialog(this, "Leaderboard", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 40, 20, 40));
        panel.setOpaque(true);
        panel.setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Top 10 Runs");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(GameFonts.get(28f, Font.BOLD));
        title.setForeground(Color.WHITE);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        if (entries.isEmpty()) {
            JLabel empty = new JLabel("No runs recorded yet.");
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setFont(GameFonts.get(20f, Font.PLAIN));
            empty.setForeground(Color.LIGHT_GRAY);
            panel.add(empty);
        } else {
            int rank = 1;
            for (LeaderboardEntry entry : entries) {
                String timeStr = formatTime(entry.getBestTimeSeconds());
                String rowText = String.format("%2d. %-12s  %s",
                        rank, entry.getUsername(), timeStr);

                JLabel row = new JLabel(rowText);
                row.setAlignmentX(Component.CENTER_ALIGNMENT);
                row.setFont(GameFonts.get(20f, Font.PLAIN));
                row.setForeground(Color.WHITE);
                panel.add(row);
                panel.add(Box.createRigidArea(new Dimension(0, 6)));

                rank++;
            }
        }

        panel.add(Box.createRigidArea(new Dimension(0, 16)));

        JButton close = new JButton("Close");
        close.setAlignmentX(Component.CENTER_ALIGNMENT);
        close.setFont(GameFonts.get(18f, Font.BOLD));
        close.addActionListener(e -> dialog.dispose());
        panel.add(close);

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setSize(Math.max(dialog.getWidth(), 400), Math.max(dialog.getHeight(), 360));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /** Simple mm:ss formatter for leaderboard rows. */
    private String formatTime(double seconds) {
        int total = (int) Math.floor(seconds);
        int mins = total / 60;
        int secs = total % 60;
        return String.format("%02d:%02d", mins, secs);
    }

}
