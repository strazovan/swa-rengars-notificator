package cz.cvut.fel.swa.rengars.notifications.api.controllers;

import cz.cvut.fel.swa.rengars.notifications.api.components.queue.NotificationQueue;
import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.model.Error;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationPostEntry;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationQueueEntry;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static cz.cvut.fel.swa.rengars.notifications.api.DateUtils.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ApiV1Implementation implements V1ApiDelegate {

    private final NotificationQueue queue;

    @Autowired
    public ApiV1Implementation(NotificationQueue queue) {
        this.queue = queue;
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
        return ResponseEntity.ok(this.entryToDto(entry));
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
                .filter(entry -> state == null ||  entry.getStatus() == state)
                .map(this::entryToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(entryList);
    }

    private NotificationQueueEntry entryToDto(NotificationEntry entry) {
        final NotificationQueueEntry dto = new NotificationQueueEntry();
        dto.setId(entry.getId());
        dto.setParameters(entry.getParameters());
        dto.setScheduledAt(toISOString(entry.getScheduledAt()));
        dto.setStatus(entry.getStatus());
        if (entry.getSentAt() != null) {
            dto.setSentAt(toISOString(entry.getSentAt()));
        }
        return dto;
    }

    private NotificationEntry dtoToEntry(NotificationPostEntry dto) {
        final var entry = new NotificationEntry();
        entry.setType(dto.getType());
        entry.setParameters((Map<String, Object>) dto.getParameters()); // todo
        entry.setScheduledAt(dto.getSendAt() != null ? fromISOString(dto.getSendAt()) : new Date());
        return entry;
    }
}
