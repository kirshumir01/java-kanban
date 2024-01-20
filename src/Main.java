public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Спринт 4", "Изучить теорию");
        Task task2 = new Task("Спринт 5", "Изучить теорию");

        Epic epic3 = new Epic("ТЗ №4", "Решить задачу");
        Epic epic4 = new Epic("ТЗ №5", "Решить задачу");

        SubTask subTask5 = new SubTask("Шаг №1", "Написать код", 3);
        SubTask subTask6 = new SubTask("Шаг №2", "Отправить решение на проверку", 3);

        SubTask subTask7 = new SubTask("Щаг №1", "Разобраться в условиях задачи", 4);

        // проверка работы методов
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.addEpic(epic3);
        taskManager.addEpic(epic4);

        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);
        taskManager.addSubTask(subTask7);

        System.out.println("Список задач, эпиков, подзадач:");
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubTasks());

        System.out.println(" ");
        System.out.println("Задача id=1, эпик id=3, подзадача id=5:");
        System.out.println(taskManager.getTaskById(1));
        System.out.println(taskManager.getEpicById(3));
        System.out.println(taskManager.getSubTaskById(5));

        System.out.println(" ");
        System.out.println("Статусы задачи id=1, эпика id=3, подзадач id=5 и id=6:");
        System.out.println(task1.getStatus());
        System.out.println(epic3.getStatus());
        System.out.println(subTask5.getStatus());
        System.out.println(subTask6.getStatus());

        System.out.println(" ");
        System.out.println("Изменение статусов на DONE задачи id=1, подзадач id=5 и id=6:");
        task1.setStatus(TaskStatus.DONE);
        subTask5.setStatus(TaskStatus.DONE);
        subTask6.setStatus(TaskStatus.DONE);
        taskManager.checkEpicStatus(3);

        System.out.println(" ");
        System.out.println("Новые статусы задачи id=1, эпика id=3, подзадач id=5 и id=6:");
        System.out.println(task1.getStatus());
        System.out.println(epic3.getStatus());
        System.out.println(subTask5.getStatus());
        System.out.println(subTask6.getStatus());

        System.out.println(" ");
        System.out.println("Удаление задачи id=2, эпика id=4:");
        taskManager.removeTaskById(2);
        taskManager.removeEpicById(4);

        System.out.println(" ");
        System.out.println("Список задач, эпиков, подзадач:");
        System.out.println(taskManager.getListOfTasks());
        System.out.println(taskManager.getListOfEpics());
        System.out.println(taskManager.getListOfSubTasks());
    }
}
