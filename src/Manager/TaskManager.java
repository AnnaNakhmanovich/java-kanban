package Manager;

import Tasks.Status;
import Tasks.Task;
import Tasks.Epic;
import Tasks.Subtask;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TaskManager {

    public int nextId = 1;
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();

    // Создание новой задачи
    public Task createTask(Task task) {
        if (task == null) {
           return null;
        }
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

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
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void updateTask(Task task) {
        if (task == null) {
            return;
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpicDetails(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }
        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        updateEpicStatus(epic); // После обновления статуса подзадачи обновляем статус эпика
}

       // Получение всех подзадач по ID эпика
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
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    // Удаление по id
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subId : epic.getSubtaskIds()) {
                subtasks.remove(subId);
            }
        }
    }

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
    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
        // Также удаляем все подзадачи
        subtasks.clear();
    }

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
