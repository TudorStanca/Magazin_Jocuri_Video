package services;

public interface IObserver {

    void notifyClients();

    void notifyStockOperators(Long id);

    void notifyAdmin();

    void notifyClientsReview(Long id);

    void notifyClientsBuy();

    void terminateDeleteSession(Long id);

    void terminateUpdateSession(Long id);
}