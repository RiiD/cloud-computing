package reactive_microservice_db;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class LoanController {
    private LoanService loanService;

    @Autowired
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @RequestMapping(
            path="/loans/{isbn:^\\d{10}|\\d{13}$}",
            method=RequestMethod.POST,
            consumes=MediaType.APPLICATION_JSON_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public Mono<Loan> create(
    		@PathVariable("isbn") String isbn,
            @Valid @RequestBody Reader reader) {
    	return this.loanService.create(isbn, reader);
    }
    
    @RequestMapping(
            path="/loans/{loanId}",
            method = RequestMethod.PUT)
    public Mono<Void> returnBook(@PathVariable("loanId") String loanId){
        return this.loanService
                .returnBook(loanId);
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
    
    @RequestMapping(
            path="/loans/{loanId}/track",
            method = RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public Mono<Loan> track(@PathVariable("loanId") String loanId){
        return this.loanService.track(loanId);
    }
    
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage handleException(NotFoundException e) {
        return new ErrorMessage(e.getMessage());
    }
    
    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleException(InvalidParamException e) {
        return new ErrorMessage(e.getMessage());
    }
}
