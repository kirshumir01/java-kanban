package ru.yandex.practicum.managers;

import ru.yandex.practicum.model.Task;
import ru.yandex.practicum.model.SubTask;
import ru.yandex.practicum.model.Epic;
import ru.yandex.practicum.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    public int defaultId = 0;
    private final HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskHashMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

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
    public void removeAllTask() {
        taskHashMap.clear();
    }

    @Override
    public void removeAllEpics() {
        // удалить идентификаторы подзадач, обновить статус эпика, удалить все подзадачи
        for (Epic epic : epicHashMap.values()) {
            int epicId = epic.getId();
            ArrayList<Integer> subTaskIdList = epicHashMap.get(epicId).getSubtasksId();
            // удалить все подзадачи эпика по идентификаторам
            for (Integer id : subTaskIdList) {
                subTaskHashMap.remove(id);
            }
        }
        // удалить все эпики
        epicHashMap.clear();
    }

    @Override
    public void removeAllSubTasks() {
        // удалить идентификаторы подзадач из эпика, обновить статус эпика
        for (Epic epic : epicHashMap.values()) {
            epic.getSubtasksId().clear();
            checkEpicStatus(epic.getId());
        }
        // удалить все подзадачи
        subTaskHashMap.clear();
    }

    @Override
    public Task getTaskById(int id) {
        // добавить task в список истории просмотра history
        if (taskHashMap.get(id) != null) {
            historyManager.addHistory(taskHashMap.get(id));
        }
        return taskHashMap.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        // добавить epic в список истории просмотра history
        if (epicHashMap.get(id) != null) {
            historyManager.addHistory(epicHashMap.get(id));
        }
        return epicHashMap.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        // добавить subTask в список истории просмотра history
        if (subTaskHashMap.get(id) != null) {
            historyManager.addHistory(subTaskHashMap.get(id));
        }
        return subTaskHashMap.get(id);
    }

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
        // получить идентификатор эпика
        // создать список идентификаторов подзадач соответствующего эпика
        // сохранить в список значение идентификатора новой подзадачи
        int epicId = newSubTask.getEpicId();
        ArrayList<Integer> subTaskIdList = epicHashMap.get(epicId).getSubtasksId();
        subTaskIdList.add(newSubTask.getId());
        checkEpicStatus(epicId);
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
            // получить идентификатор эпика, чтобы актуализировать его статус
            int epicId = subTaskHashMap.get(newSubTask.getId()).getEpicId();
            checkEpicStatus(epicId);
        }
    }

    @Override
    public void removeTaskById(int id) {
        taskHashMap.remove(id);
    }

    @Override
    public void removeEpicById(int id) {
        // удалить подзадачи эпика до удаления самого эпика
        ArrayList<Integer> subTasksId = epicHashMap.get(id).getSubtasksId();
        for (Integer subTaskId : subTasksId) {
            subTaskHashMap.remove(subTaskId);
        }
        epicHashMap.remove(id);
    }

    @Override
    public void removeSubTaskById(int id) {
        // получить идентификатор эпика, чтобы после удаления подзадач обновить его статус
        int epicId = subTaskHashMap.get(id).getEpicId();
        // удалить идентификаторы подзадач из эпика
        ArrayList<Integer> subTasksId = epicHashMap.get(epicId).getSubtasksId();
        subTasksId.remove((Integer) id);
        // проверить статус эпика
        checkEpicStatus(epicId);
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
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }
}
