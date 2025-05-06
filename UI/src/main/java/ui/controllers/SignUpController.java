package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import service.Service;
import ui.MainApplication;
import ui.View;
import ui.utils.MessageAlert;

public class SignUpController implements IController {

    private Service service;
    private Stage stage;

    @FXML
    private TextField username, name, cnp, telephoneNumber, address;

    @FXML
    private PasswordField password;

    public SignUpController(Service service, Stage stage) {
        this.service = service;
        this.stage = stage;
    }

    private void switchToSignIn() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.SIGN_IN.path));
            fxmlLoader.setControllerFactory(c -> new SignInController(service, stage));

            Pane root = fxmlLoader.load();
            stage.getScene().setRoot(root);

            root.requestFocus();
            stage.setResizable(false);
            stage.setTitle(View.SIGN_IN.title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSignUp(ActionEvent event) {
        try{
            String username = this.username.getText();
            String password = this.password.getText();
            String name = this.name.getText();
            String cnp = this.cnp.getText();
            String telephone = this.telephoneNumber.getText();
            String address = this.address.getText();

            service.signUp(username, password, name, cnp, telephone, address);

            MessageAlert.showMessage(stage, "Information", "Client added successfully!");
            switchToSignIn();
        } catch (Exception e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    @FXML
    private void handleSignInHyperlink(ActionEvent event) {
        switchToSignIn();
    }
}
