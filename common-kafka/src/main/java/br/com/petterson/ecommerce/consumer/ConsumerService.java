package br.com.petterson.ecommerce.consumer;

import br.com.petterson.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService<T> {
    void parse(ConsumerRecord<String, Message<T>> record) throws Exception;
    String getTopics();
    String getConsumerGroup();
}
