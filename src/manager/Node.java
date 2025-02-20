package manager;

public class Node<T> {
    public T data;
    public Node<T> next;
    public Node<T> prev;
    Integer key;

    public Node(Node<T> prev, T data, Node<T> next, Integer key) {
        this.prev = prev;
        this.data = data;
        this.next = next;
        this.key = key;
    }
}
