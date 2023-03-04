package ru.yandex.practicum.kanban.http;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private final URL url;
    private String apiToken;

    public URL getUrl() {
        return url;
    }

    public String getApiToken() {
        return apiToken;
    }

    public KVTaskClient(URL url) {
        this.client = HttpClient.newHttpClient();
        this.url = url;
        apiToken = null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString() + "/register"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                if (response.body().length() == 0) {
                    System.out.println("Ответ от сервера не соответствует ожидаемому");
                    return;
                }
                apiToken = response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            throw new RuntimeException(e);
        }
    }

    public void put (String key, String json) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString() + "/save/?API_TOKEN=" + key))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Состояние менеджера задач сохранено");
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            throw new RuntimeException(e);
        }
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.toString() + "/load/?API_TOKEN=" + key))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                if (response.body().length() == 0) {
                    System.out.println("Ответ от сервера не соответствует ожидаемому");
                }
                System.out.println(response.body());
                return response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
            throw new RuntimeException(e);
        }
        return "";
    }

    }


