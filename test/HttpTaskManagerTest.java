import org.junit.jupiter.api.*;
import ru.yandex.practicum.kanban.http.KVServer;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.service.HttpTaskManager;
import ru.yandex.practicum.kanban.service.InMemoryTaskManager;
import ru.yandex.practicum.kanban.service.Manager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>  {
    private static HttpTaskManager httpTaskManager;
    private static KVServer kvServer;
    private static final Task[] tasksExpectedArray = new Task[7];
    private static Task[] historyExpectedArray = new Task[4];

    public HttpTaskManagerTest() {
        super(httpTaskManager);
    }

    @BeforeAll
    static void beforeAll() throws IOException {
        Task task = new Task(1, "Task 1 from file", "Details Task 1 from file", TaskStatus.NEW, TaskType.SIMPLE_TASK, LocalDateTime.parse("2023-02-01T08:00"), LocalDateTime.parse("2023-02-02T14:00"), 1800);
        tasksExpectedArray[0] = task;
        task = new Task(2, "Task 2 from file", "Details Task 2 from file", TaskStatus.NEW, TaskType.SIMPLE_TASK, LocalDateTime.parse("2023-02-05T18:00"), LocalDateTime.parse("2023-02-07T20:00"), 3000);
        tasksExpectedArray[1] = task;
        List<Integer> subtasksList = Arrays.asList(4, 5, 6);
        Epic epic = new Epic(3, "Epic 1 from file", "Details Epic 1 from file", TaskStatus.NEW, subtasksList, LocalDateTime.parse("2023-02-09T11:00"), LocalDateTime.parse("2023-02-21T17:00"), 3330);
        tasksExpectedArray[2] = epic;
        Subtask subtask = new Subtask(4, "Subtask 1 from file", "Details Subtask 1 from file", TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-09T11:00"), LocalDateTime.parse("2023-02-10T02:00"), 900);
        tasksExpectedArray[3] = subtask;
        subtask = new Subtask(5, "Subtask 2 from file", "Details Subtask 2 from file", TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-12T12:00"), LocalDateTime.parse("2023-02-13T03:30"), 930);
        tasksExpectedArray[4] = subtask;
        subtask = new Subtask(6, "Subtask 3 from file", "Details Subtask 3 from file", TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-20T16:00"), LocalDateTime.parse("2023-02-21T17:00"), 1500);
        tasksExpectedArray[5] = subtask;
        epic = new Epic(7, "Epic 2 from file", "Details Epic 2 from file", TaskStatus.NEW, new ArrayList<>());
        tasksExpectedArray[6] = epic;
        historyExpectedArray = new Task[]{tasksExpectedArray[1], tasksExpectedArray[2], tasksExpectedArray[0], tasksExpectedArray[6]};
        kvServer = new KVServer();
        kvServer.start();
        httpTaskManager = new HttpTaskManager();
    }

     @Test
    void loadFromFileShouldReturnEmptyListWhenFileHasNotTasks() {
        httpTaskManager.clearTaskList();
        httpTaskManager.loadFromStorage();
        assertTrue(httpTaskManager.retrieveCompleteList().isEmpty(), "Список задач не пуст");
    }

    @Test
    void saveMethodShouldSaveCreatedTasksIntoFileSavingCheckByLoadFormFileAndRetrieveCompleteListGetHistory() {
       httpTaskManager.clearTaskList();
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

        httpTaskManager.createTask(task1);
        httpTaskManager.createTask(task2);
        httpTaskManager.createTask(epic1);
        httpTaskManager.createTask(subtask1);
        httpTaskManager.createTask(subtask2);
        httpTaskManager.createTask(subtask3);
        httpTaskManager.createTask(epic2);
        httpTaskManager.retrieveTaskById(2);
        httpTaskManager.retrieveTaskById(3);
        httpTaskManager.retrieveTaskById(1);
        httpTaskManager.retrieveTaskById(7);
        Manager.getInMemoryTask().clearTaskList();
        httpTaskManager.loadFromStorage();
        httpTaskManager.retrieveCompleteList().toArray(taskLoadedFormFileArray);
        Task[] historyLoadedArray = new Task[Manager.getDefaultHistory().getHistory().size()];
        Manager.getDefaultHistory().getHistory().toArray(historyLoadedArray);

        assertArrayEquals(tasksExpectedArray, taskLoadedFormFileArray, "Считанный список событий изменен");
        assertArrayEquals(historyExpectedArray, historyLoadedArray, "Считанный история изменена");
    }

    @Test
    void SaveMethodShouldSaveTasksWithNoHistoryIntoFileSavingCheckByLoadFormFileAndRetrieveCompleteListGetHistory() {
        httpTaskManager.clearTaskList();
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

        httpTaskManager.createTask(task1);
        httpTaskManager.createTask(task2);
        httpTaskManager.createTask(epic1);
        httpTaskManager.createTask(subtask1);
        httpTaskManager.createTask(subtask2);
        httpTaskManager.createTask(subtask3);
        httpTaskManager.createTask(epic2);
        Manager.getInMemoryTask().clearTaskList();
        httpTaskManager.loadFromStorage();
        httpTaskManager.retrieveCompleteList().toArray(taskLoadedFormFileArray);
        history = Manager.getDefaultHistory().getHistory();

        assertArrayEquals(tasksExpectedArray, taskLoadedFormFileArray, "Считанный список событий изменен");
        assertTrue(history.isEmpty(), "Список истории не пустой");
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
    }
}