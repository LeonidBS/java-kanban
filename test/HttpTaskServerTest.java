import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.http.HttpTaskServer;
import ru.yandex.practicum.kanban.http.KVServer;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.service.HttpTaskManager;
import ru.yandex.practicum.kanban.service.Manager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskServerTest {
    private HttpTaskManager httpTaskManager = new HttpTaskManager();
    private static KVServer kvServer;
    private static HttpTaskServer httpTaskServer;
    private static final Task[] tasksExpectedArray = new Task[7];
    private static Task[] historyExpectedArray = new Task[4];

    @BeforeAll
    static void beforeAll() throws IOException {
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
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }

    @Test
    void getTaskShouldReturnAllSimpleTasks() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        List<Task> taskExpectedList = Arrays.asList(tasksExpectedArray[0], tasksExpectedArray[1]);
        List<Task> taskTestedList = new ArrayList<>();
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        httpTaskManager.createTask(task1);
        Task task2 = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        httpTaskManager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            taskTestedList.add(Manager.getGson().fromJson(element, Task.class));
        }

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(taskExpectedList, taskTestedList, "Список задач не соответствует ожидаемому");
    }

    @Test
    void getSubtaskShouldReturnAllSubtasks() throws IOException, InterruptedException {
        List<Task> subtaskExpectedList = Arrays.asList(tasksExpectedArray[3], tasksExpectedArray[4]);
        httpTaskManager.clearTaskList();
        List<Subtask> subtaskTestedList = new ArrayList<>();
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        httpTaskManager.createTask(task1);
        Task task2 = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        httpTaskManager.createTask(task2);
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        httpTaskManager.createTask(epic);
        Subtask subtask1 = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                3, LocalDateTime.parse("2023-02-09T11:00"), 900);
        httpTaskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                3, LocalDateTime.parse("2023-02-12T12:00"), 930);
        httpTaskManager.createTask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            subtaskTestedList.add(Manager.getGson().fromJson(element, Subtask.class));
        }

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(subtaskExpectedList, subtaskTestedList, "Список подзадач не соответствует ожидаемому");
    }

    @Test
    void getEpicsShouldReturnAllEpics() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        List<Epic> epicTestedList = new ArrayList<>();
        List<Task> epicExpectedList = Arrays.asList(tasksExpectedArray[2], tasksExpectedArray[6]);
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        httpTaskManager.createTask(task1);
        Task task2 = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        httpTaskManager.createTask(task2);
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        httpTaskManager.createTask(epic);
        Subtask subtask1 = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                3, LocalDateTime.parse("2023-02-09T11:00"), 900);
        httpTaskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                3, LocalDateTime.parse("2023-02-12T12:00"), 930);
        httpTaskManager.createTask(subtask2);
        Subtask subtask3 = new Subtask("Subtask 3 from file", "Details Subtask 3 from file",
                3, LocalDateTime.parse("2023-02-20T16:00"), 1500);
        httpTaskManager.createTask(subtask3);
        Epic epic2 = new Epic("Epic 2 from file", "Details Epic 2 from file");
        httpTaskManager.createTask(epic2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            epicTestedList.add(Manager.getGson().fromJson(element, Epic.class));
        }

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(epicExpectedList, epicTestedList, "Список эпиков не соответствует ожидаемому");
    }

    @Test
    void getTaskByIdShouldReturnSimpleTaskById() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Task taskTested;
        Task taskExpected = tasksExpectedArray[0];
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        httpTaskManager.createTask(task1);
        Task task2 = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        httpTaskManager.createTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        taskTested = Manager.getGson().fromJson(jsonElement, Task.class);

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(taskExpected, taskTested, "Задача не соответствует ожидаемой");
    }

    @Test
    void getSubtaskByIdShouldReturnSubtaskById() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Subtask subtaskExpected = (Subtask) tasksExpectedArray[3];
        Subtask subtaskTested;
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        httpTaskManager.createTask(task1);
        Task task2 = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        httpTaskManager.createTask(task2);
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        httpTaskManager.createTask(epic);
        Subtask subtask1 = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                3, LocalDateTime.parse("2023-02-09T11:00"), 900);
        httpTaskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                3, LocalDateTime.parse("2023-02-12T12:00"), 930);
        httpTaskManager.createTask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/?id=4"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        subtaskTested = Manager.getGson().fromJson(jsonElement, Subtask.class);

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(subtaskExpected, subtaskTested, "Список подзадач не соответствует ожидаемому");
    }

    @Test
    void getEpicByIdShouldReturnEpicById() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Epic epicTested;
        Epic epicExpected = (Epic) tasksExpectedArray[2];
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        httpTaskManager.createTask(task1);
        Task task2 = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        httpTaskManager.createTask(task2);
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        httpTaskManager.createTask(epic);
        Subtask subtask1 = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                3, LocalDateTime.parse("2023-02-09T11:00"), 900);
        httpTaskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                3, LocalDateTime.parse("2023-02-12T12:00"), 930);
        httpTaskManager.createTask(subtask2);
        Subtask subtask3 = new Subtask("Subtask 3 from file", "Details Subtask 3 from file",
                3, LocalDateTime.parse("2023-02-20T16:00"), 1500);
        httpTaskManager.createTask(subtask3);
        Epic epic2 = new Epic("Epic 2 from file", "Details Epic 2 from file");
        httpTaskManager.createTask(epic2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=3"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        epicTested = Manager.getGson().fromJson(jsonElement, Epic.class);

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(epicExpected, epicTested, "Список подзадач не соответствует ожидаемому");
    }

    @Test
    void getHistoryShouldReturnListOfHistoryTasks() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        List<Task> testedHistory = new ArrayList<>();
        List<Task> expectedHistory = Arrays.asList(tasksExpectedArray[0],
                tasksExpectedArray[2], tasksExpectedArray[4]);
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        httpTaskManager.createTask(task1);
        Task task2 = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        httpTaskManager.createTask(task2);
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        httpTaskManager.createTask(epic);
        Subtask subtask1 = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                3, LocalDateTime.parse("2023-02-09T11:00"), 900);
        httpTaskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                3, LocalDateTime.parse("2023-02-12T12:00"), 930);
        httpTaskManager.createTask(subtask2);
        Subtask subtask3 = new Subtask("Subtask 3 from file", "Details Subtask 3 from file",
                3, LocalDateTime.parse("2023-02-20T16:00"), 1500);
        httpTaskManager.createTask(subtask3);
        Epic epic2 = new Epic("Epic 2 from file", "Details Epic 2 from file");
        httpTaskManager.createTask(epic2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/history"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        httpTaskManager.retrieveTaskById(1);
        httpTaskManager.retrieveTaskById(3);
        httpTaskManager.retrieveTaskById(5);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            if (element.toString().contains("SIMPLE_TASK")) {
                testedHistory.add(Manager.getGson().fromJson(element, Task.class));
            }
            if (element.toString().contains("SUBTASK")) {
                testedHistory.add(Manager.getGson().fromJson(element, Subtask.class));
            }
            if (element.toString().contains("EPIC")) {
                testedHistory.add(Manager.getGson().fromJson(element, Epic.class));
            }
        }

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(expectedHistory, testedHistory, "Список подзадач не соответствует ожидаемому");
    }

    @Test
    void getSubtaskByEpicIdShouldReturnSubtaskByEpicId() throws IOException, InterruptedException {
        List<Task> subtaskExpectedList = Arrays.asList(tasksExpectedArray[3], tasksExpectedArray[4]);
        httpTaskManager.clearTaskList();
        List<Subtask> subtaskTestedList = new ArrayList<>();
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        httpTaskManager.createTask(task1);
        Task task2 = new Task("Task 2 from file", "Details Task 2 from file",
                LocalDateTime.parse("2023-02-05T18:00"), 3000);
        httpTaskManager.createTask(task2);
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        httpTaskManager.createTask(epic);
        Subtask subtask1 = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                3, LocalDateTime.parse("2023-02-09T11:00"), 900);
        httpTaskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                3, LocalDateTime.parse("2023-02-12T12:00"), 930);
        httpTaskManager.createTask(subtask2);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/epic/?id=3"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            subtaskTestedList.add(Manager.getGson().fromJson(element, Subtask.class));
        }

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(subtaskExpectedList, subtaskTestedList, "Список подзадач не соответствует ожидаемому");
    }

    @Test
    void createTaskShouldReturnNewTask() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Task expectedTask = new Task(1, "Task 1 from file", "Details Task 1 from file",
                TaskStatus.NEW, TaskType.SIMPLE_TASK, LocalDateTime.parse("2023-02-01T08:00"),
                LocalDateTime.parse("2023-02-02T14:00"), 1800);
        Task task = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        String json = Manager.getGson().toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task testedTask = httpTaskManager.retrieveTaskById(1);

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(expectedTask, testedTask, "Список задач не соответствует ожидаемому");
    }

    @Test
    void createSubtaskShouldReturnNewSubtask() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        httpTaskManager.createTask(epic);
        Subtask expectedSubtask = new Subtask(2, "Subtask 1 from file", "Details Subtask 1 from file",
                TaskStatus.NEW, 1, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-10T02:00"), 900);
        Subtask subtask = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                1, LocalDateTime.parse("2023-02-09T11:00"), 900);
        String json = Manager.getGson().toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task testedSubtask = httpTaskManager.retrieveTaskById(2);

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(expectedSubtask, testedSubtask, "Список задач не соответствует ожидаемому");
    }

    @Test
    void createEpicShouldReturnNewEpic() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Epic expectedEpic = new Epic(1, "Epic 1 from file", "Details Epic 1 from file",
                TaskStatus.NEW, new ArrayList<>());
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        httpTaskManager.createTask(epic);
        String json = Manager.getGson().toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task testedEpic = httpTaskManager.retrieveTaskById(1);

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(expectedEpic, testedEpic, "Список задач не соответствует ожидаемому");
    }

    @Test
    void updateTaskShouldReturnUpdatedTask() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Task expectedTask = new Task(1, "Task 1 from file", "Details Task 1 from file",
                TaskStatus.DONE, TaskType.SIMPLE_TASK, LocalDateTime.parse("2023-02-01T08:00"),
                LocalDateTime.parse("2023-02-02T14:00"), 1800);
        Task task = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        httpTaskManager.createTask(task);
        String json = Manager.getGson().toJson(expectedTask);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task testedTask = httpTaskManager.retrieveTaskById(1);

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(expectedTask, testedTask, "Список задач не соответствует ожидаемому");
    }

    @Test
    void updateSubtaskShouldReturnUpdatedSubtask() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        httpTaskManager.createTask(epic);
        Subtask expectedSubtask = new Subtask(2, "Subtask 1 from file", "Details Subtask 1 from file",
                TaskStatus.IN_PROGRESS, 1, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-10T02:00"), 900);
        Subtask subtask = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                1, LocalDateTime.parse("2023-02-09T11:00"), 900);
        httpTaskManager.createTask(subtask);
        String json = Manager.getGson().toJson(expectedSubtask);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask/"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task testedSubtask = httpTaskManager.retrieveTaskById(2);

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(expectedSubtask, testedSubtask, "Список задач не соответствует ожидаемому");
        assertEquals(TaskStatus.IN_PROGRESS, httpTaskManager.retrieveTaskById(1).getStatus(),
                "Статус эпика не обновился");
    }

    @Test
    void updateEpicShouldReturnUpdatedEpic() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Epic expectedEpic = new Epic(1, "UPDATED HTTP Epic 1 from file", "Details Epic 1 from file",
                TaskStatus.NEW, new ArrayList<>());
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        httpTaskManager.createTask(epic);
        String json = Manager.getGson().toJson(expectedEpic);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task testedEpic = httpTaskManager.retrieveTaskById(1);

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(expectedEpic, testedEpic, "Список задач не соответствует ожидаемому");
    }

    @Test
    void deleteTaskByIdShouldNotReturnSiDeletedTaskById() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        httpTaskManager.createTask(task1);
       List<Task> taskList = httpTaskManager.retrieveCompleteList();
        Task task = httpTaskManager.retrieveTaskById(1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(task,tasksExpectedArray[0], "Задача не создана");
        assertTrue(!taskList.isEmpty(), "Список пуст до удаления" );
        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertTrue(httpTaskManager.retrieveAllTasks().isEmpty(), "Задача не удалена");
    }

    @Test
    void deleteSubtaskByIdShouldNotReturnSiDeletedSubtaskById() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
        httpTaskManager.createTask(epic);
        Subtask subtask = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                1, LocalDateTime.parse("2023-02-09T11:00"), 900);
        httpTaskManager.createTask(subtask);
        Subtask subtaskExpected = new Subtask(2, "Subtask 1 from file", "Details Subtask 1 from file",
                TaskStatus.NEW,1,  LocalDateTime.parse("2023-02-09T11:00"), 900);
        Epic epicInList = (Epic) httpTaskManager.retrieveTaskById(1);
        Subtask subtaskInList = (Subtask) httpTaskManager.retrieveTaskById(2);
        List<Task> taskList = httpTaskManager.retrieveCompleteList();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest requestDeleteSubtask = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();

        HttpResponse<String> responseDeleteSubtask = client.send(requestDeleteSubtask,
                HttpResponse.BodyHandlers.ofString());

        HttpRequest requestDeleteEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=2"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();


        HttpResponse<String> responseDeleteEpic = client.send(requestDeleteEpic,
                HttpResponse.BodyHandlers.ofString());

        assertEquals(subtaskInList, subtaskExpected, "Задача не создана");
        assertTrue(!taskList.isEmpty(), "Список пуст до удаления" );
        assertEquals(responseDeleteSubtask.statusCode(), 200,
                "Не получено подтвреждение ответа от сервера");
        assertTrue(httpTaskManager.retrieveAllTasks().isEmpty(), "Задача не удалена");
    }

    @Test
    void deleteEpicByIdShouldNotReturnSiDeletedTEpicById() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Epic epic = new Epic("Task 1 from file", "Details Task 1 from file");
        httpTaskManager.createTask(epic);
        Epic epicExpected = new Epic(1,"Task 1 from file", "Details Task 1 from file",
                TaskStatus.NEW, new ArrayList<>());
        List<Task> taskList = httpTaskManager.retrieveCompleteList();
        Epic epicTested = (Epic) httpTaskManager.retrieveTaskById(1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(epicTested, epicExpected, "Задача не создана");
        assertTrue(!taskList.isEmpty(), "Список пуст до удаления");
        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertTrue(httpTaskManager.retrieveAllTasks().isEmpty(), "Задача не удалена");
    }

    @Test
    void deleteAllTaskShouldReturnEpmptyListOfTasks() throws IOException, InterruptedException {
        httpTaskManager.clearTaskList();
        Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        httpTaskManager.createTask(task1);
        List<Task> taskList = httpTaskManager.retrieveAllTasks();
        Task task = httpTaskManager.retrieveTaskById(1);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(task,tasksExpectedArray[0], "Задача не создана");
        assertTrue(!taskList.isEmpty(), "Список пуст до удаления" );
        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertTrue(httpTaskManager.retrieveAllTasks().isEmpty(), "Задача не удалена");
    }

    @Test
    void retrievePrioritizingList() throws IOException, InterruptedException {
        List<Task> allTasks = new ArrayList<>();
            httpTaskManager.clearTaskList();
            List<Subtask> subtaskTestedList = new ArrayList<>();
            Task task1 = new Task("Task 1 from file", "Details Task 1 from file",
                    LocalDateTime.parse("2023-02-01T08:00"), 1800);
            httpTaskManager.createTask(task1);
            Task task2 = new Task("Task 2 from file", "Details Task 2 from file",
                    LocalDateTime.parse("2023-02-05T18:00"), 3000);
            httpTaskManager.createTask(task2);
            Epic epic = new Epic("Epic 1 from file", "Details Epic 1 from file");
            httpTaskManager.createTask(epic);
            Subtask subtask1 = new Subtask("Subtask 1 from file", "Details Subtask 1 from file",
                    3, LocalDateTime.parse("2023-02-09T11:00"), 900);
            httpTaskManager.createTask(subtask1);
            Subtask subtask2 = new Subtask("Subtask 2 from file", "Details Subtask 2 from file",
                    3, LocalDateTime.parse("2023-02-12T12:00"), 930);
            httpTaskManager.createTask(subtask2);
     Map<LocalDateTime, Task> taskTimeMap = new HashMap<>();

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/"))
                    .header("Accept", "application/json")
                    .version(HttpClient.Version.HTTP_1_1)
                    .GET()
                    .build();
            int totalDuration = 0;
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonElement jsonElement = JsonParser.parseString(response.body());
        for (Map.Entry<String, JsonElement> stringJsonElementEntry : jsonElement.getAsJsonObject().entrySet()) {
            if (stringJsonElementEntry.getValue().toString().contains("SIMPLE_TASK")) {
                taskTimeMap.put(LocalDateTime.parse(stringJsonElementEntry.getKey()),
                        Manager.getGson().fromJson(stringJsonElementEntry.getValue(), Task.class));
            } else if (stringJsonElementEntry.getValue().toString().contains("SUBTASK")) {
                taskTimeMap.put(LocalDateTime.parse(stringJsonElementEntry.getKey()),
                        Manager.getGson().fromJson(stringJsonElementEntry.getValue(), Subtask.class));
            }
        }

        Map<LocalDateTime, Task> taskTimeMapExpected = new HashMap<>();
        TreeMap<LocalDateTime, Task> timeslotMap = httpTaskManager.getTimeSlotMap();
        for (Map.Entry<LocalDateTime, Task> entry : timeslotMap.entrySet()) {
            if (entry.getValue() != null) {
                taskTimeMapExpected.put(entry.getKey(), entry.getValue());
            }
        }

        assertEquals(response.statusCode(), 200, "Не получено подтвреждение ответа от сервера");
        assertEquals(taskTimeMapExpected, taskTimeMap, "Общее занятое время на выполнение событий" +
                " не соответствует сумме продолжительности всех событий");
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
        httpTaskServer.stop();
    }
}