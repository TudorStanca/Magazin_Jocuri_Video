package ui;

import ui.controllers.SignInController;
import service.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import repository.*;
import repository.interfaces.*;

import java.io.IOException;

public class MainApplication extends Application {
    private void initView(Service service, Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(View.SIGN_IN.path));
        fxmlLoader.setControllerFactory(c -> new SignInController(service, stage));

        Pane root = fxmlLoader.load();
        stage.setScene(new Scene(root));

        root.requestFocus();
        stage.setResizable(false);
        stage.setTitle(View.SIGN_IN.title);

        stage.show();
    }

    @Override
    public void start(Stage stage) throws IOException {
        IClientRepository client = new ClientRepository();
        IStockOperatorRepository stock = new StockOperatorRepository();
        IGameRepository game = new GameRepository();
        IReviewRepository review = new ReviewRepository();
        ICartRepository cart = new CartRepository();
        IOwnedGamesRepository owned = new OwnedGameRepository();

        Service service = new Service(client, stock, game, review, cart, owned);
        initView(service, stage);
    }

    public static void main(String[] args) {
        launch();
    }
}