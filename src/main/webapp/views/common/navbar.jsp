<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="navbar navbar-inverse" style="border-radius: 0; margin-bottom: 25px; background: #17324d; border-color: #0f2438;">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#main-navbar" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="<c:url value='/home'/>" style="color: #fff; font-weight: bold; letter-spacing: .5px;">
                <i class="fa fa-shopping-bag" style="color: #20c997;"></i> Shopping MVC
            </a>
        </div>

        <div class="collapse navbar-collapse" id="main-navbar">
            <ul class="nav navbar-nav">
                <li><a href="<c:url value='/home'/>"><i class="fa fa-home"></i> Trang chủ</a></li>
                <li><a href="<c:url value='/categories'/>"><i class="fa fa-folder-open"></i> Danh mục sản phẩm</a></li>
                <li><a href="<c:url value='/video'/>"><i class="fa fa-film"></i> Video</a></li>
                <c:if test="${sessionScope.account != null && (sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2)}">
                    <li><a href="<c:url value='/admin/users'/>"><i class="fa fa-users"></i> Quản lý User</a></li>
                </c:if>
            </ul>

            <ul class="nav navbar-nav navbar-right">
                <c:choose>
                    <c:when test="${sessionScope.account != null}">
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false" style="color: #fff; padding-top: 12px; padding-bottom: 12px;">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.account.avatar && (sessionScope.account.avatar.startsWith('http://') || sessionScope.account.avatar.startsWith('https://'))}">
                                        <img src="${sessionScope.account.avatar}" alt="Avatar" style="width: 26px; height: 26px; border-radius: 50%; object-fit: cover; margin-right: 5px; border: 1px solid #fff;" />
                                    </c:when>
                                    <c:when test="${not empty sessionScope.account.avatar}">
                                        <img src="<c:url value='/image?fname=${sessionScope.account.avatar}'/>" alt="Avatar" style="width: 26px; height: 26px; border-radius: 50%; object-fit: cover; margin-right: 5px; border: 1px solid #fff;" />
                                    </c:when>
                                    <c:otherwise>
                                        <i class="fa fa-user-circle-o fa-lg" style="margin-right: 5px;"></i>
                                    </c:otherwise>
                                </c:choose>
                                <strong><c:out value="${sessionScope.account.fullName}"/></strong>
                                <c:choose>
                                    <c:when test="${sessionScope.account.roleid == 1}">
                                        <span class="label label-danger" style="margin-left: 4px; font-size: 10px;">Admin</span>
                                    </c:when>
                                    <c:when test="${sessionScope.account.roleid == 2}">
                                        <span class="label label-warning" style="margin-left: 4px; font-size: 10px;">Manager</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="label label-info" style="margin-left: 4px; font-size: 10px;">User</span>
                                    </c:otherwise>
                                </c:choose>
                                <span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu">
                                <li><a href="<c:url value='/profile'/>"><i class="fa fa-id-card-o"></i> Thông tin cá nhân (Profile)</a></li>
                                <li><a href="<c:url value='/video?filter=my'/>"><i class="fa fa-video-camera"></i> Video của tôi</a></li>
                                <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                                    <li role="separator" class="divider"></li>
                                    <li><a href="<c:url value='/admin/categories'/>"><i class="fa fa-cogs"></i> Quản lý Danh mục</a></li>
                                    <li><a href="<c:url value='/admin/users'/>"><i class="fa fa-users"></i> Quản lý Người dùng</a></li>
                                </c:if>
                                <li role="separator" class="divider"></li>
                                <li><a href="<c:url value='/logout'/>" style="color: #d9534f;"><i class="fa fa-sign-out"></i> Đăng xuất</a></li>
                            </ul>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="<c:url value='/login'/>"><i class="fa fa-sign-in"></i> Đăng nhập</a></li>
                        <li><a href="<c:url value='/register'/>"><i class="fa fa-user-plus"></i> Đăng ký</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>
