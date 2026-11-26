package vn.iotstar.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Gửi email OTP qua SMTP (mặc định Gmail).
 * Cấu hình đọc theo thứ tự ưu tiên:
 *   1. Biến môi trường  MAIL_USERNAME, MAIL_PASSWORD, MAIL_HOST, MAIL_PORT
 *   2. File src/main/resources/mail.properties (KHÔNG commit lên GitHub - xem mail.properties.example)
 * Nếu chưa cấu hình username/password thì chạy chế độ DEV: in OTP ra console của Tomcat.
 */
public final class EmailUtil {
    private static final Properties CONFIG = loadConfig();

    private EmailUtil() {
    }

    private static Properties loadConfig() {
        Properties p = new Properties();
        try (InputStream in = EmailUtil.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (in != null) {
                p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            System.err.println("[MAIL] Không đọc được mail.properties: " + e.getMessage());
        }
        return p;
    }

    private static String get(String key, String envName, String defaultValue) {
        String v = System.getenv(envName);
        if (v == null || v.isBlank()) {
            v = CONFIG.getProperty(key);
        }
        return (v == null || v.isBlank()) ? defaultValue : v.trim();
    }

    /**
     * Gửi email chứa mã OTP.
     * @return true nếu gửi thành công (hoặc đang ở chế độ DEV), false nếu lỗi SMTP.
     */
    public static boolean sendOtp(String to, String otp, String purpose) {
        final String username = get("mail.username", "MAIL_USERNAME", null);
        final String password = get("mail.password", "MAIL_PASSWORD", null);

        if (username == null || password == null) {
            System.out.println("[MAIL][DEV] Chưa cấu hình SMTP -> không gửi mail thật. OTP cho "
                    + to + " (" + purpose + ") = " + otp);
            return true;
        }

        boolean activate = Constant.PURPOSE_ACTIVATE.equals(purpose);
        String subject = activate ? "Mã OTP kích hoạt tài khoản" : "Mã OTP đặt lại mật khẩu";
        String intro = activate ? "kích hoạt tài khoản" : "đặt lại mật khẩu";
        String html = "<div style=\"font-family:Arial,sans-serif;font-size:15px;color:#333\">"
                + "<p>Xin chào,</p>"
                + "<p>Mã OTP để " + intro + " của bạn là:</p>"
                + "<p style=\"font-size:30px;font-weight:bold;letter-spacing:6px;color:#0088cc\">" + otp + "</p>"
                + "<p>Mã có hiệu lực trong <b>" + Constant.OTP_EXPIRE_MINUTES + " phút</b>. "
                + "Không chia sẻ mã này cho bất kỳ ai.</p>"
                + "<p>Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email.</p>"
                + "</div>";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", get("mail.host", "MAIL_HOST", "smtp.gmail.com"));
        props.put("mail.smtp.port", get("mail.port", "MAIL_PORT", "587"));
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        try {
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username, "Shopping Service", "UTF-8"));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject, "UTF-8");
            message.setContent(html, "text/html; charset=UTF-8");
            Transport.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("[MAIL] Gửi email thất bại: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
