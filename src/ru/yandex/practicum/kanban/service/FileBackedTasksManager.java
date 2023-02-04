package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static ru.yandex.practicum.kanban.model.TaskType.SIMPLE_TASK;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final static Path path = Paths.get("resources\\TasksStorageFile.csv");
    private final static String title = "id,type,name,status,details,special";

    public static void main(String[] args) {
        TaskManager fileBackedTasksManager = Manager.getFileBacked();

        System.out.println("Запускаем тест из метода main класса FileBackedTasksManager\n");

        System.out.println("Читаем файл\n");
        loadFromFile(path);
        System.out.println("\nПечать всех задач считанный из файла\n");
        System.out.println(fileBackedTasksManager.printAll());

        System.out.println("Добавляем различные задачи согласно задания из менеджера\n");
        Task task = new Task("ИЗ МЕНЕДЖЕРА, Первая простая задача",
                "Детали к первой простой задачи ИЗ МЕНЕДЖЕРА",
                TaskStatus.NEW);
        int newID = fileBackedTasksManager.createTask(task);
        System.out.println(newID);

        task = new Task("ИЗ МЕНЕДЖЕРА, Вторая простая задача",
                "Детали ко второй простой задачи ИЗ МЕНЕДЖЕРА",
                TaskStatus.NEW);
        newID = fileBackedTasksManager.createTask(task);
        System.out.println(newID);

        Epic epic = new Epic("ИЗ МЕНЕДЖЕРА, Первый эпик",
                "Детали первого эпика ИЗ МЕНЕДЖЕРА",
                TaskStatus.NEW);
        newID = fileBackedTasksManager.createTask(epic);
        System.out.println(newID);

        Subtask subtask = new Subtask("ИЗ МЕНЕДЖЕРА, Первая подзадача к первому эпику",
                "Детали первой подзадачи к первому эпику ИЗ МЕНЕДЖЕРА", TaskStatus.NEW, 10);
        newID = fileBackedTasksManager.createTask(subtask);
        System.out.println(newID);

        subtask = new Subtask("ИЗ МЕНЕДЖЕРА, Вторая подзадача к первому эпику",
                "Детали второй подзадачи к первому эпику ИЗ МЕНЕДЖЕРА", TaskStatus.NEW, 10);
        newID = fileBackedTasksManager.createTask(subtask);
        System.out.println(newID);

        epic = new Epic("ИЗ МЕНЕДЖЕРА, Второй эпик",
                "Детали второго эпика ИЗ МЕНЕДЖЕРА",
                TaskStatus.NEW);
        newID = fileBackedTasksManager.createTask(epic);
        System.out.println(newID);

        System.out.println("\nПечать всех задач");
        System.out.println(fileBackedTasksManager.printAll());

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("1 - Просмотр задачи\n" +
                        "2 - Добавить автоматически в историю все задачи несколько раз\n" +
                        "3 - История просмотра\n" +
                        "4 - удалить задачу\n" +
                        "5 - выход\n" +
                        "6 - Вывод всех задач\n");
                int command = scanner.nextInt();
                if (command == 1) {
                    System.out.println("Введите номер задачи");
                    int id = scanner.nextInt();
                    System.out.println(fileBackedTasksManager.retrieveTaskById(id));
                }
                if (command == 2) {
                    for (int i = 4; i < 13; i++) {
                        fileBackedTasksManager.retrieveTaskById(i);
                    }
                    for (int i = 12; i > 7; i--) {
                        fileBackedTasksManager.retrieveTaskById(i);
                    }
                    fileBackedTasksManager.retrieveTaskById(1);
                    fileBackedTasksManager.retrieveTaskById(13);
                    //     System.out.println(Manager.getDefaultHistory().printHistory());
                } else if (command == 3) {
                    System.out.println(Manager.getDefaultHistory().printHistory());
                } else if (command == 4) {
                    System.out.println("Введите номер задачи");
                    int idDelete = scanner.nextInt();
                    System.out.println(fileBackedTasksManager.deleteTask(idDelete));
                } else if (command == 5) {
                    break;
                } else if (command == 6) {
                    System.out.println(fileBackedTasksManager.printAll());
                }
            }
        } catch (InputMismatchException e) {
            e.printStackTrace();
            System.out.println("Введите числовое значение из меню");
        }
    }

    public static void loadFromFile(Path path) {
        try {
            List<String> taskLines = Files.readAllLines(path);
            int i;
            for (i = 1; i < taskLines.size(); i++) {
                if (taskLines.get(i).length() != 0) {
                    writingHashFormFile(fromString(taskLines.get(i)));
                } else {
                    break;
                }
            }
            if (taskLines.get(i).length() == 0 & taskLines.get(i + 1).length() > 0) {
                String historyString = taskLines.get(i + 1);
                Manager.getDefaultHistory().historyFromString(historyString);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Файл с данных не найден");
        }
    }

    public static Task fromString(String value) {
        String[] partOfTaskLine = value.split(",");
        int firstIndexName = value.indexOf(partOfTaskLine[2]) + 1;
        int lastIndexName = 0;
        int firstIndexDetails = 0;
        TaskStatus status = TaskStatus.NEW;
        for (TaskStatus statusOfTask : TaskStatus.values()) {
            lastIndexName = value.indexOf("," + statusOfTask.toString() + ",");
            if (lastIndexName != -1) {
                status = statusOfTask;
                firstIndexDetails = lastIndexName + statusOfTask.toString().length() + 3;
                break;
            }
        }
        String name = value.substring(firstIndexName, lastIndexName - 1);
        String details = value.substring(firstIndexDetails, value.length() - 1);
        int id = Integer.parseInt(partOfTaskLine[0]);
        if (id > InMemoryTaskManager.getIdWithoutIncrement()) {
            InMemoryTaskManager.setId(id);
        }
        switch (TaskType.valueOf(partOfTaskLine[1])) {
            case SIMPLE_TASK:
                return new Task(id, name, details, status, SIMPLE_TASK);
            case EPIC:
                        /* Хранение в классе Epic subtasks IDs реализовывалось
                         и сейчас реализовывается в файле только для выполнения требвоаний ТЗ №3
                         " - Для каждой подзадачи известно, в рамках какого эпика она выполняется.
                           - Каждый ЭПИК знает, какие подзадачи в него входят." */

                int lastIndexDetails = value.indexOf(",\"subtaskReferences:");
                int indexOfSubtasks = lastIndexDetails + 21;
                ArrayList<Integer> subtaskReferences = new ArrayList<>();
                if (lastIndexDetails != -1) {
                    String subtasksIds = value.substring(indexOfSubtasks, value.length() - 2);
                    int lastIndexOfDetails = details.length() - subtasksIds.length() - 23;
                    details = details.substring(0, lastIndexOfDetails);
                    if (subtasksIds.length() > 0) {
                        String[] subtasksList = subtasksIds.split(",");
                        for (String s : subtasksList) {
                            subtaskReferences.add(Integer.parseInt(s.trim()));
                        }
                    }
                }
                return new Epic(id, name, details, status, subtaskReferences);
            case SUBTASK:
                int epicReference = Integer.parseInt(partOfTaskLine[partOfTaskLine.length - 1]);
                int lastIndexOfDetails = partOfTaskLine[partOfTaskLine.length - 1].length() + 1;
                details = details.substring(0, details.length() - lastIndexOfDetails);
                return new Subtask(id, name, details, status, epicReference);
        }
        return new Task();
    }

    public void save() {
        List<String> taskLines = new ArrayList<>();
        ArrayList<Task> allTasks = retrieveCompleteList();
        for (Task task : allTasks) {
            taskLines.add(task.toStringInFile());
        }
        if (Manager.getDefaultHistory().getHistory().size() > 0) {
            taskLines.add("");
            taskLines.add(Manager.getDefaultHistory().historyToString());
        }
        try {
            Files.writeString(path, title + "\n");
            for (String taskLine : taskLines) {
                Files.writeString(path, taskLine + "\n", StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Не удалость обносить информацию в файле данных.");
        }
    }

    @Override
    public int createTask(Task task) {
        int newID = super.createTask(task);
        save();
        return newID;
    }

    @Override
    public int createTask(Epic epic) {
        int newID = super.createTask(epic);
        save();
        return newID;
    }

    @Override
    public int createTask(Subtask subtask) {
        int newID = super.createTask(subtask);
        save();
        return newID;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean isImplemented = super.updateTask(task);
        save();
        return isImplemented;

    }

    @Override
    public boolean updateTask(Subtask subtask) {
        boolean isImplemented = super.updateTask(subtask);
        save();
        return isImplemented;
    }

    @Override
    public boolean deleteTask(int id) {
        boolean isImplemented = super.deleteTask(id);
        save();
        return isImplemented;
    }

    @Override
    public void clearTaskList() {
        super.clearTaskList();
        save();
    }

    @Override
    public ArrayList<Task> retrieveCompleteList() {
        return super.retrieveCompleteList();
    }

    @Override
    public Task retrieveTaskById(int id) {
        Task task = super.retrieveTaskById(id);
        save();
        return task;
    }

    @Override
    public ArrayList<Subtask> retrieveSubtasks(int idEpic) {
        return super.retrieveSubtasks(idEpic);
    }

}
