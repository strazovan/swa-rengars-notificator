package cz.cvut.fel.swa.rengars.notifications.api.components.notificators;

import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationsConfiguration;

import java.util.Map;

public interface Notificator {
    void processEntry(String receiver, NotificationsConfiguration configuration, NotificationEntry entry);

    String getName();

    default String getTextRepresentation(String template, Map<String, Object> parameters) {
        // TODO make better rendering, but for concept this is good enough
        if (template == null) return ""; // let's just return an empty string and avoid NPE
        var representation = template;
        for (Map.Entry<String, Object> kv : parameters.entrySet()) {
            if(kv.getValue() != null)
                representation = representation.replace("${" + kv.getKey() + "}", kv.getValue().toString());
        }
        return representation;
    }
}
