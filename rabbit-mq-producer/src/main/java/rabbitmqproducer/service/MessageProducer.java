package rabbitmqproducer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rabbitmqproducer.model.Employee;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessageProducer {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Employee employee) {
        log.info("MessageProducer.sendMessage.start : {}", employee);
        rabbitTemplate.convertAndSend(exchange, routingKey, employee);
        log.info("MessageProducer.sendMessage.end");
    }
}
