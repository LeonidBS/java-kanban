import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.TaskManager;
import ru.yandex.practicum.kanban.service.TaskStatus;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        System.out.println("Запускаем тест...\n");

        TaskManager taskManager = new TaskManager();

        System.out.println("Добавление 7 различных задач согласно задания на тест\n");
        Task task = new Task("Первая простая задача: сделать уроки",
                "Пройти темы2 Вычисляем хеш-код через hashCode(). Выполнить задание",
                TaskStatus.NEW);
        int newID = taskManager.createTask(task);
        System.out.println(newID);

        task = new Task( "Вторая простая задача: сделать анализ чертежей ПС",
                "Проверить габариты РУ 10кВ, кабкльные присоединения.",
                TaskStatus.NEW);
        newID = taskManager.createTask(task);
        System.out.println(newID);

        Epic epic = new Epic("Первый эпик: Сходить в магазин.",
                "Сходить в 'Пятёрку', купить хлеб  и молоко.",
                TaskStatus.NEW, new ArrayList<>());
        newID = taskManager.createTask(epic);
        System.out.println(newID);

        epic = new Epic("Второй эпик: Убраться в квартире",
                "Пропылесосить и помыть пол с участием робота",
                TaskStatus.NEW, new ArrayList<>());
        newID = taskManager.createTask(epic);
        System.out.println(newID);

        Subtask subtask = new Subtask("Первая подзадача к первому эпику",
                "Одется и обуться", TaskStatus.NEW, 3);
        newID = taskManager.createTask(subtask);
        System.out.println(newID);

        subtask = new Subtask("Вторая подзадача к первому эпику",
                "Дойти до маганзина и купить продукты", TaskStatus.NEW, 3);
        newID = taskManager.createTask(subtask);
        System.out.println(newID);

        subtask = new Subtask("Первая подзадача ко второму эпику",
                "Открыть приложение робота-пылесоса", TaskStatus.NEW, 4);
        newID = taskManager.createTask(subtask);
        System.out.println(newID);

        System.out.println("\nПечать отдельных типов задач согласно заданию на тест");
        System.out.println(taskManager.printTasks());
        System.out.println(taskManager.printEpics());
        System.out.println(taskManager.printSubtasks());

        System.out.println("\nПечать всех задач");
        System.out.println(taskManager.printAll());

        System.out.println("\nПечать подзадач первого эпика");
        System.out.println(taskManager.printSubtasksByEpic(3));

        System.out.println("\n Печать задачи 1 по идентификатору");
        System.out.println(taskManager.obtainTaskById(1));


        System.out.println("\nОбновление первой простой задачи");
        task = new Task(1, "Первая простая задача: сделать уроки",
                "Пройти темы2 Вычисляем хеш-код через hashCode(). Выполнить задание",
                TaskStatus.IN_PROGRESS);
        boolean success = taskManager.updateTask(task);
        System.out.println(success);

        System.out.println("\nОбновление первой подзадачи второго эпика");
        subtask = new Subtask(7,"Первая подзадача ко второму эпику",
                "Открыть приложение робота-пылесоса", TaskStatus.IN_PROGRESS, 4);
        success = taskManager.updateTask(subtask);
        System.out.println(success);

        System.out.println("\nОбновление первой подзадачи первого эпика");
        subtask = new Subtask(5,"Первая подзадача к первому эпику",
                "Одется и обуться", TaskStatus.DONE, 3);
        success = taskManager.updateTask(subtask);
        System.out.println(success);

        System.out.println("\nОбновление второй подзадачи первого эпика");
        subtask = new Subtask(6,"Вторая подзадача к первому эпику",
                "Дойти до маганзина и купить продукты", TaskStatus.IN_PROGRESS, 3);
        success = taskManager.updateTask(subtask);
        System.out.println(success);

        System.out.println("\nПечать всех задач после обновления");
        System.out.println(taskManager.printAll());

        System.out.println("\nОбновление второй подзадачи первого эпика до DONE");
        subtask = new Subtask(6,"Вторая подзадача к первому эпику",
                "Дойти до маганзина и купить продукты", TaskStatus.DONE, 3);
        success = taskManager.updateTask(subtask);
        System.out.println(success);

        System.out.println("\n Печать обновленного эпика по иднетификатору");
        System.out.println(taskManager.obtainTaskById(3));

        System.out.println("\nПечать подзадач первого обновленного эпика");
        System.out.println(taskManager.printSubtasksByEpic(3));

        System.out.println("\nУдаление второй простой задачи");
        taskManager.deleteTask(2);
        System.out.println(success);

        System.out.println("\nУдаление первого эпика");
        taskManager.deleteTask(3);
        System.out.println(success);

        System.out.println("\nУдаление первой едиственной подзадачи второго эпика");
        taskManager.deleteTask(7);
        System.out.println(success);

        System.out.println("\nПечать всех задач после удалений задач");
        System.out.println(taskManager.printAll());

        System.out.println(taskManager.printSubtasks());

    }
}
