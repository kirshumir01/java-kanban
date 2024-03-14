import ru.yandex.practicum.services.Managers;
import ru.yandex.practicum.services.taskmanager.TaskManager;
import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

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

        printAllTasks(taskManager);
    }

    // дополнительная реализация технического задания
    private static void printAllTasks(TaskManager manager) {
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(1);
        manager.getTaskById(2);

        manager.getEpicById(4);
        manager.getEpicById(3);
        manager.getEpicById(4);
        manager.getEpicById(3);

        manager.getSubTaskById(5);
        manager.getSubTaskById(7);
        manager.getSubTaskById(6);
        manager.getSubTaskById(6);
        manager.getSubTaskById(5);
        manager.getSubTaskById(7);

        System.out.println("\nИстория просмотров:");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            System.out.println((i + 1) + ". " + manager.getHistory().get(i));
        }

        System.out.println("\nУдаление задачи с id = 1");
        manager.removeTaskById(1);

        System.out.println("\nИстория просмотров после удаления задачи с id = 1:");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            System.out.println((i + 1) + ". " + manager.getHistory().get(i));
        }

        System.out.println("\nУдаление эпика с id = 3, в котором сохранены 3 подзадачи");
        manager.removeEpicById(3);

        System.out.println("\nИстория просмотров после удаления эпика с id = 3:");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            System.out.println((i + 1) + ". " + manager.getHistory().get(i));
        }
    }
}
