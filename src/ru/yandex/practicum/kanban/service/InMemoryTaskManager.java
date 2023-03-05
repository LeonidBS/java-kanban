package ru.yandex.practicum.kanban.service;

import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.exceptions.IdPassingException;
import ru.yandex.practicum.kanban.service.exceptions.SubtaskCreationException;
import ru.yandex.practicum.kanban.service.exceptions.TimeSlotException;
import ru.yandex.practicum.kanban.service.exceptions.UpdateTaskException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;

public class InMemoryTaskManager implements TaskManager {  // README includes some comments about this code implementation
    private int id = 0;
    private LocalDateTime firstFreeSlot;
    private LocalDateTime startCalendarDate;
    private LocalDateTime endCalendarDate;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    private final Comparator<LocalDateTime> comparator = Comparator.naturalOrder();
    private final TreeMap<LocalDateTime, Task> timeSlotMap =
            new TreeMap<>(comparator);

    private final Predicate<Task> isExistTask = task -> tasks.get(task.getId()) != null;
    private final Predicate<Task> isExistEpic = epic -> epics.get(epic.getId()) != null;
    private final Predicate<Task> isExistSubtask = subtask -> subtasks.get(subtask.getId()) != null;
    private final Predicate<Task> isStartTimeSet = task -> task.getStartTime() == null;
    private final Predicate<Task> isTimeslotValid = task -> task.getStartTime().isAfter(startCalendarDate)
            && task.getStartTime().plusMinutes(task.getDuration()).isBefore(endCalendarDate)
            && task.getDuration() >= 15 && task.getDuration() % 15 == 0;
    private final Predicate<Task> isConsistWithExisted = task -> task.hashCode() == tasks.get(task.getId()).hashCode();
    private final Predicate<Subtask> isConsistWithExistedSubtask = subtask ->
            subtask.hashCode() == subtasks.get(subtask.getId()).hashCode();
    private final Predicate<Task> isConsistWithExistedEpic = epic ->
            epic.hashCode() == epics.get(epic.getId()).hashCode()
                    && epic.getStatus() == epics.get(epic.getId()).getStatus();

    public InMemoryTaskManager() {
        startCalendarDate = LocalDate.now().atStartOfDay().minusMonths(2);
        endCalendarDate = LocalDate.now().atStartOfDay()
                .plusMonths(13);
        LocalDateTime followTime = startCalendarDate;
        while (!followTime.isAfter(endCalendarDate)) {
            timeSlotMap.put(followTime, null);
            followTime = followTime.plusMinutes(15);
        }
        firstFreeSlot = startCalendarDate.plusMinutes(15);
        if (firstFreeSlot.isBefore(LocalDateTime.now())) {
            firstFreeSlot = getCurrentTime();
        }
    }

    @Override
    public TreeMap<LocalDateTime, Task> getTimeSlotMap() {
        return timeSlotMap;
    }

    @Override
    public int createTask(Task task) {
        task.setId(getId());
        if (isStartTimeSet.test(task)) {
            task.setStartTime(firstFreeSlot);
        }
        if (isTimeslotValid.test(task)) {
            task.setStartTime(takeTimeSlot(task));
            tasks.put(task.getId(), task);
            return task.getId();
        } else {
            throw new TimeSlotException("Получено некорректное время выполнения задачи: ",
                    task.getStartTime(), task.getDuration());
        }
    }

