package Manager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import java.util.List;

public interface TaskManager {
    // Создание новой задачи
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtask(Subtask subtask);

    // Получение списка всех задач
    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<Subtask> getAllSubtasks();

    void updateTask(Task task);

    void updateEpicDetails(Epic epic);

    void updateSubtask(Subtask subtask);

    // Получение всех подзадач по ID эпика
    List<Subtask> getSubtasksByEpicId(int epicId);

    // Метод для получения текущего статуса задачи по ID
    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    // Удаление по id
    void deleteTaskById(int id);

    void deleteEpicById(int id);

    void deleteSubtaskById(int id);

    // Удаление всех задач
    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    List<Task> getHistory();
}
