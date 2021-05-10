package cz.cvut.fel.swa.rengars.notifications.api.components.notificators;

import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailNotificator implements Notificator {

    private final JavaMailSender emailSender;

    @Autowired
    public MailNotificator(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }


    @Override
    public void processEntry(NotificationsConfiguration configuration, NotificationEntry entry) {
        final var message = new SimpleMailMessage();
        message.setFrom(configuration.getNotificatorParameters().get("from"));
        message.setTo(entry.getParameters().get("to"));
        message.setSubject(configuration.getNotificatorParameters().get("subject"));
        message.setText(this.getTextRepresentation(configuration.getTemplate(), entry.getParameters()));
        emailSender.send(message);
    }

    @Override
    public String getName() {
        return "MailNotificator";
    }
}
