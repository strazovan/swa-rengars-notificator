package cz.cvut.fel.swa.rengars.notifications.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Sender {

    private static final String BROKER_URL = "amqp://127.0.0.1:8888";

    private ConnectionFactory factory = new ConnectionFactory();

    @Value("${configuration.rabbitmq.exchange}")
    private String exchange;

    public void sendMessage(String text) throws Exception {
        factory.setUri(BROKER_URL);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.basicPublish(exchange, "",  null, text.getBytes());
    }
}