/**
 * Singly Linked List implementation (own class, not java.util.LinkedList).
 *
 * Maintains head and tail pointers plus a size field, so add(x)
 * (append) is O(1). All index-based operations must traverse from
 * the head, so they are O(n) (or O(index) precisely).
 *
 * The same opCounter convention as DynamicArray is used: one
 * increment per node visited / compared / relinked, so the two
 * structures' instrumentation is directly comparable in benchmarks.
 */
public class LinkedList<T> {

    private static class Node<T> {
        T value;
        Node<T> next;
        Node(T value) { this.value = value; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    private long opCounter = 0;

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void resetCounter() {
        opCounter = 0;
    }

    public long getCounter() {
        return opCounter;
    }

    /** Appends x at the tail. O(1) because of the tail pointer. */
    public void add(T x) {
        Node<T> node = new Node<>(x);
        opCounter++; // one write (link)
        if (head == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    /**
     * Inserts x at the given index. Valid indices: [0, size].
     * O(1) at index 0 (no traversal needed), O(index) otherwise,
     * O(n) worst case (index == size).
     */
    public void add(int index, T x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == size) { // append, including the empty-list case
            add(x);
            return;
        }
        Node<T> node = new Node<>(x);
        if (index == 0) {
            node.next = head;
            head = node;
            if (tail == null) tail = node;
            opCounter++; // one relink
            size++;
            return;
        }
        Node<T> prev = head;
        for (int i = 0; i < index - 1; i++) {
            prev = prev.next;
            opCounter++; // one node traversed
        }
        node.next = prev.next;
        prev.next = node;
        opCounter++; // one relink
        size++;
    }

    /**
     * Removes and returns the element at index.
     * O(1) at index 0, O(index) otherwise, O(n) worst case.
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        T removed;
        if (index == 0) {
            removed = head.value;
            head = head.next;
            if (head == null) tail = null;
            opCounter++; // one relink
        } else {
            Node<T> prev = head;
            for (int i = 0; i < index - 1; i++) {
                prev = prev.next;
                opCounter++; // one node traversed
            }
            Node<T> target = prev.next;
            removed = target.value;
            prev.next = target.next;
            if (target == tail) tail = prev;
            opCounter++; // one relink
        }
        size--;
        return removed;
    }

    /** Returns the element at index. Must traverse from head: O(index), worst case O(n). */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node<T> cur = head;
        for (int i = 0; i < index; i++) {
            cur = cur.next;
            opCounter++; // one node traversed
        }
        opCounter++; // final access
        return cur.value;
    }

    /** Linear search from head. O(n) worst/average, O(1) best case. */
    public boolean contains(T x) {
        Node<T> cur = head;
        while (cur != null) {
            opCounter++; // one comparison
            if (cur.value == null ? x == null : cur.value.equals(x)) {
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> cur = head;
        while (cur != null) {
            sb.append(cur.value);
            if (cur.next != null) sb.append(", ");
            cur = cur.next;
        }
        return sb.append("]").toString();
    }
}
