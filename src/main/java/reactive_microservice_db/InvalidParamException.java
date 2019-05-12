package reactive_microservice_db;

public class InvalidParamException extends Exception {
    private static final long serialVersionUID = 4660923193544513458L;


    public InvalidParamException(String message) {
        super(message);
    }
}
