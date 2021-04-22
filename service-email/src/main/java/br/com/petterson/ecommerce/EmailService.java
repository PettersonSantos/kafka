package br.com.petterson.ecommerce;

import br.com.petterson.ecommerce.consumer.ConsumerService;
import br.com.petterson.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class EmailService implements ConsumerService<String> {

    public static void main(String[] args) {
        new ServiceRunner(EmailService::new).start(5);
    }

    public String getConsumerGroup(){
        return EmailService.class.getSimpleName();
    }

    public String getTopics(){
        return "ECOMMERCE_SEND_EMAIL";
    }

    public void parse(ConsumerRecord<String, Message<String>> record) {
        System.out.println("-----------------------------------");
        System.out.println("Processing send email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //ignoring
            e.printStackTrace();
        }

        System.out.println("Email sent");
    }
}
