package ru.yandex.practicum.kanban.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.kanban.model.Endpoint;
import ru.yandex.practicum.kanban.model.Epic;
import ru.yandex.practicum.kanban.model.Subtask;
import ru.yandex.practicum.kanban.model.Task;
import ru.yandex.practicum.kanban.service.HistoryManager;
import ru.yandex.practicum.kanban.service.Manager;
import ru.yandex.practicum.kanban.service.TaskManager;
import ru.yandex.practicum.kanban.service.exceptions.IdPassingException;
import ru.yandex.practicum.kanban.service.exceptions.SubtaskCreationException;
import ru.yandex.practicum.kanban.service.exceptions.TimeSlotException;
import ru.yandex.practicum.kanban.service.exceptions.UpdateTaskException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final HttpServer server;


    private final TaskManager taskManager;
    private final HistoryManager historyManager;

    public HttpTaskServer() throws IOException {
        this.taskManager = Manager.getFileBacked();
        this.historyManager = Manager.getDefaultHistory();
        gson = Manager.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handleCommon);
        server.createContext("/tasks/task", this::handleTask);
        server.createContext("/tasks/subtask", this::handleSubtask);
        server.createContext("/tasks/epic", this::handleEpic);
    }

    public void handleCommon(HttpExchange exchange) throws IOException {
        try {
            String query = exchange.getRequestURI().getQuery();
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                    exchange.getRequestMethod(), Optional.empty(), query);
            switch (endpoint) {
                case GET_HISTORY: {
//                    List<Task> listTasks = historyManager.getHistory();
//                    String responseString ="";
//                    for (Task listTask : listTasks) {
//                        responseString +=
//                    }
                    String responseString = gson.toJson(historyManager.getHistory());
                    writeResponse(exchange, responseString, 200);
                    break;
                }
                case GET_PRIORITIZED_TASKS: {
                    String responseString = gson.toJson(taskManager.getTimeSlotMap());
                    writeResponse(exchange, responseString, 200);
                    break;
                }
                case UNKNOWN:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        } catch (IdPassingException e) {
            writeResponse(exchange, e.getDetailedMessage(), 404);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    public void handleTask(HttpExchange exchange) throws IOException {
        try {
            Optional<Task> optionalTask = Optional.ofNullable(gson.fromJson(getBody(exchange), Task.class));
            String query = exchange.getRequestURI().getQuery();
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                    exchange.getRequestMethod(), optionalTask, query);
            switch (endpoint) {
                case GET_TASK_ID: {
                    String responseString = gson.toJson(taskManager
                            .retrieveTaskById(Integer.parseInt(query
                                    .replaceFirst("id=", ""))));
                    writeResponse(exchange, responseString, 200);
                    break;
                }
                case GET_TASKS: {
                    String responseString = gson.toJson(taskManager.retrieveAllTasks());
                    writeResponse(exchange, responseString, 200);
                    break;
                }
                case POST_CREATE_TASK: {
                    if (optionalTask.isPresent()) {
                        taskManager.createTask(optionalTask.get());
                        exchange.sendResponseHeaders(200, 0);
                        break;
                    }
                }
                case POST_UPDATE_TASK: {
                    if (optionalTask.isPresent()) {
                        taskManager.updateTask(optionalTask.get());
                        exchange.sendResponseHeaders(200, 0);
                        break;
                    }
                }
                case DELETE_TASK_ID: {
                    taskManager.deleteTask(Integer.parseInt(query
                            .replaceFirst("id=", "")));
                    exchange.sendResponseHeaders(200, 0);
                    break;
                }
                case DELETE_TASKS: {
                    taskManager.clearTaskList();
                    exchange.sendResponseHeaders(200, 0);
                    break;
                }
                case UNKNOWN:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        } catch (IdPassingException e) {
            writeResponse(exchange, e.getDetailedMessage(), 404);
        } catch (TimeSlotException e) {
            writeResponse(exchange, e.getDetailedMessage(), 416);
        } catch (UpdateTaskException e) {
            writeResponse(exchange, e.getDetailedMessage(), 406);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    public void handleSubtask(HttpExchange exchange) throws IOException {
        try {
            Optional<Task> optionalTask = Optional.ofNullable(gson.fromJson(getBody(exchange), Subtask.class));
            String query = exchange.getRequestURI().getQuery();
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                    exchange.getRequestMethod(), optionalTask, query);
            switch (endpoint) {
                case GET_TASK_ID: {
                    String responseString = gson.toJson(taskManager
                            .retrieveTaskById(Integer.parseInt(query
                                    .replaceFirst("id=", ""))));
                    writeResponse(exchange, responseString, 200);
                    break;
                }
                case GET_SUBTASKS: {
                    String responseString = gson.toJson(taskManager.retrieveAllSubtasks());
                    writeResponse(exchange, responseString, 200);
                    break;
                }
                case POST_CREATE_SUBTASK: {
                    if (optionalTask.isPresent()) {
                        taskManager.createTask((Subtask) optionalTask.get());
                        exchange.sendResponseHeaders(200, 0);
                        break;
                    }
                }
                case POST_UPDATE_SUBTASK: {
                    if (optionalTask.isPresent()) {
                        taskManager.updateTask((Subtask) optionalTask.get());
                        exchange.sendResponseHeaders(200, 0);
                        break;
                    }
                }
                case GET_EPIC_SUBTASKS: {
                    String responseString = gson.toJson(taskManager
                            .retrieveSubtasks(Integer.parseInt(query
                                    .replaceFirst("id=", ""))));
                    writeResponse(exchange, responseString, 200);
                    break;
                }
                case DELETE_TASK_ID: {
                    taskManager.deleteTask(Integer.parseInt(query
                            .replaceFirst("id=", "")));
                    exchange.sendResponseHeaders(200, 0);
                    break;
                }
                case DELETE_TASKS: {
                    taskManager.clearTaskList();
                    exchange.sendResponseHeaders(200, 0);
                    break;
                }
                case UNKNOWN:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        } catch (IdPassingException e) {
            writeResponse(exchange, e.getDetailedMessage(), 404);
        } catch (SubtaskCreationException e) {
            writeResponse(exchange, e.getDetailedMessage(), 404);
        } catch (TimeSlotException e) {
            writeResponse(exchange, e.getDetailedMessage(), 416);
        } catch (UpdateTaskException e) {
            writeResponse(exchange, e.getDetailedMessage(), 406);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    public void handleEpic(HttpExchange exchange) throws IOException {
        try {
            Optional<Task> optionalTask = Optional.ofNullable(gson.fromJson(getBody(exchange), Epic.class));
            String query = exchange.getRequestURI().getQuery();
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                    exchange.getRequestMethod(), optionalTask, query);
            switch (endpoint) {
                case GET_TASK_ID: {
                    String responseString = gson.toJson(taskManager
                            .retrieveTaskById(Integer.parseInt(query
                                    .replaceFirst("id=", ""))));
                    writeResponse(exchange, responseString, 200);
                    break;
                }
                case GET_EPICS: {
                    String responseString = gson.toJson(taskManager.retrieveAllEpics());
                    writeResponse(exchange, responseString, 200);
                    break;
                }
                case POST_CREATE_EPIC: {
                    if (optionalTask.isPresent()) {
                        taskManager.createTask((Epic) optionalTask.get());
                        exchange.sendResponseHeaders(200, 0);
                        break;
                    }
                }
                case POST_UPDATE_EPIC: {
                    if (optionalTask.isPresent()) {
                        taskManager.updateTask((Epic) optionalTask.get());
                        exchange.sendResponseHeaders(200, 0);
                        break;
                    }
                }
                case DELETE_TASK_ID: {
                    taskManager.deleteTask(Integer.parseInt(query
                            .replaceFirst("id=", "")));
                    exchange.sendResponseHeaders(200, 0);
                    break;
                }
                case DELETE_TASKS: {
                    taskManager.clearTaskList();
                    exchange.sendResponseHeaders(200, 0);
                    break;
                }
                case UNKNOWN:
                    writeResponse(exchange, "Такого эндпоинта не существует", 404);
            }
        } catch (IdPassingException e) {
            writeResponse(exchange, e.getDetailedMessage(), 404);
        } catch (SubtaskCreationException e) {
            writeResponse(exchange, e.getDetailedMessage(), 404);
        } catch (TimeSlotException e) {
            writeResponse(exchange, e.getDetailedMessage(), 416);
        } catch (UpdateTaskException e) {
            writeResponse(exchange, e.getDetailedMessage(), 406);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod,
                                 Optional<Task> optionalTask, String query) {

        switch (requestMethod) {
            case "GET": {
                if (query == null) {
                    if (Pattern.matches("^/tasks/task/?$", requestPath)) {
                        return Endpoint.GET_TASKS;
                    }
                    if (Pattern.matches("^/tasks/subtask/?$", requestPath)) {
                        return Endpoint.GET_SUBTASKS;
                    }
                    if (Pattern.matches("^/tasks/epic/?$", requestPath)) {
                        return Endpoint.GET_EPICS;
                    }
                    if (Pattern.matches("^/tasks/history/?$", requestPath)) {
                        return Endpoint.GET_HISTORY;
                    }
                    if (Pattern.matches("^/tasks/?$", requestPath)) {
                        return Endpoint.GET_PRIORITIZED_TASKS;
                    }
                } else {
                    if (Pattern.matches("^(/tasks/)(task|subtask|epic)/?$", requestPath)
                            && Pattern.matches("^id=\\d+$", query)) {
                        return Endpoint.GET_TASK_ID;
                    }
                    if (Pattern.matches("^/tasks/subtask/epic/?$", requestPath)
                            && Pattern.matches("^id=\\d+$", query)) {
                        return Endpoint.GET_EPIC_SUBTASKS;
                    }
                }
                return Endpoint.UNKNOWN;
            }
            case "POST": {
                if (Pattern.matches("^/tasks/task/?$", requestPath) && optionalTask.isPresent()) {
                    if (optionalTask.get().getId() == 0) {
                        return Endpoint.POST_CREATE_TASK;
                    } else if (optionalTask.get().getId() > 0) {
                        return Endpoint.POST_UPDATE_TASK;
                    }
                }

                if (Pattern.matches("^/tasks/epic/?$", requestPath) && optionalTask.isPresent()) {
                    if (optionalTask.get().getId() == 0) {
                        return Endpoint.POST_CREATE_EPIC;
                    } else if (optionalTask.get().getId() > 0) {
                        return Endpoint.POST_UPDATE_EPIC;
                    }
                }

                if (Pattern.matches("^/tasks/subtask/?$", requestPath) && optionalTask.isPresent()) {
                    if (optionalTask.get().getId() == 0) {
                        return Endpoint.POST_CREATE_SUBTASK;
                    } else if (optionalTask.get().getId() != 0) {
                        return Endpoint.POST_UPDATE_SUBTASK;
                    }
                }
                return Endpoint.UNKNOWN;
            }

            case "DELETE": {
                if (query == null) {
                    if (Pattern.matches("^(/tasks/)(task|subtask|epic)/?$", requestPath)) {
                        return Endpoint.DELETE_TASKS;
                    }
                } else {
                    if (Pattern.matches("^(/tasks/)(task|subtask|epic)/?$", requestPath)
                            && Pattern.matches("^id=\\d+$", query)) {
                        return Endpoint.DELETE_TASK_ID;
                    }
                }
                return Endpoint.UNKNOWN;
            }
            default:
                return Endpoint.UNKNOWN;
        }
    }


    private void writeResponse(HttpExchange exchange, String responseString,
                               int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private String getBody(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);

    }

    public void start() {
        System.out.println("Запускаем сервер на порту c логикой API " + PORT);
        server.start();
    }

    public void stop() {
        System.out.println("Сервер на порту " + PORT + " c логикой API остановлен ");
        server.stop(1);
    }

}

