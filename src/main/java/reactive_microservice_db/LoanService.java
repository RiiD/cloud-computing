package reactive_microservice_db;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanService {

    public Mono<Loan> store(Loan loan);
    public Mono<Loan> getByKey(String loanId);
    public Flux<Loan> getAll();
    public Mono<Void> deleteAll();
}
