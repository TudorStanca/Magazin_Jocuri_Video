package server;

import model.*;
import model.dto.*;
import model.exception.ServerSideException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.*;
import repository.utils.ToDTOMapper;
import server.utils.FromDTOMapper;
import server.utils.PasswordHashing;
import services.IServices;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
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
            case Client -> user = clientRepository.delete(id).orElseThrow(() -> new ServerSideException("Client cannot be deleted"));
            case StockOperator -> user = stockOperatorRepository.delete(id).orElseThrow(() -> new ServerSideException("Stock operator cannot be deleted"));
            case Admin -> user = adminRepository.delete(id).orElseThrow(() -> new ServerSideException("Admin cannot be deleted"));
            default -> throw new ServerSideException("Invalid UserType");
        }
        return user;
    }

    @Override
    public synchronized UserDTO updateUser(Long id, String newUsername, UserType type) throws ServerSideException {
        switch (type) {
            case Client -> {
                var entity = clientRepository.findById(id).orElseThrow(() -> new ServerSideException("Client not found"));
                var client = FromDTOMapper.fromDTO(entity);
                client.setUsername(newUsername);
                return clientRepository.update(client).orElse(null);
            }
            case StockOperator -> {
                var entity = stockOperatorRepository.findById(id).orElseThrow(() -> new ServerSideException("Stock operator not found"));
                var stockOperator = FromDTOMapper.fromDTO(entity);
                stockOperator.setUsername(newUsername);
                return stockOperatorRepository.update(stockOperator).orElse(null);
            }
            case Admin -> {
                var entity = adminRepository.findById(id).orElseThrow(() -> new ServerSideException("Admin could not be found"));
                var admin = FromDTOMapper.fromDTO(entity);
                admin.setUsername(newUsername);
                return adminRepository.update(admin).orElse(null);
            }
            default -> throw new ServerSideException("Invalid UserType");
        }
    }

    @Override
    public synchronized Iterable<GameDTO> getAllGames(Long id) {
        return gameRepository.findByStockOperator(id);
    }

    @Override
    public synchronized void addNewGame(String name, String genre, String platform, BigDecimal price, Long idStockOperator) throws ServerSideException {
        Game newGame = new Game(name, genre, platform, price);
        newGame.setStockOperator(FromDTOMapper.fromDTO(stockOperatorRepository.findById(idStockOperator).orElseThrow(() -> new ServerSideException("Stock operator not found"))));
        Optional<GameDTO> game = gameRepository.save(newGame);

        if(game.isEmpty()) {
            throw new ServerSideException("Game could not be created");
        }
    }

    @Override
    public synchronized GameDTO deleteGame(Long id) throws ServerSideException {
        Optional<GameDTO> deletedGame = gameRepository.delete(id);

        if(deletedGame.isEmpty()) {
            throw new ServerSideException("Game could not be deleted");
        }

        return deletedGame.get();
    }

    @Override
    public synchronized GameDTO updateGame(Long id, String newName, String newGenre, String newPlatform, BigDecimal newPrice) throws ServerSideException {
        GameDTO entity = gameRepository.findById(id).orElseThrow(() -> new ServerSideException("Game not found"));
        Game game = FromDTOMapper.fromDTO(entity, stockOperatorRepository);
        game.setName(newName);
        game.setGenre(newGenre);
        game.setPlatform(newPlatform);
        game.setPrice(newPrice);
        return gameRepository.update(game).orElse(null);
    }

    @Override
    public Iterable<GameDTO> getAllAvailableGames(Long id) {
        return gameRepository.getAllAvailableGames(id);
    }

    @Override
    public Iterable<OwnedGameDTO> getAllOwnedGames(Long id) {
        return ownedGamesRepository.findAllOwnedGamesForClient(id);
    }

    @Override
    public Iterable<ReviewDTO> getAllReviews(Long id) {
        return reviewRepository.getAllReviewsForGame(id);
    }

    @Override
    public void addNewReview(StarRating stars, String description, Long idGame) throws ServerSideException {
        Review newReview = new Review(stars, description);
        newReview.setGame(FromDTOMapper.fromDTO(gameRepository.findById(idGame).orElseThrow(() -> new ServerSideException("Game not found")), stockOperatorRepository));
        Optional<ReviewDTO> review = reviewRepository.save(newReview);

        if(review.isEmpty()) {
            throw new ServerSideException("Review could not be created");
        }
    }

    @Override
    public void addGameToCart(Long idClient, Long idGame, Instant date) {
        Client client = FromDTOMapper.fromDTO(clientRepository.findById(idClient).orElseThrow(() -> new ServerSideException("Client not found")));
        Game game = FromDTOMapper.fromDTO(gameRepository.findById(idGame).orElseThrow(() -> new ServerSideException("Client not found")), stockOperatorRepository);

        Cart newCart = new Cart(client, game, date);
        newCart.setId(new CartId(client.getId(), game.getId()));
        Optional<CartDTO> cart = cartRepository.save(newCart);

        if(cart.isEmpty()) {
            throw new ServerSideException("Cart could not be created");
        }
    }

    @Override
    public void logout(Long id) {

    }
}
