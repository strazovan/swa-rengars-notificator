package cz.cvut.fel.swa.rengars.notifications.api.components.queue.service;

import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class InMemoryNotificationService implements NotificationService {

    private final Map<String, NotificationEntry> store = new HashMap<>();

    @Override
    public NotificationEntry persist(NotificationEntry entry) {
        final String id = UUID.randomUUID().toString();
        entry.setId(id);
        store.put(id, entry);
        return entry;
    }

    @Override
    public NotificationEntry getById(String id) {
        return this.store.get(id);
    }

    @Override
    public NotificationEntry cancel(String id) {
        final var entry = this.store.get(id);
        entry.setStatus(NotificationStatus.CANCELLED);
        return entry;
    }
}
