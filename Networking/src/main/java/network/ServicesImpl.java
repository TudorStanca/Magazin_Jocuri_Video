package network;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import model.UserType;
import model.dto.*;
import model.exception.ServerSideException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IServices;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.StreamSupport;

public class ServicesImpl extends ServicesGrpc.ServicesImplBase {

    private final IServices service;

    private final Map<Long, StreamObserver<Notification>> loggedClients;
    private static Logger logger = LogManager.getLogger(ServicesImpl.class);

    private final ExecutorService executor;

    public ServicesImpl(IServices service, ExecutorService executor) {
        this.service = service;
        this.executor = executor;
        this.loggedClients = new ConcurrentHashMap<>();
    }

    private void addObserver(Long userId, StreamObserver<Notification> observer) {
        loggedClients.put(userId, observer);
    }

    private StreamObserver<Notification> removeObserver(Long userId) {
        return loggedClients.remove(userId);
    }

    private StreamObserver<Notification> wrapObserver(long userId, StreamObserver<Notification> original) {
        return new StreamObserver<>() {
            @Override
            public void onNext(Notification value) {
                original.onNext(value);
            }

            @Override
            public void onError(Throwable t) {
                logger.warn("Client {} stream error: {}", userId, t.getMessage());
                removeObserver(userId);
            }

            @Override
            public void onCompleted() {
                logger.info("Client {} closed stream", userId);
                removeObserver(userId);
            }
        };
    }

