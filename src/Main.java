import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.TaskManager;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Спринт 4", "Изучить теорию");
        Task task2 = new Task("Спринт 5", "Изучить теорию");

        Epic epic3 = new Epic("ТЗ №4", "Решить задачу");
        Epic epic4 = new Epic("ТЗ №5", "Решить задачу");

        SubTask subTask5 = new SubTask("Шаг №1", "Написать код", 3);
        SubTask subTask6 = new SubTask("Шаг №2", "Отправить решение на проверку", 3);

        SubTask subTask7 = new SubTask("Шаг №1", "Разобраться в условиях задачи", 4);
        SubTask subTask8 = new SubTask("Шаг №2", "Написать код", 4);


        // проверка работы истории просмотров задач
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.addEpic(epic3);
        taskManager.addEpic(epic4);

        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);
        taskManager.addSubTask(subTask7);
        taskManager.addSubTask(subTask8);

        printAllTasks(taskManager);
    }
    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getListOfTasks()) {
            manager.getTaskById(task.getId());
            System.out.println(task);
        }
        System.out.println("\nЭпики:");
        for (Task epic : manager.getListOfEpics()) {
            manager.getEpicById(epic.getId());
            System.out.println(epic);

            for (Task subTask : manager.getListOfSubTasksByEpic(epic.getId())) {
                manager.getSubTaskById(subTask.getId());
                System.out.println("--> " + subTask);
            }
        }
        System.out.println("\nПодзадачи:");
        for (Task subtask : manager.getListOfSubTasks()) {
            manager.getSubTaskById(subtask.getId());
            System.out.println(subtask);
        }

        System.out.println("\nИстория просмотров:");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            System.out.println((i + 1) + ". " + manager.getHistory().get(i));
        }
    }
}
