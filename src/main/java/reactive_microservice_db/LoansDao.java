package reactive_microservice_db;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

public interface LoansDao extends ReactiveMongoRepository<Loan,String> {
	
	Flux<Loan> findAllByIsbn(String isbn);
	Flux<Loan> findAllByReturnDateIsNotNull();

}
