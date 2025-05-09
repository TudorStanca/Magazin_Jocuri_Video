package repository.interfaces;

import model.StockOperator;
import model.dto.StockOperatorDTO;

import java.util.Optional;

public interface IStockOperatorRepository extends IRepository<Long, StockOperator, StockOperatorDTO> {
    Optional<StockOperatorDTO> findByUsername(String username);
}
