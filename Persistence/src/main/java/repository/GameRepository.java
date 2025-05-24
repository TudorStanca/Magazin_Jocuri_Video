package repository;

import jakarta.persistence.EntityManager;
import model.Client;
import model.Game;
import model.dto.GameDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.IGameRepository;
import repository.utils.ToDTOMapper;
import repository.utils.JPAUtils;

import java.util.Optional;

public class GameRepository implements IGameRepository {
    private static final Logger logger = LogManager.getLogger(GameRepository.class);

    @Override
    public Optional<GameDTO> findById(Long aLong) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var entity = em.find(Game.class, aLong);
            return entity == null ? Optional.empty() : Optional.of(ToDTOMapper.toDTO(entity));
        }
    }

    @Override
    public Iterable<GameDTO> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select g from Game g", Game.class).getResultList().stream()
                    .map(ToDTOMapper::toDTO)
                    .toList();
        }
    }

    @Override
    public Optional<GameDTO> save(Game entity) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return Optional.of(ToDTOMapper.toDTO(entity));
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
    public Optional<GameDTO> delete(Long aLong) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            GameDTO dto = null;
            em.getTransaction().begin();
            var entity = em.find(Game.class, aLong);
            if (entity != null) {
                dto = ToDTOMapper.toDTO(entity);
                em.remove(entity);
            }
            em.getTransaction().commit();
            return entity == null ? Optional.empty() : Optional.of(dto);
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
    public Optional<GameDTO> update(Game entity) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            var updatedEntity = em.merge(entity);
            em.getTransaction().commit();
            return Optional.of(ToDTOMapper.toDTO(updatedEntity));
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

    @Override
    public Iterable<GameDTO> findByStockOperator(Long id) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select g from Game g where g.stockOperator.id = :id", Game.class).setParameter("id", id).getResultList().stream()
                    .map(ToDTOMapper::toDTO)
                    .toList();
        }
    }

    @Override
    public Iterable<GameDTO> getAllAvailableGames() {
        String query = """
            select g from Game g
            left join Cart c on c.game = g
            left join OwnedGame og on og.game = g
            where c.game is null and og.game is null
        """;
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery(query, Game.class).getResultList().stream()
                    .map(ToDTOMapper::toDTO)
                    .toList();
        }
    }
}
