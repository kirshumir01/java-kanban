package ru.yandex.practicum.managers;

import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Task;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getListOfTasks();

    ArrayList<Epic> getListOfEpics();

    ArrayList<SubTask> getListOfSubTasks();

    void removeAllTask();

    void removeAllEpics();

    void removeAllSubTasks();

    Task getTaskById(int id);

    Epic getEpicById (int id);

    SubTask getSubTaskById (int id);

    void addTask(Task newTask);

    void addEpic(Epic newEpic);

    void addSubTask (SubTask newSubTask);

    void updateTask (Task newTask);

    void updateEpic(Epic newEpic);

    void updateSubTask(SubTask newSubTask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubTaskById(int id);

    ArrayList<SubTask> getListOfSubTasksByEpic (int id);

    void checkEpicStatus(int id);

    ArrayList<Task> getHistory();
}
