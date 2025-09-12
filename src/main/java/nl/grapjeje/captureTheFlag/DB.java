package nl.grapjeje.captureTheFlag;

import lombok.Getter;
import org.flywaydb.core.Flyway;

import java.sql.*;

public class DB {
    @Getter
    private static Connection connection;

    private final String url;
    private final String user;
    private final String pass;

    public DB() {
        String host = Main.getConfig().getString("database.host");
        int port = Main.getConfig().getInt("database.port");
        String db = Main.getConfig().getString("database.name");
        this.user = Main.getConfig().getString("database.user");
        this.pass = Main.getConfig().getString("database.password");

        this.url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=false&serverTimezone=UTC";

        this.migrate();
        this.connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to database");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void migrate() {
        Flyway flyway = Flyway.configure()
                .dataSource(url, user, pass)
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
        System.out.println("Migrated database");
    }

    public void close() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Connection closed");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
