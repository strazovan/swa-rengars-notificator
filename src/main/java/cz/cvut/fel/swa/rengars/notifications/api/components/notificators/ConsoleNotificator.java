package cz.cvut.fel.swa.rengars.notifications.api.components.notificators;

import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationsConfiguration;
import org.springframework.stereotype.Component;

@Component
public class ConsoleNotificator implements Notificator {

    @Override
    public void processEntry(NotificationsConfiguration configuration, NotificationEntry entry) {
        System.out.println("[" + entry.getId() + "]" + configuration.getNotificatorParameters().get("prefix") + " "
                + this.getTextRepresentation(configuration.getTemplate(), entry.getParameters()));
    }

    @Override
    public String getName() {
        return "ConsoleNotificator";
    }
}
