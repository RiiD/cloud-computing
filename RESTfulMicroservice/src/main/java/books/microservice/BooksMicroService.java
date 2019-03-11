package books.microservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BooksMicroService {
	@RequestMapping(
			path="/books/echo",
			method=RequestMethod.POST,
			produces=MediaType.APPLICATION_JSON_VALUE,
			consumes=MediaType.APPLICATION_JSON_VALUE)
	public Book analyze (@RequestBody Book book) throws InvalidBookException {
		validateBook(book);
		return book;
	}
	
	@ExceptionHandler
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException (InvalidBookException e) {
		return new ErrorMessage(e.getMessage());
	}
	
	private void validateBook(Book book) throws InvalidBookException {
		if (book.getISBN() == null) {
			throw new InvalidBookException("ISBN missing");
		}
		
		int isbnLength = book.getISBN().length();
		if (isbnLength < 10 || isbnLength > 13) {
			throw new InvalidBookException("ISBN is invalid");
		}
		
		int rating = book.getRating();
		if (rating < 0 || rating > 5) {
			throw new InvalidBookException("Rating is invalid");
		}
	}
}
