import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import model.Client;
import model.User;
import repository.JPAUtils;
import repository.interfaces.ClientRepository;
import repository.interfaces.IClientRepository;

public class Main {
    public static void main(String[] args) {
        IClientRepository clientRepository = new ClientRepository();
        System.out.println(clientRepository.findAll());
    }
}
