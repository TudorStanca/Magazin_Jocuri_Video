package ui.controllers;

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
import model.dto.ClientDTO;
import model.dto.GameDTO;
import model.dto.OwnedGameDTO;
import model.dto.ReviewDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IServices;
import ui.MainApplication;
import ui.View;
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
                    service.addGameToCart(user.getId(), game.getId(), Instant.now());
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

    @FXML
    private void initialize() {
        initAvailableGamesTable();
        setAvailableGameList();
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

    public void updateAvailableGames() {
        setAvailableGameList();
    }

    public void updateCartGames() {
        logger.debug("Am fost notificat");
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

    }
}
