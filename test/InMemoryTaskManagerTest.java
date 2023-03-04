import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import ru.yandex.practicum.kanban.http.KVServer;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.service.HttpTaskManager;
import ru.yandex.practicum.kanban.service.InMemoryTaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public static final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    private static KVServer kvServer;

    InMemoryTaskManagerTest() {
        super(inMemoryTaskManager);
    }
@BeforeAll
static void beforeAll () throws IOException {
    kvServer = new KVServer();
    kvServer.start();
}
    @AfterAll
    static void computeTotalDurationTasksGotByRetrieveCompeteListToCompareWithTimeslotMapToCheckOverlapping() {
        List<Task> allTasks = inMemoryTaskManager.retrieveCompleteList();
        int totalDuration = allTasks.stream()
                .filter(task -> !task.getTaskType().equals(TaskType.EPIC))
                .mapToInt(Task::getDuration)
                .sum();

        TreeMap<LocalDateTime, Task> timeslotMap = inMemoryTaskManager.getTimeSlotMap();
        int totalTimeslot = (int) timeslotMap.values().stream()
                .filter(Objects::nonNull)
                .count() * 15;

        assertEquals(totalDuration, totalTimeslot, "Общее занятое время на выполнение событий" +
                " не соответствует сумме продолжительности всех событий");

            kvServer.stop();
      }
}