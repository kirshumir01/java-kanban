package ru.yandex.practicum.services.filemanager;

import ru.yandex.practicum.models.*;
import ru.yandex.practicum.services.exceptions.ManagerSaveException;
import ru.yandex.practicum.services.taskmanager.InMemoryTaskManager;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    protected File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {
        File history = new File("resources/history.csv");
        FileBackedTaskManager fileManager = loadFromFile(history);

        System.out.println("Список задач из TaskManager:");
        System.out.println(fileManager.getListOfTasks() + "\n");
        System.out.println(fileManager.getListOfEpics() + "\n");
        System.out.println(fileManager.getListOfSubTasks() + "\n");

        System.out.println("Список задач из HistoryManager:");
        for (Task task : fileManager.getHistory()) {
            System.out.println(task.toString() + "\n");
        }
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic,startTime,duration,endTime\n");

            for (Task task : taskHashMap.values()) {
                writer.write(CSVTaskFormatter.toString(task) + "\n");
            }

            for (Epic epic : epicHashMap.values()) {
                writer.write(CSVTaskFormatter.toString(epic) + "\n");
            }

            for (SubTask subTask : subTaskHashMap.values()) {
                writer.write(CSVTaskFormatter.toString(subTask) + "\n");
            }

            writer.write("\n");
            writer.write(CSVTaskFormatter.historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи данных в файл.");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file);

        try (Reader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
            int maxNumberId = 0;

            br.readLine();

            while (br.ready()) {
                String line = br.readLine();
                if (!line.isEmpty()) {
                    Task task = CSVTaskFormatter.fromString(line);
                    if (task.getType().toString().equals("EPIC")) {
                        // добавить task в sortedTasks
                        fileManager.sortedTasks.add(task);
                        fileManager.epicHashMap.put(task.getId(), (Epic) task);
                    } else if (task.getType().toString().equals("SUBTASK")) {
                        // добавить task в sortedTasks
                        fileManager.sortedTasks.add(task);
                        fileManager.subTaskHashMap.put(task.getId(), (SubTask) task);
                        // получить epic подзадачи и записать id подзадачи в epic
                        Epic epic = fileManager.epicHashMap.get(((SubTask) task).getEpicId());
                        epic.getSubtasksId().add(task.getId());
                        fileManager.updateEpicTime(epic);
                    } else {
                        fileManager.taskHashMap.put(task.getId(), task);
                    }
                    if (maxNumberId <= task.getId()) {
                        maxNumberId = task.getId();
                        fileManager.defaultId = maxNumberId;
                    }
                }

                if (line.isBlank()) {
                    line = br.readLine();
                    List<Integer> viewedTaskId = CSVTaskFormatter.historyFromString(line);

                    for (Integer id : viewedTaskId) {
                        if (fileManager.taskHashMap.containsKey(id)) {
                            fileManager.historyManager.add(fileManager.taskHashMap.get(id));
                        } else if (fileManager.epicHashMap.containsKey(id)) {
                            fileManager.historyManager.add(fileManager.epicHashMap.get(id));
                        } else if (fileManager.subTaskHashMap.containsKey(id)) {
                            fileManager.historyManager.add(fileManager.subTaskHashMap.get(id));
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new ManagerSaveException("Файл не найден.");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения данных из файла.");
        }
        return fileManager;
    }

    private void setDefaultId(int id) {
        this.defaultId = id;
    }

    @Override
    public void addTask(Task newTask) {
        super.addTask(newTask);
        save();
    }

    @Override
    public void addEpic(Epic newEpic) {
        super.addEpic(newEpic);
        save();
    }

    @Override
    public void addSubTask(SubTask newSubTask) {
        super.addSubTask(newSubTask);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
    }

    @Override
    public void updateSubTask(SubTask newSubTask) {
        super.updateSubTask(newSubTask);
        save();
    }

    @Override
    public void checkEpicStatus(int id) {
        super.checkEpicStatus(id);
        save();
    }

    @Override
    public void removeAllTask() {
        super.removeAllTask();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }
}
