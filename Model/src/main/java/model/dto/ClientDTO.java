package model.dto;

import java.util.List;

public class ClientDTO extends UserDTO {

    private String name;
    private String cnp;
    private String telephoneNumber;
    private String address;
    private List<CartDTO> carts;
    private List<OwnedGameDTO> ownedGames;

    public ClientDTO(Long id, String username, byte[] password, byte[] salt, String name, String cnp, String telephoneNumber,
                     String address, List<CartDTO> carts, List<OwnedGameDTO> ownedGames) {
        super(id, username, password, salt);
        this.name = name;
        this.cnp = cnp;
        this.telephoneNumber = telephoneNumber;
        this.address = address;
        this.carts = carts;
        this.ownedGames = ownedGames;
    }

    public String getName() {
        return name;
    }

    public String getCnp() {
        return cnp;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public List<CartDTO> getCarts() {
        return carts;
    }

    public List<OwnedGameDTO> getOwnedGames() {
        return ownedGames;
    }

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
