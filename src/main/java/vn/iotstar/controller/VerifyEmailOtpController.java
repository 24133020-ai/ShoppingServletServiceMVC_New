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
@WebServlet(urlPatterns = { "/verify-email-otp" })
public class VerifyEmailOtpController extends HttpServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String pendingEmail = (String) session.getAttribute(Constant.SESSION_NEW_EMAIL);
        if (pendingEmail == null || pendingEmail.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        req.setAttribute("newEmail", pendingEmail);
        req.getRequestDispatcher(Constant.Path.VERIFY_EMAIL_OTP).forward(req, resp);
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

        String pendingEmail = (String) session.getAttribute(Constant.SESSION_NEW_EMAIL);
        if (pendingEmail == null || pendingEmail.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/profile");
            return;
        }

        User sessionUser = (User) session.getAttribute("account");
        User freshUser = userService.get(sessionUser.getUserName());
        if (freshUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        if ("resend".equals(action)) {
            OtpResult r = userService.sendUpdateEmailOtp(freshUser, pendingEmail);
            if (r == OtpResult.OK) {
                req.setAttribute("success", "Đã gửi lại mã OTP tới: " + pendingEmail);
            } else if (r == OtpResult.TOO_SOON) {
                req.setAttribute("alert", "Yêu cầu gửi lại quá nhanh. Vui lòng chờ 60 giây.");
            } else {
                req.setAttribute("alert", "Lỗi khi gửi email OTP. Vui lòng thử lại sau.");
            }
            req.setAttribute("newEmail", pendingEmail);
            req.getRequestDispatcher(Constant.Path.VERIFY_EMAIL_OTP).forward(req, resp);
            return;
        }

        String otp = FormValidationUtil.safeTrim(req.getParameter("otp"));
        if (otp.length() != Constant.OTP_LENGTH || !otp.matches("^[0-9]+$")) {
            req.setAttribute("alert", "Mã OTP phải gồm 6 chữ số.");
            req.setAttribute("newEmail", pendingEmail);
            req.getRequestDispatcher(Constant.Path.VERIFY_EMAIL_OTP).forward(req, resp);
            return;
        }

        OtpResult result = userService.verifyAndUpdateEmail(freshUser, pendingEmail, otp);
        if (result == OtpResult.OK) {
            session.removeAttribute(Constant.SESSION_NEW_EMAIL);
            session.setAttribute("account", freshUser);
            resp.sendRedirect(req.getContextPath() + "/profile?msg=email_updated");
        } else if (result == OtpResult.EXPIRED) {
            req.setAttribute("alert", "Mã OTP đã hết hạn. Hãy bấm 'Gửi lại mã OTP'.");
            req.setAttribute("newEmail", pendingEmail);
            req.getRequestDispatcher(Constant.Path.VERIFY_EMAIL_OTP).forward(req, resp);
        } else if (result == OtpResult.TOO_MANY_ATTEMPTS) {
            req.setAttribute("alert", "Bạn đã nhập sai quá số lần cho phép. Hãy bấm 'Gửi lại mã OTP' mới.");
            req.setAttribute("newEmail", pendingEmail);
            req.getRequestDispatcher(Constant.Path.VERIFY_EMAIL_OTP).forward(req, resp);
        } else {
            req.setAttribute("alert", "Mã OTP không chính xác. Vui lòng kiểm tra lại hộp thư.");
            req.setAttribute("newEmail", pendingEmail);
            req.getRequestDispatcher(Constant.Path.VERIFY_EMAIL_OTP).forward(req, resp);
        }
    }
}
