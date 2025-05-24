package ui.controllers;

import javafx.stage.Stage;
import model.dto.ClientDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IServices;

public class ClientReviewPageController implements IController {

    private IServices service;
    private Stage stage;
    private ClientDTO user;

    private static Logger logger = LogManager.getLogger(ClientMainPageController.class);

    public ClientReviewPageController(IServices service, Stage stage, ClientDTO user) {
        this.service = service;
        this.stage = stage;
        this.user = user;
    }
}
