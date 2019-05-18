package reactive_microservice_db;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
			Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	private static final String DIGIT_REGEX = "\\d+";

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
    public Flux<Loan> getbyIsbn(String isbn) {
    	if (isbn.length() == 13 || isbn.length() == 10) {
            return this.loansDao
                    .findAllByIsbn(isbn)
                    .switchIfEmpty(Mono.error(new NotFoundException()));
    	}
    	else
    	{
			throw new RuntimeException("isbn not legal");
    	}
    }
    
	@Override
	public Flux<Loan> getReturned(String status) {
		if (status.equals("returned")){
	        return this.loansDao
	                .findAllByReturnDateIsNotNull()
	                .switchIfEmpty(Mono.error(new NotFoundException()));
		}
		else
		{
			throw new RuntimeException("status not legal");
		}
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
	public Flux<Loan> loanByEmail(String email) {
    	if(validateEmailAddress(email)){
			return this.loansDao.findAllByReader_email(email);
		}
		else{
			throw new RuntimeException("invalid Email Address");
		}
	}

	@Override
	public Flux<Loan> betweenDates(Date fromDate, Date toDate) {
		return this.loansDao.findByLoanDateBetween(fromDate, toDate);
	}

	@Override
    public Mono<Loan> create(String isbn, Reader reader) {
		if(isReaderValid(reader)){
			Loan loan = new Loan(isbn,reader);
			return this.loansDao.save(loan);
		}
		else{
			throw new RuntimeException("isbn/reader is invalid");
		}
    }

	private boolean isReaderValid(Reader reader) {
		return reader.getId().trim().matches(DIGIT_REGEX) &&
				validateEmailAddress(reader.getEmail()) &&
				reader.getFirstName() !=null &&
				reader.getLastName() != null ;


	}

	public  boolean validateEmailAddress(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
		return matcher.find();
	}
}
