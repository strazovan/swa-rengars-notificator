package cz.cvut.fel.swa.rengars.notifications.mail;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import cz.cvut.fel.swa.rengars.notifications.api.components.notificators.MailNotificator;
import cz.cvut.fel.swa.rengars.notifications.api.components.queue.model.NotificationEntry;
import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationsConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
public class MailTest {


    public static final String RECIPIENT = "test@test.test";
    public static final String TEMPLATE = "Hello ${name}!";
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("sender", "springboot"))
            .withPerMethodLifecycle(false);


    @Autowired
    MailNotificator notificator;

    @Test
    public void mail_sent() throws MessagingException {
        final NotificationsConfiguration configuration = new NotificationsConfiguration();
        configuration.setNotificatorParameters(Map.of("from", "me@me.me", "subject", "test"));
        configuration.setTemplate(TEMPLATE);

        final NotificationEntry entry = new NotificationEntry();
        entry.setParameters(Collections.singletonMap("name", "test"));
        notificator.processEntry(RECIPIENT, configuration, entry);

        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
        assertEquals("Hello test!", GreenMailUtil.getBody(receivedMessage));
        assertEquals(RECIPIENT, receivedMessage.getAllRecipients()[0].toString());
    }
}
