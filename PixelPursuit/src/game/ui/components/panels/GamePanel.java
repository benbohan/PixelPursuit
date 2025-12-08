package game.ui.components.panels;

import game.gameplay.*;
import game.ui.theme.UiColors;
import game.ui.windows.GameWindow;
import game.world.*;
import game.cosmetics.*;
import game.settings.GameConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

/**
 * GamePanel - Draws the maze and runner, handles keyboard input,
 * and drives movement with a Swing timer.
 */
public class GamePanel extends JPanel implements KeyListener {

    // ---------- FIELDS ----------

    private static final long serialVersionUID = 1L;

    private final Session session;
    private final Maze maze;
    private final Runner runner;
    private final GameWindow window;

    private Image stoneOriginal;
    private Image stoneScaled;
    private int stoneForCellSize = -1;
    private int stoneDrawSize = 0;

    private Image goldOriginal;
    private Image goldScaled;
    private int goldForCellSize = -1;
    private int goldDrawSize = 0;

    private Image diamondOriginal;
    private Image diamondScaled;
    private int diamondForCellSize = -1;
    private int diamondDrawSize = 0;

    private final Timer movementTimer;
    private static final int MOVE_INTERVAL_MS = GameConfig.RUNNER_MOVE_INTERVAL_MS;

    // ---------- CONSTRUCTORS ----------

    // GamePanel - Wires session, loads textures, and starts the movement timer
    public GamePanel(Session session, GameWindow window) {
        this.session = session;
        this.maze = session.getMaze();
        this.runner = session.getRunner();
        this.window = window;

        setOpaque(false);
        setFocusable(true);
        addKeyListener(this);

        try {
            stoneOriginal = new ImageIcon(
                    GamePanel.class.getResource("/game/resources/images/stone.png")
            ).getImage();
        } catch (Exception e) {
            stoneOriginal = null;
        }

        try {
            goldOriginal = new ImageIcon(
                    GamePanel.class.getResource("/game/resources/images/gold.png")
            ).getImage();
        } catch (Exception e) {
            goldOriginal = null;
        }

        try {
            diamondOriginal = new ImageIcon(
                    GamePanel.class.getResource("/game/resources/images/diamond.png")
            ).getImage();
        } catch (Exception e) {
            diamondOriginal = null;
        }

        movementTimer = new Timer(MOVE_INTERVAL_MS, e -> {
            runner.step();

            double dt = MOVE_INTERVAL_MS / 1000.0;
            session.update(dt);

            window.updateHudFromSession();

            // Check exit first
            if (runner.isAlive()
                    && runner.getX() == maze.getExitX()
                    && runner.getY() == maze.getExitY()) {
                window.handleRunnerReachedExit();
                return;
            }

            // Then check death
            if (!runner.isAlive()) {
                window.handleRunnerDied();
                return;
            }

            repaint();
        });
        movementTimer.start();
    }

    // ---------- PUBLIC API ----------

    // stopMovement - Stops the movement timer when the run ends
    public void stopMovement() {
        movementTimer.stop();
    }

    // ---------- PAINTING ----------

    // paintComponent - Renders maze, pickups, chasers, and runner + cosmetic
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int cols = maze.getWidth();
        int rows = maze.getHeight();

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

        Color runnerColor  = (window != null)
                ? window.getRunnerColor()
                : UiColors.PLAYER_DEFAULT_GRAY;

        Color chaserColor  = UiColors.CHASER_DEFAULT;

        // Scale stone texture for walls
        if (stoneOriginal != null && cellSize > 0 && stoneForCellSize != cellSize) {
            stoneDrawSize = (int) Math.round(cellSize * 1.8);
            stoneScaled = stoneOriginal.getScaledInstance(
                    stoneDrawSize, stoneDrawSize, Image.SCALE_SMOOTH
            );
            stoneForCellSize = cellSize;
        }

        // Scale gold texture for pickups
        if (goldOriginal != null && cellSize > 0 && goldForCellSize != cellSize) {
            goldDrawSize = (int) Math.round(cellSize * 0.9);
            goldScaled = goldOriginal.getScaledInstance(
                    goldDrawSize, goldDrawSize, Image.SCALE_SMOOTH
            );
            goldForCellSize = cellSize;
        }

        // Scale diamond texture for diamond pickups
        if (diamondOriginal != null && cellSize > 0 && diamondForCellSize != cellSize) {
            diamondDrawSize = (int) Math.round(cellSize * 0.9);
            diamondScaled = diamondOriginal.getScaledInstance(
                    diamondDrawSize, diamondDrawSize, Image.SCALE_SMOOTH
            );
            diamondForCellSize = cellSize;
        }

        Shape oldClip = g2.getClip();

        // Draw maze cells and pickups
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Cell cell = maze.getCell(x, y);

                int px = offsetX + x * cellSize;
                int py = offsetY + y * cellSize;

