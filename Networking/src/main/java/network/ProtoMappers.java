package network;

import com.google.protobuf.Timestamp;
import model.StarRating;
import model.dto.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class ProtoMappers {

    private static Timestamp toProtoTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    private static Instant fromProtoTimestamp(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    public static ClientProto toProto(ClientDTO client) {
        List<CartProto> carts = client.getCarts().stream()
                .map(ProtoMappers::toProto)
                .toList();
        List<OwnedGameProto> ownedGames = client.getOwnedGames().stream()
                .map(ProtoMappers::toProto)
                .toList();

        return ClientProto.newBuilder()
                .setId(client.getId())
                .setUsername(client.getUsername())
                .setName(client.getName())
                .setCnp(client.getCnp())
                .setTelephoneNumber(client.getTelephoneNumber())
                .setAddress(client.getAddress())
                .addAllCarts(carts)
                .addAllOwnedGames(ownedGames)
                .build();
    }

    public static StockOperatorProto toProto(StockOperatorDTO stockOperator) {
        List<GameProto> games = stockOperator.getGames().stream()
                .map(ProtoMappers::toProto)
                .toList();
        return StockOperatorProto.newBuilder()
                .setId(stockOperator.getId())
                .setUsername(stockOperator.getUsername())
                .setCompany(stockOperator.getCompany())
                .addAllGames(games)
                .build();
    }

    public static CartProto toProto(CartDTO cart) {
        return CartProto.newBuilder()
                .setGame(toProto(cart.game()))
                .setDate(toProtoTimestamp(cart.date()))
                .build();
    }

    public static OwnedGameProto toProto(OwnedGameDTO ownedGame) {
        return OwnedGameProto.newBuilder()
                .setGame(toProto(ownedGame.game()))
                .setNrHours(ownedGame.nrHours())
                .build();
    }

    public static GameProto toProto(GameDTO game) {
        List<ReviewProto> reviews = game.reviews().stream()
                .map(ProtoMappers::toProto)
                .toList();
        return GameProto.newBuilder()
                .setId(game.id())
                .setName(game.name())
                .setGenre(game.genre())
                .setPlatform(game.platform())
                .setPrice(game.price().doubleValue())
                .setIdStockOperator(game.stockOperatorId())
                .addAllReviews(reviews)
                .build();
    }

    public static ReviewProto toProto(ReviewDTO review) {
        return ReviewProto.newBuilder()
                .setId(review.id())
                .setStars(ReviewProto.StarRating.valueOf(review.starRating().toString()))
                .setDescription(review.description())
                .setIdGame(review.gameId())
                .build();
    }

    public static AdminProto toProto(AdminDTO admin) {
        return AdminProto.newBuilder()
                .setId(admin.getId())
                .setUsername(admin.getUsername())
                .build();
    }

    public static UserProto toProto(UserDTO user) {
        return UserProto.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .build();
    }

    public static ClientDTO fromProto(ClientProto proto) {
        return new ClientDTO(
                proto.getId(),
                proto.getUsername(),
                new byte[]{},
                new byte[]{},
                proto.getName(),
                proto.getCnp(),
                proto.getTelephoneNumber(),
                proto.getAddress(),
                proto.getCartsList().stream().map(ProtoMappers::fromProto).toList(),
                proto.getOwnedGamesList().stream().map(ProtoMappers::fromProto).toList()
        );
    }

    public static CartDTO fromProto(CartProto proto) {
        return new CartDTO(
                fromProto(proto.getGame()),
                fromProtoTimestamp(proto.getDate())
        );
    }

    public static OwnedGameDTO fromProto(OwnedGameProto proto) {
        return new OwnedGameDTO(
                fromProto(proto.getGame()),
                proto.getNrHours()
        );
    }

    public static GameDTO fromProto(GameProto proto) {
        return new GameDTO(
                proto.getId(),
                proto.getName(),
                proto.getGenre(),
                proto.getPlatform(),
                BigDecimal.valueOf(proto.getPrice()),
                proto.getIdStockOperator(),
                proto.getReviewsList().stream().map(ProtoMappers::fromProto).toList()
        );
    }

    public static ReviewDTO fromProto(ReviewProto proto) {
        return new ReviewDTO(
                proto.getId(),
                StarRating.valueOf(proto.getStars().toString()),
                proto.getDescription(),
                proto.getIdGame()
        );
    }

    public static StockOperatorDTO fromProto(StockOperatorProto proto) {
        return new StockOperatorDTO(
                proto.getId(),
                proto.getUsername(),
                new byte[]{},
                new byte[]{},
                proto.getCompany(),
                proto.getGamesList().stream().map(ProtoMappers::fromProto).toList()
        );
    }

    public static AdminDTO fromProto(AdminProto proto) {
        return new AdminDTO(
                proto.getId(),
                proto.getUsername(),
                new byte[]{},
                new byte[]{}
        );
    }

    public static UserDTO fromProto(UserProto proto) {
        return new UserDTO(
                proto.getId(),
                proto.getUsername(),
                new byte[]{},
                new byte[]{}
        );
    }
}
