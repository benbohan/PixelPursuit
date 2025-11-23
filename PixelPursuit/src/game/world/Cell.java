package game.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A single tile in the maze grid.
 */
public class Cell {

    private final int x;
    private final int y;

    // true = floor, false = wall
    private boolean walkable;

    private int gold;

    // Later these will be Runner / Chaser; for now keep it generic.
    private final List<Object> entities = new ArrayList<>();

    public Cell(int x, int y, boolean walkable) {
        this.x = x;
        this.y = y;
        this.walkable = walkable;
        this.gold = 0;
    }

    // --- basic info ---

    public int getX() { return x; }
    public int getY() { return y; }

    public boolean isWalkable() { return walkable; }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    // convenience if you like wall terminology
    public boolean isWall() {
        return !walkable;
    }

    public void setWall(boolean wall) {
        this.walkable = !wall;
    }

    // --- gold ---

    public boolean hasGold() {
        return gold > 0;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Gold amount cannot be negative");
        }
        this.gold = amount;
    }

    public int takeGold() {
        int amount = gold;
        gold = 0;
        return amount;
    }

    // --- entities ---

    public void addEntity(Object entity) {
        if (entity != null) {
            entities.add(entity);
        }
    }

    public void removeEntity(Object entity) {
        entities.remove(entity);
    }

    public List<Object> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public void clearEntities() {
        entities.clear();
    }
}
