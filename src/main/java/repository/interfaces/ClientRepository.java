package repository.interfaces;

import jakarta.persistence.EntityManager;
import model.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.JPAUtils;

import java.util.Optional;

public class ClientRepository implements IClientRepository {

    private static final Logger logger = LogManager.getLogger(ClientRepository.class);

    @Override
    public Optional<Client> findById(Long aLong) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return Optional.ofNullable(em.find(Client.class, aLong));
        }
    }

    @Override
    public Iterable<Client> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select c from Client c", Client.class).getResultList();
        }
    }

    @Override
    public Optional<Client> save(Client entity) {
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
            logger.debug("Error save client {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Client> delete(Long aLong) {
        Optional<Client> entity = findById(aLong);
        entity.ifPresent(this::delete);
        return entity;
    }

    @Override
    public Optional<Client> delete(Client entity) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(entity);
            em.getTransaction().commit();
            return Optional.of(entity);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.debug("Error delete client {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Client> update(Client entity) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Client updatedClient = em.merge(entity);
            em.getTransaction().commit();
            return Optional.of(updatedClient);
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
}
