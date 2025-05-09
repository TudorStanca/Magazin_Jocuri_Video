package repository.interfaces;

import model.Client;
import model.dto.ClientDTO;

import java.util.Optional;

public interface IClientRepository extends IRepository<Long, Client, ClientDTO>{
    Optional<ClientDTO> findByUsername(String username);
}
