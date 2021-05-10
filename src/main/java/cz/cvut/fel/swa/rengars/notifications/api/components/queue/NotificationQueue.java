package cz.cvut.fel.swa.rengars.notifications.api.components.queue;

import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;

import java.util.List;

public interface NotificationQueue {
    NotificationEntry queue(NotificationEntry entry);

    List<NotificationEntry> getCurrentQueueState();

    NotificationEntry getById(String id);

    NotificationEntry cancel(String id);
}
