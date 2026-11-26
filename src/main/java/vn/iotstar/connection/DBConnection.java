package vn.iotstar.connection;

import java.sql.Connection;
import java.sql.DriverManager;

/** Kết nối JDBC dùng cho kiểm tra độc lập; thông tin đăng nhập lấy từ biến môi trường. */
public class DBConnection {
    public Connection getConnection() throws Exception {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        if (url == null || url.isBlank()) {
            url = "jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;databaseName=ShoppingServiceMVC";
        }
        if (user == null || user.isBlank()) {
            user = "sa";
        }
        if (password == null || password.isBlank()) {
            password = "1234@a$";
        }
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url, user, password);
    }

    public static void main(String[] args) {
        try (Connection conn = new DBConnection().getConnection()) {
            System.out.println("Kết nối CSDL SQL Server thành công!");
        } catch (Exception e) {
            System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
