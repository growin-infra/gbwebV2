<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<%@ include file="../include/knob.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
	fn_init();
});
function fn_init() {
	
	bakAndRestoreModal("BACKUP","PC 전원을 끄거나 인터넷 창을 닫지마세요.");
	
	var lvl = $("#imd_lvl").val();
	var avt_nam = $("#avt_nam").val();
	if (lvl == "L01" && avt_nam == "M01") {
		$("#v_progress").show();
	}
	var compress="<c:out value='${cpr}'/>";
	if (compress == "Y") {
		$("#v_progress").hide();
	}
	
	fn_brun_exe();
// 	$.when(fn_status(), fn_endChk()).done(function(){
// 	});
	fn_endChk();
	$("#id_log").append("<div class='id_log_ing'>백업 진행중</div>");
	if(ieVersion == true){
		$("#id_log").append("<div id='ieVerLoading' class='loading_bar'></div>");
	}else{
		$("#id_log").append("<div class='loading_bar_ie9'><img src='/webdoc/images/loading/loading.gif'/></div>");
	}
	
}

var bms_dir_sve, bms_ip, bms_port, bms_usr, bms_pwd, backup_dir, backup_pdir, lvl, avt_nam, cpr;
var bts_usr, bts_pwd, ssh_usr, hst_ip, pot_num;
var wrk_dt, bms_nam, bts_nam, kep_pod, bak_typ, rmt_msq_bny_pth;
var ms_usr, ms_pwd, ms_ip, ms_port, ms_bny_pth;
var backup_complete = false;

function fn_brun_exe() {
	
	bms_nam = "<c:out value='${bms_nam}'/>";
	bts_nam = "<c:out value='${bts_nam}'/>";
	
	var comAjax = new ComAjaxRun();
	comAjax.setUrl("/bak_run_exe");
	comAjax.setCallback("fn_brun_exeload");
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("bms_nam", bms_nam);
	comAjax.addParam("bts_nam", bts_nam);
	comAjax.addParam("imd_lvl", $("#imd_lvl").val());
	comAjax.addParam("imd_kep_pod", $("#imd_kep_pod").val());
	comAjax.ajax();
}
function fn_brun_exeload(data) {
	var result = data.run_result;
	if (result) {
		bms_dir_sve = data.bms_dir_sve;
		backup_dir = data.backup_dir;
		backup_pdir = data.backup_pdir;
		lvl = data.lvl;
		avt_nam = data.avt_nam;
		wrk_dt = data.wrk_dt;
		bms_nam = data.bms_nam;
		bms_usr = data.bms_usr;
		bts_nam = data.bts_nam;
		bms_pwd = data.bms_pwd;
		kep_pod = data.kep_pod;
		ssh_usr = data.ssh_usr;
		hst_ip = data.hst_ip;
		bts_usr = data.bts_usr;
		bts_pwd = data.bts_pwd;
		pot_num = data.pot_num;
		bak_typ = data.bak_typ;
		rmt_msq_bny_pth = data.rmt_msq_bny_pth;
		
		//2017.09.17 add
		ms_usr = data.ms_usr;
		ms_pwd = data.ms_pwd;
		ms_ip = data.ms_ip;
		ms_port = data.ms_port;
		cpr = data.cpr;
		ms_bny_pth = data.ms_bny_pth;
	} else {
		$(".modal").remove();
		bakRunModal("ERROR","백업실행 중 오류발생하였습니다.", function(){
			var comSubmit = new ComSubmit();
			comSubmit.setUrl("/set_b_run");
			comSubmit.addParam("menu_cd", "A01020206");
			comSubmit.addParam("bms_id", $("#bms_id").val());
			comSubmit.addParam("bts_id", $("#bts_id").val());
			comSubmit.submit();
		});
	}
	
}

function fn_endChk() {
	var comAjax = new ComAjaxRun();
	comAjax.setUrl("/bak_run_endChk");
	comAjax.setCallback("fn_endChkload");
	comAjax.addParam("backup_dir", backup_dir);
	comAjax.addParam("lvl", lvl);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bms_pwd", bms_pwd);
	comAjax.addParam("avt_nam", avt_nam);
	comAjax.addParam("wrk_dt", wrk_dt);
	comAjax.addParam("bms_nam", bms_nam);
	comAjax.addParam("bts_nam", bts_nam);
	comAjax.addParam("kep_pod", kep_pod);
	comAjax.addParam("bak_typ", bak_typ);
	
	//2017.09.17 add
	comAjax.addParam("ms_usr", ms_usr);
	comAjax.addParam("ms_pwd", ms_pwd);
	comAjax.addParam("ms_ip", ms_ip);
	comAjax.addParam("ms_port", ms_port);
	
	comAjax.ajax();
}
function fn_endChkload(data) {
	var complete = data.complete;
	if (complete == "ok") {
		backup_complete = true;
		var complete_msg = data.complete_msg;
		if (complete_msg != null 
				&& complete_msg != "" 
				&& new String(complete_msg).valueOf() != "undefined") {
			
			openAlertModal("INFORMATION",complete_msg);
		}
	}
	if (!backup_complete) {
// 		setTimeout(function() { $.when(fn_endChk()).done(function(){}); }, 6000);
		var endChkTimeout = setTimeout(function() { fn_endChk(); fn_status(); }, 5000);
	} else {
		closeModal2();
		clearTimeout(endChkTimeout);
		fn_log_status();
	}
}

