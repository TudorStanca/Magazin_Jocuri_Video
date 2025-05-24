package repository.interfaces;

import model.OwnedGame;
import model.OwnedGameId;
import model.dto.OwnedGameDTO;

public interface IOwnedGamesRepository extends IRepository<OwnedGameId, OwnedGame, OwnedGameDTO> {

    Iterable<OwnedGameDTO> findAllOwnedGamesForClient(Long id);
}
