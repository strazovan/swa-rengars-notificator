package cz.cvut.fel.swa.rengars.notifications.api.components.queue.model;

import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationStatus;

import java.util.Date;
import java.util.Map;

public class NotificationEntry {
    private String id;
    private String type;
    private Map<String, String> parameters;
    private Date sentAt;
    private NotificationStatus status;
    private Date scheduledAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public Date getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Date scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}
