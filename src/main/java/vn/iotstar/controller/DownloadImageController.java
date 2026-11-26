package vn.iotstar.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.util.Constant;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/image") // Gọi dạng: /image?fname=abc.png
public class DownloadImageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fileName = req.getParameter("fname");
        if (fileName == null || fileName.isEmpty() || fileName.contains("..")
                || fileName.contains("/") || fileName.contains("\\")) {
            return;
        }

        File file = new File(Constant.DIR + File.separator + "category" + File.separator + fileName);
        if (!file.exists()) {
            file = new File(Constant.DIR + File.separator + "video" + File.separator + fileName);
        }
        if (!file.exists()) {
            file = new File(Constant.DIR + File.separator + fileName);
        }

        resp.setContentType("image/jpeg");
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                IOUtils.copy(fis, resp.getOutputStream());
            }
        }
    }
}
