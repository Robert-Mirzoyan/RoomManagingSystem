package Model;

public class Booking {
    private int id;
    private Room room;
    private TimeSlot timeSlot;
    private Student student;
    private Status status;

    public Booking(int id, Room room, TimeSlot timeSlot, Student student, Status status) {
        this.id = id;
        this.room = room;
        this.timeSlot = timeSlot;
        this.student = student;
        this.status = status;
    }

    public boolean isEditable() {
        return status == Status.PENDING;
    }

    public int getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public Student getStudent() {
        return student;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public void setRoom(Room room) {
        this.room = room;
    }

    @SuppressWarnings("unused")
    public void setStudent(Student student) {
        this.student = student;
    }

    @SuppressWarnings("unused")
    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    @SuppressWarnings("unused")
    public void setStatus(Status status) {
        this.status = status;
    }
}
