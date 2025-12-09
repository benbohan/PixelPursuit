package game.gameplay;

import game.world.Maze;
import game.settings.GameConfig;
import game.world.Cell;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

/**
 * Chaser AI with two behaviors:
 *  - When far from the Runner, roam toward random targets across the map,
 *    biased toward cells that have been visited less often.
 *  - When close enough, use BFS to chase the Runner around walls.
 */
public class SimpleChaserAI implements ChaserAI {

    // ---------- FIELDS ----------

    // detectionRadius - Manhattan distance threshold to switch from roaming to direct chase
    private final int detectionRadius;

    // ROAM_PATH_LIFETIME - How long to keep a roam target before giving up
    private static final int ROAM_PATH_LIFETIME = 90;

    private final Random rng = new Random();

    // Persistent per-AI roam target
    private int roamTargetX = -1;
    private int roamTargetY = -1;
    private int roamStepsRemaining = 0;

    // visitCount[y][x] - How many times this chaser has stepped onto each cell
    private int[][] visitCount = null;

    // ---------- CONSTRUCTORS ----------

    // SimpleChaserAI - Uses difficulty config to set the detection radius
    public SimpleChaserAI() {
        this.detectionRadius = GameConfig.getDetectionRadiusForCurrentDifficulty();
    }

    // ---------- MAIN UPDATE ----------

    // update - Chooses between chase/roam behavior and moves the chaser accordingly
    @Override
    public void update(Chaser chaser, Session session) {
        if (!chaser.isActive()) {
            return;
        }

        Maze maze = session.getMaze();
        ensureVisitMap(maze);

        Runner runner = session.getRunner();

        int cx = chaser.getX();
        int cy = chaser.getY();
        int rx = runner.getX();
        int ry = runner.getY();

        int manhattan = Math.abs(cx - rx) + Math.abs(cy - ry);

        // --- Chase mode: close to the player ---
        if (manhattan <= detectionRadius) {
            clearRoamTarget();

            boolean moved = moveAlongShortestPath(chaser, maze, cx, cy, rx, ry);
            if (!moved) {
                // If pathfinding fails (should be rare), do a simple random step
                randomWalk(chaser, maze);
            } else {
                // mark new position as visited
                markVisited(maze, chaser.getX(), chaser.getY());
            }
            return;
        }

        // --- Roam mode: far from the player ---
        if (!hasValidRoamTarget(maze)) {
            pickNewRoamTarget(maze, cx, cy);
        }

        boolean moved = moveAlongShortestPath(chaser, maze, cx, cy,
                                              roamTargetX, roamTargetY);

        if (!moved) {
            // Target probably weird/unhelpful – throw it away and try something else next time
            clearRoamTarget();
            randomWalk(chaser, maze);
            markVisited(maze, chaser.getX(), chaser.getY());
        } else {
            roamStepsRemaining--;

            int ncx = chaser.getX();
            int ncy = chaser.getY();
            markVisited(maze, ncx, ncy);

            // If we reached the roam target or used up our “budget”,
            // force a new roam target on a future tick.
            if ((ncx == roamTargetX && ncy == roamTargetY) || roamStepsRemaining <= 0) {
                clearRoamTarget();
            }
        }
    }

    // ---------- VISIT MAP HELPERS ----------

    // ensureVisitMap - Initializes or resizes the visit map to match the maze size
    private void ensureVisitMap(Maze maze) {
        int w = maze.getWidth();
        int h = maze.getHeight();
        if (visitCount == null || visitCount.length != h || visitCount[0].length != w) {
            visitCount = new int[h][w];
        }
    }

    // markVisited - Increments the visit count for the given cell
    private void markVisited(Maze maze, int x, int y) {
        ensureVisitMap(maze);
        if (maze.inBounds(x, y)) {
            visitCount[y][x]++;
        }
    }

    // ---------- ROAMING HELPERS ----------

    // clearRoamTarget - Resets the current roam target and its step budget
    private void clearRoamTarget() {
        roamTargetX = -1;
        roamTargetY = -1;
        roamStepsRemaining = 0;
    }

    // hasValidRoamTarget - Returns true if the current roam target is usable
    private boolean hasValidRoamTarget(Maze maze) {
        if (roamTargetX < 0 || roamTargetY < 0) return false;
        if (!maze.inBounds(roamTargetX, roamTargetY)) return false;
        return maze.getCell(roamTargetX, roamTargetY).isWalkable()
                && roamStepsRemaining > 0;
    }

