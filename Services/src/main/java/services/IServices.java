package services;

import model.StarRating;
import model.UserType;
import model.dto.*;

import java.math.BigDecimal;
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

    Iterable<GameDTO> getAllAvailableGames(Long clientId);

    Iterable<OwnedGameDTO> getAllOwnedGames(Long id);

    Iterable<ReviewDTO> getAllReviews(Long id);

    void addNewReview(StarRating stars, String description, Long idGame);

    void logout(Long id);
}