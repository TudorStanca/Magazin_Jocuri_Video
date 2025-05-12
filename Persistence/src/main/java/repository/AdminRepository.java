package repository;

import jakarta.persistence.EntityManager;
import model.Admin;
import model.dto.AdminDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.IAdminRepository;
import repository.utils.ToDTOMapper;
import repository.utils.JPAUtils;

import java.util.Optional;

public class AdminRepository implements IAdminRepository {

    private static final Logger logger = LogManager.getLogger(AdminRepository.class);

    @Override
    public Optional<AdminDTO> findById(Long aLong) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var entity = em.find(Admin.class, aLong);
            return entity == null ? Optional.empty() : Optional.of(ToDTOMapper.toDTO(entity));
        }
    }

    @Override
    public Iterable<AdminDTO> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select a from Admin a", Admin.class).getResultList().stream()
                    .map(ToDTOMapper::toDTO)
                    .toList();
        }
    }

    @Override
    public Optional<AdminDTO> save(Admin entity) {
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
            logger.debug("Error save admin {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<AdminDTO> delete(Long aLong) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            AdminDTO dto = null;
            em.getTransaction().begin();
            var entity = em.find(Admin.class, aLong);
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
            logger.debug("Error delete id admin {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<AdminDTO> update(Admin entity) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Admin updatedEntity = em.merge(entity);
            em.getTransaction().commit();
            return Optional.of(ToDTOMapper.toDTO(updatedEntity));
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.debug("Error update admin {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<AdminDTO> findByUsername(String username) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var result = em.createQuery("select a from Admin a where a.username = :username", Admin.class).setParameter("username", username).getResultList();
            var entity = result.isEmpty() ? null : result.getFirst();
            return entity == null ? Optional.empty() : Optional.of(ToDTOMapper.toDTO(entity));
        }
    }
}
