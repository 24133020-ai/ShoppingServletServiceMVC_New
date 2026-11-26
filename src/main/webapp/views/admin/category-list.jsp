<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh mục sản phẩm - Shopping MVC</title>
    <style>
        .cate-panel { background: #fff; border-radius: 8px; box-shadow: 0 4px 15px rgba(0,0,0,0.06); padding: 25px; margin-bottom: 30px; }
        .table-cate th { background: #f8f9fa; color: #495057; }
        .table-cate td { vertical-align: middle !important; }
        .cate-img { max-width: 100px; max-height: 70px; object-fit: cover; border: 1px solid #dee2e6; border-radius: 4px; padding: 2px; }
    </style>
</head>
<body>

<jsp:include page="/views/common/navbar.jsp" />

<div class="container">
    <div class="cate-panel">
        <c:if test="${alert != null}">
            <div class="alert alert-danger alert-dismissible" role="alert">
                <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <c:out value="${alert}"/>
            </div>
        </c:if>

        <div class="row" style="margin-bottom: 20px;">
            <div class="col-sm-8">
                <h3 style="margin: 0; font-weight: bold; color: #17324d;">
                    <i class="fa fa-folder-open text-primary"></i> Danh mục sản phẩm (JPA Hibernate)
                </h3>
                <p class="text-muted" style="margin-top: 5px;">
                    Tổng số danh mục: <strong>${listcate.size()}</strong>
                    <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                        · <span class="label label-danger">Chế độ Quản trị</span>
                    </c:if>
                </p>
            </div>
            <div class="col-sm-4 text-right">
                <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                    <a href="<c:url value='/admin/category/add'/>" class="btn btn-success">
                        <i class="fa fa-plus"></i> Thêm Category mới
                    </a>
                </c:if>
                <a href="<c:url value='/home'/>" class="btn btn-default">
                    <i class="fa fa-home"></i> Trang chủ
                </a>
            </div>
        </div>

        <div class="table-responsive">
            <table class="table table-bordered table-hover table-cate">
                <thead>
                    <tr>
                        <th style="width: 60px; text-align: center;">STT</th>
                        <th style="width: 120px; text-align: center;">Hình ảnh</th>
                        <th>Tên danh mục</th>
                        <th style="width: 130px; text-align: center;">Trạng thái</th>
                        <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                            <th style="width: 160px; text-align: center;">Thao tác</th>
                        </c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${listcate}" var="cate" varStatus="STT">
                        <tr>
                            <td class="text-center"><strong>${STT.index + 1}</strong></td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${cate.images != null && (cate.images.startsWith('http://') || cate.images.startsWith('https://'))}">
                                        <c:url value="${cate.images}" var="imgUrl"></c:url>
                                    </c:when>
                                    <c:otherwise>
                                        <c:url value="/image?fname=${cate.images}" var="imgUrl"></c:url>
                                    </c:otherwise>
                                </c:choose>
                                <img class="cate-img" src="${imgUrl}" alt="${cate.categoryname}" onerror="this.src='https://via.placeholder.com/100x70?text=No+Image'" />
                            </td>
                            <td><strong style="font-size: 15px;"><c:out value="${cate.categoryname}"/></strong></td>
                            <td class="text-center">
                                <c:choose>
                                    <c:when test="${cate.status == 1}">
                                        <span class="label label-success">Hoạt động</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="label label-default">Đang khóa</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <c:if test="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2}">
                                <td class="text-center">
                                    <a href="<c:url value='/admin/category/edit?id=${cate.categoryId}'/>" class="btn btn-warning btn-xs">
                                        <i class="fa fa-pencil"></i> Sửa
                                    </a>
                                    <a href="<c:url value='/admin/category/delete?id=${cate.categoryId}'/>" class="btn btn-danger btn-xs" onclick="return confirm('Bạn có chắc chắn muốn xóa danh mục này?');">
                                        <i class="fa fa-trash"></i> Xóa
                                    </a>
                                </td>
                            </c:if>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty listcate}">
                        <tr>
                            <td colspan="${sessionScope.account.roleid == 1 || sessionScope.account.roleid == 2 ? 5 : 4}" class="text-center text-muted" style="padding: 30px;">
                                Chưa có danh mục nào trong hệ thống.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

</body>
</html>
