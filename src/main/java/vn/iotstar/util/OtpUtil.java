package vn.iotstar.util;

import java.security.SecureRandom;

public final class OtpUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpUtil() {
    }

    /** Sinh mã OTP gồm {@link Constant#OTP_LENGTH} chữ số (giữ cả số 0 đứng đầu). */
    public static String generate() {
        int bound = (int) Math.pow(10, Constant.OTP_LENGTH);
        return String.format("%0" + Constant.OTP_LENGTH + "d", RANDOM.nextInt(bound));
    }
}
