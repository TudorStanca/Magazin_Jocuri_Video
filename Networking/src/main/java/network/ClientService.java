package network;

import com.google.protobuf.Empty;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import model.StarRating;
import model.UserType;
import model.dto.*;
import model.exception.ClientSideException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IObserver;
import services.IServices;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ClientService implements IServices {

    private static final Logger logger = LogManager.getLogger(ClientService.class.getName());

    private final String target; // ip:port
    private final IObserver client;

    private ManagedChannel channel;
    private ServicesGrpc.ServicesBlockingStub blockingStub;
    private ServicesGrpc.ServicesStub asyncStub;

    public ClientService(String target, IObserver client) {
        this.target = target;
        this.client = client;
    }

    private void createConnection() {
        channel = Grpc.newChannelBuilder(target, InsecureChannelCredentials.create()).build();
        blockingStub = ServicesGrpc.newBlockingStub(channel);
        asyncStub = ServicesGrpc.newStub(channel);
    }

    public void shutdownConnection() {
        try {
            if (channel != null && !channel.isShutdown()) {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
                logger.debug("Client shutdown complete.");
            }
        } catch (InterruptedException e) {
            logger.error("Shutdown interrupted.");
            channel.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void subscribeClient(Long userId) {
        SubscribeRequest subscribeRequest = SubscribeRequest.newBuilder().setId(userId).build();
        asyncStub.subscribe(subscribeRequest, new StreamObserver<>() {
            @Override
            public void onNext(Notification value) {
                logger.info("Received notification: {}", value);
                if (value.getType() == NotificationType.ClientNotification) {
                    client.notifyClients();
                }
                if (value.getType() == NotificationType.StockOperatorNotification) {
                    client.notifyStockOperators(value.getId());
                }
                if (value.getType() == NotificationType.AdminNotification) {
                    client.notifyAdmin();
                }
                if (value.getType() == NotificationType.TerminateSessionDeleteNotification) {
                    client.terminateDeleteSession(value.getId());
                }
                if (value.getType() == NotificationType.TerminateSessionUpdateNotification) {
                    client.terminateUpdateSession(value.getId());
                }
                if (value.getType() == NotificationType.ClientReviewNotification) {
                    client.notifyClientsReview(value.getId());
                }
                if (value.getType() == NotificationType.ClientBuyNotification) {
                    client.notifyClientsBuy();
                }
            }

            @Override
            public void onError(Throwable t) {
                logger.error("Error while subscribing to user", t);
            }

            @Override
            public void onCompleted() {
                shutdownConnection();
            }
        });
    }

    @Override
    public UserDTO signIn(String username, String password, UserType userType) throws ClientSideException {
        try {
            createConnection();
            SignInRequest signInRequest = SignInRequest.newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .setType(network.UserType.valueOf(userType.toString()))
                    .build();
            SignInResponse signInResponse = blockingStub.signIn(signInRequest);
            UserDTO user;
            switch (userType){
                case Client -> user = ProtoMappers.fromProto(signInResponse.getClient());
                case StockOperator -> user = ProtoMappers.fromProto(signInResponse.getStockOperator());
                case Admin -> user = ProtoMappers.fromProto(signInResponse.getAdmin());
                default -> throw new ClientSideException("Invalid signInType");
            }

            subscribeClient(user.getId());

            return user;
        } catch (StatusRuntimeException ex) {
            shutdownConnection();
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public ClientDTO signUp(String username, String password, String name, String cnp, String telephoneNumber, String address) {
        try {
            createConnection();
            SignUpRequest signUpRequest = SignUpRequest.newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .setName(name)
                    .setCnp(cnp)
                    .setTelephoneNumber(telephoneNumber)
                    .setAddress(address)
                    .build();

            SignUpResponse signUpResponse = blockingStub.signUp(signUpRequest);
            return ProtoMappers.fromProto(signUpResponse.getClient());
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        } finally {
            shutdownConnection();
        }
    }

    @Override
    public Map<Class<? extends UserDTO>, Iterable<? extends UserDTO>> getAllUsers() {
        logger.debug("Sending request to getAllUsers");
        GetAllUsersResponse response = blockingStub.getAllUsers(Empty.getDefaultInstance());
        logger.debug("Received response: {}", response);

        Map<Class<? extends UserDTO>, Iterable<? extends UserDTO>> users = new HashMap<>();
        users.put(ClientDTO.class, response.getClientsList().stream().map(ProtoMappers::fromProto).toList());
        users.put(StockOperatorDTO.class, response.getStockOperatorsList().stream().map(ProtoMappers::fromProto).toList());
        users.put(AdminDTO.class, response.getAdminsList().stream().map(ProtoMappers::fromProto).toList());

        return users;
    }

    @Override
    public void addNewClient(String username, String password, String name, String cnp, String telephoneNumber, String address) throws ClientSideException {
        try {
            logger.debug("Sending request to addNewClient");
            AddNewClientRequest request = AddNewClientRequest.newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .setName(name)
                    .setCnp(cnp)
                    .setTelephoneNumber(telephoneNumber)
                    .setAddress(address)
                    .build();

            blockingStub.addNewClient(request);
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public void addNewStockOperator(String username, String password, String company) throws ClientSideException {
        try {
            logger.debug("Sending request to addNewStockOperator");
            AddNewStockOperatorRequest request = AddNewStockOperatorRequest.newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .setCompany(company)
                    .build();

            blockingStub.addNewStockOperator(request);
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public UserDTO deleteUser(Long id, UserType type) throws ClientSideException {
        try {
            logger.debug("Sending request to deleteUser");
            DeleteUserRequest request = DeleteUserRequest.newBuilder()
                    .setId(id)
                    .setType(network.UserType.valueOf(type.toString()))
                    .build();

            var response = blockingStub.deleteUser(request);
            return ProtoMappers.fromProto(response.getUser());
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public UserDTO updateUser(Long id, String newUsername, UserType type) throws ClientSideException {
        try {
            logger.debug("Sending request to updateUser");
            UpdateUserRequest request = UpdateUserRequest.newBuilder()
                    .setId(id)
                    .setNewUsername(newUsername)
                    .setType(network.UserType.valueOf(type.toString()))
                    .build();

            var response = blockingStub.updateUser(request);
            return ProtoMappers.fromProto(response.getUser());
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public Iterable<GameDTO> getAllGames(Long id) {
        logger.debug("Sending request to getAllGames");
        GetAllGamesRequest request = GetAllGamesRequest.newBuilder()
                .setId(id)
                .build();
        GetAllGamesResponse response = blockingStub.getAllGames(request);
        logger.debug("Received GetAllGamesResponse response: {}", response);

        return response.getGamesList().stream().map(ProtoMappers::fromProto).toList();
    }

    @Override
    public void addNewGame(String name, String genre, String platform, BigDecimal price, Long idStockOperator) throws ClientSideException {
        try {
            logger.debug("Sending request to addNewGame");
            AddNewGameRequest request = AddNewGameRequest.newBuilder()
                    .setName(name)
                    .setGenre(genre)
                    .setPlatform(platform)
                    .setPrice(price.doubleValue())
                    .setIdStockOperator(idStockOperator)
                    .build();

            blockingStub.addNewGame(request);
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public GameDTO deleteGame(Long id) throws ClientSideException {
        try {
            logger.debug("Sending request to deleteGame");
            DeleteGameRequest request = DeleteGameRequest.newBuilder()
                    .setId(id)
                    .build();

            var response = blockingStub.deleteGame(request);
            return ProtoMappers.fromProto(response.getGame());
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public GameDTO updateGame(Long id, String newName, String newGenre, String newPlatform, BigDecimal newPrice) throws ClientSideException {
        try {
            logger.debug("Sending request to updateGame");
            UpdateGameRequest request = UpdateGameRequest.newBuilder()
                    .setId(id)
                    .setNewName(newName)
                    .setNewGenre(newGenre)
                    .setNewPlatform(newPlatform)
                    .setNewPrice(newPrice.doubleValue())
                    .build();

            var response = blockingStub.updateGame(request);
            return ProtoMappers.fromProto(response.getGame());
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public Iterable<GameDTO> getAllAvailableGames(Long id) {
        logger.debug("Sending request to getAllAvailableGames");
        GetAllGamesClientRequest request = GetAllGamesClientRequest.newBuilder()
                .setId(id)
                .build();
        GetAllGamesClientResponse response = blockingStub.getAllGamesClient(request);
        logger.debug("Received response: {}", response);

        return response.getGamesList().stream().map(ProtoMappers::fromProto).toList();
    }

    @Override
    public Iterable<OwnedGameDTO> getAllOwnedGames(Long id) {
        logger.debug("Sending request to getAllOwnedGames");
        GetAllOwnedGamesRequest request = GetAllOwnedGamesRequest.newBuilder()
                .setId(id)
                .build();

        GetAllOwnedGamesResponse response = blockingStub.getAllOwnedGames(request);
        logger.debug("Received response: {}", response);

        return response.getGamesList().stream().map(ProtoMappers::fromProto).toList();
    }

    @Override
    public Iterable<ReviewDTO> getAllReviews(Long id) {
        logger.debug("Sending request to getAllReviews");
        GetAllReviewsRequest request = GetAllReviewsRequest.newBuilder()
                .setId(id)
                .build();

        GetAllReviewsResponse response = blockingStub.getAllReviews(request);
        logger.debug("Received response: {}", response);

        return response.getReviewsList().stream().map(ProtoMappers::fromProto).toList();
    }

    @Override
    public void addNewReview(StarRating stars, String description, Long idGame) {
        try {
            logger.debug("Sending request to addNewReview");
            AddNewReviewRequest request = AddNewReviewRequest.newBuilder()
                    .setStars(ReviewProto.StarRating.valueOf(stars.toString()))
                    .setDescription(description)
                    .setIdGame(idGame)
                    .build();

            blockingStub.addGameReview(request);
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public Iterable<CartDTO> getAllGamesInCart(Long idClient) {
        logger.debug("Sending request to getAllGamesInCart");
        GetAllCartGamesRequest request = GetAllCartGamesRequest.newBuilder()
                .setId(idClient)
                .build();

        GetAllCartGamesResponse response = blockingStub.getAllCartGames(request);
        logger.debug("Received response: {}", response);

        return response.getCartsList().stream().map(ProtoMappers::fromProto).toList();
    }

    @Override
    public void addGameToCart(Long idClient, Long idGame, Instant date) throws ClientSideException {
        try {
            logger.debug("Sending request to addGameToCart");
            AddGameToCartRequest request = AddGameToCartRequest.newBuilder()
                    .setIdClient(idClient)
                    .setIdGame(idGame)
                    .setDate(ProtoMappers.toProtoTimestamp(date))
                    .build();

            blockingStub.addGameToCart(request);
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public CartDTO deleteGameFromCart(Long idClient, Long idGame) throws ClientSideException {
        try {
            logger.debug("Sending request to deleteGameFromCart");
            DeleteGameFromCartRequest request = DeleteGameFromCartRequest.newBuilder()
                    .setIdClient(idClient)
                    .setIdGame(idGame)
                    .build();

            var response = blockingStub.deleteGameFromCart(request);
            return ProtoMappers.fromProto(response.getCart());
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public void addGameToOwnedGames(Long idClient, Long idGame) {
        try {
            logger.debug("Sending request to addGameToOwnedGames");
            AddGameToOwnedGamesRequest request = AddGameToOwnedGamesRequest.newBuilder()
                    .setIdClient(idClient)
                    .setIdGame(idGame)
                    .build();

            blockingStub.addGameToOwnedGames(request);
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }

    @Override
    public void logout(Long id) throws ClientSideException {
        try {
            LogoutRequest request = LogoutRequest.newBuilder().setId(id).build();
            blockingStub.logout(request);
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        } finally {
            shutdownConnection();
        }
    }
}
