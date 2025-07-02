package tests;

import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.Status;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksWithSameIdAreEqual() {
        Task t1 = new Task(1, "a", "b", Status.NEW);
        Task t2 = new Task(1, "x", "y", Status.DONE);
        assertEquals(t1, t2);
    }

    @Test
    void epicsWithSameIdAreEqual() {
        Epic e1 = new Epic(2, "a", "b");
        Epic e2 = new Epic(2, "x", "y");
        assertEquals(e1, e2);
    }

    @Test
    void subtasksWithSameIdAreEqual() {
        Subtask s1 = new Subtask(3, "a", "b", Status.NEW, 1);
        Subtask s2 = new Subtask(3, "x", "y", Status.DONE, 2);
        assertEquals(s1, s2);
    }

    @Test
    void epicCannotContainItselfAsSubtask() {
        Epic epic = new Epic(1, "Epic", "desc");
        epic.addSubtaskId(1);
        assertFalse(epic.getSubtaskIds().contains(1), "Epic не должен содержать себя как подзадачу");
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        Subtask subtask = new Subtask(5, "Sub", "desc", Status.NEW, 5);
        assertNotEquals(subtask.getId(), subtask.getEpicId(), "Subtask не может быть своим же эпиком");
    }
}