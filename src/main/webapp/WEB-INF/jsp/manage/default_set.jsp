<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
	
	$('#id_bak_set').on('click',function() {
		$("#bak_set").show();
		$("#bak_mtd").hide();
		$("#dft_bak_set_title").html("백업설정");
		fn_dbs();
	});
	$('#id_bak_mtd').on('click',function() {
		$("#bak_set").hide();
		$("#bak_mtd").show();
		$("#dft_bak_set_title").html("백업방법");
		fn_dbm();
	});
	
	$("#bak_mtd_sel").change(function() {
		var idx = $("#bak_mtd_sel option:selected").index();
		if (idx == 0) {
			$("#xtra_sh").show();
			$("#mysql_sh").hide();
		 	$("#xtra_sh").removeAttr('disabled');
		 	$("#mysql_sh").attr('disabled','disabled');
		} else {
			$("#xtra_sh").hide();
			$("#mysql_sh").show();
		 	$("#xtra_sh").attr('disabled','disabled');
		 	$("#mysql_sh").removeAttr('disabled');
		}
		
	});
	
	$("#dbs_save").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_dbs_save();
	});
	
	$("#dbs_delete").on("click", function(e){
		e.preventDefault();
		fn_dbs_delete();
	});
	
	$("#dbm_save").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_dbm_save();
	});
	
	$("#dbm_delete").on("click", function(e){
		e.preventDefault();
		fn_dbm_delete();
	});
	
	fn_init();
});

function fn_init() {
	$("#bak_set").show();
	$("#bak_mtd").hide();
	$("#dft_bak_set_title").html("백업설정");
	$("#bak_mtd_sel option:eq(0)").attr("selected","selected");
	$("#xtra_sh").show();
	$("#mysql_sh").hide();
 	$("#xtra_sh").removeAttr('disabled');
 	$("#mysql_sh").attr('disabled','disabled');
	//fn_setload();
	
	fn_dbs();
}

function fn_dbs() {
	var comAjax = new ComAjax("");
	comAjax.setUrl("/findDftBakSet");
	comAjax.setCallback("fn_dbsload");
	comAjax.addParam("menu_cd", menu_cd);
	comAjax.addParam("menu_id", menu_id);
	comAjax.ajax();
}
function fn_dbsload(data) {
	var map = data.dbsmap;
	if (map != null) {
		$("#msq_clt_utl_pth").val(map.msq_clt_utl_pth);
		$("#mng_xtr_bny_log_pth").val(map.mng_xtr_bny_log_pth);
		$("#source_db").val(map.sou_db);
// 		$("#ssh_usr").val(map.ssh_usr);
		$("#tmp_dir").val(map.tmp_dir);
		$("#id_bak_typ").val(map.bak_typ);
	}
}

function fn_dbm() {
	var comAjax = new ComAjax("dbm_frm");
	comAjax.setUrl("/findDftBakMtd");
	comAjax.setCallback("fn_dbmload");
	comAjax.addParam("menu_cd", menu_cd);
	comAjax.addParam("menu_id", menu_id);
	comAjax.ajax();
}
function fn_dbmload(data) {
	var map = data.dbmmap;
	if (map != null) {
		var avt_nam = map.avt_nam;
		if (avt_nam == "M02") {
			$("#bak_mtd_sel option:eq(1)").attr("selected","selected");
			$("#xtra_sh").hide();
			$("#mysql_sh").show();
		 	$("#xtra_sh").attr('disabled','disabled');
		 	$("#mysql_sh").removeAttr('disabled');
		 	
		 	$("#id_lck_opt").val(map.lck_opt);
		 	$("#id_cha_set").val(map.cha_set);
		 	$("#id_fsh_log").val(map.fsh_log);
		 	$("#msq_opt").val(map.msq_opt);
		 	$("#msq_bny_log_pth").val(map.msq_bny_log_pth);
		 	$("#mysql_rmt_msq_bny_pth").val(map.rmt_msq_bny_pth);
		 	
		 	$("#bny_pth").val("");
			$("#xtr_opt").val("");
			$("#xtra_rmt_msq_bny_pth").val("");
			$("#xtr_bny_log_pth").val("");
			$("#dft_fil").val("");
			$("#id_cpr option:eq(0)").attr("selected", "selected");
			$("#id_use_mmy option:eq(0)").attr("selected", "selected");
			$("#id_pel option:eq(0)").attr("selected", "selected");
			$("#id_trt option:eq(0)").attr("selected", "selected");
			
		} else if (avt_nam == "M01") {
			$("#bak_mtd_sel option:eq(0)").attr("selected","selected");
			$("#xtra_sh").show();
			$("#mysql_sh").hide();
		 	$("#xtra_sh").removeAttr('disabled');
		 	$("#mysql_sh").attr('disabled','disabled');
		 	
			$("#bny_pth").val(map.bny_pth);
			$("#id_use_mmy").val(map.use_mmy);
			$("#id_pel").val(map.pel);
			$("#id_trt").val(map.trt);
			$("#id_cpr").val(map.cpr);
			$("#xtr_opt").val(map.xtr_opt);
			$("#xtra_rmt_msq_bny_pth").val(map.rmt_msq_bny_pth);
			$("#xtr_bny_log_pth").val(map.xtr_bny_log_pth);
			$("#dft_fil").val(map.dft_fil);
			
		 	$("#msq_opt").val("");
		 	$("#msq_bny_log_pth").val("");
		 	$("#mysql_rmt_msq_bny_pth").val("");
			$("#id_lck_opt option:eq(0)").attr("selected", "selected");
			$("#id_cha_set option:eq(0)").attr("selected", "selected");
			$("#id_fsh_log option:eq(0)").attr("selected", "selected");
			
		} else {
			
			$("#bak_mtd_sel option:eq(0)").attr("selected","selected");
		}
	}
}

