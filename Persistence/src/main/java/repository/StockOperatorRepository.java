package repository;

import jakarta.persistence.EntityManager;
import model.Client;
import model.StockOperator;
import model.dto.StockOperatorDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.IStockOperatorRepository;
import repository.utils.DTOMapper;
import repository.utils.JPAUtils;

import java.util.Optional;
import java.util.stream.Collectors;

public class StockOperatorRepository implements IStockOperatorRepository {
    private static final Logger logger = LogManager.getLogger(StockOperatorRepository.class);

    @Override
    public Optional<StockOperatorDTO> findById(Long aLong) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var entity = em.find(StockOperator.class, aLong);
            return entity == null ? Optional.empty() : Optional.of(DTOMapper.toDTO(entity));
        }
    }

    @Override
    public Iterable<StockOperatorDTO> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select so from StockOperator so", StockOperator.class).getResultList().stream()
                    .map(DTOMapper::toDTO)
                    .toList();
        }
    }

    @Override
    public Optional<StockOperatorDTO> save(StockOperator entity) {
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
            logger.debug("Error save stock operator {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<StockOperatorDTO> delete(Long aLong) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            StockOperatorDTO dto = null;
            em.getTransaction().begin();
            var entity = em.find(StockOperator.class, aLong);
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
            logger.debug("Error delete stock operator {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<StockOperatorDTO> update(StockOperator entity) {
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
            logger.debug("Error update stock operator {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<StockOperatorDTO> findByUsername(String username) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var result = em.createQuery("select so from StockOperator so where so.username = :username", StockOperator.class).setParameter("username", username).getResultList();
            var entity = result.isEmpty() ? null : result.getFirst();
            return entity == null ? Optional.empty() : Optional.of(DTOMapper.toDTO(entity));
        }
    }
}
