package ui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.dto.StockOperatorDTO;
import model.exception.ClientSideException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IServices;
import ui.MainApplication;
import ui.View;
import ui.utils.MessageAlert;
import ui.utils.ObserverManager;
import ui.viewItem.GameViewItem;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class StockOperatorMainPageController implements IController {

    private IServices service;
    private Stage stage;
    private StockOperatorDTO user;

    private static Logger logger = LogManager.getLogger(StockOperatorMainPageController.class);

    private ObservableList<GameViewItem> gameList = FXCollections.observableArrayList();

    @FXML
    private TextField name, genre, platform, price;

    @FXML
    private TableView<GameViewItem> table;

    @FXML
    private TableColumn<GameViewItem, String> nameColumn, genreColumn, platformColumn;

    @FXML
    private TableColumn<GameViewItem, BigDecimal> priceColumn;

    public StockOperatorMainPageController(IServices service, Stage stage, StockOperatorDTO user) {
        this.service = service;
        this.stage = stage;
        this.user = user;
    }

    private void initTable() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        platformColumn.setCellValueFactory(new PropertyValueFactory<>("platform"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        table.setItems(gameList);

        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        table.setEditable(true);
        nameColumn.setEditable(true);
        genreColumn.setEditable(true);
        platformColumn.setEditable(true);
        priceColumn.setEditable(true);

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        genreColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        platformColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        StringConverter<BigDecimal> bigDecimalConverter = new StringConverter<>() {
            @Override
            public String toString(BigDecimal object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public BigDecimal fromString(String string) {
                try {
                    return new BigDecimal(string);
                } catch (NumberFormatException e) {
                    return BigDecimal.ZERO;
                }
            }
        };

        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(bigDecimalConverter));

        nameColumn.setOnEditCommit(event -> {
            GameViewItem item = event.getRowValue();
            String newName = event.getNewValue();

            if(newName != null && !newName.trim().isEmpty()) {
                try{
                    logger.debug("Edited name value: {}", newName);
                    service.updateGame(item.getId(), newName, item.getGenre(), item.getPlatform(), item.getPrice());
                } catch (ClientSideException e) {
                    MessageAlert.showError(stage, e.getMessage());
                    table.refresh();
                }
            } else {
                table.refresh();
            }
        });

        genreColumn.setOnEditCommit(event -> {
            GameViewItem item = event.getRowValue();
            String newGenre = event.getNewValue();

            if(newGenre != null && !newGenre.trim().isEmpty()) {
                try{
                    logger.debug("Edited genre value: {}", newGenre);
                    service.updateGame(item.getId(), item.getName(), newGenre, item.getPlatform(), item.getPrice());
                } catch (ClientSideException e) {
                    MessageAlert.showError(stage, e.getMessage());
                    table.refresh();
                }
            } else {
                table.refresh();
            }
        });

        platformColumn.setOnEditCommit(event -> {
            GameViewItem item = event.getRowValue();
            String newPlatform = event.getNewValue();

            if(newPlatform != null && !newPlatform.trim().isEmpty()) {
                try{
                    logger.debug("Edited platform value: {}", newPlatform);
                    service.updateGame(item.getId(), item.getName(), item.getGenre(), newPlatform, item.getPrice());
                } catch (ClientSideException e) {
                    MessageAlert.showError(stage, e.getMessage());
                    table.refresh();
                }
            } else {
                table.refresh();
            }
        });

        priceColumn.setOnEditCommit(event -> {
            GameViewItem item = event.getRowValue();
            BigDecimal newPrice = event.getNewValue();

            if(newPrice != null) {
                try{
                    logger.debug("Edited price value: {}", newPrice);
                    service.updateGame(item.getId(), item.getName(), item.getGenre(), item.getPlatform(), newPrice);
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

        initTable();
        setGameList();
    }

    @FXML
    private void handleAddGame(ActionEvent event) {
        try {
            String name = this.name.getText().trim();
            String genre = this.genre.getText().trim();
            String platform = this.platform.getText().trim();
            String priceString = this.price.getText().trim();
            BigDecimal price = new BigDecimal(priceString);

            service.addNewGame(name, genre, platform, price, user.getId());
            MessageAlert.showMessage(stage, "Confirmation", "New game added");
        } catch (NumberFormatException e) {
            MessageAlert.showError(stage, "Price format error");
        } catch (ClassCastException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    @FXML
    private void handleDeleteGame(ActionEvent event) {
        try {
            GameViewItem selectedItem = table.getSelectionModel().getSelectedItem();
            logger.debug("Selected game: {}", selectedItem);

            if(selectedItem != null) {
                service.deleteGame(selectedItem.getId());
                table.getSelectionModel().clearSelection();
            }
        } catch (ClientSideException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    private void setGameList() {
        gameList.setAll(StreamSupport.stream(service.getAllGames(user.getId()).spliterator(), false)
                .map(g -> new GameViewItem(g.id(), g.name(), g.genre(), g.platform(), g.price())).toList());
    }

    public void updateGameList(Long id) {
        if(user.getId().equals(id)) {
            Platform.runLater(this::setGameList);
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        terminateSession();
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
