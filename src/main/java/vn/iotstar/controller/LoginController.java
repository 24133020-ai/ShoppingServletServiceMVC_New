package vn.iotstar.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.UserService.OtpResult;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.FormValidationUtil;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/login")
public class LoginController extends HttpServlet {
    private UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("account") != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        // Cookie "Nhớ tôi" chỉ dùng để điền sẵn tên đăng nhập.
        // (Không tự động đăng nhập bằng cookie: ai cũng có thể giả cookie username=admin.)
        String remembered = readRememberedUsername(req);
        if (remembered != null) {
            req.setAttribute("rememberedUsername", remembered);
        }

        // Thông báo thành công sau khi redirect về (chỉ nhận các mã cố định, không in lại input người dùng)
        String msg = req.getParameter("msg");
        if ("activated".equals(msg)) {
            req.setAttribute("success", "Kích hoạt tài khoản thành công! Hãy đăng nhập.");
        } else if ("reset".equals(msg)) {
            req.setAttribute("success", "Đặt lại mật khẩu thành công! Hãy đăng nhập bằng mật khẩu mới.");
        } else if ("logout".equals(msg)) {
            req.setAttribute("success", "Bạn đã đăng xuất.");
        }
        req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        boolean isRememberMe = "on".equals(req.getParameter("remember"));

        username = username == null ? "" : username.trim();
        if (!FormValidationUtil.hasText(username) || username.length() > 50) {
            showError(req, resp, username, "Vui lòng nhập tài khoản hợp lệ (tối đa 50 ký tự).");
            return;
        }
        if (!FormValidationUtil.hasText(password) || password.length() > 128) {
            showError(req, resp, username, "Vui lòng nhập mật khẩu (tối đa 128 ký tự).");
            return;
        }

        User user = service.login(username, password);
        if (user == null) {
            showError(req, resp, username, "Tài khoản hoặc mật khẩu không đúng");
            return;
        }

        // Mật khẩu đúng nhưng chưa kích hoạt -> gửi lại OTP và chuyển sang trang nhập OTP
        if (!user.isActive()) {
            HttpSession session = req.getSession(true);
            session.setAttribute(Constant.SESSION_OTP_EMAIL, user.getEmail());
            OtpResult r = service.sendOtp(user.getEmail(), Constant.PURPOSE_ACTIVATE);
            String code = (r == OtpResult.MAIL_ERROR) ? "mailerror" : (r == OtpResult.TOO_SOON ? "toosoon" : "inactive");
            resp.sendRedirect(req.getContextPath() + "/verify-otp?msg=" + code);
            return;
        }

        // Đăng nhập thành công: tạo session mới (chống session fixation)
        HttpSession old = req.getSession(false);
        if (old != null) {
            old.invalidate();
        }
        HttpSession session = req.getSession(true);
        session.setAttribute("account", user);
        session.setAttribute(Constant.SESSION_USERNAME, user.getUserName());

        if (isRememberMe) {
            saveRememberMe(req, resp, user.getUserName());
        } else {
            clearRememberMe(req, resp);
        }
        resp.sendRedirect(req.getContextPath() + "/waiting");
    }

    private void showError(HttpServletRequest req, HttpServletResponse resp, String username, String message)
            throws ServletException, IOException {
        req.setAttribute("alert", message);
        if (username != null) {
            req.setAttribute("rememberedUsername", username.trim());
        }
        req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
    }

    private String readRememberedUsername(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constant.COOKIE_REMEMBER.equals(cookie.getName())) {
                    try {
                        return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    private void saveRememberMe(HttpServletRequest req, HttpServletResponse resp, String username) {
        Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, URLEncoder.encode(username, StandardCharsets.UTF_8));
        cookie.setMaxAge(30 * 60); // Lưu 30 phút
        cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        cookie.setHttpOnly(true);
        resp.addCookie(cookie);
    }

    private void clearRememberMe(HttpServletRequest req, HttpServletResponse resp) {
        Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, "");
        cookie.setMaxAge(0);
        cookie.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
        resp.addCookie(cookie);
    }
}
