import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import model.User;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = JPAUtils.getEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            User user = new User("admin", "admin");
            em.persist(user);
            em.getTransaction().commit();

            em.createQuery("from User", User.class)
                    .getResultList()
                    .forEach(System.out::println);
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
            JPAUtils.close();
        }
    }
}
