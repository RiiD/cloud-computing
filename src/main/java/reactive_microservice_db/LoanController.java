package reactive_microservice_db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class LoanController {

    private static final int MAX_ISBN_LENGTH = 13;
    private static final int MIN_ISBN_LENGTH = 10;
    private LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @RequestMapping(
            path="/loans/{isbn}",
            method = RequestMethod.POST,
            consumes= MediaType.APPLICATION_JSON_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public Mono<Loan> store(
            @PathVariable("isbn") int isbn,
            @RequestBody  Loan newLoan) throws InvalidParamException {
        if(isIsbnValid(isbn)){
            return this.loanService
                        .store(newLoan);
        }
        else{
            throw new InvalidParamException("isbn is invalid");
        }
    }

    @RequestMapping(
            path="/loans/{loanId}",
            method = RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public Mono<Loan> getByKey(@PathVariable("loanId") String loanId){
        return this.loanService
                .getByKey(loanId);

    }

    @RequestMapping(
            path="/loans",
            method = RequestMethod.GET,
            produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Loan> getAll(){
        return this.loanService
                .getAll();
    }

    @RequestMapping(
            path="/loans",
            method = RequestMethod.DELETE,
            produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<Void> deleteAll(){
        return this.loanService
                .deleteAll();
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleException(InvalidParamException e) {
        e.printStackTrace();
        return new ErrorMessage(e.getMessage());
    }

    private boolean isIsbnValid(int isbnLength) {
        return (isbnLength == MAX_ISBN_LENGTH || isbnLength == MIN_ISBN_LENGTH);
    }
}
