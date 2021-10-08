<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<%@ include file="../include/knob.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
	fn_init();
});
function fn_init() {
	
	bakAndRestoreModal("RESTORE","PC 전원을 끄거나 인터넷 창을 닫지마세요.");
	
	var lvl = $("#lvl").val();
	var mtd = $("#mtd").val();
	if (lvl == "L01" && mtd == "M01") {
		$("#v_progress").show();
	}
	var compress="<c:out value='${cpr}'/>";
	if (compress == "Y") {
		$("#v_progress").hide();
	}
	
	fn_rrun_exe();
	//fn_endChk();
	$("#id_log").append("<div class='id_log_ing'>복구 진행중</div>");
	if(ieVersion == true){
		$("#id_log").append("<div id='ieVerLoading' class='loading_bar'></div>");
	}else{
		$("#id_log").append("<div class='loading_bar_ie9'><img src='/webdoc/images/loading/loading.gif'/></div>");
	}
	
}

var bms_dir_sve, bms_ip, bms_pot, bms_usr, bms_pwd, backup_dir, backup_pdir, lvl, mtd, run_id;
var wrk_dt, bts_nam, bms_nam, kep_pod, ssh_usr, hst_ip, bak_typ, cpr;
var restore_logDir, restore_dir, log_file_path, err_log_file_path;
var ms_usr, ms_pwd, ms_ip, ms_port;
var recovery_complete = false;

function fn_rrun_exe() {
	
	var comAjax = new ComAjaxRsrRun();
	comAjax.setUrl("/rsr_run_exe");
	comAjax.setCallback("fn_rrun_exeload");
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("wrk_dt", $("#wrk_dt").val());
	comAjax.addParam("bms_nam", $("#bms_nam").val());
	comAjax.addParam("bts_nam", $("#bts_nam").val());
	comAjax.addParam("lvl", $("#lvl").val());
	comAjax.addParam("mtd", $("#mtd").val());
	comAjax.addParam("typ", $("#typ").val());
	comAjax.addParam("kep_pod", $("#kep_pod").val());
	comAjax.addParam("run_id", $("#run_id").val());
	comAjax.addParam("hst_ip", $("#hst_ip").val());
	comAjax.ajax();
}
function fn_rrun_exeload(data) {
	
	var result = data.run_result;
	bms_dir_sve = data.bms_dir_sve;
	backup_dir = data.backup_dir;
	backup_pdir = data.backup_pdir;
	lvl = data.lvl;
	mtd = data.mtd;
	wrk_dt = data.wrk_dt;
	bms_nam = data.bms_nam;
	bts_nam = data.bts_nam;
	kep_pod = data.kep_pod;
	ssh_usr = data.ssh_usr;
	hst_ip = data.hst_ip;
	typ = data.typ;
	kep_pod = data.kep_pod;
	run_id = data.run_id;
	restore_logDir = data.restore_logDir;
	restore_dir = data.restore_dir;
	log_file_path = data.log_file_path;
	err_log_file_path = data.err_log_file_path;
	cpr = data.cpr;
	
	//2017.09.17 add
	ms_usr = data.ms_usr;
	ms_pwd = data.ms_pwd;
	ms_ip = data.ms_ip;
	ms_port = data.ms_port;
// 	if (!data.connection_fail) {
// 		bakRunModal("ERROR","DB 접속이 실패되었습니다.", function(){
// 			var comSubmit = new ComSubmit();
// 			comSubmit.setUrl("/set_r_run");
// 			comSubmit.addParam("menu_cd", "A01020211");
// 			comSubmit.addParam("bms_id", $("#bms_id").val());
// 			comSubmit.addParam("bts_id", $("#bts_id").val());
// 			comSubmit.submit();
// 		});
// 	}
// 	else {
		fn_endChk();
// 	}
}

function fn_endChk() {
	var comAjax = new ComAjax();
	comAjax.setUrl("/rsr_run_endChk");
	comAjax.setCallback("fn_endChkload");
	comAjax.addParam("backup_dir", backup_dir);
	comAjax.addParam("lvl", lvl);
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("mtd", mtd);
	comAjax.addParam("typ", typ);
	comAjax.addParam("kep_pod", kep_pod);
	comAjax.addParam("run_id", run_id);
	comAjax.addParam("wrk_dt", wrk_dt);
	comAjax.addParam("bms_nam", bms_nam);
	comAjax.addParam("bts_nam", bts_nam);
	comAjax.addParam("hst_ip", hst_ip);
	comAjax.addParam("restore_dir", restore_dir);
	comAjax.addParam("log_file_path", log_file_path);
	comAjax.addParam("err_log_file_path", err_log_file_path);
	
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
		recovery_complete = true;
		var complete_msg = data.complete_msg;
		if (complete_msg != null 
				&& complete_msg != "" 
				&& new String(complete_msg).valueOf() != "undefined") {

			openAlertModal("INFORMATION",complete_msg);
		}
	}
	if (!recovery_complete) {
		var endChkTimeout = setTimeout(function() { fn_endChk(); fn_status(); }, 5000);
	} else {
		closeModal2();
		clearTimeout(endChkTimeout);
		fn_log_status();
	}
}

function fn_status() {
	var comAjax = new ComAjaxAsyncRun();
	comAjax.setUrl("/rsr_run_status");
	comAjax.setCallback("fn_statusload");
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("bms_ip", bms_ip);
	comAjax.addParam("backup_dir", backup_dir);
	comAjax.addParam("backup_pdir", backup_pdir);
	comAjax.addParam("lvl", lvl);
	comAjax.addParam("mtd", mtd);
	comAjax.addParam("wrk_dt", wrk_dt);
	comAjax.addParam("bms_nam", bms_nam);
	comAjax.addParam("bts_nam", bts_nam);
	comAjax.addParam("kep_pod", kep_pod);
	
	//2017.09.17 add
	comAjax.addParam("ms_usr", ms_usr);
	comAjax.addParam("ms_pwd", ms_pwd);
	comAjax.addParam("ms_ip", ms_ip);
	comAjax.addParam("ms_port", ms_port);
	
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
	comAjax.setUrl("/rsr_run_log");
	comAjax.setCallback("fn_log_statusload");
	comAjax.addParam("knd", "G02");
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
<input type="hidden" id="wrk_dt" value="${wrk_dt }" />
<input type="hidden" id="bms_nam" value="${bms_nam }" />
<input type="hidden" id="bts_nam" value="${bts_nam }" />
<input type="hidden" id="lvl" value="${lvl }" />
<input type="hidden" id="mtd" value="${mtd }" />
<input type="hidden" id="typ" value="${typ }" />
<input type="hidden" id="kep_pod" value="${kep_pod }" />
<input type="hidden" id="run_id" value="${run_id }" />
<input type="hidden" id="cpr" value="${cpr }" />
<input type="hidden" id="hst_ip" value="${hst_ip }" />
	<!-- untitle -->
	<div class="box">
		<div class="untitle"></div>
		<ul class="box_content2">
			<li id="v_progress" style="display: none;">
				<div class="demo">
					<p>복구 진행률</p>
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
