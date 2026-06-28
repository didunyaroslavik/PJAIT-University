package logistics_system.container;

public class Box<T> {
    private T content;

    public Box() {
        this.content = null;
    }

    public Box(T content) {
        this.content = content;
    }

    public void put(T item) {
        if (this.content != null) {
            throw new IllegalStateException("Container must be emptied before reuse.");
        }
        this.content = item;
    }

    public T get() {
        if (this.content == null) {
            throw new IllegalStateException("Container is empty.");
        }
        return this.content;
    }

    public T getAndClear() {
        T item = get();
        clear();
        return item;
    }

    public boolean isEmpty() {
        return this.content == null;
    }

    public void clear() {
        this.content = null;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Box{EMPTY}";
        }
        return "Box{" + content.toString() + "}";
    }
}