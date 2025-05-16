package Model;
import java.time.Duration;
import java.time.LocalDateTime;

public class TimeSlot {
    private int id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeSlot(int id, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Duration duration() {
        return Duration.between(startTime, endTime);
    }

    public boolean overlaps(TimeSlot other) {
        return startTime.isBefore(other.endTime) && endTime.isAfter(other.startTime);
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
