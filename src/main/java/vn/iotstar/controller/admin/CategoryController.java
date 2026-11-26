package vn.iotstar.controller.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.FormValidationUtil;

@SuppressWarnings("serial")
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
@WebServlet(urlPatterns = { "/admin/categories", "/admin/category/add", "/admin/category/insert",
        "/admin/category/edit", "/admin/category/update", "/admin/category/delete", "/admin/category/list", "/categories" })
public class CategoryController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ICategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/admin/categories".equals(path) || "/admin/category/list".equals(path) || "/categories".equals(path)) {
            List<Category> list = cateService.findAll();
            req.setAttribute("listcate", list);
            req.setAttribute("cateList", list);
            if ("toolarge".equals(req.getParameter("msg"))) {
                req.setAttribute("alert", "Ảnh tải lên vượt quá giới hạn 5MB.");
            } else if ("forbidden".equals(req.getParameter("msg"))) {
                req.setAttribute("alert", "Bạn không có quyền quản trị để thực hiện thao tác này.");
            }
            req.getRequestDispatcher("/views/admin/category-list.jsp").forward(req, resp);
            return;
        }

        // Kiểm tra phân quyền cho các thao tác Thêm, Sửa, Xóa
        jakarta.servlet.http.HttpSession session = req.getSession(false);
        vn.iotstar.entity.User account = (session != null) ? (vn.iotstar.entity.User) session.getAttribute("account") : null;
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (account.getRoleid() != 1 && account.getRoleid() != 2) {
            resp.sendRedirect(req.getContextPath() + "/categories?msg=forbidden");
            return;
        }

        if ("/admin/category/add".equals(path)) {
            req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
        } else if ("/admin/category/edit".equals(path)) {
            Integer id = parsePositiveInt(req.getParameter("id"));
            Category category = id == null ? null : cateService.findById(id);
            if (category == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }
            req.setAttribute("cate", category);
            req.setAttribute("category", category);
            req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
        } else if ("/admin/category/delete".equals(path)) {
            Integer id = parsePositiveInt(req.getParameter("id"));
            if (id != null) {
                try { cateService.delete(id); } catch (Exception e) { log("Cannot delete category " + id, e); }
            }
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String path = req.getServletPath();

        try {
            if ("/admin/category/insert".equals(path)) {
                insertCategory(req, resp);
            } else if ("/admin/category/update".equals(path)) {
                updateCategory(req, resp);
            }
        } catch (IllegalStateException e) {
            // Multipart vượt quá giới hạn @MultipartConfig.
            if ("/admin/category/update".equals(path)) {
                resp.sendRedirect(req.getContextPath() + "/admin/categories?msg=toolarge");
            } else {
                req.setAttribute("alert", "Dữ liệu tải lên quá lớn. Ảnh phải nhỏ hơn hoặc bằng 5MB.");
                req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
            }
        }
    }

    private void insertCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String name = FormValidationUtil.safeTrim(req.getParameter("categoryname"));
        String imageRef = FormValidationUtil.safeTrim(req.getParameter("images"));
        String statusValue = req.getParameter("status");
        int status = "0".equals(statusValue) ? 0 : 1;
        Part imagePart = getImagePart(req);

        String error = validateCategory(name, imageRef, statusValue, imagePart);
        if (error == null && cateService.findByCategoryname(name) != null) {
            error = "Tên danh mục này đã tồn tại.";
        }
        if (error != null) {
            req.setAttribute("alert", error);
            req.setAttribute("formCategoryName", name);
            req.setAttribute("formImage", imageRef);
            req.setAttribute("formStatus", statusValue);
            req.getRequestDispatcher("/views/admin/category-add.jsp").forward(req, resp);
            return;
        }

        Category category = new Category();
        category.setCategoryname(name);
        category.setStatus(status);
        category.setImages(saveImage(imagePart, imageRef, "avatar.png"));
        cateService.insert(category);
        resp.sendRedirect(req.getContextPath() + "/admin/categories");
    }

    private void updateCategory(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Integer id = parsePositiveInt(req.getParameter("categoryid"));
        if (id == null) id = parsePositiveInt(req.getParameter("id"));
        Category category = id == null ? null : cateService.findById(id);
        if (category == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        String name = FormValidationUtil.safeTrim(req.getParameter("categoryname"));
        String imageRef = FormValidationUtil.safeTrim(req.getParameter("images"));
        String statusValue = req.getParameter("status");
        Part imagePart = getImagePart(req);

        String error = validateCategory(name, imageRef, statusValue, imagePart);
        if (error == null) {
            Category duplicate = cateService.findByCategoryname(name);
            if (duplicate != null && duplicate.getCategoryId() != category.getCategoryId()) {
                error = "Tên danh mục này đã được sử dụng bởi danh mục khác.";
            }
        }
        if (error != null) {
            category.setCategoryname(name);
            if (!imageRef.isEmpty()) category.setImages(imageRef);
            if ("0".equals(statusValue)) category.setStatus(0);
            else if ("1".equals(statusValue)) category.setStatus(1);
            showEditError(req, resp, category, error);
            return;
        }

        String oldImage = category.getImages();
        String newImage = saveImage(imagePart, imageRef, oldImage);
        category.setCategoryname(name);
        category.setStatus("0".equals(statusValue) ? 0 : 1);
        category.setImages(newImage);
        cateService.update(category);

        // Chỉ xóa ảnh cũ sau khi đã cập nhật database thành công.
        if (imagePart != null && imagePart.getSize() > 0 && oldImage != null
                && !oldImage.startsWith("http://") && !oldImage.startsWith("https://")) {
            deleteFile(Paths.get(Constant.DIR, "category", oldImage).toString());
        }
        resp.sendRedirect(req.getContextPath() + "/admin/categories");
    }

    private String validateCategory(String name, String imageRef, String statusValue, Part imagePart) {
        if (name.length() < 2 || name.length() > 255) {
            return "Tên danh mục bắt buộc có từ 2 đến 255 ký tự.";
        }
        if (!FormValidationUtil.isValidStatus(statusValue)) {
            return "Trạng thái danh mục không hợp lệ.";
        }
        if ((imagePart == null || imagePart.getSize() == 0) && !FormValidationUtil.isImageReference(imageRef)) {
            return "Đường dẫn ảnh phải là URL HTTP(S) hợp lệ hoặc tên file ảnh an toàn.";
        }
        if (!FormValidationUtil.isValidImageUpload(imagePart)) {
            return "File tải lên phải là ảnh PNG, JPG, JPEG, GIF hoặc WEBP.";
        }
        return null;
    }

    private Part getImagePart(HttpServletRequest req) throws IOException, ServletException {
        Part part = req.getPart("images1");
        return part != null ? part : req.getPart("icon");
    }

    private String saveImage(Part part, String imageRef, String fallback) throws IOException {
        if (part != null && part.getSize() > 0) {
            String submitted = part.getSubmittedFileName().replace('\\', '/');
            String original = submitted.substring(submitted.lastIndexOf('/') + 1);
            String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
            String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            Path uploadDir = Paths.get(Constant.DIR, "category");
            Files.createDirectories(uploadDir);
            part.write(uploadDir.resolve(filename).toString());
            return filename;
        }
        return FormValidationUtil.hasText(imageRef) ? imageRef.trim() : fallback;
    }

    private void showEditError(HttpServletRequest req, HttpServletResponse resp, Category category, String message)
            throws ServletException, IOException {
        req.setAttribute("alert", message);
        req.setAttribute("cate", category);
        req.setAttribute("category", category);
        req.getRequestDispatcher("/views/admin/category-edit.jsp").forward(req, resp);
    }

    private Integer parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void deleteFile(String filePath) {
        try { Files.deleteIfExists(Paths.get(filePath)); } catch (Exception ignored) { }
    }
}
