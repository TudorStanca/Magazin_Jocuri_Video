package model.dto;

public class AdminDTO extends UserDTO {
    public AdminDTO(Long id, String username, byte[] password, byte[] salt) {
        super(id, username, password, salt);
    }

    @Override
    public String toString() {
        return "AdminDTO{" +
               "id=" + id +
               ", username='" + username + '\'' +
               '}';
    }
}
