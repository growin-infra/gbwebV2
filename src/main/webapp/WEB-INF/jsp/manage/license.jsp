<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<!-- 내용 -->
<div class="content">
	<div class="box" id="box_product">
		<div class="box_title">라이선스 정보</div>
		<div class="box_product">
			<dl class="box_content">
			<c:choose>
				<c:when test="${!empty lcnInfoMap}">
				<dt>인스턴스수량</dt>
				<dd><input type="text" value="${lcnInfoMap.svr}" disabled></dd>
				<dt>발급일자</dt>
				<dd><input type="text" value="${lcnInfoMap.sdt}" disabled></dd>
				<dt>남은기간</dt>
				<dd><input type="text" value="${lcnInfoMap.remainDt}" disabled></dd>
				<dt>만료일자</dt>
				<dd><input type="text" value="${lcnInfoMap.edt}" disabled></dd>
				</c:when>
				<c:otherwise>
				<dt>라이선스 정보가 없습니다.</dt>
				</c:otherwise>
			</c:choose>
			</dl>
			<div class="box_product_content">
				<img src="/webdoc/images/common/product_support2.png"/>	
	        </div>
		</div>
	</div>
		
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
