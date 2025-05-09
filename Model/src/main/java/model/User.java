package model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Arrays;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public class User extends EntityId<Long> {
    @Size(max = 255)
    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Column(name = "password", nullable = false)
    private byte[] password;

    @NotNull
    @Column(name = "salt", nullable = false)
    private byte[] salt;

    public User() {}

    public User(String username, byte[] password, byte[] salt) {
        this.username = username;
        this.password = password;
        this.salt = salt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    @Override
    public String toString() {
        return "User{" +
               "username='" + username + '\'' +
               ", password='" + Arrays.toString(password) + '\'' +
               '}';
    }
}