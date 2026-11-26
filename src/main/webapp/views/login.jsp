<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập hệ thống</title>
    <!-- Bootstrap 3 CDN -->
<style>
        body {
            background-color: #f4f7f6;
            padding-top: 60px;
        }
        .login-box {
            max-width: 420px;
            margin: auto;
            background: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        .login-title {
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
        .btn-login {
            background-color: #0088cc;
            color: white;
            font-size: 16px;
            padding: 10px;
            font-weight: bold;
        }
        .btn-login:hover {
            background-color: #006699;
            color: white;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="login-box">
        <h2 class="login-title">Đăng Nhập Vào Hệ Thống</h2>

        <c:if test="${success != null}">
            <div class="alert alert-success"><c:out value="${success}"/></div>
        </c:if>
        <c:if test="${alert != null}">
            <div class="alert alert-danger"><c:out value="${alert}"/></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <section class="form-group">
                <div class="input-group">
                    <span class="input-group-addon"><i class="fa fa-user"></i></span>
                    <input type="text" placeholder="Tài khoản" name="username" class="form-control" maxlength="50" autocomplete="username" value="<c:out value='${rememberedUsername}'/>" required aria-describedby="usernameHelp" />
                </div>
            </section>

            <section class="form-group">
                <div class="input-group">
                    <span class="input-group-addon"><i class="fa fa-lock"></i></span>
                    <input type="password" placeholder="Mật khẩu" name="password" class="form-control" minlength="6" maxlength="128" autocomplete="current-password" required />
                </div>
            </section>

            <div class="form-group clearfix">
                <div class="pull-left">
                    <label>
                        <input type="checkbox" name="remember" ${rememberedUsername != null ? 'checked' : ''}> Nhớ tôi
                    </label>
                </div>
                <div class="pull-right">
                    <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a>
                </div>
            </div>

            <button type="submit" class="btn btn-login btn-block">Đăng nhập</button>

            <div class="text-center" style="margin-top: 20px;">
                <span>Nếu bạn chưa có tài khoản trên hệ thống, thì hãy </span>
                <a href="${pageContext.request.contextPath}/register">Đăng ký</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
