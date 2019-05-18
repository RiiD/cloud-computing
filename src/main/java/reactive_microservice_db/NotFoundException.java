package reactive_microservice_db;

public class NotFoundException extends Exception {
	private static final long serialVersionUID = 217216800717663213L;
	
	public NotFoundException() {
		super("Loan not found");
	}
}