    @Override
    public void signIn(SignInRequest request, StreamObserver<SignInResponse> responseObserver) {
        try {
            logger.debug("Client {} attempting to log in", request);
            UserType type = UserType.valueOf(request.getType().toString());
            UserDTO user = service.signIn(request.getUsername(), request.getPassword(), type);

            if (loggedClients.containsKey(user.getId())) {
                throw new ServerSideException("User already logged in");
            }

            var responseBuilder = SignInResponse.newBuilder();
            switch (type) {
                case Client -> responseBuilder.setClient(ProtoMappers.toProto((ClientDTO) user));
                case StockOperator -> responseBuilder.setStockOperator(ProtoMappers.toProto((StockOperatorDTO) user));
                case Admin -> responseBuilder.setAdmin(ProtoMappers.toProto((AdminDTO) user));
                default -> throw new ServerSideException("Invalid type");
            }
            SignInResponse response = responseBuilder.build();

            logger.debug("Client logged in: {}", response);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ServerSideException ex) {
            responseObserver.onError(Status.ABORTED.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void subscribe(SubscribeRequest request, StreamObserver<Notification> responseObserver) {
        logger.debug("Client {} subscribed", request.getId());
        addObserver(request.getId(), wrapObserver(request.getId(), responseObserver));
    }

    private void notifyObservers(Notification notification) {
        for(Map.Entry<Long, StreamObserver<Notification>> client : loggedClients.entrySet()){
            final Long userId = client.getKey();
            final StreamObserver<Notification> observer = client.getValue();
            executor.execute(() -> {
                synchronized (observer) {
                    try {
                        logger.debug("Notifying client [{}] with {}", userId, notification);
                        try {
                            observer.onNext(notification);
                        } catch (Exception e) {
                            logger.warn("Client disconnected, removing observer.");
                            removeObserver(userId);
                        }
                    } catch (ServerSideException e) {
                        logger.error("Error notifying client [{}]", userId);
                    }
                }
            });
        }
    }

    private void notifyClients(){
        Notification notification = Notification.newBuilder()
                .setType(NotificationType.ClientNotification)
                .build();

        notifyObservers(notification);
    }

    private void notifyStockOperators(Long id){
        Notification notification = Notification.newBuilder()
                .setType(NotificationType.StockOperatorNotification)
                .setId(id)
                .build();

        notifyObservers(notification);
    }

    private void notifyAdmins() {
        Notification notification = Notification.newBuilder()
                .setType(NotificationType.AdminNotification)
                .build();

        notifyObservers(notification);
    }

    private void notifyDeleteTermination(Long id) {
        Notification notification = Notification.newBuilder()
                .setType(NotificationType.TerminateSessionDeleteNotification)
                .setId(id)
                .build();

        notifyObservers(notification);
    }

    private void notifyUpdateTermination(Long id) {
        Notification notification = Notification.newBuilder()
                .setType(NotificationType.TerminateSessionUpdateNotification)
                .setId(id)
                .build();

        notifyObservers(notification);
    }

    @Override
    public void signUp(SignUpRequest request, StreamObserver<SignUpResponse> responseObserver) {
        try {
            logger.debug("Client {} attempting to sign up", request);
            ClientDTO user = service.signUp(request.getUsername(), request.getPassword(), request.getName(), request.getCnp(), request.getTelephoneNumber(), request.getAddress());

            SignUpResponse response = SignUpResponse.newBuilder()
                    .setClient(ProtoMappers.toProto(user))
                    .build();
            logger.debug("Client sign up: {}", response);

            notifyAdmins();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ServerSideException ex) {
            responseObserver.onError(Status.ABORTED.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAllUsers(Empty request, StreamObserver<GetAllUsersResponse> responseObserver) {
        try {
            logger.debug("Client {} attempting to get all users", request);
            var responseBuilder = GetAllUsersResponse.newBuilder();
            var usersMap = service.getAllUsers();

            if (usersMap.containsKey(ClientDTO.class)) {
                responseBuilder.addAllClients(StreamSupport.stream(usersMap.get(ClientDTO.class).spliterator(), false)
                        .map(ClientDTO.class::cast)
                        .map(ProtoMappers::toProto)
                        .toList());
            } else {
                throw new ServerSideException("ClientDTO not found in response");
            }

            if (usersMap.containsKey(StockOperatorDTO.class)) {
                responseBuilder.addAllStockOperators(StreamSupport.stream(usersMap.get(StockOperatorDTO.class).spliterator(), false)
                        .map(StockOperatorDTO.class::cast)
                        .map(ProtoMappers::toProto)
                        .toList());
            } else {
                throw new ServerSideException("StockOperatorDTO not found in response");
            }

            if (usersMap.containsKey(AdminDTO.class)) {
                responseBuilder.addAllAdmins(StreamSupport.stream(usersMap.get(AdminDTO.class).spliterator(), false)
                        .map(AdminDTO.class::cast)
                        .map(ProtoMappers::toProto)
                        .toList());
            } else {
                throw new ServerSideException("AdminDTO not found in response");
            }

            var response = responseBuilder.build();
            logger.debug("Client getting response: {}", response);
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ServerSideException ex) {
            responseObserver.onError(Status.ABORTED.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void addNewClient(AddNewClientRequest request, StreamObserver<Empty> responseObserver) {
        try {
            logger.debug("Client {} attempting to addNewClient", request);
            service.addNewClient(request.getUsername(), request.getPassword(), request.getName(), request.getCnp(), request.getTelephoneNumber(), request.getAddress());

            logger.debug("Client saved");

            notifyAdmins();

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (ServerSideException ex) {
            responseObserver.onError(Status.ABORTED.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void addNewStockOperator(AddNewStockOperatorRequest request, StreamObserver<Empty> responseObserver) {
        try {
            logger.debug("Client {} attempting to addNewStockOperator", request);
            service.addNewStockOperator(request.getUsername(), request.getPassword(), request.getCompany());

            logger.debug("StockOperator saved");

            notifyAdmins();

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (ServerSideException ex) {
            responseObserver.onError(Status.ABORTED.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deleteUser(DeleteUserRequest request, StreamObserver<DeleteUserResponse> responseObserver) {
        try {
            logger.debug("Client {} attempting to delete user", request);
            var type = UserType.valueOf(request.getType().toString());
            UserDTO deletedUser = service.deleteUser(request.getId(), type);

            logger.debug("User {} deleted", request.getId());

            notifyAdmins();
            notifyDeleteTermination(deletedUser.getId());

            var response = DeleteUserResponse.newBuilder()
                    .setUser(ProtoMappers.toProto(deletedUser))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ServerSideException ex) {
            responseObserver.onError(Status.ABORTED.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UpdateUserResponse> responseObserver) {
        try {
            logger.debug("Client {} attempting to update user", request);
            var type = UserType.valueOf(request.getType().toString());
            UserDTO updatedUser = service.updateUser(request.getId(), request.getNewUsername(), type);

            logger.debug("User {} updated", request.getId());

            notifyAdmins();
            notifyUpdateTermination(updatedUser.getId());

            var response = UpdateUserResponse.newBuilder()
                    .setUser(ProtoMappers.toProto(updatedUser))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ServerSideException ex) {
            responseObserver.onError(Status.ABORTED.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAllGames(GetAllGamesRequest request, StreamObserver<GetAllGamesResponse> responseObserver) {
        logger.debug("Stock operator {} attempting to getAllGames", request);
        Iterable<GameDTO> games = service.getAllGames(request.getId());
        GetAllGamesResponse response = GetAllGamesResponse.newBuilder()
                .addAllGames(StreamSupport.stream(games.spliterator(), false).map(ProtoMappers::toProto).toList())
                .build();

        logger.debug("Stock operator getting response: {}", response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addNewGame(AddNewGameRequest request, StreamObserver<Empty> responseObserver) {
        try {
            logger.debug("Client {} attempting to addNewGame", request);
            service.addNewGame(request.getName(), request.getGenre(), request.getPlatform(), BigDecimal.valueOf(request.getPrice()), request.getIdStockOperator());
            logger.debug("Game added");

            notifyStockOperators(request.getIdStockOperator());
            notifyClients();

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (ServerSideException ex) {
            responseObserver.onError(Status.ABORTED.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deleteGame(DeleteGameRequest request, StreamObserver<DeleteGameResponse> responseObserver) {
        try {
            logger.debug("Stock operator {} attempting to delete game", request);
            GameDTO deletedGame = service.deleteGame(request.getId());

            logger.debug("Game {} deleted", request.getId());

            notifyStockOperators(deletedGame.stockOperatorId());
            notifyClients();

            var response = DeleteGameResponse.newBuilder()
                    .setGame(ProtoMappers.toProto(deletedGame))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ServerSideException ex) {
            responseObserver.onError(Status.ABORTED.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void updateGame(UpdateGameRequest request, StreamObserver<UpdateGameResponse> responseObserver) {
        try {
            logger.debug("Stock operator {} attempting to update game", request);
            GameDTO updatedGame = service.updateGame(request.getId(), request.getNewName(), request.getNewGenre(), request.getNewPlatform(), BigDecimal.valueOf(request.getNewPrice()));

            logger.debug("Game {} updated", request.getId());

            notifyStockOperators(updatedGame.stockOperatorId());
            notifyClients();

            var response = UpdateGameResponse.newBuilder()
                    .setGame(ProtoMappers.toProto(updatedGame))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ServerSideException ex) {
            responseObserver.onError(Status.ABORTED.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void logout(LogoutRequest request, StreamObserver<Empty> responseObserver) {
        StreamObserver<Notification> client = removeObserver(request.getId());

        if(client != null){
            try {
                client.onCompleted();
                logger.debug("User [{}] logged out and stream closed.", request.getId());
            } catch (Exception e) {
                logger.warn("Failed to close stream", e);
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        }
        else {
            responseObserver.onError(Status.CANCELLED.withDescription("User " + request.getId() + " not logged in").asRuntimeException());
        }
    }
}
