package server;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import network.ServicesImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.*;
import repository.interfaces.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class StartServer {

    private static int defaultPort = 55555;

    private static Logger logger = LogManager.getLogger(StartServer.class);

    private static void start(Server server) throws IOException {
        server.start();
        logger.info("Server started, listening on {}", server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                stop(server);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    private static void stop(Server server) throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    private static void blockUntilShutdown(Server server) throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main (String[] args) throws Exception {
        logger.debug("Starting server");

        Properties serverProps = new Properties(System.getProperties());
        try {
            serverProps.load(new FileReader("src/main/resources/server.config"));
            logger.info("Server properties set {}", serverProps);
            System.setProperties(serverProps);
        } catch (IOException e) {
            logger.error("Cannot find server.config file {}", e);
            logger.debug("Looking ofr server.config in folder {}", (new File("src/main/resources/server.config").getAbsolutePath()));
            return;
        }

        IClientRepository client = new ClientRepository();
        IStockOperatorRepository stock = new StockOperatorRepository();
        IGameRepository game = new GameRepository();
        IReviewRepository review = new ReviewRepository();
        ICartRepository cart = new CartRepository();
        IOwnedGamesRepository owned = new OwnedGameRepository();

        ServerService service = new ServerService(client, stock, game, review, cart, owned);

        int serverPort = defaultPort;
        try {
            serverPort = Integer.parseInt(System.getProperty("server.port"));
        } catch (NumberFormatException e) {
            logger.error("Wrong port number: {}", e.getMessage());
            logger.debug("Using default port: {}", defaultPort);
        }
        logger.info("Using server port: {}", serverPort);

        Server server = Grpc.newServerBuilderForPort(serverPort, InsecureServerCredentials.create())
                .addService(new ServicesImpl(service))
                .build();

        start(server);
        blockUntilShutdown(server);
    }
}
