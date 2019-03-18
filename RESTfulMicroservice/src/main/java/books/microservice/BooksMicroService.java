package books.microservice;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BooksMicroService {

	ArrayList<Book> booksRepo = new ArrayList<>();

	@RequestMapping(
			path = "/books", 
			method = RequestMethod.DELETE)
	public void remove() {
		this.booksRepo.clear();
	}

	@RequestMapping(
			path = "/books/echo", 
			method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public Book analyze(@RequestBody Book book) throws InvalidBookException {
		validateBook(book);
		this.booksRepo.add(book);
		return book;
	}

	@RequestMapping(
			path = "/books/byIsbn/{isbn}", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public Book getBookByIsbn(
			@PathVariable("isbn") String isbn) throws UnfitParamException, InvalidBookException {
		int isbnLength = isbn.length();
		if (isIsbnValid(isbnLength)) {
			Book rv = getBookByISBN(isbn);
			if (rv != null) {
				return rv;
			} else {
				throw new UnfitParamException("Book not found: " + isbn);
			}
		} else {
			throw new InvalidBookException("ISBN is invalid");
		}
	}

	private Book getBookByISBN(String isbn) {
		for (int i = 0; i < booksRepo.size(); i++) {
			if (booksRepo.get(i).getISBN().equals(isbn)) {
				return booksRepo.get(i);
			}
		}
		return null;
	}

	@RequestMapping(
			path = "/books/all", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public String[] isbnBooks(
			@RequestParam(name = "content", required = false, defaultValue = "detailed") String param)
			throws UnfitParamException, InvalidBookException {
		String[] rv = null;
		if (this.booksRepo.isEmpty()) {
			throw new UnfitParamException("No Books Founds!");
		} else {
			switch (param) {
			case "isbn":
				rv = IntStream.range(0, booksRepo.size()).mapToObj(i -> booksRepo.get(i).getISBN())
						.collect(Collectors.toList()).toArray(new String[0]);
				break;
			case "title":
				rv = IntStream.range(0, booksRepo.size()).mapToObj(i -> booksRepo.get(i).getTitle())
						.collect(Collectors.toList()).toArray(new String[0]);
				break;
			case "detailed":
				rv = IntStream.range(0, booksRepo.size()).mapToObj(i -> booksRepo.get(i).toString())
						.collect(Collectors.toList()).toArray(new String[0]);
				break;
			default:
				throw new InvalidBookException("Paramater in path is invalid");
			}
		}

		return rv;
	}



	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public String handleException(UnfitParamException e) {
		e.printStackTrace();

		return "error: " + e.getMessage();
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorMessage handleException(InvalidBookException e) {
		return new ErrorMessage(e.getMessage());
	}

	private void validateBook(Book book) throws InvalidBookException {
		if (book.getISBN() == null) {
			throw new InvalidBookException("ISBN missing");
		}

		int isbnLength = book.getISBN().length();
		if (!isIsbnValid(isbnLength)) {
			throw new InvalidBookException("ISBN is invalid");
		}

		int rating = book.getRating();
		if (rating < 0 || rating > 5) {
			throw new InvalidBookException("Rating is invalid");
		}
	}

	private boolean isIsbnValid(int isbnLength) {
		if (isbnLength < 10 || isbnLength > 13) {
			return false;
		}

		return true;
	}
}
