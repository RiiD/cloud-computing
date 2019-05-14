package reactive_microservice_db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LoanInitializer implements CommandLineRunner {

    private LoanService loanService;

    @Autowired
    public LoanInitializer(LoanService loanService) {
        super();
        this.loanService = loanService;
    }

    @Override
    public void run(String... args) throws Exception {
        System.err.println("Initializing...");
        this.loanService.deleteAll().block();

        System.err.println("Invoked...");

        Reader reader = new Reader("11","o@gmail.com","cloud","course");

        loanService.store(
                new Loan(
                "100010001000", reader))
                .subscribe(System.err:: println);


        loanService.store(
                new Loan(
                        "100010001001", reader))
                .subscribe(System.err:: println);



//        loanService.store(new Loan("F16"))
//                .subscribe(System.err:: println);
        }


}
