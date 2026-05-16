import java.net.InetAddress;
import java.net.Socket;

public class NetworkCheck {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 3306;
        
        System.out.println("=== Network Connectivity Check ===\n");
        
        try {
            InetAddress address = InetAddress.getByName(host);
            System.out.println("Host: " + host);
            System.out.println("IP Address: " + address.getHostAddress());
            System.out.println("Reachable: " + address.isReachable(3000) + "\n");
        } catch (Exception e) {
            System.err.println("Failed to resolve host: " + e.getMessage() + "\n");
        }
        
        System.out.println("Testing TCP connection to " + host + ":" + port + "...");
        try (Socket socket = new Socket(host, port)) {
            System.out.println("[SUCCESS] TCP connection established!");
            System.out.println("MySQL server is running on " + host + ":" + port);
        } catch (Exception e) {
            System.err.println("[FAILED] TCP connection failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nPossible causes:");
            System.err.println("1. MySQL server is not running");
            System.err.println("2. Wrong port (default is 3306)");
            System.err.println("3. Firewall blocking the connection");
            System.err.println("4. MySQL bound to different interface (not 0.0.0.0 or 127.0.0.1)");
            System.err.println("\nPlease check:");
            System.err.println("- 'systemctl status mysql' or 'service mysql status'");
            System.err.println("- 'netstat -tlnp | grep 3306'");
            System.err.println("- MySQL configuration (my.cnf/my.ini) bind-address");
        }
    }
}
