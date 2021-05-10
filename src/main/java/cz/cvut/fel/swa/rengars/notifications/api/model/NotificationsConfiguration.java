package cz.cvut.fel.swa.rengars.notifications.api.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "configurations")
public class NotificationsConfiguration {
    @Id
    private String id;
    private String type;
    private String notificatorName;
    private Map<String, String> notificatorParameters;
    private String template;

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

    public String getNotificatorName() {
        return notificatorName;
    }

    public void setNotificatorName(String notificatorName) {
        this.notificatorName = notificatorName;
    }

    public Map<String, String> getNotificatorParameters() {
        return notificatorParameters;
    }

    public void setNotificatorParameters(Map<String, String> notificatorParameters) {
        this.notificatorParameters = notificatorParameters;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
