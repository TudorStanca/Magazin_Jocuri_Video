package services;

public interface IObserver {

    void notifyAdmin();

    void terminateSession(Long id);
}