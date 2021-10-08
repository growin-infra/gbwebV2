<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function(){
	fn_init();
});
function fn_init() {
	var wdt = "<c:out value='${llmap.wrk_dt }'/>";
	$("#wrk_dt").html("");
	if (wdt != "") {
		$("#wrk_dt").append(strToCustomFormat(wdt));
	}
}
</script>
<!-- 내용 -->
<div class="content">
	
	<div class="box">
		<!-- untitle -->
		<div class="untitle"></div>
		<dl class="box_content view">
			<dt>마지막 백업</dt>
			<dd id="wrk_dt"></dd>
			<dt>성공 여부</dt>
			<dd>${llmap.suc_yon }</dd>
			<dt>백업 타입</dt>
			<dd>${llmap.typ }</dd>
			<dt>백업 레벨</dt>
			<dd>${llmap.lvl }</dd>
			<dt>백업 방법</dt>
			<dd>${llmap.mtd }</dd>
			<dt>경과 시간</dt>
			<dd>${cfn:secondToStr(llmap.pas_tme) }</dd>
			<dt>보관 기간</dt>
			<c:if test="${empty llmap.kep_pod}">
			<dd></dd>
			</c:if>
			<c:if test="${!empty llmap.kep_pod}">
			<dd>${llmap.kep_pod } 일</dd>
			</c:if>
		</dl>
	</div>
	
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
