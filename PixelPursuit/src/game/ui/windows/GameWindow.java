package game.ui.windows;

import game.account.*;
import game.world.*;
import game.gameplay.*;
import game.ui.*;
import game.scoring.*;
import game.settings.*;
import game.ui.components.panels.*;
import game.ui.theme.*;
import game.cosmetics.PlayerCosmetics;
import game.cosmetics.MultiplierInfo;
import game.ui.components.controls.RoundedHoverButton;

import javax.swing.*;
import java.awt.*;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * GameWindow - Top-level window for running a game session.
 *  - Builds maze, runner, chasers, and in-game HUD.
 *  - Owns the GamePanel and end-of-run summary dialog flow.
 */
public class GameWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    // ---------- FIELDS ----------

    private final WindowManager windowManager;
    private final Account currentAccount;
    private final ScoreSystem scoreSystem;
    private final Difficulty difficulty;
    private final Session session;
    private final GamePanel gamePanel;
    private final LootDisplayPanel lootDisplay;

    private final Color runnerColor;
    private final int runnerCosmeticId;

    private boolean gameOver = false;

    // ---------- CONSTRUCTORS ----------

    // GameWindow - Creates the full-screen game window and starts a new session
    public GameWindow(WindowManager windowManager, Account account) {
        super("Pixel Pursuit - Game");
        this.windowManager = windowManager;
        this.currentAccount = account;
        this.scoreSystem = new ScoreSystem();
        this.difficulty = GameConfig.getCurrentDifficulty();

        // load player appearance from account
        if (currentAccount != null) {
            this.runnerColor = PlayerCosmetics.getRunnerColor(currentAccount);
            int equippedCosmetic = PlayerCosmetics.getEquippedCosmetic(currentAccount);
            this.runnerCosmeticId = equippedCosmetic;
        } else {
            this.runnerColor = PlayerCosmetics.DEFAULT_COLOR;
            this.runnerCosmeticId = -1;
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        // world / gameplay setup
        Maze maze = new Maze();
        Runner runner = new Runner(maze, maze.getEntranceX(), maze.getEntranceY());
        this.session = new Session(maze, runner);

        int chaserCount = GameConfig.getChaserCountForCurrentDifficulty();

        int spawnX = Math.max(1, maze.getExitX() - 3);
        int baseY = maze.getExitY();

        for (int i = 0; i < chaserCount; i++) {
            int dy = (i - (chaserCount - 1) / 2);
            int spawnY = baseY + 2 * dy;

            // clamp inside borders
            spawnY = Math.max(1, Math.min(maze.getHeight() - 2, spawnY));

            // walk upward until we find a walkable spawn cell
            while (!maze.getCell(spawnX, spawnY).isWalkable() && spawnY > 1) {
                spawnY--;
            }

            Chaser chaser = new Chaser(maze, spawnX, spawnY, new SimpleChaserAI());
            session.addChaser(chaser);
        }

        // background frame art
        BackgroundPanel mainPanel = new BackgroundPanel("/game/resources/images/gameBackground.png");
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);

        // ---------- LOOT DISPLAY ----------

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        this.lootDisplay = new LootDisplayPanel(
                0,    // starting run gold
                0,    // starting run diamonds
                0.0   // time
        );

        // set multiplier + difficulty for the in-game HUD
        double equippedMult = getEquippedMultiplierValue();
        lootDisplay.setMultiplierAndDifficulty(equippedMult, difficulty.getDisplayName());

        JPanel rightBox = new JPanel();
        rightBox.setOpaque(false);
        rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.X_AXIS));
        rightBox.add(Box.createHorizontalGlue());
        rightBox.add(lootDisplay);

        topBar.add(rightBox, BorderLayout.EAST);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // center game panel
        this.gamePanel = new GamePanel(session, this);
        mainPanel.add(gamePanel, BorderLayout.CENTER);

        setVisible(true);
        SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
    }

    // ---------- ACCESSORS ----------

    // getRunnerColor - Returns the runner's color for rendering
    public Color getRunnerColor() {
        return runnerColor;
    }

    // getRunnerCosmeticId - Returns the equipped cosmetic id, or -1 if none
    public int getRunnerCosmeticId() {
        return runnerCosmeticId;
    }

    // getCurrentAccount - Returns the account tied to this game window
    public Account getCurrentAccount() {
        return currentAccount;
    }

    // getSession - Returns the underlying Session for this run
    public Session getSession() {
        return session;
    }

    // ---------- GAME FLOW ----------

    // handleRunnerReachedExit - Called when the runner reaches the maze exit
    public void handleRunnerReachedExit() {
        if (gameOver) return;
        gameOver = true;

        gamePanel.stopMovement();
        showEndOfRunDialog(true);
    }

    // updateHudFromSession - Syncs HUD values from the current session state
    public void updateHudFromSession() {
        if (lootDisplay == null) return;

        int runGold = session.getRunGold();
        int runDiamonds = session.getPickupDiamonds();
        double time = session.getElapsedTimeSeconds();

        if (currentAccount != null) {
            currentAccount.setFreeGold(runGold);
            currentAccount.setFreeDiamonds(runDiamonds);
        }

        lootDisplay.setAmounts(runGold, runDiamonds);
        lootDisplay.setTime(time);

        double equippedMult = getEquippedMultiplierValue();
        lootDisplay.setMultiplierAndDifficulty(equippedMult, difficulty.getDisplayName());
    }

    // handleRunnerDied - Called when a chaser catches the runner
    public void handleRunnerDied() {
        if (gameOver) return;
        gameOver = true;

        gamePanel.stopMovement();
        showEndOfRunDialog(false);
    }

    // ---------- END-OF-RUN DIALOG ----------

    // showEndOfRunDialog - Builds and shows the run summary dialog
    private void showEndOfRunDialog(boolean escaped) {
        int accountMultIndex = (currentAccount != null) ? currentAccount.getMultiplier() : -1;

        SessionResult result = scoreSystem.compute(
                session,
                difficulty,
                escaped,
                accountMultIndex
        );

        // compute score components
        int timeGold = result.getTimeGold();
        int pickupGold = result.getPickupGold();
        int baseGold = result.getBaseGold();

        int multiplier = result.getMultiplier().asInt();
        int finalGold = result.getFinalGold();

        double timeSec = result.getTimeSeconds();
        String timeStr = formatTime(timeSec);

        int pickupDiamonds = result.getDiamondsCollected();
        int finalDiamonds = result.getFinalDiamonds();

        // update Account with final rewards
        if (currentAccount != null) {
            double oldBest = currentAccount.getBestTime();
            if (timeSec > oldBest) {
                currentAccount.setBestTime(timeSec);
            }

            int vaultGold = currentAccount.getVaultGold();
            int freeGold = currentAccount.getFreeGold();

            int vaultDiamonds = currentAccount.getVaultDiamonds();
            int freeDiamonds = currentAccount.getFreeDiamonds();

            freeGold = 0;
            freeDiamonds = 0;

            vaultGold += finalGold;
            vaultDiamonds += finalDiamonds;

            currentAccount.setVaultGold(vaultGold);
            currentAccount.setFreeGold(freeGold);
            currentAccount.setVaultDiamonds(vaultDiamonds);
            currentAccount.setFreeDiamonds(freeDiamonds);
        }

        String titleText = escaped ? "You Escaped!" : "You Were Caught!";

        JDialog dialog = new JDialog(this, "Run Summary", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        Color bgColor = new Color(30, 30, 30);

        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(true);
        content.setBackground(bgColor);

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(GameFonts.get(28f, Font.BOLD));
        titleLabel.setForeground(Color.WHITE);

        JLabel timeLabel = new JLabel("Time survived: " + timeStr);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setFont(GameFonts.get(20f, Font.PLAIN));
        timeLabel.setForeground(Color.WHITE);

        // equation block using JTextPane so some lines can be colored
        JTextPane eqPane = new JTextPane();
        eqPane.setEditable(false);
        eqPane.setOpaque(false);
        eqPane.setFont(GameFonts.get(20f, Font.PLAIN));
        eqPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        eqPane.setHighlighter(null);

        StyledDocument doc = eqPane.getStyledDocument();

        Style normal = eqPane.addStyle("normal", null);
        StyleConstants.setForeground(normal, Color.WHITE);

        Style goldStyle = eqPane.addStyle("gold", normal);
        StyleConstants.setForeground(goldStyle, new Color(255, 215, 0));

        Style diamondStyle = eqPane.addStyle("diamond", normal);
        StyleConstants.setForeground(diamondStyle, new Color(150, 220, 255));

        try {
            insertLine(doc, String.format("  Time Gold       %4d", timeGold), normal);
            insertLine(doc, "", normal);
            insertLine(doc, String.format("+ Pickup Gold     %4d", pickupGold), normal);
            insertLine(doc, "", normal);
            insertLine(doc, "-------------------------", normal);
            insertLine(doc, "", normal);
            insertLine(doc, String.format("  Base Gold       %4d", baseGold), normal);
            insertLine(doc, "", normal);
            insertLine(doc, String.format("× Multiplier      %4d", multiplier), normal);
            insertLine(doc, "", normal);
            insertLine(doc, "-------------------------", normal);
            insertLine(doc, "", normal);
            insertLine(doc, String.format("  Final Gold      %4d", finalGold), goldStyle);
            insertLine(doc, "", normal);
            insertLine(doc, "", normal);
            insertLine(doc, String.format("  Diamonds        %4d", pickupDiamonds), normal);
            insertLine(doc, "", normal);
            insertLine(doc, String.format("× Multiplier      %4d", multiplier), normal);
            insertLine(doc, "", normal);
            insertLine(doc, "-------------------------", normal);
            insertLine(doc, "", normal);
            insertLine(doc, String.format("  Final Diamonds  %4d", finalDiamonds), diamondStyle);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        content.add(titleLabel);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(timeLabel);
        content.add(Box.createRigidArea(new Dimension(0, 16)));
        content.add(eqPane);
        content.add(Box.createRigidArea(new Dimension(0, 24)));

        RoundedHoverButton playAgainBtn = new RoundedHoverButton("Play Again");
        RoundedHoverButton menuBtn = new RoundedHoverButton("Return to Menu");

        Dimension buttonSize = new Dimension(380, 72);
        playAgainBtn.setPreferredSize(buttonSize);
        playAgainBtn.setMinimumSize(buttonSize);
        playAgainBtn.setMaximumSize(buttonSize);
        playAgainBtn.setFont(GameFonts.get(22f, Font.BOLD));
        playAgainBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        menuBtn.setPreferredSize(buttonSize);
        menuBtn.setMinimumSize(buttonSize);
        menuBtn.setMaximumSize(buttonSize);
        menuBtn.setFont(GameFonts.get(22f, Font.BOLD));
        menuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(playAgainBtn);
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(menuBtn);

        dialog.setContentPane(content);
        dialog.pack();

        int minW = 540;
        int minH = 520;
        int w = Math.max(dialog.getWidth(), minW);
        int h = Math.max(dialog.getHeight(), minH);
        dialog.setSize(w, h);

        dialog.setLocationRelativeTo(this);

        playAgainBtn.addActionListener(e -> {
            dialog.dispose();
            this.dispose();
            if (windowManager != null) {
                windowManager.showGameWindow();
            } else {
                new GameWindow(null, currentAccount);
            }
        });

        menuBtn.addActionListener(e -> {
            dialog.dispose();
            this.dispose();
            if (windowManager != null) {
                windowManager.showMainMenu();
            } else {
                new MainMenuWindow(null, currentAccount);
            }
        });

        dialog.setVisible(true);
    }

    // ---------- TEXT HELPERS ----------

    // formatTime - Formats seconds as mm:ss for display labels
    private String formatTime(double seconds) {
        int total = (int) Math.floor(seconds);
        int mins = total / 60;
        int secs = total % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    // insertLine - Appends a styled line plus newline to the summary document
    private void insertLine(StyledDocument doc, String text, Style style) throws BadLocationException {
        doc.insertString(doc.getLength(), text + "\n", style);
    }

    // ---------- MULTIPLIER HELPER ----------

    // getEquippedMultiplierValue - Returns the equipped account multiplier value (2,3,5,10) or 1
    private double getEquippedMultiplierValue() {
        if (currentAccount == null) {
            return 1.0;
        }
        int idx = currentAccount.getMultiplier();
        return MultiplierInfo.getValueForIndex(idx);
    }
}
