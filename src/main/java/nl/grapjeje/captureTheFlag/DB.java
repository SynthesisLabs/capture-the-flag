package nl.grapjeje.captureTheFlag;

import lombok.Getter;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    @Getter
    private static Connection connection;

    private final String url;
    private final String user;
    private final String pass;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver loaded");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("MySQL Driver not found!");
        }
    }

    public DB() {
        String host = Main.getFileConfig().getString("database.host", "localhost");
        int port = Main.getFileConfig().getInt("database.port", 3306);
        String db = Main.getFileConfig().getString("database.database", "capture_the_flag");
        this.user = Main.getFileConfig().getString("database.user", "root");
        this.pass = Main.getFileConfig().getString("database.password", "");

        this.url = "jdbc:mysql://" + host + ":" + port + "/" + db;

        this.migrate();
        this.connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(url, user, pass);
            Main.getInstance().getLogger().info("Connected to database");
        } catch (SQLException ex) {
            ex.printStackTrace();
            Main.getInstance().disablePlugin();
        }
    }

    private void migrate() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(url, user, pass)
                    .locations("classpath:db/migration")
                    .load();

            flyway.migrate();
            Main.getInstance().getLogger().info("Migrated database");
        } catch (Exception ex) {
            ex.printStackTrace();
            Main.getInstance().disablePlugin();
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                Main.getInstance().getLogger().info("Connection closed");
            } catch (SQLException ex) {
                ex.printStackTrace();
                Main.getInstance().disablePlugin();
            }
        }
    }
}
