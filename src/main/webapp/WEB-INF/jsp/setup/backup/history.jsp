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
	comAjax.setUrl("/set_b_rec_log");
	comAjax.setCallback("fn_logload");
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("day", sDay);
	comAjax.ajax();
}
function fn_logload(data) {
	var list = data.list;
	var total_full = 0;
	var succ_full = 0;
	var fail_full = 0;
	var tot_incr = 0;
	var succ_incr = 0;
	var fail_incr = 0;
	
	if (list != null && list.length > 0) {
		total_full = list[0].total_full;
		succ_full = list[0].succ_full;
		fail_full = list[0].fail_full != 0 ? "<font color='red'>"+list[0].fail_full+"</font>" : list[0].fail_full;
		tot_incr = list[0].tot_incr;
		succ_incr = list[0].succ_incr;
		fail_incr = list[0].fail_incr != 0 ? "<font color='red'>"+list[0].fail_incr+"</font>" : list[0].fail_incr;
	}
	
	$("#log_contents").html;
	
	$("#full_total").html(" [ "+total_full+" ]");
	$("#full_success").html(" [ "+succ_full+" ]");
	$("#full_fail").html(" [ "+fail_full+" ]");
	$("#incr_total").html(" [ "+tot_incr+" ]");
	$("#incr_success").html(" [ "+succ_incr+" ]");
	$("#incr_fail").html(" [ "+fail_incr+" ]");
	
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
			tmp += "</tr>";
		}
	}else{
		tmp = "<tr><td colspan='7' class='nodata'>조회된 결과가 없습니다.</td></tr>";
	}
	$('#tbody').append(tmp);
    var str = "";
	str += "풀 백업 :  Total [ "+total_full+ " ]  성공[ "+succ_full+"] 실패     [ "+fail_full+" ]"+"증분백업 : Total["+tot_incr+ "] 성공[ "+succ_incr+"] 실패 ["+fail_incr+"]";
			
	var sDay = data.sDay;
	$("#iDay").html(sDay);
	
}

function taclose() {
	$(".log_modal").hide();
}

function fn_logdetail(seq,day) {
	$(".log_modal").show();
	
	var comAjax = new ComAjax();
	comAjax.setUrl("/set_b_rec_logdetail");
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
			<li><a class="today" href="#void" id='btnTodayB'></a></li>
		</ul>
	</div>
	<div id="calendar"></div>
	<!-- //달력 -->

	<!-- 로그 -->
	<div class="box">
		<div class="box_title">백업 이력<span class="subtext" id="iDay">2016-08-01(Today)</span>
			<div class="sub_layout">
				<div>
					<span class="sub_result">실패<span id="full_fail"></span></span>
					<span class="sub_result">성공<span id="full_success"></span></span>
					<span class="sub_result">풀 백업 :Total<span id="full_total"></span></span>
				</div>
				<div>
					<span class="sub_result">실패<span id="incr_fail"></span></span>
					<span class="sub_result">성공<span id="incr_success"></span></span>
					<span class="sub_result">증분백업 :Total<span id="incr_total"></span></span>
				</div>
			</div>
			
		</div>
		<!-- 하단:레벨,기간 -->
		<!-- 표 -->
		<table class="table1 trLink">
			<colgroup>
				<col style="width:26%;"> 
				<col style="width:16%;"> 
				<col style="width:10%;"> 
				<col style="width:16%;"> 
				<col style="width:10%;"> 
				<col style="width:12%;"> 
				<col style="width:10%;"> 
			</colgroup>
			<thead>
				<th>백업일시</th>
				<th>백업타입</th>
				<th>백업레벨</th>
				<th>백업방법</th>
				<th>보관기간</th>
				<th>경과시간</th>
				<th>결과</th>
			</thead>
			<tbody id="tbody">
			</tbody>
		</table>
		<!-- //표 -->
		
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
</div>
<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
