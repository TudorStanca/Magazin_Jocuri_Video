package server;

import model.Admin;
import model.Client;
import model.StockOperator;
import model.UserType;
import model.dto.AdminDTO;
import model.dto.ClientDTO;
import model.dto.StockOperatorDTO;
import model.dto.UserDTO;
import model.exception.ServerSideException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.*;
import repository.utils.DTOMapper;
import server.utils.PasswordHashing;
import services.IServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ServerService implements IServices {
    private static Logger logger = LogManager.getLogger(ServerService.class);

    private final IClientRepository clientRepository;
    private final IStockOperatorRepository stockOperatorRepository;
    private final IAdminRepository adminRepository;
    private final IGameRepository gameRepository;
    private final IReviewRepository reviewRepository;
    private final ICartRepository cartRepository;
    private final IOwnedGamesRepository ownedGamesRepository;

    public ServerService(IClientRepository client, IStockOperatorRepository stock, IAdminRepository admin, IGameRepository game, IReviewRepository review, ICartRepository cart, IOwnedGamesRepository ownedGame) {
        this.clientRepository = client;
        this.stockOperatorRepository = stock;
        this.adminRepository = admin;
        this.gameRepository = game;
        this.reviewRepository = review;
        this.cartRepository = cart;
        this.ownedGamesRepository = ownedGame;
    }

    @Override
    public synchronized UserDTO signIn(String username, String password, UserType userType) throws ServerSideException {
        Optional<UserDTO> user;
        switch (userType) {
            case Client -> user = clientRepository.findByUsername(username).map(c -> (UserDTO) c);
            case StockOperator -> user = stockOperatorRepository.findByUsername(username).map(c -> (UserDTO) c);
            case Admin -> user = adminRepository.findByUsername(username).map(c -> (UserDTO) c);
            default -> throw new ServerSideException("Invalid signInType");
        }

        if (user.isEmpty()) {
            throw new ServerSideException("User not found");
        } else if (!PasswordHashing.isExpectedPassword(password, user.get().getPassword(), user.get().getSalt())) {
            throw new ServerSideException("Wrong password");
        }

        return user.get();
    }

    @Override
    public synchronized ClientDTO signUp(String username, String password, String name, String cnp, String telephone, String address) throws ServerSideException {
        byte[] salt = PasswordHashing.generateSalt();
        byte[] hash = PasswordHashing.generateHash(password, salt);
        Optional<ClientDTO> client = clientRepository.save(new Client(username, hash, salt, name, cnp, telephone, address));

        if(client.isEmpty()) {
            throw new ServerSideException("User could not be created");
        }

        return client.get();
    }

    @Override
    public synchronized Map<Class<? extends UserDTO>, Iterable<? extends UserDTO>> getAllUsers() {
        Map<Class<? extends UserDTO>, Iterable<? extends UserDTO>> users = new HashMap<>();
        users.put(ClientDTO.class, clientRepository.findAll());
        users.put(StockOperatorDTO.class, stockOperatorRepository.findAll());
        users.put(AdminDTO.class, adminRepository.findAll());
        return users;
    }

    @Override
    public synchronized void addNewClient(String username, String password, String name, String cnp, String telephoneNumber, String address) throws ServerSideException {
        byte[] salt = PasswordHashing.generateSalt();
        byte[] hash = PasswordHashing.generateHash(password, salt);
        Optional<ClientDTO> client = clientRepository.save(new Client(username, hash, salt, name, cnp, telephoneNumber, address));

        if(client.isEmpty()) {
            throw new ServerSideException("Client could not be created");
        }
    }

    @Override
    public synchronized void addNewStockOperator(String username, String password, String company) throws ServerSideException {
        byte[] salt = PasswordHashing.generateSalt();
        byte[] hash = PasswordHashing.generateHash(password, salt);
        Optional<StockOperatorDTO> stockOperator = stockOperatorRepository.save(new StockOperator(username, hash, salt, company));

        if(stockOperator.isEmpty()) {
            throw new ServerSideException("StockOperator could not be created");
        }
    }

    @Override
    public synchronized UserDTO deleteUser(Long id, UserType type) throws ServerSideException {
        UserDTO user;
        switch (type) {
            case Client -> user = clientRepository.delete(id).orElse(null);
            case StockOperator -> user = stockOperatorRepository.delete(id).orElse(null);
            case Admin -> user = adminRepository.delete(id).orElse(null);
            default -> throw new ServerSideException("Invalid UserType");
        }
        return user;
    }

    @Override
    public synchronized UserDTO updateUser(Long id, String newUsername, UserType type) throws ServerSideException {
        switch (type) {
            case Client -> {
                var entity = clientRepository.findById(id).orElseThrow(() -> new ServerSideException("Client not found"));
                var client = DTOMapper.fromDTO(entity);
                client.setUsername(newUsername);
                return clientRepository.update(client).orElse(null);
            }
            case StockOperator -> {
                var entity = stockOperatorRepository.findById(id).orElseThrow(() -> new ServerSideException("Stock operator not found"));
                var stockOperator = DTOMapper.fromDTO(entity);
                stockOperator.setUsername(newUsername);
                return stockOperatorRepository.update(stockOperator).orElse(null);
            }
            case Admin -> {
                var entity = adminRepository.findById(id).orElseThrow(() -> new ServerSideException("Admin could not be found"));
                var admin = DTOMapper.fromDTO(entity);
                admin.setUsername(newUsername);
                return adminRepository.update(admin).orElse(null);
            }
            default -> throw new ServerSideException("Invalid UserType");
        }
    }

    @Override
    public void logout(Long id) {

    }
}
