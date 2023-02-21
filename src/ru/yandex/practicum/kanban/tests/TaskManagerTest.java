package ru.yandex.practicum.kanban.tests;

import ru.yandex.practicum.kanban.exceptions.IdPassingException;
import ru.yandex.practicum.kanban.exceptions.SubtaskCreationException;
import ru.yandex.practicum.kanban.exceptions.TimeSlotException;
import ru.yandex.practicum.kanban.exceptions.UpdateTaskException;
import ru.yandex.practicum.kanban.model.*;
import ru.yandex.practicum.kanban.service.TaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.kanban.service.InMemoryTaskManager.writingHashFormFile;

abstract class TaskManagerTest<T extends TaskManager> {
    private final T taskManager;
    private IdPassingException idPassingException;
    private SubtaskCreationException subtaskCreationException;
    private UpdateTaskException updateTaskException;
    private TimeSlotException timeSlotException;

    public TaskManagerTest(T taskManager) {
        this.taskManager = taskManager;
    }

    void retrieveTaskByIdTest() {
        taskManager.clearTaskList();
        List<Integer> subtaskList = Arrays.asList(2, 3);
        Epic epic = new Epic(1, "TestEpic", "DetailTestEpic",
                TaskStatus.IN_PROGRESS, subtaskList);
        Subtask subtask1 = new Subtask(2, "TestSubtask1", "DetailSubtask1",
                TaskStatus.NEW, 1, LocalDateTime.parse("2023-02-09T05:00"), 900);
        Subtask subtask2 = new Subtask(3, "TestSubtask2", "DetailSubtask2",
                TaskStatus.DONE, 1, LocalDateTime.parse("2023-02-12T12:00"), 930);
        Task task = new Task(4, "TestTask", "DetailTestTask",
                TaskStatus.NEW, LocalDateTime.parse("2023-02-20T16:00"), 1500);

        writingHashFormFile(epic);
        writingHashFormFile(subtask1);
        writingHashFormFile(subtask2);
        writingHashFormFile(task);

        Epic savedEpic = (Epic) taskManager.retrieveTaskById(1);
        Subtask savedSubtask1 = (Subtask) taskManager.retrieveTaskById(2);
        Subtask savedSubtask2 = (Subtask) taskManager.retrieveTaskById(3);
        Task savedTask = taskManager.retrieveTaskById(4);
        assertEquals(epic, savedEpic, "После записи и считавания эпик не сопадает");
        assertEquals(subtask1, savedSubtask1, "После записи и считавания подзадача не сопадает");
        assertEquals(subtask2, savedSubtask2, "После записи и считавания подзадача не сопадает");
        assertEquals(task, savedTask, "После записи и считавания задача не сопадает");
        taskManager.clearTaskList();
    }

    void retrieveTaskByIdTestException() {
        idPassingException = assertThrows(IdPassingException.class, () -> taskManager.retrieveTaskById(0));
        assertEquals("Не существует задачи с переданным ID: " + 0, idPassingException.getDetailedMessage());
    }

    void createSimpleTaskTest() {
        Task newTask = new Task("Test createSimpleTaskTest", "Details for Test createSimpleTaskTest",
                LocalDateTime.parse("2023-02-01T08:00"), 1800);

        final int newID = taskManager.createTask(newTask);
        Task savedTask = taskManager.retrieveTaskById(newID);
        Task task = new Task(newID, "Test createSimpleTaskTest",
                "Details for Test createSimpleTaskTest", TaskStatus.NEW, TaskType.SIMPLE_TASK,
                LocalDateTime.parse("2023-02-01T08:00"), 1800);
        assertEquals(task, savedTask, "Созданая задача не совпадает со считанной");
    }

    void createSimpleTaskTimeExceptionTest() {
        Task newTask = new Task("Test createSimpleTaskTimeExceptionTest",
                "Details for Test createSimpleTaskTimeExceptionTest",
                LocalDateTime.parse("2021-02-01T08:00"), 1800);
        timeSlotException = assertThrows(TimeSlotException.class,
                () -> taskManager.createTask(newTask));
        assertEquals("Получено некорректное время выполнения задачи: "
                        + "время старта:" + "2021-02-01T08:00" + ", продолжительность:" + 1800,
                timeSlotException.getDetailedMessage());
    }

