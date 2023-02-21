package ru.yandex.practicum.kanban.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.service.FileBackedTasksManager;
import ru.yandex.practicum.kanban.service.InMemoryTaskManager;
import ru.yandex.practicum.kanban.service.Manager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.kanban.service.FileBackedTasksManager.loadFromFile;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public static final FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
    private final static Path copyPath = Paths.get("resources\\TasksStorageFileCopy.csv");
    private final static Path mainPath = fileBackedTasksManager.getPath();
    private static final Task[] tasksExpectedArray = new Task[7];
    private static Task[] historyExpectedArray = new Task[4];

    FileBackedTasksManagerTest() {
        super(fileBackedTasksManager);
    }

    @BeforeAll
    static void changePathLoadFormFileTest() {
        fileBackedTasksManager.setPath(copyPath);

        Task task = new Task(1, "Task 1 from file", "Details Task 1 from file",
                TaskStatus.NEW, TaskType.SIMPLE_TASK, LocalDateTime.parse("2023-02-01T08:00"),
                LocalDateTime.parse("2023-02-02T14:00"), 1800);
        tasksExpectedArray[0] = task;
        task = new Task(2, "Task 2 from file", "Details Task 2 from file",
                TaskStatus.NEW, TaskType.SIMPLE_TASK, LocalDateTime.parse("2023-02-05T18:00"),
                LocalDateTime.parse("2023-02-07T20:00"), 3000);
        tasksExpectedArray[1] = task;
        List<Integer> subtasksList = Arrays.asList(4, 5, 6);
        Epic epic = new Epic(3, "Epic 1 from file", "Details Epic 1 from file",
                TaskStatus.NEW, subtasksList, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 3330);
        tasksExpectedArray[2] = epic;
        Subtask subtask = new Subtask(4, "Subtask 1 from file", "Details Subtask 1 from file",
                TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-10T02:00"), 900);
        tasksExpectedArray[3] = subtask;
        subtask = new Subtask(5, "Subtask 2 from file", "Details Subtask 2 from file",
                TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-12T12:00"),
                LocalDateTime.parse("2023-02-13T03:30"), 930);
        tasksExpectedArray[4] = subtask;
        subtask = new Subtask(6, "Subtask 3 from file", "Details Subtask 3 from file",
                TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-20T16:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 1500);
        tasksExpectedArray[5] = subtask;
        epic = new Epic(7, "Epic 2 from file", "Details Epic 2 from file",
                TaskStatus.NEW, new ArrayList<>());
        tasksExpectedArray[6] = epic;

        historyExpectedArray = new Task[]{tasksExpectedArray[1], tasksExpectedArray[2],
                tasksExpectedArray[0], tasksExpectedArray[6]};

        loadFromFile();

        Task[] tasksLoadedArray = new Task[7];
        fileBackedTasksManager.retrieveCompleteList().toArray(tasksLoadedArray);
        Task[] historyLoadedArray = new Task[Manager.getDefaultHistory().getHistory().size()];
        Manager.getDefaultHistory().getHistory().toArray(historyLoadedArray);

        assertArrayEquals(tasksExpectedArray, tasksLoadedArray, "Считанный список событий изменен");
        assertArrayEquals(historyExpectedArray, historyLoadedArray, "Считанный история изменена");
    }

    @BeforeEach
    @Override
    void clearTaskListTest() {
        super.clearTaskListTest();
    }

    @Test
    void loadFromEmptyFile() {
        loadFromFile();
        assertTrue(fileBackedTasksManager.retrieveCompleteList().isEmpty(), "Список задач не пуст");
    }

    @Test
    void SaveToFileTest() {
        Task task = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        fileBackedTasksManager.createTask(task);
        task = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        fileBackedTasksManager.createTask(task);
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        fileBackedTasksManager.createTask(epic);
        Subtask subtask = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                3, LocalDateTime.parse("2023-02-09T11:00"), 900);
        fileBackedTasksManager.createTask(subtask);
        subtask = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                3, LocalDateTime.parse("2023-02-12T12:00"), 930);
        fileBackedTasksManager.createTask(subtask);
        subtask = new Subtask("Subtask 3 from file", "Details Subtask 3 from file",
                3, LocalDateTime.parse("2023-02-20T16:00"), 1500);
        fileBackedTasksManager.createTask(subtask);
        epic = new Epic("Epic 2 from file", "Details Epic 2 from file");
        fileBackedTasksManager.createTask(epic);
        fileBackedTasksManager.retrieveTaskById(2);
        fileBackedTasksManager.retrieveTaskById(3);
        fileBackedTasksManager.retrieveTaskById(1);
        fileBackedTasksManager.retrieveTaskById(7);
        Manager.getDefault().clearTaskList();

        loadFromFile();

        Task[] taskLoadedFormFileArray = new Task[7];
        fileBackedTasksManager.retrieveCompleteList().toArray(taskLoadedFormFileArray);
        Task[] historyLoadedArray = new Task[Manager.getDefaultHistory().getHistory().size()];
        Manager.getDefaultHistory().getHistory().toArray(historyLoadedArray);
        assertArrayEquals(tasksExpectedArray, taskLoadedFormFileArray, "Считанный список событий изменен");
        assertArrayEquals(historyExpectedArray, historyLoadedArray, "Считанный история изменена");
    }

    @Test
    void SaveToFileWithNoHistoryTest() {
        Task task = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        fileBackedTasksManager.createTask(task);
        task = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        fileBackedTasksManager.createTask(task);
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        fileBackedTasksManager.createTask(epic);
        Subtask subtask = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                3, LocalDateTime.parse("2023-02-09T11:00"), 900);
        fileBackedTasksManager.createTask(subtask);
        subtask = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                3, LocalDateTime.parse("2023-02-12T12:00"), 930);
        fileBackedTasksManager.createTask(subtask);
        subtask = new Subtask("Subtask 3 from file", "Details Subtask 3 from file",
                3, LocalDateTime.parse("2023-02-20T16:00"), 1500);
        fileBackedTasksManager.createTask(subtask);
        epic = new Epic("Epic 2 from file", "Details Epic 2 from file");
        fileBackedTasksManager.createTask(epic);

        Manager.getDefault().clearTaskList();
        Task[] taskLoadedFormFileArray = new Task[7];

        loadFromFile();
        fileBackedTasksManager.retrieveCompleteList().toArray(taskLoadedFormFileArray);
        Task[] historyLoadedArray = new Task[Manager.getDefaultHistory().getHistory().size()];
        Manager.getDefaultHistory().getHistory().toArray(historyLoadedArray);
        assertArrayEquals(tasksExpectedArray, taskLoadedFormFileArray, "Считанный список событий изменен");
        assertTrue(Manager.getDefaultHistory().getHistory().isEmpty(),
                "Список истории не пустой");
    }

    @Test
    @Override
    void retrieveTaskByIdTest() {
        super.retrieveTaskByIdTest();
    }

    @Test
    @Override
    void retrieveTaskByIdTestException() {
        super.retrieveTaskByIdTestException();
    }

    @Test
    @Override
    void createSimpleTaskTest() {
        super.createSimpleTaskTest();
    }

    @Test
    @Override
    void createEpicTest() {
        super.createEpicTest();
    }

    @Test
    @Override
    void createSubtaskTest() {
        super.createSubtaskTest();
    }

    @Test
    @Override
    void createSubtaskWithoutEpicTest() {
        super.createSubtaskWithoutEpicTest();
    }

    @Test
    @Override
    void updateTaskTest() {
        super.updateTaskTest();
    }

    @Test
    @Override
    void updateTaskTestIDException() {
        super.updateTaskTestIDException();
    }

    @Test
    @Override
    void updateTaskTestEqualityException() {
        super.updateTaskTestEqualityException();
    }

    @Test
    @Override
    void updateEpicTest() {
        super.updateEpicTest();
    }

    @Test
    @Override
    void updateEpicTestIDException() {
        super.updateEpicTestIDException();
    }

    @Test
    @Override
    void updateTaskEpicEqualityException() {
        super.updateTaskEpicEqualityException();
    }

    @Test
    @Override
    void updateSubtaskTest() {
        super.updateSubtaskTest();
    }

    @Test
    @Override
    void updateSubtaskTestIDException() {
        super.updateSubtaskTestIDException();
    }

    @Test
    @Override
    void updateTaskSubtaskEqualityException() {
        super.updateTaskSubtaskEqualityException();
    }

    @Test
    @Override
    void deleteTaskTest() {
        super.deleteTaskTest();
    }

    @Test
    @Override
    void deleteEpicWithNoSubtasksTest() {
        super.deleteEpicWithNoSubtasksTest();
    }

    @Test
    @Override
    void deleteEpicWithSubtasksTest() {
        super.deleteEpicWithSubtasksTest();
    }

    @Test
    @Override
    void deleteSubtaskTest() {
        super.deleteSubtaskTest();
    }

    @Test
    @Override
    void deleteTaskIdExcetionTest() {
        super.deleteTaskIdExcetionTest();
    }

    @Test
    @Override
    void retrieveCompleteListTest() {
        super.retrieveCompleteListTest();
    }

    @Test
    @Override
    void retrieveCompleteListIfEmptyTest() {
        super.retrieveCompleteListIfEmptyTest();
    }

    @AfterAll
    static void recoverTestFileAndPath() {
        fileBackedTasksManager.clearTaskList();
        Task task = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        fileBackedTasksManager.createTask(task);
        task = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        fileBackedTasksManager.createTask(task);
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file",
                LocalDateTime.parse("2023-02-09T11:00"), 3300);
        fileBackedTasksManager.createTask(epic);
        Subtask subtask = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                3, LocalDateTime.parse("2023-02-09T11:00"), 900);
        fileBackedTasksManager.createTask(subtask);
        subtask = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                3, LocalDateTime.parse("2023-02-12T12:00"), 930);
        fileBackedTasksManager.createTask(subtask);
        subtask = new Subtask("Subtask 3 from file", "Details Subtask 3 from file",
                3, LocalDateTime.parse("2023-02-20T16:00"), 1500);
        fileBackedTasksManager.createTask(subtask);
        epic = new Epic("Epic 2 from file", "Details Epic 2 from file");
        fileBackedTasksManager.createTask(epic);
        fileBackedTasksManager.retrieveTaskById(2);
        fileBackedTasksManager.retrieveTaskById(3);
        fileBackedTasksManager.retrieveTaskById(1);
        fileBackedTasksManager.retrieveTaskById(7);
        fileBackedTasksManager.setPath(mainPath);
    }
}
