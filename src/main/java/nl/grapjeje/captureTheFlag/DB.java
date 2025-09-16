package nl.grapjeje.captureTheFlag;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

public class DB {
    @Getter
    private static HikariDataSource dataSource;

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

        this.url = "jdbc:mysql://" + host + ":" + port + "/" + db
                + "?useSSL=false"
                + "&allowPublicKeyRetrieval=true"
                + "&serverTimezone=UTC";

        this.connectWithHikari();
    }

    private void connectWithHikari() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(pass);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // ---- Pool sizing ----
            config.setMaximumPoolSize(32);
            config.setMinimumIdle(4);

            // ---- Timeouts ----
            config.setConnectionTimeout(5_000);
            config.setInitializationFailTimeout(-1);

            // ---- Lifecycle ----
            config.setKeepaliveTime(120_000);
            config.setIdleTimeout(240_000);
            config.setMaxLifetime(1_200_000);

            // ---- MySQL driver props ----
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            // ---- Nice-to-have ----
            config.addDataSourceProperty("applicationName", "capture-the-flag");

            dataSource = new HikariDataSource(config);

            Main.getInstance().getLogger().info("Connected to database via HikariCP");
        } catch (Exception ex) {
            ex.printStackTrace();
            Main.getInstance().disablePlugin();
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) {
            try {
                dataSource.close();
                Main.getInstance().getLogger().info("HikariCP pool closed");
            } catch (Exception ex) {
                ex.printStackTrace();
                Main.getInstance().disablePlugin();
            }
        }
    }
}
