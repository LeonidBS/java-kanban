package ru.yandex.practicum.kanban.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.model.TaskType;
import ru.yandex.practicum.kanban.service.InMemoryTaskManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    public static final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    InMemoryTaskManagerTest() {
        super(inMemoryTaskManager);
    }

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

    @Override
    void createSimpleTaskTimeExceptionTest() {
        super.createSimpleTaskTimeExceptionTest();
    }

    @Override
    void createSubtaskTimeExceptionTest() {
        super.createSubtaskTimeExceptionTest();
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

    @Override
    void retrieveCompleteListTest() {
        super.retrieveCompleteListTest();
    }

    @Override
    void retrieveCompleteListIfEmptyTest() {
        super.retrieveCompleteListIfEmptyTest();
    }

    @Override
    void clearTaskListTest() {
        super.clearTaskListTest();
    }

    @AfterAll
    static void tasksTimeOverlapping() {
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
    }

}