package cz.cvut.fel.swa.rengars.notifications.api.controllers;

import cz.cvut.fel.swa.rengars.notifications.api.components.queue.NotificationQueue;
import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.model.*;
import cz.cvut.fel.swa.rengars.notifications.api.model.Error;
import cz.cvut.fel.swa.rengars.notifications.dao.SubscriptionsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ApiV1Implementation implements V1ApiDelegate {

    private final NotificationQueue queue;
    private final SubscriptionsDao subscriptionsDao; // todo should be service, but aint no time for that

    @Autowired
    public ApiV1Implementation(NotificationQueue queue, SubscriptionsDao subscriptionsDao) {
        this.queue = queue;
        this.subscriptionsDao = subscriptionsDao;
    }

    @Override
    public ResponseEntity<NotificationQueueEntry> cancelNotification(String id) {
        final NotificationEntry canceled = this.queue.cancel(id);
        if (canceled == null) {
            return ResponseEntity.notFound().build();
        }
        if (canceled.getStatus() == NotificationStatus.SENT || canceled.getStatus() == NotificationStatus.FAILED) {
            final var error = new Error();
            error.setType("notifications.error.invalidState");
            error.setMessage("Notification is not in a cancelable state");
            error.setDetails(Map.of("state", canceled.getStatus()));
            // return ResponseEntity.badRequest().body(error); todo
            return ResponseEntity.badRequest().build();
        }
        final NotificationQueueEntry dto = this.entryToDto(canceled);
        return ResponseEntity.status(200).body(dto);
    }

    @Override
    public ResponseEntity<NotificationQueueEntry> createNotification(NotificationPostEntry notificationPostEntry) {
        final NotificationEntry entry = this.queue.queue(this.dtoToEntry(notificationPostEntry));
        return ResponseEntity.status(HttpStatus.CREATED).body(this.entryToDto(entry));
    }

    @Override
    public ResponseEntity<NotificationQueueEntry> getNotification(String id) {
        final NotificationEntry entry = this.queue.getById(id);
        return ResponseEntity.ok(this.entryToDto(entry));
    }

    @Override
    public ResponseEntity<List<NotificationQueueEntry>> getNotifications(NotificationStatus state) {
        final List<NotificationEntry> queueState = this.queue.getCurrentQueueState();
        final List<NotificationQueueEntry> entryList = queueState.stream()
                .filter(entry -> state == null || entry.getStatus() == state)
                .map(this::entryToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(entryList);
    }

    @Override
    public ResponseEntity<Subscription> subscribe(Subscription subscription) {
        final SubscriptionDocument subscriptionDocument = this.subscriptionToSubscriptionDocument(subscription);
        final SubscriptionDocument createdSubscription = this.subscriptionsDao.create(subscriptionDocument);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriptionDocumentToSubscription(createdSubscription));
    }

    @Override
    public ResponseEntity<Subscription> unsubscribe(String id) {
        final Optional<SubscriptionDocument> subscription = this.subscriptionsDao.delete(id);
        if (subscription.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(this.subscriptionDocumentToSubscription(subscription.get()));
    }

    private SubscriptionDocument subscriptionToSubscriptionDocument(Subscription subscription) {
        final var document = new SubscriptionDocument();
        document.setNotificator(subscription.getNotificator().getValue());
        document.setType(subscription.getType());
        document.setReceiver(subscription.getReceiver());
        document.setObjectId(subscription.getObjectId() != null ? subscription.getObjectId().longValue() : null);
        return document;
    }

    private Subscription subscriptionDocumentToSubscription(SubscriptionDocument document) {
        final var subscription = new Subscription();
        subscription.setId(document.getId());
        subscription.setNotificator(Subscription.NotificatorEnum.fromValue(document.getNotificator()));
        subscription.setReceiver(document.getReceiver());
        subscription.setType(document.getType());
        subscription.setObjectId(document.getObjectId() != null ? new BigDecimal(document.getObjectId()) : null);
        return subscription;
    }

    private NotificationQueueEntry entryToDto(NotificationEntry entry) {
        final NotificationQueueEntry dto = new NotificationQueueEntry();
        dto.setId(entry.getId());
        dto.setParameters(entry.getParameters());
        if (entry.getScheduledAt() != null)
            dto.setScheduledAt(entry.getScheduledAt().toInstant().atOffset(ZoneOffset.UTC));
        dto.setStatus(entry.getStatus());
        if (entry.getSentAt() != null) {
            dto.setSentAt(entry.getSentAt().toInstant().atOffset(ZoneOffset.UTC));
        }
        return dto;
    }

    private NotificationEntry dtoToEntry(NotificationPostEntry dto) {
        final var entry = new NotificationEntry();
        entry.setType(dto.getType());
        entry.setObjectId(dto.getObjectId() != null ? dto.getObjectId().longValue(): null);
        entry.setParameters((Map<String, Object>) dto.getParameters()); // todo
        entry.setScheduledAt(dto.getSendAt() != null ? new Date(dto.getSendAt().toInstant().toEpochMilli()) : new Date());
        return entry;
    }
}
