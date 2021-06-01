package cz.cvut.fel.swa.rengars.notifications.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subscriptions")
public class SubscriptionDocument {
    @Id
    private String id;
    private String type;
    private Long objectId;
    private String receiver;
    private String notificator;

    public SubscriptionDocument() {
    }

    public SubscriptionDocument(String id, String type, Long objectId, String receiver, String notificator) {
        this.id = id;
        this.type = type;
        this.objectId = objectId;
        this.receiver = receiver;
        this.notificator = notificator;
    }

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

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getNotificator() {
        return notificator;
    }

    public void setNotificator(String notificator) {
        this.notificator = notificator;
    }
}
