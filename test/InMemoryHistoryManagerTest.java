import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.service.exceptions.IdPassingException;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.service.InMemoryHistoryManager;
import ru.yandex.practicum.kanban.service.InMemoryTaskManager;
import ru.yandex.practicum.kanban.service.Manager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static final InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    private static final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @Test
    void getTaskHistoryManagerShouldReturnListOfTaskAddedToEmptyHistoryList() {
        inMemoryHistoryManager.clearHistory();
        Task task = new Task(1, "New Task for HistoryManager testing",
                "details for Task for HistoryManager testing", TaskStatus.NEW, TaskType.SIMPLE_TASK);
        Epic epic = new Epic(2, "New Epic for HistoryManager testing",
                "details for Epic for HistoryManager testing", TaskStatus.NEW, new ArrayList<>());
        epic.setTaskType(TaskType.EPIC);
        Task[] excpectedHistoryList = {task, epic};
        Task[] actualHistoryList = new Task[2];

        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.getTasks().toArray(actualHistoryList);

        assertArrayEquals(excpectedHistoryList, actualHistoryList,
                "История не содержит только добавленные задачи");

    }

    @Test
    void getHistoryShouldReturnListOfTaskAddedToHistoryListWhichAlreadyGotTasksList() {
        inMemoryHistoryManager.clearHistory();
        Task existedTask = new Task("Existed Task for HistoryManager testing",
                "details for Task for HistoryManager testing",
                LocalDateTime.parse("2023-02-07T18:00"), 3000);
        Epic existedEpic = new Epic("Existed Epic for HistoryManager testing",
                "details for Epic for HistoryManager testing");
        inMemoryTaskManager.createTask(existedTask);
        inMemoryTaskManager.createTask(existedEpic);
        inMemoryHistoryManager.add(existedTask);
        inMemoryHistoryManager.add(existedEpic);
        Task task = new Task(3, "New Task for HistoryManager testing",
                "details for Task for HistoryManager testing", TaskStatus.NEW,
                TaskType.SIMPLE_TASK, LocalDateTime.parse("2023-02-21T07:00"), 3000);
        Epic epic = new Epic(4, "New Epic for HistoryManager testing",
                "details for Epic for HistoryManager testing", TaskStatus.NEW, new ArrayList<>());
        epic.setTaskType(TaskType.EPIC);
        Task[] excpectedHistoryList = {existedTask, existedEpic, task, epic};
        Task[] actualHistoryList = new Task[4];

        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.getHistory().toArray(actualHistoryList);

        assertArrayEquals(excpectedHistoryList, actualHistoryList,
                "История не содержит только добавленные задачи");
    }

    @Test
    void getTaskOfHistoryManagerShouldReturnOnlyOneTaskAfterTwoAttemptsToAddSameTask() {
        inMemoryHistoryManager.clearHistory();
        Task task = new Task(1, "New Task for HistoryManager testing",
                "details for Task for HistoryManager testing", TaskStatus.NEW, TaskType.SIMPLE_TASK);
        Task duplicateTask = new Task(1, "New Task for HistoryManager testing",
                "details for Task for HistoryManager testing", TaskStatus.NEW, TaskType.SIMPLE_TASK);

        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(duplicateTask);
        List<Task> actualHistoryList = inMemoryHistoryManager.getTasks();

        assertTrue(actualHistoryList.contains(task), "История не содержит добавленную задачу");
        assertEquals(1, actualHistoryList.size(), "В истории две одинаковых задачи");
    }

    @Test
    void removeMethodShouldRemoveTasksFormHistoryListRemovingCheckByGetTaskOfHistoryManager() {
        inMemoryHistoryManager.clearHistory();
        Task task = new Task(1, "New Task for HistoryManager testing",
                "details for Task for HistoryManager testing", TaskStatus.NEW, TaskType.SIMPLE_TASK);
        Epic epic = new Epic(2, "New Epic for HistoryManager testing",
                "details for Epic for HistoryManager testing", TaskStatus.NEW, new ArrayList<>());

        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.remove(1);

        assertFalse(inMemoryHistoryManager.getTasks().contains(task), "История не пуста");
    }

    @Test
    void removeMethodShouldThrowExceptionWhenIdisNotExist() {
        inMemoryHistoryManager.clearHistory();

        IdPassingException idPassingException = assertThrows(IdPassingException.class,
                () -> inMemoryHistoryManager.remove(1));

        assertEquals("В истории не существует задачи с переданным ID: "
                + 1, idPassingException.getDetailedMessage());
    }

    @Test
    void clearHistoryShouldRemoveAllTaskFormHistoryListGetTaskShouldReturnEmptyList() {
        Task task = new Task(1, "New Task for HistoryManager testing",
                "details for Task for HistoryManager testing", TaskStatus.NEW, TaskType.SIMPLE_TASK);
        Epic epic = new Epic(2, "New Epic for HistoryManager testing",
                "details for Epic for HistoryManager testing", TaskStatus.NEW, new ArrayList<>());
        epic.setTaskType(TaskType.EPIC);

        inMemoryHistoryManager.add(task);
        inMemoryHistoryManager.add(epic);
        inMemoryHistoryManager.clearHistory();

        assertTrue(inMemoryHistoryManager.getTasks().isEmpty(),
                "История содержит задачи");
    }
}