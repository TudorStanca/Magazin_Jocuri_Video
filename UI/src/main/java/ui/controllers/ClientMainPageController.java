package ui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.dto.ClientDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IServices;
import ui.MainApplication;
import ui.View;
import ui.utils.MessageAlert;
import ui.utils.ObserverManager;

import java.util.Objects;

public class ClientMainPageController implements IController {

    private IServices service;
    private Stage stage;
    private ClientDTO user;

    private static Logger logger = LogManager.getLogger(ClientMainPageController.class);

    @FXML
    private Label usernameLabel;

    public ClientMainPageController(IServices service, Stage stage, ClientDTO user) {
        this.service = service;
        this.stage = stage;
        this.user = user;
    }

    @FXML
    private void initialize() {
        stage.setOnCloseRequest(event -> {
            service.logout(user.getId());
            logger.debug("Closing application");
            System.exit(0);
        });

        usernameLabel.setText(user.getUsername());
    }

    private void terminateSession() {
        try {
            service.logout(user.getId());

            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.SIGN_IN.path));
            fxmlLoader.setControllerFactory(c -> new SignInController(service, stage));

            Pane root = fxmlLoader.load();
            stage.setScene(new Scene(root));

            root.requestFocus();
            stage.setResizable(false);
            stage.setTitle(View.SIGN_IN.title);

            ObserverManager.getInstance().setCurrentController(fxmlLoader.getController());
            stage.setOnCloseRequest(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        terminateSession();
    }

    public void terminateSessionUserDeleted(Long id) {
        if(Objects.equals(user.getId(), id)) {
            Platform.runLater(() -> {
                terminateSession();
                MessageAlert.showMessage(stage, "Session Terminated", "User has been deleted by admin");
            });
        }
    }

    public void terminateSessionUserUpdated(Long id) {
        if(Objects.equals(user.getId(), id)) {
            Platform.runLater(() -> {
                terminateSession();
                MessageAlert.showMessage(stage, "Session Terminated", "User has been updated by admin, please sign in again");
            });
        }
    }
}
