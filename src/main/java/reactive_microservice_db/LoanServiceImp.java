package reactive_microservice_db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LoanServiceImp implements LoanService {

    private LoansDao loansDao;

    @Autowired
    public LoanServiceImp(LoansDao loansDao) {
        this.loansDao = loansDao;
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
                .findById(loanId);
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
}
