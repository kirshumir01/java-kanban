package ru.yandex.practicum.services.taskmanager;

import ru.yandex.practicum.models.*;
import ru.yandex.practicum.services.Managers;
import ru.yandex.practicum.services.history.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected int defaultId = 0;
    protected HashMap<Integer, Task> taskHashMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    protected HashMap<Integer, SubTask> subTaskHashMap = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void addTask(Task newTask) {
        newTask.setId(++defaultId);
        taskHashMap.put(newTask.getId(), newTask);
    }

    @Override
    public void addEpic(Epic newEpic) {
        newEpic.setId(++defaultId);
        epicHashMap.put(newEpic.getId(), newEpic);
    }

    @Override
    public void addSubTask(SubTask newSubTask) {
        newSubTask.setId(++defaultId);
        subTaskHashMap.put(newSubTask.getId(), newSubTask);
        int epicId = newSubTask.getEpicId();
        ArrayList<Integer> subTaskIdList = epicHashMap.get(epicId).getSubtasksId();
        subTaskIdList.add(newSubTask.getId());
        checkEpicStatus(epicId);
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
        for (int subtaskId : subTasksId) {
            subtasksByEpic.add(subTaskHashMap.get(subtaskId));
        }
        return subtasksByEpic;
    }

    @Override
    public void updateTask(Task newTask) {
        if (taskHashMap.containsKey(newTask.getId())) {
            taskHashMap.put(newTask.getId(), newTask);
        }
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (epicHashMap.containsKey(newEpic.getId())) {
            epicHashMap.put(newEpic.getId(), newEpic);
        }
    }

    @Override
    public void updateSubTask(SubTask newSubTask) {
        if (subTaskHashMap.containsKey(newSubTask.getId())) {
            subTaskHashMap.put(newSubTask.getId(), newSubTask);
            int epicId = subTaskHashMap.get(newSubTask.getId()).getEpicId();
            checkEpicStatus(epicId);
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
    public void removeAllTask() {
        for (Task task : taskHashMap.values()) {
            historyManager.remove(task.getId());
        }
        taskHashMap.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epicHashMap.values()) {
            int epicId = epic.getId();
            ArrayList<Integer> subTaskIdList = epicHashMap.get(epicId).getSubtasksId();
            for (Integer id : subTaskIdList) {
                subTaskHashMap.remove(id);
                historyManager.remove(id);
            }
            historyManager.remove(epic.getId());
        }
        epicHashMap.clear();
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epicHashMap.values()) {
            for (int subTaskId : epic.getSubtasksId()) {
                historyManager.remove(subTaskId);
            }
            epic.getSubtasksId().clear();
            checkEpicStatus(epic.getId());
        }
        subTaskHashMap.clear();
    }

    @Override
    public void removeTaskById(int id) {
        historyManager.remove(id);
        taskHashMap.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        // удалить подзадачи эпика до удаления самого эпика
        ArrayList<Integer> subTasksId = epicHashMap.get(id).getSubtasksId();
        for (Integer subTaskId : subTasksId) {
            // удалить subTask из истории просмотров
            historyManager.remove(subTaskId);
            subTaskHashMap.remove(subTaskId);
        }
        // удалить epic из истории просмотров
        historyManager.remove(id);
        epicHashMap.remove(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        int epicId = subTaskHashMap.get(id).getEpicId();
        ArrayList<Integer> subTasksId = epicHashMap.get(epicId).getSubtasksId();
        historyManager.remove(id);
        subTasksId.remove((Integer) id);
        // fix: записать в epic новый список subTasksId
        Epic epic = epicHashMap.get(epicId);
        epic.setSubTaskIdList(subTasksId);
        // fix: удалить subTask из subTaskHashMap
        subTaskHashMap.remove(id);
        checkEpicStatus(epicId);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
