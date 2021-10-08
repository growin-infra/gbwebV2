<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<%@ include file="../include/calendar.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
	fn_log(today);
});

function fn_log(sDay) {
	$(".log_modal").hide();
	var comAjax = new ComAjax();
	comAjax.setUrl("/set_r_rec_log");
	comAjax.setCallback("fn_logload");
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("day", sDay);
	comAjax.ajax();
}
function fn_logload(data) {
	var list = data.list;
	$("#iDay").html("");
	$('#tbody').html("");
	var tmp = "";
	if (list != null && list.length > 0) {
		for (var i=0; i<list.length; i++) {
			tmp += "<tr onclick='fn_logdetail("+list[i].seq+",\""+list[i].wrk_dt+"\");'>";
			tmp += "<td>"+strToCustomFormat(list[i].wrk_dt)+"</td>";
			tmp += "<td>"+list[i].typ+"</td>";
			tmp += "<td>"+list[i].lvl+"</td>";
			tmp += "<td>"+list[i].mtd+"</td>";
			tmp += "<td>"+list[i].kep_pod+"일"+"</td>";
			tmp += "<td>"+secToStr(list[i].pas_tme)+"</td>";
			tmp += "<td>"+list[i].suc_yon+"</td>";
			tmp += "<td>"+list[i].hst_ip+"</td>";
			tmp += "</tr>";
		}
	} else {
		tmp = "<tr><td colspan='8' class='nodata'>조회된 결과가 없습니다.</td></tr>";
	}
	$('#tbody').append(tmp);
	var sDay = data.sDay;
	$("#iDay").html(sDay);
}

function taclose() {
	$(".log_modal").hide();
}

function fn_logdetail(seq,day) {
	$(".log_modal").show();
	
	var comAjax = new ComAjax();
	comAjax.setUrl("/set_r_rec_logdetail");
	comAjax.setCallback("fn_logdetailload");
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("seq", seq);
	comAjax.addParam("day", day);
	comAjax.ajax();
}
function fn_logdetailload(data) {
	$("#log_contents2").html("");
	if (data.logmap != null) {
		var log = data.logmap;
		$("#log_contents2").html(log);
	}
}

function cmaTextareaSize(obj, bsize) { // 객체명, 기본사이즈
    var sTextarea = document.getElementById(obj);
    var csize = (sTextarea.scrollHeight >= bsize) ? sTextarea.scrollHeight+"px" : bsize+"px";
    sTextarea.style.height = bsize+"px"; 
    sTextarea.style.height = csize;
}

</script>
<!-- 내용 -->
<div class="content">
	
	<!-- 달력 -->
	<div class="calendar_title">
		<ul>
			<li><a class="first" href="#void" id='btnPrev12'></a></li>
			<li><a class="prev" href="#void" id='btnPrev'></a></li>
			<li class="yyyymm" id="currentDate">2016.08</li>
			<li><a class="next" href="#void" id='btnNext'></a></li>
			<li><a class="last" href="#void" id='btnNext12'></a></li>
			<li><a class="today" href="#void" id='btnTodayR'></a></li>
		</ul>
	</div>
	<div id="calendar"></div>
	
	<!-- 로그 -->
	<div class="box">
		<div class="box_title">복구 이력<span class="subtext" id="iDay">2016-08-01(Today)</span></div>
		<table class="table1 trLink">
			<colgroup>
				<col style="width:24%;"> 
				<col style="width:14%;"> 
				<col style="width:9%;"> 
				<col style="width:14%;"> 
				<col style="width:9%;"> 
				<col style="width:10%;"> 
				<col style="width:9%;"> 
				<col style="width:11%;">
			</colgroup>
			<thead>
				<th>복구시점</th>
				<th>복구타입</th>
				<th>복구레벨</th>
				<th>복구방법</th>
				<th>보관기간</th>
				<th>경과시간</th>
				<th>결과</th>
				<th>호스트</th>
			</thead>
			<tbody id="tbody">
			</tbody>
		</table>
		<div class="log_modal">
		    <div class="log_new_contents">
				<div class="log_modal_inside">
					<div>
							<h5 class="close_modal_title">상세 로그</h5>
					</div>
					<div class="close_log_Btn">
							
						<img class="log_x_btn" src="/webdoc/images/btn/delete_icon.png" onclick='taclose();'/>
					</div>
					
				    <textarea class="log_textArea" rows="19" cols="10" id="log_contents2"></textarea>
			    </div>
			</div>
		</div>
	</div><!-- //로그 -->
	
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
