package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.service.UserService;
import vn.iotstar.service.UserService.OtpResult;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

/** Bước 2 quên mật khẩu: nhập OTP + mật khẩu mới. */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/reset-password")
public class ResetPasswordController extends HttpServlet {
    private UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = resetEmail(req);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }
        req.setAttribute("email", email);

        String msg = req.getParameter("msg");
        if ("sent".equals(msg)) {
            req.setAttribute("info", "Nếu email tồn tại trong hệ thống, mã OTP đã được gửi. Mã có hiệu lực "
                    + Constant.OTP_EXPIRE_MINUTES + " phút.");
        } else if ("resent".equals(msg)) {
            req.setAttribute("info", "Đã gửi lại mã OTP mới (nếu email hợp lệ).");
        } else if ("toosoon".equals(msg)) {
            req.setAttribute("info", "Mã OTP đã được gửi trước đó. Hãy kiểm tra email, hoặc chờ "
                    + Constant.OTP_RESEND_SECONDS + " giây để gửi lại.");
        } else if ("mailerror".equals(msg)) {
            req.setAttribute("alert", "Không gửi được email. Hãy thử lại sau ít phút.");
        }
        req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = resetEmail(req);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }
        req.setAttribute("email", email);

        if ("resend".equals(req.getParameter("action"))) {
            OtpResult r = service.sendOtp(email, Constant.PURPOSE_RESET);
            String code = r == OtpResult.MAIL_ERROR ? "mailerror" : (r == OtpResult.TOO_SOON ? "toosoon" : "resent");
            resp.sendRedirect(req.getContextPath() + "/reset-password?msg=" + code);
            return;
        }

        String otp = req.getParameter("otp");
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirm");
        otp = otp == null ? "" : otp.trim();

        if (!otp.matches("\\d{" + Constant.OTP_LENGTH + "}")) {
            fail(req, resp, "Mã OTP gồm " + Constant.OTP_LENGTH + " chữ số.");
            return;
        }
        if (password == null || password.length() < Constant.PASSWORD_MIN_LENGTH) {
            fail(req, resp, "Mật khẩu mới phải có ít nhất " + Constant.PASSWORD_MIN_LENGTH + " ký tự.");
            return;
        }
        if (!password.equals(confirm)) {
            fail(req, resp, "Mật khẩu nhập lại không khớp.");
            return;
        }

        OtpResult r = service.resetPassword(email, otp, password);
        if (r == OtpResult.OK) {
            req.getSession().removeAttribute(Constant.SESSION_RESET_EMAIL);
            resp.sendRedirect(req.getContextPath() + "/login?msg=reset");
            return;
        }

        switch (r) {
            case EXPIRED:
                fail(req, resp, "Mã OTP đã hết hạn. Hãy bấm \"Gửi lại mã\".");
                break;
            case TOO_MANY_ATTEMPTS:
                fail(req, resp, "Bạn đã nhập sai quá nhiều lần. Hãy bấm \"Gửi lại mã\" để nhận mã mới.");
                break;
            default: // INVALID hoặc NOT_FOUND: thông báo giống nhau
                fail(req, resp, "Mã OTP không đúng hoặc không hợp lệ.");
        }
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("alert", message);
        req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
    }

    private String resetEmail(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (String) session.getAttribute(Constant.SESSION_RESET_EMAIL);
    }
}
