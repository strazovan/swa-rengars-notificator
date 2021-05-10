package cz.cvut.fel.swa.rengars.notifications.api.components.queue.service;

import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;

public interface NotificationService {
    NotificationEntry persist(NotificationEntry entry);

    NotificationEntry getById(String id);

    NotificationEntry cancel(String id);
}
