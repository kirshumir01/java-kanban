package ru.yandex.practicum.services.history;
import ru.yandex.practicum.models.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    // создать хэш-таблицу: ключ - id Task, значение - узел связного списка, который хранит ссылку на Task
    private final Map<Integer, Node> nodesByTaskId = new HashMap<Integer, Node>();
    // создать двусвязный список, который сохраняет Task со ссылкой на её позицию в списке
    private final List<Task> nodesOfTasks = new LinkedList<>();
    private Node<Task> last;

    // создать метод linkLast, который добавляет Task в связный список
    private void linkLast(Task task) {
        // текущий элемент в списке станет предыдущим для добавляемого элемента Task
        Node<Task> currentNode = last;
        // определить позицию (ссылку) в списке для элемента Task, планируемого к добавлению в список
        Node<Task> newNode = new Node<>(last, task, null);
        // если связный список пуст, добавляемый task станет первым и последним (last)
        if (last == null) {
            last = newNode;
        // иначе - добавляемый task займет следйющую позицию и становится последним (last)
        } else {
            currentNode.next = newNode;
            last = newNode;
        }
    }

    // переопределить метод с учетом новых требований - удаление из истории просмотра повторных просмотров
    // в истории должен остаться только последний просмотр задачи
    @Override
    public void addHistory(Task task) {
        // если nodesByTask пуст - определить позицию task в связном списке и добавить в хэш-мапу с привязкой по id task
        if (nodesByTaskId.isEmpty()) {
            linkLast(task);
            nodesOfTasks.add(task);
            nodesByTaskId.put(task.getId(), last);
        // иначе - если nodesByTask не пуст, выполнить поиск task по id в хэш-таблице
        } else {
            if (nodesByTaskId.containsKey(task.getId())) {
                Map<Integer, Node> newTasks = new HashMap<>(nodesByTaskId);
                // если в хэш-мапе уже сохранен просмотр task, выполнить поиск task по id и удалить узел и просмотр
                for (Integer id : newTasks.keySet()) {
                    if (id == task.getId()) {
                        // в отличии от ArrayList элементы после удаления в связном списке не смещаются
                        // это обеспечивает константную сложность O(1) выполнения операции удаления
                        // удалить узел task из связного списка nodesOfTasks
                        removeNode(newTasks.get(task.getId()));
                        // определить позицию (ссылку) для task в связном списке
                        // и добавить в хэш-мапу новый просмотр task
                        linkLast(task);
                        nodesOfTasks.add(task);
                        nodesByTaskId.put(task.getId(), last);
                    }
                }
            // иначе - если task ранее не было в истории просмотров, добавить task в хэш-мапу
            } else {
                linkLast(task);
                nodesOfTasks.add(task);
                nodesByTaskId.put(task.getId(), last);
            }
        }
    }

    // определить метод для удаления узла task из спика nodesOfTasks
    public void removeNode(Node<Task> node) {
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;

        if (prev == null) {
            next = null;
            nodesOfTasks.remove(node.getData());
            nodesByTaskId.remove(node.getData().getId());
        } else if (next == null) {
            prev.next = null;
            nodesOfTasks.remove(node.getData());
            nodesByTaskId.remove(node.getData().getId());
        } else {
            next.prev = prev.next;
            prev.next = next.prev;
            nodesOfTasks.remove(node.getData());
            nodesByTaskId.remove(node.getData().getId());
        }
    }

    // переопределить метод удаления задачи из истории просмотра
    @Override
    public void remove(int id) {
        if (nodesByTaskId.get(id) != null) {
            removeNode(nodesByTaskId.get(id));
        }
    }

    // вернуть список истории просмотров, приведенный из связного списка к ArrayList<>()
    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(nodesOfTasks);
    }
}
