package rabbitmqconsumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@EnableRabbit
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    //    public static final String EXCHANGE = "main-exchange";
//    public static final String RETRY_EXCHANGE = "retry-exchange";
//    public static final String MAIN_ROUTING_KEY = "Routing_Key";
//    public static final String QUEUE = "mainQueue";
//    public static final String DL_QUEUE_1 = QUEUE + ".retry.1";
//    public static final String DL_QUEUE_2 = QUEUE + ".retry.2";
//    public static final String DL_QUEUE_3 = QUEUE + ".retry.3";
//    public static final String UNDELIVERED_QUEUE = QUEUE + ".undelivered";
    private final AppProperties props;


    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    DirectExchange exchange() {
        return new DirectExchange(props.getMainExchange(), true, false);
    }

    @Bean
    DirectExchange retryExchange() {
        return new DirectExchange(props.getRetryExchange(), true, false);
    }

    @Bean
    Queue mainQueue() {
        return QueueBuilder.durable(props.getMainQueue())
                .build();
    }

    @Bean
    Queue retryQuequeFirst() {
        return QueueBuilder.durable(props.getRetryQueueName(1))
                .deadLetterExchange(props.getMainExchange())
                .deadLetterRoutingKey(props.getMainRoutingKey())
                .ttl(10000)
                .build();
    }

    @Bean
    Queue retryQuequeSecond() {
        return QueueBuilder.durable(props.getRetryQueueName(2))
                .deadLetterExchange(props.getMainExchange())
                .deadLetterRoutingKey(props.getMainRoutingKey())
                .ttl(20000)
                .build();
    }

    @Bean
    Queue retryQuequeThird() {
        return QueueBuilder.durable(props.getRetryQueueName(3))
                .deadLetterExchange(props.getMainExchange())
                .deadLetterRoutingKey(props.getMainRoutingKey())
                .ttl(30000)
                .build();
    }

    @Bean
    Queue undeliveredQueue() {
        return QueueBuilder.durable(props.getUndeliveredQueue()).build();
    }

    @Bean
    Binding mainBinding(Queue mainQueue, DirectExchange exchange) {
        return BindingBuilder.bind(mainQueue).to(exchange).with(props.getMainRoutingKey());
    }

    @Bean
    Binding firstRetryBinding(Queue retryQuequeFirst, DirectExchange retryExchange) {
        return BindingBuilder.bind(retryQuequeFirst).to(retryExchange).with(props.getRetryQueueName(1));
    }

    @Bean
    Binding secondRetryBinding(Queue retryQuequeSecond, DirectExchange retryExchange) {
        return BindingBuilder.bind(retryQuequeSecond).to(retryExchange).with(props.getRetryQueueName(2));
    }

    @Bean
    Binding thirdRetryBinding(Queue retryQuequeThird, DirectExchange retryExchange) {
        return BindingBuilder.bind(retryQuequeThird).to(retryExchange).with(props.getRetryQueueName(3));
    }

    @Bean
    Binding undeliveredBinding(Queue undeliveredQueue, DirectExchange exchange) {
        return BindingBuilder.bind(undeliveredQueue).to(exchange).with(props.getUndeliveredQueue());
    }

    @Bean
    public RetryOperationsInterceptor messageRetryInterceptor(
            RabbitTemplate template) {
        return RetryInterceptorBuilder.StatelessRetryInterceptorBuilder
                .stateless()
                .maxAttempts(1)
                .recoverer(new ErrorMessageResolver(template, props))
                .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            RetryOperationsInterceptor messageRetryInterceptor) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setAdviceChain(messageRetryInterceptor);
        return factory;
    }
}
