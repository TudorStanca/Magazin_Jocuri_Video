package ui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ClientReviewPageController implements IController {

    private IServices service;
    private Stage stage;
    private ClientDTO user;

    private static Logger logger = LogManager.getLogger(ClientMainPageController.class);

    private ObservableList<GameViewItem> gameList = FXCollections.observableArrayList();

    @FXML
    private TableView<GameViewItem> table;

    @FXML
    private TableColumn<GameViewItem, String> name, genre, platform;

    @FXML
    private TableColumn<GameViewItem, BigDecimal> price, rating;

    @FXML
    private TextField search;

    @FXML
    private TextArea area;

    @FXML
    private ComboBox<StarRating> comboBox;

    public ClientReviewPageController(IServices service, Stage stage, ClientDTO user) {
        this.service = service;
        this.stage = stage;
        this.user = user;
    }

    private void initTable() {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        genre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        platform.setCellValueFactory(new PropertyValueFactory<>("platform"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        rating.setCellValueFactory(new PropertyValueFactory<>("starRating"));

        FilteredList<GameViewItem> filteredGames = new FilteredList<>(gameList, g -> true);
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

        table.setItems(filteredGames);
    }

    private void setGameList() {
        Map<OwnedGameDTO, BigDecimal> gameToAvgRating = StreamSupport.stream(service.getAllOwnedGames(user.getId()).spliterator(), false)
                .collect(Collectors.toMap(
                        Function.identity(),
                        game -> {
                            List<ReviewDTO> reviews = Optional.ofNullable(game.game().reviews())
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
        gameList.setAll(gameToAvgRating.entrySet().stream()
                .map(el -> new GameViewItem(el.getKey().game().id(), el.getKey().game().name(), el.getKey().game().genre(), el.getKey().game().platform(), el.getKey().game().price(), el.getValue()))
                .toList());
    }

    @FXML
    private void initialize() {
        comboBox.setItems(FXCollections.observableArrayList(StarRating.values()));
        initTable();
        setGameList();
    }

    @FXML
    private void handleBack(){
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
    private void handleAddReview(){
        try {
            StarRating stars = comboBox.getSelectionModel().getSelectedItem();
            String description = this.area.getText().trim();
            GameViewItem selectedGame = table.getSelectionModel().getSelectedItem();

            if (selectedGame == null) {
                MessageAlert.showError(stage, "You must select a game to review");
                return;
            }
            if(stars == null) {
                MessageAlert.showError(stage, "You must select a star rating");
                return;
            }

            service.addNewReview(stars, description, selectedGame.getId());
            table.getSelectionModel().clearSelection();
            MessageAlert.showMessage(stage, "Confirmation", "New review added");
        } catch (ClassCastException e) {
            MessageAlert.showError(stage, e.getMessage());
        }
    }

    public void updateGameList(Long id) {
        if (gameList.stream().anyMatch(el -> el.getId().equals(id))) {
            logger.debug("s-a ajung aici");
            setGameList();
        }
    }
}
