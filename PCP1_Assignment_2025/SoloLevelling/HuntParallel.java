/**
 * HuntParallel.java
 *
 * Parallel version of Hunt, using DungeonMapParallel.
 * Each Hunt represents one random search in the dungeon.
 *
 * M. Kuttel 2025 (adapted for parallel by student)
 */

public class HuntParallel {
    private int id;          // identifier for this hunt
    private int posRow, posCol; // Position in the dungeon
    private int steps;       // number of steps taken
    private boolean stopped; // did the search stop early?

    private DungeonMapParallel dungeon;

    public enum Direction {
        STAY,
        LEFT,
        RIGHT,
        UP,
        DOWN,
        UP_LEFT,
        UP_RIGHT,
        DOWN_LEFT,
        DOWN_RIGHT
    }

    public HuntParallel(int id, int pos_row, int pos_col, DungeonMapParallel dungeon) {
        this.id = id;
        this.posRow = pos_row;
        this.posCol = pos_col;
        this.dungeon = dungeon;
        this.stopped = false;
    }

    /**
     * Find the local maximum mana from an initial starting point
     * 
     * @return the highest mana located
     */
    public int findManaPeak() {
        int power = Integer.MIN_VALUE;
        Direction next = Direction.STAY;

        while (!dungeon.visited(posRow, posCol)) { // stop when hitting existing path
            power = dungeon.getManaLevel(posRow, posCol);
            dungeon.setVisited(posRow, posCol, id);
            steps++;
            next = dungeon.getNextStepDirection(posRow, posCol);

            switch (next) {
                case STAY:
                    return power; // found local maximum
                case LEFT:
                    posRow--;
                    break;
                case RIGHT:
                    posRow++;
                    break;
                case UP:
                    posCol--;
                    break;
                case DOWN:
                    posCol++;
                    break;
                case UP_LEFT:
                    posRow--;
                    posCol--;
                    break;
                case UP_RIGHT:
                    posRow++;
                    posCol--;
                    break;
                case DOWN_LEFT:
                    posRow--;
                    posCol++;
                    break;
                case DOWN_RIGHT:
                    posRow++;
                    posCol++;
                    break;
            }
        }
        stopped = true;
        return power;
    }

    public int getID() { return id; }
    public int getPosRow() { return posRow; }
    public int getPosCol() { return posCol; }
    public int getSteps() { return steps; }
    public boolean isStopped() { return stopped; }
}
