package reactive_microservice_db;

import java.util.Date;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderRecord;

@Service
public class LoanServiceImp implements LoanService {
    private LoansDao loansDao;
    private KafkaServiceInterface kafkaService;

    @Autowired
    public LoanServiceImp(LoansDao loansDao, KafkaServiceInterface kafkaService) {
        this.loansDao = loansDao;
        this.kafkaService = kafkaService;
    }
    
    @Override
    public Mono<Loan> getByKey(String loanId) {
        return this.loansDao
                .findById(loanId)
                .switchIfEmpty(Mono.error(new NotFoundException()));
    }

    @Override
    public Flux<Loan> getAll() {
        return this.loansDao
                .findAll();
    }

    @Override
    public Mono<Void> deleteAll() {
        return this.loansDao
                .deleteAll();
    }
    
    @Override
    public Mono<Loan> track(String loanId) {
    	return getByKey(loanId)
    			.flatMap(l -> {
    				if (l.getReturnDate() == null) {
    					return kafkaService
    			    			.<String, String>getReceiverFor(loanId)
    			    			.log()
    			    			.take(1)
    			    			.single()
    			    			.doOnNext(r -> r.receiverOffset().acknowledge())
    			    			.then(getByKey(loanId));
    				} else {
    					return Mono.just(l);
    				}
    			});
    }
    
    @Override
    public Mono<Void> returnBook(String loanId) {
    	return getByKey(loanId)
	    	.flatMap(l -> {
	    		if (l.getReturnDate() == null) {
	    			l.setReturnDate(new Date());
	    			return this
		    			.loansDao
		    			.save(l)
		    			.and(kafkaService
	        				.<String, String>getSender()
	    	    			.send(Mono.just(SenderRecord.create(new ProducerRecord<String, String>(loanId, loanId, loanId), loanId)))
	    	    			.single());
	    		}
	    		
	    		return Mono.just(l);
	    	})
	    	.then();
    }
    
    @Override
    public Mono<Loan> create(String isbn, Reader reader) {
    	Loan loan = new Loan(isbn, reader);
    	return loansDao.insert(loan);
    }
}
