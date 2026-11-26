<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý Người dùng (User Management) - Shopping MVC</title>
    <style>
        .user-card { background: #fff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); padding: 25px; margin-bottom: 30px; }
        .table-user th { background: #f8f9fa; color: #495057; }
        .table-user td { vertical-align: middle !important; }
        .user-avatar-sm { width: 38px; height: 38px; border-radius: 50%; object-fit: cover; border: 1px solid #dee2e6; }
    </style>
</head>
<body>

<jsp:include page="/views/common/navbar.jsp" />

<div class="container">
    <div class="user-card">
        <div class="row" style="margin-bottom: 20px;">
            <div class="col-sm-8">
                <h3 style="margin: 0; font-weight: bold; color: #17324d;">
                    <i class="fa fa-users text-primary"></i> Quản lý danh sách Người dùng
                </h3>
                <p class="text-muted" style="margin-top: 5px;">
                    Tổng số người dùng trong hệ thống: <strong>${userList.size()}</strong>
                </p>
            </div>
            <div class="col-sm-4 text-right">
                <a href="<c:url value='/home'/>" class="btn btn-default"><i class="fa fa-arrow-left"></i> Trang chủ</a>
                <a href="<c:url value='/admin/categories'/>" class="btn btn-info"><i class="fa fa-folder-open"></i> Quản lý Danh mục</a>
            </div>
        </div>

        <div class="table-responsive">
            <table class="table table-bordered table-hover table-user">
                <thead>
                    <tr>
                        <th style="width: 50px;" class="text-center">ID</th>
                        <th style="width: 60px;" class="text-center">Avatar</th>
                        <th>Tài khoản</th>
                        <th>Họ và tên</th>
                        <th>Email</th>
                        <th>Số điện thoại</th>
                        <th style="width: 100px;" class="text-center">Vai trò</th>
                        <th style="width: 120px;" class="text-center">Trạng thái</th>
                        <th style="width: 190px;" class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${userList}" var="u">
                        <tr>
                            <td class="text-center"><strong>${u.id}</strong></td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${not empty u.avatar && (u.avatar.startsWith('http://') || u.avatar.startsWith('https://'))}">
                                        <img src="${u.avatar}" alt="Avatar" class="user-avatar-sm" />
                                    </c:when>
                                    <c:when test="${not empty u.avatar}">
                                        <img src="<c:url value='/image?fname=${u.avatar}'/>" alt="Avatar" class="user-avatar-sm" />
                                    </c:when>
                                    <c:otherwise>
                                        <div style="width: 38px; height: 38px; border-radius: 50%; background: #e9ecef; color: #adb5bd; line-height: 38px; text-align: center; margin: auto;">
                                            <i class="fa fa-user"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <strong><c:out value="${u.userName}"/></strong>
                                <c:if test="${sessionScope.account.id == u.id}">
                                    <span class="badge" style="background: #20c997;">Bạn</span>
                                </c:if>
                            </td>
                            <td><c:out value="${u.fullName}"/></td>
                            <td><c:out value="${u.email}"/></td>
                            <td><c:out value="${not empty u.phone ? u.phone : '-'}"/></td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${u.roleid == 1}">
                                        <span class="label label-danger">Admin</span>
                                    </c:when>
                                    <c:when test="${u.roleid == 2}">
                                        <span class="label label-warning">Manager</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="label label-info">User</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${u.active}">
                                        <span class="label label-success"><i class="fa fa-check"></i> Hoạt động</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="label label-default"><i class="fa fa-ban"></i> Đang khóa</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${sessionScope.account.id == u.id}">
                                        <span class="text-muted" style="font-size: 12px;">(Tài khoản hiện tại)</span>
                                    </c:when>
                                    <c:otherwise>
                                        <!-- Khóa / Mở khóa -->
                                        <c:choose>
                                            <c:when test="${u.active}">
                                                <a href="<c:url value='/admin/user/toggle-status?id=${u.id}'/>"
                                                   class="btn btn-xs btn-warning"
                                                   onclick="return confirm('Bạn có chắc muốn KHÓA tài khoản [${u.userName}]?');">
                                                    <i class="fa fa-lock"></i> Khóa
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="<c:url value='/admin/user/toggle-status?id=${u.id}'/>"
                                                   class="btn btn-xs btn-success"
                                                   onclick="return confirm('Bạn có chắc muốn KÍCH HOẠT lại tài khoản [${u.userName}]?');">
                                                    <i class="fa fa-unlock"></i> Mở
                                                </a>
                                            </c:otherwise>
                                        </c:choose>

                                        <!-- Thay đổi vai trò -->
                                        <c:choose>
                                            <c:when test="${u.roleid == 1}">
                                                <a href="<c:url value='/admin/user/change-role?id=${u.id}&role=3'/>"
                                                   class="btn btn-xs btn-default"
                                                   onclick="return confirm('Chuyển [${u.userName}] thành User thường?');">
                                                    <i class="fa fa-level-down"></i> Về User
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="<c:url value='/admin/user/change-role?id=${u.id}&role=1'/>"
                                                   class="btn btn-xs btn-danger"
                                                   onclick="return confirm('NÂNG CẤP [${u.userName}] lên làm Admin?');">
                                                    <i class="fa fa-shield"></i> Lên Admin
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>
