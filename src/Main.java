import ru.yandex.practicum.services.Managers;
import ru.yandex.practicum.services.taskmanager.TaskManager;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;

public class Main {

    public static void main(String[] args) {
        // System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Спринт 6", "Изучить теорию");
        Task task2 = new Task("Спринт 7", "Изучить теорию");

        Epic epic3 = new Epic("ТЗ №6", "Решить задачу");
        Epic epic4 = new Epic("ТЗ №7", "Решить задачу");

        SubTask subTask5 = new SubTask("Шаг №1", "Написать код", 3);
        SubTask subTask6 = new SubTask("Шаг №2", "Отправить решение на проверку", 3);
        SubTask subTask7 = new SubTask("Шаг №3", "Внести правки в код", 3);

        // проверка работы истории просмотров задач
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.addEpic(epic3);
        taskManager.addEpic(epic4);

        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);
        taskManager.addSubTask(subTask7);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);

        taskManager.getEpicById(4);
        taskManager.getEpicById(3);
        taskManager.getEpicById(4);
        taskManager.getEpicById(3);

        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(6);
        taskManager.getSubTaskById(6);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(7);

        printAllTasks(taskManager);
    }

    // дополнительная реализация технического задания
    private static void printAllTasks(TaskManager manager) {

        System.out.println("\nИстория просмотров:");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            System.out.println((i + 1) + ". " + manager.getHistory().get(i));
        }
    }
}
