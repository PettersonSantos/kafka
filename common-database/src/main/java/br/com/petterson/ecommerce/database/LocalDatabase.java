package br.com.petterson.ecommerce.database;

import java.sql.*;
import java.util.UUID;

public class LocalDatabase {

    private final Connection connection;

    public LocalDatabase(String name) throws SQLException {
        var url = "jdbc:sqlite:target/"+name+".db";
        connection = DriverManager.getConnection(url);
    }

    //yes, this is way too generic
    public void createIfNotExists(String sql){
        try {
            connection.createStatement().execute(sql);
        }catch (SQLException e){
            // be careful, the sql could be wrong, be really careful
            e.printStackTrace();
        }
    }

    public boolean update(String steatement, String ... params) throws SQLException {
        return prepare(steatement, params).execute();
    }

    private PreparedStatement prepare(String steatement, String[] params) throws SQLException {
        var preparedStatement = connection.prepareStatement(steatement);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i + 1, params[i]);
        }
        return preparedStatement;
    }

    public ResultSet query(String query, String ... params) throws SQLException {
        return prepare(query, params).executeQuery();
    }

    public void close() throws SQLException {
        connection.close();
    }
}