function fn_dbs_save() {
	
	var bak_typ = $("#id_bak_typ option:selected").val();
	var sou_db = $("#source_db option:selected").val();
	
	var comAjax = new ComAjax("dbs_frm");
	comAjax.setUrl("/insertDftBakSet");
	comAjax.setCallback("fn_insertDftBakSet");
	comAjax.addParam("bak_typ", bak_typ);
	comAjax.addParam("sou_db", sou_db);
	comAjax.ajax();
}
function fn_insertDftBakSet(data) {
	var result = data.result;
	if (result) {
		openAlertModal("SUCCESS","백업설정이 저장되었습니다.");
	} else {
		openAlertModal("FAIL","백업설정 저장이 실패 되었습니다.");
	}
}

function fn_dbs_delete() {
	openConfirmModal("CONFIRM", "삭제하시겠습니까?", function(){
		loadingDiv.on();
		var comAjax = new ComAjax();
		comAjax.setUrl("/deleteDftBakSet");
		comAjax.setCallback("fn_deleteDftBakSet");
		comAjax.addParam("menu_cd", menu_cd);
		comAjax.addParam("menu_id", menu_id);
		comAjax.ajax();
	});
}
function fn_deleteDftBakSet(data) {
	loadingDiv.off();

	var result = data.result;
	if (result) {
		openAlertModal("SUCCESS","백업설정이 삭제되었습니다.");
		$("#msq_clt_utl_pth").val("");
		$("#mng_xtr_bny_log_pth").val("");
		$("#source_db option:eq(0)").attr("selected", "selected");
// 		$("#ssh_usr").val("");
		$("#tmp_dir").val("");
		$("#id_bak_typ option:eq(0)").attr("selected", "selected");
		
	} else {
		openAlertModal("FAIL","백업설정 삭제가 실패 되었습니다.");
	}
}

