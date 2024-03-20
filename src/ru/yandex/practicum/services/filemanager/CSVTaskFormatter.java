package ru.yandex.practicum.services.filemanager;

import ru.yandex.practicum.models.*;
import ru.yandex.practicum.services.history.HistoryManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CSVTaskFormatter {

    // конвертировать данные task в текстовую строку
    public static String toString(Task task) {
        String type;

        if (task.getType().toString().equals("SUBTASK")) {
            type = TaskType.SUBTASK.toString();
        } else if (task.getType().toString().equals("EPIC")) {
            type = TaskType.EPIC.toString();
        } else {
            type = TaskType.TASK.toString();
        }

        // конвертировать task и epic в строку в формате:
        // id,type,name,status,description
        String line = task.getId() + "," + type + "," + task.getTitle() + "," + task.getStatus() + "," +
                task.getDescription();

        // конвертировать subtask в строку в формате:
        // id,type,name,status,description,epic
        if (task.getType().toString().equals("SUBTASK")) {
            line = line + "," + ((SubTask) task).getEpicId();
        }
        return line;
    }

    // конвертировать данные task из текстовой строки
    public static Task fromString(String value) {
        String[] values = value.split(",");
        Task task = null;

        if (values[1].equals(TaskType.TASK.toString())) {
            task = new Task(values[2], values[4]);
            task.setId(Integer.parseInt(values[0]));
            task.setStatus(TaskStatus.valueOf(values[3]));
        } else if (values[1].equals((TaskType.EPIC.toString()))) {
            task = new Epic(values[2], values[4]);
            task.setId(Integer.parseInt(values[0]));
            task.setStatus(TaskStatus.valueOf(values[3]));
        } else if (values[1].equals((TaskType.SUBTASK.toString()))) {
            task = new SubTask(values[2], values[4], Integer.parseInt(values[5]));
            task.setId(Integer.parseInt(values[0]));
            task.setStatus(TaskStatus.valueOf(values[3]));
        }
        return task;
    }

    // конвертировать id просмотренных task в текстовую строку
    public static String historyToString(HistoryManager manager) {
        StringBuilder viewedTaskId = new StringBuilder();

        for (Task task : manager.getHistory()) {
            viewedTaskId.append(task.getId()).append(",");
        }
        return viewedTaskId.toString();
    }

    // распарсить id просмотренных task из истории
    public static List<Integer> historyFromString(String value) {
        if (value != null) {
            String[] values = value.split(",");
            List<Integer> viewedTaskId = new ArrayList<>();

            for (String s : values) {
                viewedTaskId.add(Integer.parseInt(s));
            }
            return viewedTaskId;
        } else {
            return Collections.emptyList();
        }
    }
}
