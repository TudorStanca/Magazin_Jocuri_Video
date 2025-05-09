package ui.controllers;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import model.dto.StockOperatorDTO;
import services.IServices;

public class StockOperatorMainPageController implements IController {

    private IServices service;
    private Stage stage;
    private StockOperatorDTO user;

    public StockOperatorMainPageController(IServices service, Stage stage, StockOperatorDTO user) {
        this.service = service;
        this.stage = stage;
        this.user = user;
    }
}
