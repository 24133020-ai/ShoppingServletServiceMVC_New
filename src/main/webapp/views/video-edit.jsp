<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chỉnh sửa Video - Shopping MVC</title>
    <style>
        .form-card { background: #fff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); padding: 30px; margin-bottom: 30px; }
        .form-title { font-weight: bold; color: #17324d; margin-top: 0; margin-bottom: 20px; border-bottom: 2px solid #e9ecef; padding-bottom: 10px; }
        .poster-preview { width: 180px; height: 100px; object-fit: cover; border: 1px solid #ddd; border-radius: 4px; margin: 8px 0; }
    </style>
</head>
<body>

<jsp:include page="/views/common/navbar.jsp" />

<div class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="form-card">
                <h3 class="form-title"><i class="fa fa-pencil text-warning"></i> Chỉnh sửa Video</h3>

                <c:if test="${alert != null}">
                    <div class="alert alert-danger alert-dismissible" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <i class="fa fa-exclamation-triangle"></i> <c:out value="${alert}"/>
                    </div>
                </c:if>

                <form action="<c:url value='/video/update'/>" method="post" enctype="multipart/form-data">
                    <div class="form-group">
                        <label>Video ID:</label>
                        <input type="text" class="form-control" name="videoId" value="<c:out value='${video.videoId}'/>" readonly />
                    </div>

                    <div class="form-group">
                        <label for="title">Tiêu đề Video <span class="text-danger">*</span>:</label>
                        <input type="text" class="form-control" id="title" name="title" minlength="1" maxlength="255"
                               value="<c:out value='${video.title}'/>" required autofocus />
                    </div>

                    <div class="form-group">
                        <label for="description">Mô tả video:</label>
                        <textarea class="form-control" id="description" name="description" rows="4" maxlength="5000"><c:out value="${video.description}"/></textarea>
                    </div>

                    <div class="form-group">
                        <label>Poster hiện tại:</label><br/>
                        <c:choose>
                            <c:when test="${not empty video.poster && (video.poster.startsWith('http://') || video.poster.startsWith('https://'))}">
                                <img src="${video.poster}" alt="Poster" class="poster-preview" />
                            </c:when>
                            <c:when test="${not empty video.poster}">
                                <img src="<c:url value='/image?fname=${video.poster}'/>" alt="Poster" class="poster-preview" />
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">Chưa có poster</span>
                            </c:otherwise>
                        </c:choose>
                        <br/>
                        <label for="poster" style="margin-top: 8px;">Tải ảnh poster mới (nếu muốn thay thế):</label>
                        <input type="file" class="form-control" id="poster" name="poster" accept="image/png,image/jpeg,image/gif,image/webp" />
                        <small class="help-block text-muted">File ảnh PNG, JPG, GIF hoặc WEBP (tối đa 5MB). Để trống nếu giữ poster cũ.</small>
                    </div>

                    <div class="form-group">
                        <label>Trạng thái:</label><br/>
                        <label class="radio-inline">
                            <input type="radio" name="active" value="1" ${video.active ? 'checked' : ''} />
                            <span class="label label-success">Công khai</span>
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="active" value="0" ${!video.active ? 'checked' : ''} />
                            <span class="label label-default">Riêng tư</span>
                        </label>
                    </div>

                    <hr/>

                    <div class="form-group text-right" style="margin-bottom: 0;">
                        <a href="<c:url value='/video'/>" class="btn btn-default" style="margin-right: 10px;">Quay lại danh sách</a>
                        <button type="submit" class="btn btn-warning" style="padding-left: 25px; padding-right: 25px;">
                            <i class="fa fa-save"></i> Cập nhật Video
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