    // pickNewRoamTarget - Chooses a new walkable roam cell, biased toward low-visit cells
    private void pickNewRoamTarget(Maze maze, int cx, int cy) {
        ensureVisitMap(maze);
        int w = maze.getWidth();
        int h = maze.getHeight();

        int bestX = cx;
        int bestY = cy;
        int bestScore = Integer.MAX_VALUE;

        // Occasionally force a “distant” search to encourage crossing the map.
        boolean forceDistant = rng.nextDouble() < 0.25;

        for (int tries = 0; tries < 80; tries++) {
            int x = 1 + rng.nextInt(w - 2);
            int y = 1 + rng.nextInt(h - 2);

            if (!maze.getCell(x, y).isWalkable()) {
                continue;
            }

            int manhattan = Math.abs(x - cx) + Math.abs(y - cy);
            if (manhattan < 4) {
                // avoid tiny micro-roams
                continue;
            }

            if (forceDistant) {
                // If we’re trying to go far, skip things that are still pretty close
                if (manhattan < (w + h) / 6) {
                    continue;
                }
            }

            int visits = visitCount[y][x];

            // Lower visits = more attractive. Use a tie-breaker with some randomness.
            if (visits < bestScore || (visits == bestScore && rng.nextDouble() < 0.3)) {
                bestScore = visits;
                bestX = x;
                bestY = y;
            }
        }

        // Fallback: if we somehow never improved, pick a reasonably far cell on the opposite side.
        if (bestScore == Integer.MAX_VALUE) {
            int targetX;
            if (cx < w / 2) {
                // we’re on the left, bias target to the right third
                targetX = w - 2 - rng.nextInt(Math.max(1, w / 3));
            } else {
                // we’re on the right, bias target to the left third
                targetX = 1 + rng.nextInt(Math.max(1, w / 3));
            }
            int targetY = 1 + rng.nextInt(h - 2);

            bestX = targetX;
            bestY = targetY;
        }

        roamTargetX = bestX;
        roamTargetY = bestY;
        roamStepsRemaining = ROAM_PATH_LIFETIME;
    }

    // randomWalk - Takes a random valid step as a backup movement
    private void randomWalk(Chaser chaser, Maze maze) {
        int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
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
            return;
        }
        // If all neighbors blocked, stay put.
    }

    // ---------- SHORTEST PATH VIA BFS ----------

    // moveAlongShortestPath - Uses BFS to step one tile along a shortest path to (tx, ty)
    private boolean moveAlongShortestPath(Chaser chaser,
                                          Maze maze,
                                          int sx, int sy,
                                          int tx, int ty) {

        if (sx == tx && sy == ty) {
            return false;
        }

        int w = maze.getWidth();
        int h = maze.getHeight();

        boolean[][] visited = new boolean[h][w];
        int[][] parentX = new int[h][w];
        int[][] parentY = new int[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                parentX[y][x] = -1;
                parentY[y][x] = -1;
            }
        }

        Queue<int[]> queue = new ArrayDeque<>();
        queue.add(new int[] { sx, sy });
        visited[sy][sx] = true;

        // Deterministic neighbor order: this kills the back-and-forth stutter,
        // because the chosen shortest path is stable from tick to tick.
        int[][] dirs = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 } };

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
            return false;
        }

        // Reconstruct path backwards from target to source, but only
        // keep the first step after (sx, sy).
        int curX = tx;
        int curY = ty;
        int nextX = tx;
        int nextY = ty;

        while (!(curX == sx && curY == sy)) {
            int px = parentX[curY][curX];
            int py = parentY[curY][curX];

            if (px == -1 && py == -1) {
                // Should not happen if found == true, but just in case.
                return false;
            }

            nextX = curX;
            nextY = curY;

            curX = px;
            curY = py;
        }

        int moveDx = nextX - sx;
        int moveDy = nextY - sy;

        if (moveDx == 0 && moveDy == 0) {
            return false;
        }

        chaser.moveBy(moveDx, moveDy);
        return true;
    }

    // shuffleDirs - Shuffle (currently unused, kept for future randomness)
    @SuppressWarnings("unused")
    private void shuffleDirs(int[][] dirs) {
        for (int i = dirs.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            int[] tmp = dirs[i];
            dirs[i] = dirs[j];
            dirs[j] = tmp;
        }
    }
}
