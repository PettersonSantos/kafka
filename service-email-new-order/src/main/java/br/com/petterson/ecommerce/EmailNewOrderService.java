package br.com.petterson.ecommerce;

import br.com.petterson.ecommerce.consumer.ConsumerService;
import br.com.petterson.ecommerce.consumer.ServiceRunner;
import br.com.petterson.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order>  {
    public static void main(String[] args) {
        new ServiceRunner(EmailNewOrderService::new).start(1);
    }

    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("-----------------------------------");
        System.out.println("Processing new order, preparing email");
        var message = record.value();
        System.out.println(record.value());

        var emailCode = "Thank you for your order! We are processing your order";
        var order = record.value().getPayload();
        var id = message.getId().continueWith(EmailNewOrderService.class.getSimpleName());
        emailDispatcher.send("ECOMMERCE_SEND_EMAIL",
                order.getEmail(),
                id,
                emailCode);
    }

    @Override
    public String getTopics() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }
}
