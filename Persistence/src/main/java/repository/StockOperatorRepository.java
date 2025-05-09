package repository;

import jakarta.persistence.EntityManager;
import model.StockOperator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.IStockOperatorRepository;
import repository.utils.JPAUtils;

import java.util.Optional;

public class StockOperatorRepository implements IStockOperatorRepository {
    private static final Logger logger = LogManager.getLogger(StockOperatorRepository.class);

    @Override
    public Optional<StockOperator> findById(Long aLong) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var entity = em.find(StockOperator.class, aLong);
            if (entity != null) {
                entity.getGames().size();
            }
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public Iterable<StockOperator> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select distinct so from StockOperator so left join fetch so.games", StockOperator.class).getResultList();
        }
    }

    @Override
    public Optional<StockOperator> save(StockOperator entity) {
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
            logger.debug("Error save stock operator {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<StockOperator> delete(Long aLong) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            var entity = em.find(StockOperator.class, aLong);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.debug("Error delete stock operator {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<StockOperator> update(StockOperator entity) {
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
            logger.debug("Error update stock operator {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