function fn_dbm_save() {
	
	var comAjax = new ComAjax("dbm_frm");
	comAjax.setUrl("/insertDftBakMtd");
	comAjax.setCallback("fn_insertDftBakMtd");
	comAjax.addParam("menu_cd", menu_cd);
	comAjax.addParam("menu_id", menu_id);
    
	if($("#xtra_sh").css("display") != "none") { 
		$("#xtra_sh").removeAttr('disabled');
	 	$("#mysql_sh").attr('disabled','disabled');
		comAjax.addParam("avt_nam", "M01");
		var cpr = $("#id_cpr option:selected").val();
		comAjax.addParam("cpr", cpr);
		var use_mmy = $("#id_use_mmy option:selected").val();
		comAjax.addParam("use_mmy", use_mmy);
		var pel = $("#id_pel option:selected").val();
		comAjax.addParam("pel", pel);
		var trt = $("#id_trt option:selected").val();
		comAjax.addParam("trt", trt);
		var rmt_msq_bny_pth = $("#xtra_rmt_msq_bny_pth").val();
		comAjax.addParam("rmt_msq_bny_pth", rmt_msq_bny_pth);
	} else {
	 	$("#xtra_sh").attr('disabled','disabled');
		$("#mysql_sh").removeAttr('disabled');
		comAjax.addParam("avt_nam", "M02");
		var lck_opt = $("#id_lck_opt option:selected").val();
		comAjax.addParam("lck_opt", lck_opt);
		var cha_set = $("#id_cha_set option:selected").val();
		comAjax.addParam("cha_set", cha_set);
		var fsh_log = $("#id_fsh_log option:selected").val();
		comAjax.addParam("fsh_log", fsh_log);
		var rmt_msq_bny_pth = $("#mysql_rmt_msq_bny_pth").val();
		comAjax.addParam("rmt_msq_bny_pth", rmt_msq_bny_pth);
	}
	
	comAjax.ajax();
}
function fn_insertDftBakMtd(data) {
	var result = data.result;
	if (result) {
		openAlertModal("SUCCESS","백업방법이 저장되었습니다.");
	} else {
		openAlertModal("FAIL","백업방법 저장이 실패 되었습니다.");
	}
}

function fn_dbm_delete() {
	openConfirmModal("CONFIRM", "삭제하시겠습니까?", function(){
		loadingDiv.on();
		var comAjax = new ComAjax();
		comAjax.setUrl("/deleteDftBakMtd");
		comAjax.setCallback("fn_deleteDftBakMtd");
		comAjax.addParam("menu_cd", menu_cd);
		comAjax.addParam("menu_id", menu_id);
		comAjax.ajax();
	});
}
function fn_deleteDftBakMtd(data) {
	loadingDiv.off();

	var result = data.result;
	if (result) {
		openAlertModal("SUCCESS","백업방법이 삭제되었습니다.");
		
		if($("#xtra_sh").css("display") != "none") { 
			$("#id_cpr option:eq(0)").attr("selected", "selected");
			$("#id_use_mmy option:eq(0)").attr("selected", "selected");
			$("#id_pel option:eq(0)").attr("selected", "selected");
			$("#id_trt option:eq(0)").attr("selected", "selected");
			$("#bny_pth").val("");
			$("#xtr_opt").val("");
			$("#xtra_rmt_msq_bny_pth").val("");
			$("#xtr_bny_log_pth").val("");
			$("#dft_fil").val("");
			
		} else {
			$("#id_lck_opt option:eq(0)").attr("selected", "selected");
			$("#id_cha_set option:eq(0)").attr("selected", "selected");
			$("#id_fsh_log option:eq(0)").attr("selected", "selected");
			$("#msq_opt").val("");
			$("#msq_bny_log_pth").val("");
			$("#mysql_rmt_msq_bny_pth").val("");
		}
		
	} else {
		openAlertModal("FAIL","백업방법 삭제가 실패 되었습니다.");
	}
}

function fn_popclose() {
	layerClose2();
}
function setChildValue(name , type){
	if(type == "xtra"){
		$('#xtr_opt').val($('#xtr_opt').val() + " " + name);
	}else{
		$('#msq_opt').val($('#msq_opt').val() + " " + name);
	}
}

</script>
<!-- 내용 -->
<div class="content">
	<!-- 표 -->
	<table class="table1 top set">
		<colgroup>
			<col style="width:30%;"> <!-- Backup How Settings -->
			<col style="width:70%;"> <!-- Backup What -->
		</colgroup>
		<thead>
			<th>
				<div class="thpad">
					<div class="floatL">디폴트 백업 설정</div>
				</div>
			</th>
			<th class="change_text" id="dft_bak_set_title">Backup What</th>
		</thead>
		<tbody>
			<tr>
				<!-- (왼)Backup How Settings : 리스트 추가 -->
				<td>
					<ul class="left_part">
						<li><a href="#void" id="id_bak_set">백업설정</a></li>
						<li><a href="#void" id="id_bak_mtd">백업방법</a></li>
					</ul>
				</td>

				<!-- (오)Backup What-->
				<td>
					<div class="server_list" id="bak_set">
					<form id="dbs_frm" name="dbs_frm">
						<!-- MySQL Server Parameters -->
						<div class="right_part">
							<div>Manager Server</div>
							<dl>
								<dt class="tooltip" title="Manager Server DB 'mysql' File Path">DB Client Utilities Path</dt>
								<dd><input type="text" id="msq_clt_utl_pth" name="msq_clt_utl_pth" value="" maxlength="100"></dd>
								<dt class="tooltip" title="Manager Server MariaBackup 'mariabackup' File Path">DB Backup Util Path</dt>
								<dd><input type="text" id="mng_xtr_bny_log_pth" name="mng_xtr_bny_log_pth" value="" maxlength="100"></dd>
							</dl>
						</div>
						<div class="right_part">
							<div>Source DB</div>
							<dl>
								<dt>DB 종류</dt>
								<dd>
									<select id="source_db">
										<c:choose>
											<c:when test="${fn:length(dblist) > 0}">
												<c:forEach var="row" items="${dblist}" varStatus="status">
													<option value="${row.com_cod }" <c:if test="${row.com_cod eq bsmap.sou_db}">selected</c:if>>${row.com_cod_nam }</option>
												</c:forEach>
											</c:when>
										</c:choose>
									</select>
								</dd>
