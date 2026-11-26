package vn.iotstar.util;

import java.net.URI;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.servlet.http.Part;

/** Các quy tắc validation dùng chung cho những form của ứng dụng. */
public final class FormValidationUtil {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9._-]{4,30}$");
    private static final Pattern FULL_NAME = Pattern.compile("^[\\p{L}][\\p{L}\\p{M} .'-]{1,99}$");
    private static final Pattern VIDEO_ID = Pattern.compile("^[A-Za-z0-9_-]{1,50}$");
    private static final Pattern LOCAL_IMAGE = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9._-]{0,254}$");
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private FormValidationUtil() { }

    public static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isEmail(String value) {
        return hasText(value) && value.length() <= 254 && EMAIL.matcher(value.trim()).matches();
    }

    public static boolean isUsername(String value) {
        return value != null && USERNAME.matcher(value.trim()).matches();
    }

    public static boolean isFullName(String value) {
        return value != null && FULL_NAME.matcher(value.trim()).matches();
    }

    public static boolean isOptionalPhone(String value) {
        if (!hasText(value)) return true;
        String digits = value.replaceAll("[\\s().-]", "");
        if (digits.startsWith("+")) digits = digits.substring(1);
        return digits.matches("[0-9]{9,15}") && value.matches("[+0-9\\s().-]+");
    }

    public static boolean isPassword(String value) {
        return value != null && !value.isBlank()
                && value.length() >= Constant.PASSWORD_MIN_LENGTH && value.length() <= 128;
    }

    public static boolean isVideoId(String value) {
        return value != null && VIDEO_ID.matcher(value.trim()).matches();
    }

    public static boolean isValidStatus(String value) {
        return "0".equals(value) || "1".equals(value);
    }

    /** Chấp nhận URL HTTP(S) hoặc tên file cục bộ an toàn (ví dụ avatar.png). */
    public static boolean isImageReference(String value) {
        if (!hasText(value)) return true;
        String candidate = value.trim();
        if (candidate.length() > 500 || candidate.contains("..")) return false;
        if (candidate.matches("(?i)^https?://.*")) {
            try {
                URI uri = URI.create(candidate);
                return uri.getHost() != null && ("http".equalsIgnoreCase(uri.getScheme())
                        || "https".equalsIgnoreCase(uri.getScheme()));
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        if (!LOCAL_IMAGE.matcher(candidate).matches()) return false;
        int dot = candidate.lastIndexOf('.');
        return dot > 0 && IMAGE_EXTENSIONS.contains(candidate.substring(dot + 1).toLowerCase(Locale.ROOT));
    }

    public static boolean isValidImageUpload(Part part) {
        if (part == null || part.getSize() == 0) return true;
        String filename = part.getSubmittedFileName();
        if (!hasText(filename)) return false;
        String safeName = filename.replace('\\', '/');
        safeName = safeName.substring(safeName.lastIndexOf('/') + 1);
        int dot = safeName.lastIndexOf('.');
        if (dot < 1 || dot == safeName.length() - 1) return false;
        String ext = safeName.substring(dot + 1).toLowerCase(Locale.ROOT);
        String contentType = part.getContentType();
        return IMAGE_EXTENSIONS.contains(ext) && contentType != null && contentType.toLowerCase(Locale.ROOT).startsWith("image/");
    }

    public static String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
