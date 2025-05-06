package repository.interfaces;

import model.Client;

import java.util.Optional;

public interface IClientRepository extends IRepository<Long, Client>{
    Optional<Client> findByUsername(String username);
}
