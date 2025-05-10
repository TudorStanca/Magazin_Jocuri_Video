package ui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.UserType;
import model.dto.AdminDTO;
import model.dto.ClientDTO;
import model.dto.StockOperatorDTO;
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

    @FXML
    private ToggleButton stockOperator, admin;

    private ToggleGroup loginToggleGroup = new ToggleGroup();

    private static Logger logger = LogManager.getLogger(SignInController.class);

    public SignInController(IServices service, Stage stage) {
        this.stage = stage;
        this.service = service;
    }

    @FXML
    private void initialize() {
        stockOperator.setToggleGroup(loginToggleGroup);
        admin.setToggleGroup(loginToggleGroup);

        stockOperator.setOnAction(event -> {
            if(stockOperator.isSelected()) {
                loginToggleGroup.selectToggle(stockOperator);
            } else {
                loginToggleGroup.selectToggle(null);
            }
        });

        admin.setOnAction(event -> {
            if(admin.isSelected()) {
                loginToggleGroup.selectToggle(admin);
            } else {
                loginToggleGroup.selectToggle(null);
            }
        });
    }

    @FXML
    private void handleSignIn(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader;
            String username = this.username.getText();
            String password = this.password.getText();
            if(loginToggleGroup.getSelectedToggle() != null) {
                if(loginToggleGroup.getSelectedToggle() == stockOperator) {
                    var user = service.signIn(username, password, UserType.StockOperator);

                    fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.STOCK_OPERATOR_MAIN.path));
                    fxmlLoader.setControllerFactory(c -> new StockOperatorMainPageController(service, stage, (StockOperatorDTO) user));
                    stage.setTitle(View.STOCK_OPERATOR_MAIN.title);
                } else {
                    var user = service.signIn(username, password, UserType.Admin);

                    fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.ADMIN_MAIN.path));
                    fxmlLoader.setControllerFactory(c -> new AdminPageMainController(service, stage, (AdminDTO) user));
                    stage.setTitle(View.ADMIN_MAIN.title);
                }
            } else {
                var user = service.signIn(username, password, UserType.Client);

                fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.CLIENT_MAIN.path));
                fxmlLoader.setControllerFactory(c -> new ClientMainPageController(service, stage, (ClientDTO) user));
                stage.setTitle(View.CLIENT_MAIN.title);
            }
            Pane root = fxmlLoader.load();
            stage.setScene(new Scene(root));

            root.requestFocus();
            stage.setResizable(false);

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
