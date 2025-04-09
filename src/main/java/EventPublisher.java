public interface EventPublisher {
    void addListener(EventListener listener);
    void notifyListener(String msg);
}
