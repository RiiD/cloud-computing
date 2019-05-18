package reactive_microservice_db;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface LoanService {
    public Mono<Loan> create(String isbn, Reader reader);
    public Mono<Loan> getByKey(String loanId);
    public Flux<Loan> getAll();
    public Flux<Loan> getbyIsbn(String isbn);
    public Flux<Loan> getReturned(String status);
    public Mono<Void> deleteAll();
    public Mono<Loan> track(String loanId);
    public Mono<Void> returnBook(String loanId);
    Flux<Loan> loanByEmail(String email);

    Flux<Loan> betweenDates(Date fromDate, Date toDate);
}
