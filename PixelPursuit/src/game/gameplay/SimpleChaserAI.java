package game.gameplay;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import game.world.Cell;
import game.world.Maze;

/*
 * Chaser AI with two behaviors:
 *  - When far from the Runner, wander randomly.
 *  - When close enough, use BFS to follow the shortest path around walls.
 */
public class SimpleChaserAI implements ChaserAI {
	private static final int DETECTION_RADIUS = 7;
	private final Random rng = new Random();

	@Override
	public void update(Chaser chaser, Session session) {
		if (!chaser.isActive())
			return;

		Runner runner = session.getRunner();
		Maze maze = session.getMaze();

		int cx = chaser.getX();
		int cy = chaser.getY();
		int rx = runner.getX();
		int ry = runner.getY();

		int manhattan = Math.abs(cx - rx) + Math.abs(cy - ry);

		if (manhattan > DETECTION_RADIUS) {
			randomWalk(chaser, maze);
		} else {
			// Close enough: try to use a shortest path.
			boolean moved = moveAlongShortestPath(chaser, maze, cx, cy, rx, ry);
			if (!moved) {
				// No path found or something weird: fall back to random movement.
				randomWalk(chaser, maze);
			}
		}
	}

	private void randomWalk(Chaser chaser, Maze maze) {
		// 4-directional movement
		int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

		// Start from a random direction to avoid bias
		int startIndex = rng.nextInt(dirs.length);

		for (int i = 0; i < dirs.length; i++) {
			int[] d = dirs[(startIndex + i) % dirs.length];
			int dx = d[0];
			int dy = d[1];

			int nx = chaser.getX() + dx;
			int ny = chaser.getY() + dy;

			if (!maze.inBounds(nx, ny)) {
				continue;
			}
			Cell target = maze.getCell(nx, ny);
			if (!target.isWalkable()) {
				continue;
			}

			chaser.moveBy(dx, dy);
			return; // moved successfully
		}

		// If we get here, all neighbors were blocked
	}

	private boolean moveAlongShortestPath(Chaser chaser, Maze maze, int sx, int sy, int tx, int ty) {

		// If already on the runner, do nothing
		if (sx == tx && sy == ty) {
			return false;
		}

		int w = maze.getWidth();
		int h = maze.getHeight();

		boolean[][] visited = new boolean[h][w];
		int[][] parentX = new int[h][w];
		int[][] parentY = new int[h][w];

		// Initialize parents
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				parentX[y][x] = -1;
				parentY[y][x] = -1;
			}
		}

		Queue<int[]> queue = new ArrayDeque<>();
		queue.add(new int[] { sx, sy });
		visited[sy][sx] = true;

		// 4-directional neighbors
		int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

		boolean found = false;

		while (!queue.isEmpty()) {
			int[] cur = queue.remove();
			int cx = cur[0];
			int cy = cur[1];

			if (cx == tx && cy == ty) {
				found = true;
				break;
			}

			for (int[] d : dirs) {
				int nx = cx + d[0];
				int ny = cy + d[1];

				if (!maze.inBounds(nx, ny)) {
					continue;
				}
				if (visited[ny][nx]) {
					continue;
				}
				if (!maze.getCell(nx, ny).isWalkable()) {
					continue;
				}

				visited[ny][nx] = true;
				parentX[ny][nx] = cx;
				parentY[ny][nx] = cy;
				queue.add(new int[] { nx, ny });
			}
		}

		if (!found) {
			// No path to runner (should be rare in your layouts).
			return false;
		}

		// Reconstruct path backwards from target to source.
		int stepX = tx;
		int stepY = ty;

		// Walk back until the parent is the start cell (sx, sy).
		while (true) {
			int px = parentX[stepY][stepX];
			int py = parentY[stepY][stepX];

			// If the parent is the start cell, then (stepX, stepY) is our
			// first move from the start.
			if (px == sx && py == sy) {
				break;
			}

			// If we hit a dead end, bail out.
			if (px == -1 && py == -1) {
				return false;
			}

			stepX = px;
			stepY = py;
		}

		int moveDx = stepX - sx;
		int moveDy = stepY - sy;

		chaser.moveBy(moveDx, moveDy);
		return true;
	}
}
