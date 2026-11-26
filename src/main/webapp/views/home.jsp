<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Trang chủ - Shopping MVC</title>
    <style>
        .hero-banner { background: #fff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); padding: 25px 30px; margin-bottom: 25px; }
        .hero-title { font-weight: bold; color: #17324d; margin-top: 0; }
        .section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; border-bottom: 2px solid #e9ecef; padding-bottom: 8px; }
        .section-header h3 { margin: 0; font-weight: bold; color: #17324d; }
        .cate-card { background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); overflow: hidden; margin-bottom: 20px; transition: transform .2s; }
        .cate-card:hover { transform: translateY(-3px); }
        .cate-img-container { height: 140px; background: #f8f9fa; display: flex; align-items: center; justify-content: center; overflow: hidden; }
        .cate-img-box { max-width: 100%; max-height: 140px; object-fit: cover; }
        .cate-body { padding: 15px; }
        .video-card { background: #fff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); overflow: hidden; margin-bottom: 20px; }
        .video-thumb { width: 100%; height: 140px; object-fit: cover; background: #e5e5e5; }
        .video-body { padding: 12px 15px; }
    </style>
</head>
<body>

<jsp:include page="/views/common/navbar.jsp" />

<div class="container">
    <!-- Banner chào mừng người dùng -->
    <div class="hero-banner">
        <div class="row">
            <div class="col-md-8">
                <h2 class="hero-title">
                    <i class="fa fa-shopping-bag text-primary"></i> Chào mừng, <c:out value="${sessionScope.account != null ? sessionScope.account.fullName : 'Trần Hữu Việt Huân'}"/>!
                </h2>
                <p class="text-muted" style="font-size: 14px;">
                    Email: <strong><c:out value="${sessionScope.account.email}"/></strong> · Vai trò: 
                    <c:choose>
                        <c:when test="${sessionScope.account.roleid == 1}">
                            <span class="label label-danger">Quản trị viên (Admin)</span>
                        </c:when>
                        <c:when test="${sessionScope.account.roleid == 2}">
                            <span class="label label-warning">Quản lý (Manager)</span>
                        </c:when>
                        <c:otherwise>
                            <span class="label label-info">Người dùng (User)</span>
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
            <div class="col-md-4 text-right" style="padding-top: 15px;">
                <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                    <a href="<c:url value='/admin/category/add'/>" class="btn btn-success" style="margin-right: 5px;">
                        <i class="fa fa-plus"></i> Thêm Danh mục
                    </a>
                </c:if>
                <a href="<c:url value='/video/add'/>" class="btn btn-primary" style="margin-right: 5px;">
                    <i class="fa fa-upload"></i> Đăng Video
                </a>
                <a href="<c:url value='/profile'/>" class="btn btn-default">
                    <i class="fa fa-user-circle"></i> Profile
                </a>
            </div>
        </div>
    </div>

    <!-- 1. Danh mục sản phẩm (Categories) -->
    <div class="section-header">
        <h3><i class="fa fa-folder-open text-primary"></i> Danh mục sản phẩm (Categories)</h3>
        <div>
            <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                <a href="<c:url value='/admin/category/add'/>" class="btn btn-xs btn-success"><i class="fa fa-plus"></i> Thêm mới</a>
            </c:if>
            <a href="<c:url value='/categories'/>" class="btn btn-xs btn-default">Xem tất cả &rarr;</a>
        </div>
    </div>

    <div class="row">
        <c:forEach items="${categories}" var="c">
            <div class="col-xs-6 col-sm-4 col-md-3">
                <div class="cate-card">
                    <div class="cate-img-container">
                        <c:choose>
                            <c:when test="${not empty c.images && (c.images.startsWith('http://') || c.images.startsWith('https://'))}">
                                <img src="${c.images}" alt="${c.categoryname}" class="cate-img-box" />
                            </c:when>
                            <c:when test="${not empty c.images}">
                                <img src="<c:url value='/image?fname=${c.images}'/>" alt="${c.categoryname}" class="cate-img-box"
                                     onerror="this.src='https://via.placeholder.com/200x140?text=No+Image'" />
                            </c:when>
                            <c:otherwise>
                                <img src="https://via.placeholder.com/200x140?text=No+Image" alt="No image" class="cate-img-box" />
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="cate-body">
                        <h4 style="margin: 0 0 6px 0; font-size: 16px; font-weight: bold;">
                            <c:out value="${c.categoryname}"/>
                        </h4>
                        <div class="clearfix">
                            <span class="pull-left">
                                <c:choose>
                                    <c:when test="${c.status == 1}"><span class="label label-success">Hoạt động</span></c:when>
                                    <c:otherwise><span class="label label-default">Đang khóa</span></c:otherwise>
                                </c:choose>
                            </span>
                            <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                                <span class="pull-right">
                                    <a href="<c:url value='/admin/category/edit?id=${c.categoryId}'/>" class="text-primary" style="margin-right: 8px;"><i class="fa fa-pencil"></i></a>
                                    <a href="<c:url value='/admin/category/delete?id=${c.categoryId}'/>" class="text-danger" onclick="return confirm('Bạn có chắc muốn xóa danh mục này?');"><i class="fa fa-trash"></i></a>
                                </span>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty categories}">
            <div class="col-md-12 text-center text-muted" style="padding: 20px;">Chưa có danh mục nào.</div>
        </c:if>
    </div>

    <!-- 2. Video mới nhất -->
    <div class="section-header" style="margin-top: 20px;">
        <h3><i class="fa fa-film text-primary"></i> Video mới nhất</h3>
        <div>
            <a href="<c:url value='/video/add'/>" class="btn btn-xs btn-primary"><i class="fa fa-upload"></i> Đăng Video</a>
            <a href="<c:url value='/video'/>" class="btn btn-xs btn-default">Xem tất cả &rarr;</a>
        </div>
    </div>

    <div class="row">
        <c:forEach items="${newestVideos}" var="v">
            <div class="col-xs-6 col-sm-4 col-md-3">
                <div class="video-card">
                    <c:choose>
                        <c:when test="${not empty v.poster && (v.poster.startsWith('http://') || v.poster.startsWith('https://'))}">
                            <img src="${v.poster}" alt="poster" class="video-thumb" />
                        </c:when>
                        <c:when test="${not empty v.poster}">
                            <img src="<c:url value='/image?fname=${v.poster}'/>" alt="poster" class="video-thumb"
                                 onerror="this.src='https://via.placeholder.com/260x140?text=No+Poster'" />
                        </c:when>
                        <c:otherwise>
                            <div class="video-thumb" style="display: flex; align-items: center; justify-content: center; color: #adb5bd;">
                                <i class="fa fa-film fa-3x"></i>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <div class="video-body">
                        <h4 style="margin: 0 0 6px 0; font-size: 15px; font-weight: bold; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                            <a href="<c:url value='/video/detail?id=${v.videoId}'/>" title="${v.title}">
                                <c:out value="${v.title}"/>
                            </a>
                        </h4>
                        <p class="text-muted" style="margin: 0; font-size: 12px;">
                            <i class="fa fa-user"></i> <c:out value="${v.user.fullName}"/>
                            <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.id == v.user.id}">
                                <span class="pull-right">
                                    <a href="<c:url value='/video/edit?id=${v.videoId}'/>" class="text-primary"><i class="fa fa-pencil"></i></a>
                                </span>
                            </c:if>
                        </p>
                    </div>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty newestVideos}">
            <div class="col-md-12 text-center text-muted" style="padding: 20px;">Chưa có video nào.</div>
        </c:if>
    </div>
</div>

</body>
</html>
