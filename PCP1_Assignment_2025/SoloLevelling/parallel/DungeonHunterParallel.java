import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class DungeonHunterParallel {
    static final boolean DEBUG = false;
    static long startTime = 0;
    static long endTime = 0;
    private static void tick() { startTime = System.currentTimeMillis(); }
    private static void tock() { endTime = System.currentTimeMillis(); }

    // ✅ Result wrapper (mana + coordinates)
    static class Result {
        final int mana;
        final double x, y;
        Result(int mana, double x, double y) {
            this.mana = mana;
            this.x = x;
            this.y = y;
        }
    }

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
        Result best = pool.invoke(new SearchTask(dungeon, numSearches, rand));
        tock();

        // ✅ Output in the same style as serial version
        System.out.printf("\tdungeon size: %d\n", gateSize);
        System.out.printf("\trows: %d, columns: %d\n", dungeon.getRows(), dungeon.getColumns());
        System.out.printf("\tNumber searches: %d\n", numSearches);
        System.out.printf("\n\ttime: %d ms\n", endTime - startTime);
        System.out.printf("Dungeon Master (mana %d) found at:  x=%.1f y=%.1f\n",
                best.mana, best.x, best.y);

        dungeon.visualisePowerMap("parallelSearch.png", false);
        dungeon.visualisePowerMap("parallelSearchPath.png", true);
    }

    // ✅ Fork/Join Task returning Result
    static class SearchTask extends RecursiveTask<Result> {
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
        protected Result compute() {
            if (numSearches <= SEQUENTIAL_CUTOFF) {
                int bestMana = Integer.MIN_VALUE;
                double bestX = 0, bestY = 0;

                Random localRand = new Random(rand.nextLong()); // thread-local seed

                for (int i = 0; i < numSearches; i++) {
                    HuntParallel search = new HuntParallel(
                            i + 1,
                            localRand.nextInt(dungeon.getRows()),
                            localRand.nextInt(dungeon.getColumns()),
                            dungeon
                    );
                    int localMana = search.findManaPeak();
                    if (localMana > bestMana) {
                        bestMana = localMana;
                        bestX = search.getBestX();
                        bestY = search.getBestY();
                    }
                }
                return new Result(bestMana, bestX, bestY);
            } else {
                int half = numSearches / 2;
                SearchTask left = new SearchTask(dungeon, half, new Random(rand.nextLong()));
                SearchTask right = new SearchTask(dungeon, numSearches - half, new Random(rand.nextLong()));

                left.fork();
                Result rightResult = right.compute();
                Result leftResult = left.join();

                return (leftResult.mana > rightResult.mana) ? leftResult : rightResult;
            }
        }
    }
}
