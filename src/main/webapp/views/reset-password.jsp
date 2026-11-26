<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt lại mật khẩu</title>
<style>
        body { background-color: #f4f7f6; padding-top: 60px; }
        .box { max-width: 440px; margin: auto; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
        .box-title { text-align: center; font-size: 22px; font-weight: 600; margin-bottom: 20px; color: #333; }
        .input-group-addon { background-color: #eee; min-width: 42px; }
        .btn-main { background-color: #0088cc; color: #fff; font-size: 16px; padding: 10px; font-weight: bold; }
        .btn-main:hover { background-color: #006699; color: #fff; }
        .otp-input { font-size: 24px; letter-spacing: 8px; text-align: center; }
    </style>
</head>
<body>
<div class="container">
    <div class="box">
        <h2 class="box-title">Đặt lại mật khẩu</h2>
        <p class="text-center text-muted">Nhập mã OTP đã gửi tới<br><strong><c:out value="${email}"/></strong> và mật khẩu mới.</p>

        <c:if test="${info != null}">
            <div class="alert alert-info">${info}</div>
        </c:if>
        <c:if test="${alert != null}">
            <div class="alert alert-danger"><c:out value="${alert}"/></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/reset-password" method="post">
            <div class="form-group">
                <input type="text" name="otp" class="form-control otp-input" maxlength="6"
                       inputmode="numeric" autocomplete="one-time-code" placeholder="------" required autofocus />
            </div>
            <div class="form-group">
                <div class="input-group">
                    <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                    <input type="password" name="password" placeholder="Mật khẩu mới" class="form-control" minlength="6" required />
                </div>
            </div>
            <div class="form-group">
                <div class="input-group">
                    <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                    <input type="password" name="confirm" placeholder="Nhập lại mật khẩu mới" class="form-control" minlength="6" required />
                </div>
            </div>
            <button type="submit" class="btn btn-main btn-block">Đổi mật khẩu</button>
        </form>

        <form action="${pageContext.request.contextPath}/reset-password" method="post" style="margin-top: 15px;">
            <input type="hidden" name="action" value="resend" />
            <button type="submit" class="btn btn-link btn-block">Chưa nhận được mã? Gửi lại mã</button>
        </form>

        <div class="text-center" style="margin-top: 10px;">
            <a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
        </div>
    </div>
</div>
</body>
</html>
