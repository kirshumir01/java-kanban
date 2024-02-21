package ru.yandex.practicum.services.history;

import ru.yandex.practicum.models.Task;

public class Node<T> {
    public T data;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T data, Node<T> next) {
        this.prev = prev;
        this.data = data;
        this.next = next;
    }

    public Task getData() {
        return (Task) this.data;
    }
}
