package services;

public interface IObserver {

    void notifyStockOperators(Long id);

    void notifyAdmin();

    void terminateDeleteSession(Long id);

    void terminateUpdateSession(Long id);
}