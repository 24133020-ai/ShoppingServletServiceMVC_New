package vn.iotstar.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;

/**
 * Chặn truy cập khi chưa đăng nhập (hoặc sai quyền) và tắt cache các trang đã bảo vệ,
 * để sau khi đăng xuất bấm nút Back không xem lại được trang cũ.
 */
@WebFilter(urlPatterns = { "/admin/*", "/views/admin/*", "/home", "/video", "/video/*" })
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("account");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();
        boolean adminArea = path.startsWith("/admin") || path.startsWith("/views/admin");
        if (adminArea && user.getRoleid() != 1 && user.getRoleid() != 2) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        chain.doFilter(request, response);
    }
}
