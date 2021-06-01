package cz.cvut.fel.swa.rengars.notifications.rabbitmq;


import cz.cvut.fel.swa.rengars.notifications.api.components.notificators.Notificator;
import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationsConfiguration;
import org.springframework.stereotype.Component;

@Component("mockNotificator")
public class MockNotificator implements Notificator {
    private NotificationEntry entry;

    @Override
    public void processEntry(String receiver, NotificationsConfiguration configuration, NotificationEntry entry) {
        this.entry = entry;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    public NotificationEntry getEntry() {
        return entry;
    }
}
