import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
public class Tests {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
        } else {
            failed++;
            System.out.println("  [FAIL] " + name);
        }
    }

    // ---------------------------------------------------------------
    // DynamicArray
    // ---------------------------------------------------------------
    static void testDynamicArray() {
        System.out.println("DynamicArray:");

        DynamicArray<Integer> a = new DynamicArray<>();
        check("empty size == 0", a.size() == 0);
        check("empty isEmpty", a.isEmpty());
        check("empty contains(1) == false", !a.contains(1));

        a.add(42);
        check("one element size == 1", a.size() == 1);
        check("one element get(0) == 42", a.get(0) == 42);
        check("one element contains(42)", a.contains(42));
        check("one element !contains(7)", !a.contains(7));

        a.remove(0);
        check("after remove, empty again", a.isEmpty());

        for (int i = 0; i < 10; i++) a.add(i);
        check("multiple elements size == 10", a.size() == 10);
        check("get(0) == 0", a.get(0) == 0);
        check("get(9) == 9", a.get(9) == 9);

        // duplicates
        DynamicArray<Integer> dup = new DynamicArray<>();
        dup.add(5); dup.add(5); dup.add(5);
        check("duplicates size == 3", dup.size() == 3);
        check("duplicates contains(5)", dup.contains(5));
        dup.remove(0);
        check("duplicates after one remove size == 2", dup.size() == 2);
        check("duplicates still contains(5)", dup.contains(5));

        // boundary indices
        DynamicArray<Integer> b = new DynamicArray<>();
        b.add(1); b.add(2); b.add(3);
        b.add(0, 0);               // insert at front
        check("insert at front", b.get(0) == 0 && b.get(1) == 1);
        b.add(b.size(), 99);        // insert at end (== size)
        check("insert at end (index==size)", b.get(b.size() - 1) == 99);
        int firstVal = b.get(0);
        b.remove(0);
        check("remove at front", firstVal == 0 && b.get(0) == 1);
        int lastVal = b.get(b.size() - 1);
        b.remove(b.size() - 1);
        check("remove at last valid index", lastVal == 99);

        // invalid indices
        boolean threw;
        threw = false;
        try { b.get(-1); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("get(-1) throws", threw);
        threw = false;
        try { b.get(b.size()); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("get(size) throws", threw);
        threw = false;
        try { b.add(-1, 1); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("add(-1,x) throws", threw);
        threw = false;
        try { b.add(b.size() + 1, 1); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("add(size+1,x) throws", threw);
        threw = false;
        try { b.remove(b.size()); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("remove(size) throws", threw);

        // large input cross-validated against java.util.ArrayList
        DynamicArray<Integer> big = new DynamicArray<>();
        ArrayList<Integer> ref = new ArrayList<>();
        Random rnd = new Random(42);
        for (int i = 0; i < 20000; i++) {
            int v = rnd.nextInt(1_000_000);
            big.add(v);
            ref.add(v);
        }
        boolean matches = true;
        for (int i = 0; i < ref.size(); i++) {
            if (!big.get(i).equals(ref.get(i))) { matches = false; break; }
        }
        check("large input (20000) matches java.util.ArrayList", matches);
        check("large input contains a known value", big.contains(ref.get(12345)));
    }

    // ---------------------------------------------------------------
    // LinkedList
    // ---------------------------------------------------------------
    static void testLinkedList() {
        System.out.println("LinkedList:");

        LinkedList<Integer> a = new LinkedList<>();
        check("empty size == 0", a.size() == 0);
        check("empty isEmpty", a.isEmpty());
        check("empty !contains(1)", !a.contains(1));

        a.add(42);
        check("one element size == 1", a.size() == 1);
        check("one element get(0) == 42", a.get(0) == 42);
        a.remove(0);
        check("after remove, empty again", a.isEmpty());

        for (int i = 0; i < 10; i++) a.add(i);
        check("multiple elements size == 10", a.size() == 10);
        check("get(0) == 0", a.get(0) == 0);
        check("get(9) == 9", a.get(9) == 9);

        LinkedList<Integer> dup = new LinkedList<>();
        dup.add(5); dup.add(5); dup.add(5);
        check("duplicates size == 3", dup.size() == 3);
        check("duplicates contains(5)", dup.contains(5));
        dup.remove(1);
        check("duplicates after middle remove size == 2", dup.size() == 2);

        LinkedList<Integer> b = new LinkedList<>();
        b.add(1); b.add(2); b.add(3);
        b.add(0, 0);
        check("insert at front", b.get(0) == 0 && b.get(1) == 1);
        b.add(b.size(), 99);
        check("insert at end (index==size)", b.get(b.size() - 1) == 99);
        int firstVal = b.get(0);
        b.remove(0);
        check("remove at front", firstVal == 0 && b.get(0) == 1);
        int lastVal = b.get(b.size() - 1);
        b.remove(b.size() - 1);
        check("remove at last valid index (tail update)", lastVal == 99);
        b.add(7); // tail pointer must still work after removing old tail
        check("add after removing tail still O(1)-correct", b.get(b.size() - 1) == 7);

        boolean threw;
        threw = false;
        try { b.get(-1); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("get(-1) throws", threw);
        threw = false;
        try { b.get(b.size()); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("get(size) throws", threw);
        threw = false;
        try { b.remove(b.size()); } catch (IndexOutOfBoundsException e) { threw = true; }
        check("remove(size) throws", threw);

        LinkedList<Integer> big = new LinkedList<>();
        ArrayList<Integer> ref = new ArrayList<>();
        Random rnd = new Random(42);
        for (int i = 0; i < 20000; i++) {
            int v = rnd.nextInt(1_000_000);
            big.add(v);
            ref.add(v);
        }
        boolean matches = true;
        for (int i = 0; i < ref.size(); i++) {
            if (!big.get(i).equals(ref.get(i))) { matches = false; break; }
        }
        check("large input (20000) matches java.util.ArrayList", matches);
        check("large input contains a known value", big.contains(ref.get(12345)));
    }

    // ---------------------------------------------------------------
    // MinHeap
    // ---------------------------------------------------------------
    static void testMinHeap() {
        System.out.println("MinHeap:");

        MinHeap<Integer> h = new MinHeap<>();
        check("empty size == 0", h.size() == 0);
        check("empty isEmpty", h.isEmpty());
        boolean threw = false;
        try { h.peekMin(); } catch (java.util.NoSuchElementException e) { threw = true; }
        check("peekMin on empty throws", threw);
        threw = false;
        try { h.extractMin(); } catch (java.util.NoSuchElementException e) { threw = true; }
        check("extractMin on empty throws", threw);

        h.insert(10);
        check("one element peekMin == 10", h.peekMin() == 10);
        check("one element extractMin == 10", h.extractMin() == 10);
        check("after extract, empty again", h.isEmpty());

        int[] vals = {5, 3, 8, 1, 9, 2, 7, 4, 6, 0};
        for (int v : vals) h.insert(v);
        check("multiple elements size == 10", h.size() == 10);
        check("heap property holds after inserts", h.isValidHeap());

        // duplicates
        MinHeap<Integer> dup = new MinHeap<>();
        dup.insert(4); dup.insert(4); dup.insert(4);
        check("duplicates size == 3", dup.size() == 3);
        check("duplicates heap property holds", dup.isValidHeap());
        check("duplicates extractMin == 4", dup.extractMin() == 4);

        // non-decreasing extraction order + heap property after each extract
        boolean nonDecreasing = true;
        boolean validAfterEachExtract = true;
        int prev = Integer.MIN_VALUE;
        while (!h.isEmpty()) {
            int m = h.extractMin();
            if (m < prev) nonDecreasing = false;
            prev = m;
            if (!h.isValidHeap()) validAfterEachExtract = false;
        }
        check("extractMin returns non-decreasing sequence", nonDecreasing);
        check("heap property holds after every extraction", validAfterEachExtract);

        // large input cross-validated against java.util.PriorityQueue
        MinHeap<Integer> big = new MinHeap<>();
        PriorityQueue<Integer> ref = new PriorityQueue<>();
        Random rnd = new Random(42);
        for (int i = 0; i < 20000; i++) {
            int v = rnd.nextInt(1_000_000);
            big.insert(v);
            ref.add(v);
        }
        check("large input heap property holds", big.isValidHeap());
        boolean orderMatches = true;
        while (!ref.isEmpty()) {
            if (!big.extractMin().equals(ref.poll())) { orderMatches = false; break; }
        }
        check("large input extraction order matches java.util.PriorityQueue", orderMatches);
    }

    public static void main(String[] args) {
        testDynamicArray();
        testLinkedList();
        testMinHeap();
        System.out.println();
        System.out.println("Passed: " + passed + ", Failed: " + failed);
        if (failed > 0) System.exit(1);
    }
}
