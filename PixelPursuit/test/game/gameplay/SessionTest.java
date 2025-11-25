package game.gameplay;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import game.world.Maze;

public class SessionTest {

    @Test
    void survivalGoldAccumulatesOverTime() {
        Maze maze = new Maze(5, 5);
        // Start runner at entrance so we know it's valid
        Runner runner = new Runner(maze,
                                   maze.getEntranceX(),
                                   maze.getEntranceY());
        Session session = new Session(maze, runner);

        assertEquals(0, session.getTimeGold());
        assertEquals(0, session.getRunGold());

        // SURVIVAL_GOLD_INTERVAL in Session is 1.0 second
        session.update(1.0);
        assertEquals(1, session.getTimeGold());
        assertEquals(1, session.getRunGold());

        session.update(0.5);
        session.update(0.5);
        assertEquals(2, session.getTimeGold());
        assertEquals(2, session.getRunGold());
    }

    @Test
    void chaserCollisionEndsSession() {
        Maze maze = new Maze(5, 5);
        int x = 2;
        int y = 2;
        maze.getCell(x, y).setWalkable(true);

        Runner runner = new Runner(maze, x, y);
        Session session = new Session(maze, runner);

        // Chaser with null AI; we just care about collision
        Chaser chaser = new Chaser(maze, x, y, null);
        session.addChaser(chaser);

        assertTrue(session.isRunning());
        assertTrue(runner.isAlive());

        // update() will check for chaser on top of runner
        session.update(0.1);

        assertFalse(session.isRunning());
        assertFalse(runner.isAlive());
    }
}
