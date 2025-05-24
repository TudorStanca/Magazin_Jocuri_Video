package server.utils;

import model.*;
import model.dto.*;
import model.exception.ServerSideException;
import repository.interfaces.IClientRepository;
import repository.interfaces.IGameRepository;
import repository.interfaces.IStockOperatorRepository;

public class FromDTOMapper {

    public static Client fromDTO(ClientDTO dto) {
        var client = new Client(
                dto.getUsername(),
                dto.getPassword(),
                dto.getSalt(),
                dto.getName(),
                dto.getCnp(),
                dto.getTelephoneNumber(),
                dto.getAddress()
        );
        client.setId(dto.getId());
        return client;
    }

    public static StockOperator fromDTO(StockOperatorDTO dto) {
        var stockOperator = new StockOperator(
                dto.getUsername(),
                dto.getPassword(),
                dto.getSalt(),
                dto.getCompany()
        );
        stockOperator.setId(dto.getId());
        return stockOperator;
    }

    public static Admin fromDTO(AdminDTO dto) {
        var admin = new Admin(
                dto.getUsername(),
                dto.getPassword(),
                dto.getSalt()
        );
        admin.setId(dto.getId());
        return admin;
    }

    public static Game fromDTO(GameDTO dto, IStockOperatorRepository repo) {
        var game = new Game(
                dto.name(),
                dto.genre(),
                dto.platform(),
                dto.price()
        );
        game.setId(dto.id());
        game.setStockOperator(fromDTO(repo.findById(dto.stockOperatorId()).orElseThrow(() -> new ServerSideException("Cannot find stock operator"))));
        return game;
    }

    public static Review fromDTO(ReviewDTO dto, IGameRepository gameRepo, IStockOperatorRepository stockRepo) {
        var review = new Review(
                dto.starRating(),
                dto.description()
        );
        review.setId(dto.id());
        review.setGame(fromDTO(gameRepo.findById(dto.gameId()).orElseThrow(() -> new ServerSideException("Cannot find game")), stockRepo));
        return review;
    }

    public static Cart fromDTO(CartDTO dto, IClientRepository clientRepo, IGameRepository gameRepo, IStockOperatorRepository stockRepo) {
        var cart = new Cart(
                fromDTO(clientRepo.findById(dto.clientId()).orElseThrow(() -> new ServerSideException("Cannot find client"))),
                fromDTO(dto.game(), stockRepo),
                dto.date()
        );
        cart.setId(new CartId(dto.clientId(), dto.game().id()));
        return cart;
    }

    public static OwnedGame fromDTO(OwnedGameDTO dto, IClientRepository clientRepo, IGameRepository gameRepo, IStockOperatorRepository stockRepo) {
        var ownedGame = new OwnedGame(
                fromDTO(clientRepo.findById(dto.clientId()).orElseThrow(() -> new ServerSideException("Cannot find client"))),
                fromDTO(dto.game(), stockRepo)
        );
        ownedGame.setNrHours(dto.nrHours());
        ownedGame.setId(new OwnedGameId(dto.clientId(), dto.game().id()));
        return ownedGame;
    }
}
