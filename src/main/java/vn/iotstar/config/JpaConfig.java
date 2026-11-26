package vn.iotstar.config;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaConfig {
    private static EntityManagerFactory factory;

    public static synchronized EntityManager getEntityManager() {
        if (factory == null || !factory.isOpen()) {
            factory = Persistence.createEntityManagerFactory("jpa-hibernate-mysql", jpaProperties());
        }
        return factory.createEntityManager();
    }

    private static Map<String, Object> jpaProperties() {
        String user = System.getenv("DB_USER");
        if (user == null || user.isBlank()) {
            user = "sa";
        }
        String password = System.getenv("DB_PASSWORD");
        if (password == null || password.isBlank()) {
            password = "1234@a$";
        }

        Map<String, Object> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.user", user);
        properties.put("jakarta.persistence.jdbc.password", password);
        String url = System.getenv("DB_URL");
        if (url == null || url.isBlank()) {
            url = "jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;databaseName=ShoppingServiceMVC";
        }
        properties.put("jakarta.persistence.jdbc.url", url.trim());
        return properties;
    }

    public static synchronized void shutdown() {
        if (factory != null && factory.isOpen()) {
            factory.close();
        }
    }
}
