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
    public Mono<Loan> store(Loan loan) {
        if (loan.getLoanId() != null) {
            return
                    this.loansDao.existsById(loan.getLoanId())
                            .flatMap(bool->{
                                if (!bool) {
                                    return this.loansDao.save(loan);
                                }else{
                                    throw new RuntimeException("loan already exists with key");
                                }
                            });
        }else {
            return this.loansDao.save(loan);
        }

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
}
