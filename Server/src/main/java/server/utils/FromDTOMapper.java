package server.utils;

import model.Admin;
import model.Client;
import model.Game;
import model.StockOperator;
import model.dto.AdminDTO;
import model.dto.ClientDTO;
import model.dto.GameDTO;
import model.dto.StockOperatorDTO;
import model.exception.ServerSideException;
import repository.interfaces.IStockOperatorRepository;

public class FromDTOMapper {

    public static Client fromDTO(ClientDTO dto) {
        var client = new Client(
                dto.getUsername(),
                dto.getPassword(),
                dto.getSalt(),
                dto.getName(),
                dto.getCnp(),
                dto.getTelephoneNumber(),
                dto.getAddress()
        );
        client.setId(dto.getId());
        return client;
    }

    public static StockOperator fromDTO(StockOperatorDTO dto) {
        var stockOperator = new StockOperator(
                dto.getUsername(),
                dto.getPassword(),
                dto.getSalt(),
                dto.getCompany()
        );
        stockOperator.setId(dto.getId());
        return stockOperator;
    }

    public static Admin fromDTO(AdminDTO dto) {
        var admin = new Admin(
                dto.getUsername(),
                dto.getPassword(),
                dto.getSalt()
        );
        admin.setId(dto.getId());
        return admin;
    }

    public static Game fromDTO(GameDTO dto, IStockOperatorRepository repo) {
        var game = new Game(
                dto.name(),
                dto.genre(),
                dto.platform(),
                dto.price()
        );
        game.setId(dto.id());
        game.setStockOperator(fromDTO(repo.findById(dto.stockOperatorId()).orElseThrow(() -> new ServerSideException("Cannot find stock operator"))));
        return game;
    }
}
