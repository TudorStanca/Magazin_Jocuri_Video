package ui;

import network.ClientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IServices;
import ui.controllers.SignInController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import ui.utils.ObserverManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MainApplication extends Application {

    private static int defaultPort = 55555;
    private static String defaultHost = "localhost";

    private static Logger logger = LogManager.getLogger(MainApplication.class);

    private void initView(IServices service, Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.SIGN_IN.path));
        fxmlLoader.setControllerFactory(c -> new SignInController(service, stage));

        Pane root = fxmlLoader.load();
        stage.setScene(new Scene(root));

        root.requestFocus();
        stage.setResizable(false);
        stage.setTitle(View.SIGN_IN.title);
        ObserverManager.getInstance().setCurrentController(fxmlLoader.getController());

        stage.show();
    }

    @Override
    public void start(Stage stage) throws IOException {
        logger.debug("Starting client");

        Properties clientProps = new Properties(System.getProperties());
        try{
            clientProps.load(new FileReader("src/main/resources/client.config"));
            logger.info("Client properties set {}", clientProps);
            System.setProperties(clientProps);
        } catch (IOException e) {
            logger.error("Cannot find client.config file {}", e);
            logger.debug("Looking ofr client.config in folder {}", (new File("src/main/resources/client.config").getAbsolutePath()));
            return;
        }

        String serverIP = System.getProperty("server.host", defaultHost);
        int serverPort = defaultPort;

        try{
            serverPort = Integer.parseInt(System.getProperty("server.port"));
        } catch (NumberFormatException e) {
            logger.error("Wrong port number {}", e.getMessage());
            logger.debug("Using default port {}", defaultPort);
        }
        logger.info("Using server IP {}", serverIP);
        logger.info("Using server port {}", serverPort);

        IServices server = new ClientService(serverIP + ":" + serverPort, ObserverManager.getInstance());

        initView(server, stage);
    }

    public static void main(String[] args) {
        launch();
    }
}