package repository;

import jakarta.persistence.EntityManager;
import model.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.IClientRepository;
import repository.utils.JPAUtils;

import java.util.Optional;

public class ClientRepository implements IClientRepository {

    private static final Logger logger = LogManager.getLogger(ClientRepository.class);

    @Override
    public Optional<Client> findById(Long aLong) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var client = em.find(Client.class, aLong);
            if (client != null) {
                client.getCarts().size();
                client.getOwnedGames().size();
            }
            return Optional.ofNullable(client);
        }
    }

    @Override
    public Iterable<Client> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select distinct c from Client c left join fetch c.carts left join fetch c.ownedGames", Client.class).getResultList();
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
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            var entity = em.find(Client.class, aLong);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
            return Optional.ofNullable(entity);
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

    @Override
    public Optional<Client> findByUsername(String username) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var result = em.createQuery("select c from Client c where c.username = :username", Client.class).setParameter("username", username).getResultList();
            var client = result.isEmpty() ? null : result.getFirst();
            if (client != null) {
                client.getCarts().size();
                client.getOwnedGames().size();
            }
            return Optional.ofNullable(client);
        }
    }
}
