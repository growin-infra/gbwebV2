<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function(){
	
	$("#bak_run").on("click", function(e){
		e.preventDefault();
		if ($("#id_blank_set").css("display") != "none" && $("#id_blank_mtd").css("display") != "none") {
			openAlertModal("ERROR" , "설정정보가 없습니다.");
		} else {
			fn_bak_run();
		}
		
	});
	
	//보관기간
	$("#id_kep_pod").change(function(){
		//var idx = $("#id_kep_pod option:selected").index();
		var val = $("#id_kep_pod option:selected").val();
		kpComboset(val,"");
	});
	
	fn_init();
	
	$("#spin").hide();
});
function fn_init() {
	
	$("#id_blank_set").hide();
	$("#id_bak_set").hide();
	$("#id_bak_set2").hide();
	var bak_set_blank = "<c:out value='${bak_set_blank}'/>";
	if (!bak_set_blank) {
		$("#id_bak_set").show();
		$("#id_bak_set2").show();
	} else {
		$("#id_blank_set").show();
	}
	
	$("#id_xtra").hide();
	$("#id_msq").hide();
	$("#id_blank_mtd").hide();
	
// 	var avt_nam = "<c:out value='${fbmmap.avt_nam}'/>";
	var avt_nam = $("#avt_nam").val();
// 	if (avt_nam.toLowerCase().indexOf("mysql") != -1) {
	if (avt_nam == "M01") {
		$("#id_xtra").show();
// 		$("#id_msq").hide();
// 	} else if (avt_nam.toLowerCase().indexOf("xtra") != -1) {
	} else if (avt_nam == "M02") {
// 		$("#id_xtra").hide();
		$("#id_msq").show();
	} else {
		$("#id_blank_mtd").show();
	}
	kpComboset("","");
	
	
// 	if ($("#id_blank_set").css("display") != "none" && $("#id_blank_mtd").css("display") != "none") {
// 		$("#bak_run").prop("disabled", true);
// 	} else {
// 		$("#bak_run").prop("disabled", false);
// 	}
	
}


function kpComboset(val,itm) {

	if (val == "0") {	//직접입력
// 		$("#id_kep_pod_itm").removeAttr("disabled");
		$("#id_kep_pod_itm").prop("disabled",false);
		$("#id_kep_pod_itm").val(itm);
		$("#id_kep_pod_itm").focus();
	} else if (val == "") {
// 		$("#id_kep_pod_itm").attr("disabled","disabled");
		$("#id_kep_pod").val("");
		$("#id_kep_pod_itm").prop("disabled",true);
		$("#id_kep_pod_itm").val("");
	} else {
		$("#id_kep_pod").val(val);
		$("#id_kep_pod_itm").prop("disabled",true);
		$("#id_kep_pod_itm").val("");
	}
}

function fn_setChk() {
	var comAjax = new ComAjax();
	comAjax.setUrl("/is_set_yn");
	comAjax.setCallback("fn_setChkload");
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.ajax();
}
function fn_setChkload(data) {
	is_set_yn = data.is_set_yn;
}
var is_set_yn = false;
function fn_bak_run() {
	
	fn_setChk();
	
	if (!is_set_yn) {
		openAlertModal("WARNING","백업설정및 백업방법 정보가 없습니다.");
		return;
	}
	
	var lvl = $("#id_lvl");
	if (lvl.val() == "") {
		openAlertModal("WARNING","백업레벨을 선택하세요.");
		return;
	}
	var kp_val = "";
	var kep_pod = "";
	
	var kep_pod = $("#id_kep_pod").val();
	var kep_pod_itm = $("#id_kep_pod_itm").val();
	
	if(kep_pod == "0" && kep_pod_itm == "" || kep_pod_itm =="0"){
		openAlertModal("WARNING","보관기간을 선택하세요.");
		return;
	}
	if(kep_pod == ""){
		openAlertModal("WARNING","보관기간을 선택하세요.");
		return;
	}
	
	if(kep_pod != "" && kep_pod != "0"){
		kp_val = $("#id_kep_pod").val();
	}else if((kep_pod == "0" && kep_pod_itm != "") || kep_pod_itm != 0){
		kp_val = $("#id_kep_pod_itm").val();
	}
	
	openConfirmModal("CONFIRM", "즉시실행 하시겠습니까?", function(){
		var comSubmit = new ComSubmit();
	    comSubmit.setUrl("/set_imd_run");
	    comSubmit.addParam("menu_cd", menu_cd);
	    comSubmit.addParam("menu_id", menu_id);
	    comSubmit.addParam("bms_id", bms_id);
	    comSubmit.addParam("bts_id", bts_id);
	    comSubmit.addParam("bms_nam", $("#bms_nam").val());
	    comSubmit.addParam("bms_usr", $("#bms_usr").val());
	    comSubmit.addParam("bts_nam", $("#bts_nam").val());
	    comSubmit.addParam("imd_lvl", lvl.val());
	    comSubmit.addParam("imd_kep_pod", kp_val);
	    comSubmit.addParam("avt_nam", $("#avt_nam").val());
	    comSubmit.addParam("cpr", $("#cpr").val());
	    comSubmit.submit();
	});
}

