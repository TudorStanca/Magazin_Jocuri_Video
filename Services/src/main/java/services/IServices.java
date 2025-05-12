package services;

import model.UserType;
import model.dto.ClientDTO;
import model.dto.GameDTO;
import model.dto.UserDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IServices {

    UserDTO signIn(String username, String password, UserType userType);

    ClientDTO signUp(String username, String password, String name, String cnp, String telephoneNumber, String address);

    Map<Class<? extends UserDTO>, Iterable<? extends UserDTO>> getAllUsers();

    void addNewClient(String username, String password, String name, String cnp, String telephoneNumber, String address);

    void addNewStockOperator(String username, String password, String company);

    UserDTO deleteUser(Long id, UserType type);

    UserDTO updateUser(Long id, String newUsername, UserType type);

    Iterable<GameDTO> getAllGames(Long id);

    void addNewGame(String name, String genre, String platform, BigDecimal price, Long idStockOperator);

    GameDTO deleteGame(Long id);

    GameDTO updateGame(Long id, String newName, String newGenre, String newPlatform, BigDecimal newPrice);

    void logout(Long id);
}