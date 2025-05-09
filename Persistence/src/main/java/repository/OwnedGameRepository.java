package repository;

import jakarta.persistence.EntityManager;
import model.OwnedGame;
import model.OwnedGameId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.IOwnedGamesRepository;
import repository.utils.JPAUtils;

import java.util.Optional;

public class OwnedGameRepository implements IOwnedGamesRepository {
    private static final Logger logger = LogManager.getLogger(OwnedGameRepository.class);

    @Override
    public Optional<OwnedGame> findById(OwnedGameId ownedGameId) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var entity = em.find(OwnedGame.class, ownedGameId);
            if (entity != null) {
                entity.getGame().getName();
                entity.getClient().getName();
            }
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public Iterable<OwnedGame> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select og from OwnedGame og left join fetch og.game left join fetch og.client", OwnedGame.class).getResultList();
        }
    }

    @Override
    public Optional<OwnedGame> save(OwnedGame entity) {
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
            logger.debug("Error save owned game {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<OwnedGame> delete(OwnedGameId ownedGameId) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            var entity = em.find(OwnedGame.class, ownedGameId);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.debug("Error delete owned game {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<OwnedGame> update(OwnedGame entity) {
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
            logger.debug("Error update owned game {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
