package repository;

import jakarta.persistence.EntityManager;
import model.Client;
import model.Game;
import model.StockOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.IGameRepository;

import java.util.Optional;

public class GameRepository implements IGameRepository {
    private static final Logger logger = LogManager.getLogger(GameRepository.class);

    @Override
    public Optional<Game> findById(Long aLong) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var entity = em.find(Game.class, aLong);
            if (entity != null) {
                entity.getStockOperator().getCompany();
                entity.getReviews().size();
            }
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public Iterable<Game> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select distinct g from Game g left join fetch g.reviews left join fetch g.stockOperator", Game.class).getResultList();
        }
    }

    @Override
    public Optional<Game> save(Game entity) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return Optional.of(entity);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.debug("Error save game {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Game> delete(Long aLong) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            var entity = em.find(Game.class, aLong);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.debug("Error delete game {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Game> update(Game entity) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            var updatedEntity = em.merge(entity);
            em.getTransaction().commit();
            return Optional.of(updatedEntity);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.debug("Error update game {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
