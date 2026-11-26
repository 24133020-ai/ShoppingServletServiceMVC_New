<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng ký tài khoản</title>
    <!-- Bootstrap 3 CDN -->
<style>
        body {
            background-color: #f4f7f6;
            padding-top: 40px;
        }
        .register-box {
            max-width: 440px;
            margin: auto;
            background: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        .register-title {
            text-align: center;
            font-size: 22px;
            font-weight: 600;
            margin-bottom: 25px;
            color: #333;
        }
        .input-group-addon {
            background-color: #eee;
            min-width: 42px;
        }
        .btn-register {
            background-color: #0088cc;
            color: white;
            font-size: 16px;
            padding: 10px;
            font-weight: bold;
        }
        .btn-register:hover {
            background-color: #006699;
            color: white;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="register-box">
        <h2 class="register-title">Tạo tài khoản mới</h2>

        <c:if test="${alert != null}">
            <div class="alert alert-danger"><c:out value="${alert}"/></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post">
            <div class="form-group">
                <div class="input-group">
                    <span class="input-group-addon"><i class="fa fa-user"></i></span>
                    <input type="text" placeholder="Tài khoản (4–30 ký tự)" name="username" class="form-control"
                       minlength="4" maxlength="30" pattern="[A-Za-z0-9._-]{4,30}"
                       title="4–30 ký tự: chữ không dấu, số, dấu chấm, gạch dưới hoặc gạch ngang."
                       autocomplete="username" value="<c:out value='${formUsername}'/>" required />
                </div>
            </div>

            <div class="form-group">
                <div class="input-group">
                    <span class="input-group-addon"><i class="fa fa-id-card"></i></span>
                    <input type="text" placeholder="Họ tên" name="fullname" class="form-control"
                       minlength="2" maxlength="100" autocomplete="name"
                       value="<c:out value='${formFullname}'/>" required />
                </div>
            </div>

            <div class="form-group">
                <div class="input-group">
                    <span class="input-group-addon"><i class="fa fa-envelope"></i></span>
                    <input type="email" placeholder="Nhập Email" name="email" class="form-control"
                       maxlength="254" autocomplete="email" value="<c:out value='${formEmail}'/>" required />
                </div>
            </div>

            <div class="form-group">
                <div class="input-group">
                    <span class="input-group-addon"><i class="fa fa-phone"></i></span>
                    <input type="tel" placeholder="Số điện thoại (không bắt buộc)" name="phone" class="form-control"
                       pattern="[+0-9 ().-]{9,18}" maxlength="18" autocomplete="tel"
                       title="Nhập số điện thoại gồm 9–15 chữ số." value="<c:out value='${formPhone}'/>" />
                </div>
            </div>

            <div class="form-group">
                <div class="input-group">
                    <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                    <input type="password" placeholder="Mật khẩu (ít nhất 6 ký tự)" name="password" class="form-control"
                       minlength="6" maxlength="128" autocomplete="new-password" required />
                </div>
            </div>

            <div class="form-group">
                <div class="input-group">
                    <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                    <input type="password" placeholder="Nhập lại mật khẩu" name="confirm" class="form-control"
                           minlength="6" maxlength="128" autocomplete="new-password" required />
                </div>
            </div>

            <button type="submit" class="btn btn-register btn-block">Tạo tài khoản</button>

            <div class="text-center" style="margin-top: 20px;">
                <span>Nếu bạn đã có tài khoản? </span>
                <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
            </div>
        </form>
    </div>
</div>
<script>
(function () {
    var form = document.querySelector('form[action$="/register"]');
    if (!form) return;
    var password = form.querySelector('input[name="password"]');
    var confirm = form.querySelector('input[name="confirm"]');
    function validateConfirmation() {
        confirm.setCustomValidity(password.value === confirm.value ? '' : 'Mật khẩu xác nhận không khớp.');
    }
    password.addEventListener('input', validateConfirmation);
    confirm.addEventListener('input', validateConfirmation);
    form.addEventListener('submit', validateConfirmation);
}());
</script>
</body>
</html>
