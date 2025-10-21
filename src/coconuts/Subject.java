package coconuts;

public interface Subject {
    void attach(Observer o);
    void detach(Observer o);
    void notifyAllObservers(HitEvent event);

}
