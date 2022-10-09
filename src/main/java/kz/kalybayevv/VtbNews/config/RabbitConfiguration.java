package kz.kalybayevv.VtbNews.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfiguration {
    private final Logger log = LoggerFactory.getLogger(RabbitConfiguration.class);

    private final RabbitProperties rabbitProperties;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory();
        if (rabbitProperties.getHost() != null) {
            connectionFactory.setHost(rabbitProperties.getHost());
        }
        if (rabbitProperties.getUsername() != null) {
            connectionFactory.setUsername(rabbitProperties.getUsername());
        }
        if (rabbitProperties.getPassword() != null) {
            connectionFactory.setPassword(rabbitProperties.getPassword());
        }
        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    // <p> Clock Generate Report region begin
//    @Bean
//    public Queue clockQueue() {
//        return new Queue(Queues.CLOCK);
//    }
//
//    @Bean
//    public MessageListenerAdapter clockListener(ClockPdfConsumer consumer) {
//        log.info("Rabbit {ClockPdfConsumer} message sent to the consumer");
//        return new MessageListenerAdapter(consumer, "receiveMessage");
//    }
//
//    @Bean
//    public SimpleMessageListenerContainer clockContainer(ConnectionFactory connectionFactory,
//                                                         @Qualifier("clockListener") MessageListenerAdapter adapter) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(Queues.CLOCK);
//        container.setMessageListener(adapter);
//        return container;
//    }
    // </p> End region
}
