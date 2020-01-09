package finalproject.airbnb.managers;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {

    private static final String DB_HOSTNAME = "127.0.0.1";
    private static final String DB_PORT = "3306";
    private static final String SCHEMA_NAME = "airbnb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "0000";

    private static DBManager mInstance;

    private Connection connection;

    private DBManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not loaded! " + e.getMessage());
        }
        connection = createConnection();
    }

    public Connection getConnection() {
        try {
            if(connection.isClosed()){
                connection = createConnection();
            }
        } catch (SQLException e) {
            System.out.println("Cannot establish connection!");
        }
        return connection;
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://" +
                    DB_HOSTNAME + ":" + DB_PORT + "/" + SCHEMA_NAME, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection failed! " + e.getMessage());
            return null;
        }
    }

    public synchronized static DBManager getInstance() {
        if (mInstance == null) {
            mInstance = new DBManager();
        }
        return mInstance;
    }

}
