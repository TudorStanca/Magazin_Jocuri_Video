package ui.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IObserver;
import ui.controllers.*;

public class ObserverManager implements IObserver {

    private static final ObserverManager instance = new ObserverManager();
    private IController currentController;

    private static Logger logger = LogManager.getLogger(ObserverManager.class);

    private ObserverManager() {}

    public static ObserverManager getInstance() {
        return instance;
    }

    public void setCurrentController(IController controller) {
        this.currentController = controller;
    }

    @Override
    public void notifyClients() {
        if (currentController instanceof ClientMainPageController controller) {
            logger.debug("Updating clients game list");
            controller.updateAvailableGameList();
            controller.updateOwnedGameList();
        }
    }

    @Override
    public void notifyAdmin() {
        if (currentController instanceof AdminPageMainController controller) {
            logger.debug("Updating users table");
            controller.updateAdminUserList();
        }
    }

    @Override
    public void notifyStockOperators(Long id) {
        if (currentController instanceof StockOperatorMainPageController controller) {
            logger.debug("Updating game table");
            controller.updateGameList(id);
        }
    }

    @Override
    public void notifyClientsReview(Long id) {
        if (currentController instanceof ClientReviewPageController controller) {
            logger.debug("Updating clients review game list {}", id);
            controller.updateGameList(id);
        }
        if(currentController instanceof ClientMainPageController controller) {
            logger.debug("Updating review table {}", id);
            controller.updateReviewTable(id);
        }
    }

    @Override
    public void notifyClientsBuy() {
        if (currentController instanceof ClientBuyPageController controller) {
            logger.debug("Updating clients buy game list");
            controller.updateAvailableGames();
            controller.updateCartGames();
        }
    }

    @Override
    public void terminateDeleteSession(Long id) {
        if (currentController instanceof ClientMainPageController controller) {
            logger.debug("Terminating delete session for client {}", id);
            controller.terminateSessionUserDeleted(id);
        }
        if (currentController instanceof StockOperatorMainPageController controller) {
            logger.debug("Terminating delete session for client {}", id);
            controller.terminateSessionUserDeleted(id);
        }
    }

    @Override
    public void terminateUpdateSession(Long id) {
        if (currentController instanceof ClientMainPageController controller) {
            logger.debug("Terminating update session for client {}", id);
            controller.terminateSessionUserUpdated(id);
        }
        if (currentController instanceof StockOperatorMainPageController controller) {
            logger.debug("Terminating update session for client {}", id);
            controller.terminateSessionUserUpdated(id);
        }
    }
}
