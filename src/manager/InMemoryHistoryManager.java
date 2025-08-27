package manager;

import tasks.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_LIMIT = 10;

    private Node head;
    private Node tail;

    private final Map<Integer, Node> nodeMap = new HashMap<>();

    private int size = 0;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        // Удаляем задачу из списка, если она уже есть
        Node existingNode = nodeMap.get(task.getId());
        if (existingNode != null) {
            removeNode(existingNode);
        }

        linkLast(task);

        nodeMap.put(task.getId(), tail);

        if (size > HISTORY_LIMIT) {
            removeFirst();
        }
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.get(id);
        if (node != null) {
            removeNode(node);
            nodeMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        Node newNode = new Node(task);

        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.prev = tail;
            tail.next = newNode;
            tail = newNode;
        }

        size++;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;

        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }

        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        node.prev = null;
        node.next = null;

        size--;
    }

    // Удаляет первый элемент списка (для ограничения размера)
    private void removeFirst() {
        if (head != null) {
            Node firstNode = head;
            removeNode(firstNode);
            nodeMap.remove(firstNode.task.getId());
        }
    }
}
