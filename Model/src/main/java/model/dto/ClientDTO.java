package model.dto;

import java.util.List;

public record ClientDTO(Long id, String username, byte[] password, byte[] salt, String name, String cnp, String telephoneNumber,
                        String address, List<CartDTO> carts, List<OwnedGameDTO> ownedGames) implements RepositoryDTO {

    @Override
    public String toString() {
        return "ClientDTO{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", name='" + name + '\'' +
               ", cnp='" + cnp + '\'' +
               ", telephoneNumber='" + telephoneNumber + '\'' +
               ", address='" + address + '\'' +
               ", carts=" + (carts != null ? carts.size() : 0) +
               ", ownedGames=" + (ownedGames != null ? ownedGames.size() : 0) +
               '}';
    }
}
