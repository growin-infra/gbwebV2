<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function(){
	
	$("#mtd_save").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_fb_save();
	});
	$("#id_avt_nam").change(function(){
		var idx = $("#id_avt_nam option:selected").index();
		if (idx == 0) {
			$("#xtra_b").show();
			$("#mysql_d").hide();
		 	$("#xtra_b").removeAttr('disabled');
		 	$("#mysql_d").attr('disabled','disabled');
		} else {
			$("#xtra_b").hide();
			$("#mysql_d").show();
		 	$("#xtra_b").attr('disabled','disabled');
		 	$("#mysql_d").removeAttr('disabled');
		}
		
	});
	
	fn_init();
	
});
function fn_init() {
	var avt_nam = "<c:out value='${fbmmap.avt_nam}'/>";
// 	if (avt_nam.toLowerCase().indexOf("xtrabackup") != -1) {
	if (avt_nam == "M02") {
		$("#id_avt_nam option:eq(1)").attr("selected","selected");
		$("#xtra_b").hide();
		$("#mysql_d").show();
	 	$("#xtra_b").attr('disabled','disabled');
	 	$("#mysql_d").removeAttr('disabled');
	} else {
		$("#id_avt_nam option:eq(2)").attr("selected","selected");
		$("#xtra_b").show();
		$("#mysql_d").hide();
	 	$("#xtra_b").removeAttr('disabled');
	 	$("#mysql_d").attr('disabled','disabled');
	}
	
	var insert_result="<c:out value='${insert_result}'/>";
	if (insert_result) {
		openAlertModal("SUCCESS","백업방법이 저장되었습니다.");
	}
	
// 	$("#xtra_b").attr('disabled','disabled');
// 	$("#mysql_d").removeAttr('disabled');
// 	$("#xtra_b").removeAttr('disabled');
// 	$("#mysql_d").attr('disabled','disabled');
	
	//id_cpr : Compress
	//id_lck_opt : Locking Options
	//id_cha_set : Character Set
}


