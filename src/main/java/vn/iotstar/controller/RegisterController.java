package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
@WebServlet(urlPatterns = "/register")
public class RegisterController extends HttpServlet {
    private UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("account") != null) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }
        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        String username = FormValidationUtil.safeTrim(req.getParameter("username"));
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");
        String email = FormValidationUtil.safeTrim(req.getParameter("email")).toLowerCase();
        String fullname = FormValidationUtil.safeTrim(req.getParameter("fullname"));
        String phone = FormValidationUtil.safeTrim(req.getParameter("phone"));

        if (!FormValidationUtil.isUsername(username)) {
            fail(req, resp, "Tên tài khoản phải dài 4–30 ký tự, chỉ gồm chữ không dấu, số, dấu chấm, gạch dưới hoặc gạch ngang.", username, fullname, email, phone);
            return;
        }
        if (!FormValidationUtil.isFullName(fullname)) {
            fail(req, resp, "Họ tên phải dài 2–100 ký tự và chỉ chứa chữ cái, khoảng trắng hoặc dấu nối thông dụng.", username, fullname, email, phone);
            return;
        }
        if (!FormValidationUtil.isEmail(email)) {
            fail(req, resp, "Địa chỉ email không hợp lệ.", username, fullname, email, phone);
            return;
        }
        if (!FormValidationUtil.isOptionalPhone(phone)) {
            fail(req, resp, "Số điện thoại không hợp lệ (9–15 chữ số, có thể có dấu +, khoảng trắng hoặc dấu gạch nối).", username, fullname, email, phone);
            return;
        }
        if (!FormValidationUtil.isPassword(password)) {
            fail(req, resp, "Mật khẩu phải có từ " + Constant.PASSWORD_MIN_LENGTH + " đến 128 ký tự.", username, fullname, email, phone);
            return;
        }
        if (!password.equals(confirm)) {
            fail(req, resp, "Mật khẩu xác nhận không khớp.", username, fullname, email, phone);
            return;
        }
        if (service.checkExistEmail(email)) {
            fail(req, resp, "Email đã tồn tại! Nếu bạn chưa kích hoạt, hãy đăng nhập để nhận lại mã OTP.", username, fullname, email, phone);
            return;
        }
        if (service.checkExistUsername(username)) {
            fail(req, resp, "Tài khoản đã tồn tại!", username, fullname, email, phone);
            return;
        }

        User user = service.register(username, password, email, fullname.trim(), phone == null ? null : phone.trim());
        if (user == null) {
            fail(req, resp, "Không thể tạo tài khoản lúc này. Vui lòng thử lại.", username, fullname, email, phone);
            return;
        }

        // Gửi OTP kích hoạt tới email rồi chuyển sang trang nhập OTP
        OtpResult r = service.sendOtp(email, Constant.PURPOSE_ACTIVATE);
        req.getSession(true).setAttribute(Constant.SESSION_OTP_EMAIL, email);
        resp.sendRedirect(req.getContextPath() + "/verify-otp?msg=" + (r == OtpResult.OK ? "sent" : "mailerror"));
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String message,
                      String username, String fullname, String email, String phone)
            throws ServletException, IOException {
        req.setAttribute("alert", message);
        req.setAttribute("formUsername", username);
        req.setAttribute("formFullname", fullname);
        req.setAttribute("formEmail", email);
        req.setAttribute("formPhone", phone);
        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }
}
