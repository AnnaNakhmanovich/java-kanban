package Manager;

import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

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

        // Обновляем статус эпиков по их подзадачам
        for (Epic ep : taskManager.getAllEpics()) {
            taskManager.updateEpicStatus(ep);
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

    }
}
