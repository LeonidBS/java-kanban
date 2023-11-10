# java-kanban
Проект выполнен в рамках обучения по программе "Java-разработчик" Yandex Practicum, тема "Java Core"

TaskManager, трекер задач позволяют эффективно организовать совместную работу над задачами.
Стек: Java8+
SDK Java 11
Проект без сборки
Ссылка на GitHub: https://github.com/LeonidBS/java-kanban.git

Для хранения списка задач используется csv файл (TaskStorageFile.csv). 
Поскольку в задании нет четкого понимания по определению стаусов эпика NEW и IN_PROGRESS, принимаем:
1.Cтатус NEW устанавливаестя , если все подзадачи имеют статус NEW,
2.Cогласно заданию статус DONE устанавливается, если все подзадачи имеют статус DONE, во всех других случаях статус эпика устанавливается , как IN_PROGRESS

Запись новых задач
Исключена запись подзадачи , если нет соответствующего эпика. 

Вывод общего списка
Список всех задач выводится по правилу, сначала простые задачи, потом эпики,
за каждым эпиком идет список подзадач. 

Удаление задач
При удалении эпика удаляются все его подзадачи.
При удалении всех подзадач статус эпика устанавливается , как NEW.

Реализовано Техническое задание №5. Методы вывода на консоль списков задач оставлены.
Добавен метод clearHistory() - отчистить историю

Реализовано Техническое задание №6.

Реализовано Техническое задание №7. Релализована проверка пересечений за O(1).
Допускается (изменение кода) выбор ширины периода планирования в будущее и прошлое от настоящего. 
Реализована сетка по 15 мин. Заполнение временных слотов происходит двумя способами:
1. В соответствии с заданием, если время старта не задано, то время страта выбирается автоматически, 
сразу за меткой времени завершения крайней задачи.
2. Если время страта передается с новой задачей , то если данное время свободно , начиная с этой метки
происходит заполнение каждого поля ссылкой на таск, если поле свободное. Таким образом задача 
или задачи могут выполнятся в свободных временных слотах между другими задачами . Если переданное 
время старта занято , то автоматически выбирается первое свободное время за занятой меткой. 
При этом исключений не выдается. Новое время страта передатется объекту задачи. 

Реализовано Техническое задание №8

