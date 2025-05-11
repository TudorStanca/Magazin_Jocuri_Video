package services;

public interface IObserver {

    void notifyAdmin();

    void terminateDeleteSession(Long id);

    void terminateUpdateSession(Long id);
}