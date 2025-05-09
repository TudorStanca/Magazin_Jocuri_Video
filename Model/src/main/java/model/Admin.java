package model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {

    public Admin() {}

    public Admin(String username, byte[] password, byte[] salt) {
        super(username, password, salt);
    }

    @Override
    public String toString() {
        return "Admin { " + super.toString() + " }";
    }
}
