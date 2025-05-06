import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.interfaces.*;

public class Service {
    private static Logger logger = LogManager.getLogger(Service.class);

    private final IClientRepository clientRepository;
    private final IStockOperatorRepository stockOperatorRepository;
    private final IGameRepository gameRepository;
    private final IReviewRepository reviewRepository;
    private final ICartRepository cartRepository;
    private final IOwnedGamesRepository ownedGamesRepository;

    public Service(IClientRepository client, IStockOperatorRepository stock, IGameRepository game, IReviewRepository review, ICartRepository cart, IOwnedGamesRepository ownedGame) {
        this.clientRepository = client;
        this.stockOperatorRepository = stock;
        this.gameRepository = game;
        this.reviewRepository = review;
        this.cartRepository = cart;
        this.ownedGamesRepository = ownedGame;
    }
}
