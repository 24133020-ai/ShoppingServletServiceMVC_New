package vn.iotstar.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Băm mật khẩu bằng PBKDF2-HMAC-SHA256 + salt ngẫu nhiên (chỉ dùng JDK, không cần thư viện ngoài).
 * Định dạng lưu trong DB:  pbkdf2$<số vòng lặp>$<salt base64>$<hash base64>
 * Mỗi lần băm cùng một mật khẩu sẽ ra chuỗi khác nhau (do salt khác nhau).
 */
public final class PasswordUtil {
    private static final String PREFIX = "pbkdf2";
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    /** Băm mật khẩu thô thành chuỗi để lưu vào DB. */
    public static String hash(String rawPassword) {
        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(rawPassword.toCharArray(), salt, ITERATIONS);
        Base64.Encoder enc = Base64.getEncoder();
        return PREFIX + "$" + ITERATIONS + "$" + enc.encodeToString(salt) + "$" + enc.encodeToString(hash);
    }

    /** Kiểm tra mật khẩu thô có khớp với chuỗi đã băm trong DB không. */
    public static boolean matches(String rawPassword, String stored) {
        if (rawPassword == null || !isHashed(stored)) {
            return false;
        }
        try {
            String[] parts = stored.split("\\$");
            if (parts.length != 4) {
                return false;
            }
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = pbkdf2(rawPassword.toCharArray(), salt, iterations);
            return MessageDigest.isEqual(expected, actual); // so sánh hằng thời gian
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /** Chuỗi trong DB đã ở dạng hash chưa (false = mật khẩu cũ dạng plain-text). */
    public static boolean isHashed(String stored) {
        return stored != null && stored.startsWith(PREFIX + "$");
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_BITS);
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Không thể băm mật khẩu", e);
        }
    }

    /** So sánh 2 chuỗi (vd. OTP) trong thời gian không phụ thuộc nội dung. */
    public static boolean safeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}
