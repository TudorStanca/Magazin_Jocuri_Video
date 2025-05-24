package ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.dto.ClientDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IServices;
import ui.MainApplication;
import ui.View;
import ui.utils.ObserverManager;

public class ClientBuyPageController implements IController {

    private IServices service;
    private Stage stage;
    private ClientDTO user;

    private static Logger logger = LogManager.getLogger(ClientMainPageController.class);

    public ClientBuyPageController(IServices service, Stage stage, ClientDTO user) {
        this.service = service;
        this.stage = stage;
        this.user = user;
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
}
