package ru.yandex.practicum.kanban.tests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest extends TaskManagerTest<InMemoryTaskManager> {
    public static final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    public Epic testedEpic = new Epic("New Epic for status conformity testing",
            "details for Epic status conformity testing");

    EpicTest() {
        super(inMemoryTaskManager);
    }

    private int subtaskGetUpdate(int idEpic, TaskStatus status, String name) {
        Subtask subtask = new Subtask(name + " Subtask for Status Epic test", "Details of "
                + name + " Subtask for Status Epic test", idEpic, 2700);
        int idSubtask = inMemoryTaskManager.createTask(subtask);
        subtask = (Subtask) inMemoryTaskManager.retrieveTaskById(idSubtask);
        subtask.setStatus(status);
        inMemoryTaskManager.updateTask(subtask);
        return idSubtask;
    }

    @Test
    void StatusIfEmptySubtaskReferencesAfterEpicCreated() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);
        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус нового Эпика сразу после создания : "
                + epic.getStatus());
    }

    @Test
    void StatusIfEmptySubtaskReferencesAfterSubtasksEdited() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);
        int idSubtaskFirst = subtaskGetUpdate(idTestedEpic, TaskStatus.IN_PROGRESS, "First");
        int idSubtaskSecond = subtaskGetUpdate(idTestedEpic, TaskStatus.DONE, "Second");
        inMemoryTaskManager.deleteTask(idSubtaskFirst);
        inMemoryTaskManager.deleteTask(idSubtaskSecond);
        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус Эпика после добавления и "
                + "именения статуса и удаления всех подзадач : " + epic.getStatus());
    }

    @Test
    void StatusIfAllSubtaskGotNEWStatusAfterSubtaskCreated() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);
        Subtask subtaskFirst = new Subtask("The first Subtask for Status Epic test"
                , "Details of the first Subtask for Status Epic test", idTestedEpic, 1200);
        inMemoryTaskManager.createTask(subtaskFirst);
        Subtask subtaskSecond = new Subtask("The second Subtask for Status Epic test"
                , "Details of the second Subtask for Status Epic test", idTestedEpic, 1800);
        inMemoryTaskManager.createTask(subtaskSecond);
        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус Эпика после добавления "
                + "новых подзадач со статусом NEW : " + epic.getStatus());
    }

    @Test
    void StatusIfAllSubtaskGotNEWStatusAfterSubtaskEdited() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);
        int idSubtaskFirst = subtaskGetUpdate(idTestedEpic, TaskStatus.IN_PROGRESS, "First");
        int idSubtaskSecond = subtaskGetUpdate(idTestedEpic, TaskStatus.DONE, "Second");
        Subtask subtaskFirst = (Subtask) inMemoryTaskManager.retrieveTaskById(idSubtaskFirst);
        Subtask subtaskSecond = (Subtask) inMemoryTaskManager.retrieveTaskById(idSubtaskSecond);
        subtaskFirst.setStatus(TaskStatus.NEW);
        subtaskSecond.setStatus(TaskStatus.NEW);
        inMemoryTaskManager.updateTask(subtaskFirst);
        inMemoryTaskManager.updateTask(subtaskSecond);
        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус Эпика после добавления, "
                + "именения статусов подзадач и возврата статусов NEW всем подзадачам : " + epic.getStatus());
    }

    @Test
    void StatusIfAllSubtaskGotDONEStatus() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);
        subtaskGetUpdate(idTestedEpic, TaskStatus.DONE, "First");
        subtaskGetUpdate(idTestedEpic, TaskStatus.DONE, "Second");
        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус Эпика после добавления и "
                + "установки статуса DONE всем подзадачам : " + epic.getStatus());
    }

    @Test
    void StatusIfSubtaskGotDONEandNEWStatus() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);
        subtaskGetUpdate(idTestedEpic, TaskStatus.NEW, "First");
        subtaskGetUpdate(idTestedEpic, TaskStatus.DONE, "Second");
        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус Эпика, если статусы "
                + "подзадач DONE и NEW : " + epic.getStatus());
    }

    @Test
    void StatusIfAllSubtaskGotIN_PROCESSStatus() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);
        subtaskGetUpdate(idTestedEpic, TaskStatus.IN_PROGRESS, "First");
        subtaskGetUpdate(idTestedEpic, TaskStatus.IN_PROGRESS, "Second");
        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус Эпика, если статусы"
                + "всех подзадач IN_PROCESS : " + epic.getStatus());
    }

}