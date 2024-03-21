package ru.yandex.practicum.services.taskmanager;

import ru.yandex.practicum.models.Epic;
import ru.yandex.practicum.models.SubTask;
import ru.yandex.practicum.models.Task;
import ru.yandex.practicum.services.history.HistoryManager;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;

public interface TaskManager {

    TreeSet<Task> getPrioritizedTasks();

    boolean isCrossingTasks(Task task);

    void addTask(Task newTask);

    void addEpic(Epic newEpic);

    void addSubTask(SubTask newSubTask);

    ArrayList<Task> getListOfTasks();

    ArrayList<Epic> getListOfEpics();

    ArrayList<SubTask> getListOfSubTasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    ArrayList<SubTask> getListOfSubTasksByEpic(int id);

    void updateTask(Task newTask);

    void updateEpic(Epic newEpic);

    void updateSubTask(SubTask newSubTask);

    void checkEpicStatus(int id);

    void removeAllTask();

    void removeAllEpics();

    void removeAllSubTasks();

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubTaskById(int id);

    List<Task> getHistory();

    HistoryManager getHistoryManager();
}
