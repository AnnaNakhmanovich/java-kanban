package tests;

import manager.Managers;
import manager.TaskManager;
import tasks.Status;
import tasks.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class TestHistoryRemoval {

    private TaskManager taskManager;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();

        task1 = new Task(0, "Задача 1", "Описание 1", Status.NEW);
        task2 = new Task(0, "Задача 2", "Описание 2", Status.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
    }

    @Test
    void testHistoryAfterAddingTasks() {
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());

        List<Task> history = taskManager.getHistory();

        assertEquals(2, history.size());

        assertTrue(history.stream().anyMatch(task -> task.getId() == task1.getId()));
        assertTrue(history.stream().anyMatch(task -> task.getId() == task2.getId()));
    }

    @Test
    void testHistoryAfterRemovingTask() {
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());

        taskManager.deleteTaskById(task1.getId());

        List<Task> history = taskManager.getHistory();

        assertFalse(history.stream().anyMatch(task -> task.getId() == task1.getId()));

        assertTrue(history.stream().anyMatch(task -> task.getId() == task2.getId()));

        assertEquals(1, history.size());
    }

    @Test
    void testHistoryAfterRemovingNonExistentTask() {
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());

        List<Task> historyBefore = taskManager.getHistory();
        assertEquals(2, historyBefore.size());

        // Удаляем несуществующую задачу
        taskManager.deleteTaskById(999);

        List<Task> historyAfter = taskManager.getHistory();

        // История не должна измениться
        assertEquals(2, historyAfter.size());
        assertTrue(historyAfter.stream().anyMatch(task -> task.getId() == task1.getId()));
        assertTrue(historyAfter.stream().anyMatch(task -> task.getId() == task2.getId()));
    }

    @Test
    void testHistoryWithDuplicateViews() {
        // Добавляем task1 несколько раз
        taskManager.getTask(task1.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task1.getId());

        List<Task> history = taskManager.getHistory();

        // В истории должна быть только одна запись для task1
        assertEquals(1, history.size());
        assertEquals(task1.getId(), history.get(0).getId());
    }

    @Test
    void testHistoryOrderAfterDuplicateViews() {
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());

        taskManager.getTask(task1.getId());

        List<Task> history = taskManager.getHistory();

        // Проверяем порядок: task2 должен быть первым, task1 - последним
        assertEquals(2, history.size());
        assertEquals(task2.getId(), history.get(0).getId());
        assertEquals(task1.getId(), history.get(1).getId());
    }
}