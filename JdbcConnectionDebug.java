import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcConnectionDebug {

    public static void main(String[] args) {
        String host = "localhost";
        int port = 3306;
        String database = "test_db";
        String user = "root";
        String password = "your_password";

        System.out.println("=== MySQL JDBC Connection Debug ===\n");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("[OK] JDBC Driver loaded successfully\n");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERROR] JDBC Driver not found!");
            System.err.println("  Please make sure MySQL Connector/J is in your classpath.");
            e.printStackTrace();
            return;
        }

        String[] urls = {
            "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&connectTimeout=5000&socketTimeout=5000",
            "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&connectTimeout=5000&socketTimeout=5000",
            "jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&connectTimeout=5000&socketTimeout=5000"
        };

        for (int i = 0; i < urls.length; i++) {
            System.out.println("\n--- Testing URL " + (i + 1) + " ---");
            System.out.println("URL: " + urls[i]);
            
            Connection conn = null;
            try {
                Properties props = new Properties();
                props.setProperty("user", user);
                props.setProperty("password", password);
                
                conn = DriverManager.getConnection(urls[i], props);
                System.out.println("[SUCCESS] Connection established!");
                
                System.out.println("\nConnection Info:");
                System.out.println("  Database URL: " + urls[i]);
                System.out.println("  Database Product: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("  Database Version: " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("  JDBC Driver: " + conn.getMetaData().getDriverName());
                System.out.println("  Driver Version: " + conn.getMetaData().getDriverVersion());
                
                break;
            } catch (SQLException e) {
                System.err.println("[ERROR] Connection failed!");
                System.err.println("  SQL State: " + e.getSQLState());
                System.err.println("  Error Code: " + e.getErrorCode());
                System.err.println("  Message: " + e.getMessage());
                
                if (e.getMessage().contains("Communications link failure")) {
                    System.err.println("\n  Possible causes:");
                    System.err.println("  1. MySQL server is not running on " + host + ":" + port);
                    System.err.println("  2. Firewall is blocking the connection");
                    System.err.println("  3. Incorrect host or port");
                } else if (e.getMessage().contains("Access denied")) {
                    System.err.println("\n  Possible causes:");
                    System.err.println("  1. Incorrect username or password");
                    System.err.println("  2. User doesn't have permission to access the database");
                } else if (e.getMessage().contains("Unknown database")) {
                    System.err.println("\n  Possible causes:");
                    System.err.println("  1. Database '" + database + "' does not exist");
                    System.err.println("  2. Database name is case-sensitive on some systems");
                } else if (e.getMessage().contains("time zone")) {
                    System.err.println("\n  Time zone issue - trying next URL with different time zone");
                }
                
                if (i < urls.length - 1) {
                    System.out.println("\nTrying next URL configuration...");
                }
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                        System.out.println("\nConnection closed.");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
