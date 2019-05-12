package reactive_microservice_db;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface LoansDao extends ReactiveMongoRepository<Loan,String> {

}