    void createEpicTest() {
        Epic newEpic = new Epic("Test createEpicTest",
                "Details for Test createEpicTest");

        final int newID = taskManager.createTask(newEpic);
        Epic savedEpic = (Epic) taskManager.retrieveTaskById(newID);
        Epic epic = new Epic(newID, "Test createEpicTest",
                "Details for Test createEpicTest", TaskStatus.NEW, new ArrayList<>());
        epic.setTaskType(TaskType.EPIC);
        assertEquals(epic, savedEpic, "Созданая задача не совпадает со считанной");
    }

    void createSubtaskTest() {
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

    void createSubtaskWithoutEpicTest() {
        Subtask newSubtask = new Subtask("Epic createSubtaskWithoutEpicTest",
                "Details Epic for Test createSubtaskWithoutEpicTest", 0, 1800);

        subtaskCreationException = assertThrows(SubtaskCreationException.class,
                () -> taskManager.createTask(newSubtask));
        assertEquals("Не существует Эпика с переданным ID: " + 0,
                subtaskCreationException.getDetailedMessage());
    }

    void createSubtaskTimeExceptionTest() {
        Epic epic = new Epic("Epic createSubtaskTimeExceptionTest",
                "Details Epic for Test createSubtaskTimeExceptionTest");
        final int epicID = taskManager.createTask(epic);
        Subtask newSubtask = new Subtask("Test createSubtaskTimeExceptionTest",
                "Details for Test createSubtaskTimeExceptionTest", epicID,
                LocalDateTime.parse("2023-02-01T08:00"), 1833);

        timeSlotException = assertThrows(TimeSlotException.class,
                () -> taskManager.createTask(newSubtask));
        assertEquals("Получено некорректное время выполнения задачи: "
                        + "время старта:" + "2023-02-01T08:00" + ", продолжительность:" + 1833,
                timeSlotException.getDetailedMessage());
    }

    void updateTaskTest() {
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

    void updateTaskTestIDException() {
        Task updatedTask = new Task(0, "Test updateTaskTestIDException",
                "Details for Test updateTaskTestIDException", TaskStatus.IN_PROGRESS,
                LocalDateTime.parse("2023-02-21T18:00"), 3000);

        idPassingException = assertThrows(IdPassingException.class,
                () -> taskManager.updateTask(updatedTask));
        assertEquals("Не существует задачи с переданным ID: " + 0,
                idPassingException.getDetailedMessage());
    }

    void updateTaskTestEqualityException() {
        Task originalTask = new Task("Test updateTaskTestEqualityException",
                "Details for Test updateTaskTestEqualityException", 9000);
        final int newID = taskManager.createTask(originalTask);
        Task savedTask = taskManager.retrieveTaskById(newID);
        Task updatedTask = new Task(newID, "Test updateTaskTestEqualityException",
                "Details for Test updateTaskTestEqualityException",
                TaskStatus.IN_PROGRESS, TaskType.EPIC, LocalDateTime.parse("2023-02-21T18:00"), LocalDateTime.parse("2023-02-28T05:20"), 9000);

        updateTaskException = assertThrows(UpdateTaskException.class, () -> taskManager.updateTask(updatedTask));
        assertEquals("Переданная задача и задача в базе данных имеют недопустимые отличия\n"
                + updatedTask + "\n" + savedTask, updateTaskException.getDetailedMessage());
    }

    void updateEpicTest() {
        Epic originalEpic = new Epic("Test updateEpicTest",
                "Details for Test updateEpicTest");
        final int newID = taskManager.createTask(originalEpic);
        Epic updatedEpic = new Epic(newID, "Test updateEpicTest. Text is edited",
                "Details for Test updateEpicTest. Text is edited", TaskStatus.NEW, new ArrayList<>());

        taskManager.updateTask(updatedEpic);
        Epic savedEpic = (Epic) taskManager.retrieveTaskById(newID);
        assertEquals(updatedEpic, savedEpic, "Переданная задача не совпадает со считанной");
    }

    void updateEpicTestIDException() {
        Epic updatedTEpic = new Epic(0, "Test updateEpicTestIDException",
                "Details for Test updateEpicTestIDException",
                TaskStatus.IN_PROGRESS, new ArrayList<>());

        idPassingException = assertThrows(IdPassingException.class,
                () -> taskManager.updateTask(updatedTEpic));
        assertEquals("Не существует задачи с переданным ID: " + 0,
                idPassingException.getDetailedMessage());
    }

    void updateTaskEpicEqualityException() {
        Epic originalEpic = new Epic("Test updateTaskEpicEqualityException",
                "Details for Test updateTaskEpicEqualityException");
        final int newID = taskManager.createTask(originalEpic);

        Epic updatedStatusEpic = new Epic(newID, "Test updateTaskEpicEqualityException. Text is edited",
                "Details for Test updateTaskEpicEqualityException. Text is edited",
                TaskStatus.IN_PROGRESS, new ArrayList<>());
        Epic savedEpic = (Epic) taskManager.retrieveTaskById(newID);
        updateTaskException = assertThrows(UpdateTaskException.class,
                () -> taskManager.updateTask(updatedStatusEpic));
        assertEquals("Переданная задача и задача в базе данных имеют недопустимые отличия\n"
                + updatedStatusEpic + "\n" + savedEpic, updateTaskException.getDetailedMessage());

        List<Integer> subtasksList = Arrays.asList(7, 8);
        Epic updatedSubtasksEpic = new Epic(newID, "Test updateTaskEpicEqualityException. Text is edited",
                "Details for Test updateTaskEpicEqualityException. Text is edited",
                TaskStatus.NEW, subtasksList);
        updateTaskException = assertThrows(UpdateTaskException.class,
                () -> taskManager.updateTask(updatedSubtasksEpic));
        assertEquals("Переданная задача и задача в базе данных имеют недопустимые отличия\n"
                + updatedSubtasksEpic + "\n" + savedEpic, updateTaskException.getDetailedMessage());
    }

    void updateSubtaskTest() {
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

    void updateSubtaskTestIDException() {
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

        idPassingException = assertThrows(IdPassingException.class,
                () -> taskManager.updateTask(updatedSubtask));
        assertEquals("Не существует задачи с переданным ID: "
                + 0, idPassingException.getDetailedMessage());
    }

    void updateTaskSubtaskEqualityException() {
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

        updateTaskException = assertThrows(UpdateTaskException.class,
                () -> taskManager.updateTask(updatedSubtask));
        assertEquals("Переданная задача и задача в базе данных имеют недопустимые отличия\n"
                + updatedSubtask + "\n" + originalSubtask, updateTaskException.getDetailedMessage());
    }

    void deleteTaskTest() {
        Task task = new Task("Test deleteTaskTest", "Details for Test deleteTaskTest",
                LocalDateTime.parse("2023-02-21T18:00"), 3000);
        final int newID = taskManager.createTask(task);
        Task savedTask = taskManager.retrieveTaskById(newID);

        taskManager.deleteTask(newID);
        idPassingException = assertThrows(IdPassingException.class,
                () -> taskManager.retrieveTaskById(newID));
        assertEquals("Не существует задачи с переданным ID: "
                + newID, idPassingException.getDetailedMessage());

        List<Task> tasksList = taskManager.retrieveCompleteList();
        assertFalse(tasksList.contains(savedTask), "Задача не удалена из списка");
    }

    void deleteEpicWithNoSubtasksTest() {
        Epic epic = new Epic("Test deleteEpicWithNoSubtasksTest",
                "Details for Test deleteEpicWithNoSubtasksTest");
        final int newID = taskManager.createTask(epic);
        Task savedEpic = taskManager.retrieveTaskById(newID);

        taskManager.deleteTask(newID);
        idPassingException = assertThrows(IdPassingException.class,
                () -> taskManager.retrieveTaskById(newID));
        assertEquals("Не существует задачи с переданным ID: "
                + newID, idPassingException.getDetailedMessage());

        List<Task> tasksList = taskManager.retrieveCompleteList();
        assertFalse(tasksList.contains(savedEpic), "Задача не удалена из списка");
    }

    void deleteEpicWithSubtasksTest() {
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

        idPassingException = assertThrows(IdPassingException.class,
                () -> taskManager.retrieveTaskById(idNewEpic));
        assertEquals("Не существует задачи с переданным ID: "
                + idNewEpic, idPassingException.getDetailedMessage());
        idPassingException = assertThrows(IdPassingException.class,
                () -> taskManager.retrieveTaskById(idSubtaskFirst));
        assertEquals("Не существует задачи с переданным ID: "
                + idSubtaskFirst, idPassingException.getDetailedMessage());
        idPassingException = assertThrows(IdPassingException.class,
                () -> taskManager.retrieveTaskById(idSubtaskSecond));
        assertEquals("Не существует задачи с переданным ID: "
                + idSubtaskSecond, idPassingException.getDetailedMessage());

        List<Task> tasksList = taskManager.retrieveCompleteList();
        assertFalse(tasksList.contains(savedEpic) || tasksList.contains(savedSubtaskFirst)
                || tasksList.contains(savedSubtaskSecond), "Задача не удалена из списка");
    }

    void deleteSubtaskTest() {
        Epic epic = new Epic("Test deleteSubtaskTest",
                "Details for Test deleteSubtaskTest");
        final int idNewEpic = taskManager.createTask(epic);
        Subtask subtask = new Subtask("Test deleteSubtaskTest",
                "Details for Test deleteSubtaskTest", idNewEpic,
                LocalDateTime.parse("2023-02-21T18:00"), 3000);
        final int newID = taskManager.createTask(subtask);
        Subtask savedSubtask = (Subtask) taskManager.retrieveTaskById(newID);

        taskManager.deleteTask(newID);
        idPassingException = assertThrows(IdPassingException.class,
                () -> taskManager.retrieveTaskById(newID));
        assertEquals("Не существует задачи с переданным ID: "
                + newID, idPassingException.getDetailedMessage());
        List<Task> tasksList = taskManager.retrieveCompleteList();
        assertFalse(tasksList.contains(savedSubtask), "Задача не удалена из списка");
    }

    void deleteTaskIdExcetionTest() {
        idPassingException = assertThrows(IdPassingException.class,
                () -> taskManager.deleteTask(0));
        assertEquals("Не существует задачи с переданным ID: "
                + 0, idPassingException.getDetailedMessage());
    }

    void retrieveCompleteListTest() {
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

    void retrieveCompleteListIfEmptyTest() {
        taskManager.clearTaskList();
        List<Task> tasksCompleteList = taskManager.retrieveCompleteList();
        StringBuilder allTasks = new StringBuilder();
        for (Task task : tasksCompleteList) {
            allTasks.append(task.toString()).append("\n");
        }
        assertTrue(tasksCompleteList.isEmpty(), "Список задач не пуст" + allTasks);
    }

    void clearTaskListTest() {
        Epic epic = new Epic("Epic Test clearTaskListTest",
                "Epic Details for Test clearTaskListTest");
        final int idNewEpic = taskManager.createTask(epic);
        Subtask subtask = new Subtask("Subtask Test clearTaskListTest",
                "Subtask Details for Test clearTaskListTest", idNewEpic,
                LocalDateTime.parse("2023-02-21T11:00"), 3300);
        Task task = new Task("Task Test clearTaskListTest",
                "Task Details for Test clearTaskListTest",
                LocalDateTime.parse("2023-02-24T12:00"), 930);

        taskManager.clearTaskList();
        List<Task> tasksCompleteList = taskManager.retrieveCompleteList();
        assertTrue(tasksCompleteList.isEmpty(), "Список задач не пуст");

    }

}