function fn_status() {
	var comAjax = new ComAjaxAsyncRun();
	comAjax.setUrl("/bak_run_status");
	comAjax.setCallback("fn_statusload");
	comAjax.addParam("backup_dir", backup_dir);
	comAjax.addParam("backup_pdir", backup_pdir);
	comAjax.addParam("lvl", lvl);
	comAjax.addParam("bms_id", bms_id);
	//comAjax.addParam("bms_usr", $("bms_usr").val());
	comAjax.addParam("bms_usr", bms_usr);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("bms_pwd", bms_pwd);
	comAjax.addParam("avt_nam", avt_nam);
	comAjax.addParam("wrk_dt", wrk_dt);
	comAjax.addParam("bts_nam", bts_nam);
	comAjax.addParam("kep_pod", kep_pod);
	comAjax.addParam("ssh_usr", ssh_usr);
	comAjax.addParam("hst_ip", hst_ip);
	comAjax.addParam("bts_usr", bts_usr);
	comAjax.addParam("bts_pwd", bts_pwd);
	comAjax.addParam("pot_num", pot_num);
	comAjax.addParam("rmt_msq_bny_pth", rmt_msq_bny_pth);
	
	//2017.09.17 add
	comAjax.addParam("ms_usr", ms_usr);
	comAjax.addParam("ms_pwd", ms_pwd);
	comAjax.addParam("ms_ip", ms_ip);
	comAjax.addParam("ms_port", ms_port);
	comAjax.addParam("ms_bny_pth", ms_bny_pth);
	
	comAjax.ajax();
}
function fn_statusload(data) {
	var processVal = data.processVal;
	if (processVal != null) {
		$("#id_progress").val(processVal).trigger("change");
	}
	var cpuUseVal = data.cpuUseVal;
	if (cpuUseVal != null) {
		$("#id_cpu").val(cpuUseVal).trigger("change");
	}
	var memUseVal = data.memUseVal;
	if (memUseVal != null) {
		$("#id_memory").val(memUseVal).trigger("change");
	}
	var diskUseVal = data.diskUseVal;
	if (diskUseVal != null) {
		$("#id_disk").val(diskUseVal).trigger("change");
	}
	
	var swapVal = data.swapVal;
	if (swapVal != null) {
		$("#id_swap").val(swapVal).trigger("change");
	}
}


function fn_log_status() {
	
	var comAjax = new ComAjax();
	comAjax.setUrl("/bak_run_log");
	comAjax.setCallback("fn_log_statusload");
	comAjax.addParam("knd", "G01");
	comAjax.addParam("wrk_dt", wrk_dt);
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.ajax();
}
function fn_log_statusload(data) {
	var log = data.logmap;
	var obj = $("#id_log");
	var tmp = "";
	if (log != null) {
		$(obj).html("");
		tmp = "<div class='box_content'>";
		tmp += log.replace(/\n/gi, "<br>");
		tmp += "</div>";
		$(obj).append(tmp);
	}
}


function fn_popclose() {
	layerClose2();
}

</script>
<form id='commonFormAsync' name='commonFormAsync'></form>
<!-- 내용 -->
<div class="content">
<input type="hidden" id="imd_lvl" value="${imd_lvl }" />
<input type="hidden" id="imd_kep_pod" value="${imd_kep_pod }" />
<input type="hidden" id="avt_nam" value="${avt_nam }" />
<input type="hidden" id="cpr" value="${cpr }" />
<input type="hidden" id="bms_usr" value="${bms_usr }" />
	<!-- untitle -->
	<div class="box">
		<div class="untitle"></div>
		<ul class="box_content2">
			<li id="v_progress" style="display: none;">
				<div class="demo">
					<p>백업 진행률</p>
					<input class="knob" data-width="150" id="id_progress" value="0">
				</div>
			</li>
			<li>
				<div class="demo">
					<p>CPU 사용률</p>
					<input class="knob" data-width="150" data-fgColor="#85D785" id="id_cpu" value="0">
				</div>
			</li>
			<li>
				<div class="demo">
					<p>메모리 사용률</p>
					<input class="knob" data-width="150" data-fgColor="#F04848" id="id_memory" value="0">
				</div>
			</li>
			<li>
				<div class="demo">
					<p>SWAP 사용률</p>
					<input class="knob" data-width="150" id="id_swap" value="0">
				</div>
			</li>
			<li>
				<div class="demo">
					<p>디스크 사용률</p>
					<input class="knob" data-width="150" data-fgColor="#0222C4" id="id_disk" value="0">
				</div>
			</li>
		</ul>
	</div>
	
	<!-- 로그 -->
	<div class="box">
		<div class="box_title">로그</div>
		<div id="id_log">
		</div>
	</div><!-- //로그 -->
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>