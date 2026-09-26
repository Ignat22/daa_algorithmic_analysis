public class DynamicArray<T> {

    private Object[] data;
    private int size;

    private long opCounter = 0;

    public DynamicArray() {
        this(16);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) initialCapacity = 1;
        data = new Object[initialCapacity];
        size = 0;
    }

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

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= data.length) return;
        int newCapacity = data.length;
        while (newCapacity < minCapacity) newCapacity *= 2;
        Object[] newData = new Object[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);
        data = newData;
    }

    public void add(T x) {
        ensureCapacity(size + 1);
        data[size] = x;
        size++;
        opCounter++; // one write
    }

    public void add(int index, T x) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(size + 1);
        // shift elements [index, size) one position to the right
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
            opCounter++; // one element move
        }
        data[index] = x;
        opCounter++;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        T removed = (T) data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
            opCounter++; // one element move
        }
        data[size - 1] = null;
        size--;
        return removed;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        opCounter++; // one direct access
        return (T) data[index];
    }

    public boolean contains(T x) {
        for (int i = 0; i < size; i++) {
            opCounter++; // one comparison
            if (data[i] == null ? x == null : data[i].equals(x)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[i]);
            if (i < size - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
