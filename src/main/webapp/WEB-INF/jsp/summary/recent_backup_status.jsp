<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
	$("#rbs_refresh").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        rbs_refresh(1);
    });
	$("#rbs_excel").on("click", function(e){
        e.preventDefault();
        fn_exceldown();
    });
	
	fn_init();
	
});

function fn_init() {

	fn_disk_use();
}
function rbs_refresh(pageNo) {
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/smy_rbs");
    comSubmit.addParam("menu_cd", $("#menu_cd").val());
    comSubmit.addParam("currentPageNo", pageNo);
    comSubmit.submit();
}

function fn_disk_use() {
	var comAjax = new ComAjax();
	comAjax.setUrl("/smy_disk_use");
	comAjax.setCallback("fn_disk_useload");
	comAjax.ajax();	
}
function fn_disk_useload(data) {
	
	var list = data.disklist;
 	var obj = $("#id_disk");
 	obj.html("");
 	var str = "";
	if (list != null) {
 		if (list.length > 0) {
 			var ms_nam, ms_ip, ms_port, ms_usr, ms_pwd, ms_sve_dir;
 			for (var i=0; i<list.length; i++) {
 				var nam = list[i].ms_nam;
 				if (nam != null && nam != "") {
 					str += "<span id='"+nam+"'></span>";
 				}
 			}
		 	obj.append(str);

 			for (var i=0; i<list.length; i++) {
 				var nam = list[i].ms_nam;
 				var ms_ip = list[i].ms_ip;
 				var ms_port = list[i].ms_port;
 				var ms_usr = list[i].ms_usr;
 				var ms_pwd = list[i].ms_pwd;
 				var ms_sve_dir = list[i].ms_sve_dir;
 				if (nam != null && nam != "") {
 					var opt10 = {colorRange:true,horTitle:nam};
 					fn_disk_val(nam,ms_ip,ms_port,ms_usr,ms_pwd,ms_sve_dir);
 				}
 			}
	 	}
	}
	
	/*
	//,horLabelPos:'top' //'left'/'right'/'top'
	var opt10 = {colorRange:true,horTitle:'vm1'};
	$('#bar').html(20);
	$('#bar').barIndicator(opt10);
	
	var opt10 = {colorRange:true,horTitle:'vm2'};
	$('#bar1').html(40);
	$('#bar1').barIndicator(opt10);
	
	var opt10 = {colorRange:true,horTitle:'vm3'};
	$('#bar2').html(60);
	$('#bar2').barIndicator(opt10);
	
	var opt10 = {colorRange:true,horTitle:'vm4'};
	$('#bar3').html(50);
	$('#bar3').barIndicator(opt10);
	*/
}

function fn_disk_val(nam,ms_ip,ms_port,ms_usr,ms_pwd,ms_sve_dir) {
	var comAjax = new ComAjaxAsync();
	comAjax.setUrl("/disk_use");
	comAjax.setCallback("fn_disk_valload");
	comAjax.addParam("dir", ms_sve_dir);
	comAjax.addParam("ip", ms_ip);
	comAjax.addParam("port", ms_port);
	comAjax.addParam("usr", ms_usr);
	comAjax.addParam("pwd", ms_pwd);
	comAjax.addParam("nam", nam);
	comAjax.ajax();	
}
var idCount = 0;
function fn_disk_valload(data) {
	var serverName = data.nam;
	var rateValue =data.diskUseVal;
	var sizeValue = data.diskUsed +" / " + data.diskTotal;
	idCount++;
	ginianBarChart(serverName, rateValue, sizeValue , idCount);
	loadingDiv.off();
}

var cpno = "<c:out value='${cpno}'/>";
//Excel Download
function fn_exceldown() {
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/smy_rbs_excel");
    comSubmit.addParam("excel_title", $("h2").text()); //Excel Title
    comSubmit.addParam("cpno", cpno);
    comSubmit.submit();
}

</script>
<!-- 내용 -->
<div class="content">

	<div class="box">
		<div class="box_title">관리서버 디스크 사용량</div>
		<div id="barChart"></div>
	</div>

	<!-- 제목 -->
	<div class="title">
		<h2>서버별 최근 백업현황</h2>
		<div class="btn">
			<a href="#void" class="reset" id="rbs_refresh">Refresh</a>
			<a href="#void" class="down" id="rbs_excel">Excel Download</a>
		</div>
	</div>
	
	<!-- 표 -->
	<table class="table1 trLink">
		<colgroup>
			<col style="width:14%;"> <!-- 대상서버 -->
			<col style="width:12%;"> <!-- 인스턴스 -->
			<col style="width:16%;"> <!-- 최근백업 -->
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
			<th>최근백업</th>
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
			<tr onclick="fn_b_rec('${row.bms_id }','${row.bts_id }')">
				<td class="align_left">${row.mng_svr }</td>
				<%-- <td class="align_left">${row.tgt_svr }</td> --%>
				<%-- <td class="align_left">${row.upd_dt }</td> --%>
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
				<a href="javascript:rbs_refresh(${pagination.startPage-1})">&laquo;</a>
			</c:if>
			<c:forEach var="i" begin="${pagination.startPage }" end="${pagination.endPage }" varStatus="status">
				<a <c:if test="${i eq pagination.currentPage }">class="active"</c:if> href="javascript:rbs_refresh(${i})">${i}</a>
			</c:forEach>
			<c:if test="${pagination.endPage % pagination.pageCount == 0 && pagination.lastPage > pagination.endPage}">
				<a href="javascript:rbs_refresh(${pagination.endPage+1})">&raquo;</a>
			</c:if>
		</div>
	</div>
	<!-- //페이징 -->
	
</div><!-- //내용 -->
<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
