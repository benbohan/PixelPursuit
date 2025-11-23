package game.gameplay;

import game.world.Maze;

/**
 * Very simple AI: always tries to step closer to the runner
 * in Manhattan distance, respecting walls.
 */
public class SimpleChaserAI implements ChaserAI {

    @Override
    public void update(Chaser chaser, Session session) {
        if (!chaser.isActive()) return;

        Runner runner = session.getRunner();
        Maze maze = session.getMaze();

        int cx = chaser.getX();
        int cy = chaser.getY();
        int rx = runner.getX();
        int ry = runner.getY();

        int dx = 0;
        int dy = 0;

        // Prefer the axis with greater distance
        int diffX = rx - cx;
        int diffY = ry - cy;

        if (Math.abs(diffX) >= Math.abs(diffY)) {
            dx = Integer.compare(rx, cx);
        } else {
            dy = Integer.compare(ry, cy);
        }

        // Try primary direction
        if (!tryMove(chaser, maze, dx, dy)) {
            // If blocked, try the other axis
            if (dx != 0) {
                dx = 0;
                dy = Integer.compare(ry, cy);
            } else if (dy != 0) {
                dy = 0;
                dx = Integer.compare(rx, cx);
            }
            tryMove(chaser, maze, dx, dy);
        }
    }

    private boolean tryMove(Chaser chaser, Maze maze, int dx, int dy) {
        if (dx == 0 && dy == 0) return false;
        int nx = chaser.getX() + dx;
        int ny = chaser.getY() + dy;

        if (!maze.inBounds(nx, ny)) return false;
        if (!maze.getCell(nx, ny).isWalkable()) return false;

        chaser.moveBy(dx, dy);
        return true;
    }
}
