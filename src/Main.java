import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
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
                Task task = new Task("Первая простая задача: сделать уроки",
                "Пройти темы2 Вычисляем хеш-код через hashCode(). Выполнить задание",
                TaskStatus.NEW);
        int newID = inMemoryTaskManager.createTask(task);
        System.out.println(newID);

        task = new Task( "Вторая простая задача: сделать анализ чертежей ПС",
                "Проверить габариты РУ 10кВ, кабкльные присоединения.",
                TaskStatus.NEW);
        newID = inMemoryTaskManager.createTask(task);
        System.out.println(newID);

        Epic epic = new Epic("Первый эпик: Сходить в магазин.",
                "Сходить в 'Пятёрку', купить хлеб  и молоко.",
                TaskStatus.NEW, new ArrayList<>());
        newID = inMemoryTaskManager.createTask(epic);
        System.out.println(newID);

        epic = new Epic("Второй эпик: Убраться в квартире",
                "Пропылесосить и помыть пол с участием робота",
                TaskStatus.NEW, new ArrayList<>());
        newID = inMemoryTaskManager.createTask(epic);
        System.out.println(newID);

        Subtask subtask = new Subtask("Первая подзадача к первому эпику",
                "Одется и обуться", TaskStatus.NEW, 3);
        newID = inMemoryTaskManager.createTask(subtask);
        System.out.println(newID);

        subtask = new Subtask("Вторая подзадача к первому эпику",
                "Дойти до маганзина и купить продукты", TaskStatus.NEW, 3);
        newID = inMemoryTaskManager.createTask(subtask);
        System.out.println(newID);

        subtask = new Subtask("Первая подзадача ко второму эпику",
                "Открыть приложение робота-пылесоса", TaskStatus.NEW, 4);
        newID = inMemoryTaskManager.createTask(subtask);
        System.out.println(newID);

        task = new Task("Простая задача: сделать зарядку",
                "приседать три раза",
                TaskStatus.NEW);
        newID = inMemoryTaskManager.createTask(task);
        System.out.println(newID);

        task = new Task( "Простая задача: Ответить на письмо",
                "Прочить письмо",
                TaskStatus.NEW);
        newID = inMemoryTaskManager.createTask(task);
        System.out.println(newID);

        epic = new Epic("Зпик: Посмотреть кино",
                "Найти и посмотреть хороший фильм",
                TaskStatus.NEW, new ArrayList<>());
        newID = inMemoryTaskManager.createTask(epic);
        System.out.println(newID);

        epic = new Epic("Эпик: воспитать детей",
                "Правильно воспитать детей",
                TaskStatus.NEW, new ArrayList<>());
        newID = inMemoryTaskManager.createTask(epic);
        System.out.println(newID);

        subtask = new Subtask("Подзадача к эпику про кино",
                "Найти хороший фильм", TaskStatus.NEW, 10);
        newID = inMemoryTaskManager.createTask(subtask);
        System.out.println(newID);

        subtask = new Subtask("Вторая подзадача к эпику про кино",
                "Купить покорн", TaskStatus.NEW, 10);
        newID = inMemoryTaskManager.createTask(subtask);
        System.out.println(newID);

        subtask = new Subtask("Первая подзадача к эпику про детей",
                "Прочитать «Тайная опора» Людмилы Петрановской", TaskStatus.NEW, 11);
        newID = inMemoryTaskManager.createTask(subtask);
        System.out.println(newID);

        System.out.println("\nПечать всех задач");
        System.out.println(inMemoryTaskManager.printAll());

        System.out.println("1 - Просмотр задачи \n" +
                "2 - Добавить автоматически в историю 14 задач и вывести историю  \n" +
                "3 - История просмотра");
        int command = scanner.nextInt();
        if (command == 1) {
            while (true) {
                System.out.println("Введите номер задачи");
                int id = scanner.nextInt();
                System.out.println(inMemoryTaskManager.obtainTaskById(id));
                System.out.println("1 - Просмотр задачи \n" +
                        "2 - Добавить автоматически в историю 14 задач и вывести историю  \n" +
                        "3 - История просмотра");
                command = scanner.nextInt();
                if (command != 1) {
                    System.out.println(Manager.getDefaultHistory().printHistory());
                    break;
                }
            }
        } else if (command == 2) {
            for (int i = 1; i < 15; i++) {
                inMemoryTaskManager.obtainTaskById(i);
            }
            System.out.println(Manager.getDefaultHistory().printHistory());
        } else {
            System.out.println(Manager.getDefaultHistory().printHistory());
        }
    }
}
