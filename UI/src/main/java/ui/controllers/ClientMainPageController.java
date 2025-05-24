package ui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Review;
import model.StarRating;
import model.dto.ClientDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IServices;
import ui.MainApplication;
import ui.View;
import ui.utils.MessageAlert;
import ui.utils.ObserverManager;
import ui.viewItem.GameViewItem;
import ui.viewItem.ReviewViewItem;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class ClientMainPageController implements IController {

    private IServices service;
    private Stage stage;
    private ClientDTO user;

    private static Logger logger = LogManager.getLogger(ClientMainPageController.class);

    private ObservableList<GameViewItem> availableGameList = FXCollections.observableArrayList();
    private ObservableList<GameViewItem> ownedGameList = FXCollections.observableArrayList();
    private ObservableList<ReviewViewItem> reviewList = FXCollections.observableArrayList();

    @FXML
    private TableView<GameViewItem> availableGamesTable, ownedGamesTable;

    @FXML
    private TableView<ReviewViewItem> reviewTable;

    @FXML
    private TableColumn<GameViewItem, String> availableGameName, availableGameGenre, availableGamePlatform, ownedGameName, ownedGameGenre, ownedGamePlatform;

    @FXML
    private TableColumn<GameViewItem, BigDecimal> availableGamePrice;

    @FXML
    private TableColumn<GameViewItem, Integer> ownedGamePrice;

    @FXML
    private TableColumn<ReviewViewItem, String> reviewDescription;

    @FXML
    private TableColumn<ReviewViewItem, StarRating> reviewStars;

    @FXML
    private TextField searchAvailableGames, searchOwnedGames;

    @FXML
    private Label usernameLabel;

    public ClientMainPageController(IServices service, Stage stage, ClientDTO user) {
        this.service = service;
        this.stage = stage;
        this.user = user;
    }

    private void initAvailableTable() {
        availableGameName.setCellValueFactory(new PropertyValueFactory<>("name"));
        availableGameGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availableGamePlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        availableGamePrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        FilteredList<GameViewItem> filteredGames = new FilteredList<>(availableGameList, p -> true);
        searchAvailableGames.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredGames.setPredicate(game -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return game.getName().toLowerCase().contains(lowerCaseFilter)
                        || game.getGenre().toLowerCase().contains(lowerCaseFilter)
                        || game.getPlatform().toLowerCase().contains(lowerCaseFilter);
            });
        });

        availableGamesTable.setItems(filteredGames);
    }

    private void initOwnedTable() {
        ownedGameName.setCellValueFactory(new PropertyValueFactory<>("name"));
        ownedGameGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        ownedGamePlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        ownedGamePrice.setCellValueFactory(new PropertyValueFactory<>("nrHours"));

        FilteredList<GameViewItem> filteredGames = new FilteredList<>(ownedGameList, p -> true);
        searchOwnedGames.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredGames.setPredicate(game -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return game.getName().toLowerCase().contains(lowerCaseFilter)
                       || game.getGenre().toLowerCase().contains(lowerCaseFilter)
                       || game.getPlatform().toLowerCase().contains(lowerCaseFilter);
            });
        });

        ownedGamesTable.setItems(filteredGames);
    }

    private void initReviewTable() {
        reviewStars.setCellValueFactory(new PropertyValueFactory<>("starRating"));
        reviewDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        reviewTable.setItems(reviewList);
    }

    private void setReviewTable() {
        availableGamesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setReviewList(newSelection.getId());
                ownedGamesTable.getSelectionModel().clearSelection();
            }
        });

        ownedGamesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                setReviewList(newSelection.getId());
                availableGamesTable.getSelectionModel().clearSelection();
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

        usernameLabel.setText(user.getUsername());

        initAvailableTable();
        initOwnedTable();
        initReviewTable();

        setAvailableGameList();
        setOwnedGameList();
        setReviewTable();
    }

    private void setAvailableGameList() {
        availableGameList.setAll(StreamSupport.stream(service.getAllAvailableGames().spliterator(), false)
                .map(g -> new GameViewItem(g.id(), g.name(), g.genre(), g.platform(), g.price())).toList());
    }

    private void setOwnedGameList() {
        ownedGameList.setAll(StreamSupport.stream(service.getAllOwnedGames(user.getId()).spliterator(), false)
                .map(g -> new GameViewItem(g.game().id(), g.game().name(), g.game().genre(), g.game().platform(), g.game().price(), g.nrHours())).toList());
    }

    private void setReviewList(Long id) {
        reviewList.setAll(StreamSupport.stream(service.getAllReviews(id).spliterator(), false)
                .map(r -> new ReviewViewItem(r.id(), r.starRating(), r.description())).toList());
    }

    @FXML
    private void handleBuy(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.CLIENT_BUY.path));
            fxmlLoader.setControllerFactory(c -> new ClientBuyPageController(service, stage, user));

            Pane root = fxmlLoader.load();
            stage.getScene().setRoot(root);

            root.requestFocus();
            stage.setResizable(false);
            stage.setTitle(View.CLIENT_BUY.title);

            ObserverManager.getInstance().setCurrentController(fxmlLoader.getController());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReview(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.CLIENT_REVIEW.path));
            fxmlLoader.setControllerFactory(c -> new ClientReviewPageController(service, stage, user));

            Pane root = fxmlLoader.load();
            stage.getScene().setRoot(root);

            root.requestFocus();
            stage.setResizable(false);
            stage.setTitle(View.CLIENT_REVIEW.title);

            ObserverManager.getInstance().setCurrentController(fxmlLoader.getController());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
