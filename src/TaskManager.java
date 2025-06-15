import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TaskManager {

    private int nextId = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    // Создание новой задачи
    public Task createTask(Task task) {
        if (task == null) {
            System.out.println("Необходимо ввести значение задачи");
            return null; // или можно вернуть что-то другое, например, пустой объект
        }
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Необходимо ввести значение эпика'");
            return null; // или можно вернуть что-то другое, например, пустой объект
        }
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) {
            System.out.println("Необходимо ввести значение задачи'");
            return null; // или можно вернуть что-то другое, например, пустой объект
        }
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        // добавляем подзадачу в список эпика
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic); // Обновляем статус эпика при добавлении подзадачи
        }
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

    // Общий метод для обновления любой задачи (замена полностью)
    public void updateTask(Task task) {
        if (task == null) {
            System.out.println("Не может быть null");
        }
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
            // Для обычных задач статус можно менять напрямую
        } else if (epics.containsKey(id)) {
            // Обновление эпика — статус не меняется вручную
            epics.put(id, (Epic) task);
            // Можно дополнительно вызвать updateEpicStatus(epic), если нужно
            // Но по условию статус меняется только при изменении подзадач
        } else if (subtasks.containsKey(id)) {

            subtasks.put(id, (Subtask) task);
            // После обновления подзадачи обновляем статус эпика
            updateEpicStatus(epics.get(((Subtask) task).getEpicId()));
        } else {
            System.out.println("Задача с id " + id + " не найдена");
        }
    }

    // Метод для автоматического обновления статуса эпика по его подзадачам
    private void updateEpicStatus(Epic epic) {
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

    public void refreshEpicStatus(int epicId) {
        updateEpicStatus(epics.get(epicId));
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

    // Методы для добавления новых задач/эпиков/подзадач
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        // Добавляем ID подзадачи в список эпика
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic); // Обновляем статус эпика при добавлении новой подзадачи
        }
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
}