    @Override
    public int createTask(Epic epic) {
        epic.setId(getId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int createTask(Subtask subtask) {
        int epicReference = subtask.getEpicReference();
        if (epics.get(epicReference) != null) {
            subtask.setId(getId());
            if (isStartTimeSet.test(subtask)) {
                subtask.setStartTime(firstFreeSlot);
            }
            if (isTimeslotValid.test(subtask)) {
                subtask.setStartTime(takeTimeSlot(subtask));
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epics.get(epicReference);
                List<Integer> subtaskReferences = epic.getSubtaskReferences();
                subtaskReferences.add(subtask.getId());
                epic.setSubtaskReferences(subtaskReferences);
                epic.setStatus(epic.statusBySubtask(subtasks));
                if (epic.startTimeBySubtask(subtasks).isPresent()) {
                    epic.setStartTime(epic.startTimeBySubtask(subtasks).get());
                } else {
                    epic.setStartTime(null);
                }
                epic.setDuration(epic.durationBySubtask(subtasks));
                if (epic.endTimeBySubtask(subtasks).isPresent()) {
                    epic.setEndTime(epic.endTimeBySubtask(subtasks).get());
                } else {
                    epic.setEndTime(null);
                }
                epics.put(epicReference, epic);
                return subtask.getId();
            } else {
                throw new TimeSlotException("Получено некорректное время выполнения задачи: ",
                        subtask.getStartTime(), subtask.getDuration());
            }
        } else {
            throw new SubtaskCreationException("Не существует Эпика с переданным ID: ", epicReference);
        }
    }

    @Override
    public int updateTask(Task task) {
        if (isExistTask.test(task)) {
            if (isTimeslotValid.test(task)) {
                if (isConsistWithExisted.test(task)) {
                    deleteTimeSlot(tasks.get(task.getId()));
                    takeTimeSlot(task);
                    tasks.put(task.getId(), task);
                    return tasks.get(task.getId()).getId();
                } else {
                    throw new UpdateTaskException("Переданная задача и задача в базе " +
                            "данных имеют недопустимые отличия", task, tasks.get(task.getId()));
                }
            } else {
                throw new TimeSlotException("Получено некорректное время выполнения задачи: ",
                        task.getStartTime(), task.getDuration());
            }
        } else {
            throw new IdPassingException("Не существует задачи с переданным ID: ", task.getId());
        }
    }

    //Метод добавлен только для гипотетической возможности править название или описание Эпика
    @Override
    public int updateTask(Epic epic) {
        if (isExistEpic.test(epic)) {
            if (isConsistWithExistedEpic.test(epic)) {
                epics.put(epic.getId(), epic);
                return epics.get(epic.getId()).getId();
            } else {
                throw new UpdateTaskException("Переданная задача и задача в базе " +
                        "данных имеют недопустимые отличия", epic, epics.get(epic.getId()));
            }
        } else {
            throw new IdPassingException("Не существует задачи с переданным ID: ", epic.getId());
        }
    }

    @Override
    public int updateTask(Subtask subtask) {
        if (isExistSubtask.test(subtask)) {
            if (isTimeslotValid.test(subtask)) {
                if (isConsistWithExistedSubtask.test(subtask)) {
                    deleteTimeSlot(subtasks.get(subtask.getId()));
                    takeTimeSlot(subtask);
                    subtasks.put(subtask.getId(), subtask);
                    Epic epic = epics.get(subtask.getEpicReference());
                    epic.setStatus(epic.statusBySubtask(subtasks));
                    if (epic.startTimeBySubtask(subtasks).isPresent()) {
                        epic.setStartTime(epic.startTimeBySubtask(subtasks).get());
                    } else {
                        epic.setStartTime(null);
                    }
                    epic.setDuration(epic.durationBySubtask(subtasks));
                    if (epic.endTimeBySubtask(subtasks).isPresent()) {
                        epic.setEndTime(epic.endTimeBySubtask(subtasks).get());
                    } else {
                        epic.setEndTime(null);
                    }
                    epics.put(epic.getId(), epic);
                    return subtasks.get(subtask.getId()).getId();
                } else {
                    throw new UpdateTaskException("Переданная задача и задача в базе " +
                            "данных имеют недопустимые отличия", subtask, subtasks.get(subtask.getId()));
                }
            } else {
                throw new TimeSlotException("Получено некорректное время выполнения задачи: ",
                        subtask.getStartTime(), subtask.getDuration());
            }
        } else {
            throw new IdPassingException("Не существует задачи с переданным ID: ", subtask.getId());
        }
    }

    @Override
    public int deleteTask(int id) {
        if (tasks.get(id) != null) {
            deleteTimeSlot(tasks.get(id));
            tasks.remove(id);
            inMemoryHistoryManager.remove(id);
            return id;
        } else if (epics.get(id) != null) {
            List<Integer> subtaskReferences = epics.get(id).getSubtaskReferences();
            for (Integer subtaskReference : subtaskReferences) {
                if (subtasks.get(subtaskReference) != null) {
                    deleteTimeSlot(subtasks.get(subtaskReference));
                    subtasks.remove(subtaskReference);
                    inMemoryHistoryManager.remove(subtaskReference);
                }
            }
            epics.remove(id);
            inMemoryHistoryManager.remove(id);
            return id;
        } else if (subtasks.get(id) != null) {
            deleteTimeSlot(subtasks.get(id));
            int epicReference = subtasks.get(id).getEpicReference();
            subtasks.remove(id);
            inMemoryHistoryManager.remove(id);
            Epic epic = epics.get(epicReference);
            List<Integer> epicSubtaskReferences = epic.getSubtaskReferences();
            epicSubtaskReferences.remove(Integer.valueOf(id));
            epic.setSubtaskReferences(epicSubtaskReferences);
            epic.setStatus(epic.statusBySubtask(subtasks));
            if (epic.startTimeBySubtask(subtasks).isPresent()) {
                epic.setStartTime(epic.startTimeBySubtask(subtasks).get());
            } else {
                epic.setStartTime(null);
            }
            epic.setDuration(epic.durationBySubtask(subtasks));
            if (epic.endTimeBySubtask(subtasks).isPresent()) {
                epic.setEndTime(epic.endTimeBySubtask(subtasks).get());
            } else {
                epic.setEndTime(null);
            }
            epics.put(epic.getId(), epic);
            return id;
        } else {
            throw new IdPassingException("Не существует задачи с переданным ID: ", id);
        }
    }

    @Override
    public void clearTaskList() {
        timeSlotMap.clear();
        tasks.clear();
        subtasks.clear();
        epics.clear();
        id = 0;
        inMemoryHistoryManager.clearHistory();
    }

    @Override
    public List<Task> retrieveCompleteList() {
        List<Task> listOfTasks = new ArrayList<>(tasks.values());

        for (Epic epic : epics.values()) {
            listOfTasks.add(epic);
            List<Integer> subtaskReferences = epic.getSubtaskReferences();
            for (Integer subtaskReference : subtaskReferences) {
                listOfTasks.add(subtasks.get(subtaskReference));
            }
        }
        return listOfTasks;
    }

    @Override
    public List<Task> retrieveAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> retrieveAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> retrieveAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task retrieveTaskById(int id) {
        HashMap<Integer, Task> allTasks = new HashMap<>();
        allTasks.putAll(tasks);
        allTasks.putAll(epics);
        allTasks.putAll(subtasks);

        Optional<Task> taskOptimal = Optional.ofNullable(allTasks.get(id));
        if (taskOptimal.isPresent()) {
            inMemoryHistoryManager.add(taskOptimal.get());
        } else {
            throw new IdPassingException("Не существует задачи с переданным ID: ", id);
        }
        return taskOptimal.get();
    }

    @Override
    public List<Subtask> retrieveSubtasks(int idEpic) {
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>();
        Epic epic = epics.getOrDefault(idEpic, null);

        if (epic != null) {
            for (int id : subtasks.keySet()) {
                if (subtasks.get(id).getEpicReference() == idEpic) {
                    subtasksByEpic.add(subtasks.get(id));
                }
            }
        }
        return subtasksByEpic;
    }

    @Override
    public InMemoryHistoryManager getInMemoryHistoryManager() {
        return inMemoryHistoryManager;
    }

    @Override
    public String printAll() {
        List<Task> listOfTasks = retrieveCompleteList();
        StringBuilder string = new StringBuilder("\nПечать всех задач\n");

        for (Task task : listOfTasks) {
            string.append(task).append("\n");
        }
        return string.toString();
    }

    private int getId() {
        id++;
        return id;
    }

    protected LocalDateTime takeTimeSlot(Task task) {
        int followDuration = 0;
        LocalDateTime followTime;
        LocalDateTime startTime = task.getStartTime();
        if (task.getStartTime().isAfter(firstFreeSlot) || task.getStartTime().equals(firstFreeSlot)) {
            followTime = task.getStartTime();
            while (task.getDuration() > followDuration) {
                timeSlotMap.put(followTime, task);
                followTime = followTime.plusMinutes(15);
                followDuration += 15;
            }
            task.setEndTime(followTime);
            firstFreeSlot = followTime;
        } else {
            followTime = task.getStartTime();
            while (task.getDuration() > followDuration) {
                if (timeSlotMap.putIfAbsent(followTime, task) == null) {
                    if (followDuration == 0) {
                        startTime = followTime;
                    }
                    followDuration += 15;
                }
                followTime = followTime.plusMinutes(15);
            }
            task.setEndTime(followTime);
            if (firstFreeSlot.isBefore(followTime.plusMinutes(15))) {
                firstFreeSlot = followTime.plusMinutes(15);
            }
        }
        return startTime;
    }

    protected void setId(int newID) {
        id = newID;
    }

    protected int getIdWithoutIncrement() {
        return id;
    }

    protected void putTaskToMapFormFile(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void putTaskToMapFormFile(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    protected void putTaskToMapFormFile(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }


    private static LocalDateTime getCurrentTime() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.HOURS)
                .plusMinutes(15 * (LocalDateTime.now().getMinute() / 15) + 15);
    }

    private void deleteTimeSlot(Task task) {
        Map<LocalDateTime, Task> limitedMap = timeSlotMap.subMap(task.getStartTime(), task.getEndTime());
        for (Map.Entry<LocalDateTime, Task> entry : limitedMap.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue().equals(task)) {
                    timeSlotMap.put(entry.getKey(), null);
                }
            }
        }
    }

}
