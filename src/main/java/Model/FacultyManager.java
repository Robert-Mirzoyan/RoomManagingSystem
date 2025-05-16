package Model;

public class FacultyManager extends User {
    public FacultyManager(int id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getRole() {
        return "FacultyManager";
    }
}
