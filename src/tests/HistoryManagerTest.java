package tests;
import manager.HistoryManager;
import manager.Managers;
import tasks.Task;
import tasks.Status;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    @Test
    void historyManagerStoresPreviousTaskVersion() {
        HistoryManager history = Managers.getDefaultHistory();
        Task t1 = new Task(1, "t", "d", Status.NEW);
        history.add(t1);
        Task t2 = new Task(1, "changed", "d", Status.NEW);
        history.add(t2);
        List<Task> hist = history.getHistory();
        assertEquals("t", hist.get(0).getName());
        assertEquals("changed", hist.get(1).getName());
    }
}