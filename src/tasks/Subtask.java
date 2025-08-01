package tasks;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        if (id != epicId) {
            this.epicId = epicId;
        }
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (getId() != epicId) {
            this.epicId = epicId;
        }
    }

    @Override
    public String toString() {
        return "Tasks.Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}