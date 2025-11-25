package game.world;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MazeTest {

    @Test
    void getCellWithinBoundsReturnsNotNull() {
        Maze maze = new Maze(10, 8);
        Cell c = maze.getCell(0, 0);
        assertNotNull(c);
    }

    @Test
    void getCellOutOfBoundsThrows() {
        Maze maze = new Maze(5, 5);

        assertThrows(IndexOutOfBoundsException.class,
                     () -> maze.getCell(-1, 0));
        assertThrows(IndexOutOfBoundsException.class,
                     () -> maze.getCell(5, 0));
    }

    @Test
    void entranceAndExitAreWalkable() {
        Maze maze = new Maze(10, 10);
        Cell entrance = maze.getEntranceCell();
        Cell exit = maze.getExitCell();

        assertTrue(entrance.isWalkable());
        assertTrue(exit.isWalkable());
    }
}
