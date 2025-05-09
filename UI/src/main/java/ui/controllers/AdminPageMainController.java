package ui.controllers;

import javafx.stage.Stage;
import model.dto.AdminDTO;
import model.dto.StockOperatorDTO;
import services.IServices;

public class AdminPageMainController implements IController {

    private IServices service;
    private Stage stage;
    private AdminDTO user;

    public AdminPageMainController(IServices service, Stage stage, AdminDTO user) {
        this.service = service;
        this.stage = stage;
        this.user = user;
    }
}
