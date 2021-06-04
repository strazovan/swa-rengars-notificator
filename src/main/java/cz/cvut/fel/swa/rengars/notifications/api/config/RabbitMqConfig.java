package cz.cvut.fel.swa.rengars.notifications.api.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

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
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Binding binding(FanoutExchange exchange,
                           Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange);
    }
}