function fn_fb_save() {
	
	var comSubmit = new ComSubmit("frm");
    comSubmit.setUrl("/insertBakMtd");
    comSubmit.addParam("menu_cd", menu_cd);
    comSubmit.addParam("menu_id", menu_id);
    comSubmit.addParam("bms_id", bms_id);
    comSubmit.addParam("bts_id", bts_id);
    
	if($("#xtra_b").css("display") != "none") { 
	    comSubmit.addParam("avt_nam", "M01");
		var cpr = $("#id_cpr option:selected").val();
	    comSubmit.addParam("cpr", cpr);
		var use_mmy = $("#id_use_mmy option:selected").val();
	    comSubmit.addParam("use_mmy", use_mmy);
		var pel = $("#id_pel option:selected").val();
	    comSubmit.addParam("pel", pel);
		var trt = $("#id_trt option:selected").val();
	    comSubmit.addParam("trt", trt);
	} else {
	    comSubmit.addParam("avt_nam", "M02");
		var lck_opt = $("#id_lck_opt option:selected").val();
	    comSubmit.addParam("lck_opt", lck_opt);
		var cha_set = $("#id_cha_set option:selected").val();
	    comSubmit.addParam("cha_set", cha_set);
		var fsh_log = $("#id_fsh_log option:selected").val();
	    comSubmit.addParam("fsh_log", fsh_log);
	}
	
    comSubmit.submit();
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
<form id="frm" name="frm">
	<!-- 상단:백업방법선택 -->
	<div class="method_title">
		<select class="w20" id="id_avt_nam">
		<c:choose>
			<c:when test="${fn:length(mList) > 0}">
				<c:forEach var="row" items="${mList}" varStatus="status">
			<option value="${row.com_cod }" <c:if test="${row.com_cod eq avt_nam}">selected</c:if>>${row.com_cod_nam }</option>
				</c:forEach>
			</c:when>
		</c:choose>
		</select>
	</div>
	
	<div class="box">
		<!-- Source DB -->
		<div class="box_title">Source 인스턴스</div>
		<dl class="box_content" id="xtra_b">
			<dt class="tooltip" title="Source DB MariaBackup 'mariabackup' File Path">Backup Util Path<span class="required" >*</span></dt>
			<dd><input type="text" value="${fbmmap.bny_pth }" id="bny_pth" name="bny_pth" style="ime-mode:disabled;" maxlength="100"></dd>
			<dt>Use Memory</dt>
			<dd>
				<select id="id_use_mmy">
				<c:choose>
					<c:when test="${fn:length(umlist) > 0}">
						<c:forEach var="row" items="${umlist}" varStatus="status">
					<option value="${row.com_cod }" <c:if test="${row.com_cod eq fbmmap.use_mmy}">selected</c:if>>${row.com_cod_nam }</option>
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
					<option value="${row.com_cod }" <c:if test="${row.com_cod eq fbmmap.pel}">selected</c:if>>${row.com_cod_nam }</option>
						</c:forEach>
					</c:when>
				</c:choose>				
				</select>
<%-- 				<input type="number" min="1" value="${fbmmap.pel }" id="pel" name="pel"> --%>
			</dd>
			<dt>Throttle</dt>
			<dd>
				<select id="id_trt">
				<c:choose>
					<c:when test="${fn:length(trlist) > 0}">
						<c:forEach var="row" items="${trlist}" varStatus="status">
					<option value="${row.com_cod }" <c:if test="${row.com_cod eq fbmmap.trt}">selected</c:if>>${row.com_cod_nam }</option>
						</c:forEach>
					</c:when>
				</c:choose>				
				</select>
<%-- 				<input type="number" min="0" value="${fbmmap.trt }" id="trt" name="trt"> --%>
			</dd>
			<dt>Compress</dt>
			<dd>
				<select id="id_cpr">
					<option value="N" <c:if test="${fbmmap.cpr eq 'N'}">selected</c:if>>N</option>
					<option value="Y" <c:if test="${fbmmap.cpr eq 'Y'}">selected</c:if>>Y</option>
				</select>
			</dd>
			<dt>Options</dt>
			<dd class="question"><input type="text" value="${fbmmap.xtr_opt }" id="xtr_opt" name="xtr_opt">
				<a href="#void" class="question" onclick="newpopup('xtra')">검색</a>
			</dd>
			<dt class="tooltip" title="Source DB 'mysqlbinlog' and 'my_print_default' File Path">MySQL Binary Path<span class="required" >*</span></dt>
			<dd><input type="text" value="${fbmmap.rmt_msq_bny_pth }" id="rmt_msq_bny_pth" name="rmt_msq_bny_pth" style="ime-mode:disabled;" maxlength="100"></dd>
			<dt class="tooltip" title="Source DB Configuration File 'log-bin' Setting Value">Binary Log Path<span class="required" >*</span></dt>
			<dd><input type="text" value="${fbmmap.xtr_bny_log_pth }" id="xtr_bny_log_pth" name="xtr_bny_log_pth" style="ime-mode:disabled;" maxlength="100"></dd>
			<dt class="tooltip" title="Source DB Configuration File Path">Default File<span class="required" >*</span></dt>
			<dd><input type="text" value="${fbmmap.dft_fil }" id="dft_fil" name="dft_fil" style="ime-mode:disabled;" maxlength="100"></dd>
			<c:if test="${bts_mlt_ins eq 'Y'}">
			<dt class="tooltip" title="Defaults Group Number">Defaults Group<span class="required" >*</span></dt>
			<dd><input type="text" value="${fbmmap.dft_grp }" id="dft_grp" name="dft_grp" style="ime-mode:disabled;" maxlength="100"></dd>
			</c:if>
		</dl>
		
		<dl class="box_content" id="mysql_d">
			<dt>Locking Options</dt>
			<dd>
				<select id="id_lck_opt">
					<option value="single" <c:if test="${fbmmap.lck_opt eq 'single'}">selected</c:if>>single</option>
					<option value="lock-all-tables" <c:if test="${fbmmap.lck_opt eq 'lock-all-tables'}">selected</c:if>>lock-all-tables</option>
				</select>
			</dd>
			<dt>Character Set</dt>
			<dd>
				<select id="id_cha_set">
				<c:choose>
					<c:when test="${fn:length(csList) > 0}">
						<c:forEach var="row" items="${csList}" varStatus="status">
					<option value="${row.com_cod }" <c:if test="${row.com_cod eq fbmmap.cha_set}">selected</c:if>>${row.com_cod_nam }</option>
						</c:forEach>
					</c:when>
				</c:choose>				
				</select>
			</dd>
			<dt>Flush Logs</dt>
			<dd>
				<select id="id_fsh_log">
					<option value="Y" <c:if test="${fbmmap.fsh_log eq 'Y'}">selected</c:if>>Y</option>
					<option value="N" <c:if test="${fbmmap.fsh_log eq 'N'}">selected</c:if>>N</option>
				</select>
			</dd>
			<dt>Options</dt>
			<dd class="question"><input type="text" value="${fbmmap.msq_opt }" id="msq_opt" name="msq_opt">
				<a href="#void" class="question" onclick="newpopup('mysql')">검색</a>
			</dd>
			<dt class="tooltip" title="Source DB 'mysqldump' File Path">MySQL Binary Path<span class="required" >*</span></dt>
			<dd><input type="text" value="${fbmmap.rmt_msq_bny_pth }" id="rmt_msq_bny_pth" name="rmt_msq_bny_pth" style="ime-mode:disabled;" maxlength="100"></dd>
			<dt class="tooltip" title="Source DB Configuration File 'log-bin' Setting Value">Binary Log Path<span class="required" >*</span></dt>
			<dd><input type="text" value="${fbmmap.msq_bny_log_pth }" id="msq_bny_log_pth" name="msq_bny_log_pth" style="ime-mode:disabled;" maxlength="100"></dd>
		</dl>
	</div>
	
	<!-- 하단:버튼 -->
	<div class="bottom_btn">
		<a href="#void" class="save" id="mtd_save">저장</a>
	</div>
</form>
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
