package books.microservice;

class InvalidBookException extends Exception {
	private static final long serialVersionUID = 4660923193544513458L;
	
	public InvalidBookException(String message) {
		super(message);
	}
}