package game.ui.windows;

import game.account.*;
import game.world.*;
import game.gameplay.*;
import game.ui.*;
import game.scoring.*;
import game.settings.*;
import game.ui.components.panels.*;
import game.ui.theme.*;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private final WindowManager windowManager;
	private final Account currentAccount;
	private final AccountManager accountManager;
	private final ScoreSystem scoreSystem;
	private final Difficulty difficulty;
	private final Session session;
	private final GamePanel gamePanel;
	private final LootDisplayPanel lootDisplay;

	private boolean gameOver = false;

	public GameWindow(WindowManager windowManager, Account account) {
		super("Pixel Pursuit - Game");
		this.windowManager = windowManager;
		this.currentAccount = account;
		this.accountManager = windowManager.getAccountManager();
		this.scoreSystem = new ScoreSystem();
		this.difficulty = GameConfig.getCurrentDifficulty();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setResizable(false);

		// --- world / gameplay setup ---
		Maze maze = new Maze(); // 32x18 with basic layout
		Runner runner = new Runner(maze, maze.getEntranceX(), maze.getEntranceY());
		this.session = new Session(maze, runner);

		int chaserCount = GameConfig.getChaserCountForCurrentDifficulty();

		int spawnX = Math.max(1, maze.getExitX() - 3);
		int baseY = maze.getExitY();

		for (int i = 0; i < chaserCount; i++) {
			int dy = (i - (chaserCount - 1) / 2); // -1,0,1 pattern for 3; 0,1 for 2; etc.
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

		// --- background frame art ---
		BackgroundPanel mainPanel = new BackgroundPanel("/game/resources/images/gameBackground.png");
		mainPanel.setLayout(new BorderLayout());
		add(mainPanel);

		// ---------- LOOT DISPLAY ----------
		JPanel topBar = new JPanel(new BorderLayout());
		topBar.setOpaque(false);

		this.lootDisplay = new LootDisplayPanel(
				0, // starting run gold
				0, // starting run diamonds
				0.0 // time
		);

		JPanel rightBox = new JPanel();
		rightBox.setOpaque(false);
		rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.X_AXIS));
		rightBox.add(Box.createHorizontalGlue());
		rightBox.add(lootDisplay);

		topBar.add(rightBox, BorderLayout.EAST);
		mainPanel.add(topBar, BorderLayout.NORTH);

		// --- center game panel ---
		this.gamePanel = new GamePanel(session, this); // <--- NOTE: no type here
		mainPanel.add(gamePanel, BorderLayout.CENTER);

		setVisible(true);
		SwingUtilities.invokeLater(gamePanel::requestFocusInWindow);
	}

	public Session getSession() {
		return session;
	}

	/**
	 * Called by GamePanel when the runner reaches the exit cell. Moves freeGold ->
	 * vaultGold, saves the account, and returns to main menu.
	 */
	public void handleRunnerReachedExit() {
		if (gameOver)
			return;
		gameOver = true;

		gamePanel.stopMovement();

		showEndOfRunDialog(/* escaped = */ true);
	}

	public void updateHudFromSession() {
		if (lootDisplay == null)
			return;

		int runGold = session.getRunGold();
		int runDiamonds = session.getPickupDiamonds();
		double time = session.getElapsedTimeSeconds();

		if (currentAccount != null) {
			// Keep "free" balances in sync with what you're carrying this run
			currentAccount.setFreeGold(runGold);
			currentAccount.setFreeDiamonds(runDiamonds);
		}

		// In GAME context, LootDisplayPanel expects (gold, diamonds)
		lootDisplay.setAmounts(runGold, runDiamonds);
		lootDisplay.setTime(time);
	}

	public void handleRunnerDied() {
		if (gameOver)
			return;
		gameOver = true;

		gamePanel.stopMovement();

		showEndOfRunDialog(/* escaped = */ false);
	}

	private void showEndOfRunDialog(boolean escaped) {
		SessionResult result = scoreSystem.compute(session, difficulty, escaped);

		// --- compute score components ---
		int timeGold = result.getTimeGold(); // from surviving
		int pickupGold = result.getPickupGold(); // from gold on the map
		int baseGold = result.getBaseGold();

		int multiplier = result.getMultiplier().asInt();
		int finalGold = result.getFinalGold();

		double timeSec = result.getTimeSeconds();
		String timeStr = formatTime(timeSec);

		int pickupDiamonds = result.getDiamondsCollected();
		int finalDiamonds   = result.getFinalDiamonds();

		// --- update Account with finalGold ---
		if (currentAccount != null) {
			double oldBest = currentAccount.getBestTime();
			if (timeSec > oldBest) {
				currentAccount.setBestTime(timeSec);
			}

			int vaultGold = currentAccount.getVaultGold();
			int freeGold = currentAccount.getFreeGold();

			int vaultDiamonds = currentAccount.getVaultDiamonds();
			int freeDiamonds = currentAccount.getFreeDiamonds();

			// run is over – free loot resets
			freeGold = 0;
			freeDiamonds = 0;

			// add rewards to vault
			vaultGold += finalGold; // gold reward from score system
			vaultDiamonds += finalDiamonds;

			currentAccount.setVaultGold(vaultGold);
			currentAccount.setFreeGold(freeGold);
			currentAccount.setVaultDiamonds(vaultDiamonds);
			currentAccount.setFreeDiamonds(freeDiamonds);
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
		        "  Time Gold       %4d%n%n" +
		        "+ Pickup Gold     %4d%n%n" +
		        "-------------------------%n%n" +
		        "  Base Gold       %4d%n%n" +
		        "× Multiplier      %4d%n%n" +
		        "-------------------------%n%n" +
		        "  Final Gold      %4d%n%n" +
		        "%n" +
		        "  Diamonds        %4d%n%n" +
		        "× Multiplier      %4d%n%n" +
		        "-------------------------%n%n" +
		        "  Final Diamonds  %4d",
		        timeGold, pickupGold, baseGold, multiplier, finalGold,
		        pickupDiamonds, multiplier, finalDiamonds
		);

		JTextArea eqArea = new JTextArea(eqText);
		eqArea.setEditable(false);
		eqArea.setOpaque(false);
		eqArea.setForeground(Color.WHITE);
		eqArea.setFont(GameFonts.get(20f, Font.PLAIN)); // uses your game font
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
		EndButton menuBtn = new EndButton("Return to Menu");

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
			if (windowManager != null) {
				windowManager.showGameWindow(); // new run via manager
			} else {
				new GameWindow(null, currentAccount); // fallback if ever needed
			}
		});

		menuBtn.addActionListener(e -> {
			dialog.dispose();
			this.dispose();
			if (windowManager != null) {
				windowManager.showMainMenu();
			} else {
				new MainMenuWindow(null, currentAccount); // fallback
			}
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
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

			float scale = getModel().isRollover() ? 1.0f : IDLE_SCALE;

			int scaledW = (int) (w * scale);
			int scaledH = (int) (h * scale);
			int x = (w - scaledW) / 2;
			int y = (h - scaledH) / 2;

			Color fill = Color.WHITE;
			Color border = fill.darker();

			g2.setColor(fill);
			g2.fillRoundRect(x, y, scaledW - 1, scaledH - 1, ARC, ARC);

			g2.setColor(border);
			g2.setStroke(new BasicStroke(3f));
			g2.drawRoundRect(x + 1, y + 1, scaledW - 3, scaledH - 3, ARC, ARC);

			// text
			FontMetrics fm = g2.getFontMetrics(getFont());
			String text = getText();
			int textWidth = fm.stringWidth(text);
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
