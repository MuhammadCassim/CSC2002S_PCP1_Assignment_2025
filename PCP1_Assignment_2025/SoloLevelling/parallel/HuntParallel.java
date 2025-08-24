public class HuntParallel {

    public enum Direction {
        STAY,
        UP, DOWN, LEFT, RIGHT,
        UP_LEFT, UP_RIGHT, DOWN_LEFT, DOWN_RIGHT
    }

    private final int id;
    private final int startRow, startCol;
    private final DungeonMapParallel dungeon;

    private int bestMana;
    private double bestX, bestY;

    public HuntParallel(int id, int startRow, int startCol, DungeonMapParallel dungeon) {
        this.id = id;
        this.startRow = startRow;
        this.startCol = startCol;
        this.dungeon = dungeon;
    }

    public int findManaPeak() {
        int row = startRow;
        int col = startCol;
        int mana = dungeon.getMana(row, col);

        bestMana = mana;
        bestX = dungeon.getX(row);
        bestY = dungeon.getY(col);

        boolean found = false;
        while (!found) {
            found = true;
            int bestLocal = mana;
            int bestRow = row, bestCol = col;

            // Check 8 neighbours
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    int newRow = row + dr;
                    int newCol = col + dc;
                    if (dungeon.isValid(newRow, newCol)) {
                        int neighbourMana = dungeon.getMana(newRow, newCol);
                        if (neighbourMana > bestLocal) {
                            bestLocal = neighbourMana;
                            bestRow = newRow;
                            bestCol = newCol;
                            found = false;
                        }
                    }
                }
            }

            if (!found) {
                row = bestRow;
                col = bestCol;
                mana = bestLocal;

                if (mana > bestMana) {
                    bestMana = mana;
                    bestX = dungeon.getX(row);
                    bestY = dungeon.getY(col);
                }
            }
        }
        return bestMana;
    }

    // getters
    public int getBestMana() { return bestMana; }
    public double getBestX() { return bestX; }
    public double getBestY() { return bestY; }
}
