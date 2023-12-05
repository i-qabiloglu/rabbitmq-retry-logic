package rabbitmqconsumer.config;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;
import rabbitmqconsumer.exceptions.FailedProcessException;


@Component
@RequiredArgsConstructor
@Slf4j
public class ErrorMessageResolver implements MessageRecoverer {
    private final RabbitTemplate template;
    private final AppProperties properties;


    @Override
    public void recover(Message message, Throwable cause) {
        if (cause instanceof ListenerExecutionFailedException &&
                cause.getCause() instanceof FailedProcessException) {
            log.error("Exception: {}", cause.getCause().getMessage());
            try {

                MessageProperties props = message.getMessageProperties();
                String xRetriedCountHeader = props.getHeader("x-retried-count");
                int xRetriedCount = xRetriedCountHeader == null ? 0 : Integer.parseInt(xRetriedCountHeader);
                props.setHeader("x-retried-count", String.valueOf(++xRetriedCount));
                log.info("xRetriedCount: {}", xRetriedCount);
                if (xRetriedCount > 3) {
                    log.info("message sent to: {}", properties.getUndeliveredQueue());
                    template.convertAndSend(properties.getMainExchange(), properties.getUndeliveredQueue(), message);
                    return;
                }
                log.info("message sent to: {}", properties.getRetryQueueName(xRetriedCount));
                template.convertAndSend(properties.getRetryExchange(), properties.getRetryQueueName(xRetriedCount), message);

            } catch (Exception ex) {
                throw new AmqpRejectAndDontRequeueException("Unable to recover message in try ", ex);
            }
        } else {
            throw new AmqpRejectAndDontRequeueException("Unable to recover message");
        }
    }
}
