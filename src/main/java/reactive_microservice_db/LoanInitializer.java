package reactive_microservice_db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        Reader reader1 = new Reader("11","1@gmail.com","cloud","course");
        Reader reader2 = new Reader("11","2@gmail.com","cloud","course");
        Reader reader3 = new Reader("11","3@gmail.com","cloud","course");
        Reader reader4 = new Reader("11","4@gmail.com","cloud","course");

        loanService.create("1000100010001", reader1)
                .subscribe(System.err:: println);

        loanService.create("1000100010012", reader1)
                .subscribe(System.err:: println);


        loanService.create("1000100010013", reader1)
                .subscribe(System.err:: println);

        loanService.create("1000100010014", reader1)
                .subscribe(System.err:: println);

        Thread.sleep(5000);

//        loanService.loanByEmail("1@gmail.com")
//                .subscribe(System.err:: println);



        loanService.betweenDates(parseDate("2019-05-01"), parseDate("2019-06-01"))
                .subscribe(System.err:: println);

//        loanService.store(new Loan("F16"))
//                .subscribe(System.err:: println);
        }

    public Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }


}
