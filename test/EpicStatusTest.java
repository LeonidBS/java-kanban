import org.junit.jupiter.api.Test;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.TaskStatus;
import ru.yandex.practicum.kanban.service.InMemoryTaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicStatusTest {
    public static final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    public Epic testedEpic = new Epic("New Epic for status conformity testing",
            "details for Epic status conformity testing");

    @Test
    void epicGetStatusShouldNewWhenNoSubtaskAfterEpicIsCreated() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);

        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус нового Эпика сразу после создания : "
                + epic.getStatus());
    }

    @Test
    void epicGetStatusShouldNewWhenNoSubtaskAfterAllSubtaskAreDeleted() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);
        int idSubtaskFirst = subtaskCreatedUpdated(idTestedEpic, TaskStatus.IN_PROGRESS, "First");
        int idSubtaskSecond = subtaskCreatedUpdated(idTestedEpic, TaskStatus.DONE, "Second");

        inMemoryTaskManager.deleteTask(idSubtaskFirst);
        inMemoryTaskManager.deleteTask(idSubtaskSecond);
        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "Статус Эпика после добавления и "
                + "именения статуса и удаления всех подзадач : " + epic.getStatus());
    }

    @Test
    void epicGetStatusShouldNewWhenSubtaskAreCreatedNewNotEdited() {
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
    void epicGetStatusShouldNewWhenSubtaskBeenFirstUpdatedASecondRevertedToNew() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);
        int idSubtaskFirst = subtaskCreatedUpdated(idTestedEpic, TaskStatus.IN_PROGRESS, "First");
        int idSubtaskSecond = subtaskCreatedUpdated(idTestedEpic, TaskStatus.DONE, "Second");

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
    void epicGetStatusShouldDoneWhenAllSubtaskAreDone() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);

        subtaskCreatedUpdated(idTestedEpic, TaskStatus.DONE, "First");
        subtaskCreatedUpdated(idTestedEpic, TaskStatus.DONE, "Second");

        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус Эпика после добавления и "
                + "установки статуса DONE всем подзадачам : " + epic.getStatus());
    }

    @Test
    void epicGetStatusShouldInProcessWhenAtLeastOneSubtaskIsDoneOtherIsNew() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);
        subtaskCreatedUpdated(idTestedEpic, TaskStatus.NEW, "First");
        subtaskCreatedUpdated(idTestedEpic, TaskStatus.DONE, "Second");

        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус Эпика, если статусы "
                + "подзадач DONE и NEW : " + epic.getStatus());
    }

    @Test
    void epicGetStatusShouldInProcessWhenAllSubtasksIsInProcess() {
        int idTestedEpic = inMemoryTaskManager.createTask(testedEpic);
        subtaskCreatedUpdated(idTestedEpic, TaskStatus.IN_PROGRESS, "First");
        subtaskCreatedUpdated(idTestedEpic, TaskStatus.IN_PROGRESS, "Second");

        Epic epic = (Epic) inMemoryTaskManager.retrieveTaskById(idTestedEpic);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус Эпика, если статусы"
                + "всех подзадач IN_PROCESS : " + epic.getStatus());
    }

    private int subtaskCreatedUpdated(int idEpic, TaskStatus status, String name) {
        Subtask subtask = new Subtask(name + " Subtask for Status Epic test", "Details of "
                + name + " Subtask for Status Epic test", idEpic, 2700);

        int idSubtask = inMemoryTaskManager.createTask(subtask);

        subtask = (Subtask) inMemoryTaskManager.retrieveTaskById(idSubtask);
        subtask.setStatus(status);
        inMemoryTaskManager.updateTask(subtask);
        return idSubtask;
    }

}