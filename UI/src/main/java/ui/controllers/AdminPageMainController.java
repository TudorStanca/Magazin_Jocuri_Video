package ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.User;
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
import ui.utils.ObserverManager;
import ui.viewItem.AdminUsersViewItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class AdminPageMainController implements IController {

    private IServices service;
    private Stage stage;
    private AdminDTO user;

    private static Logger logger = LogManager.getLogger(AdminPageMainController.class);

    private ObservableList<AdminUsersViewItem> userList = FXCollections.observableArrayList();

    @FXML
    private TextField username, name, cnp, telephoneNumber, address, company;

    @FXML
    private PasswordField password;

    @FXML
    private TableView<AdminUsersViewItem> table;

    @FXML
    private TableColumn<AdminUsersViewItem, Integer> idColumn;

    @FXML
    private TableColumn<AdminUsersViewItem, String> usernameColumn;

    @FXML
    private TableColumn<AdminUsersViewItem, UserType> userTypeColumn;

    public AdminPageMainController(IServices service, Stage stage, AdminDTO user) {
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

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
        table.setItems(userList);

        setAdminUserList();
    }

    @FXML
    private void handleAddClient(ActionEvent event) {

    }

    @FXML
    private void handleAddStockOperator(ActionEvent event) {

    }

    @FXML
    private void handleDelete(ActionEvent event) {

    }

    @FXML
    private void handleLogout(ActionEvent event) {
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

    private void setAdminUserList() {
        var users = service.getAllUsers();
        userList.clear();
        if (users.containsKey(ClientDTO.class)) {
            userList.addAll(StreamSupport.stream(users.get(ClientDTO.class).spliterator(), false)
                    .map(c -> new AdminUsersViewItem(c.getId(), c.getUsername(), UserType.Client))
                    .toList());
        } else {
            throw new ClientSideException("ClientDTO not found in response");
        }
        if (users.containsKey(StockOperatorDTO.class)) {
            userList.addAll(StreamSupport.stream(users.get(StockOperatorDTO.class).spliterator(), false)
                    .map(c -> new AdminUsersViewItem(c.getId(), c.getUsername(), UserType.StockOperator))
                    .toList());
        } else {
            throw new ClientSideException("StockOperatorDTO not found in response");
        }
        if (users.containsKey(AdminDTO.class)) {
            userList.addAll(StreamSupport.stream(users.get(AdminDTO.class).spliterator(), false)
                    .map(c -> new AdminUsersViewItem(c.getId(), c.getUsername(), UserType.Admin))
                    .toList());
        } else {
            throw new ClientSideException("AdminDTO not found in response");
        }
    }
}
