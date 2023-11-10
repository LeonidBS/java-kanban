import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.service.TaskManager;
import ru.yandex.practicum.kanban.service.exceptions.IdPassingException;
import ru.yandex.practicum.kanban.service.exceptions.SubtaskCreationException;
import ru.yandex.practicum.kanban.service.exceptions.TimeSlotException;
import ru.yandex.practicum.kanban.service.exceptions.UpdateTaskException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    private final T taskManager;
    private IdPassingException idPassingException;
    private UpdateTaskException updateTaskException;
    private TimeSlotException timeSlotException;

    public TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    @Test
    void retrieveTaskByIdTestShouldThrowExceptionWhenIdIsNotExisted() {

        Executable executable = () -> taskManager.retrieveTaskById(0);

        idPassingException = assertThrows(IdPassingException.class, executable);
        assertEquals("Не существует задачи с переданным ID: " + 0, idPassingException.getDetailedMessage());
    }

    @Test
    void createTaskShouldAddTypeStatusTimeslotAndPutInTasksMapSameTaskWhichCalledByConstructor() {
        Task newTask = new Task("Test createSimpleTaskTest", "Details for Test createSimpleTaskTest",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);

        final int newID = taskManager.createTask(newTask);
        Task savedTask = taskManager.retrieveTaskById(newID);
        Task task = new Task(newID, "Test createSimpleTaskTest",
                "Details for Test createSimpleTaskTest", TaskStatus.NEW, TaskType.SIMPLE_TASK,
                LocalDateTime.parse("2023-02-01T08:00"), 1800);

        assertEquals(task, savedTask, "Созданая задача не совпадает со считанной");
    }

    @Test
    void createSimpleTaskTimeExceptionTest() {
        Task newTask = new Task("Test createSimpleTaskTimeExceptionTest",
                "Details for Test createSimpleTaskTimeExceptionTest",
                LocalDateTime.parse("2021-02-01T08:00"), 1800);


        Executable executable = () -> taskManager.createTask(newTask);

        timeSlotException = assertThrows(TimeSlotException.class, executable);
        assertEquals("Получено некорректное время выполнения задачи: "
                        + "время старта:" + "2021-02-01T08:00" + ", продолжительность:" + 1800,
                timeSlotException.getDetailedMessage());
    }

    @Test
    void createTaskShouldAddTypeStatusTimeslotAndPutInEpicsMapSameEpicWhichCalledByConstructor() {
        Epic newEpic = new Epic("Test createEpicTest",
                "Details for Test createEpicTest");

        final int newID = taskManager.createTask(newEpic);
        Epic savedEpic = (Epic) taskManager.retrieveTaskById(newID);
        Epic epic = new Epic(newID, "Test createEpicTest",
                "Details for Test createEpicTest", TaskStatus.NEW, new ArrayList<>());
        epic.setTaskType(TaskType.EPIC);

        assertEquals(epic, savedEpic, "Созданая задача не совпадает со считанной");
    }

    @Test
    void createTaskShouldAddTypeStatusTimeslotAndPutInSubtasksMapSameSubtaskWhichCalledByConstructor() {
        Epic epic = new Epic("Epic createSubtaskTest",
                "Details Epic for Test createSubtaskTest");

        final int epicID = taskManager.createTask(epic);

        Subtask newSubtask = new Subtask("Subtask createSubtaskTest",
                "Details Subtask for Test createSubtaskTest", epicID,
                LocalDateTime.parse("2023-04-09T05:00"), 900);

        final int newID = taskManager.createTask(newSubtask);

        Subtask savedEpic = (Subtask) taskManager.retrieveTaskById(newID);
        Subtask subtask = new Subtask(newID, "Subtask createSubtaskTest",
                "Details Subtask for Test createSubtaskTest", TaskStatus.NEW, epicID,
                LocalDateTime.parse("2023-04-09T05:00"), 900);
        subtask.setTaskType(TaskType.SUBTASK);

        assertEquals(subtask, savedEpic, "Созданая задача не совпадает со считанной");
    }

    @Test
    void createTaskShouldThrowExceptionWhenIdEpicOfFieldsOfNewSubtaskIsNotExist() {
        Subtask newSubtask = new Subtask("Epic createSubtaskWithoutEpicTest",
                "Details Epic for Test createSubtaskWithoutEpicTest", 0, 1800);

        Executable executable = () -> taskManager.createTask(newSubtask);

        SubtaskCreationException subtaskCreationException = assertThrows(SubtaskCreationException.class, executable);
        assertEquals("Не существует Эпика с переданным ID: " + 0,
                subtaskCreationException.getDetailedMessage());
    }

    @Test
    void createTaskShouldThrowExceptionWhenTimeslotOfNewSubtaskIsNotCorrect() {
        Epic epic = new Epic("Epic createSubtaskTimeExceptionTest",
                "Details Epic for Test createSubtaskTimeExceptionTest");
        final int epicID = taskManager.createTask(epic);
        Subtask newSubtask = new Subtask("Test createSubtaskTimeExceptionTest",
                "Details for Test createSubtaskTimeExceptionTest", epicID,
                LocalDateTime.parse("2023-02-01T08:00"), 1833);


        Executable executable = () -> taskManager.createTask(newSubtask);

        timeSlotException = assertThrows(TimeSlotException.class, executable);
        assertEquals("Получено некорректное время выполнения задачи: "
                        + "время старта:" + "2023-02-01T08:00" + ", продолжительность:" + 1833,
                timeSlotException.getDetailedMessage());
    }

    @Test
    void updateTaskShouldPutInTasksMapTaskWhichCreatedByConstructorWithUpdatedStatusTimeslot() {
        Task originalTask = new Task("Test updateSimpleTaskTest",
                "Details for Test updateSimpleTaskTest",
                LocalDateTime.parse("2023-02-21T18:00"), 3000);
        final int newID = taskManager.createTask(originalTask);
        Task updatedTask = new Task(newID, "Test updateSimpleTaskTest",
                "Details for Test updateSimpleTaskTest", TaskStatus.IN_PROGRESS,
                LocalDateTime.parse("2023-02-25T08:00"), 3300);

        taskManager.updateTask(updatedTask);
        Task savedTask = taskManager.retrieveTaskById(newID);

        assertEquals(updatedTask, savedTask, "Переданная задача не совпадает со считанной");
    }

    @Test
    void updateTaskShouldThrowExceptionWhenIdOfUpdatedTaskIsNotExist() {
        Task updatedTask = new Task(0, "Test updateTaskTestIDException",
                "Details for Test updateTaskTestIDException", TaskStatus.IN_PROGRESS,
                LocalDateTime.parse("2023-02-21T18:00"), 3000);

        Executable executable = () -> taskManager.updateTask(updatedTask);

        idPassingException = assertThrows(IdPassingException.class, executable);
        assertEquals("Не существует задачи с переданным ID: " + 0,
                idPassingException.getDetailedMessage());
    }

    @Test
    void updateTaskShouldThrowExceptionWhenITimeslotOfUpdatedTaskIsNotCorrect() {
        Task originalTask = new Task("Test updateSimpleTaskTest",
                "Details for Test updateSimpleTaskTest",
                LocalDateTime.parse("2023-02-21T18:00"), 3000);
        final int newID = taskManager.createTask(originalTask);
        Task updatedTask = new Task(newID, "Test updateSimpleTaskTest",
                "Details for Test updateSimpleTaskTest", TaskStatus.IN_PROGRESS,
                LocalDateTime.parse("2027-02-25T08:00"), 999);


        Executable executable = () -> taskManager.updateTask(updatedTask);

        timeSlotException = assertThrows(TimeSlotException.class, executable);
        assertEquals("Получено некорректное время выполнения задачи: "
                        + "время старта:" + "2027-02-25T08:00" + ", продолжительность:" + 999,
                timeSlotException.getDetailedMessage());
    }

    @Test
    void updateTaskShouldThrowExceptionWhenUpdatedTaskTypeNotConsistWithSavedTask() {
        Task originalTask = new Task("Test updateTaskTestEqualityException",
                "Details for Test updateTaskTestEqualityException", 9000);
        final int newID = taskManager.createTask(originalTask);
        Task savedTask = taskManager.retrieveTaskById(newID);
        Task updatedTask = new Task(newID, "Test updateTaskTestEqualityException",
                "Details for Test updateTaskTestEqualityException",
                TaskStatus.IN_PROGRESS, TaskType.EPIC, LocalDateTime.parse("2023-02-21T18:00"), LocalDateTime.parse("2023-02-28T05:20"), 9000);

        Executable executable = () -> taskManager.updateTask(updatedTask);

        updateTaskException = assertThrows(UpdateTaskException.class, executable);
        assertEquals("Переданная задача и задача в базе данных имеют недопустимые отличия\n"
                + updatedTask + "\n" + savedTask, updateTaskException.getDetailedMessage());
    }

    @Test
    void updateTaskShouldPutInEpicsMapEpicWhichCreatedByConstructorWithUpdatedStatus() {
        Epic originalEpic = new Epic("Test updateEpicTest",
                "Details for Test updateEpicTest");
        final int newID = taskManager.createTask(originalEpic);
        Epic updatedEpic = new Epic(newID, "Test updateEpicTest. Text is edited",
                "Details for Test updateEpicTest. Text is edited", TaskStatus.NEW, new ArrayList<>());

        taskManager.updateTask(updatedEpic);
        Epic savedEpic = (Epic) taskManager.retrieveTaskById(newID);

        assertEquals(updatedEpic, savedEpic, "Переданная задача не совпадает со считанной");
    }

    @Test
    void updateTaskShouldThrowExceptionWhenIdOfUpdatedEpicIsNotExist() {
        Epic updatedTEpic = new Epic(0, "Test updateEpicTestIDException",
                "Details for Test updateEpicTestIDException",
                TaskStatus.IN_PROGRESS, new ArrayList<>());

        Executable executable = () -> taskManager.updateTask(updatedTEpic);

        idPassingException = assertThrows(IdPassingException.class, executable);
        assertEquals("Не существует задачи с переданным ID: " + 0,
                idPassingException.getDetailedMessage());
    }

    @Test
    void updateTaskShouldThrowExceptionWhenUpdatedEpicSubtaskListNotConsistWithSavedEpicList() {
        Epic originalEpic = new Epic("Test updateTaskEpicEqualityException",
                "Details for Test updateTaskEpicEqualityException");
        final int newID = taskManager.createTask(originalEpic);
        List<Integer> subtasksList = Arrays.asList(7, 8);
        Epic updatedSubtasksEpic = new Epic(newID, "Test updateTaskEpicEqualityException. Text is edited",
                "Details for Test updateTaskEpicEqualityException. Text is edited",
                TaskStatus.NEW, subtasksList);
        Epic savedEpic = (Epic) taskManager.retrieveTaskById(newID);

        Executable executable = () -> taskManager.updateTask(updatedSubtasksEpic);

        updateTaskException = assertThrows(UpdateTaskException.class, executable);
        assertEquals("Переданная задача и задача в базе данных имеют недопустимые отличия\n"
                + updatedSubtasksEpic + "\n" + savedEpic, updateTaskException.getDetailedMessage());
    }

    @Test
    void updateTaskShouldPutInSubtasksMapSubtaskWhichCreatedByConstructorWithUpdatedStatusCorrectedNameDetails() {
        Epic epic = new Epic("Epic updateSubtaskTest",
                "Details Epic for Test updateSubtaskTest");
        final int epicID = taskManager.createTask(epic);
        Subtask originalSubtask = new Subtask("Test updateSubtaskTest",
                "Details for Test updateSubtaskTest", epicID
                , LocalDateTime.parse("2023-02-07T18:00"), 3000);
        final int newID = taskManager.createTask(originalSubtask);
        Subtask updatedSubtask = new Subtask(newID, "Test updateSubtaskTest. Text is edited",
                "Details for Test updateSubtaskTest. Text is edited", TaskStatus.IN_PROGRESS,
                epicID, LocalDateTime.parse("2023-02-17T18:00"), 3000);

        taskManager.updateTask(updatedSubtask);
        Subtask savedSubtask = (Subtask) taskManager.retrieveTaskById(newID);

        assertEquals(updatedSubtask, savedSubtask, "Переданная задача не совпадает со считанной");
    }

    @Test
    void updateTaskShouldThrowExceptionWhenIdOfUpdatedSubtaskIsNotExist() {
        Epic epic = new Epic("Epic Test updateSubtaskTestIDException",
                "Details Epic for Test updateSubtaskTestIDException");
        final int epicID = taskManager.createTask(epic);
        Subtask originalSubtask = new Subtask("Subtask updateSubtaskTestIDException",
                "Subtask Details for Test updateSubtaskTestIDException", epicID,
                LocalDateTime.parse("2023-02-07T18:00"), 3000);
        taskManager.createTask(originalSubtask);
        Subtask updatedSubtask = new Subtask(0, "Subtask Test updateSubtaskTestIDException",
                "Subtask Details for Test updateSubtaskTestIDException", TaskStatus.IN_PROGRESS,
                epicID, LocalDateTime.parse("2023-02-17T18:00"), 3300);

        Executable executable = () -> taskManager.updateTask(updatedSubtask);

        idPassingException = assertThrows(IdPassingException.class, executable);
        assertEquals("Не существует задачи с переданным ID: "
                + 0, idPassingException.getDetailedMessage());
    }

    @Test
    void updateTaskShouldThrowExceptionWhenUpdatedSubtaskEpicIdNotConsistWithSavedSubtaskEpicId() {
        Epic epic = new Epic("Epic updateTaskSubtaskEqualityException",
                "Details Epic for Test updateTaskSubtaskEqualityException");
        final int epicID = taskManager.createTask(epic);
        Subtask originalSubtask = new Subtask("Subtask Test updateTaskSubtaskEqualityException",
                "Subtask Details for Test updateTaskSubtaskEqualityException", epicID,
                LocalDateTime.parse("2023-02-07T18:00"), 3000);
        int newID = taskManager.createTask(originalSubtask);
        Subtask updatedSubtask = new Subtask(newID, "Subtask Test updateTaskSubtaskEqualityException",
                "Subtask Details for Test updateTaskSubtaskEqualityException", TaskStatus.NEW,
                0, LocalDateTime.parse("2023-02-17T08:00"), 3000);

        Executable executable = () -> taskManager.updateTask(updatedSubtask);

        updateTaskException = assertThrows(UpdateTaskException.class, executable);
        assertEquals("Переданная задача и задача в базе данных имеют недопустимые отличия\n"
                + updatedSubtask + "\n" + originalSubtask, updateTaskException.getDetailedMessage());
    }

    @Test
    void updateTaskShouldThrowExceptionWhenITimeslotOfUpdatedSubtaskIsNotCorrect() {
        Epic epic = new Epic("Epic updateTaskSubtaskEqualityException",
                "Details Epic for Test updateTaskSubtaskEqualityException");
        final int epicID = taskManager.createTask(epic);
        Subtask originalSubtask = new Subtask("Subtask Test updateTaskSubtaskEqualityException",
                "Subtask Details for Test updateTaskSubtaskEqualityException", epicID,
                LocalDateTime.parse("2023-02-07T18:00"), 3000);
        int newID = taskManager.createTask(originalSubtask);
        Subtask updatedSubtask = new Subtask(newID, "Subtask Test updateTaskSubtaskEqualityException",
                "Subtask Details for Test updateTaskSubtaskEqualityException", TaskStatus.IN_PROGRESS,
                epicID, LocalDateTime.parse("2020-02-17T08:00"), 0);

        Executable executable = () -> taskManager.updateTask(updatedSubtask);

        timeSlotException = assertThrows(TimeSlotException.class, executable);
        assertEquals("Получено некорректное время выполнения задачи: "
                        + "время старта:" + "2020-02-17T08:00" + ", продолжительность:" + 0,
                timeSlotException.getDetailedMessage());
    }

    @Test
    void deleteTaskShouldRemoveFromTasksMapTaskWithIdRemovingCheckByRetrieveCompleteList() {
        Task task = new Task("Test deleteTaskTest", "Details for Test deleteTaskTest",
                LocalDateTime.parse("2023-02-21T18:00"), 3000);
        final int newID = taskManager.createTask(task);
        Task savedTask = taskManager.retrieveTaskById(newID);

        taskManager.deleteTask(newID);
        List<Task> tasksList = taskManager.retrieveCompleteList();
        Executable executable = () -> taskManager.retrieveTaskById(newID);

        idPassingException = assertThrows(IdPassingException.class, executable);
        assertEquals("Не существует задачи с переданным ID: "
                + newID, idPassingException.getDetailedMessage());
        assertFalse(tasksList.contains(savedTask), "Задача не удалена из списка");
    }

    @Test
    void deleteTaskShouldRemoveFromEpicsMapEpicWithNoSubtaskRemovingCheckByRetrieveCompleteList() {
        Epic epic = new Epic("Test deleteEpicWithNoSubtasksTest",
                "Details for Test deleteEpicWithNoSubtasksTest");
        final int newID = taskManager.createTask(epic);
        Task savedEpic = taskManager.retrieveTaskById(newID);

        taskManager.deleteTask(newID);
        List<Task> tasksList = taskManager.retrieveCompleteList();

        Executable executable = () -> taskManager.retrieveTaskById(newID);

        idPassingException = assertThrows(IdPassingException.class, executable);
        assertEquals("Не существует задачи с переданным ID: "
                + newID, idPassingException.getDetailedMessage());
        assertFalse(tasksList.contains(savedEpic), "Задача не удалена из списка");
    }

    //Смысл данного теста проверить , если удаляется Эпик, то удаляются все го подзадачи.
    @Test
    void deleteTaskShouldRemoveFromEpicsMapEpicAndItsSubtaskWithEpicIdRemovingCheckByRetrieveTaskById() {
        Epic epic = new Epic("Test deleteEpicWithSubtasksTest",
                "Details for Test deleteEpicWithSubtasksTest");
        final int idNewEpic = taskManager.createTask(epic);
        Subtask subtaskFirst = new Subtask("S1 Test deleteEpicWithSubtasksTest",
                "S1 Details for Test deleteEpicWithSubtasksTest", idNewEpic,
                LocalDateTime.parse("2023-02-21T18:00"), 3000);
        final int idSubtaskFirst = taskManager.createTask(subtaskFirst);
        Subtask subtaskSecond = new Subtask("S2 Test deleteEpicWithSubtasksTest",
                "S2 Details for Test deleteEpicWithSubtasksTest", idNewEpic,
                LocalDateTime.parse("2023-02-25T08:00"), 3330);
        final int idSubtaskSecond = taskManager.createTask(subtaskSecond);
        Epic savedEpic = (Epic) taskManager.retrieveTaskById(idNewEpic);
        Subtask savedSubtaskFirst = (Subtask) taskManager.retrieveTaskById(idSubtaskFirst);
        Subtask savedSubtaskSecond = (Subtask) taskManager.retrieveTaskById(idSubtaskSecond);

        taskManager.deleteTask(idNewEpic);
        List<Task> tasksList = taskManager.retrieveCompleteList();
        Executable executableIdNewEpic = () -> taskManager.retrieveTaskById(idNewEpic);
        Executable executableIdSubtaskFirst = () -> taskManager.retrieveTaskById(idSubtaskFirst);
        Executable executableIdSubtaskSecond = () -> taskManager.retrieveTaskById(idSubtaskSecond);


        idPassingException = assertThrows(IdPassingException.class, executableIdNewEpic);
        assertEquals("Не существует задачи с переданным ID: "
                + idNewEpic, idPassingException.getDetailedMessage());
        idPassingException = assertThrows(IdPassingException.class, executableIdSubtaskFirst);
        assertEquals("Не существует задачи с переданным ID: "
                + idSubtaskFirst, idPassingException.getDetailedMessage());
        idPassingException = assertThrows(IdPassingException.class, executableIdSubtaskSecond);
        assertEquals("Не существует задачи с переданным ID: "
                + idSubtaskSecond, idPassingException.getDetailedMessage());
        assertFalse(tasksList.contains(savedEpic) || tasksList.contains(savedSubtaskFirst)
                || tasksList.contains(savedSubtaskSecond), "Задача не удалена из списка");
    }

    @Test
    void deleteTaskShouldRemoveFromSubtasksMapSubtaskWithIdRemovingCheckByRetrieveCompleteListAndRetrieveTaskById() {
        Epic epic = new Epic("Test deleteSubtaskTest",
                "Details for Test deleteSubtaskTest");
        final int idNewEpic = taskManager.createTask(epic);
        Subtask subtask = new Subtask("Test deleteSubtaskTest",
                "Details for Test deleteSubtaskTest", idNewEpic,
                LocalDateTime.parse("2023-02-21T18:00"), 3000);
        final int newID = taskManager.createTask(subtask);
        Subtask savedSubtask = (Subtask) taskManager.retrieveTaskById(newID);

        taskManager.deleteTask(newID);
        List<Task> tasksList = taskManager.retrieveCompleteList();
        Executable executable = () -> taskManager.retrieveTaskById(newID);

        idPassingException = assertThrows(IdPassingException.class, executable);
        assertEquals("Не существует задачи с переданным ID: "
                + newID, idPassingException.getDetailedMessage());
        assertFalse(tasksList.contains(savedSubtask), "Задача не удалена из списка");
    }

    @Test
    void deleteTaskShouldThrowExcetionWhenTaskIdIsNotExist() {
        idPassingException = assertThrows(IdPassingException.class,
                () -> taskManager.deleteTask(0));

        assertEquals("Не существует задачи с переданным ID: "
                + 0, idPassingException.getDetailedMessage());
    }

    @Test
    void retrieveCompleteListShouldReturnAllExistedTaskWhichAreCreated() {
        taskManager.clearTaskList();
        Epic epic = new Epic("Epic Test retrieveCompleteListTest",
                "Epic Details for Test retrieveCompleteListTest");
        final int idNewEpic = taskManager.createTask(epic);
        Subtask subtask = new Subtask("Subtask Test retrieveCompleteListTest",
                "Subtask Details for Test retrieveCompleteListTest", idNewEpic,
                LocalDateTime.parse("2023-02-21T11:00"), 3300);
        taskManager.createTask(subtask);
        Task task = new Task("Task Test retrieveCompleteListTest",
                "Task Details for Test retrieveCompleteListTest",
                LocalDateTime.parse("2023-02-24T12:00"), 930);
        taskManager.createTask(task);

        List<Task> tasksCompleteList = taskManager.retrieveCompleteList();

        assertEquals(3, tasksCompleteList.size(), "Неверное количество задач в списке");
        assertTrue(tasksCompleteList.contains(epic)
                && tasksCompleteList.contains(subtask)
                && tasksCompleteList.contains(task), "Полный лист задач не содержит все созданные задачи");
    }

    @Test
    void retrieveCompleteListShouldReturnEmptyListWhenTasksEpicsSubtasksMapAreEmpty() {
        taskManager.clearTaskList();
        List<Task> tasksCompleteList = taskManager.retrieveCompleteList();
        StringBuilder allTasks = new StringBuilder();
        for (Task task : tasksCompleteList) {
            allTasks.append(task.toString()).append("\n");
        }

        assertTrue(tasksCompleteList.isEmpty(), "Список задач не пуст" + allTasks);
    }

    @Test
    void clearTaskListShouldRemoveAllTasksEpicsSubtasksRemovingCheckByRetrieveCompleteList() {
        Epic epic = new Epic("Epic Test clearTaskListTest",
                "Epic Details for Test clearTaskListTest");
        final int idNewEpic = taskManager.createTask(epic);
        new Subtask("Subtask Test clearTaskListTest",
                "Subtask Details for Test clearTaskListTest", idNewEpic,
                LocalDateTime.parse("2023-02-21T11:00"), 3300);
        new Task("Task Test clearTaskListTest",
                "Task Details for Test clearTaskListTest",
                LocalDateTime.parse("2023-02-24T12:00"), 930);

        taskManager.clearTaskList();
        List<Task> tasksCompleteList = taskManager.retrieveCompleteList();

        assertTrue(tasksCompleteList.isEmpty(), "Список задач не пуст");
    }

    @Test
    void getSubtasksBYEpicIdShouldRetrieveExcpectedSubtasks() {
        Epic epic = new Epic("Test deleteSubtaskTest",
                "Details for Test deleteSubtaskTest");
        final int idNewEpic = taskManager.createTask(epic);
        Subtask subtask1 = new Subtask("Test deleteSubtaskTest",
                "Details for Test deleteSubtaskTest", idNewEpic,
                LocalDateTime.parse("2023-02-21T18:00"), 3000);
        taskManager.createTask(subtask1);
        Subtask subtask2 = new Subtask("Test deleteSubtaskTest",
                "Details for Test deleteSubtaskTest", idNewEpic,
                LocalDateTime.parse("2023-02-21T18:00"), 3000);
        taskManager.createTask(subtask2);
        List<Subtask> expectedSubtaskList = Arrays.asList(subtask1, subtask2);

        List<Subtask> testedSubtaskList = taskManager.retrieveSubtasks(idNewEpic);

        assertNotNull(testedSubtaskList, "Список подзадач не получен");
        assertEquals(expectedSubtaskList, testedSubtaskList, "Список подзадач не соответствует заданному");
    }

}