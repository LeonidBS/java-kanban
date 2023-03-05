import ru.yandex.practicum.kanban.http.HttpTaskServer;
import ru.yandex.practicum.kanban.http.KVServer;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.HttpTaskManager;
import ru.yandex.practicum.kanban.service.exceptions.IdPassingException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        new KVServer().start();

        HttpTaskManager httpTaskManager = new HttpTaskManager();
        new HttpTaskServer(httpTaskManager).start();

        System.out.println("Добавляем различные задачи согласно задания ИЗ HTTP\n");
        Task task = new Task("ИЗ HTTP, Первая простая задача",
                "Детали к первой простой задачи ИЗ HTTP", 1200);
        int newID = httpTaskManager.createTask(task);
        System.out.println(newID);

        task = new Task("ИЗ HTTP, Вторая простая задача",
                "Детали ко второй простой задачи ИЗ HTTP", 2700);
        newID = httpTaskManager.createTask(task);
        System.out.println(newID);

        Epic epic = new Epic("ИЗ HTTP, Первый эпик",
                "Детали первого эпика ИЗ HTTP");
        int newEpicID = httpTaskManager.createTask(epic);
        System.out.println(newID);

        Subtask subtask = new Subtask("ИЗ HTTP, Первая подзадача к первому эпику",
                "Детали первой подзадачи к первому эпику ИЗ HTTP", newEpicID, 60);
        newID = httpTaskManager.createTask(subtask);
        System.out.println(newID);

        subtask = new Subtask("ИЗ HTTP, Вторая подзадача к первому эпику",
                "Детали второй подзадачи к первому эпику ИЗ HTTP", newEpicID, 75);
        newID = httpTaskManager.createTask(subtask);
        System.out.println(newID);

        epic = new Epic("ИЗ HTTP, Второй эпик",
                "Детали второго эпика ИЗ HTTP");
        newID = httpTaskManager.createTask(epic);
        System.out.println(newID);

        System.out.println("\nПечать всех задач");
        httpTaskManager.loadFromStorage();
        System.out.println(httpTaskManager.printAll());

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("1 - Просмотр задачи\n"
                        + "2 - Добавить автоматически в историю все задачи несколько раз\n"
                        + "3 - История просмотра\n"
                        + "4 - удалить задачу\n"
                        + "5 - выход\n"
                        + "6 - Вывод всех задач\n"
                        + "7 - Вывод таблицы календаря событий\n");
                int command = scanner.nextInt();
                if (command == 1) {
                    System.out.println("Введите номер задачи");
                    int id = scanner.nextInt();
                    try {
                        System.out.println(httpTaskManager.retrieveTaskById(id));
                    } catch (IdPassingException e) {
                        System.out.println(e.getDetailedMessage());

                    }
                }
                if (command == 2) {
                    for (int i = 2; i < 6; i++) {
                        httpTaskManager.retrieveTaskById(i);
                    }
                    for (int i = 3; i > 0; i--) {
                        httpTaskManager.retrieveTaskById(i);
                    }
                    httpTaskManager.retrieveTaskById(6);

                } else if (command == 3) {
                    System.out.println(httpTaskManager.getInMemoryHistoryManager().printHistory());
                } else if (command == 4) {
                    System.out.println("Введите номер задачи");
                    int idDelete = scanner.nextInt();
                    System.out.println(httpTaskManager.deleteTask(idDelete));
                } else if (command == 5) {
                    break;
                } else if (command == 6) {
                    System.out.println(httpTaskManager.printAll());
                } else if (command == 7) {
                    System.out.println("Введите год:");
                    int year = scanner.nextInt();
                    System.out.println("Введите месяц (число от 1 -12):");
                    int month = scanner.nextInt();
                    for (Map.Entry<LocalDateTime, Task> entry :
                            httpTaskManager.getTimeSlotMap().entrySet()) {
                        if (entry.getKey().getMonthValue() == month && entry.getKey().getYear() == year) {
                            String taskInTable;
                            if (entry.getValue() != null) {
                                taskInTable = " Слот занят задачей с ID: " + entry.getValue().getId();
                            } else {
                                taskInTable = "свободный слот";
                            }
                            System.out.println(entry.getKey() + "-->" + taskInTable);
                        }
                    }
                }
            }
        } catch (InputMismatchException e) {
            e.printStackTrace();
            System.out.println("Введите числовое значение из меню");
        }
    }
}
