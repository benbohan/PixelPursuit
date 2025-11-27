package game.gameplay;

import game.world.Cell;
import game.world.Maze;
import game.settings.GameConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * One running game session. Tracks elapsed time, run gold, time-based gold,
 * pickup-based gold, chasers, and random gold spawns.
 */
public class Session {

	private final Maze maze;
	private final Runner runner;
	private final List<Chaser> chasers = new ArrayList<>();

	private double elapsedTimeSeconds = 0.0;
	private boolean running = true;

	// Gold breakdown
	private int runGold = 0; // total = timeGold + pickupGold
	private int timeGold = 0; // from surviving
	private int pickupGold = 0; // from gold on the map

	// Chaser movement pacing
	private double chaserMoveAccumulator = 0.0;
	// values pulled from GameConfig to keep magic numbers in one place
	private static final double CHASER_MOVE_INTERVAL = GameConfig.CHASER_MOVE_INTERVAL_SEC; // seconds per step

	// Gold spawning pacing
	private double goldSpawnAccumulator = 0.0;
	private static final double GOLD_SPAWN_INTERVAL = GameConfig.GOLD_SPAWN_INTERVAL_SEC; // seconds between spawns

	// Survival gold pacing
	private double survivalGoldAccumulator = 0.0;
	private static final double SURVIVAL_GOLD_INTERVAL = GameConfig.SURVIVAL_GOLD_INTERVAL_SEC; // 1 sec
	private static final int SURVIVAL_GOLD_PER_TICK = GameConfig.SURVIVAL_GOLD_PER_TICK; // base 1 gold per second

	private final Random rng = new Random();

	public Session(Maze maze, Runner runner) {
		this.maze = maze;
		this.runner = runner;
	}

	public Maze getMaze() {
		return maze;
	}

	public Runner getRunner() {
		return runner;
	}

	public List<Chaser> getChasers() {
		return Collections.unmodifiableList(chasers);
	}

	public void addChaser(Chaser chaser) {
		if (chaser != null) {
			chasers.add(chaser);
		}
	}

	public double getElapsedTimeSeconds() {
		return elapsedTimeSeconds;
	}

	/** Total gold this run (time + pickups). */
	public int getRunGold() {
		return runGold;
	}

	/** Gold earned purely from surviving. */
	public int getTimeGold() {
		return timeGold;
	}

	/** Gold earned purely from pickups. */
	public int getPickupGold() {
		return pickupGold;
	}

	public boolean isRunning() {
		return running;
	}

	public void endSession() {
		running = false;
	}

	public boolean isRunnerAtExit() {
		return runner.getX() == maze.getExitX() && runner.getY() == maze.getExitY();
	}

	/**
	 * Advance the game by deltaSeconds. Called from GamePanel's timer every tick.
	 */
	public void update(double deltaSeconds) {
		if (!running)
			return;

		// --- 1) Time ---
		elapsedTimeSeconds += deltaSeconds;

		// --- 2) Survival gold (time-based) ---
		survivalGoldAccumulator += deltaSeconds;
		if (survivalGoldAccumulator >= SURVIVAL_GOLD_INTERVAL) {
			int ticks = (int) (survivalGoldAccumulator / SURVIVAL_GOLD_INTERVAL);
			survivalGoldAccumulator -= ticks * SURVIVAL_GOLD_INTERVAL;

			int gained = ticks * SURVIVAL_GOLD_PER_TICK;
			timeGold += gained;
			runGold += gained;
		}

		// --- 3) Pickup gold if runner is standing on it ---
		Cell rc = maze.getCell(runner.getX(), runner.getY());
		if (rc.hasGold()) {
			int amount = rc.takeGold(); // should clear gold in the cell
			pickupGold += amount;
			runGold += amount;
		}

		// --- 4) Move chasers at a slower rate ---
		chaserMoveAccumulator += deltaSeconds;
		if (chaserMoveAccumulator >= CHASER_MOVE_INTERVAL) {
			chaserMoveAccumulator -= CHASER_MOVE_INTERVAL;

			for (Chaser chaser : chasers) {
				chaser.update(this);
			}
		}

		// --- 5) Spawn random gold occasionally ---
		goldSpawnAccumulator += deltaSeconds;
		if (goldSpawnAccumulator >= GOLD_SPAWN_INTERVAL) {
			goldSpawnAccumulator -= GOLD_SPAWN_INTERVAL;
			spawnRandomGold();
		}

		// --- 6) Collision: chaser on runner? ---
		for (Chaser chaser : chasers) {
			if (chaser.getX() == runner.getX() && chaser.getY() == runner.getY()) {
				runner.kill();
				running = false;
				break;
			}
		}
	}

	/** Spawn a single gold piece in a random walkable cell. */
	private void spawnRandomGold() {
		int w = maze.getWidth();
		int h = maze.getHeight();

		// Try up to 100 random spots per spawn
		for (int tries = 0; tries < 100; tries++) {
			int x = rng.nextInt(w);
			int y = rng.nextInt(h);

			// Skip borders and entrance/exit
			if (x == 0 || x == w - 1 || y == 0 || y == h - 1)
				continue;
			if (x == maze.getEntranceX() && y == maze.getEntranceY())
				continue;
			if (x == maze.getExitX() && y == maze.getExitY())
				continue;

			Cell c = maze.getCell(x, y);
			if (!c.isWalkable())
				continue;
			if (c.hasGold())
				continue;

			c.setGold(1); // one gold for now
			break;
		}
	}
}
