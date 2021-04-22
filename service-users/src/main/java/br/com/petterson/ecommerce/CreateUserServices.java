package br.com.petterson.ecommerce;

import br.com.petterson.ecommerce.consumer.ConsumerService;
import br.com.petterson.ecommerce.consumer.ServiceRunner;
import br.com.petterson.ecommerce.database.LocalDatabase;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.SQLException;
import java.util.UUID;

public class CreateUserServices implements ConsumerService<Order> {

    private final LocalDatabase database;

    CreateUserServices() throws SQLException{
        this.database = new LocalDatabase("users_database");
        this.database.createIfNotExists("create table Users(" +
                "uuid varchar(200) primary key," +
                "email varchar(200))");
    }

    public static void main(String[] args) {
        new ServiceRunner<>(CreateUserServices::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("-----------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());
        var message = record.value();
        var order = message.getPayload();
        if(isNewUser(order.getEmail())){
            insertNewUser(order.getEmail());
        }
    }

    @Override
    public String getTopics() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserServices.class.getSimpleName();
    }

    private void insertNewUser(String email) throws SQLException {
        var uuid = UUID.randomUUID().toString();
        database.update("insert into Users (uuid, email)" +
                "values (?,?)", uuid, email);

        System.out.println("Usuario "+ email + " adicionado");
    }

    private boolean isNewUser(String email) throws SQLException {

        var results = database.query("select uuid from Users " +
                "where email = ? limit 1", email);


        return !results.next();
    }
}
