// Подкласс Epic, представляющий крупную задачу-эпик
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds; // список ID подзадач// Можно добавить дополнительные поля или методы для эпика

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        this.subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }
    public void removeSubtaskId(int subtaskId) {
       subtaskIds.remove(Integer.valueOf(subtaskId));
    }
}
