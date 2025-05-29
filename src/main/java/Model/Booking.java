package Model;

import java.util.ArrayList;

public class Booking {
    private int id;
    private Room room;
    private TimeSlot timeSlot;
    private Student student;
    private Status status;
    private ArrayList<Student> participants;

    public Booking(int id, Room room, TimeSlot timeSlot, Student student, Status status) {
        this.id = id;
        this.room = room;
        this.timeSlot = timeSlot;
        this.student = student;
        this.status = status;
        this.participants = new ArrayList<>();
    }

    public void approve() {
        this.status = Status.APPROVED;
    }

    public void reject() {
        this.status = Status.REJECTED;
    }

    public void cancel() {
        this.status = Status.CANCELLED;
    }

    public boolean isEditable() {
        return status == Status.PENDING;
    }

    public boolean conflictsWith(Booking other) {
        return this.room.getId() == other.room.getId() && this.timeSlot.overlaps(other.timeSlot);
    }

    public boolean addParticipant(Student student) {
        if (this.room.getCapacity() <= participants.size() + 1){
            System.out.println("Room is full.");
            return false;
        }
        participants.add(student);
        return true;
    }

    public boolean removeParticipant(Student student) {
        if (participants.contains(student)){
            participants.remove(student);
            return true;
        }
        System.out.println("No such student in the booking.");
        return false;
    }

    public ArrayList<Student> getParticipants() {
        System.out.println("List of Participants:");
        System.out.println("ID: " + this.student.getId() +
                ", Name: " + this.student.getName() +
                "(Requester), Email: " + this.student.getEmail());
        for (Student participant : participants) {
            System.out.println("ID: " + participant.getId() +
                    ", Name: " + participant.getName() +
                    ", Email: " + participant.getEmail());
        }
        return participants;
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

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
