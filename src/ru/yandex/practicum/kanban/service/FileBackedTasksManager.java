package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.exceptions.IdPassingException;
import ru.yandex.practicum.kanban.exceptions.ManagerSaveException;
import ru.yandex.practicum.kanban.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private static Path path = Paths.get("resources\\TasksStorageFile.csv");
    private final static String title = "id,type,name,status,details,special";

    public static void main(String[] args) {
        TaskManager fileBackedTasksManager = Manager.getFileBacked();

        System.out.println("Запускаем тест из метода main класса FileBackedTasksManager\n");

        System.out.println("Читаем файл\n");
        loadFromFile();
        System.out.println("\nПечать всех задач считанный из файла\n");
        System.out.println(fileBackedTasksManager.printAll());

        System.out.println("Добавляем различные задачи согласно задания из менеджера\n");
        Task task = new Task("ИЗ МЕНЕДЖЕРА, Первая простая задача",
                "Детали к первой простой задачи ИЗ МЕНЕДЖЕРА", 1200);
        int newID = fileBackedTasksManager.createTask(task);
        System.out.println(newID);

        task = new Task("ИЗ МЕНЕДЖЕРА, Вторая простая задача",
                "Детали ко второй простой задачи ИЗ МЕНЕДЖЕРА", 2700);
        newID = fileBackedTasksManager.createTask(task);
        System.out.println(newID);

        Epic epic = new Epic("ИЗ МЕНЕДЖЕРА, Первый эпик",
                "Детали первого эпика ИЗ МЕНЕДЖЕРА");
        int newEpicID = fileBackedTasksManager.createTask(epic);
        System.out.println(newID);

        Subtask subtask = new Subtask("ИЗ МЕНЕДЖЕРА, Первая подзадача к первому эпику",
                "Детали первой подзадачи к первому эпику ИЗ МЕНЕДЖЕРА", newEpicID, 60);
        newID = fileBackedTasksManager.createTask(subtask);
        System.out.println(newID);

        subtask = new Subtask("ИЗ МЕНЕДЖЕРА, Вторая подзадача к первому эпику",
                "Детали второй подзадачи к первому эпику ИЗ МЕНЕДЖЕРА", newEpicID, 75);
        newID = fileBackedTasksManager.createTask(subtask);
        System.out.println(newID);

        epic = new Epic("ИЗ МЕНЕДЖЕРА, Второй эпик",
                "Детали второго эпика ИЗ МЕНЕДЖЕРА");
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
                    try {
                        System.out.println(fileBackedTasksManager.retrieveTaskById(id));
                    } catch (IdPassingException e) {
                        System.out.println(e.getDetailedMessage());

                    }
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
                } else if (command == 7) {
                    fileBackedTasksManager.clearTaskList();
                } else if (command == 8) {
                    for (Map.Entry<LocalDateTime, Task> entry :
                            Manager.getDefault().getTimeSlotMap().entrySet()) {
                        System.out.println(entry.getKey() + "-->" + entry.getValue().getId());
                    }
                }
            }
        } catch (InputMismatchException e) {
            e.printStackTrace();
            System.out.println("Введите числовое значение из меню");
        }
    }

    public static void loadFromFile() {
        List<String> taskLines;
        try {
            taskLines = Files.readAllLines(path);
            Map<Boolean, List<String>> taskListFromFile = taskLines.stream()
                    .filter(line -> line.length() > 1)
                    .filter(line -> !(line.contains("id,type,name,status,details,special")))
                    .collect(Collectors.partitioningBy(line -> line.matches("[^a-zA-Z]+")));

            for (String taskLine : taskListFromFile.get(false)) {
                writingHashFormFile(fromString(taskLine));
            }
            if (!taskListFromFile.get(true).isEmpty()) {
                Manager.getDefaultHistory().historyFromString(taskListFromFile.get(true).get(0));
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Файл с данных не найден");
        }
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        FileBackedTasksManager.path = path;
    }

    public static Task fromString(String value) {
        String[] splitedTaskLine = value.split(",");
        int firstIndexName = value.indexOf(splitedTaskLine[2]) + 1;
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

        int id = Integer.parseInt(splitedTaskLine[0]);
        if (id > InMemoryTaskManager.getIdWithoutIncrement()) {
            InMemoryTaskManager.setId(id);
        }
        int duration = 0;
        String stringStartTime = splitedTaskLine[splitedTaskLine.length - 3];
        String stringEndTime = splitedTaskLine[splitedTaskLine.length - 2];
        String dateTimePatten = "^(((2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])T" +
                "([01]?[0-9]|2[0-3])):[0-5][0-9]$";
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (stringStartTime.matches(dateTimePatten)) {
            startTime = LocalDateTime.parse(stringStartTime);
            endTime = LocalDateTime.parse(stringEndTime);
            duration = Integer.parseInt(splitedTaskLine[splitedTaskLine.length - 1]);
            int timeslotNumberChars = (splitedTaskLine[splitedTaskLine.length - 1]
                    + splitedTaskLine[splitedTaskLine.length - 2] +
                    splitedTaskLine[splitedTaskLine.length - 2]).length() + 3;
            value = value.substring(0, value.length() - timeslotNumberChars);
        }
        String name = value.substring(firstIndexName, lastIndexName - 1);
        String details = value.substring(firstIndexDetails, value.length() - 1);
        switch (TaskType.valueOf(splitedTaskLine[1])) {
            case SIMPLE_TASK:
                Task task = new Task(id, name, details, status, startTime, duration);
                if (stringStartTime.matches(dateTimePatten)) {
                    takeTimeSlot(task);
                }
                return task;
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
                if (startTime == null) {
                    return new Epic(id, name, details, status, subtaskReferences);
                } else {
                    return new Epic(id, name, details, status, subtaskReferences,
                            startTime, endTime, duration);
                }
            case SUBTASK:
                int epicReference = Integer.parseInt(splitedTaskLine[splitedTaskLine.length - 4]);
                int lastIndexOfDetails = splitedTaskLine[splitedTaskLine.length - 4].length() + 1;
                details = details.substring(0, details.length() - lastIndexOfDetails);
                Subtask subtask = new Subtask(id, name, details, status, epicReference, startTime, duration);
                if (stringStartTime.matches(dateTimePatten)) {
                    takeTimeSlot(subtask);
                }
                return subtask;
        }
        return new Task();
    }

    public void save() {
        List<String> taskLines = new ArrayList<>();
        List<Task> allTasks = retrieveCompleteList();
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
            throw new ManagerSaveException("Не удалость обновить информацию в файле данных.");
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
    public int updateTask(Task task) {
        int id = super.updateTask(task);
        save();
        return id;

    }

    @Override
    public int updateTask(Epic epic) {
        int id = super.updateTask(epic);
        save();
        return id;
    }

    @Override
    public int updateTask(Subtask subtask) {
        int id = super.updateTask(subtask);
        save();
        return id;
    }

    @Override
    public int deleteTask(int id) {
        super.deleteTask(id);
        save();
        return id;
    }

    @Override
    public void clearTaskList() {
        super.clearTaskList();
        save();
    }

    @Override
    public List<Task> retrieveCompleteList() {
        return super.retrieveCompleteList();
    }

    @Override
    public Task retrieveTaskById(int id) {
        Task task = super.retrieveTaskById(id);
        save();
        return task;
    }

}