<!-- 								<dt>SSH 아이디</dt> -->
<!-- 								<dd><input type="text" id="ssh_usr" name="ssh_usr" maxlength="30"></dd> -->
								<dt class="tooltip" title="Temporary Directory to use for Backup Operations on the Source Server">임시디렉토리</dt>
								<dd><input type="text" id="tmp_dir" name="tmp_dir" maxlength="100"></dd>
								<dt class="tooltip" title="Select all-databases, Database(s), Tabale(s)">Backup Source Type</dt>
								<dd>
									<select id="id_bak_typ">
										<c:choose>
											<c:when test="${fn:length(tylist) > 0}">
												<c:forEach var="row" items="${tylist}" varStatus="status">
												<option value="${row.com_cod }" <c:if test="${row.com_cod eq dbsmap.bak_typ}">selected</c:if>>${row.com_cod_nam }</option>
												</c:forEach>
											</c:when>
										</c:choose>
									</select>
								</dd>
							</dl>
						</div> <!-- //MySQL Server Parameters -->
						
						<!-- 하단:버튼 -->
						<div class="bottom_btn">
							<a href="#void" class="save btn_s" id="dbs_save">저장</a>
							<a href="#void" class="delete btn_s" id="dbs_delete">삭제</a>
						</div> <!-- //하단:버튼 -->
					</form>
					</div>
					
					<div class="server_list" id="bak_mtd">
					<form id="dbm_frm" name="dbm_frm">
						<div class="right_part">
							<div class="method_title2">
								<select class="w30" id="bak_mtd_sel">
								<c:choose>
									<c:when test="${fn:length(mList) > 0}">
										<c:forEach var="row" items="${mList}" varStatus="status">
									<option value="${row.com_cod }" <c:if test="${row.com_cod eq avt_nam}">selected</c:if>>${row.com_cod_nam }</option>
										</c:forEach>
									</c:when>
								</c:choose>
								</select>
							</div>
						</div>
						<div class="right_part">
							<div>Source DB</div>
							<dl id="xtra_sh">
								<dt>Backup Util Path</dt>
								<dd><input type="text" id="bny_pth" name="bny_pth" style="ime-mode:disabled;" maxlength="100"></dd>
								<dt>Use Memory</dt>
								<dd>
									<select id="id_use_mmy">
									<c:choose>
										<c:when test="${fn:length(umlist) > 0}">
											<c:forEach var="row" items="${umlist}" varStatus="status">
										<option value="${row.com_cod }" <c:if test="${row.com_cod eq dbmmap.use_mmy}">selected</c:if>>${row.com_cod_nam }</option>
											</c:forEach>
										</c:when>
									</c:choose>				
									</select>
								</dd>
								<dt>Parallel</dt>
								<dd>
									<select id="id_pel">
									<c:choose>
										<c:when test="${fn:length(prlist) > 0}">
											<c:forEach var="row" items="${prlist}" varStatus="status">
										<option value="${row.com_cod }" <c:if test="${row.com_cod eq dbmmap.pel}">selected</c:if>>${row.com_cod_nam }</option>
											</c:forEach>
										</c:when>
									</c:choose>				
									</select>
								</dd>
								<dt>Throttle</dt>
								<dd>
									<select id="id_trt">
									<c:choose>
										<c:when test="${fn:length(trlist) > 0}">
											<c:forEach var="row" items="${trlist}" varStatus="status">
											<option value="${row.com_cod }" <c:if test="${row.com_cod eq dbmmap.trt}">selected</c:if>>${row.com_cod_nam }</option>
											</c:forEach>
										</c:when>
									</c:choose>				
									</select>
								</dd>
								<dt>Compress</dt>
								<dd>
									<select id="id_cpr">
										<option value="N" <c:if test="${dbmmap.cpr eq 'N'}">selected</c:if>>N</option>
										<option value="Y" <c:if test="${dbmmap.cpr eq 'Y'}">selected</c:if>>Y</option>
									</select>
								</dd>
								<dt>Options</dt>
								<dd class="question2"><input class="input_w220" type="text" id="xtr_opt" name="xtr_opt">
