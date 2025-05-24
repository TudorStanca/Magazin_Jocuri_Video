package repository;

import jakarta.persistence.EntityManager;
import model.Cart;
import model.CartId;
import model.Client;
import model.Game;
import model.dto.CartDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.ICartRepository;
import repository.utils.ToDTOMapper;
import repository.utils.JPAUtils;

import java.util.Optional;

public class CartRepository implements ICartRepository {
    private static final Logger logger = LogManager.getLogger(CartRepository.class);

    @Override
    public Optional<CartDTO> findById(CartId cartId) {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            var entity = em.find(Cart.class, cartId);
            return entity == null ? Optional.empty() : Optional.of(ToDTOMapper.toDTO(entity));
        }
    }

    @Override
    public Iterable<CartDTO> findAll() {
        try (EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager()) {
            return em.createQuery("select c from Cart c", Cart.class).getResultList().stream()
                    .map(ToDTOMapper::toDTO)
                    .toList();
        }
    }

    @Override
    public Optional<CartDTO> save(Cart entity) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            Client c = em.find(Client.class, entity.getClient().getId());
            Game g = em.find(Game.class, entity.getGame().getId());
            entity.setClient(c);
            entity.setGame(g);
            em.persist(entity);
            em.getTransaction().commit();
            return Optional.of(ToDTOMapper.toDTO(entity));
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
    public Optional<CartDTO> delete(CartId cartId) {
        EntityManager em = JPAUtils.getEntityManagerFactory().createEntityManager();
        try {
            CartDTO dto = null;
            em.getTransaction().begin();
            var entity = em.find(Cart.class, cartId);
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
            logger.debug("Error delete cart {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<CartDTO> update(Cart entity) {
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
            logger.debug("Error update cart {}", e.getMessage());
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
