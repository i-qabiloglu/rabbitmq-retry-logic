package rabbitmqconsumer.exceptions;

public class FailedProcessException extends RuntimeException {

    public FailedProcessException(String message) {
        super(message);
    }
}
