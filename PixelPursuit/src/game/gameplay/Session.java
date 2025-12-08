package game.gameplay;

import game.world.Cell;
import game.world.Maze;
import game.settings.GameConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * One running game session:
 *  - Tracks elapsed time, survival gold, pickup gold, and diamond pickups.
 *  - Owns the maze, runner, and all chasers for this single run.
 *  - Handles per-tick updates: survival gold, chaser movement, loot spawns, and collisions.
 */
public class Session {

    // ---------- FIELDS ----------

    private final Maze maze;
    private final Runner runner;
    private final List<Chaser> chasers = new ArrayList<>();

    private double elapsedTimeSeconds = 0.0;
    private boolean running = true;

    // Gold breakdown
    private int runGold = 0;        // total = timeGold + pickupGold
    private int timeGold = 0;       // from surviving
    private int pickupGold = 0;     // from gold on the map
    private int pickupDiamonds = 0; // number of diamond pickups

    // Chaser movement pacing
    private double chaserMoveAccumulator = 0.0;
    // values pulled from GameConfig to keep magic numbers in one place
    private static final double CHASER_MOVE_INTERVAL =
            GameConfig.CHASER_MOVE_INTERVAL_SEC; // seconds per step

    // Gold spawning pacing
    private double goldSpawnAccumulator = 0.0;
    private final double goldSpawnInterval;
    private final double diamondChance;

    // Survival gold pacing
    private double survivalGoldAccumulator = 0.0;
    private static final double SURVIVAL_GOLD_INTERVAL =
            GameConfig.SURVIVAL_GOLD_INTERVAL_SEC; // seconds between survival ticks
    private static final int SURVIVAL_GOLD_PER_TICK =
            GameConfig.SURVIVAL_GOLD_PER_TICK;     // base gold per survival tick

    private final Random rng = new Random();

    // ---------- CONSTRUCTORS ----------

    // Session - Creates a new game session using the given maze and runner
    public Session(Maze maze, Runner runner) {
        this.maze = maze;
        this.runner = runner;

        this.goldSpawnInterval = GameConfig.getGoldSpawnIntervalForCurrentDifficulty();
        this.diamondChance     = GameConfig.getDiamondChanceForCurrentDifficulty();
    }

    // ---------- ACCESSORS / INFO ----------

    // getMaze - Returns the maze used for this session
    public Maze getMaze() {
        return maze;
    }

    // getRunner - Returns the runner controlled in this session
    public Runner getRunner() {
        return runner;
    }

    // getChasers - Returns an unmodifiable view of all chasers in this session
    public List<Chaser> getChasers() {
        return Collections.unmodifiableList(chasers);
    }

    // addChaser - Registers a new chaser with this session
    public void addChaser(Chaser chaser) {
        if (chaser != null) {
            chasers.add(chaser);
        }
    }

    // getElapsedTimeSeconds - Returns the total elapsed time in seconds
    public double getElapsedTimeSeconds() {
        return elapsedTimeSeconds;
    }

    // getRunGold - Returns total run gold (time + pickups)
    public int getRunGold() {
        return runGold;
    }

    // getTimeGold - Returns gold earned purely from survival time
    public int getTimeGold() {
        return timeGold;
    }

    // getPickupGold - Returns gold earned from pickups on the map
    public int getPickupGold() {
        return pickupGold;
    }

    // getPickupDiamonds - Returns the number of diamonds collected this run
    public int getPickupDiamonds() {
        return pickupDiamonds;
    }

    // isRunning - Returns true while the session is active
    public boolean isRunning() {
        return running;
    }

    // endSession - Stops the session so update() no longer advances the game
    public void endSession() {
        running = false;
    }

    // isRunnerAtExit - Returns true if the runner is currently on the exit cell
    public boolean isRunnerAtExit() {
        return runner.getX() == maze.getExitX() && runner.getY() == maze.getExitY();
    }

    // ---------- TICK / UPDATE LOOP ----------

    // update - Advances the session by deltaSeconds; called once per tick
    public void update(double deltaSeconds) {
        if (!running) return;

        // 1) Time
        elapsedTimeSeconds += deltaSeconds;

        // 2) Survival gold (time-based)
        survivalGoldAccumulator += deltaSeconds;
        if (survivalGoldAccumulator >= SURVIVAL_GOLD_INTERVAL) {
            int ticks = (int) (survivalGoldAccumulator / SURVIVAL_GOLD_INTERVAL);
            survivalGoldAccumulator -= ticks * SURVIVAL_GOLD_INTERVAL;

            int gained = ticks * SURVIVAL_GOLD_PER_TICK;
            timeGold += gained;
            runGold  += gained;
        }

        // 3) Pickup gold/diamonds if runner is standing on them
        Cell rc = maze.getCell(runner.getX(), runner.getY());
        if (rc.hasGold()) {
            int amount = rc.takeGold(); // clears gold in the cell
            pickupGold += amount;
            runGold    += amount;
        }
        if (rc.hasDiamond()) {
            rc.takeDiamond();
            pickupDiamonds++;

            int value = GameConfig.DIAMOND_GOLD_VALUE;
            pickupGold += value;   // treat as bonus score
            runGold    += value;
        }

        // 4) Move chasers at a slower rate
        chaserMoveAccumulator += deltaSeconds;
        if (chaserMoveAccumulator >= CHASER_MOVE_INTERVAL) {
            chaserMoveAccumulator -= CHASER_MOVE_INTERVAL;

            for (Chaser chaser : chasers) {
                chaser.update(this);
            }
        }

        // 5) Spawn random loot occasionally
        goldSpawnAccumulator += deltaSeconds;
        if (goldSpawnAccumulator >= goldSpawnInterval) {
            goldSpawnAccumulator -= goldSpawnInterval;
            spawnRandomLoot();
        }

        // 6) Collision: chaser on runner?
        for (Chaser chaser : chasers) {
            if (chaser.getX() == runner.getX() && chaser.getY() == runner.getY()) {
                runner.kill();
                running = false;
                break;
            }
        }
    }

    // ---------- LOOT SPAWNING ----------

    // isOuterRing - Returns true if (x, y) is in the outer ring of the maze
    private boolean isOuterRing(int x, int y, int w, int h) {
        int marginX = 4; // columns near left/right edges
        int marginY = 3; // rows near top/bottom

        return (x < marginX || x >= w - marginX ||
                y < marginY || y >= h - marginY);
    }

    // spawnRandomLoot - Spawns gold or a diamond in a random walkable cell
    private void spawnRandomLoot() {
        int w = maze.getWidth();
        int h = maze.getHeight();

        for (int tries = 0; tries < 100; tries++) {
            int x = rng.nextInt(w);
            int y = rng.nextInt(h);

            // Skip borders and entrance/exit
            if (x == 0 || x == w - 1 || y == 0 || y == h - 1) continue;
            if (x == maze.getEntranceX() && y == maze.getEntranceY()) continue;
            if (x == maze.getExitX() && y == maze.getExitY())         continue;

            Cell c = maze.getCell(x, y);
            if (!c.isWalkable())              continue;
            if (c.hasGold() || c.hasDiamond()) continue;

            boolean outer = isOuterRing(x, y, w, h);

            // 60% of the time we insist on an outer-ring tile
            if (rng.nextDouble() < 0.60 && !outer) {
                continue;
            }

            // Decide whether this spawn is a diamond.
            // Diamonds only spawn on outer ring to really reward exploration.
            boolean wantDiamond = (rng.nextDouble() < diamondChance);
            if (wantDiamond) {
                c.setDiamond(true);
            } else {
                c.setGold(1);
            }
            break;
        }
    }
}
