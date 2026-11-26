<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Cập nhật Category - JPA Hibernate</title>
    <style>
        .form-card { background: #fff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); padding: 30px; margin-bottom: 30px; }
        .form-title { font-weight: bold; color: #17324d; margin-top: 0; margin-bottom: 20px; border-bottom: 2px solid #e9ecef; padding-bottom: 10px; }
        .img-preview { max-width: 140px; max-height: 100px; object-fit: cover; border: 1px solid #ddd; border-radius: 4px; padding: 3px; margin: 8px 0; }
    </style>
</head>
<body>

<jsp:include page="/views/common/navbar.jsp" />

<div class="container">
    <div class="row">
        <div class="col-md-8 col-md-offset-2">
            <div class="form-card">
                <h3 class="form-title"><i class="fa fa-pencil text-warning"></i> Chỉnh sửa Danh mục (JPA 3.0)</h3>

                <c:if test="${alert != null}">
                    <div class="alert alert-danger alert-dismissible" role="alert">
                        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <i class="fa fa-exclamation-triangle"></i> <c:out value="${alert}"/>
                    </div>
                </c:if>

                <form action="<c:url value='/admin/category/update'/>" method="post" enctype="multipart/form-data">
                    <input type="hidden" name="categoryid" value="${cate.categoryId}">

                    <div class="form-group">
                        <label for="categoryname">Tên danh mục <span class="text-danger">*</span>:</label>
                        <input type="text" class="form-control" id="categoryname" name="categoryname"
                               value="<c:out value='${cate.categoryname}'/>" minlength="2" maxlength="255" required />
                    </div>

                    <div class="form-group">
                        <label for="images">Link hình ảnh (URL):</label>
                        <input type="text" class="form-control" id="images" name="images" maxlength="500"
                               value="<c:out value='${cate.images}'/>" />
                    </div>

                    <div class="form-group">
                        <label>Hình ảnh hiện tại:</label><br/>
                        <c:choose>
                            <c:when test="${cate.images != null && (cate.images.startsWith('http://') || cate.images.startsWith('https://'))}">
                                <c:url value="${cate.images}" var="imgUrl"></c:url>
                            </c:when>
                            <c:otherwise>
                                <c:url value="/image?fname=${cate.images}" var="imgUrl"></c:url>
                            </c:otherwise>
                        </c:choose>
                        <img class="img-preview" src="${imgUrl}" alt="${cate.categoryname}" onerror="this.src='https://via.placeholder.com/140x100?text=No+Image'"><br/>

                        <label for="images1" style="margin-top: 10px;">Tải ảnh mới từ máy tính (nếu muốn thay thế):</label>
                        <input type="file" class="form-control" id="images1" name="images1" accept="image/png,image/jpeg,image/gif,image/webp">
                        <small class="help-block text-muted">Hỗ trợ PNG, JPG, JPEG, GIF hoặc WEBP; tối đa 5MB. Để trống nếu giữ ảnh hiện tại.</small>
                    </div>

                    <div class="form-group">
                        <label>Trạng thái hoạt động:</label><br/>
                        <label class="radio-inline">
                            <input type="radio" id="ston" name="status" value="1" ${cate.status == 1 ? 'checked' : ''} required />
                            <span class="label label-success">Hoạt động</span>
                        </label>
                        <label class="radio-inline">
                            <input type="radio" id="stoff" name="status" value="0" ${cate.status != 1 ? 'checked' : ''} />
                            <span class="label label-default">Đang khóa</span>
                        </label>
                    </div>

                    <hr/>

                    <div class="form-group text-right" style="margin-bottom: 0;">
                        <a href="<c:url value='/admin/categories'/>" class="btn btn-default" style="margin-right: 10px;">Quay lại danh sách</a>
                        <button type="submit" class="btn btn-warning" style="padding-left: 25px; padding-right: 25px;">
                            <i class="fa fa-save"></i> Cập nhật danh mục
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
