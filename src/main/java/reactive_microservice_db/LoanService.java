package reactive_microservice_db;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanService {
    public Mono<Loan> create(String isbn, Reader reader);
    public Mono<Loan> getByKey(String loanId);
    public Flux<Loan> getAll();
    public Mono<Void> deleteAll();
    public Mono<Loan> track(String loanId);
    public Mono<Void> returnBook(String loanId);
}
