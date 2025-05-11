package repository;

import jakarta.persistence.EntityManager;
import model.OwnedGame;
import model.OwnedGameId;
import model.dto.OwnedGameDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.IOwnedGamesRepository;
import repository.utils.DTOMapper;
import repository.utils.JPAUtils;

import java.util.Optional;

public class OwnedGameRepository implements IOwnedGamesRepository {
    private static final Logger logger = LogManager.getLogger(OwnedGameRepository.class);

    @Override
    public Optional<OwnedGameDTO> findById(OwnedGameId ownedGameId) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var entity = em.find(OwnedGame.class, ownedGameId);
            return entity == null ? Optional.empty() : Optional.of(DTOMapper.toDTO(entity));
        }
    }

    @Override
    public Iterable<OwnedGameDTO> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select og from OwnedGame og", OwnedGame.class).getResultList().stream()
                    .map(DTOMapper::toDTO)
                    .toList();
        }
    }

    @Override
    public Optional<OwnedGameDTO> save(OwnedGame entity) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return Optional.of(DTOMapper.toDTO(entity));
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
    public Optional<OwnedGameDTO> delete(OwnedGameId ownedGameId) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            OwnedGameDTO dto = null;
            em.getTransaction().begin();
            var entity = em.find(OwnedGame.class, ownedGameId);
            if (entity != null) {
                dto = DTOMapper.toDTO(entity);
                em.remove(entity);
            }
            em.getTransaction().commit();
            return entity == null ? Optional.empty() : Optional.of(dto);
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
    public Optional<OwnedGameDTO> update(OwnedGame entity) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            var updatedEntity = em.merge(entity);
            em.getTransaction().commit();
            return Optional.of(DTOMapper.toDTO(updatedEntity));
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
