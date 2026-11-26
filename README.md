# Shopping Servlet Service MVC

## 1. Giới thiệu
Website quản lý cửa hàng được xây dựng
bằng Java Servlet, JSP và mô hình MVC.

## 2. Chức năng
- **Quản lý Hồ sơ cá nhân (Profile)**: xem và cập nhật `fullname`, `phone`, `email` mới (hệ thống tự động gửi **mã OTP đến email mới** để xác thực trước khi cập nhật thành công), upload ảnh đại diện (avatar/images) lên **Cloudinary bằng Multipart**
- **Đồng bộ giao diện Admin và User**: Giao diện thống nhất qua **SiteMesh Decorator 3 (Bootstrap 3.4.1)** với Navigation Bar dùng chung; cả Admin và User đều xem được Danh mục sản phẩm (Categories) và Video trên Trang chủ cũng như các trang quản lý; Admin có toàn quyền CRUD danh mục/video, User quản lý video của chính mình
- **Quản lý Người dùng dành cho Admin (`/admin/users`)**: Xem danh sách toàn bộ tài khoản, khóa / mở khóa tài khoản, phân quyền vai trò
- Đăng ký tài khoản, **kích hoạt tài khoản bằng mã OTP gửi qua email**
- Đăng nhập với **mật khẩu được mã hóa (PBKDF2-HMAC-SHA256 + salt)** và đăng xuất
- **Quên mật khẩu**: nhận mã OTP qua email để đặt lại mật khẩu
- Quản lý video: thêm, sửa, xóa video, upload poster bằng Multipart
- Trang chủ `/home` hiển thị Danh mục sản phẩm và các video mới nhất cho cả Admin và User
- Trang `/video` hiển thị tất cả video cộng đồng và video của người dùng, phân trang
- Quản lý danh mục sản phẩm (JPA 3.0 & Hibernate)
- Validation 2 lớp (HTML5/JS phía client và FormValidationUtil phía Servlet) cho các chức năng có FORM

## 3. Luồng OTP
| Chức năng | URL | Mô tả |
|---|---|---|
| Đăng ký | `/register` | Tạo tài khoản ở trạng thái **chưa kích hoạt**, gửi OTP 6 số tới email |
| Kích hoạt | `/verify-otp` | Nhập OTP để kích hoạt; có nút "Gửi lại mã" |
| Đăng nhập | `/login` | Chỉ tài khoản đã kích hoạt mới vào được. Nếu chưa kích hoạt sẽ tự gửi lại OTP |
| Đổi Email Profile | `/verify-email-otp` | Khi đổi email trong Profile, OTP gửi đến **email mới** để xác thực |
| Quên mật khẩu | `/forgot-password` | Nhập email để nhận OTP |
| Đặt lại mật khẩu | `/reset-password` | Nhập OTP + mật khẩu mới |
| Đăng xuất | `/logout` | Hủy session, xóa cookie "Nhớ tôi" |

Quy tắc OTP: 6 chữ số, hiệu lực 5 phút, sai tối đa 5 lần, gửi lại cách nhau tối thiểu 60 giây
(chỉnh trong `vn.iotstar.util.Constant`).

## 4. Cấu hình và chạy dự án
1. Cài đặt JDK 22, Maven và Tomcat 11 (ứng dụng đang dùng Jakarta Servlet 6.1).
2. Mở dự án bằng IntelliJ IDEA hoặc Eclipse.
3. Chạy `database.sql` trong SQL Server Management Studio (script tạo/cập nhật các bảng `User`, `categories`, `videos`).
4. Thiết lập biến môi trường cho cửa sổ PowerShell sẽ chạy Tomcat:

   ```powershell
   $env:DB_USER = "tai_khoan_sql_server"
   $env:DB_PASSWORD = "mat_khau_sql_server"
   # Nếu SQL Server dùng instance/port khác, có thể cấu hình thêm:
   # $env:DB_URL = "jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;databaseName=ShoppingServiceMVC"
   ```

5. Cấu hình gửi email (xem mục 5). Nếu chưa cấu hình MAIL_USERNAME/MAIL_PASSWORD, OTP được in trong console Tomcat để kiểm thử ở chế độ DEV.
6. Chạy lệnh:

   mvn clean package

7. Deploy file WAR trong thư mục `target` lên Apache Tomcat 11.

## 5. Cấu hình gửi email
Khuyến nghị dùng biến môi trường `MAIL_USERNAME` và `MAIL_PASSWORD` (Google App Password):

```powershell
$env:MAIL_USERNAME = "gmail_gui_otp@gmail.com"
$env:MAIL_PASSWORD = "google_app_password"
```

Có thể cấu hình thêm `MAIL_HOST` và `MAIL_PORT`; mặc định là `smtp.gmail.com:587`.
Không commit App Password hoặc file `mail.properties` lên GitHub. File cấu hình mẫu là `mail.properties.example`.

Nếu chưa cấu hình, ứng dụng chạy chế độ DEV: mã OTP được in ra console của Tomcat
(dòng bắt đầu bằng `[MAIL][DEV]`) thay vì gửi email thật.

## 6. SiteMesh và validation
- Maven dependency: `org.sitemesh:sitemesh:3.3.0`.
- SiteMesh 3 được đăng ký trong `WEB-INF/web.xml` bằng `ConfigurableSiteMeshFilter`.
- Cấu hình mapping nằm tại `WEB-INF/sitemesh3.xml`.
- Bootstrap decorator dùng chung nằm tại `WEB-INF/decorators/bootstrap.html`.
- Các form kiểm tra dữ liệu ở cả HTML5 và Servlet trước khi gọi Service/DAO.
- Các form CRUD danh mục và video kiểm tra độ dài, định dạng mã/tên và loại file ảnh; giới hạn upload ảnh là 5MB.

## 7. Truy cập website
http://localhost:8080/ShoppingServletServiceMVC/login

## 8. Tài khoản thử nghiệm
Tài khoản để  test danh mục sản phẩm: admin

Mật khẩu: 123456



Tài khoản này cũng là admin nhưng dùng tên khác: viethuan

Mật khẩu: 123456



Tài khoản user để kiểm tra về phần video: hi

Mật khẩu: 123456

(Mật khẩu trong database đã được băm, các tài khoản mới đăng ký phải kích hoạt bằng OTP.)
