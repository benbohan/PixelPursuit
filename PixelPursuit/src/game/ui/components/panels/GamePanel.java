package game.ui.components.panels;

import game.gameplay.*;
import game.ui.theme.UiColors;
import game.ui.windows.GameWindow;
import game.world.*;
import game.cosmetics.*;
import game.settings.GameConfig;

import game.account.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Draws the maze and the runner, handles keyboard input,
 * and advances the runner over time using a Swing Timer.
 * Walls are textured with stone.png, gold tiles use gold.png.
 */
public class GamePanel extends JPanel implements KeyListener {

    private static final long serialVersionUID = 1L;

    private final Session session;
    private final Maze maze;
    private final Runner runner;
    private final GameWindow window;
    private Account account;

    // Stone texture for wall cells
    private Image stoneOriginal;
    private Image stoneScaled;
    private int stoneForCellSize = -1;   // cache based on cellSize
    private int stoneDrawSize = 0;       // actual size of scaled texture

    // Gold texture for gold tiles
    private Image goldOriginal;
    private Image goldScaled;
    private int goldForCellSize = -1;
    private int goldDrawSize = 0;
    
    // Diamond texture for diamond tiles
    private Image diamondOriginal;
    private Image diamondScaled;
    private int diamondForCellSize = -1;
    private int diamondDrawSize = 0;

    // Movement timer: smaller delay = faster glide
    private final Timer movementTimer;
    private static final int MOVE_INTERVAL_MS = GameConfig.RUNNER_MOVE_INTERVAL_MS; // tweak speed here

    public GamePanel(Session session, GameWindow window) {
        this.session = session;
        this.maze = session.getMaze();
        this.runner = session.getRunner();
        this.window = window;

        setOpaque(false);
        setFocusable(true);
        addKeyListener(this);

        // Load the stone texture (place stone.png in /game/resources/)
        try {
            stoneOriginal = new ImageIcon(
                    GamePanel.class.getResource("/game/resources/images/stone.png")
            ).getImage();
        } catch (Exception e) {
            stoneOriginal = null;
        }

        // Load the gold texture (place gold.png in /game/resources/)
        try {
            goldOriginal = new ImageIcon(
                    GamePanel.class.getResource("/game/resources/images/gold.png")
            ).getImage();
        } catch (Exception e) {
            goldOriginal = null;
        }
        
     // Load the diamond texture (diamond.png in /game/resources/images/)
        try {
            diamondOriginal = new ImageIcon(
                    GamePanel.class.getResource("/game/resources/images/diamond.png")
            ).getImage();
        } catch (Exception e) {
            diamondOriginal = null;
        }

        // Movement timer = our "clock"
        movementTimer = new Timer(MOVE_INTERVAL_MS, e -> {
            runner.step();

            double dt = MOVE_INTERVAL_MS / 1000.0;
            session.update(dt);

            window.updateHudFromSession();

            // Check exit first (if you reach it before being killed)
            if (runner.isAlive()
                    && runner.getX() == maze.getExitX()
                    && runner.getY() == maze.getExitY()) {
                window.handleRunnerReachedExit();
                return;
            }

            // If a chaser caught you, runner will be dead now
            if (!runner.isAlive()) {
                window.handleRunnerDied();
                return;
            }

            repaint();
        });
        movementTimer.start();
    }

    // Set the account for cosmetics / stats
    public void setAccount(Account account) {
        this.account = account;
    }

