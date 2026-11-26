<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thông tin cá nhân (Profile) - Shopping MVC</title>
    <style>
        .profile-card { background: #fff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); padding: 30px; margin-bottom: 30px; }
        .avatar-container { text-align: center; margin-bottom: 25px; }
        .avatar-img { width: 140px; height: 140px; border-radius: 50%; object-fit: cover; border: 4px solid #e9ecef; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
        .avatar-placeholder { width: 140px; height: 140px; border-radius: 50%; background: #e9ecef; color: #868e96; line-height: 140px; font-size: 60px; margin: auto; }
        .section-title { font-weight: bold; color: #17324d; margin-top: 0; margin-bottom: 20px; border-bottom: 2px solid #e9ecef; padding-bottom: 10px; }
        .help-block { font-size: 12px; margin-top: 5px; }
    </style>
</head>
<body>

<jsp:include page="/views/common/navbar.jsp" />

<div class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="profile-card">
                <h3 class="section-title"><i class="fa fa-user-circle"></i> Hồ sơ người dùng</h3>

                <c:if test="${not empty success}">
                    <div class="alert alert-success alert-dismissible" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <i class="fa fa-check-circle"></i> <c:out value="${success}"/>
                    </div>
                </c:if>

                <c:if test="${not empty alert}">
                    <div class="alert alert-danger alert-dismissible" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <i class="fa fa-exclamation-triangle"></i> <c:out value="${alert}"/>
                    </div>
                </c:if>

                <form action="<c:url value='/profile/update'/>" method="post" enctype="multipart/form-data">
                    <div class="avatar-container">
                        <c:choose>
                            <c:when test="${not empty user.avatar && (user.avatar.startsWith('http://') || user.avatar.startsWith('https://'))}">
                                <img src="${user.avatar}" alt="Avatar" class="avatar-img" />
                            </c:when>
                            <c:when test="${not empty user.avatar}">
                                <img src="<c:url value='/image?fname=${user.avatar}'/>" alt="Avatar" class="avatar-img" />
                            </c:when>
                            <c:otherwise>
                                <div class="avatar-placeholder"><i class="fa fa-user"></i></div>
                            </c:otherwise>
                        </c:choose>
                        <h4 style="margin-top: 15px; font-weight: bold;"><c:out value="${user.fullName}"/></h4>
                        <p class="text-muted">
                            <c:choose>
                                <c:when test="${user.roleid == 1}"><span class="label label-danger">Quản trị viên (Admin)</span></c:when>
                                <c:when test="${user.roleid == 2}"><span class="label label-warning">Quản lý (Manager)</span></c:when>
                                <c:otherwise><span class="label label-info">Người dùng (User)</span></c:otherwise>
                            </c:choose>
                            · Tài khoản: <strong><c:out value="${user.userName}"/></strong>
                        </p>
                    </div>

                    <div class="row">
                        <div class="col-md-6 form-group">
                            <label>Tài khoản (Username):</label>
                            <input type="text" class="form-control" value="<c:out value='${user.userName}'/>" disabled />
                        </div>
                        <div class="col-md-6 form-group">
                            <label for="fullname">Họ và tên <span class="text-danger">*</span>:</label>
                            <input type="text" class="form-control" id="fullname" name="fullname"
                                   value="<c:out value='${user.fullName}'/>" minlength="2" maxlength="100" required />
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 form-group">
                            <label for="phone">Số điện thoại:</label>
                            <input type="tel" class="form-control" id="phone" name="phone"
                                   placeholder="0901234567" value="<c:out value='${user.phone}'/>"
                                   pattern="[+0-9 ().-]{9,18}" maxlength="18" />
                        </div>
                        <div class="col-md-6 form-group">
                            <label for="email">Địa chỉ Email <span class="text-danger">*</span>:</label>
                            <input type="email" class="form-control" id="email" name="email"
                                   value="<c:out value='${user.email}'/>" maxlength="254" required />
                            <small class="help-block text-primary">
                                <i class="fa fa-info-circle"></i> Nếu đổi email mới, hệ thống sẽ gửi <strong>mã OTP</strong> đến email mới để xác nhận.
                            </small>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="avatar">Cập nhật ảnh đại diện (Cloudinary Multipart):</label>
                        <input type="file" class="form-control" id="avatar" name="avatar"
                               accept="image/png,image/jpeg,image/gif,image/webp" />
                        <small class="help-block text-muted">
                            <i class="fa fa-cloud-upload"></i> Định dạng hỗ trợ: PNG, JPG, JPEG, GIF, WEBP (tối đa 5MB). Ảnh được tải lên lưu trữ đám mây <strong>Cloudinary</strong>.
                        </small>
                    </div>

                    <hr/>

                    <div class="form-group text-right" style="margin-bottom: 0;">
                        <a href="<c:url value='/home'/>" class="btn btn-default" style="margin-right: 10px;">Quay lại Trang chủ</a>
                        <button type="submit" class="btn btn-primary" style="padding-left: 25px; padding-right: 25px;">
                            <i class="fa fa-save"></i> Cập nhật hồ sơ
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
