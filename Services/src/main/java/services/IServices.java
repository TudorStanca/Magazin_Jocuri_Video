package services;

import model.SignInType;
import model.dto.ClientDTO;
import model.dto.UserDTO;

public interface IServices {

    UserDTO signIn(String username, String password, SignInType signInType);

    ClientDTO signUp(String username, String password, String name, String cnp, String telephoneNumber, String address);

    void logout(Long id);
}