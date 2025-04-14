// Jiachen Si 1085839
public interface EventPublisher {
    void addListener(EventListener listener);
    void notifyListener(String msg);
}
