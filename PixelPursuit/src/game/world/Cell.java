package game.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Single tile in the maze grid:
 *  - Knows its (x, y) location and whether it is walkable or a wall.
 *  - Can hold gold and a single diamond as loot.
 *  - Tracks entities (Runner, Chaser, etc.) standing on this cell.
 */
public class Cell {

    // ---------- FIELDS ----------

    private final int x;
    private final int y;

    // true = floor, false = wall
    private boolean walkable;

    private int gold;
    private boolean diamond;

    // Later these will be Runner / Chaser, for now keep it generic Object
    private final List<Object> entities = new ArrayList<>();

    // ---------- CONSTRUCTORS ----------

    // Cell - Creates a cell at (x, y) with initial walkable flag
    public Cell(int x, int y, boolean walkable) {
        this.x = x;
        this.y = y;
        this.walkable = walkable;
        this.gold = 0;
    }

    // ---------- BASIC INFO ----------

    // getX - Returns the cell's x-coordinate in the maze
    public int getX() {
        return x;
    }

    // getY - Returns the cell's y-coordinate in the maze
    public int getY() {
        return y;
    }

    // isWalkable - Returns true if this cell can be walked on
    public boolean isWalkable() {
        return walkable;
    }

    // setWalkable - Marks this cell as walkable or not
    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    // isWall - Returns true if this cell is a wall
    public boolean isWall() {
        return !walkable;
    }

    // setWall - Sets this cell to wall or floor
    public void setWall(boolean wall) {
        this.walkable = !wall;
    }

    // ---------- GOLD ----------

    // hasGold - Returns true if this cell currently has any gold
    public boolean hasGold() {
        return gold > 0;
    }

    // getGold - Returns the amount of gold on this cell
    public int getGold() {
        return gold;
    }

    // setGold - Sets the amount of gold on this cell (must be non-negative)
    public void setGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Gold amount cannot be negative");
        }
        this.gold = amount;
    }

    // takeGold - Removes and returns all gold on this cell
    public int takeGold() {
        int amount = gold;
        gold = 0;
        return amount;
    }

    // ---------- DIAMOND ----------

    // hasDiamond - Returns true if this cell currently has a diamond
    public boolean hasDiamond() {
        return diamond;
    }

    // setDiamond - Places or removes a diamond on this cell
    public void setDiamond(boolean value) {
        this.diamond = value;
    }

    // takeDiamond - Removes the diamond and returns true if there was one
    public boolean takeDiamond() {
        boolean had = diamond;
        diamond = false;
        return had;
    }

    // ---------- ENTITIES ----------

    // addEntity - Adds a non-null entity standing on this cell
    public void addEntity(Object entity) {
        if (entity != null) {
            entities.add(entity);
        }
    }

    // removeEntity - Removes the given entity from this cell
    public void removeEntity(Object entity) {
        entities.remove(entity);
    }

    // getEntities - Returns an unmodifiable view of all entities on this cell
    public List<Object> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    // clearEntities - Removes all entities from this cell
    public void clearEntities() {
        entities.clear();
    }
}
