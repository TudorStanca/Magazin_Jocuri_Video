package repository;

import jakarta.persistence.EntityManager;
import model.Client;
import model.dto.ClientDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.IClientRepository;
import repository.utils.ToDTOMapper;
import repository.utils.JPAUtils;

import java.util.Optional;

public class ClientRepository implements IClientRepository {

    private static final Logger logger = LogManager.getLogger(ClientRepository.class);

    @Override
    public Optional<ClientDTO> findById(Long aLong) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var entity = em.find(Client.class, aLong);
            return entity == null ? Optional.empty() : Optional.of(ToDTOMapper.toDTO(entity));
        }
    }

    @Override
    public Iterable<ClientDTO> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select c from Client c", Client.class).getResultList().stream()
                    .map(ToDTOMapper::toDTO)
                    .toList();
        }
    }

    @Override
    public Optional<ClientDTO> save(Client entity) {
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
            logger.debug("Error save client {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<ClientDTO> delete(Long aLong) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            ClientDTO dto = null;
            em.getTransaction().begin();
            logger.debug("Deleting client {}", aLong);
            var entity = em.find(Client.class, aLong);
            logger.debug("Found client {}", entity);
            if (entity != null) {
                dto = ToDTOMapper.toDTO(entity);
                System.out.println(dto);
                em.remove(entity);
            }
            logger.debug("Entity successfully deleted");
            em.getTransaction().commit();
            return entity == null ? Optional.empty() : Optional.of(dto);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.debug("Error delete id client {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<ClientDTO> update(Client entity) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Client updatedEntity = em.merge(entity);
            em.getTransaction().commit();
            return Optional.of(ToDTOMapper.toDTO(updatedEntity));
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.debug("Error update client {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<ClientDTO> findByUsername(String username) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var result = em.createQuery("select c from Client c where c.username = :username", Client.class).setParameter("username", username).getResultList();
            var entity = result.isEmpty() ? null : result.getFirst();
            return entity == null ? Optional.empty() : Optional.of(ToDTOMapper.toDTO(entity));
        }
    }
}
