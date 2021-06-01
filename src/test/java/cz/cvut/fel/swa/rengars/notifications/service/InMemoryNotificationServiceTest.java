package cz.cvut.fel.swa.rengars.notifications.service;

import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.components.queue.service.InMemoryNotificationService;
import cz.cvut.fel.swa.rengars.notifications.api.components.queue.service.NotificationService;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryNotificationServiceTest {

    private final NotificationService service = new InMemoryNotificationService();

    @Test
    void persist_sets_id() {
        final var entry = new NotificationEntry();
        final NotificationEntry persisted = service.persist(entry);
        assertNotNull(persisted.getId());
    }

    @Test
    void find_by_id() {
        final var entry = new NotificationEntry();
        final NotificationEntry persisted = service.persist(entry);
        final NotificationEntry byId = service.getById(persisted.getId());
        assertNotNull(byId);
    }

    @Test
    void cancel() {
        final var entry = new NotificationEntry();
        final NotificationEntry persisted = service.persist(entry);
        final NotificationEntry cancelled = this.service.cancel(persisted.getId());
        assertEquals(persisted.getId(), cancelled.getId());
        assertEquals(NotificationStatus.CANCELLED, cancelled.getStatus());
    }
}