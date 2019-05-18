package reactive_microservice_db;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

import java.util.Date;

public interface LoansDao extends ReactiveMongoRepository<Loan,String> {
	
	Flux<Loan> findAllByIsbn(String isbn);
	Flux<Loan> findAllByReturnDateIsNotNull();

    Flux<Loan> findAllByReader_email(String email);

	Flux<Loan> findByLoanDateBetween(Date fromDate, Date toDate);
}
