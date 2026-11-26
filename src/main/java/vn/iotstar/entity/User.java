package vn.iotstar.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "[User]")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email", columnDefinition = "NVARCHAR(255) NOT NULL")
    private String email;

    @Column(name = "username", columnDefinition = "NVARCHAR(50) NOT NULL", unique = true)
    private String userName;

    @Column(name = "fullname", columnDefinition = "NVARCHAR(100) NOT NULL")
    private String fullName;

    @Column(name = "password", columnDefinition = "NVARCHAR(255) NOT NULL")
    private String passWord;

    @Column(name = "avatar", columnDefinition = "NVARCHAR(500) NULL")
    private String avatar;

    @Column(name = "roleid", nullable = false)
    private int roleid = 3;

    @Column(name = "phone", columnDefinition = "NVARCHAR(20) NULL")
    private String phone;

    @Column(name = "createdDate")
    private Date createdDate;

    // Tài khoản đã kích hoạt bằng OTP chưa (tài khoản cũ mặc định = 1)
    @Column(name = "active", columnDefinition = "BIT NOT NULL DEFAULT 1")
    private boolean active = true;

    // Thông tin OTP hiện hành (kích hoạt hoặc quên mật khẩu)
    @Column(name = "otpCode", columnDefinition = "NVARCHAR(10) NULL")
    private String otpCode;

    @Column(name = "otpPurpose", columnDefinition = "NVARCHAR(20) NULL")
    private String otpPurpose;

    @Column(name = "otpExpiry")
    private LocalDateTime otpExpiry;

    @Column(name = "otpAttempts", columnDefinition = "INT NOT NULL DEFAULT 0")
    private int otpAttempts;

    // bi-directional one-to-many association to Video (1 user có nhiều video)
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Video> videos = new ArrayList<>();

    public User() {
    }

    public User(String email, String userName, String fullName, String passWord, String avatar, int roleid, String phone, Date createdDate) {
        this.email = email;
        this.userName = userName;
        this.fullName = fullName;
        this.passWord = passWord;
        this.avatar = avatar;
        this.roleid = roleid;
        this.phone = phone;
        this.createdDate = createdDate;
    }

    public User(int id, String email, String userName, String fullName, String passWord, String avatar, int roleid, String phone, Date createdDate) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.fullName = fullName;
        this.passWord = passWord;
        this.avatar = avatar;
        this.roleid = roleid;
        this.phone = phone;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullname() {
        return fullName;
    }

    public void setFullname(String fullname) {
        this.fullName = fullname;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPassword() {
        return passWord;
    }

    public void setPassword(String password) {
        this.passWord = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getRoleid() {
        return roleid;
    }

    public void setRoleid(int roleid) {
        this.roleid = roleid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getOtpPurpose() {
        return otpPurpose;
    }

    public void setOtpPurpose(String otpPurpose) {
        this.otpPurpose = otpPurpose;
    }

    public LocalDateTime getOtpExpiry() {
        return otpExpiry;
    }

    public void setOtpExpiry(LocalDateTime otpExpiry) {
        this.otpExpiry = otpExpiry;
    }

    public int getOtpAttempts() {
        return otpAttempts;
    }

    public void setOtpAttempts(int otpAttempts) {
        this.otpAttempts = otpAttempts;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
