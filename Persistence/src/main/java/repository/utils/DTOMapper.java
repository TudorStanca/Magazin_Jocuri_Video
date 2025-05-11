package repository.utils;

import model.*;
import model.dto.*;

import java.util.List;

public class DTOMapper {

    public static ClientDTO toDTO(Client client) {
        List<CartDTO> carts = client.getCarts().stream()
                .map(DTOMapper::toDTO)
                .toList();
        List<OwnedGameDTO> ownedGames = client.getOwnedGames().stream()
                .map(DTOMapper::toDTO)
                .toList();
        return new ClientDTO(
                client.getId(),
                client.getUsername(),
                client.getPassword(),
                client.getSalt(),
                client.getName(),
                client.getCnp(),
                client.getTelephoneNumber(),
                client.getAddress(),
                carts,
                ownedGames
        );
    }

    public static CartDTO toDTO(Cart cart) {
        return new CartDTO(
                toDTO(cart.getGame()),
                cart.getDate()
        );
    }

    public static OwnedGameDTO toDTO(OwnedGame ownedGame) {
        return new OwnedGameDTO(
                toDTO(ownedGame.getGame()),
                ownedGame.getNrHours()
        );
    }

    public static GameDTO toDTO(Game game) {
        List<ReviewDTO> reviews = game.getReviews().stream()
                .map(DTOMapper::toDTO)
                .toList();
        return new GameDTO(
                game.getId(),
                game.getName(),
                game.getGenre(),
                game.getPlatform(),
                game.getPrice(),
                reviews
        );
    }

    public static ReviewDTO toDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getStars(),
                review.getDescription()
        );
    }

    public static StockOperatorDTO toDTO(StockOperator stockOperator) {
        List<GameDTO> games = stockOperator.getGames().stream()
                .map(DTOMapper::toDTO)
                .toList();
        return new StockOperatorDTO(
                stockOperator.getId(),
                stockOperator.getUsername(),
                stockOperator.getPassword(),
                stockOperator.getSalt(),
                stockOperator.getCompany(),
                games
        );
    }

    public static AdminDTO toDTO(Admin entity) {
        return new AdminDTO(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getSalt()
        );
    }

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
}
