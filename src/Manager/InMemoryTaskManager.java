package Manager;

import Tasks.Status;
import Tasks.Task;
import Tasks.Epic;
import Tasks.Subtask;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {

    public int nextId = 1;
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();
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
        updateEpicStatus(epic); // Автоматическое обновление статуса эпика

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
        updateEpicStatus(epic); // После обновления статуса подзадачи обновляем статус эпика
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

    // Метод для получения текущего статуса задачи по ID
    @Override
    public Task getTask(int id) {
        if (tasks.get(id) != null) getHistory();
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) != null) getHistory();
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.get(id) != null) getHistory();
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Удаление по id
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
            }
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask sub = subtasks.remove(id);
        if (sub != null) {
            Epic epic = epics.get(sub.getEpicId());
            if (epic != null) {
                epic.getSubtaskIds().remove(Integer.valueOf(id));
                updateEpicStatus(epic);
            }
        }
    }

    // Удаление всех задач
    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        // Также удаляем все подзадачи
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        // Обновляем статус всех эпиков после удаления подзадач
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(Status.NEW);
        }
    }
    // Метод для автоматического обновления статуса эпика по его подзадачам
    protected void updateEpicStatus(Epic epic) {
        // Проверка на null
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
