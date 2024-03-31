package ru.yandex.practicum.services.taskmanager;

import ru.yandex.practicum.models.*;
import ru.yandex.practicum.services.Managers;
import ru.yandex.practicum.services.exceptions.ManagerSaveException;
import ru.yandex.practicum.services.history.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int defaultId = 0;
    protected HashMap<Integer, Task> taskHashMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    protected HashMap<Integer, SubTask> subTaskHashMap = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Comparator<Task> comparator = (task1, task2) -> {
        if (task1.getStartTime() == null) {
            return 1;
        } else if (task2.getStartTime() == null) {
            return -1;
        }
        return task1.getStartTime().compareTo(task2.getStartTime());
    };
    protected TreeSet<Task> sortedTasks = new TreeSet<>(comparator);

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return sortedTasks;
    }

    @Override
    public boolean isCrossingTasks(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null) {
            return !sortedTasks
                    .stream()
                    .filter(t -> t.getEndTime() != null && !t.equals(task) && (t.getId() != task.getId()))
                    .allMatch(t -> (
                            (task.getStartTime().isBefore(t.getStartTime())
                                    && task.getEndTime().isBefore(t.getStartTime())) ||
                            (task.getStartTime().isAfter(t.getEndTime())
                                    && task.getEndTime().isAfter(t.getEndTime())) ||
                            (task.getEndTime().equals(t.getStartTime()) || task.getStartTime().equals(t.getEndTime()))));
        } else if (task.getStartTime() == null && task.getDuration() == null) {
            return false;
        }
        return true;
    }

    protected void updateEpicTime(Epic epic) {
        Duration epicDuration;
        LocalDateTime earlyStartTime = LocalDateTime.MAX;
        LocalDateTime lateEndTime = LocalDateTime.MIN;

        if (!epic.getSubtasksId().isEmpty()) {
            for (Integer id : epic.getSubtasksId()) {
                SubTask subTask = subTaskHashMap.get(id);

                if (subTask.getStartTime() != null && subTask.getDuration() != null) {
                    if (earlyStartTime.isAfter(subTask.getStartTime())) {
                        earlyStartTime = subTask.getStartTime();
                    }

                    if (lateEndTime.isBefore(subTask.getEndTime())) {
                        lateEndTime = subTask.getEndTime();
                    }
                    epicDuration = Duration.between(earlyStartTime, lateEndTime);

                    epic.setStartTime(earlyStartTime);
                    epic.setEndTime(lateEndTime);
                    epic.setDuration(epicDuration);
                }
            }
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
        }
    }

    @Override
    public void addTask(Task newTask) {
        newTask.setId(++defaultId);
        // если по времени задачи пересекаются - обнулить время добавляемой задачи
        if (isCrossingTasks(newTask)) {
            throw new ManagerSaveException("Задача не сохранена. Время выполнения задачи пересекается" +
                    " со временем существующих задач");
        } else {
            sortedTasks.add(newTask);
        }
        newTask.setType();
        taskHashMap.put(newTask.getId(), newTask);
    }

    @Override
    public void addEpic(Epic newEpic) {
        newEpic.setId(++defaultId);
        newEpic.setType();
        updateEpicTime(newEpic);
        epicHashMap.put(newEpic.getId(), newEpic);
    }

    @Override
    public void addSubTask(SubTask newSubTask) {
        // если по времени задачи пересекаются - обнулить время добавляемой задачи
        newSubTask.setId(++defaultId);
        if (isCrossingTasks(newSubTask)) {
            throw new ManagerSaveException("Подзадача не сохранена. Время выполнения подзадачи пересекается" +
                    " со временем существующих задач");
        } else {
            sortedTasks.add(newSubTask);
        }

        if (epicHashMap.get(newSubTask.getEpicId()) != null && epicHashMap.containsKey(newSubTask.getEpicId())) {
            newSubTask.setType();
            subTaskHashMap.put(newSubTask.getId(), newSubTask);
            ArrayList<Integer> subTaskIdList = epicHashMap.get(newSubTask.getEpicId()).getSubtasksId();
            subTaskIdList.add(newSubTask.getId());
            checkEpicStatus(newSubTask.getEpicId());
            updateEpicTime(epicHashMap.get(newSubTask.getEpicId()));
        }
    }

    @Override
    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(taskHashMap.values());
    }

    @Override
    public ArrayList<Epic> getListOfEpics() {
        return new ArrayList<>(epicHashMap.values());
    }

    @Override
    public ArrayList<SubTask> getListOfSubTasks() {
         return new ArrayList<>(subTaskHashMap.values());
    }

    @Override
    public Task getTaskById(int id) {
        if (taskHashMap.get(id) != null) {
            historyManager.add(taskHashMap.get(id));
        }
        return taskHashMap.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (epicHashMap.get(id) != null) {
            historyManager.add(epicHashMap.get(id));
        }
        return epicHashMap.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (subTaskHashMap.get(id) != null) {
            historyManager.add(subTaskHashMap.get(id));
        }
        return subTaskHashMap.get(id);
    }

    @Override
    public ArrayList<SubTask> getListOfSubTasksByEpic(int id) {
        ArrayList<Integer> subTasksId = epicHashMap.get(id).getSubtasksId();
        ArrayList<SubTask> subtasksByEpic = new ArrayList<>();
        // преобразовать for-each в stream
        subTasksId.stream().forEach(i -> subtasksByEpic.add(subTaskHashMap.get(i)));
        return subtasksByEpic;
    }

    @Override
    public void updateTask(Task newTask) {
        if (taskHashMap.containsKey(newTask.getId())) {
            if (isCrossingTasks(newTask)) {
                throw new ManagerSaveException("Задача не сохранена. Время выполнения задачи пересекается" +
                        " со временем существующих задач");
            }

            newTask.setType();
            taskHashMap.put(newTask.getId(), newTask);

            // создать новый sortedList без старой Task и добавить newTask
            sortedTasks = sortedTasks.stream()
                    .filter(t -> t.getId() != newTask.getId())
                    .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));
            sortedTasks.add(newTask);
        }
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (epicHashMap.containsKey(newEpic.getId())) {
            newEpic.setType();
            updateEpicTime(newEpic);
            epicHashMap.put(newEpic.getId(), newEpic);
        }
    }

    @Override
    public void updateSubTask(SubTask newSubTask) {
        if (subTaskHashMap.containsKey(newSubTask.getId())) {
            if (isCrossingTasks(newSubTask)) {
                throw new ManagerSaveException("Подзадача не сохранена. Время выполнения подзадачи пересекается" +
                        " со временем существующих задач");
            }

            newSubTask.setType();
            subTaskHashMap.put(newSubTask.getId(), newSubTask);

            if (epicHashMap.get(newSubTask.getEpicId()) != null) {
                checkEpicStatus(newSubTask.getEpicId());
                updateEpicTime(epicHashMap.get(newSubTask.getEpicId()));
            }

            // создать новый sortedList без старой SubTask и добавить newSubTask
            sortedTasks = sortedTasks.stream()
                    .filter(t -> t.getId() != newSubTask.getId())
                    .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));
            sortedTasks.add(newSubTask);
        }
    }

    @Override
    public void checkEpicStatus(int id) {
        int countNewTasks = 0;
        int countDoneTasks = 0;

        ArrayList<Integer> subTasksId = epicHashMap.get(id).getSubtasksId();

        for (Integer subTaskId : subTasksId) {
            if (subTaskHashMap.get(subTaskId).getStatus().equals(TaskStatus.NEW)) {
                countNewTasks++;
            } else if (subTaskHashMap.get(subTaskId).getStatus().equals(TaskStatus.DONE)) {
                countDoneTasks++;
            }
        }

        if (subTasksId.size() == countDoneTasks) {
            epicHashMap.get(id).setStatus(TaskStatus.DONE);
        } else if (subTasksId.size() == countNewTasks || subTasksId.isEmpty()) {
            epicHashMap.get(id).setStatus(TaskStatus.NEW);
        } else {
            epicHashMap.get(id).setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void removeAllTasks() {
        // преобразовать for-each в stream
        taskHashMap.values().stream().forEach(task -> {
            historyManager.remove(task.getId());
            sortedTasks.remove(task);
        });
        taskHashMap.clear();
    }

    @Override
    public void removeAllEpics() {
        // преобразовать for-each в stream
        epicHashMap.values()
                .stream()
                .forEach(epic -> {
                    epicHashMap.get(epic.getId()).getSubtasksId()
                            .stream()
                            .forEach(id -> {
                                historyManager.remove(id);
                                sortedTasks.remove(subTaskHashMap.get(id));
                                subTaskHashMap.remove(id);
                            });
                    historyManager.remove(epic.getId());

                });
        epicHashMap.clear();
    }

    @Override
    public void removeAllSubTasks() {
        // преобразовать for-each в stream
        epicHashMap.values()
                .stream()
                .forEach(epic -> {
                    epic.getSubtasksId()
                            .forEach(id -> {
                                historyManager.remove(id);
                                sortedTasks.remove(subTaskHashMap.get(id));
                            });
                    epic.getSubtasksId().clear();
                    checkEpicStatus(epic.getId());
                });
        subTaskHashMap.clear();

        // обновить время Epic'ов
        epicHashMap.values().stream().forEach(epic -> updateEpicTime(epic));
    }

    @Override
    public void removeTaskById(int id) {
        // удалить Task из SortedList
        sortedTasks.remove(taskHashMap.get(id));
        historyManager.remove(id);
        taskHashMap.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        ArrayList<Integer> subTasksId = epicHashMap.get(id).getSubtasksId();
        // преобразовать for-each в stream
        subTasksId
                .stream()
                .filter(i -> subTaskHashMap.containsKey(i))
                .forEach(i -> {
                    // удалить SubTask Epic'а из sortedList
                    sortedTasks.remove(subTaskHashMap.get(i));
                    historyManager.remove(i);
                    subTaskHashMap.remove(i);
                });
        historyManager.remove(id);
        epicHashMap.remove(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        int epicId = subTaskHashMap.get(id).getEpicId();
        epicHashMap.get(epicId).getSubtasksId().remove((Integer) id);
        // создать новый sortedList без удаленной SubTask
        sortedTasks.remove(subTaskHashMap.get(id));
        historyManager.remove(id);
        subTaskHashMap.remove(id);
        checkEpicStatus(epicId);
        // обновить время Epic'а
        updateEpicTime(epicHashMap.get(epicId));
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}
