package services;

import model.UserType;
import model.dto.ClientDTO;
import model.dto.UserDTO;

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

    void logout(Long id);
}