<!-- 									<a href="#void" class="question2" onclick="newpopup('xtra')">검색</a> -->
									<a href="#void" class="question2" onclick="window.open('/help_xtra','new','scrollbars=yes,resizable=yes,width=500,height=500,top=100,left=100');return false;">검색</a>
								</dd>
								<dt class="tooltip" title="Source DB 'mysqlbinlog' and 'my_print_default' File Path">MySQL Binary Path</dt>
								<dd><input type="text" id="xtra_rmt_msq_bny_pth" name="xtra_rmt_msq_bny_pth" style="ime-mode:disabled;" maxlength="100"></dd>
								<dt class="tooltip" title="Source DB Configuration File 'log-bin' Setting Value">Binary Log Path</dt>
								<dd><input type="text" id="xtr_bny_log_pth" name="xtr_bny_log_pth" style="ime-mode:disabled;" maxlength="100"></dd>
								<dt class="tooltip" title="Source DB Configuration File Path">Default File</dt>
								<dd><input type="text" id="dft_fil" name="dft_fil" style="ime-mode:disabled;" maxlength="100"></dd>
							</dl>
							<dl id="mysql_sh">
								<dt>Locking Options</dt>
								<dd>
									<select id="id_lck_opt">
										<option value="single" <c:if test="${dbmmap.lck_opt eq 'single'}">selected</c:if>>single</option>
										<option value="lock-all-tables" <c:if test="${dbmmap.lck_opt eq 'lock-all-tables'}">selected</c:if>>lock-all-tables</option>
									</select>
								</dd>
								<dt>Character Set</dt>
								<dd>
									<select id="id_cha_set">
									<c:choose>
										<c:when test="${fn:length(csList) > 0}">
											<c:forEach var="row" items="${csList}" varStatus="status">
											<option value="${row.com_cod }" <c:if test="${row.com_cod eq dbmmap.cha_set}">selected</c:if>>${row.com_cod_nam }</option>
											</c:forEach>
										</c:when>
									</c:choose>				
									</select>
								</dd>
								<dt>Flush Logs</dt>
								<dd>
									<select id="id_fsh_log">
										<option value="N" <c:if test="${dbmmap.fsh_log eq 'N'}">selected</c:if>>N</option>
										<option value="Y" <c:if test="${dbmmap.fsh_log eq 'Y'}">selected</c:if>>Y</option>
									</select>
								</dd>
								<dt>Options</dt>
								<dd class="question2"><input class="input_w220" type="text" id="msq_opt" name="msq_opt">
									<a href="#void" class="question2" onclick="newpopup('mysql')">검색</a>
								</dd>
								<dt class="tooltip" title="Source DB 'mysqldump' File Path">MySQL Binary Path</dt>
								<dd><input type="text" id="mysql_rmt_msq_bny_pth" name="mysql_rmt_msq_bny_pth" style="ime-mode:disabled;" maxlength="100"></dd>
								<dt class="tooltip" title="Source DB Configuration File 'log-bin' Setting Value">Binary Log Path</dt>
								<dd><input type="text" id="msq_bny_log_pth" name="msq_bny_log_pth" style="ime-mode:disabled;" maxlength="100"></dd>
							</dl>
 						</div> <!--//MySQL Server Parameters -->
						
						<!-- 하단:버튼 -->
						<div class="bottom_btn">
							<a href="#void" class="save btn_s" id="dbm_save">저장</a>
							<a href="#void" class="delete btn_s" id="dbm_delete">삭제</a>
 						</div>
 						<!--//하단:버튼 -->
					</form>
					</div>
				</td> <!-- //(오)Backup What -->
				
			</tr>
		</tbody>
	</table><!-- //표 -->
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
