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
        String host = Main.getFileConfig().getString("database.host", "localhost");
        int port = Main.getFileConfig().getInt("database.port", 3306);
        String db = Main.getFileConfig().getString("database.database", "capture_the_flag");
        this.user = Main.getFileConfig().getString("database.user", "root");
        this.pass = Main.getFileConfig().getString("database.password", "");

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
            Main.getInstance().disablePlugin();
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
            if (connection == null) return;
            connection.close();
            System.out.println("Connection closed");
        } catch (Exception ex) {
            ex.printStackTrace();
            Main.getInstance().disablePlugin();
        }
    }
}
