package repository.interfaces;

import model.Game;
import model.dto.GameDTO;

public interface IGameRepository extends IRepository<Long, Game, GameDTO>{

    Iterable<GameDTO> findByStockOperator(Long id);

    Iterable<GameDTO> getAllAvailableGames();
}
