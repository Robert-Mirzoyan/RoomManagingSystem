package Model;

public class Admin extends User {
    public Admin(int id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getRole() {
        return "Admin";
    }

    @Override
    public String toString() {
        return "Admin{id=" + getId() + ", name='" + getName() + "', email='" + getEmail() + "'}";
    }
}
