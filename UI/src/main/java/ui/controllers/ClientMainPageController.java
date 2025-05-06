package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.User;
import service.Service;
import ui.MainApplication;
import ui.View;

public class ClientMainPageController implements IController {

    private Service service;
    private Stage stage;
    private User user;

    @FXML
    private Label usernameLabel;

    public ClientMainPageController(Service service, Stage stage, User user) {
        this.service = service;
        this.stage = stage;
        this.user = user;
    }

    @FXML
    private void initialize() {
        usernameLabel.setText(user.getUsername());
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.SIGN_IN.path));
            fxmlLoader.setControllerFactory(c -> new SignInController(service, stage));

            Pane root = fxmlLoader.load();
            stage.setScene(new Scene(root));

            root.requestFocus();
            stage.setResizable(false);
            stage.setTitle(View.SIGN_IN.title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
