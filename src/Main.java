import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.service.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        TaskManager inMemoryTaskManager =  Manager.getDefault();

        System.out.println("Поехали!");
        System.out.println("Запускаем тест...\n");

        System.out.println("Добавление различных задач согласно задания на тест\n");

        Task task = new Task("Первая простая задача",
                "Детали к первой простой задачи",
                TaskStatus.NEW);
        int newID = inMemoryTaskManager.createTask(task);
        System.out.println(newID);

        task = new Task("Вторая простая задача",
                "Детали ко второй простой задачи",
                TaskStatus.NEW);
        newID = inMemoryTaskManager.createTask(task);
        System.out.println(newID);

        Epic epic = new Epic("Первый эпик",
                "Детали первого эпика",
                TaskStatus.NEW, new ArrayList<>());
        newID = inMemoryTaskManager.createTask(epic);
        System.out.println(newID);

        Subtask subtask = new Subtask("Первая подзадача к первому эпику",
                "Детали первой подзадачи к первому эпику", TaskStatus.NEW, 3);
        newID = inMemoryTaskManager.createTask(subtask);
        System.out.println(newID);

        subtask = new Subtask("Вторая подзадача к первому эпику",
                "Детали второй подзадачи к первому эпику", TaskStatus.NEW, 3);
        newID = inMemoryTaskManager.createTask(subtask);
        System.out.println(newID);

        subtask = new Subtask("Третья подзадача к первому эпику",
                "Детали третьей подзадачи к первому эпику", TaskStatus.NEW, 3);
        newID = inMemoryTaskManager.createTask(subtask);
        System.out.println(newID);

        epic = new Epic("Второй эпик",
                "Детали второго эпика",
                TaskStatus.NEW, new ArrayList<>());
        newID = inMemoryTaskManager.createTask(epic);
        System.out.println(newID);

        System.out.println("\nПечать всех задач");
        System.out.println(inMemoryTaskManager.printAll());

        System.out.println("1 - Просмотр задачи \n" +
                "2 - Добавить автоматически в историю все задачи несколько раз и вывести историю  \n" +
                "3 - История просмотра  \n" +
                "4 - удалить задачу  \n" +
                "5 - выход  \n");
        int command = scanner.nextInt();

            while (true) {
                if (command == 1) {
                    System.out.println("Введите номер задачи");
                    int id = scanner.nextInt();
                    System.out.println(inMemoryTaskManager.retrieveTaskById(id));
                }
                if (command == 2) {
                    for (int i = 1; i < 8; i++) {
                    inMemoryTaskManager.retrieveTaskById(i);
                    }
                    for (int i = 7; i > 3; i--) {
                        inMemoryTaskManager.retrieveTaskById(i);
                    }
                    inMemoryTaskManager.retrieveTaskById(1);
                    inMemoryTaskManager.retrieveTaskById(7);
                    //     System.out.println(Manager.getDefaultHistory().printHistory());
                } else if (command == 3) {
                    System.out.println(Manager.getDefaultHistory().printHistory());

                } else if (command == 4) {
                    System.out.println("Введите номер задачи");
                    int idDelete = scanner.nextInt();
                    System.out.println(inMemoryTaskManager.deleteTask(idDelete));
                }  if (command == 5) {
                    break;
                }
                System.out.println("1 - Просмотр задачи \n" +
                        "2 - Добавить автоматически в историю 14 задач и вывести историю  \n" +
                        "3 - История просмотра  \n" +
                        "4 - удалить задачу  \n" +
                        "5 - выход  \n");
                command = scanner.nextInt();
            }
    }
}
