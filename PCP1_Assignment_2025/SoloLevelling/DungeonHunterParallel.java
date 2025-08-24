import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class DungeonHunterParallel {
    static final boolean DEBUG = false;
    static long startTime = 0;
    static long endTime = 0;
    private static void tick() { startTime = System.currentTimeMillis(); }
    private static void tock() { endTime = System.currentTimeMillis(); }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java DungeonHunterParallel <gridSize> <density> <seed>");
            System.exit(1);
        }

        int gateSize = Integer.parseInt(args[0]);
        double density = Double.parseDouble(args[1]);
        int randomSeed = Integer.parseInt(args[2]);

        double xmin = -gateSize, xmax = gateSize, ymin = -gateSize, ymax = gateSize;
        DungeonMapParallel dungeon = new DungeonMapParallel(xmin, xmax, ymin, ymax, randomSeed);

        int numSearches = (int) (density * (gateSize * 2) * (gateSize * 2) * DungeonMapParallel.RESOLUTION);
        Random rand = new Random(randomSeed);

        tick();
        ForkJoinPool pool = new ForkJoinPool();
        SearchTask task = new SearchTask(dungeon, numSearches, rand);
        int max = pool.invoke(task);
        tock();

        // Output results (similar to serial version)
        System.out.printf("\tdungeon size: %d\n", gateSize);
        System.out.printf("\trows: %d, columns: %d\n", dungeon.getRows(), dungeon.getColumns());
        System.out.printf("\tNumber searches: %d\n", numSearches);
        System.out.printf("\n\ttime: %d ms\n", endTime - startTime);
        System.out.printf("\tBest mana found: %d\n", max);

        dungeon.visualisePowerMap("parallelSearch.png", false);
        dungeon.visualisePowerMap("parallelSearchPath.png", true);
    }

    // 🔹 Inner Fork/Join Task (instead of separate file)
    static class SearchTask extends RecursiveTask<Integer> {
        private static final int SEQUENTIAL_CUTOFF = 500;
        private final DungeonMapParallel dungeon;
        private final int numSearches;
        private final Random rand;

        SearchTask(DungeonMapParallel dungeon, int numSearches, Random rand) {
            this.dungeon = dungeon;
            this.numSearches = numSearches;
            this.rand = rand;
        }

        @Override
        protected Integer compute() {
            if (numSearches <= SEQUENTIAL_CUTOFF) {
                int max = Integer.MIN_VALUE;
                for (int i = 0; i < numSearches; i++) {
                    HuntParallel search = new HuntParallel(i + 1,
                            rand.nextInt(dungeon.getRows()),
                            rand.nextInt(dungeon.getColumns()),
                            dungeon);
                    int localMax = search.findManaPeak();
                    max = Math.max(max, localMax);
                }
                return max;
            } else {
                int half = numSearches / 2;
                SearchTask left = new SearchTask(dungeon, half, rand);
                SearchTask right = new SearchTask(dungeon, numSearches - half, rand);

                left.fork();
                int rightMax = right.compute();
                int leftMax = left.join();
                return Math.max(leftMax, rightMax);
            }
        }
    }
}
