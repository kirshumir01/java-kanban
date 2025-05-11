package ru.yandex.practicum.services.history;
import ru.yandex.practicum.models.Task;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    protected Map<Integer, Node> nodesByTaskIdMap = new HashMap<Integer, Node>();
    private Node<Task> first;
    private Node<Task> last;
    private int size = 0;

    private void linkLast(Task task) {
        final Node<Task> currentNode = last;
        final Node<Task> newNode = new Node<>(currentNode, task, null);
        last = newNode;

        if (currentNode == null) {
            first = newNode;
        } else {
            currentNode.next = newNode;
        }
        size++;
    }

    private List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();
        Node<Task> node = first;

        while (node != null) {
            historyList.add(node.data);
            node = node.next;
        }
        return historyList;
    }

    private void removeNode(Node node) {
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;

        if (size == 1) {
            first = null;
            node.data = null;
            last = null;
        } else if (prev == null) {
            first = next;
            next.prev = null;
            node.next = null;
            node.data = null;
        } else if (next == null) {
            last = prev;
            prev.next = null;
            node.prev = null;
            node.data = null;
        } else {
            prev.next = next;
            next.prev = prev;
            node.next = null;
            node.prev = null;
            node.data = null;
        }
        if (size != 0) {
            size--;
        }
    }

    @Override
    public void add(Task task) {
        if (nodesByTaskIdMap.isEmpty()) {
            linkLast(task);
            nodesByTaskIdMap.put(task.getId(), last);
        } else {
            if (nodesByTaskIdMap.containsKey(task.getId())) {
                remove(task.getId());
                linkLast(task);
                nodesByTaskIdMap.put(task.getId(), last);
            } else {
                linkLast(task);
                nodesByTaskIdMap.put(task.getId(), last);
            }
        }
    }

    @Override
    public void remove(int id) {
        if (nodesByTaskIdMap.get(id) != null) {
            removeNode(nodesByTaskIdMap.get(id));
            nodesByTaskIdMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
