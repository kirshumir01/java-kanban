package ru.yandex.practicum.services.filemanager;

import ru.yandex.practicum.models.*;
import ru.yandex.practicum.services.history.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CSVTaskFormatter {

    public static String toString(Task task) {
        String type;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

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

        // в случае, если задано время и продолжительность task - конвертировать данные в строку
        if (task.getStartTime() != null && task.getDuration() != null) {
            if (task.getType().toString().equals("SUBTASK")) {
                line = line + "," + task.getStartTime().format(formatter) + "," + task.getDuration().toMinutes() + "," +
                        task.getEndTime().format(formatter);
            } else {
                line = line + ",null," + task.getStartTime().format(formatter) + "," + task.getDuration().toMinutes() + "," +
                        task.getEndTime().format(formatter);
            }
        }

        return line;
    }

    public static Task fromString(String value) {
        String[] values = value.split(",");
        Task task = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        if (values[1].equals(TaskType.TASK.toString())) {
            if (values.length == 9) {
                // чтение из разбитой строки формата:
                // [id], [title], [status], [description], [epicId], [dd.MM.yyyy HH:mm], [duration]
                task = new Task(values[2], values[4], LocalDateTime.parse(values[6], formatter),
                        Duration.ofMinutes(Long.parseLong(values[7])));
            } else {
                task = new Task(values[2], values[4]);
            }
            task.setId(Integer.parseInt(values[0]));
            task.setStatus(TaskStatus.valueOf(values[3]));
            task.setType();
        } else if (values[1].equals((TaskType.EPIC.toString()))) {
            // чтение остается без изменений - время эпика расчетное и зависит от subTask
            task = new Epic(values[2], values[4]);
            task.setId(Integer.parseInt(values[0]));
            task.setStatus(TaskStatus.valueOf(values[3]));
            task.setType();
        } else if (values[1].equals((TaskType.SUBTASK.toString()))) {
            if (values.length == 9) {
                // чтение из разбитой строки формата:
                // [id], [title], [status], [description], [epicId], [dd.MM.yyyy HH:mm], [duration]
                task = new SubTask(values[2], values[4], Integer.parseInt(values[5]),
                        LocalDateTime.parse(values[6], formatter), Duration.ofMinutes(Long.parseLong(values[7])));
            } else {
                task = new SubTask(values[2], values[4], Integer.parseInt(values[5]));
            }
            task.setId(Integer.parseInt(values[0]));
            task.setStatus(TaskStatus.valueOf(values[3]));
            task.setType();
        }
        return task;
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder viewedTaskId = new StringBuilder();

        for (Task task : manager.getHistory()) {
            viewedTaskId.append(task.getId()).append(",");
        }
        return viewedTaskId.toString();
    }

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
