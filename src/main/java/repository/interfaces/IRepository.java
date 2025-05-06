package repository.interfaces;

import model.Identifiable;

import java.io.Serializable;
import java.util.Optional;

public interface IRepository<ID extends Serializable, E extends Identifiable<ID>> {

    Optional<E> findById(ID id);

    Iterable<E> findAll();

    Optional<E> save(E entity);

    Optional<E> delete(ID id);

    Optional<E> delete(E entity);

    Optional<E> update(E entity);
}