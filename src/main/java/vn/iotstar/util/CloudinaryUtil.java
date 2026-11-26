package vn.iotstar.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.http.Part;

/**
 * Tiện ích upload hình ảnh lên Cloudinary sử dụng Multipart.
 * Có cơ chế fallback lưu trữ cục bộ khi chưa cấu hình API Key để đảm bảo hệ thống không bị gián đoạn.
 */
public final class CloudinaryUtil {
    private static final Properties CONFIG = loadConfig();
    private static Cloudinary cloudinary;

    static {
        initCloudinary();
    }

    private CloudinaryUtil() {
    }

    private static Properties loadConfig() {
        Properties p = new Properties();
        try (InputStream in = CloudinaryUtil.class.getClassLoader().getResourceAsStream("cloudinary.properties")) {
            if (in != null) {
                p.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            System.err.println("[CLOUDINARY] Không đọc được cloudinary.properties: " + e.getMessage());
        }
        return p;
    }

    private static void initCloudinary() {
        String cloudinaryUrl = System.getenv("CLOUDINARY_URL");
        if (cloudinaryUrl != null && !cloudinaryUrl.isBlank()) {
            cloudinary = new Cloudinary(cloudinaryUrl.trim());
            System.out.println("[CLOUDINARY] Khởi tạo từ biến môi trường CLOUDINARY_URL.");
            return;
        }

        String cloudName = getProperty("cloudinary.cloud_name", "CLOUDINARY_CLOUD_NAME", null);
        String apiKey = getProperty("cloudinary.api_key", "CLOUDINARY_API_KEY", null);
        String apiSecret = getProperty("cloudinary.api_secret", "CLOUDINARY_API_SECRET", null);

        if (cloudName != null && !cloudName.isBlank() 
                && apiKey != null && !apiKey.isBlank() 
                && apiSecret != null && !apiSecret.isBlank()) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName.trim(),
                    "api_key", apiKey.trim(),
                    "api_secret", apiSecret.trim(),
                    "secure", true
            ));
            System.out.println("[CLOUDINARY] Khởi tạo Cloudinary thành công với cloud_name: " + cloudName);
        } else {
            System.out.println("[CLOUDINARY][DEV] Chưa cấu hình Cloudinary API. Sẽ sử dụng lưu trữ cục bộ dự phòng (Fallback).");
        }
    }

    private static String getProperty(String key, String envName, String defaultValue) {
        String v = System.getenv(envName);
        if (v == null || v.isBlank()) {
            v = CONFIG.getProperty(key);
        }
        return (v == null || v.isBlank()) ? defaultValue : v.trim();
    }

    /**
     * Upload file Part lên Cloudinary hoặc lưu cục bộ nếu chưa cấu hình Cloudinary.
     * @param filePart Multipart Part nhận được từ form request
     * @return URL của ảnh (nếu upload Cloudinary thành công) hoặc tên file cục bộ (nếu dùng fallback)
     */
    public static String upload(Part filePart) throws Exception {
        if (filePart == null || filePart.getSize() == 0) {
            return null;
        }

        String originalName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        int dot = originalName.lastIndexOf('.');
        String ext = dot >= 0 ? originalName.substring(dot) : ".png";

        // 1. Nếu đã cấu hình Cloudinary: Upload trực tiếp lên Cloudinary bằng multipart byte array
        if (cloudinary != null) {
            try (InputStream is = filePart.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                Map<?, ?> uploadResult = cloudinary.uploader().upload(bytes, ObjectUtils.asMap(
                        "folder", "shopping_avatars",
                        "resource_type", "image"
                ));
                String secureUrl = (String) uploadResult.get("secure_url");
                if (secureUrl != null && !secureUrl.isBlank()) {
                    System.out.println("[CLOUDINARY] Upload thành công lên Cloudinary: " + secureUrl);
                    return secureUrl;
                }
            } catch (Exception e) {
                System.err.println("[CLOUDINARY] Lỗi khi upload lên Cloudinary (" + e.getMessage() + "), chuyển sang lưu cục bộ.");
            }
        }

        // 2. Che do Fallback: Luu vao thu muc avatar cuc bo
        String uniqueName = "avatar_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
        Path uploadDir = Paths.get(Constant.DIR, "avatar");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path targetPath = uploadDir.resolve(uniqueName);
        try (InputStream is = filePart.getInputStream()) {
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        System.out.println("[CLOUDINARY][FALLBACK] Đã lưu ảnh cục bộ: " + targetPath.toString());
        return uniqueName;
    }
}
