package reactive_microservice_db;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

@Service
public class KafkaService implements KafkaServiceInterface {
	
	private String bootstrapServer;
	private String groupId;
	
	@Autowired
	public KafkaService(
			@Value("${spring.kafka.bootstrap-servers}") String bootstrapServer,
			@Value("${spring.kafka.consumer.group-id}") String groupId
			) {
		this.bootstrapServer = bootstrapServer;
		this.groupId = groupId;
	}
	
	public <K, V> Flux<ReceiverRecord<K, V>> getReceiverFor(String topic) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		
		ReceiverOptions<K, V> receiverOptions = ReceiverOptions.create(props);
		
		return KafkaReceiver
				.create(receiverOptions.subscription(Collections.singleton(topic)))
				.receive();
	}
	
	@Override
	public <K, V> KafkaSender<K, V> getSender() {
		Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return KafkaSender.create(SenderOptions.create(props));
	}
}
