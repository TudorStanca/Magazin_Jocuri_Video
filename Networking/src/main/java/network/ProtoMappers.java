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
        List<CartProto> carts = client.carts().stream()
                .map(ProtoMappers::toProto)
                .toList();
        List<OwnedGameProto> ownedGames = client.ownedGames().stream()
                .map(ProtoMappers::toProto)
                .toList();

        return ClientProto.newBuilder()
                .setId(client.id())
                .setUsername(client.username())
                .setPassword(client.password())
                .setName(client.name())
                .setCnp(client.cnp())
                .setTelephoneNumber(client.telephoneNumber())
                .setAddress(client.address())
                .addAllCarts(carts)
                .addAllOwnedGames(ownedGames)
                .build();
    }

    public static StockOperatorProto toProto(StockOperatorDTO stockOperator) {
        List<GameProto> games = stockOperator.games().stream()
                .map(ProtoMappers::toProto)
                .toList();
        return StockOperatorProto.newBuilder()
                .setId(stockOperator.id())
                .setUsername(stockOperator.username())
                .setPassword(stockOperator.password())
                .setCompany(stockOperator.company())
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
                .addAllReviews(reviews)
                .build();
    }

    public static ReviewProto toProto(ReviewDTO review) {
        return ReviewProto.newBuilder()
                .setId(review.id())
                .setStars(ReviewProto.StarRating.valueOf(review.starRating().toString()))
                .setDescription(review.description())
                .build();
    }

    public static ClientDTO fromProto(ClientProto proto) {
        return new ClientDTO(
                proto.getId(),
                proto.getUsername(),
                proto.getPassword(),
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
                proto.getReviewsList().stream().map(ProtoMappers::fromProto).toList()
        );
    }

    public static ReviewDTO fromProto(ReviewProto proto) {
        return new ReviewDTO(
                proto.getId(),
                StarRating.valueOf(proto.getStars().toString()),
                proto.getDescription()
        );
    }

    public static StockOperatorDTO fromProto(StockOperatorProto proto) {
        return new StockOperatorDTO(
                proto.getId(),
                proto.getUsername(),
                proto.getPassword(),
                proto.getCompany(),
                proto.getGamesList().stream().map(ProtoMappers::fromProto).toList()
        );
    }
}
