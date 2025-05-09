package services;

import model.dto.ClientDTO;

public interface IServices {

    ClientDTO signIn(String username, String password);

    ClientDTO signUp(String username, String password, String name, String cnp, String telephoneNumber, String address);

    void logout(Long id);
}