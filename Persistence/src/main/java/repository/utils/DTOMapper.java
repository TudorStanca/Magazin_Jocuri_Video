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
}
