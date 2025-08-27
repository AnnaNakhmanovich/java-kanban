package tests;

import manager.Managers;
import manager.TaskManager;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void inMemoryTaskManagerStoresAndFindsTasksById() {
        TaskManager manager = Managers.getDefault();
        Task t = new Task(0, "t", "d", Status.NEW);
        Epic e = new Epic(0, "e", "d");
        manager.createTask(t);
        manager.createEpic(e);
        Subtask s = new Subtask(0, "s", "d", Status.NEW, e.getId());
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
    void taskIsNotChangedAfterAddingToManager_basicFieldsReadBack() {
        TaskManager manager = Managers.getDefault();
        Task t = new Task(0, "t", "d", Status.NEW);
        manager.createTask(t);
        Task fromManager = manager.getTask(t.getId());
        assertEquals(t.getName(), fromManager.getName());
        assertEquals(t.getDescription(), fromManager.getDescription());
        assertEquals(t.getStatus(), fromManager.getStatus());
    }

    // HISTORY: удаление из менеджера удаляет из истории
    @Test
    void removesFromHistoryWhenDeletingTask() {
        TaskManager manager = Managers.getDefault();
        Task t = new Task(0, "t", "d", Status.NEW);
        manager.createTask(t);

        // просмотр
        manager.getTask(t.getId());
        assertEquals(1, manager.getHistory().size());

        // удаление
        manager.deleteTaskById(t.getId());
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void removesFromHistoryWhenDeletingEpicAlsoSubtasks() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic(0, "e", "d");
        manager.createEpic(epic);
        Subtask s1 = new Subtask(0, "s1", "d", Status.NEW, epic.getId());
        Subtask s2 = new Subtask(0, "s2", "d", Status.NEW, epic.getId());
        manager.createSubtask(s1);
        manager.createSubtask(s2);

        // просмотры
        manager.getEpic(epic.getId());
        manager.getSubtask(s1.getId());
        manager.getSubtask(s2.getId());
        assertEquals(3, manager.getHistory().size());

        manager.deleteEpicById(epic.getId());
        assertTrue(manager.getHistory().isEmpty(), "История должна очищаться для эпика и всех его подзадач");
    }

    @Test
    void removesFromHistoryWhenDeletingSubtask() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic(0, "e", "d");
        manager.createEpic(epic);
        Subtask s = new Subtask(0, "s", "d", Status.NEW, epic.getId());
        manager.createSubtask(s);

        manager.getSubtask(s.getId());
        assertEquals(1, manager.getHistory().size());

        manager.deleteSubtaskById(s.getId());
        assertTrue(manager.getHistory().isEmpty());
    }

    // Целостность: внутри эпика не должно оставаться неактуальных id подзадач
    @Test
    void deleteSubtaskRemovesItsIdFromEpic() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic(0, "e", "d");
        manager.createEpic(epic);
        Subtask s = new Subtask(0, "s", "d", Status.NEW, epic.getId());
        manager.createSubtask(s);

        assertTrue(manager.getEpic(epic.getId()).getSubtaskIds().contains(s.getId()));
        manager.deleteSubtaskById(s.getId());
        assertFalse(manager.getEpic(epic.getId()).getSubtaskIds().contains(s.getId()));
    }

    @Test
    void deleteAllSubtasksClearsEpicSubtaskIds() {
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic(0, "e", "d");
        manager.createEpic(epic);
        Subtask s1 = new Subtask(0, "s1", "d", Status.NEW, epic.getId());
        Subtask s2 = new Subtask(0, "s2", "d", Status.NEW, epic.getId());
        manager.createSubtask(s1);
        manager.createSubtask(s2);

        manager.deleteAllSubtasks();

        Epic storedEpic = manager.getEpic(epic.getId());
        assertNotNull(storedEpic);
        assertTrue(storedEpic.getSubtaskIds().isEmpty(), "После удаления всех подзадач список id в эпике должен быть пуст");
    }

    // LRU: повторные просмотры не дублируются, перемещают задачу в конец
    @Test
    void duplicateViewsAreDeduplicatedAndMovedToEnd() {
        TaskManager manager = Managers.getDefault();
        Task t1 = new Task(0, "t1", "d", Status.NEW);
        Task t2 = new Task(0, "t2", "d", Status.NEW);
        manager.createTask(t1);
        manager.createTask(t2);

        manager.getTask(t1.getId()); // [t1]
        manager.getTask(t2.getId()); // [t1, t2]
        manager.getTask(t1.getId()); // [t2, t1]

        List<Task> hist = manager.getHistory();
        assertEquals(2, hist.size());
        assertEquals(t2.getId(), hist.get(0).getId());
        assertEquals(t1.getId(), hist.get(1).getId());
    }

    // Мутации: изменение полученного из менеджера объекта влияет на данные менеджера
    // Этот тест демонстрирует проблему (mutable shared state)
    @Test
    void taskMutationAffectsManagerData() {
        TaskManager manager = Managers.getDefault();
        Task t = new Task(0, "t", "d", Status.NEW);
        manager.createTask(t);

        Task fromManager = manager.getTask(t.getId());
        fromManager.setName("mutated");

        // В текущей реализации это изменение отразится в менеджере
        Task again = manager.getTask(t.getId());
        assertEquals("mutated", again.getName(), "Мутация возвращенного объекта влияет на данные менеджера");
        // Возможные решения: делать копии при выдаче/сохранении, либо сделать Task/прочие объекты иммутабельными.
    }
}
