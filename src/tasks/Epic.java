package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIds; //

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        this.subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != getId() && !subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }
    public void removeSubtaskId(int subtaskId) {
       subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    @Override
    public void setStatus(Status status) {
        super.setStatus(status);
    }
}
