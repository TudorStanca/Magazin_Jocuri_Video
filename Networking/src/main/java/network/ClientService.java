package network;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import model.SignInType;
import model.dto.ClientDTO;
import model.dto.UserDTO;
import model.exception.ClientSideException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IObserver;
import services.IServices;

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
                channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
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
                //TODO observer
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
    public UserDTO signIn(String username, String password, SignInType signInType) throws ClientSideException {
        try {
            createConnection();
            SignInRequest signInRequest = SignInRequest.newBuilder()
                    .setUsername(username)
                    .setPassword(password)
                    .setType(SignInRequest.SignInType.valueOf(signInType.toString()))
                    .build();
            SignInResponse signInResponse = blockingStub.signIn(signInRequest);
            UserDTO user;
            switch (signInType){
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
    public void logout(Long id) throws ClientSideException {
        try {
            LogoutRequest request = LogoutRequest.newBuilder().setId(id).build();
            blockingStub.logout(request);
        } catch (StatusRuntimeException ex) {
            throw new ClientSideException(ex.getMessage());
        }
    }
}
