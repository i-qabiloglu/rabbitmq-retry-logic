package rabbitmqproducer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rabbitmqproducer.model.Employee;
import rabbitmqproducer.service.MessageProducer;

@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Autowired
    private MessageProducer messageProducer;

    @GetMapping("/message")
    public void sendMessage(@RequestBody Employee employee) {

        messageProducer.sendMessage(employee);
    }


}
