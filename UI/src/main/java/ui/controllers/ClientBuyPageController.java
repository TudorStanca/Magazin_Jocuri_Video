package ui.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.StarRating;
import model.dto.ClientDTO;
import model.dto.GameDTO;
import model.dto.OwnedGameDTO;
import model.dto.ReviewDTO;
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
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ClientBuyPageController implements IController {

    private IServices service;
    private Stage stage;
    private ClientDTO user;

    private static Logger logger = LogManager.getLogger(ClientMainPageController.class);

    private ObservableList<GameViewItem> availableGameList = FXCollections.observableArrayList();
    private ObservableList<GameViewItem> cartGameList = FXCollections.observableArrayList();

    @FXML
    private TableView<GameViewItem> availableGames, cartGames;

    @FXML
    private TableColumn<GameViewItem, String> availableGameName, availableGameGenre, availableGamePlatform, cartGameName, cartGameGenre, cartGamePlatform;

    @FXML
    private TableColumn<GameViewItem, BigDecimal> availableGamePrice, availableGameRating, cartGamePrice;

    @FXML
    private TableColumn<GameViewItem, Void> addButtonColumn = new TableColumn<>("Add"), deleteButtonColumn = new TableColumn<>("Remove");

    @FXML
    private TextField search;

    @FXML
    private Label totalPrice;

    public ClientBuyPageController(IServices service, Stage stage, ClientDTO user) {
        this.service = service;
        this.stage = stage;
        this.user = user;
    }

    private void initAvailableGamesTable() {
        availableGameName.setCellValueFactory(new PropertyValueFactory<>("name"));
        availableGameGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availableGamePlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        availableGamePrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        availableGameRating.setCellValueFactory(new PropertyValueFactory<>("starRating"));

        addButtonColumn.setCellFactory(col -> new TableCell<>() {
            private final Button addButton = new Button("Add");
            {
                addButton.setMaxWidth(Double.MAX_VALUE);
                addButton.setOnAction(e -> {
                    GameViewItem game = getTableView().getItems().get(getIndex());
                    try {
                        service.addGameToCart(user.getId(), game.getId(), Instant.now());
                    } catch (ClientSideException ex) {
                        MessageAlert.showError(stage, ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(addButton);
                }
            }
        });

        FilteredList<GameViewItem> filteredGames = new FilteredList<>(availableGameList, p -> true);
        search.textProperty().addListener((observable, oldValue, newValue) -> {
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

        availableGames.setItems(filteredGames);
    }

    private void initCartGamesTable() {
        cartGameName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cartGameGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        cartGamePlatform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        cartGamePrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        deleteButtonColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.setMaxWidth(Double.MAX_VALUE);
                deleteButton.setOnAction(e -> {
                    GameViewItem game = getTableView().getItems().get(getIndex());
                    try {
                        service.deleteGameFromCart(user.getId(), game.getId());
                    } catch (ClientSideException ex) {
                        MessageAlert.showError(stage, ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        cartGames.setItems(cartGameList);
    }

    @FXML
    private void initialize() {
        initAvailableGamesTable();
        initCartGamesTable();
        setAvailableGameList();
        setCartGameList();
    }

    private void setAvailableGameList() {
        Map<GameDTO, BigDecimal> gameToAvgRating = StreamSupport.stream(service.getAllAvailableGames(user.getId()).spliterator(), false)
                .collect(Collectors.toMap(
                        Function.identity(),
                        game -> {
                            List<ReviewDTO> reviews = Optional.ofNullable(game.reviews())
                                    .orElse(Collections.emptyList());

                            List<Integer> values = reviews.stream()
                                    .filter(r -> r.starRating() != null)
                                    .map(r -> r.starRating().getValue())
                                    .toList();

                            if (values.isEmpty()) {
                                return BigDecimal.ZERO;
                            }

                            BigDecimal sum = values.stream()
                                    .map(BigDecimal::valueOf)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            return sum.divide(
                                    BigDecimal.valueOf(values.size()),
                                    2,
                                    RoundingMode.HALF_UP
                            );
                        }
                ));

        availableGameList.setAll(gameToAvgRating.entrySet().stream()
                .map(el -> new GameViewItem(el.getKey().id(), el.getKey().name(), el.getKey().genre(), el.getKey().platform(), el.getKey().price(), el.getValue()))
                .toList());
    }

    private void setCartGameList() {
        List<GameViewItem> list = StreamSupport.stream(service.getAllGamesInCart(user.getId()).spliterator(), false)
                .map(c -> new GameViewItem(c.game().id(), c.game().name(), c.game().genre(), c.game().platform(), c.game().price()))
                .toList();
        cartGameList.setAll(list);
        Platform.runLater(() -> {
            totalPrice.setText("Total price: " + list.stream().map(GameViewItem::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add));
        });
    }

    public void updateAvailableGames() {
        setAvailableGameList();
    }

    public void updateCartGames() {
        setCartGameList();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.CLIENT_MAIN.path));
            fxmlLoader.setControllerFactory(c -> new ClientMainPageController(service, stage, user));

            Pane root = fxmlLoader.load();
            stage.getScene().setRoot(root);

            root.requestFocus();
            stage.setResizable(false);
            stage.setTitle(View.CLIENT_MAIN.title);

            ObserverManager.getInstance().setCurrentController(fxmlLoader.getController());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBuy(ActionEvent event) {
        try {
            if (cartGameList.isEmpty()) {
                MessageAlert.showError(stage, "No cart games available");
                return;
            }

            for (var el : cartGameList) {
                service.addGameToOwnedGames(user.getId(), el.getId());
            }

            MessageAlert.showMessage(stage, "Confirmation", "New owned games added");
        } catch (ClassCastException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }
}
