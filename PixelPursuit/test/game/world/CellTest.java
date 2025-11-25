package game.world;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class CellTest {

    @Test
    void goldIsSetAndTakenCorrectly() {
        Cell c = new Cell(2, 3, true);

        assertFalse(c.hasGold());
        c.setGold(5);
        assertTrue(c.hasGold());
        assertEquals(5, c.getGold());

        int taken = c.takeGold();
        assertEquals(5, taken);
        assertFalse(c.hasGold());
        assertEquals(0, c.getGold());
    }

    @Test
    void setGoldRejectsNegativeAmount() {
        Cell c = new Cell(0, 0, true);
        assertThrows(IllegalArgumentException.class,
                     () -> c.setGold(-1));
    }
}
