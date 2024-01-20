import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    public int defaultId = 0;
    private final HashMap<Integer, Task> taskHashMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskHashMap = new HashMap<>();

    public ArrayList<Task> getListOfTasks() {
        return new ArrayList<>(taskHashMap.values());

    }

    public ArrayList<Epic> getListOfEpics() {
        // проверить статусы эпиков до вывода списка
        for (Epic epic : epicHashMap.values()) {
            checkEpicStatus(epic.getId());
        }
        return new ArrayList<>(epicHashMap.values());
    }

    public ArrayList<SubTask> getListOfSubTasks() {
        return new ArrayList<>(subTaskHashMap.values());
    }

    public void removeAllTask() {
        taskHashMap.clear();
    }

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

    public void removeAllSubTasks() {
        // удалить идентификаторы подзадач из эпика, обновить статус эпика
        for (Epic epic : epicHashMap.values()) {
            epic.getSubtasksId().clear();
            checkEpicStatus(epic.getId());
        }
        // удалить все подзадачи
        subTaskHashMap.clear();
    }

    public Task getTaskById(int id) {
        return taskHashMap.get(id);
    }

    public Epic getEpicById (int id) {
        // проверить статус эпика до вывода
        checkEpicStatus(id);
        return epicHashMap.get(id);
    }

    public SubTask getSubTaskById (int id) {
        return subTaskHashMap.get(id);
    }

    public void addTask(Task newTask) {
        newTask.setId(++defaultId);
        taskHashMap.put(newTask.getId(), newTask);
    }

    public void addEpic(Epic newEpic) {
        newEpic.setId(++defaultId);
        epicHashMap.put(newEpic.getId(), newEpic);
    }

    public void addSubTask (SubTask newSubTask) {
        newSubTask.setId(++defaultId);
        newSubTask.setStatus(TaskStatus.NEW);
        subTaskHashMap.put(newSubTask.getId(), newSubTask);
        // получить идентификатор эпика
        // создать список идентификаторов подзадач соответствующего эпика
        // сохранить в список значение идентификатора новой подзадачи
        int epicId = newSubTask.getEpicId();
        ArrayList<Integer> subTaskIdList = epicHashMap.get(epicId).getSubtasksId();
        subTaskIdList.add(newSubTask.getId());
        checkEpicStatus(epicId);
    }

    public void updateTask (Task newTask) {
        if (taskHashMap.containsKey(newTask.getId())) {
            taskHashMap.put(newTask.getId(), newTask);
        }
    }

    public void updateEpic(Epic newEpic) {
        if (epicHashMap.containsKey(newEpic.getId())) {
            epicHashMap.put(newEpic.getId(), newEpic);
        }
    }

    public void updateSubTask(SubTask newSubTask) {
        if (subTaskHashMap.containsKey(newSubTask.getId())) {
            subTaskHashMap.put(newSubTask.getId(), newSubTask);
            // получить идентификатор эпика, чтобы актуализировать его статус
            int epicId = subTaskHashMap.get(newSubTask.getId()).getEpicId();
            checkEpicStatus(epicId);
        }
    }

    public void removeTaskById(int id) {
        taskHashMap.remove(id);
    }

    public void removeEpicById(int id) {
        // удалить подзадачи эпика до удаления самого эпика
        ArrayList<Integer> subTasksId = epicHashMap.get(id).getSubtasksId();
        for (Integer subTaskId : subTasksId) {
            subTaskHashMap.remove(subTaskId);
        }
        epicHashMap.remove(id);
    }

    public void removeSubTaskById(int id) {
        // получить идентификатор эпика, чтобы после удаления подзадач обновить его статус
        int epicId = subTaskHashMap.get(id).getEpicId();
        // удалить идентификаторы подзадач из эпика
        ArrayList<Integer> subTasksId = epicHashMap.get(epicId).getSubtasksId();
        subTasksId.remove((Integer) id);
        // проверить статус эпика
        checkEpicStatus(epicId);
    }

    public ArrayList<SubTask> getListOfSubTasksByEpic (int id) {
        ArrayList<Integer> subTasksId = epicHashMap.get(id).getSubtasksId();
        ArrayList<SubTask> subtasksByEpic = new ArrayList<>();
        for (int subtaskId : subTasksId) {
            subtasksByEpic.add(subTaskHashMap.get(subtaskId));
        }
        return subtasksByEpic;
    }

    private void checkEpicStatus (int id) {
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
}
