package game.scoring;

import game.gameplay.Runner;
import game.gameplay.Session;
import game.settings.Difficulty;
import game.world.Maze;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ScoreSystem and Multiplier.
 *
 * These tests construct a real Session, but keep the scenario simple so
 * timeGold and pickupGold are easy to reason about.
 */
public class ScoreSystemTest {

    /**
     * Create a Session where the runner survives for
     * timeSeconds and then picks up pickupGold from the entrance cell.
     */
    private Session makeSessionWithGold(int timeSeconds, int pickupGold) {
        Maze maze = new Maze();
        Runner runner = new Runner(maze,
                                   maze.getEntranceX(),
                                   maze.getEntranceY());
        Session session = new Session(maze, runner);

        // Accumulate survival gold: 1 gold per full second
        for (int i = 0; i < timeSeconds; i++) {
            session.update(1.0);
        }

        // Add pickup gold on the runner's current cell
        if (pickupGold > 0) {
            maze.getCell(runner.getX(), runner.getY()).setGold(pickupGold);
            // dt = 0 so don't change timeGold again
            session.update(0.0);
        }

        return session;
    }

    @Test
    public void escapeOnEasyUsesMultiplier1() {
        Session session = makeSessionWithGold(10, 5); // base gold: 10 + 5 = 15
        ScoreSystem scoreSystem = new ScoreSystem();

        SessionResult result = scoreSystem.compute(
                session,
                Difficulty.EASY,
                true   // escaped
        );

        int expectedBase = session.getTimeGold() + session.getPickupGold();
        assertEquals(expectedBase, result.getBaseGold(),
                "Base gold should match Session totals");
        assertEquals(expectedBase, result.getFinalGold(),
                "Easy difficulty should use 1x multiplier");
        assertEquals(1, result.getMultiplier().asInt(),
                "Easy multiplier should be 1x");
    }

    @Test
    public void escapeOnHardUsesMultiplier2() {
        Session session = makeSessionWithGold(10, 5);
        ScoreSystem scoreSystem = new ScoreSystem();

        SessionResult result = scoreSystem.compute(
                session,
                Difficulty.HARD,
                true   // escaped
        );

        int expectedBase = session.getTimeGold() + session.getPickupGold();
        assertEquals(expectedBase, result.getBaseGold(),
                "Base gold should match Session totals");
        assertEquals(expectedBase * 2, result.getFinalGold(),
                "Hard difficulty should use 2x multiplier");
        assertEquals(2, result.getMultiplier().asInt(),
                "Hard multiplier should be 2x");
    }

    @Test
    public void deathUsesZeroMultiplier() {
        Session session = makeSessionWithGold(10, 5);
        ScoreSystem scoreSystem = new ScoreSystem();

        SessionResult result = scoreSystem.compute(
                session,
                Difficulty.HARD,  // difficulty shouldn't matter on death
                false             // didnt escape
        );

        int expectedBase = session.getTimeGold() + session.getPickupGold();
        assertEquals(expectedBase, result.getBaseGold(),
                "Base gold should still reflect Session totals");
        assertEquals(0, result.getFinalGold(),
                "Death should give 0 final gold");
        assertEquals(0, result.getMultiplier().asInt(),
                "Death multiplier should be 0x");
    }
}
