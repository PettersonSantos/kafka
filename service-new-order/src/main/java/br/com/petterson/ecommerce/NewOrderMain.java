package br.com.petterson.ecommerce;

import br.com.petterson.ecommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<Order>()) {
            var email = Math.random() + "@email.com";
            for (int i = 0; i < 10; i++) {
                var orderId = UUID.randomUUID().toString();
                var amount = BigDecimal.valueOf(Math.random() * 5000 + 1);

                var order = new Order(orderId, amount, email);

                orderDispatcher.send("ECOMMERCE_NEW_ORDER",
                        email,
                        new CorrelationId(NewOrderMain.class.getSimpleName()),
                        order);

            }
        }
    }
}
