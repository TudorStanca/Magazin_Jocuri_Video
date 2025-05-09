package model.dto;

import java.util.Arrays;

public abstract class UserDTO implements RepositoryDTO {
    protected Long id;
    protected String username;
    protected byte[] password;
    protected byte[] salt;

    public UserDTO(Long id, String username, byte[] password, byte[] salt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.salt = salt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPassword() {
        return password;
    }

    public byte[] getSalt() {
        return salt;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", password=" + Arrays.toString(password) +
               ", salt=" + Arrays.toString(salt) +
               '}';
    }
}