                if (cell.isWall()) {
                    if (stoneScaled != null) {
                        g2.setClip(px, py, cellSize, cellSize);

                        int offset = (cellSize - stoneDrawSize) / 2;
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
                    g2.setColor(floorColor);
                    g2.fillRect(px, py, cellSize, cellSize);
                }

                if (x == maze.getEntranceX() && y == maze.getEntranceY()) {
                    g2.setColor(entranceCol);
                    g2.fillRect(px, py, cellSize, cellSize);
                } else if (x == maze.getExitX() && y == maze.getExitY()) {
                    g2.setColor(exitCol);
                    g2.fillRect(px, py, cellSize, cellSize);
                }

                if (!cell.isWall() && cell.hasGold() && goldScaled != null) {
                    int gx = px + (cellSize - goldDrawSize) / 2;
                    int gy = py + (cellSize - goldDrawSize) / 2;
                    g2.drawImage(goldScaled, gx, gy, null);
                }

                if (!cell.isWall() && cell.hasDiamond() && diamondScaled != null) {
                    int dx = px + (cellSize - diamondDrawSize) / 2;
                    int dy = py + (cellSize - diamondDrawSize) / 2;
                    g2.drawImage(diamondScaled, dx, dy, null);
                }

                g2.setColor(gridColor);
                g2.drawRect(px, py, cellSize, cellSize);
            }
        }

        // Draw chasers
        for (Chaser chaser : session.getChasers()) {
            int cx = offsetX + chaser.getX() * cellSize;
            int cy = offsetY + chaser.getY() * cellSize;

            int marginC = cellSize / 6;
            int sizeC   = cellSize - 2 * marginC;
            int arcC    = cellSize / 3;

            g2.setColor(chaserColor);
            g2.fillRoundRect(cx + marginC, cy + marginC, sizeC, sizeC, arcC, arcC);
        }

        // Draw runner
        int rx = offsetX + runner.getX() * cellSize;
        int ry = offsetY + runner.getY() * cellSize;
        int margin = cellSize / 6;
        int size = cellSize - 2 * margin;
        int arc = cellSize / 3;

        int bodyX = rx + margin;
        int bodyY = ry + margin;

        g2.setColor(runnerColor);
        g2.fillRoundRect(bodyX, bodyY, size, size, arc, arc);

        // Draw cosmetic on top of runner
        if (window != null) {
            int cosmeticId = window.getRunnerCosmeticId();
            if (cosmeticId >= 0) {
                BufferedImage cosImg = PlayerCosmetics.getCosmeticImage(cosmeticId);
                if (cosImg != null) {
                    double maxScale = 1.8;
                    int imgW = cosImg.getWidth();
                    int imgH = cosImg.getHeight();
                    double scale = maxScale * size / Math.max(imgW, imgH);

                    int drawW = (int) Math.round(imgW * scale);
                    int drawH = (int) Math.round(imgH * scale);

                    int cx = bodyX + (size - drawW) / 2
                            + PlayerCosmetics.getCosmeticOffsetX(cosmeticId);
                    int cy = bodyY + (size - drawH) / 2
                            + PlayerCosmetics.getCosmeticOffsetY(cosmeticId);

                    if (isHat(cosmeticId)) {
                        cy -= size / 3;
                    } else if (isGlasses(cosmeticId)) {
                        cy -= size / 8;
                    } else if (isPet(cosmeticId)) {
                        cx += size / 10;
                        cy += size / 10;
                    }

                    g2.drawImage(cosImg, cx, cy, drawW, drawH, null);
                }
            }
        }

        g2.dispose();
    }

    // ---------- KEY INPUT ----------

    // keyPressed - Handles WASD/arrow keys to set runner direction (space to stop)
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                runner.setDirection(0, -1);
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                runner.setDirection(0, 1);
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                runner.setDirection(-1, 0);
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                runner.setDirection(1, 0);
                break;
            case KeyEvent.VK_SPACE:
                runner.stop();
                break;
        }
    }

    // keyReleased - Unused but required by KeyListener
    @Override
    public void keyReleased(KeyEvent e) { }

    // keyTyped - Unused but required by KeyListener
    @Override
    public void keyTyped(KeyEvent e) { }

    // ---------- COSMETIC HELPERS ----------

    // isHat - Returns true if the cosmetic id is a hat-style item
    private boolean isHat(int id) {
        return id == PlayerCosmetics.COSMETIC_BEANIE
            || id == PlayerCosmetics.COSMETIC_CROWN
            || id == PlayerCosmetics.COSMETIC_TOP_HAT
            || id == PlayerCosmetics.COSMETIC_HEADPHONES
            || id == PlayerCosmetics.COSMETIC_GOLD_HAT
            || id == PlayerCosmetics.COSMETIC_DIAMOND_HAT;
    }

    // isGlasses - Returns true if the cosmetic id is a glasses-style item
    private boolean isGlasses(int id) {
        return id == PlayerCosmetics.COSMETIC_SHADES
            || id == PlayerCosmetics.COSMETIC_GOLD_SHADES
            || id == PlayerCosmetics.COSMETIC_DIAMOND_SHADES;
    }

    // isPet - Returns true if the cosmetic id is a pet-style item
    private boolean isPet(int id) {
        return id == PlayerCosmetics.COSMETIC_CAT_PET
            || id == PlayerCosmetics.COSMETIC_DOG_PET;
    }
}
