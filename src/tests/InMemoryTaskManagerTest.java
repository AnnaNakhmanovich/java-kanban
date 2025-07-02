package tests;

import manager.Managers;
import manager.TaskManager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    @Test
    void inMemoryTaskManagerStoresAndFindsTasksById() {
        TaskManager manager = Managers.getDefault();
        Task t = new Task(0, "t", "d", Status.NEW);
        Epic e = new Epic(0, "e", "d");
        Subtask s = new Subtask(0, "s", "d", Status.NEW, 2);
        manager.createTask(t);
        manager.createEpic(e);
        manager.createSubtask(s);
        assertEquals(t, manager.getTask(t.getId()));
        assertEquals(e, manager.getEpic(e.getId()));
        assertEquals(s, manager.getSubtask(s.getId()));
    }

    @Test
    void manualAndAutoIdDoNotConflict() {
        TaskManager manager = Managers.getDefault();
        Task t1 = new Task(100, "t1", "d", Status.NEW);
        manager.createTask(t1);
        Task t2 = new Task(0, "t2", "d", Status.NEW);
        manager.createTask(t2);
        assertNotEquals(t1.getId(), t2.getId());
    }

    @Test
    void taskIsNotChangedAfterAddingToManager() {
        TaskManager manager = Managers.getDefault();
        Task t = new Task(0, "t", "d", Status.NEW);
        manager.createTask(t);
        Task fromManager = manager.getTask(t.getId());
        assertEquals(t.getName(), fromManager.getName());
        assertEquals(t.getDescription(), fromManager.getDescription());
        assertEquals(t.getStatus(), fromManager.getStatus());
    }
}