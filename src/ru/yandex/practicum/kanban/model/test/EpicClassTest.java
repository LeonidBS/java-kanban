package ru.yandex.practicum.kanban.model.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicClassTest {
    private static final HashMap<Integer, Subtask> subtasksTest = new HashMap<>();

    @BeforeAll
    static void beforeAll() {

    }

    @Test
    void statusBySubtaskShouldReturnNewWhenAllSubtasksNew() {
        Subtask subtask1 = new Subtask(1, "Subtask 1 from file", "Details Subtask 1 from file",
                TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-10T02:00"), 900);
        Subtask subtask2 = new Subtask(2, "Subtask 2 from file", "Details Subtask 2 from file",
                TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-12T12:00"),
                LocalDateTime.parse("2023-02-13T03:30"), 930);
        Subtask subtask3 = new Subtask(3, "Subtask 3 from file", "Details Subtask 3 from file",
                TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-20T16:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 1500);
        subtasksTest.put(1, subtask1);
        subtasksTest.put(2, subtask2);
        subtasksTest.put(3, subtask3);
        List<Integer> subtasksList = Arrays.asList(1, 2, 3);
        Epic epic = new Epic(3, "Epic 1 from file", "Details Epic 1 from file",
                TaskStatus.NEW, subtasksList, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 3330);
        assertEquals(TaskStatus.NEW, epic.statusBySubtask(subtasksTest),
                "Статус эпика: " + epic.statusBySubtask(subtasksTest));
    }

    @Test
    void statusBySubtaskShouldReturnNewWhenAllSubtasksDONE() {
        Subtask subtask1 = new Subtask(1, "Subtask 1 from file", "Details Subtask 1 from file",
                TaskStatus.DONE, 3, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-10T02:00"), 900);
        Subtask subtask2 = new Subtask(2, "Subtask 2 from file", "Details Subtask 2 from file",
                TaskStatus.DONE, 3, LocalDateTime.parse("2023-02-12T12:00"),
                LocalDateTime.parse("2023-02-13T03:30"), 930);
        Subtask subtask3 = new Subtask(3, "Subtask 3 from file", "Details Subtask 3 from file",
                TaskStatus.DONE, 3, LocalDateTime.parse("2023-02-20T16:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 1500);
        subtasksTest.put(1, subtask1);
        subtasksTest.put(2, subtask2);
        subtasksTest.put(3, subtask3);
        List<Integer> subtasksList = Arrays.asList(1, 2, 3);
        Epic epic = new Epic(3, "Epic 1 from file", "Details Epic 1 from file",
                TaskStatus.NEW, subtasksList, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 3330);
        assertEquals(TaskStatus.DONE, epic.statusBySubtask(subtasksTest),
                "Статус эпика: " + epic.statusBySubtask(subtasksTest));
    }

    @Test
    void statusBySubtaskShouldReturnNewWhenAllSubtasksInProcess() {
        Subtask subtask1 = new Subtask(1, "Subtask 1 from file", "Details Subtask 1 from file",
                TaskStatus.IN_PROGRESS, 3, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-10T02:00"), 900);
        Subtask subtask2 = new Subtask(2, "Subtask 2 from file", "Details Subtask 2 from file",
                TaskStatus.IN_PROGRESS, 3, LocalDateTime.parse("2023-02-12T12:00"),
                LocalDateTime.parse("2023-02-13T03:30"), 930);
        Subtask subtask3 = new Subtask(3, "Subtask 3 from file", "Details Subtask 3 from file",
                TaskStatus.IN_PROGRESS, 3, LocalDateTime.parse("2023-02-20T16:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 1500);
        subtasksTest.put(1, subtask1);
        subtasksTest.put(2, subtask2);
        subtasksTest.put(3, subtask3);
        List<Integer> subtasksList = Arrays.asList(1, 2, 3);
        Epic epic = new Epic(3, "Epic 1 from file", "Details Epic 1 from file",
                TaskStatus.NEW, subtasksList, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 3330);
        assertEquals(TaskStatus.IN_PROGRESS, epic.statusBySubtask(subtasksTest),
                "Статус эпика: " + epic.statusBySubtask(subtasksTest));
    }

    @Test
    void statusBySubtaskShouldReturnNewWhenAtLeasetOneSubtaskNewOtherDoneOrInProcess() {
        Subtask subtask1 = new Subtask(1, "Subtask 1 from file", "Details Subtask 1 from file",
                TaskStatus.IN_PROGRESS, 3, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-10T02:00"), 900);
        Subtask subtask2 = new Subtask(2, "Subtask 2 from file", "Details Subtask 2 from file",
                TaskStatus.DONE, 3, LocalDateTime.parse("2023-02-12T12:00"),
                LocalDateTime.parse("2023-02-13T03:30"), 930);
        Subtask subtask3 = new Subtask(3, "Subtask 3 from file", "Details Subtask 3 from file",
                TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-20T16:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 1500);
        subtasksTest.put(1, subtask1);
        subtasksTest.put(2, subtask2);
        subtasksTest.put(3, subtask3);
        List<Integer> subtasksList = Arrays.asList(1, 2, 3);
        Epic epic = new Epic(3, "Epic 1 from file", "Details Epic 1 from file",
                TaskStatus.NEW, subtasksList, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 3330);
        assertEquals(TaskStatus.IN_PROGRESS, epic.statusBySubtask(subtasksTest),
                "Статус эпика: " + epic.statusBySubtask(subtasksTest));
    }

    @Test
    void startTimeBySubtaskShouldReturnTheEarliest() {
        Subtask subtask1 = new Subtask(1, "Subtask 1 from file", "Details Subtask 1 from file",
                TaskStatus.IN_PROGRESS, 3, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-10T02:00"), 900);
        Subtask subtask2 = new Subtask(2, "Subtask 2 from file", "Details Subtask 2 from file",
                TaskStatus.DONE, 3, LocalDateTime.parse("2023-02-12T12:00"),
                LocalDateTime.parse("2023-02-13T03:30"), 930);
        Subtask subtask3 = new Subtask(3, "Subtask 3 from file", "Details Subtask 3 from file",
                TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-20T16:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 1500);
        subtasksTest.put(1, subtask1);
        subtasksTest.put(2, subtask2);
        subtasksTest.put(3, subtask3);
        List<Integer> subtasksList = Arrays.asList(1, 2, 3);
        Epic epic = new Epic(3, "Epic 1 from file", "Details Epic 1 from file",
                TaskStatus.NEW, subtasksList);
        assertEquals(Optional.of(LocalDateTime.parse("2023-02-09T11:00")), epic.startTimeBySubtask(subtasksTest),
                "Статус эпика: " + epic.statusBySubtask(subtasksTest));
    }

    @Test
    void endTimeBySubtaskShouldReturnTheLatest() {
        Subtask subtask1 = new Subtask(1, "Subtask 1 from file", "Details Subtask 1 from file",
                TaskStatus.IN_PROGRESS, 3, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-10T02:00"), 900);
        Subtask subtask2 = new Subtask(2, "Subtask 2 from file", "Details Subtask 2 from file",
                TaskStatus.DONE, 3, LocalDateTime.parse("2023-02-12T12:00"),
                LocalDateTime.parse("2023-02-13T03:30"), 930);
        Subtask subtask3 = new Subtask(3, "Subtask 3 from file", "Details Subtask 3 from file",
                TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-20T16:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 1500);
        subtasksTest.put(1, subtask1);
        subtasksTest.put(2, subtask2);
        subtasksTest.put(3, subtask3);
        List<Integer> subtasksList = Arrays.asList(1, 2, 3);
        Epic epic = new Epic(3, "Epic 1 from file", "Details Epic 1 from file",
                TaskStatus.NEW, subtasksList);
        assertEquals(Optional.of(LocalDateTime.parse("2023-02-21T17:00")), epic.endTimeBySubtask(subtasksTest),
                "Статус эпика: " + epic.statusBySubtask(subtasksTest));
    }

    @Test
    void durationBySubtaskShouldReturnSum() {
        Subtask subtask1 = new Subtask(1, "Subtask 1 from file", "Details Subtask 1 from file",
                TaskStatus.IN_PROGRESS, 3, LocalDateTime.parse("2023-02-09T11:00"),
                LocalDateTime.parse("2023-02-10T02:00"), 900);
        Subtask subtask2 = new Subtask(2, "Subtask 2 from file", "Details Subtask 2 from file",
                TaskStatus.DONE, 3, LocalDateTime.parse("2023-02-12T12:00"),
                LocalDateTime.parse("2023-02-13T03:30"), 930);
        Subtask subtask3 = new Subtask(3, "Subtask 3 from file", "Details Subtask 3 from file",
                TaskStatus.NEW, 3, LocalDateTime.parse("2023-02-20T16:00"),
                LocalDateTime.parse("2023-02-21T17:00"), 1500);
        subtasksTest.put(1, subtask1);
        subtasksTest.put(2, subtask2);
        subtasksTest.put(3, subtask3);
        List<Integer> subtasksList = Arrays.asList(1, 2, 3);
        Epic epic = new Epic(3, "Epic 1 from file", "Details Epic 1 from file",
                TaskStatus.NEW, subtasksList);
        assertEquals(3330, epic.durationBySubtask(subtasksTest),
                "Статус эпика: " + epic.statusBySubtask(subtasksTest));
    }

}





