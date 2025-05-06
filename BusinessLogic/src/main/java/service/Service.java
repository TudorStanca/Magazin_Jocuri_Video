package service;

import model.Client;
import model.User;
import model.exceptions.MyException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.*;

import java.util.Optional;

public class Service {
    private static Logger logger = LogManager.getLogger(Service.class);

    private final IClientRepository clientRepository;
    private final IStockOperatorRepository stockOperatorRepository;
    private final IGameRepository gameRepository;
    private final IReviewRepository reviewRepository;
    private final ICartRepository cartRepository;
    private final IOwnedGamesRepository ownedGamesRepository;

    public Service(IClientRepository client, IStockOperatorRepository stock, IGameRepository game, IReviewRepository review, ICartRepository cart, IOwnedGamesRepository ownedGame) {
        this.clientRepository = client;
        this.stockOperatorRepository = stock;
        this.gameRepository = game;
        this.reviewRepository = review;
        this.cartRepository = cart;
        this.ownedGamesRepository = ownedGame;
    }

    public User signIn(String username, String password) {
        Optional<Client> user = clientRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new MyException("User not found");
        } else if (!user.get().getPassword().equals(password)) {
            throw new MyException("Wrong password");
        }

        return user.get();
    }

    public void signUp(String username, String password, String name, String cnp, String telephone, String address) {
        Client client = new Client(username, password, name, cnp, telephone, address);
        clientRepository.save(client);
    }
}
