package game.gameplay;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import game.world.Maze;

public class RunnerTest {

    @Test
    void runnerMovesIntoWalkableCell() {
        Maze maze = new Maze(5, 5);

        // Make sure the cells we care about are walkable
        maze.getCell(2, 2).setWalkable(true);
        maze.getCell(3, 2).setWalkable(true);

        Runner runner = new Runner(maze, 2, 2);
        runner.moveBy(1, 0);

        assertEquals(3, runner.getX());
        assertEquals(2, runner.getY());
    }

    @Test
    void runnerBlockedByWallStaysPut() {
        Maze maze = new Maze(5, 5);

        maze.getCell(2, 2).setWalkable(true);
        maze.getCell(3, 2).setWall(true);   // force a wall

        Runner runner = new Runner(maze, 2, 2);
        runner.moveBy(1, 0);

        assertEquals(2, runner.getX());
        assertEquals(2, runner.getY());
    }

    @Test
    void runnerOutOfBoundsMoveIsIgnored() {
        Maze maze = new Maze(3, 3);

        // Make everything walkable so only bounds matter
        for (int y = 0; y < maze.getHeight(); y++) {
            for (int x = 0; x < maze.getWidth(); x++) {
                maze.getCell(x, y).setWalkable(true);
            }
        }

        Runner runner = new Runner(maze, 0, 1);
        runner.moveBy(-1, 0);  // would go to x = -1

        assertEquals(0, runner.getX());
        assertEquals(1, runner.getY());
    }
}
