import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
    private static final FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
    private static final Task[] tasksExpectedArray = new Task[7];
    private static Task[] historyExpectedArray = new Task[4];
    private final static Path copyPath = Paths.get("resources\\TasksStorageFileCopy.csv");
    private final static Path constantFilePath = Paths.get("resources\\TasksStorageFileConstant.csv");
    private final static Path mainPath = fileBackedTasksManager.getPath();


    FileBackedTasksManagerTest() {
        super(fileBackedTasksManager);
    }

    @BeforeAll
    static void beforeAll() {
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
    }

    /* ТОЛЬКО для следующего теста:
     void retrieveCompleteListShouldReturnTheSameTasksFromFileGetHistoryShouldReturnTheSameHistoryFromFile()
сформирован файл: TasksStorageFileConstant.csv.
Он должен иметь содержимое ниже, которое не меняется походу выполнения теста:
    id,type,name,status,details,special
1,SIMPLE_TASK,"Task 1 from file",NEW,"Details Task 1 from file",2023-02-01T08:00,2023-02-02T14:00,1800
2,SIMPLE_TASK,"Task 2 from file",NEW,"Details Task 2 from file",2023-02-05T18:00,2023-02-07T20:00,3000
3,EPIC,"Epic 1 from file",NEW,"Details Epic 1 from file","subtaskReferences:[4, 5, 6]",2023-02-09T11:00,2023-02-21T17:00,3330
4,SUBTASK,"Subtask 1 from file",NEW,"Details Subtask 1 from file",3,2023-02-09T11:00,2023-02-10T02:00,900
5,SUBTASK,"Subtask 2 from file",NEW,"Details Subtask 2 from file",3,2023-02-12T12:00,2023-02-13T03:30,930
6,SUBTASK,"Subtask 3 from file",NEW,"Details Subtask 3 from file",3,2023-02-20T16:00,2023-02-21T17:00,1500
7,EPIC,"Epic 2 from file",NEW,"Details Epic 2 from file","subtaskReferences:[]"

2,3,1,7

В @AfterAll заполняется именно данный файл, поскольку его , например, "ручное" изменение приведет к не выполнению теста.
     */

    @Test
    void retrieveCompleteListShouldReturnTheSameTasksFromFileGetHistoryShouldReturnTheSameHistoryFromFile() {
        fileBackedTasksManager.setPath(constantFilePath);
        Task[] tasksLoadedArray = new Task[7];
        Task[] historyLoadedArray = new Task[4];

        loadFromFile();
        fileBackedTasksManager.retrieveCompleteList().toArray(tasksLoadedArray);
        Manager.getDefaultHistory().getHistory().toArray(historyLoadedArray);

        assertArrayEquals(tasksExpectedArray, tasksLoadedArray, "Считанный список событий изменен");
        assertArrayEquals(historyExpectedArray, historyLoadedArray, "Считанный история изменена");

        fileBackedTasksManager.setPath(copyPath);
    }

    @Test
    void loadFromFileShouldReturnEmptyListWhenFileHasNotTasks() {
        fileBackedTasksManager.clearTaskList();
        loadFromFile();
        assertTrue(fileBackedTasksManager.retrieveCompleteList().isEmpty(), "Список задач не пуст");
    }

    @Test
    void saveMethodShouldSaveCreatedTasksIntoFileSavingCheckByLoadFormFileAndRetrieveCompleteListGetHistory() {
        fileBackedTasksManager.clearTaskList();
        Task[] taskLoadedFormFileArray = new Task[7];
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        Task task2 = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        Epic epic1 = new Epic("Epic 1 from file", "Details Epic 1 from file");
        Subtask subtask1 = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                3, LocalDateTime.parse("2023-02-09T11:00"), 900);
        Subtask subtask2 = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                3, LocalDateTime.parse("2023-02-12T12:00"), 930);
        Subtask subtask3 = new Subtask("Subtask 3 from file", "Details Subtask 3 from file",
                3, LocalDateTime.parse("2023-02-20T16:00"), 1500);
        Epic epic2 = new Epic("Epic 2 from file", "Details Epic 2 from file");

        fileBackedTasksManager.createTask(task1);
        fileBackedTasksManager.createTask(task2);
        fileBackedTasksManager.createTask(epic1);
        fileBackedTasksManager.createTask(subtask1);
        fileBackedTasksManager.createTask(subtask2);
        fileBackedTasksManager.createTask(subtask3);
        fileBackedTasksManager.createTask(epic2);
        fileBackedTasksManager.retrieveTaskById(2);
        fileBackedTasksManager.retrieveTaskById(3);
        fileBackedTasksManager.retrieveTaskById(1);
        fileBackedTasksManager.retrieveTaskById(7);
        Manager.getDefault().clearTaskList();
        loadFromFile();
        fileBackedTasksManager.retrieveCompleteList().toArray(taskLoadedFormFileArray);
        Task[] historyLoadedArray = new Task[Manager.getDefaultHistory().getHistory().size()];
        Manager.getDefaultHistory().getHistory().toArray(historyLoadedArray);

        assertArrayEquals(tasksExpectedArray, taskLoadedFormFileArray, "Считанный список событий изменен");
        assertArrayEquals(historyExpectedArray, historyLoadedArray, "Считанный история изменена");
    }

    @Test
    void SaveMethodShouldSaveTasksWithNoHistoryIntoFileSavingCheckByLoadFormFileAndRetrieveCompleteListGetHistory() {
        fileBackedTasksManager.clearTaskList();
        Task[] taskLoadedFormFileArray = new Task[7];
        List<Task> history;
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        Task task2 = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        Epic epic1 = new Epic("Epic 1 from file", "Details Epic 1 from file");
        Subtask subtask1 = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                3, LocalDateTime.parse("2023-02-09T11:00"), 900);
        Subtask subtask2 = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                3, LocalDateTime.parse("2023-02-12T12:00"), 930);
        Subtask subtask3 = new Subtask("Subtask 3 from file", "Details Subtask 3 from file",
                3, LocalDateTime.parse("2023-02-20T16:00"), 1500);
        Epic epic2 = new Epic("Epic 2 from file", "Details Epic 2 from file");

        fileBackedTasksManager.createTask(task1);
        fileBackedTasksManager.createTask(task2);
        fileBackedTasksManager.createTask(epic1);
        fileBackedTasksManager.createTask(subtask1);
        fileBackedTasksManager.createTask(subtask2);
        fileBackedTasksManager.createTask(subtask3);
        fileBackedTasksManager.createTask(epic2);
        Manager.getDefault().clearTaskList();
        loadFromFile();
        fileBackedTasksManager.retrieveCompleteList().toArray(taskLoadedFormFileArray);
        history = Manager.getDefaultHistory().getHistory();

        assertArrayEquals(tasksExpectedArray, taskLoadedFormFileArray, "Считанный список событий изменен");
        assertTrue(history.isEmpty(), "Список истории не пустой");
    }

    @AfterAll
    static void recoverTestFileAndPath() {
        fileBackedTasksManager.setPath(constantFilePath);
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
