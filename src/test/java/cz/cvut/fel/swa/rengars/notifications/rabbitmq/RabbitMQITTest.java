package cz.cvut.fel.swa.rengars.notifications.rabbitmq;


import cz.cvut.fel.swa.rengars.notifications.api.model.NotificationsConfiguration;
import cz.cvut.fel.swa.rengars.notifications.api.model.SubscriptionDocument;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class })
@ComponentScan(basePackages = {"cz.cvut.fel.swa.rengars"})
public class RabbitMQITTest {

    public static final String TYPE = "TEST_TYPE";

    private enum Messages {
        TEST_MESSAGE("""
                {
                  "eventType": "%s",
                  "payload": {}
                }""".formatted(TYPE));
        private final String content;

        Messages(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }

    private static EmbeddedInMemoryQpidBroker broker = new EmbeddedInMemoryQpidBroker();

    @Autowired
    Sender sender;

    @Autowired
    MockNotificator notificator;

    static MongodExecutable mongodExecutable;

    @BeforeAll
    public static void beforeAll(@Autowired MongoTemplate mongoTemplate,
                                 @Autowired RabbitAdmin admin,
                                 @Value("${configuration.rabbitmq.exchange}")
                                         String exchange,
                                 @Value("${configuration.rabbitmq.queue}")
                                         String queue,
    @Value("${configuration.mongo.port}") int mongoPort,
                                 @Value("${configuration.mongo.host}") String mongoHost) throws Exception {
       MongodStarter starter = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(mongoHost, mongoPort, Network.localhostIsIPv6()))
                .build();
        mongodExecutable = null;
        try {
            mongodExecutable = starter.prepare(mongodConfig);
            mongodExecutable.start();
        } catch (Exception e){
            // log exception here
            if (mongodExecutable != null)
                mongodExecutable.stop();
        }

        broker.start();
        mongoTemplate.insert(new NotificationsConfiguration(null, TYPE, MockNotificator.class.getName(), Collections.emptyMap(), ""));
        mongoTemplate.insert(new SubscriptionDocument(null, TYPE, null, null, MockNotificator.class.getName()));

        admin.declareExchange(new FanoutExchange(exchange));
        admin.declareQueue(new Queue(queue));
        Binding declaredBinding = new Binding(queue, Binding.DestinationType.QUEUE,
                exchange, "", null);
        admin.declareBinding(declaredBinding);
    }

    @AfterAll
    public static void afterAll() {
        broker.shutdown();
        mongodExecutable.stop();
    }

    @Test
    public void resolves_notificator() throws Exception {
        sender.sendMessage(Messages.TEST_MESSAGE.getContent());
        while (notificator.getEntry() == null) {
            Thread.sleep(500); // terrible, terrible way to do this
        }
        assertEquals(TYPE, notificator.getEntry().getType());
    }
}
