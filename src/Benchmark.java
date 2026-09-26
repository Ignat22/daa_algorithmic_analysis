import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

 public class Benchmark {

    static final int[] N_VALUES = {100, 1_000, 10_000, 100_000};
    static final int REPEATS = 5;
    static final long SEED = 42L;

    public static void main(String[] args) throws IOException {
        System.out.println("Running Workload 1: Random Access ...");
        workload1RandomAccess();
        System.out.println("Running Workload 2: Search ...");
        workload2Search();
        System.out.println("Running Workload 3: Insertion and Removal ...");
        workload3InsertRemove();
        System.out.println("Running Workload 4: Priority Processing ...");
        workload4PriorityProcessing();
        System.out.println("Done. CSVs written to results/tables/");
    }

    // -----------------------------------------------------------------
    // Workload 1 - Random Access: get(index), 10000 times, per structure
    // -----------------------------------------------------------------
    static void workload1RandomAccess() throws IOException {
        try (PrintWriter out = csv("results/tables/workload1_random_access.csv",
                "structure,n,avg_time_ns,accesses,theoretical")) {
            for (int n : N_VALUES) {
                int[] initial = randomInts(n, SEED);
                int[] indices = randomIndices(10_000, n, SEED + 1);

                // Dynamic Array
                {
                    long totalTime = 0, accesses = 0;
                    for (int r = 0; r < REPEATS; r++) {
                        DynamicArray<Integer> da = new DynamicArray<>();
                        for (int v : initial) da.add(v);
                        da.resetCounter();
                        long t0 = System.nanoTime();
                        for (int idx : indices) da.get(idx);
                        long t1 = System.nanoTime();
                        totalTime += (t1 - t0);
                        accesses += da.getCounter();
                    }
                    out.printf("DynamicArray,%d,%.1f,%d,O(1)%n",
                            n, totalTime / (double) REPEATS, accesses / REPEATS);
                }
                // LinkedList
                {
                    long totalTime = 0, accesses = 0;
                    for (int r = 0; r < REPEATS; r++) {
                        LinkedList<Integer> ll = new LinkedList<>();
                        for (int v : initial) ll.add(v);
                        ll.resetCounter();
                        long t0 = System.nanoTime();
                        for (int idx : indices) ll.get(idx);
                        long t1 = System.nanoTime();
                        totalTime += (t1 - t0);
                        accesses += ll.getCounter();
                    }
                    out.printf("LinkedList,%d,%.1f,%d,O(n)%n",
                            n, totalTime / (double) REPEATS, accesses / REPEATS);
                }
            }
        }
    }

    // -----------------------------------------------------------------
    // Workload 2 - Search: contains(value), 1000 times, per structure
    // -----------------------------------------------------------------
    static void workload2Search() throws IOException {
        try (PrintWriter out = csv("results/tables/workload2_search.csv",
                "structure,n,avg_time_ns,comparisons,theoretical")) {
            for (int n : N_VALUES) {
                int[] initial = randomInts(n, SEED);
                // search values: half present (drawn from initial), half absent (out of range)
                int[] searchValues = mixedSearchValues(1_000, initial, SEED + 2);

                {
                    long totalTime = 0, comparisons = 0;
                    for (int r = 0; r < REPEATS; r++) {
                        DynamicArray<Integer> da = new DynamicArray<>();
                        for (int v : initial) da.add(v);
                        da.resetCounter();
                        long t0 = System.nanoTime();
                        for (int v : searchValues) da.contains(v);
                        long t1 = System.nanoTime();
                        totalTime += (t1 - t0);
                        comparisons += da.getCounter();
                    }
                    out.printf("DynamicArray,%d,%.1f,%d,O(n)%n",
                            n, totalTime / (double) REPEATS, comparisons / REPEATS);
                }
                {
                    long totalTime = 0, comparisons = 0;
                    for (int r = 0; r < REPEATS; r++) {
                        LinkedList<Integer> ll = new LinkedList<>();
                        for (int v : initial) ll.add(v);
                        ll.resetCounter();
                        long t0 = System.nanoTime();
                        for (int v : searchValues) ll.contains(v);
                        long t1 = System.nanoTime();
                        totalTime += (t1 - t0);
                        comparisons += ll.getCounter();
                    }
                    out.printf("LinkedList,%d,%.1f,%d,O(n)%n",
                            n, totalTime / (double) REPEATS, comparisons / REPEATS);
                }
            }
        }
    }

    // -----------------------------------------------------------------
    // Workload 3 - Insertion and Removal at front (index 0) and middle (n/2)
    // -----------------------------------------------------------------
    static void workload3InsertRemove() throws IOException {
        try (PrintWriter out = csv("results/tables/workload3_insert_remove.csv",
                "structure,n,position,operation,avg_time_ns,movements,ops_performed,theoretical")) {
            for (int n : N_VALUES) {
                int[] initial = randomInts(n, SEED);
                int[] insertValues = randomInts(1_000, SEED + 3);

                runInsertRemove(out, "DynamicArray", n, 0, "front", initial, insertValues,
                        () -> new DAAdapter<Integer>());
                runInsertRemove(out, "LinkedList", n, 0, "front", initial, insertValues,
                        () -> new LLAdapter<Integer>());

                int mid = n / 2;
                runInsertRemove(out, "DynamicArray", n, mid, "middle", initial, insertValues,
                        () -> new DAAdapter<Integer>());
                runInsertRemove(out, "LinkedList", n, mid, "middle", initial, insertValues,
                        () -> new LLAdapter<Integer>());
            }
        }
    }

    // Small adapter interface so the same runInsertRemove logic drives both structures.
    interface Struct<T> {
        void add(T x);
        void add(int index, T x);
        T remove(int index);
        int size();
        void resetCounter();
        long getCounter();
    }
    static class DAAdapter<T> implements Struct<T> {
        DynamicArray<T> d = new DynamicArray<>();
        public void add(T x) { d.add(x); }
        public void add(int i, T x) { d.add(i, x); }
        public T remove(int i) { return d.remove(i); }
        public int size() { return d.size(); }
        public void resetCounter() { d.resetCounter(); }
        public long getCounter() { return d.getCounter(); }
    }
    static class LLAdapter<T> implements Struct<T> {
        LinkedList<T> d = new LinkedList<>();
        public void add(T x) { d.add(x); }
        public void add(int i, T x) { d.add(i, x); }
        public T remove(int i) { return d.remove(i); }
        public int size() { return d.size(); }
        public void resetCounter() { d.resetCounter(); }
        public long getCounter() { return d.getCounter(); }
    }

    interface Factory<T> { Struct<T> create(); }

    static void runInsertRemove(PrintWriter out, String name, int n, int index, String position,
                                 int[] initial, int[] insertValues, Factory<Integer> factory) {
        // Insertion: index is always <= current size here because the structure
        // only grows, so all 1000 insertions are always valid.
        {
            long totalTime = 0, movements = 0;
            for (int r = 0; r < REPEATS; r++) {
                Struct<Integer> s = factory.create();
                for (int v : initial) s.add(v);
                s.resetCounter();
                long t0 = System.nanoTime();
                for (int v : insertValues) s.add(index, v);
                long t1 = System.nanoTime();
                totalTime += (t1 - t0);
                movements += s.getCounter();
            }
            String theoretical = position.equals("front")
                    ? (name.equals("DynamicArray") ? "O(n)" : "O(1)")
                    : "O(n)";
            out.printf("%s,%d,%s,insert,%.1f,%d,%d,%s%n",
                    name, n, position, totalTime / (double) REPEATS, movements / REPEATS,
                    insertValues.length, theoretical);
        }
        // Removal (fresh copy of the original n-element structure each repeat).
        // Removing 1000 times from a fixed position is only well-defined while
        // the structure still has elements; the target index is clamped to
        // size-1 each iteration and the loop stops early if the structure
        // empties out (this only bites for n < 1000, i.e. n = 100 here).
        {
            long totalTime = 0, movements = 0;
            int opsPerformed = 0;
            for (int r = 0; r < REPEATS; r++) {
                Struct<Integer> s = factory.create();
                for (int v : initial) s.add(v);
                s.resetCounter();
                int opsThisRun = 0;
                long t0 = System.nanoTime();
                for (int k = 0; k < 1_000 && s.size() > 0; k++) {
                    int idx = Math.min(index, s.size() - 1);
                    s.remove(idx);
                    opsThisRun++;
                }
                long t1 = System.nanoTime();
                totalTime += (t1 - t0);
                movements += s.getCounter();
                opsPerformed = opsThisRun; // same every repeat (deterministic sizes)
            }
            String theoretical = position.equals("front")
                    ? (name.equals("DynamicArray") ? "O(n)" : "O(1)")
                    : "O(n)";
            out.printf("%s,%d,%s,remove,%.1f,%d,%d,%s%n",
                    name, n, position, totalTime / (double) REPEATS, movements / REPEATS,
                    opsPerformed, theoretical);
        }
    }

    // -----------------------------------------------------------------
    // Workload 4 - Priority Processing: MinHeap insert n times, extract n times
    // -----------------------------------------------------------------
    static void workload4PriorityProcessing() throws IOException {
        try (PrintWriter out = csv("results/tables/workload4_priority_processing.csv",
                "n,avg_insert_time_ns,insert_comparisons,avg_extract_time_ns,extract_comparisons,order_ok")) {
            for (int n : N_VALUES) {
                int[] values = randomInts(n, SEED);
                long insertTimeTotal = 0, insertCompsTotal = 0;
                long extractTimeTotal = 0, extractCompsTotal = 0;
                boolean orderOk = true;

                for (int r = 0; r < REPEATS; r++) {
                    MinHeap<Integer> heap = new MinHeap<>();
                    heap.resetCounter();
                    long t0 = System.nanoTime();
                    for (int v : values) heap.insert(v);
                    long t1 = System.nanoTime();
                    insertTimeTotal += (t1 - t0);
                    insertCompsTotal += heap.getCounter();

                    heap.resetCounter();
                    int prev = Integer.MIN_VALUE;
                    long t2 = System.nanoTime();
                    for (int i = 0; i < n; i++) {
                        int m = heap.extractMin();
                        if (m < prev) orderOk = false;
                        prev = m;
                    }
                    long t3 = System.nanoTime();
                    extractTimeTotal += (t3 - t2);
                    extractCompsTotal += heap.getCounter();
                }
                out.printf("%d,%.1f,%d,%.1f,%d,%b%n",
                        n,
                        insertTimeTotal / (double) REPEATS, insertCompsTotal / REPEATS,
                        extractTimeTotal / (double) REPEATS, extractCompsTotal / REPEATS,
                        orderOk);
            }
        }
    }

    // -----------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------
    static int[] randomInts(int count, long seed) {
        Random rnd = new Random(seed);
        int[] arr = new int[count];
        for (int i = 0; i < count; i++) arr[i] = rnd.nextInt(1_000_000_000);
        return arr;
    }

    static int[] randomIndices(int count, int bound, long seed) {
        Random rnd = new Random(seed);
        int[] arr = new int[count];
        for (int i = 0; i < count; i++) arr[i] = bound == 0 ? 0 : rnd.nextInt(bound);
        return arr;
    }

    /** Half values drawn from the existing data (guaranteed hits), half random (likely misses). */
    static int[] mixedSearchValues(int count, int[] initial, long seed) {
        Random rnd = new Random(seed);
        int[] arr = new int[count];
        for (int i = 0; i < count; i++) {
            if (i % 2 == 0 && initial.length > 0) {
                arr[i] = initial[rnd.nextInt(initial.length)];
            } else {
                arr[i] = rnd.nextInt(1_000_000_000) + 1_000_000_000; // out of generation range -> miss
            }
        }
        return arr;
    }

    static PrintWriter csv(String path, String header) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(path));
        pw.println(header);
        return pw;
    }
}
