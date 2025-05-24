package repository;

import jakarta.persistence.EntityManager;
import model.Review;
import model.dto.ReviewDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.IReviewRepository;
import repository.utils.ToDTOMapper;
import repository.utils.JPAUtils;

import java.util.Optional;

public class ReviewRepository implements IReviewRepository {
    private static final Logger logger = LogManager.getLogger(ReviewRepository.class);

    @Override
    public Optional<ReviewDTO> findById(Long aLong) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var entity = em.find(Review.class, aLong);
            return entity == null ? Optional.empty() : Optional.of(ToDTOMapper.toDTO(entity));
        }
    }

    @Override
    public Iterable<ReviewDTO> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select r from Review r", Review.class).getResultList().stream()
                    .map(ToDTOMapper::toDTO)
                    .toList();
        }
    }

    @Override
    public Optional<ReviewDTO> save(Review entity) {
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
            logger.debug("Error save review {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<ReviewDTO> delete(Long aLong) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            ReviewDTO dto = null;
            em.getTransaction().begin();
            var entity = em.find(Review.class, aLong);
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
            logger.debug("Error delete review {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<ReviewDTO> update(Review entity) {
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
            logger.debug("Error update review {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Iterable<ReviewDTO> getAllReviewsForGame(Long gameId) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select r from Review r where r.game.id = :gameId", Review.class).setParameter("gameId", gameId).getResultList().stream()
                    .map(ToDTOMapper::toDTO)
                    .toList();
        }
    }
}
