package vn.iotstar.service.impl;

import java.sql.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;
import vn.iotstar.util.Constant;
import vn.iotstar.util.EmailUtil;
import vn.iotstar.util.OtpUtil;
import vn.iotstar.util.PasswordUtil;

public class UserServiceImpl implements UserService {
    private UserDao userDao = new UserDaoImpl();

    @Override
    public User login(String username, String password) {
        User user = this.get(username);
        if (user == null || password == null) {
            return null;
        }
        String stored = user.getPassWord();

        if (PasswordUtil.isHashed(stored)) {
            return PasswordUtil.matches(password, stored) ? user : null;
        }

        // Tài khoản cũ còn lưu mật khẩu plain-text: cho đăng nhập 1 lần rồi tự động nâng cấp sang dạng hash.
        if (PasswordUtil.safeEquals(password, stored)) {
            user.setPassWord(PasswordUtil.hash(password));
            userDao.update(user);
            return user;
        }
        return null;
    }

    @Override
    public User get(String username) {
        return userDao.get(username);
    }

    @Override
    public User getByEmail(String email) {
        return userDao.getByEmail(email);
    }

    @Override
    public void insert(User user) {
        userDao.insert(user);
    }

    @Override
    public User register(String username, String password, String email, String fullname, String phone) {
        if (userDao.checkExistUsername(username)) {
            return null;
        }
        Date date = new Date(System.currentTimeMillis());
        User user = new User(email, username, fullname, PasswordUtil.hash(password), null, 3, phone, date);
        user.setActive(false); // phải kích hoạt bằng OTP mới đăng nhập được
        userDao.insert(user);
        return user;
    }

    @Override
    public OtpResult sendOtp(String email, String purpose) {
        User user = userDao.getByEmail(email);
        if (user == null) {
            return OtpResult.NOT_FOUND;
        }
        // Kích hoạt: chỉ tài khoản chưa kích hoạt. Quên mật khẩu: chỉ tài khoản đã kích hoạt.
        boolean activate = Constant.PURPOSE_ACTIVATE.equals(purpose);
        if (activate == user.isActive()) {
            return OtpResult.NOT_FOUND;
        }

        // Chống spam: chỉ cho gửi lại sau OTP_RESEND_SECONDS giây
        if (user.getOtpExpiry() != null && purpose.equals(user.getOtpPurpose())) {
            LocalDateTime sentAt = user.getOtpExpiry().minusMinutes(Constant.OTP_EXPIRE_MINUTES);
            if (Duration.between(sentAt, LocalDateTime.now()).getSeconds() < Constant.OTP_RESEND_SECONDS) {
                return OtpResult.TOO_SOON;
            }
        }

        String otp = OtpUtil.generate();
        user.setOtpCode(otp);
        user.setOtpPurpose(purpose);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(Constant.OTP_EXPIRE_MINUTES));
        user.setOtpAttempts(0);
        userDao.update(user);

        return EmailUtil.sendOtp(user.getEmail(), otp, purpose) ? OtpResult.OK : OtpResult.MAIL_ERROR;
    }

    /** Kiểm tra OTP. Nếu sai thì tăng số lần thử. Trả về OK nếu đúng (chưa xóa OTP). */
    private OtpResult verifyOtp(User user, String purpose, String otp) {
        if (user == null) {
            return OtpResult.NOT_FOUND;
        }
        if (user.getOtpCode() == null || !purpose.equals(user.getOtpPurpose()) || user.getOtpExpiry() == null) {
            return OtpResult.INVALID;
        }
        if (user.getOtpAttempts() >= Constant.OTP_MAX_ATTEMPTS) {
            return OtpResult.TOO_MANY_ATTEMPTS;
        }
        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            return OtpResult.EXPIRED;
        }
        if (!PasswordUtil.safeEquals(user.getOtpCode(), otp)) {
            user.setOtpAttempts(user.getOtpAttempts() + 1);
            userDao.update(user);
            return OtpResult.INVALID;
        }
        return OtpResult.OK;
    }

    private void clearOtp(User user) {
        user.setOtpCode(null);
        user.setOtpPurpose(null);
        user.setOtpExpiry(null);
        user.setOtpAttempts(0);
    }

    @Override
    public OtpResult activateAccount(String email, String otp) {
        User user = userDao.getByEmail(email);
        OtpResult result = verifyOtp(user, Constant.PURPOSE_ACTIVATE, otp);
        if (result == OtpResult.OK) {
            user.setActive(true);
            clearOtp(user);
            userDao.update(user);
        }
        return result;
    }

    @Override
    public OtpResult resetPassword(String email, String otp, String newPassword) {
        User user = userDao.getByEmail(email);
        OtpResult result = verifyOtp(user, Constant.PURPOSE_RESET, otp);
        if (result == OtpResult.OK) {
            user.setPassWord(PasswordUtil.hash(newPassword));
            clearOtp(user);
            userDao.update(user);
        }
        return result;
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    @Override
    public User findById(int id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public void toggleUserStatus(int userId) {
        User u = userDao.findById(userId);
        if (u != null) {
            u.setActive(!u.isActive());
            userDao.update(u);
        }
    }

    @Override
    public void changeUserRole(int userId, int newRole) {
        User u = userDao.findById(userId);
        if (u != null) {
            u.setRoleid(newRole);
            userDao.update(u);
        }
    }

    @Override
    public OtpResult sendUpdateEmailOtp(User user, String newEmail) {
        if (user == null || newEmail == null || newEmail.isBlank()) {
            return OtpResult.NOT_FOUND;
        }
        // Chống spam: gửi lại sau OTP_RESEND_SECONDS
        if (user.getOtpExpiry() != null && Constant.PURPOSE_UPDATE_EMAIL.equals(user.getOtpPurpose())) {
            LocalDateTime sentAt = user.getOtpExpiry().minusMinutes(Constant.OTP_EXPIRE_MINUTES);
            if (Duration.between(sentAt, LocalDateTime.now()).getSeconds() < Constant.OTP_RESEND_SECONDS) {
                return OtpResult.TOO_SOON;
            }
        }

        String otp = OtpUtil.generate();
        user.setOtpCode(otp);
        user.setOtpPurpose(Constant.PURPOSE_UPDATE_EMAIL);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(Constant.OTP_EXPIRE_MINUTES));
        user.setOtpAttempts(0);
        userDao.update(user);

        return EmailUtil.sendOtp(newEmail, otp, "Xác nhận cập nhật Email mới") ? OtpResult.OK : OtpResult.MAIL_ERROR;
    }

    @Override
    public OtpResult verifyAndUpdateEmail(User user, String newEmail, String otp) {
        if (user == null) {
            return OtpResult.NOT_FOUND;
        }
        OtpResult result = verifyOtp(user, Constant.PURPOSE_UPDATE_EMAIL, otp);
        if (result == OtpResult.OK) {
            user.setEmail(newEmail);
            clearOtp(user);
            userDao.update(user);
        }
        return result;
    }

    @Override
    public boolean checkExistEmail(String email) {
        return userDao.checkExistEmail(email);
    }

    @Override
    public boolean checkExistUsername(String username) {
        return userDao.checkExistUsername(username);
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return userDao.checkExistPhone(phone);
    }
}
