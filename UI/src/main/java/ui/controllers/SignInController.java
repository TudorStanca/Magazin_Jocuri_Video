package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.User;
import model.exceptions.MyException;
import service.Service;
import ui.MainApplication;
import ui.View;
import ui.utils.MessageAlert;

public class SignInController implements IController {

    private final Service service;
    private final Stage stage;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    public SignInController(Service service, Stage stage) {
        this.stage = stage;
        this.service = service;
    }

    @FXML
    private void handleSignIn(ActionEvent event) {
        try {
            User user = service.signIn(username.getText(), password.getText());

            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.CLIENT_MAIN.path));
            fxmlLoader.setControllerFactory(c -> new ClientMainPageController(service, stage, user));

            Pane root = fxmlLoader.load();
            stage.setScene(new Scene(root));

            root.requestFocus();
            stage.setResizable(false);
            stage.setTitle(View.CLIENT_MAIN.title);

        } catch (MyException ex) {
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
