package br.org.maosemacao.application;
import br.org.maosemacao.domain.Notification;
import java.time.*;
public class NotificationObserver implements Events.Observer {
    private final Store store;
    private final Clock clock;
    public NotificationObserver(Store store, Clock clock) { this.store=store; this.clock=clock; }
    public void on(Events.Change event) {
        event.recipients().stream().distinct().forEach(id -> store.save(new Notification(null,id,event.message(),false,LocalDateTime.now(clock))));
    }
}
