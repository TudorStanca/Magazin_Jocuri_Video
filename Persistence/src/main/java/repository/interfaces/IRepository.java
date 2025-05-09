package repository.interfaces;

import model.Identifiable;
import model.dto.RepositoryDTO;

import java.io.Serializable;
import java.util.Optional;

public interface IRepository<ID extends Serializable, E extends Identifiable<ID>, RET extends RepositoryDTO> {

    Optional<RET> findById(ID id);

    Iterable<RET> findAll();

    Optional<RET> save(E entity);

    Optional<RET> delete(ID id);

    Optional<RET> update(E entity);
}