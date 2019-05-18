//package reactive_microservice_db;
//
//import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Flux;
//
//public class LoanClient {
//    public static void main(String[] args) throws InterruptedException {
//        WebClient webClient =
//                WebClient.create("http://localhost:8090/loans?fromDate=2019-05-01&toDate=2019-06-01");
//
//        Flux<Loan> loans = webClient
//                .get()
//                .accept(MediaType.TEXT_EVENT_STREAM)
//                .retrieve()
//                .bodyToFlux(Loan.class);
//
//        Thread.sleep(5000);
//
//
//        loans
//                .subscribe(
//                        System.err::println,
//                        e->e.printStackTrace(),
//                        ()->System.err.println("read all from service"));
//
//
//    }
//}
