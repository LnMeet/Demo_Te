import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchTableData {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/test_db?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            String tableName = "users";
            List<Map<String, Object>> results = fetchTableData(conn, tableName);

            System.out.println("=== Table: " + tableName + " ===");
            System.out.println("Total rows: " + results.size());
            System.out.println();

            if (results.isEmpty()) {
                System.out.println("No data found in table.");
            } else {
                printResults(results);
            }

        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database error!");
            e.printStackTrace();
        } finally {
            closeQuietly(rs);
            closeQuietly(pstmt);
            closeQuietly(conn);
        }
    }

    public static List<Map<String, Object>> fetchTableData(Connection conn, String tableName) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();

        String sql = "SELECT * FROM " + tableName;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            List<String> columnNames = new ArrayList<String>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            while (rs.next()) {
                Map<String, Object> row = new HashMap<String, Object>();
                for (int i = 0; i < columnCount; i++) {
                    String columnName = columnNames.get(i);
                    Object value = rs.getObject(columnName);
                    row.put(columnName, value);
                }
                results.add(row);
            }

        } finally {
            closeQuietly(rs);
            closeQuietly(pstmt);
        }

        return results;
    }

    public static void printResults(List<Map<String, Object>> results) {
        if (results.isEmpty()) {
            return;
        }

        Map<String, Object> firstRow = results.get(0);
        String[] columnNames = firstRow.keySet().toArray(new String[0]);

        int[] columnWidths = new int[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            columnWidths[i] = columnNames[i].length();
            for (Map<String, Object> row : results) {
                Object value = row.get(columnNames[i]);
                String strValue = value == null ? "null" : value.toString();
                if (strValue.length() > columnWidths[i]) {
                    columnWidths[i] = strValue.length();
                }
            }
            columnWidths[i] = Math.min(columnWidths[i], 30);
        }

        StringBuilder headerLine = new StringBuilder("+");
        for (int width : columnWidths) {
            for (int j = 0; j < width + 2; j++) {
                headerLine.append("-");
            }
            headerLine.append("+");
        }

        System.out.println(headerLine.toString());

        StringBuilder header = new StringBuilder("|");
        for (int i = 0; i < columnNames.length; i++) {
            header.append(" ").append(padRight(columnNames[i], columnWidths[i])).append("|");
        }
        System.out.println(header.toString());

        System.out.println(headerLine.toString());

        for (Map<String, Object> row : results) {
            StringBuilder rowStr = new StringBuilder("|");
            for (int i = 0; i < columnNames.length; i++) {
                Object value = row.get(columnNames[i]);
                String strValue = value == null ? "null" : value.toString();
                if (strValue.length() > columnWidths[i]) {
                    strValue = strValue.substring(0, columnWidths[i] - 3) + "...";
                }
                rowStr.append(" ").append(padRight(strValue, columnWidths[i])).append("|");
            }
            System.out.println(rowStr.toString());
        }

        System.out.println(headerLine.toString());
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    private static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
