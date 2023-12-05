package rabbitmqconsumer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "app.rabbitmq")
public class AppProperties {

    private String mainExchange;

    private String retryExchange;

    private String mainRoutingKey;

    private String mainQueue;

    private String retryQueue;

    private String undeliveredQueue;

    public String getRetryQueueName(int retryCount) {
        return this.retryQueue + "." + retryCount;
    }
}
