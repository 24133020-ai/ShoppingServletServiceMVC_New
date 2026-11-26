package vn.iotstar.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.service.UserService;
import vn.iotstar.service.UserService.OtpResult;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.FormValidationUtil;

/** Bước 1 quên mật khẩu: nhập email để nhận OTP. */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/forgot-password")
public class ForgotPasswordController extends HttpServlet {
    private UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String email = req.getParameter("email");
        email = FormValidationUtil.safeTrim(email).toLowerCase();
        if (!FormValidationUtil.isEmail(email)) {
            req.setAttribute("alert", "Vui lòng nhập địa chỉ email hợp lệ.");
            req.setAttribute("formEmail", email);
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
            return;
        }

        OtpResult r = service.sendOtp(email, Constant.PURPOSE_RESET);
        if (r == OtpResult.MAIL_ERROR) {
            req.setAttribute("formEmail", email);
            req.setAttribute("alert", "Không gửi được email. Vui lòng thử lại sau.");
            req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
            return;
        }

        // Luôn trả lời giống nhau dù email có tồn tại hay không (tránh dò email)
        req.getSession(true).setAttribute(Constant.SESSION_RESET_EMAIL, email);
        resp.sendRedirect(req.getContextPath() + "/reset-password?msg=" + (r == OtpResult.TOO_SOON ? "toosoon" : "sent"));
    }
}
