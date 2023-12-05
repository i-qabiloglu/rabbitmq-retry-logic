package rabbitmqconsumer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Employee {

    @JsonProperty("name")
    private String name;

    @JsonProperty("salary")
    private Integer salary;
}
