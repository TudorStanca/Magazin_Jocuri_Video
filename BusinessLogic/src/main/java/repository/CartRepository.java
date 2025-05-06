package repository;

import jakarta.persistence.EntityManager;
import model.Cart;
import model.CartId;
import model.Game;
import model.Review;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.ICartRepository;

import java.util.Optional;

public class CartRepository implements ICartRepository {
    private static final Logger logger = LogManager.getLogger(CartRepository.class);

    @Override
    public Optional<Cart> findById(CartId cartId) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var entity = em.find(Cart.class, cartId);
            if (entity != null) {
                entity.getClient().getName();
                entity.getGame().getName();
            }
            return Optional.ofNullable(entity);
        }
    }

    @Override
    public Iterable<Cart> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select c from Cart c left join fetch c.client left join fetch c.game", Cart.class).getResultList();
        }
    }

    @Override
    public Optional<Cart> save(Cart entity) {
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
            logger.debug("Error save cart {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Cart> delete(CartId cartId) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            var entity = em.find(Cart.class, cartId);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
            return Optional.ofNullable(entity);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.debug("Error delete cart {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Cart> update(Cart entity) {
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
            logger.debug("Error update cart {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
