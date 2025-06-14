package Model;

public class Student extends User {
    public Student(int id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String toString() {
        return "Student{id=" + getId() + ", name='" + getName() + "', email='" + getEmail() + "'}";
    }
}
