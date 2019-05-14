package reactive_microservice_db;

import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;

public interface KafkaServiceInterface {
	public <K, V> Flux<ReceiverRecord<K, V>> getReceiverFor(String topic);
	public <K, V> KafkaSender<K, V> getSender();
}
