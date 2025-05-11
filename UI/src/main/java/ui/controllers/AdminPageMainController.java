package ui.controllers;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
import ui.utils.MessageAlert;
import ui.utils.ObserverManager;
import ui.viewItem.AdminUsersViewItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private Button addClient, addStockOperator, delete;

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

    private void createInputBindings() {
        BooleanBinding clientFieldsNotEmpty = name.textProperty().isNotEmpty()
                .or(cnp.textProperty().isNotEmpty())
                .or(telephoneNumber.textProperty().isNotEmpty())
                .or(address.textProperty().isNotEmpty());
        company.disableProperty().bind(clientFieldsNotEmpty);
        addStockOperator.disableProperty().bind(clientFieldsNotEmpty);

        BooleanBinding stockOperatorFieldsNotEmpty = company.textProperty().isNotEmpty();
        name.disableProperty().bind(stockOperatorFieldsNotEmpty);
        cnp.disableProperty().bind(stockOperatorFieldsNotEmpty);
        telephoneNumber.disableProperty().bind(stockOperatorFieldsNotEmpty);
        address.disableProperty().bind(stockOperatorFieldsNotEmpty);
        addClient.disableProperty().bind(stockOperatorFieldsNotEmpty);

        delete.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());
    }

    private void initTable() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        userTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
        table.setItems(userList);

        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        table.setEditable(true);
        usernameColumn.setEditable(true);
        idColumn.setEditable(false);
        userTypeColumn.setEditable(false);

        usernameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        usernameColumn.setOnEditCommit(event -> {
            AdminUsersViewItem userItem = event.getRowValue();
            String newUsername = event.getNewValue();

            if (newUsername != null && !newUsername.trim().isEmpty()) {
                userItem.setUsername(newUsername.trim());

                try {
                    logger.debug("Edited value: {}", newUsername);
                    service.updateUser(userItem.getId(), userItem.getUsername(), userItem.getUserType());
                } catch (ClientSideException e) {
                    MessageAlert.showError(stage, e.getMessage());
                    table.refresh();
                }
            } else {
                table.refresh();
            }
        });
    }

    @FXML
    private void initialize() {
        stage.setOnCloseRequest(event -> {
            service.logout(user.getId());
            logger.debug("Closing application");
            System.exit(0);
        });

        createInputBindings();
        initTable();
        setAdminUserList();
    }

    @FXML
    private void handleAddClient(ActionEvent event) {
        try {
            String username = this.username.getText().trim();
            String password = this.password.getText().trim();
            String name = this.name.getText().trim();
            String cnp = this.cnp.getText().trim();
            String telephone = this.telephoneNumber.getText().trim();
            String address = this.address.getText().trim();

            service.addNewClient(username, password, name, cnp, telephone, address);
            MessageAlert.showMessage(stage, "Confirmation", "New client added");
        } catch (ClientSideException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    @FXML
    private void handleAddStockOperator(ActionEvent event) {
        try {
            String username = this.username.getText().trim();
            String password = this.password.getText().trim();
            String company = this.company.getText().trim();

            service.addNewStockOperator(username, password, company);
            MessageAlert.showMessage(stage, "Confirmation", "New stock operator added");
        } catch (ClientSideException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        AdminUsersViewItem selectedUser = table.getSelectionModel().getSelectedItem();
        logger.debug("Selected user: {}", selectedUser);

        if (selectedUser != null) {
            if(Objects.equals(selectedUser.getId(), user.getId())) {
                MessageAlert.showError(stage, "You cannot delete your user");
                return;
            }
            service.deleteUser(selectedUser.getId(), selectedUser.getUserType());
            table.getSelectionModel().clearSelection();
        }
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

    public void updateAdminUserList() {
        Platform.runLater(this::setAdminUserList);
    }
}
