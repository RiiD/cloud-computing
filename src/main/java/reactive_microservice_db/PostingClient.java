package reactive_microservice_db;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PostingClient {

    public static void main(String[] args) throws InterruptedException {
        WebClient webClient =
                WebClient.create("http://localhost:8090/loans");

        Flux<Loan> loans = webClient
                .get()
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(Loan.class);


        Reader reader = new Reader("11","o@gmail.com","cloud","course");

        Mono<Loan> newLoan =  webClient
                .post()
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject( new Loan(
                        "100010001000", reader)))
                .retrieve()
                .bodyToMono(Loan.class);

        newLoan
                .doOnNext(System.err::println)
                .doOnSuccessOrError((i1,i2)->System.err.println("done"))
                .block();

//        newLoan.subscribe(System.err::println);
    }
}
