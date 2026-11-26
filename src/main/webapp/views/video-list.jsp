<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách Video - Shopping MVC</title>
    <style>
        .video-panel { background: #fff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); padding: 25px; margin-bottom: 30px; }
        .poster { width: 100%; height: 160px; object-fit: cover; background: #e5e5e5; }
        .no-poster { width: 100%; height: 160px; background: #e5e5e5; color: #999; line-height: 160px; text-align: center; }
        .video-title { font-weight: bold; font-size: 15px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .video-card-box { border-radius: 6px; overflow: hidden; margin-bottom: 20px; transition: transform .2s; }
        .video-card-box:hover { transform: translateY(-3px); }
    </style>
</head>
<body>

<jsp:include page="/views/common/navbar.jsp" />

<div class="container">
    <div class="video-panel">
        <c:if test="${alert != null}">
            <div class="alert alert-danger alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <c:out value="${alert}"/>
            </div>
        </c:if>

        <div class="row" style="margin-bottom: 15px;">
            <div class="col-sm-8">
                <h3 style="margin: 0; font-weight: bold; color: #17324d;">
                    <i class="fa fa-film text-primary"></i> ${filter == 'my' ? 'Video của tôi' : 'Tất cả Video'}
                </h3>
            </div>
            <div class="col-sm-4 text-right">
                <a href="<c:url value='/video/add'/>" class="btn btn-primary">
                    <i class="fa fa-upload"></i> Đăng Video mới
                </a>
                <a href="<c:url value='/home'/>" class="btn btn-default">
                    <i class="fa fa-home"></i> Trang chủ
                </a>
            </div>
        </div>

        <!-- Tabs chuyển đổi giữa Tất cả Video và Video của tôi -->
        <ul class="nav nav-tabs" style="margin-bottom: 20px;">
            <li class="${filter != 'my' ? 'active' : ''}">
                <a href="<c:url value='/video?filter=all'/>"><i class="fa fa-globe"></i> Tất cả Video</a>
            </li>
            <li class="${filter == 'my' ? 'active' : ''}">
                <a href="<c:url value='/video?filter=my'/>"><i class="fa fa-user"></i> Video của tôi</a>
            </li>
        </ul>

        <div class="row">
            <c:forEach items="${listvideo}" var="v">
                <div class="col-sm-6 col-md-4">
                    <div class="thumbnail video-card-box">
                        <c:choose>
                            <c:when test="${not empty v.poster && (v.poster.startsWith('http://') || v.poster.startsWith('https://'))}">
                                <img class="poster" src="${v.poster}" alt="poster" />
                            </c:when>
                            <c:when test="${not empty v.poster}">
                                <img class="poster" src="<c:url value='/image?fname=${v.poster}'/>" alt="poster"
                                     onerror="this.src='https://via.placeholder.com/320x160?text=No+Poster'" />
                            </c:when>
                            <c:otherwise>
                                <div class="no-poster"><i class="fa fa-film fa-2x"></i> No poster</div>
                            </c:otherwise>
                        </c:choose>
                        <div class="caption">
                            <h4 class="video-title">
                                <a href="<c:url value='/video/detail?id=${v.videoId}'/>" title="${v.title}">
                                    <c:out value="${v.title}"/>
                                </a>
                            </h4>
                            <p class="text-muted" style="font-size: 12px; margin-bottom: 10px;">
                                <i class="fa fa-user"></i> <c:out value="${v.user.fullName}"/>
                                · <i class="fa fa-eye"></i> ${v.views} lượt xem
                            </p>
                            <div class="clearfix">
                                <a href="<c:url value='/video/detail?id=${v.videoId}'/>" class="btn btn-default btn-xs">
                                    <i class="fa fa-play"></i> Xem video
                                </a>
                                <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2 || sessionScope.account.id == v.user.id}">
                                    <span class="pull-right">
                                        <a href="<c:url value='/video/edit?id=${v.videoId}'/>" class="btn btn-warning btn-xs">
                                            <i class="fa fa-pencil"></i> Sửa
                                        </a>
                                        <a href="<c:url value='/video/delete?id=${v.videoId}'/>" class="btn btn-danger btn-xs"
                                           onclick="return confirm('Bạn có chắc muốn xóa video này?');">
                                            <i class="fa fa-trash"></i> Xóa
                                        </a>
                                    </span>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty listvideo}">
                <div class="col-md-12 text-center text-muted" style="padding: 40px;">
                    <i class="fa fa-film fa-3x" style="color: #dee2e6; margin-bottom: 10px;"></i>
                    <p>Chưa có video nào trong mục này. Hãy bấm "Đăng Video mới" để chia sẻ video đầu tiên!</p>
                </div>
            </c:if>
        </div>

        <!-- Phân trang -->
        <c:if test="${totalPages > 1}">
            <div class="text-center">
                <ul class="pagination">
                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <li class="${p == currentPage ? 'active' : ''}">
                            <a href="<c:url value='/video?filter=${filter}&page=${p}'/>">${p}</a>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>
