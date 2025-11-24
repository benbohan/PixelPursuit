package game.ui.windows;

import game.account.*;
import game.world.*;
import game.gameplay.*;
import game.ui.*;
import game.ui.components.panels.BackgroundPanel;
import game.ui.components.panels.GamePanel;
import game.ui.components.panels.GoldDisplayPanel;
import game.ui.theme.GameFonts;

import javax.swing.*;
import java.awt.*;

/**
 * Top-level game window for a run of Pixel Pursuit.
 */
public class GameWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private final Account currentAccount;
    private final AccountManager accountManager;
    private final Session session;
    private final GamePanel gamePanel;
    private final GoldDisplayPanel hud;

    private boolean gameOver = false;

    public GameWindow(Account account) {
        super("Pixel Pursuit - Game");
        this.currentAccount = account;
        this.accountManager = new AccountManager();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        // --- world / gameplay setup ---
        Maze maze = new Maze();  // 32x18 with basic layout
        Runner runner = new Runner(maze, maze.getEntranceX(), maze.getEntranceY());
        this.session = new Session(maze, runner);

        int chaserStartX = Math.max(1, maze.getExitX() - 3);
        int chaserStartY = maze.getExitY();
        ChaserAI ai = new SimpleChaserAI();
        Chaser chaser1 = new Chaser(maze, chaserStartX, chaserStartY, ai);
        Chaser chaser2 = new Chaser(maze, chaserStartX, chaserStartY, ai);
        session.addChaser(chaser1);
        session.addChaser(chaser2);
        
        // --- background frame art ---
        BackgroundPanel mainPanel =
                new BackgroundPanel("/game/resources/images/gameBackground.png");
        mainPanel.setLayout(new BorderLayout());
        add(mainPanel);
        
        // --- HUD (vault + gold + time) in top-right ---
        int vault = (currentAccount != null) ? currentAccount.getVaultGold() : 0;
        int runGold = 0;
        hud = new GoldDisplayPanel(vault, runGold);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(hud, BorderLayout.EAST);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // --- center game panel ---
        this.gamePanel = new GamePanel(session, this);   // <--- NOTE: no type here
        mainPanel.add(gamePanel, BorderLayout.CENTER);

        setVisible(true);
        SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
    }

    public Session getSession() {
        return session;
    }

    /**
     * Called by GamePanel when the runner reaches the exit cell.
     * Moves freeGold -> vaultGold, saves the account, and returns to main menu.
     */
    public void handleRunnerReachedExit() {
        if (gameOver) return;
        gameOver = true;

        gamePanel.stopMovement();

        showEndOfRunDialog(/* escaped = */ true);
    }
    
    public void updateHudFromSession() {
        if (hud == null) return;

        int runGold = session.getRunGold();
        double time = session.getElapsedTimeSeconds();

        int vault = 0;
        if (currentAccount != null) {
            // Keep Account.freeGold in sync with run gold
            currentAccount.setFreeGold(runGold);
            vault = currentAccount.getVaultGold();
        }

        hud.setAmounts(vault, runGold);
        hud.setTime(time);
    }
    
    public void handleRunnerDied() {
        if (gameOver) return;
        gameOver = true;

        gamePanel.stopMovement();

        showEndOfRunDialog(/* escaped = */ false);
    }
    
    private void showEndOfRunDialog(boolean escaped) {
        // --- compute score components ---
        int timeGold    = session.getTimeGold();    // from surviving
        int pickupGold  = session.getPickupGold();  // from gold on the map
        int baseGold    = timeGold + pickupGold;

        int multiplier  = escaped ? 1 : 0;          // later: 2 for harder AIs
        int finalGold   = baseGold * multiplier;

        double timeSec  = session.getElapsedTimeSeconds();
        String timeStr  = formatTime(timeSec);

        // --- update Account with finalGold ---
        if (currentAccount != null) {
            double oldBest = currentAccount.getBestTime();
            if (timeSec > oldBest) {
                currentAccount.setBestTime(timeSec);
            }
        	
            int vault = currentAccount.getVaultGold();
            int free  = currentAccount.getFreeGold(); // mirrors run gold during game

            free = 0;                     // run is over, free gold resets
            vault += finalGold;           // if dead, finalGold = 0, so vault unchanged

            currentAccount.setVaultGold(vault);
            currentAccount.setFreeGold(free);
            accountManager.updateAccount(currentAccount);
        }

        // --- build dialog UI ---
        String titleText = escaped ? "You Escaped!" : "You Were Caught!";

        JDialog dialog = new JDialog(this, "Run Summary", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Dark background to match game board
        Color bgColor = new Color(30, 30, 30);

        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(true);
        content.setBackground(bgColor);

        // Title
        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(GameFonts.get(28f, Font.BOLD));
        titleLabel.setForeground(Color.WHITE);

        // Time label
        JLabel timeLabel = new JLabel("Time survived: " + timeStr);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeLabel.setFont(GameFonts.get(20f, Font.PLAIN));
        timeLabel.setForeground(Color.WHITE);

        // --- Vertical equation using JTextArea with GameFonts (no code font) ---
        String eqText = String.format(
                "  Time Gold    %4d%n%n" +
                "+ Pickup Gold  %4d%n%n" +
                "------------------%n%n" +
                "  Base Gold    %4d%n%n" +
                "× Multiplier   %4d%n%n" +
                "------------------%n%n" +
                "  Final Gold   %4d",
                timeGold, pickupGold, baseGold, multiplier, finalGold
        );

        JTextArea eqArea = new JTextArea(eqText);
        eqArea.setEditable(false);
        eqArea.setOpaque(false);
        eqArea.setForeground(Color.WHITE);
        eqArea.setFont(GameFonts.get(20f, Font.PLAIN));  // uses your game font
        eqArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        eqArea.setHighlighter(null); // no selection highlight

        content.add(titleLabel);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(timeLabel);
        content.add(Box.createRigidArea(new Dimension(0, 16)));
        content.add(eqArea);
        content.add(Box.createRigidArea(new Dimension(0, 24)));

        // --- Buttons: Play Again / Return to Menu stacked and bigger ---
        EndButton playAgainBtn = new EndButton("Play Again");
        EndButton menuBtn      = new EndButton("Return to Menu");

        // Bigger & same width
        Dimension buttonSize = new Dimension(380, 72);
        playAgainBtn.setPreferredSize(buttonSize);
        playAgainBtn.setMinimumSize(buttonSize);
        playAgainBtn.setMaximumSize(buttonSize);

        menuBtn.setPreferredSize(buttonSize);
        menuBtn.setMinimumSize(buttonSize);
        menuBtn.setMaximumSize(buttonSize);

        playAgainBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(playAgainBtn);
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(menuBtn);

        dialog.setContentPane(content);
        dialog.pack();

        // Make the window a bit taller and not too skinny
        int minW = 540;
        int minH = 520;
        int w = Math.max(dialog.getWidth(), minW);
        int h = Math.max(dialog.getHeight(), minH);
        dialog.setSize(w, h);

        dialog.setLocationRelativeTo(this);

        // Button actions
        playAgainBtn.addActionListener(e -> {
            dialog.dispose();
            this.dispose();
            new GameWindow(currentAccount);  // start a fresh run
        });

        menuBtn.addActionListener(e -> {
            dialog.dispose();
            this.dispose();
            new MainMenuWindow(currentAccount);
        });

        dialog.setVisible(true);
    }


    
    private String formatTime(double seconds) {
        int total = (int) Math.floor(seconds);
        int mins = total / 60;
        int secs = total % 60;
        return String.format("%02d:%02d", mins, secs);
    }
    
    /** White rounded button, styled similar to your main UI buttons. */
    private static class EndButton extends JButton {
        private static final long serialVersionUID = 1L;
        private static final int ARC = 22;
        private static final float IDLE_SCALE = 0.95f;

        public EndButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);

            // slightly larger font for big buttons
            setFont(GameFonts.get(22f, Font.BOLD));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            float scale = getModel().isRollover() ? 1.0f : IDLE_SCALE;

            int scaledW = (int) (w * scale);
            int scaledH = (int) (h * scale);
            int x = (w - scaledW) / 2;
            int y = (h - scaledH) / 2;

            Color fill   = Color.WHITE;
            Color border = fill.darker();

            g2.setColor(fill);
            g2.fillRoundRect(x, y, scaledW - 1, scaledH - 1, ARC, ARC);

            g2.setColor(border);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(x + 1, y + 1, scaledW - 3, scaledH - 3, ARC, ARC);

            // text
            FontMetrics fm = g2.getFontMetrics(getFont());
            String text = getText();
            int textWidth  = fm.stringWidth(text);
            int textHeight = fm.getAscent();
            int tx = (w - textWidth) / 2;
            int ty = (h + textHeight) / 2 - fm.getDescent();

            g2.setColor(Color.BLACK);
            g2.drawString(text, tx, ty);

            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // border already painted in paintComponent
        }
    }



}
