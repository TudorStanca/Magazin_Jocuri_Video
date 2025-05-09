package server;

import model.Client;
import model.dto.ClientDTO;
import model.exception.ServerSideException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.*;
import server.utils.PasswordHashing;
import services.IServices;

import java.util.Optional;

public class ServerService implements IServices {
    private static Logger logger = LogManager.getLogger(ServerService.class);

    private final IClientRepository clientRepository;
    private final IStockOperatorRepository stockOperatorRepository;
    private final IGameRepository gameRepository;
    private final IReviewRepository reviewRepository;
    private final ICartRepository cartRepository;
    private final IOwnedGamesRepository ownedGamesRepository;

    public ServerService(IClientRepository client, IStockOperatorRepository stock, IGameRepository game, IReviewRepository review, ICartRepository cart, IOwnedGamesRepository ownedGame) {
        this.clientRepository = client;
        this.stockOperatorRepository = stock;
        this.gameRepository = game;
        this.reviewRepository = review;
        this.cartRepository = cart;
        this.ownedGamesRepository = ownedGame;
    }

    @Override
    public synchronized ClientDTO signIn(String username, String password) throws ServerSideException {
        Optional<ClientDTO> client = clientRepository.findByUsername(username);

        if (client.isEmpty()) {
            throw new ServerSideException("User not found");
        } else if (!PasswordHashing.isExpectedPassword(password, client.get().password(), client.get().salt())) {
            throw new ServerSideException("Wrong password");
        }

        return client.get();
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
    public void logout(Long id) {

    }
}
