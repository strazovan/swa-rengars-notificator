package cz.cvut.fel.swa.rengars.notifications.api.components.notificators;

import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationsConfiguration;

import java.util.Map;

public interface Notificator {
    void processEntry(NotificationsConfiguration configuration, NotificationEntry entry);

    String getName();

    default String getTextRepresentation(String template, Map<String, String> parameters) {
        // TODO make better rendering, but for concept this is good enough
        if (template == null) return ""; // let's just return an empty string and avoid NPE
        var representation = template;
        for (Map.Entry<String, String> kv : parameters.entrySet()) {
            representation = representation.replace("${" + kv.getKey() + "}", kv.getValue());
        }
        return representation;
    }
}
