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

/** Nhập OTP để kích hoạt tài khoản sau khi đăng ký. */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/verify-otp")
public class VerifyOtpController extends HttpServlet {
    private UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = pendingEmail(req);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        req.setAttribute("email", email);

        String msg = req.getParameter("msg");
        if ("sent".equals(msg)) {
            req.setAttribute("info", "Mã OTP đã được gửi tới email của bạn. Mã có hiệu lực "
                    + Constant.OTP_EXPIRE_MINUTES + " phút.");
        } else if ("resent".equals(msg)) {
            req.setAttribute("info", "Đã gửi lại mã OTP mới.");
        } else if ("inactive".equals(msg)) {
            req.setAttribute("info", "Tài khoản chưa được kích hoạt. Mã OTP mới đã được gửi tới email của bạn.");
        } else if ("toosoon".equals(msg)) {
            req.setAttribute("alert", "Vui lòng chờ " + Constant.OTP_RESEND_SECONDS
                    + " giây giữa các lần gửi mã. Hãy dùng mã đã gửi trước đó.");
        } else if ("mailerror".equals(msg)) {
            req.setAttribute("alert", "Không gửi được email. Hãy bấm \"Gửi lại mã\" sau ít phút.");
        }
        req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = pendingEmail(req);
        if (email == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Gửi lại mã
        if ("resend".equals(req.getParameter("action"))) {
            OtpResult r = service.sendOtp(email, Constant.PURPOSE_ACTIVATE);
            String code = r == OtpResult.OK ? "resent" : (r == OtpResult.TOO_SOON ? "toosoon" : "mailerror");
            resp.sendRedirect(req.getContextPath() + "/verify-otp?msg=" + code);
            return;
        }

        String otp = req.getParameter("otp");
        otp = otp == null ? "" : otp.trim();
        if (!otp.matches("\\d{" + Constant.OTP_LENGTH + "}")) {
            req.setAttribute("email", email);
            req.setAttribute("alert", "Mã OTP gồm " + Constant.OTP_LENGTH + " chữ số.");
            req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
            return;
        }

        OtpResult r = service.activateAccount(email, otp);
        if (r == OtpResult.OK) {
            req.getSession().removeAttribute(Constant.SESSION_OTP_EMAIL);
            resp.sendRedirect(req.getContextPath() + "/login?msg=activated");
            return;
        }

        String alert;
        switch (r) {
            case EXPIRED:
                alert = "Mã OTP đã hết hạn. Hãy bấm \"Gửi lại mã\".";
                break;
            case TOO_MANY_ATTEMPTS:
                alert = "Bạn đã nhập sai quá nhiều lần. Hãy bấm \"Gửi lại mã\" để nhận mã mới.";
                break;
            default:
                alert = "Mã OTP không đúng.";
        }
        req.setAttribute("email", email);
        req.setAttribute("alert", alert);
        req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
    }

    private String pendingEmail(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (String) session.getAttribute(Constant.SESSION_OTP_EMAIL);
    }
}
