<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xác nhận OTP đổi Email - Shopping MVC</title>
    <style>
        .box { max-width: 460px; margin: 40px auto; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }
        .box-title { text-align: center; font-size: 22px; font-weight: 600; margin-bottom: 15px; color: #17324d; }
        .otp-input { font-size: 26px; letter-spacing: 10px; text-align: center; font-weight: bold; }
    </style>
</head>
<body>

<jsp:include page="/views/common/navbar.jsp" />

<div class="container">
    <div class="box">
        <h2 class="box-title"><i class="fa fa-envelope-o text-primary"></i> Xác thực Email mới</h2>
        <p class="text-center text-muted" style="margin-bottom: 20px;">
            Mã OTP gồm 6 chữ số đã được gửi đến email mới của bạn:
            <br/><strong class="text-primary" style="font-size: 15px;"><c:out value="${newEmail}"/></strong>
        </p>

        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <c:out value="${success}"/>
            </div>
        </c:if>

        <c:if test="${not empty alert}">
            <div class="alert alert-danger alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <c:out value="${alert}"/>
            </div>
        </c:if>

        <form action="<c:url value='/verify-email-otp'/>" method="post">
            <div class="form-group">
                <label for="otp">Nhập mã OTP (6 chữ số):</label>
                <input type="text" id="otp" name="otp" class="form-control input-lg otp-input"
                       pattern="[0-9]{6}" maxlength="6" inputmode="numeric" placeholder="------"
                       autocomplete="one-time-code" required autofocus />
                <small class="help-block text-muted text-center" style="margin-top: 8px;">
                    Mã có hiệu lực trong 5 phút. Vui lòng kiểm tra cả thư mục Spam.
                </small>
            </div>

            <button type="submit" class="btn btn-primary btn-block btn-lg" style="margin-top: 15px;">
                <i class="fa fa-check"></i> Xác nhận &amp; Cập nhật Email
            </button>
        </form>

        <form action="<c:url value='/verify-email-otp'/>" method="post" style="margin-top: 15px;">
            <input type="hidden" name="action" value="resend" />
            <button type="submit" class="btn btn-default btn-block">
                <i class="fa fa-refresh"></i> Gửi lại mã OTP
            </button>
        </form>

        <div class="text-center" style="margin-top: 20px;">
            <a href="<c:url value='/profile'/>" class="text-muted"><i class="fa fa-arrow-left"></i> Quay lại trang Profile</a>
        </div>
    </div>
</div>

</body>
</html>
