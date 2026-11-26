package vn.iotstar.util;

public class Constant {
    // Thư mục lưu ảnh upload
    public static final String DIR = "C:\\upload";

    // Session & Cookie keys
    public static final String SESSION_USERNAME = "username";
    public static final String COOKIE_REMEMBER = "username";

    // Session key lưu email đang chờ xác thực OTP (kích hoạt / quên mật khẩu / đổi email)
    public static final String SESSION_OTP_EMAIL = "otpEmail";
    public static final String SESSION_RESET_EMAIL = "resetEmail";
    public static final String SESSION_NEW_EMAIL = "pendingNewEmail";

    // OTP
    public static final String PURPOSE_ACTIVATE = "ACTIVATE";
    public static final String PURPOSE_RESET = "RESET";
    public static final String PURPOSE_UPDATE_EMAIL = "UPDATE_EMAIL";
    public static final int OTP_LENGTH = 6;
    public static final int OTP_EXPIRE_MINUTES = 5;      // OTP có hiệu lực 5 phút
    public static final int OTP_MAX_ATTEMPTS = 5;        // nhập sai tối đa 5 lần
    public static final int OTP_RESEND_SECONDS = 60;     // gửi lại tối thiểu cách nhau 60 giây

    // Mật khẩu
    public static final int PASSWORD_MIN_LENGTH = 6;

    // Video
    public static final int HOME_VIDEO_COUNT = 10;   // trang chủ hiển thị 10 video mới nhất
    public static final int VIDEO_PAGE_SIZE = 6;     // trang /video phân trang 6 video / trang

    // View paths
    public static final String REGISTER = "/views/register.jsp";

    public static class Path {
        public static final String REGISTER = "/views/register.jsp";
        public static final String LOGIN = "/views/login.jsp";
        public static final String VERIFY_OTP = "/views/verify-otp.jsp";
        public static final String FORGOT_PASSWORD = "/views/forgot-password.jsp";
        public static final String RESET_PASSWORD = "/views/reset-password.jsp";
        public static final String VIDEO_LIST = "/views/video-list.jsp";
        public static final String VIDEO_ADD = "/views/video-add.jsp";
        public static final String VIDEO_EDIT = "/views/video-edit.jsp";
        public static final String VIDEO_DETAIL = "/views/video-detail.jsp";
        public static final String PROFILE = "/views/profile.jsp";
        public static final String VERIFY_EMAIL_OTP = "/views/verify-email-otp.jsp";
        public static final String ADMIN_USER_LIST = "/views/admin/user-list.jsp";
    }
}
