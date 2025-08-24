// copied from DungeonMap.java

import java.util.Random;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public class DungeonMapParallel {

    public static final int PRECISION = 10000;
    public static final int RESOLUTION = 5;

    private int rows, columns; // dungeonGrid size
    private double xmin, xmax, ymin, ymax; // x and y dungeon limits
    private int [][] manaMap;
    private int [][] visit;
    private int dungeonGridPointsEvaluated;
    private double bossX;
    private double bossY;
    private double decayFactor;  

    // constructor
    public DungeonMapParallel(double xmin, double xmax, 
                              double ymin, double ymax, 
                              int seed) {
        super();
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;

        this.rows = (int) Math.round((xmax-xmin)*RESOLUTION); 
        this.columns = (int) Math.round((ymax-ymin)*RESOLUTION);

        // Randomly place the boss peak
        Random rand;
        if(seed==0) rand= new Random(); // no fixed seed
        else rand= new Random(seed);
        double xRange = xmax - xmin;
        this.bossX = xmin + (xRange) * rand.nextDouble();
        this.bossY = ymin + (ymax - ymin) * rand.nextDouble();

        // Calculate decay factor based on range
        this.decayFactor = 2.0 / (xRange * 0.1);  

        manaMap = new int[rows][columns];
        visit = new int[rows][columns];
        dungeonGridPointsEvaluated=0;

        /* Terrain initialization */
        for(int i=0; i<rows; i++ ) {
            for( int j=0; j<columns; j++ ) {
                manaMap[i][j] = Integer.MIN_VALUE; // means mana not yet measured
                visit[i][j] = -1; // grid point not yet visited
            }
        }
    }

    // has this site been visited before?
    boolean visited(int x, int y) {
        return visit[x][y] != -1;
    }

    void setVisited(int x, int y, int id) {
        if (visit[x][y]==-1) // don't reset
            visit[x][y]= id;
    }

    /**
     * Evaluates mana at a dungeonGrid coordinate (x, y) in the dungeon,
     * and writes it to the map.
     *
     * @param x The row index
     * @param y The column index
     * @return The mana value at (x, y).
     */
    int getManaLevel(int x, int y) {
        // Gaussian hill centered at bossX,bossY
        double xCoord = xmin + ((double)x / RESOLUTION);
        double yCoord = ymin + ((double)y / RESOLUTION);
        double dist2 = Math.pow(xCoord - bossX, 2) + Math.pow(yCoord - bossY, 2);
        double manaVal = Math.exp(-decayFactor * dist2) * PRECISION;
        return (int)Math.round(manaVal);
    }

    public int getRows() { return rows; }
    public int getColumns() { return columns; }

    // --- Added missing methods ---
    public boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < columns;
    }

    public int getMana(int row, int col) {
        if (manaMap[row][col] == Integer.MIN_VALUE) {
            manaMap[row][col] = getManaLevel(row, col);
        }
        return manaMap[row][col];
    }

    public double getX(int row) {
        return xmin + ((double) row / RESOLUTION);
    }

    public double getY(int col) {
        return ymin + ((double) col / RESOLUTION);
    }
}