    /** Called by GameWindow when the run ends so we stop the timer. */
    public void stopMovement() {
        movementTimer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int cols = maze.getWidth();
        int rows = maze.getHeight();

        // Leave margin so we don't cover the frame decorations
        int marginX = getWidth()  / 10;
        int marginY = getHeight() / 8;

        int availableW = getWidth()  - 2 * marginX;
        int availableH = getHeight() - 2 * marginY;

        int cellW = availableW / cols;
        int cellH = availableH / rows;
        int cellSize = Math.max(1, Math.min(cellW, cellH));

        int mazePixelW = cols * cellSize;
        int mazePixelH = rows * cellSize;

        int offsetX = (getWidth()  - mazePixelW) / 2;
        int offsetY = (getHeight() - mazePixelH) / 2;

        Color floorColor   = UiColors.MAZE_FLOOR;
        Color wallFallback = UiColors.MAZE_WALL;
        Color gridColor    = UiColors.MAZE_GRID;
        Color entranceCol  = UiColors.MAZE_ENTRANCE;
        Color exitCol      = UiColors.MAZE_EXIT;

        Color runnerColor  = (account != null)
                ? PlayerCosmetics.getRunnerColor(account)
                : UiColors.PLAYER_DEFAULT_GRAY;

        Color chaserColor  = UiColors.CHASER_DEFAULT;

        // --- Scale stone texture to a larger size than the cell (zoomed in) ---
        if (stoneOriginal != null && cellSize > 0 && stoneForCellSize != cellSize) {
            stoneDrawSize = (int) Math.round(cellSize * 1.8);  // zoom factor
            stoneScaled = stoneOriginal.getScaledInstance(
                    stoneDrawSize, stoneDrawSize, Image.SCALE_SMOOTH
            );
            stoneForCellSize = cellSize;
        }

        // --- Scale gold texture to fit nicely in the cell ---
        if (goldOriginal != null && cellSize > 0 && goldForCellSize != cellSize) {
            goldDrawSize = (int) Math.round(cellSize * 0.9);   // 70% of cell
            goldScaled = goldOriginal.getScaledInstance(
                    goldDrawSize, goldDrawSize, Image.SCALE_SMOOTH
            );
            goldForCellSize = cellSize;
        }
        
     // --- Scale diamond texture similarly ---
        if (diamondOriginal != null && cellSize > 0 && diamondForCellSize != cellSize) {
            diamondDrawSize = (int) Math.round(cellSize * 0.9);
            diamondScaled = diamondOriginal.getScaledInstance(
                    diamondDrawSize, diamondDrawSize, Image.SCALE_SMOOTH
            );
            diamondForCellSize = cellSize;
        }

        // Draw cells
        Shape oldClip = g2.getClip();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Cell cell = maze.getCell(x, y);

                int px = offsetX + x * cellSize;
                int py = offsetY + y * cellSize;

                // --- walls (stone texture) ---
                if (cell.isWall()) {
                    if (stoneScaled != null) {
                        g2.setClip(px, py, cellSize, cellSize);

                        int offset = (cellSize - stoneDrawSize) / 2; // negative -> zoomed
                        g2.drawImage(stoneScaled,
                                px + offset, py + offset,
                                stoneDrawSize, stoneDrawSize,
                                null);

                        g2.setClip(oldClip);
                    } else {
                        g2.setColor(wallFallback);
                        g2.fillRect(px, py, cellSize, cellSize);
                    }
                } else {
                    // floor
                    g2.setColor(floorColor);
                    g2.fillRect(px, py, cellSize, cellSize);
                }

                // Entrance / exit highlight
                if (x == maze.getEntranceX() && y == maze.getEntranceY()) {
                    g2.setColor(entranceCol);
                    g2.fillRect(px, py, cellSize, cellSize);
                } else if (x == maze.getExitX() && y == maze.getExitY()) {
                    g2.setColor(exitCol);
                    g2.fillRect(px, py, cellSize, cellSize);
                }

                // --- GOLD: draw gold.png on floor cells that have gold ---
                if (!cell.isWall() && cell.hasGold() && goldScaled != null) {
                    int gx = px + (cellSize - goldDrawSize) / 2;
                    int gy = py + (cellSize - goldDrawSize) / 2;
                    g2.drawImage(goldScaled, gx, gy, null);
                }
                
                // DIAMOND
                if (!cell.isWall() && cell.hasDiamond() && diamondScaled != null) {
                    int dx = px + (cellSize - diamondDrawSize) / 2;
                    int dy = py + (cellSize - diamondDrawSize) / 2;
                    g2.drawImage(diamondScaled, dx, dy, null);
                }

                // Grid line
                g2.setColor(gridColor);
                g2.drawRect(px, py, cellSize, cellSize);
            }
        }

        // --- Draw chasers ---
        for (Chaser chaser : session.getChasers()) {
            int cx = offsetX + chaser.getX() * cellSize;
            int cy = offsetY + chaser.getY() * cellSize;

            int marginC = cellSize / 6;
            int sizeC   = cellSize - 2 * marginC;
            int arcC    = cellSize / 3;

            g2.setColor(chaserColor);
            g2.fillRoundRect(cx + marginC, cy + marginC, sizeC, sizeC, arcC, arcC);
        }

        // --- Draw runner as rounded square ---
        int rx = offsetX + runner.getX() * cellSize;
        int ry = offsetY + runner.getY() * cellSize;
        int margin = cellSize / 6;
        int size = cellSize - 2 * margin;
        int arc = cellSize / 3;

        g2.setColor(runnerColor);
        g2.fillRoundRect(rx + margin, ry + margin, size, size, arc, arc);

        g2.dispose();
    }

    // ---------- key controls (WASD + arrow keys) ----------
    // Set direction; timer handles actual movement.

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                runner.setDirection(0, -1); break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                runner.setDirection(0, 1); break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                runner.setDirection(-1, 0); break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                runner.setDirection(1, 0); break;

            // Optional: Space to STOP gliding
            case KeyEvent.VK_SPACE:
                runner.stop(); break;
        }
    }

    @Override public void keyReleased(KeyEvent e) { }
    @Override public void keyTyped(KeyEvent e) { }
}
