package manager;

import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

        // Создаем две задачи
        Task task1 = new Task(0, "Задача 1", "Описание задачи 1", Status.NEW);
        Task task2 = new Task(0, "Задача 2", "Описание задачи 2", Status.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Создаем эпик с двумя подзадачами
        Epic epic1 = new Epic(0, "Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask(0, "Подзадача 1", "Описание подзадачи 1", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask(0, "Подзадача 2", "Описание подзадачи 2", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        // Создаем эпик с одной подзадачей
        Epic epic2 = new Epic(0, "Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask(0, "Подзадача 3", "Описание подзадачи 3", Status.NEW, epic2.getId());
        taskManager.createSubtask(subtask3);

        // Распечатываем все эпики и их подзадачи
        System.out.println("Эпики:");
        for (Epic ep : taskManager.getAllEpics()) {
            System.out.println(ep);
            List<Subtask> subs = taskManager.getSubtasksByEpicId(ep.getId());
            System.out.println("Подзадачи " + ep.getName() + ":");
            for (Subtask s : subs) {
                System.out.println(s);
            }
            System.out.println();
        }

        // Распечатываем все задачи
        System.out.println("Задачи:");
        for (Task t : taskManager.getAllTasks()) {
            System.out.println(t);
        }
        System.out.println();

        // Распечатываем все подзадачи
        System.out.println("Все подзадачи:");
        for (Subtask s : taskManager.getAllSubtasks()) {
            System.out.println(s);
        }
        System.out.println();

        // Изменяем статусы задач
        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);

        // Обновляем задачи в менеджере
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        // Обновляем статусы подзадач
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);

        // Обновляем подзадачи в менеджере
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        // После обновления статусов распечатываем снова
        System.out.println("После изменения статусов:");

        System.out.println("Задачи:");
        for (Task t : taskManager.getAllTasks()) {
            System.out.println(t);
        }

        System.out.println("\nПодзадачи:");
        for (Subtask s : taskManager.getAllSubtasks()) {
            System.out.println(s);
        }

        // Распечатываем обновленные эпики и их подзадачи с актуальными статусами
        System.out.println("\nОбновленные эпики после пересчета статусов:");
        for (Epic ep : taskManager.getAllEpics()) {
            System.out.println(ep);
            List<Subtask> subs = taskManager.getSubtasksByEpicId(ep.getId());
            System.out.println("Подзадачи " + ep.getName() + ":");
            for (Subtask s : subs) {
                System.out.println(s);
            }
            System.out.println();
        }

        // Демонстрация истории просмотров
        System.out.println("\nВызов task1");
        taskManager.getTask(task1.getId());
        printHistory(taskManager);

        System.out.println("\nВызов epic1");
        taskManager.getEpic(epic1.getId());
        printHistory(taskManager);

        System.out.println("\nВызов subtask1");
        taskManager.getSubtask(subtask1.getId());
        printHistory(taskManager);

        System.out.println("\nВызов task2:");
        taskManager.getTask(task2.getId());
        printHistory(taskManager);

        System.out.println("\nВызов epic2");
        taskManager.getEpic(epic2.getId());
        printHistory(taskManager);

        System.out.println("\nВызов subtask3");
        taskManager.getSubtask(subtask3.getId());
        printHistory(taskManager);


        System.out.println("\nВызов getSubtask(subtask3.getId()):");
        taskManager.getSubtask(subtask3.getId());
        printHistory(taskManager);

        System.out.println("\n=== ДОПОЛНИТЕЛЬНЫЙ СЦЕНАРИЙ ТЕСТИРОВАНИЯ ИСТОРИИ ===");

        System.out.println("\n--- Тест 1: Повторные запросы task1 ---");
        taskManager.getTask(task1.getId());
        printHistory(taskManager);

        System.out.println("\n--- Тест 2: Повторные запросы epic1 ---");
        taskManager.getEpic(epic1.getId());
        printHistory(taskManager);

        System.out.println("\n--- Тест 3: Повторные запросы subtask1 ---");
        taskManager.getSubtask(subtask1.getId());
        printHistory(taskManager);

        System.out.println("\n--- Тест 4: Еще раз task1 ---");
        taskManager.getTask(task1.getId());
        printHistory(taskManager);

        System.out.println("\n=== ТЕСТИРОВАНИЕ УДАЛЕНИЯ ===");

        System.out.println("\n--- Тест 5: Удаляем task2 ---");
        taskManager.deleteTaskById(task2.getId());
        printHistory(taskManager);

        System.out.println("\n--- Тест 6: Удаляем epic1 с подзадачами ---");
        taskManager.deleteEpicById(epic1.getId());
        printHistory(taskManager);

        System.out.println("\n=== ФИНАЛЬНАЯ ПРОВЕРКА ===");
        System.out.println("Оставшиеся объекты:");
        System.out.println("Все задачи: " + taskManager.getAllTasks().size());
        System.out.println("Все эпики: " + taskManager.getAllEpics().size());
        System.out.println("Все подзадачи: " + taskManager.getAllSubtasks().size());

        System.out.println("\nПоехали!");
    }

        private static void printHistory (TaskManager manager){
            System.out.println("История просмотров:");
            List<Task> history = manager.getHistory();
            if (history.isEmpty()) {
                System.out.println("  История пуста");
            } else {
                for (int i = 0; i < history.size(); i++) {
                    Task task = history.get(i);
                    String type = getTaskType(task);
                    System.out.println("  " + (i + 1) + ". " + type + " ID:" + task.getId() + " - " + task.getName());
                }
            }
        }

        private static String getTaskType (Task task){
            if (task instanceof Epic) {
                return "ЭПИК";
            } else if (task instanceof Subtask) {
                return "ПОДЗАДАЧА";
            } else {
                return "ЗАДАЧА";
            }
        }
    }
