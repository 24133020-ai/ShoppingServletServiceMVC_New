package vn.iotstar.controller.admin;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/admin/users", "/admin/user/toggle-status", "/admin/user/change-role" })
public class AdminUserController extends HttpServlet {
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("account") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User currentAdmin = (User) session.getAttribute("account");
        if (currentAdmin.getRoleid() != 1) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        String path = req.getServletPath();
        if ("/admin/user/toggle-status".equals(path)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                if (id != currentAdmin.getId()) {
                    userService.toggleUserStatus(id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        } else if ("/admin/user/change-role".equals(path)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                int newRole = Integer.parseInt(req.getParameter("role"));
                if (id != currentAdmin.getId() && (newRole == 1 || newRole == 3)) {
                    userService.changeUserRole(id, newRole);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        // Mặc định hiển thị danh sách người dùng
        List<User> userList = userService.findAll();
        req.setAttribute("userList", userList);
        req.getRequestDispatcher(Constant.Path.ADMIN_USER_LIST).forward(req, resp);
    }
}
