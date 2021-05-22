package cz.cvut.fel.swa.rengars.notifications.api.rabbitmq;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.swa.rengars.notifications.api.components.queue.NotificationQueue;
import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.rabbitmq.dto.NotificationEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class NotificationsReceiver {

    private final ObjectMapper objectMapper;
    private final NotificationQueue queue;

    @Autowired
    public NotificationsReceiver(ObjectMapper objectMapper, NotificationQueue queue) {
        this.objectMapper = objectMapper;
        this.queue = queue;
    }

    @RabbitListener(queues = "#{notificationsQueue.name}")
    public void receive(String message) throws JsonProcessingException {
        final var eventDto = this.objectMapper.readValue(message, NotificationEvent.class);
        final NotificationEntry entry = this.entryFromDto(eventDto);
        this.queue.queue(entry);
    }

    private NotificationEntry entryFromDto(NotificationEvent event) {
        final var entry = new NotificationEntry();
        entry.setType(event.getEventType());
        entry.setObjectId(event.getId());
        entry.setScheduledAt(new Date());
        entry.setParameters(event.getPayload());
        return entry;
    }
}
