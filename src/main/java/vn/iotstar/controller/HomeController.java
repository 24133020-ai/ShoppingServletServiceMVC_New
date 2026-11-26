
package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.Constant;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/home")
public class HomeController extends HttpServlet {
    public IVideoService videoService = new VideoServiceImpl();
    public ICategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        // Kiểm tra người dùng đã đăng nhập chưa
        if (session == null ||
            session.getAttribute("account") == null) {

            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Cả Admin và User đều xem được Danh mục sản phẩm và Video mới nhất trên Trang chủ
        req.setAttribute("categories", cateService.findAll());
        req.setAttribute("newestVideos", videoService.findNewest(Constant.HOME_VIDEO_COUNT));

        req.getRequestDispatcher("/views/home.jsp")
           .forward(req, resp);
    }
}
