package manager;

import tasks.Status;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {

    public int nextId = 1;
    public Map<Integer, Task> tasks = new HashMap<>();
    public Map<Integer, Epic> epics = new HashMap<>();
    public Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    // Создание новой задачи
    @Override
    public Task createTask(Task task) {
        if (task == null) {
           return null;
        }
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) {
            return null;
        }

        // Проверка, что эпик с данным ID существует
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null; // Если эпика нет, создание подзадачи невозможно
        }

        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic);

        return subtask;
    }

    // Получение списка всех задач
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpicDetails(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }
        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic);
}

       // Получение всех подзадач по ID эпика
       @Override
       public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Subtask> result = new ArrayList<>();
        for (Subtask st : subtasks.values()) {
            if (st.getEpicId() == epicId) {
                result.add(st);
            }
        }
        return result;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
        historyManager.add(task);
    }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
          historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
        historyManager.add(subtask);
    }
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Удаление по id
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            historyManager.remove(id);
            for (int subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
                historyManager.remove(subId);
            }
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask sub = subtasks.remove(id);
        if (sub != null) {
            historyManager.remove(id);
            Epic epic = epics.get(sub.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().remove(Integer.valueOf(id));
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(Status.NEW);
        }
    }

    private void updateEpicStatus(Epic epic) {
        if (epic == null) return;

        List<Subtask> subtasksOfEpic = getSubtasksByEpicId(epic.getId());

        if (subtasksOfEpic.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask st : subtasksOfEpic) {
            if (st.getStatus() != Status.DONE) {
                allDone = false;
            }
            if (st.getStatus() != Status.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (allNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
