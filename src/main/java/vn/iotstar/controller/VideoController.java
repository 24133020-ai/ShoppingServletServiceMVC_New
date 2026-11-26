package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.FormValidationUtil;

/**
 * CRUD Video của user đang đăng nhập, danh sách phân trang 6 video / trang tại /video,
 * xem chi tiết và play video tại /video/detail. Upload poster bằng @MultipartConfig.
 */
@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
                 maxFileSize = 1024 * 1024 * 5,
                 maxRequestSize = 1024 * 1024 * 5 * 5)
@WebServlet(urlPatterns = { "/video", "/video/add", "/video/insert", "/video/edit", "/video/update",
        "/video/delete", "/video/detail" })
public class VideoController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public IVideoService videoService = new VideoServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = currentUser(req);
        String path = req.getServletPath();

        if (path.equals("/video")) {
            String filter = req.getParameter("filter");
            boolean onlyMy = "my".equalsIgnoreCase(filter);
            int total = onlyMy ? videoService.countByUser(user.getId()) : videoService.countAll();
            int totalPages = Math.max(1, (int) Math.ceil((double) total / Constant.VIDEO_PAGE_SIZE));
            int page = parsePage(req.getParameter("page"), totalPages);
            List<Video> list = onlyMy 
                    ? videoService.findByUser(user.getId(), page - 1, Constant.VIDEO_PAGE_SIZE)
                    : videoService.findAll(page - 1, Constant.VIDEO_PAGE_SIZE);

            req.setAttribute("listvideo", list);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("filter", onlyMy ? "my" : "all");
            if ("toolarge".equals(req.getParameter("msg"))) {
                req.setAttribute("alert", "File poster quá lớn (tối đa 5MB).");
            }
            req.getRequestDispatcher(Constant.Path.VIDEO_LIST).forward(req, resp);

        } else if (path.equals("/video/add")) {
            req.getRequestDispatcher(Constant.Path.VIDEO_ADD).forward(req, resp);

        } else if (path.equals("/video/edit")) {
            Video video = findOwnVideo(req, user);
            if (video == null) {
                resp.sendRedirect(req.getContextPath() + "/video");
                return;
            }
            req.setAttribute("video", video);
            req.getRequestDispatcher(Constant.Path.VIDEO_EDIT).forward(req, resp);

        } else if (path.equals("/video/delete")) {
            Video video = findOwnVideo(req, user);
            if (video != null) {
                videoService.delete(video.getVideoId());
            }
            resp.sendRedirect(req.getContextPath() + "/video");

        } else if (path.equals("/video/detail")) {
            // Xem chi tiết và play video (mọi user đăng nhập đều xem được)
            String id = req.getParameter("id");
            Video video = (id == null) ? null : videoService.findById(id);
            if (video == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            req.setAttribute("video", video);
            req.getRequestDispatcher(Constant.Path.VIDEO_DETAIL).forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // Đọc form multipart; vượt quá giới hạn của @MultipartConfig sẽ ném IllegalStateException
        try {
            req.getParts();
        } catch (IllegalStateException e) {
            resp.sendRedirect(req.getContextPath() + "/video?msg=toolarge");
            return;
        }

        User user = currentUser(req);
        String path = req.getServletPath();

        // 1. Thêm mới (Insert)
        if (path.equals("/video/insert")) {
            String videoId = trim(req.getParameter("videoId"));
            String title = trim(req.getParameter("title"));

            Video video = new Video();
            video.setVideoId(videoId);
            video.setTitle(title);
            video.setDescription(req.getParameter("description"));
            video.setActive("1".equals(req.getParameter("active")));
            video.setUser(user);

            String error = null;
            String description = video.getDescription();
            if (videoId.isEmpty() || title.isEmpty()) {
                error = "Vui lòng nhập Video ID và tiêu đề video.";
            } else if (!FormValidationUtil.isVideoId(videoId)) {
                error = "Video ID chỉ gồm chữ không dấu, số, dấu - và _ (tối đa 50 ký tự).";
            } else if (title.length() > 255) {
                error = "Tiêu đề video không được vượt quá 255 ký tự.";
            } else if (description != null && description.length() > 5000) {
                error = "Mô tả video không được vượt quá 5.000 ký tự.";
            } else if (!FormValidationUtil.isValidStatus(req.getParameter("active"))) {
                error = "Vui lòng chọn trạng thái video hợp lệ.";
            } else if (videoService.findById(videoId) != null) {
                error = "Video ID đã tồn tại!";
            }
            if (error == null) {
                try {
                    video.setPoster(savePoster(req, null));
                } catch (IllegalArgumentException e) {
                    error = e.getMessage();
                }
            }
            if (error != null) {
                req.setAttribute("alert", error);
                req.setAttribute("video", video);
                req.getRequestDispatcher(Constant.Path.VIDEO_ADD).forward(req, resp);
                return;
            }

            videoService.insert(video);
            resp.sendRedirect(req.getContextPath() + "/video");
            return;
        }

        // 2. Cập nhật (Update)
        if (path.equals("/video/update")) {
            Video video = findOwnVideo(req, user, req.getParameter("videoId"));
            if (video == null) {
                resp.sendRedirect(req.getContextPath() + "/video");
                return;
            }
            String title = trim(req.getParameter("title"));
            String oldPoster = video.getPoster();

            video.setTitle(title);
            video.setDescription(req.getParameter("description"));
            video.setActive("1".equals(req.getParameter("active")));

            String description = video.getDescription();
            String error = title.isEmpty() ? "Vui lòng nhập tiêu đề video."
                    : (title.length() > 255 ? "Tiêu đề video không được vượt quá 255 ký tự."
                    : (description != null && description.length() > 5000
                    ? "Mô tả video không được vượt quá 5.000 ký tự."
                    : (!FormValidationUtil.isValidStatus(req.getParameter("active"))
                    ? "Vui lòng chọn trạng thái video hợp lệ." : null)));
            if (error == null) {
                try {
                    video.setPoster(savePoster(req, oldPoster));
                } catch (IllegalArgumentException e) {
                    error = e.getMessage();
                }
            }
            if (error != null) {
                video.setPoster(oldPoster);
                req.setAttribute("alert", error);
                req.setAttribute("video", video);
                req.getRequestDispatcher(Constant.Path.VIDEO_EDIT).forward(req, resp);
                return;
            }

            videoService.update(video);
            resp.sendRedirect(req.getContextPath() + "/video");
        }
    }

    private String savePoster(HttpServletRequest req, String oldPoster) throws IOException, ServletException {
        Part part = req.getPart("posterFile");
        if (part == null || part.getSize() == 0 || part.getSubmittedFileName() == null
                || part.getSubmittedFileName().trim().isEmpty()) {
            return oldPoster;
        }
        if (!FormValidationUtil.isValidImageUpload(part)) {
            throw new IllegalArgumentException("Poster phải là ảnh PNG, JPG, JPEG, GIF hoặc WEBP.");
        }

        String uploadPath = Constant.DIR + File.separator + "video";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String filename = Paths.get(part.getSubmittedFileName().replace('\\', '/')).getFileName().toString();
        int index = filename.lastIndexOf(".");
        String ext = filename.substring(index + 1).toLowerCase(java.util.Locale.ROOT);
        String fname = java.util.UUID.randomUUID().toString().replace("-", "") + "." + ext;
        part.write(uploadPath + File.separator + fname);

        if (oldPoster != null && !oldPoster.isEmpty()) {
            deleteFile(uploadPath + File.separator + oldPoster);
        }
        return fname;
    }

    public static void deleteFile(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (Exception ignored) {
        }
    }

    private User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("account");
    }

    private Video findOwnVideo(HttpServletRequest req, User user) {
        return findOwnVideo(req, user, req.getParameter("id"));
    }

    /** Trả về video nếu thuộc quyền quản lý: Admin quản lý tất cả, User quản lý video của chính mình. */
    private Video findOwnVideo(HttpServletRequest req, User user, String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        Video video = videoService.findById(id.trim());
        if (video == null) {
            return null;
        }
        if (user.getRoleid() == 1 || user.getRoleid() == 2) {
            return video; // Admin/Manager có toàn quyền
        }
        if (video.getUser() == null || video.getUser().getId() != user.getId()) {
            return null;
        }
        return video;
    }

    private int parsePage(String value, int totalPages) {
        int page = 1;
        try {
            page = Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
        }
        return Math.min(Math.max(page, 1), totalPages);
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
