<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm Category - JPA Hibernate</title>
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
                <h3 class="form-title"><i class="fa fa-plus-circle text-success"></i> Thêm mới Danh mục (JPA 3.0)</h3>

                <c:if test="${alert != null}">
                    <div class="alert alert-danger alert-dismissible" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <i class="fa fa-exclamation-triangle"></i> <c:out value="${alert}"/>
                    </div>
                </c:if>

                <form action="<c:url value='/admin/category/insert'/>" method="post" enctype="multipart/form-data">
                    <div class="form-group">
                        <label for="categoryname">Tên danh mục <span class="text-danger">*</span>:</label>
                        <input type="text" class="form-control" id="categoryname" name="categoryname"
                               placeholder="Nhập tên danh mục (ví dụ: Điện thoại, Laptop...)"
                               minlength="2" maxlength="255" value="<c:out value='${formCategoryName}'/>" required autofocus />
                    </div>

                    <div class="form-group">
                        <label for="images">Link hình ảnh trực tuyến (URL):</label>
                        <input type="text" class="form-control" id="images" name="images" maxlength="500"
                               placeholder="https://example.com/image.png" value="<c:out value='${formImage}'/>" />
                    </div>

                    <div class="form-group">
                        <label for="images1">Hoặc tải ảnh từ máy tính (Multipart Upload):</label>
                        <input type="file" class="form-control" id="images1" name="images1" accept="image/png,image/jpeg,image/gif,image/webp" />
                        <small class="help-block text-muted">Hỗ trợ PNG, JPG, JPEG, GIF hoặc WEBP; tối đa 5MB.</small>
                    </div>

                    <div class="form-group">
                        <label>Trạng thái hoạt động:</label><br/>
                        <label class="radio-inline">
                            <input type="radio" id="ston" name="status" value="1" ${empty formStatus || formStatus == '1' ? 'checked' : ''} required />
                            <span class="label label-success">Hoạt động</span>
                        </label>
                        <label class="radio-inline">
                            <input type="radio" id="stoff" name="status" value="0" ${formStatus == '0' ? 'checked' : ''} />
                            <span class="label label-default">Đang khóa</span>
                        </label>
                    </div>

                    <hr/>

                    <div class="form-group text-right" style="margin-bottom: 0;">
                        <a href="<c:url value='/admin/categories'/>" class="btn btn-default" style="margin-right: 10px;">Quay lại danh sách</a>
                        <button type="submit" class="btn btn-success" style="padding-left: 25px; padding-right: 25px;">
                            <i class="fa fa-check"></i> Lưu danh mục
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
