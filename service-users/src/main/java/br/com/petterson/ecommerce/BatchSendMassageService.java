package br.com.petterson.ecommerce;

import br.com.petterson.ecommerce.consumer.KafkaService;
import br.com.petterson.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class BatchSendMassageService {

    private final Connection connection;

    BatchSendMassageService() throws SQLException {
        var url = "jdbc:sqlite:target/users_database.db";
        connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users(" +
                    "uuid varchar(200) primary key," +
                    "email varchar(200))");
        }catch (SQLException e){
            // be careful, the sql could be wrong, be really careful
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, SQLException {
        var batchSendMassageService = new BatchSendMassageService();
        try(var service = new KafkaService<>(BatchSendMassageService.class.getSimpleName(),
                "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS",
                batchSendMassageService::parse,
                Map.of())) {
            service.run();
        }
    }

    private final KafkaDispatcher<User> userDispatcher = new KafkaDispatcher<>();

    private void parse(ConsumerRecord<String, Message<String>> record) throws ExecutionException, InterruptedException, SQLException {
        var message = record.value();

        System.out.println("-----------------------------------");
        System.out.println("Processing new batch");
        System.out.println("Topic: " + message.getPayload());

        for(User user: getAllUsers()){
            userDispatcher.sendAsync(message.getPayload(), user.getUuid(), message.getId().continueWith(BatchSendMassageService.class.getSimpleName()), user);
        }
    }

    private List<User> getAllUsers() throws SQLException {
        var results = connection.prepareStatement("select uuid from Users").executeQuery();
        List<User> users = new ArrayList<>();
        while (results.next()){
            users.add(new User(results.getString(1)));
        }
        return users;
    }
}
