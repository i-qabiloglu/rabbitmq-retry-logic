package rabbitmqconsumer.service;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import rabbitmqconsumer.exceptions.FailedProcessException;
import rabbitmqconsumer.model.Employee;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static rabbitmqconsumer.config.RabbitMQConfig.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {


    @RabbitListener(queues = "mainQueue", containerFactory = "rabbitListenerContainerFactory")
    public void mainListener(Employee employee) {
        log.info("Employee: {}", employee);
        if (employee.getSalary() < 1500) {
            throw new FailedProcessException("Proccess Exception occured");
        }
        log.info("mainListener method end.");
    }


}
