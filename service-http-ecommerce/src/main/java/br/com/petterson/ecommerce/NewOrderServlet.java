package br.com.petterson.ecommerce;

import br.com.petterson.ecommerce.dispatcher.KafkaDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var email = req.getParameter("email");
            var amount = new BigDecimal(req.getParameter("amount"));
            var orderId= req.getParameter("uuid");

            var order = new Order(orderId, amount, email);

            try (var database = new OrdersDatabase()){
                if(database.saveNew(order)) {

                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email,
                            new CorrelationId(NewOrderServlet.class.getSimpleName()),
                            order);

                    System.out.println("New order sent sucessfully.");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("New order sent sucessfully.");
                }else{
                    System.out.println("Old order received.");
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().println("Old order received.");
                }
            }


        } catch (ExecutionException | InterruptedException | SQLException e) {
            throw new ServletException(e);
        }
    }
}
