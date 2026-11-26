package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.UserService.OtpResult;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.CloudinaryUtil;
import vn.iotstar.util.Constant;
import vn.iotstar.util.FormValidationUtil;

@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 25)
@WebServlet(urlPatterns = { "/profile", "/profile/update" })
public class ProfileController extends HttpServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User sessionUser = (User) session.getAttribute("account");
        User freshUser = userService.get(sessionUser.getUserName());
        if (freshUser == null) {
            resp.sendRedirect(req.getContextPath() + "/logout");
            return;
        }
        session.setAttribute("account", freshUser);
        req.setAttribute("user", freshUser);

        String msg = req.getParameter("msg");
        if ("success".equals(msg)) {
            req.setAttribute("success", "Cập nhật thông tin cá nhân thành công!");
        } else if ("email_updated".equals(msg)) {
            req.setAttribute("success", "Xác thực OTP thành công! Email của bạn đã được cập nhật.");
        }

        req.getRequestDispatcher(Constant.Path.PROFILE).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User sessionUser = (User) session.getAttribute("account");
        User freshUser = userService.get(sessionUser.getUserName());
        if (freshUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String fullname = FormValidationUtil.safeTrim(req.getParameter("fullname"));
        String phone = FormValidationUtil.safeTrim(req.getParameter("phone"));
        String newEmail = FormValidationUtil.safeTrim(req.getParameter("email")).toLowerCase();

        // 1. Validation
        if (!FormValidationUtil.isFullName(fullname)) {
            showError(req, resp, freshUser, "Họ và tên phải dài 2–100 ký tự và không chứa ký tự đặc biệt.");
            return;
        }
        if (!FormValidationUtil.isOptionalPhone(phone)) {
            showError(req, resp, freshUser, "Số điện thoại không hợp lệ (từ 9–15 chữ số).");
            return;
        }
        if (!FormValidationUtil.isEmail(newEmail)) {
            showError(req, resp, freshUser, "Địa chỉ email không đúng định dạng.");
            return;
        }

        // 2. Upload Avatar (Cloudinary)
        try {
            Part avatarPart = req.getPart("avatar");
            if (avatarPart != null && avatarPart.getSize() > 0) {
                if (!FormValidationUtil.isValidImageUpload(avatarPart)) {
                    showError(req, resp, freshUser, "Chỉ chấp nhận file ảnh (PNG, JPG, JPEG, GIF, WEBP) và kích thước không quá 5MB.");
                    return;
                }
                String uploadedUrl = CloudinaryUtil.upload(avatarPart);
                if (uploadedUrl != null && !uploadedUrl.isBlank()) {
                    freshUser.setAvatar(uploadedUrl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError(req, resp, freshUser, "Lỗi khi tải ảnh đại diện lên Cloudinary: " + e.getMessage());
            return;
        }

        freshUser.setFullName(fullname);
        freshUser.setPhone(phone);

        // 3. Kiểm tra thay đổi Email
        boolean emailChanged = !newEmail.equalsIgnoreCase(freshUser.getEmail());
        if (emailChanged) {
            // Kiểm tra email mới đã bị tài khoản khác đăng ký chưa
            if (userService.checkExistEmail(newEmail)) {
                showError(req, resp, freshUser, "Email mới này đã được sử dụng bởi một tài khoản khác!");
                return;
            }

            // Gửi OTP xác nhận tới EMAIL MỚI
            OtpResult result = userService.sendUpdateEmailOtp(freshUser, newEmail);
            if (result == OtpResult.TOO_SOON) {
                showError(req, resp, freshUser, "Yêu cầu gửi OTP quá nhanh. Vui lòng chờ 60 giây.");
                return;
            } else if (result == OtpResult.MAIL_ERROR) {
                showError(req, resp, freshUser, "Không thể gửi email OTP đến địa chỉ mới. Vui lòng kiểm tra lại cấu hình mail.");
                return;
            }

            // Cập nhật thông tin (fullname, phone, avatar) trước, lưu email mới vào session để chờ OTP
            userService.update(freshUser);
            session.setAttribute("account", freshUser);
            session.setAttribute(Constant.SESSION_NEW_EMAIL, newEmail);

            resp.sendRedirect(req.getContextPath() + "/verify-email-otp");
            return;
        }

        // Nếu email không thay đổi -> Lưu thông tin trực tiếp
        userService.update(freshUser);
        session.setAttribute("account", freshUser);
        resp.sendRedirect(req.getContextPath() + "/profile?msg=success");
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, User user, String message)
            throws ServletException, IOException {
        req.setAttribute("alert", message);
        req.setAttribute("user", user);
        req.getRequestDispatcher(Constant.Path.PROFILE).forward(req, resp);
    }
}
