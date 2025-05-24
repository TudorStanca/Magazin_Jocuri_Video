package repository.utils;

import model.*;
import model.dto.*;

import java.util.List;

public class ToDTOMapper {

    public static ClientDTO toDTO(Client client) {
        List<CartDTO> carts = client.getCarts().stream()
                .map(ToDTOMapper::toDTO)
                .toList();
        List<OwnedGameDTO> ownedGames = client.getOwnedGames().stream()
                .map(ToDTOMapper::toDTO)
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
                cart.getClient().getId(),
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
                .map(ToDTOMapper::toDTO)
                .toList();

        Long operatorId = game.getStockOperator() != null ? game.getStockOperator().getId() : null;

        return new GameDTO(
                game.getId(),
                game.getName(),
                game.getGenre(),
                game.getPlatform(),
                game.getPrice(),
                operatorId,
                reviews
        );
    }

    public static ReviewDTO toDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getStars(),
                review.getDescription(),
                review.getGame().getId()
        );
    }

    public static StockOperatorDTO toDTO(StockOperator stockOperator) {
        List<GameDTO> games = stockOperator.getGames().stream()
                .map(ToDTOMapper::toDTO)
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
}
