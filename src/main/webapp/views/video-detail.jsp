<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title><c:out value="${video.title}"/> - Shopping MVC</title>
    <style>
        .player-card { background: #fff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); padding: 25px; margin-bottom: 30px; }
        .video-player-container { position: relative; padding-bottom: 56.25%; height: 0; overflow: hidden; border-radius: 6px; background: #000; margin-bottom: 20px; }
        .video-player-container iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: 0; }
    </style>
</head>
<body>

<jsp:include page="/views/common/navbar.jsp" />

<div class="container">
    <div class="row">
        <div class="col-md-9">
            <div class="player-card">
                <div class="video-player-container">
                    <iframe src="https://www.youtube.com/embed/<c:out value='${video.videoId}'/>?autoplay=1"
                            title="video player" allowfullscreen></iframe>
                </div>
                <h3 style="font-weight: bold; margin-top: 0; color: #17324d;"><c:out value="${video.title}"/></h3>
                <p class="text-muted" style="border-bottom: 1px solid #eee; padding-bottom: 12px; margin-bottom: 15px;">
                    <i class="fa fa-eye"></i> ${video.views} lượt xem
                    &nbsp;|&nbsp; <i class="fa fa-user"></i> Người đăng: <strong><c:out value="${video.user.fullName}"/></strong>
                    &nbsp;|&nbsp;
                    <c:choose>
                        <c:when test="${video.active}"><span class="label label-success">Công khai</span></c:when>
                        <c:otherwise><span class="label label-default">Riêng tư</span></c:otherwise>
                    </c:choose>
                </p>
                <div class="well well-sm" style="white-space: pre-wrap; background: #f8f9fa; border: 1px solid #e9ecef;"><c:out value="${video.description}"/></div>
                
                <div class="clearfix">
                    <a href="<c:url value='/video'/>" class="btn btn-default"><i class="fa fa-arrow-left"></i> Danh sách Video</a>
                    <a href="<c:url value='/home'/>" class="btn btn-default"><i class="fa fa-home"></i> Trang chủ</a>
                    <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2 || sessionScope.account.id == video.user.id}">
                        <a href="<c:url value='/video/edit?id=${video.videoId}'/>" class="btn btn-warning pull-right">
                            <i class="fa fa-pencil"></i> Sửa Video này
                        </a>
                    </c:if>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="panel panel-default">
                <div class="panel-heading"><strong>Tác vụ nhanh</strong></div>
                <div class="list-group">
                    <a href="<c:url value='/video/add'/>" class="list-group-item"><i class="fa fa-upload"></i> Đăng Video mới</a>
                    <a href="<c:url value='/video?filter=my'/>" class="list-group-item"><i class="fa fa-film"></i> Video của tôi</a>
                    <a href="<c:url value='/categories'/>" class="list-group-item"><i class="fa fa-folder-open"></i> Xem Danh mục sản phẩm</a>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
