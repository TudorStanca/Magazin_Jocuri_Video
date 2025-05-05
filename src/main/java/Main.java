import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class Main {
    public static void main(String[] args) {
        SessionFactory sessionFactory = HibernateUtils.getSessionFactory();
        User u = new User("admin", "admin");
        sessionFactory.inTransaction(session -> {
            session.persist(u);
        });
        try (Session session = sessionFactory.openSession()) {
            session.createQuery("from User", User.class).list().forEach(System.out::println);
        }
    }
}
