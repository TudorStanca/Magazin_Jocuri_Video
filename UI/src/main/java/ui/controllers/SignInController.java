package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.dto.ClientDTO;
import model.exception.ClientSideException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IServices;
import ui.MainApplication;
import ui.View;
import ui.utils.MessageAlert;
import ui.utils.ObserverManager;

public class SignInController implements IController {

    private final IServices service;
    private final Stage stage;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    private static Logger logger = LogManager.getLogger(SignInController.class);

    public SignInController(IServices service, Stage stage) {
        this.stage = stage;
        this.service = service;
    }

    @FXML
    private void handleSignIn(ActionEvent event) {
        try {
            ClientDTO user = service.signIn(username.getText(), password.getText());

            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.CLIENT_MAIN.path));
            fxmlLoader.setControllerFactory(c -> new ClientMainPageController(service, stage, user));

            Pane root = fxmlLoader.load();
            stage.setScene(new Scene(root));

            root.requestFocus();
            stage.setResizable(false);
            stage.setTitle(View.CLIENT_MAIN.title);

            ObserverManager.getInstance().setCurrentController(fxmlLoader.getController());

        } catch (ClientSideException ex) {
            MessageAlert.showError(stage, ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleSignUpHyperlink(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.SIGN_UP.path));
            fxmlLoader.setControllerFactory(c -> new SignUpController(service, stage));

            Pane root = fxmlLoader.load();
            stage.getScene().setRoot(root);

            root.requestFocus();
            stage.setResizable(false);
            stage.setTitle(View.SIGN_UP.title);

            ObserverManager.getInstance().setCurrentController(fxmlLoader.getController());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
