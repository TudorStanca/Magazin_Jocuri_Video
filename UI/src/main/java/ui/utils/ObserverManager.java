package ui.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.IObserver;
import ui.controllers.AdminPageMainController;
import ui.controllers.IController;

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
    public void notifyAdmin() {
        if (currentController instanceof AdminPageMainController controller) {
            logger.debug("Updating users table");
            controller.updateAdminUserList();
        }
    }
}
