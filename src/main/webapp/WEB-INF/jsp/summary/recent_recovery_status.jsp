<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>

<script type="text/javascript">
$(document).ready(function() {
	$("#rrs_refresh").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        rrs_refresh(1);
    });
	$("#rrs_excel").on("click", function(e){
        e.preventDefault();
        fn_exceldown();
    });
	
	$("#spin").hide();
});

function rrs_refresh(pageNo) {
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/smy_rrs");
    comSubmit.addParam("menu_cd", $("#menu_cd").val());
    comSubmit.addParam("currentPageNo", pageNo);
    comSubmit.submit();
}

var cpno = "<c:out value='${cpno}'/>";
//Excel Download
function fn_exceldown() {
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/smy_rrs_excel");
    comSubmit.addParam("excel_title", $("h2").text()); //Excel Title
    comSubmit.addParam("cpno", cpno);
    comSubmit.submit();
}
</script>
<!-- 내용 -->
<div class="content">
	<!-- 제목 -->
	<div class="title">
		<h2>서버별 최근 복구현황</h2>
		<div class="btn">
			<a href="#void" class="reset" id="rrs_refresh">Refresh</a>
			<a href="#void" class="down" id="rrs_excel">Excel Download</a>
		</div>    
	</div>
	<!-- 표 -->
	<table class="table1 trLink">
		<colgroup>
			<col style="width:14%;"> <!-- 대상서버 -->
			<col style="width:12%;"> <!-- 인스턴스 -->
			<col style="width:16%;"> <!-- 최근복구 -->
			<col style="width:7%;"> <!-- 성공여부 -->
			<col style="width:12%;"> <!-- 백업타입 -->
			<col style="width:9%;"> <!-- 백업레벨 -->
			<col style="width:12%;"> <!-- 백업방법 -->
			<col style="width:10%;"> <!-- 수행시간 -->
			<col style="width:7%;"> <!-- 보관기간 -->
		</colgroup>
		<thead>
			<th>대상서버</th>
			<th>인스턴스</th>
			<th>최근복구</th>
			<th>성공여부</th>
			<th>백업타입</th>
			<th>백업레벨</th>
			<th>백업방법</th>
			<th>수행시간</th>
			<th>보관기간</th>
		</thead>
		<tbody>
			<c:choose>
                <c:when test="${fn:length(list) > 0}">
                    <c:forEach var="row" items="${list}" varStatus="status">
                        <tr onclick="fn_r_rec_sm('${row.bms_id }','${row.bts_id }')">
                            <td class="align_left">${row.mng_svr }</td>
                            <td class="align_left">${row.tgt_svr }</td>
                            <td>${row.upd_dt }</td>
                            <td>${row.suc_yon }</td>
                            <td>${row.typ }</td>
                            <td>${row.lvl }</td>
                            <td>${row.mtd }</td>
                            <td class="align_right">${cfn:secondToStr(row.pas_tme) }</td>
                            <td class="align_right">${row.kep_pod }일</td>
                        </tr>
                    </c:forEach>  
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="9" class="nodata">조회된 결과가 없습니다.</td>
                    </tr>
                </c:otherwise>
			</c:choose>
		</tbody>
	</table><!-- //표 -->
	
	<!-- 페이징 -->
	<div class="paginationframe">
		<div class="pagination" style="margin:20px auto 0;">
			<c:if test="${pagination.startPage-1 != 0 }">
				<a href="javascript:rrs_refresh(${pagination.startPage-1})">&laquo;</a>
			</c:if>
			<c:forEach var="i" begin="${pagination.startPage }" end="${pagination.endPage }" varStatus="status">
				<a <c:if test="${i eq pagination.currentPage }">class="active"</c:if> href="javascript:rrs_refresh(${i})">${i}</a>
			</c:forEach>
			<c:if test="${pagination.endPage % pagination.pageCount == 0 && pagination.lastPage > pagination.endPage}">
				<a  href="javascript:rrs_refresh(${pagination.endPage+1})">&raquo;</a>
			</c:if>
		</div>
	</div>
	
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
