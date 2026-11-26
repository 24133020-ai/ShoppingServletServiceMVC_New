<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm Video mới - Shopping MVC</title>
    <style>
        .form-card { background: #fff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); padding: 30px; margin-bottom: 30px; }
        .form-title { font-weight: bold; color: #17324d; margin-top: 0; margin-bottom: 20px; border-bottom: 2px solid #e9ecef; padding-bottom: 10px; }
    </style>
</head>
<body>

<jsp:include page="/views/common/navbar.jsp" />

<div class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="form-card">
                <h3 class="form-title"><i class="fa fa-upload text-primary"></i> Đăng Video mới</h3>

                <c:if test="${alert != null}">
                    <div class="alert alert-danger alert-dismissible" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <i class="fa fa-exclamation-triangle"></i> <c:out value="${alert}"/>
                    </div>
                </c:if>

                <form action="<c:url value='/video/insert'/>" method="post" enctype="multipart/form-data">
                    <div class="form-group">
                        <label for="videoId">Video ID (Mã định danh YouTube) <span class="text-danger">*</span>:</label>
                        <input type="text" class="form-control" id="videoId" name="videoId" maxlength="50"
                               pattern="[A-Za-z0-9_-]{1,50}" title="Chỉ gồm chữ không dấu, số, dấu gạch ngang và gạch dưới."
                               placeholder="Ví dụ: dQw4w9WgXcQ" value="<c:out value='${video.videoId}'/>" required autofocus />
                        <small class="help-block text-muted">ID trên link YouTube, ví dụ link https://www.youtube.com/watch?v=<b>dQw4w9WgXcQ</b> thì nhập <b>dQw4w9WgXcQ</b></small>
                    </div>

                    <div class="form-group">
                        <label for="title">Tiêu đề Video <span class="text-danger">*</span>:</label>
                        <input type="text" class="form-control" id="title" name="title" minlength="1" maxlength="255"
                               placeholder="Nhập tiêu đề video" value="<c:out value='${video.title}'/>" required />
                    </div>

                    <div class="form-group">
                        <label for="description">Mô tả video:</label>
                        <textarea class="form-control" id="description" name="description" rows="4" maxlength="5000"
                                  placeholder="Nhập mô tả nội dung video..."><c:out value="${video.description}"/></textarea>
                        <small class="help-block text-muted">Mô tả tối đa 5.000 ký tự.</small>
                    </div>

                    <div class="form-group">
                        <label for="poster">Upload poster (ảnh đại diện video):</label>
                        <input type="file" class="form-control" id="poster" name="poster" accept="image/png,image/jpeg,image/gif,image/webp" />
                        <small class="help-block text-muted">File ảnh PNG, JPG, GIF hoặc WEBP (tối đa 5MB).</small>
                    </div>

                    <div class="form-group">
                        <label>Trạng thái:</label><br/>
                        <label class="radio-inline">
                            <input type="radio" name="active" value="1" ${empty video || video.active ? 'checked' : ''} />
                            <span class="label label-success">Công khai (Hoạt động)</span>
                        </label>
                        <label class="radio-inline">
                            <input type="radio" name="active" value="0" ${not empty video && !video.active ? 'checked' : ''} />
                            <span class="label label-default">Riêng tư (Ẩn)</span>
                        </label>
                    </div>

                    <hr/>

                    <div class="form-group text-right" style="margin-bottom: 0;">
                        <a href="<c:url value='/video'/>" class="btn btn-default" style="margin-right: 10px;">Quay lại danh sách</a>
                        <button type="submit" class="btn btn-primary" style="padding-left: 25px; padding-right: 25px;">
                            <i class="fa fa-check"></i> Lưu Video
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