</script>
<!-- 내용 -->
<div class="content">
<input type="hidden" id="bms_nam" name="bms_nam" value="${bms_nam }" />
<input type="hidden" id="bms_usr" name="bms_usr" value="${bms_usr }" />
<input type="hidden" id="bts_nam" name="bts_nam" value="${bts_nam }" />
<input type="hidden" id="avt_nam" name="avt_nam" value="${fbmmap.avt_nam }" />
<input type="hidden" id="cpr" name="cpr" value="${fbmmap.cpr }" />

	<!-- 하단:레벨,기간 -->
	<ul class="level_period">
		<li class="title_txt">백업 레벨</li>
		<li class="w20">
			<select id="id_lvl">
				<option value="">선택하세요</option>
			<c:choose>
				<c:when test="${fn:length(bllist) > 0}">
					<c:forEach var="row" items="${bllist}" varStatus="status">
				<option value="${row.com_cod }">${row.com_cod_nam }</option>
					</c:forEach>
				</c:when>
			</c:choose>	
			</select>
		</li>
		<li class="title_txt">보관 기간</li>
		<li class="w13">
			<select id="id_kep_pod">
				<option value="">선택하세요</option>
				<option value="0">직접입력</option>
				<% 
					int iKepPodarr[] = {1,3,7,30,90,365};
					for (int i=0; i<iKepPodarr.length; i++) {
				%>
				<option value="<%=iKepPodarr[i]%>"><%=iKepPodarr[i]%>일</option>
				<% } %>
			</select>
		</li>
		<li class="w10">
			<input type="number" min="1" class="w50" id="id_kep_pod_itm" value="" onkeypress="return fn_press(event, 'numbers');" style="ime-mode:disabled;" maxlength="3"> 일
		</li>
		<li class="w10"></li>
		<li class="w10"></li>
		<!-- 하단:버튼 -->
		<div class="bottom_btn">
			<a href="#void" class="action" id="bak_run">즉시실행</a>
		</div>
	</ul>
	
	<div class="box">
		
		<!-- 백업설정 blank -->
		<div id="id_blank_set">
		<div class="box_title">백업설정 정보</div>
		<dl class="box_content view">
			<dt>백업설정 정보가 없습니다.</dt>
		</dl>
		</div>
		
		<!-- Manager Server -->
		<div id="id_bak_set2">
		<div class="box_title">Manager Server</div>
		<dl class="box_content view">
			<dt>DB Client Utilities Path</dt>
			<dd>${bsmap.msq_clt_utl_pth }</dd>
			<dt>DB Backup Util Path</dt>
			<dd>${bsmap.mng_xtr_bny_log_pth }</dd>
		</dl>
		</div>
	</div>
	
	<div class="box">
		<!-- Source DB -->
		<div id="id_bak_set">
		<div class="box_title">Source 인스턴스</div>
		<dl class="box_content view">
			<dt>DB 종류</dt>
			<dd>${bsmap.sou_db_cdname }</dd>
			<dt>Connection Type</dt>
			<dd>Port</dd>
			<dt>Port Number</dt>
			<dd>${bsmap.pot_num }</dd>
			<dt>Host</dt>
			<dd>${bsmap.hst_ip }</dd>
			<dt>DB 아이디</dt>
			<dd>${bsmap.bts_usr }</dd>
			<dt>임시 디렉토리</dt>
			<dd>${bsmap.tmp_dir }</dd>
			<dt>Backup Source Type</dt>
			<dd>${bsmap.bak_typ_cdname }</dd>
			<c:if test="${bsmap.bak_typ_itm ne ''}"><dt></dt><dd>${bsmap.bak_typ_itm }</dd></c:if>
			<c:if test="${bsmap.bak_typ_tbe ne ''}"><dt></dt><dd>${bsmap.bak_typ_tbe }</dd></c:if>
		</dl>
		</div>
	</div>
	
	<div class="box">
		
		<!-- 백업방법 blank -->
		<div id="id_blank_mtd">
		<div class="box_title">백업방법 정보</div>
		<dl class="box_content view">
			<dt>백업방법 정보가 없습니다.</dt>
		</dl>
		</div>
		
		<!-- XtraBackup 설정 -->
		<div id="id_xtra">
		<div class="box_title">MariaBackup 설정</div>
		<dl class="box_content view">
			<dt>Binary Path</dt>
			<dd>${fbmmap.bny_pth }</dd>
			<dt>Use Memory</dt>
			<dd>${fbmmap.use_mmy_cdname }</dd>
			<dt>Parallel</dt>
			<dd>${fbmmap.pel_cdname }</dd>
			<dt>Throttle</dt>
			<dd>${fbmmap.trt_cdname }</dd>
			<dt>Compress</dt>
			<dd>${fbmmap.cpr }</dd>
			<dt>Options</dt>
			<dd>${fbmmap.xtr_opt }</dd>
			<dt>MySQL Binary Path</dt>
			<dd>${fbmmap.rmt_msq_bny_pth }</dd>
			<dt>Binary Log Path</dt>
			<dd>${fbmmap.xtr_bny_log_pth }</dd>
			<dt>Default File</dt>
			<dd>${fbmmap.dft_fil }</dd>
			<c:if test="${bts_mlt_ins eq 'Y'}">
			<dt>Defaults Group</dt>
			<dd>${fbmmap.dft_grp }</dd>
			</c:if>
		</dl>
		</div>
		
		<!-- MysqlDump 설정 -->
		<div id="id_msq">
		<div class="box_title">MySQLdump 설정</div>
		<dl class="box_content view">
			<dt>Locking Options</dt>
			<dd>${fbmmap.lck_opt }</dd>
			<dt>Character Set</dt>
			<dd>${fbmmap.cha_set_cdname }</dd>
			<dt>Flush Logs</dt>
			<dd>${fbmmap.fsh_log }</dd>
			<dt>Options</dt>
			<dd>${fbmmap.msq_opt }</dd>
			<dt>MySQL Binary Path</dt>
			<dd>${fbmmap.rmt_msq_bny_pth }</dd>
			<dt>Binary Log Path</dt>
			<dd>${fbmmap.msq_bny_log_pth }</dd>
		</dl>
		</div>
	</div><!-- //box -->
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>