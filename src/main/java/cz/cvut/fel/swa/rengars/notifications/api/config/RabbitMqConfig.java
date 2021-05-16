package cz.cvut.fel.swa.rengars.notifications.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("cz.cvut.fel.swa.rengars.notifications.api.rabbitmq")
public class RabbitMqConfig {
    @Value("${configuration.rabbitmq.exchange}")
    private String exchange;

    @Value("${configuration.rabbitmq.queue}")
    private String queue;

    @Bean
    public FanoutExchange exchange() {
        return new FanoutExchange(this.exchange);
    }

    @Bean(name = "notificationsQueue")
    public Queue notificationsEventsQueue() {
        return new Queue(queue);
    }


    @Bean
    public Binding binding(FanoutExchange exchange,
                           Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange);
    }
}
