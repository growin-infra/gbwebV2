<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function(){
	$("#rsset_save").on("click", function(e){
		e.preventDefault();
		fn_rs_save();
	});
	fn_init();
});
function fn_init() {
	var insert_result="<c:out value='${insert_result}'/>";
	if (insert_result) {
		openAlertModal("SUCCESS","복구설정이 저장되었습니다.");
	}
}

function fn_rs_save() {
	var cnn_typ = $(":radio[name='id_cnn_typ']:checked").val();
	
	var sto_yon = $("#id_sto_yon option:selected").val();
	loadingDiv.on();	
	var comSubmit = new ComSubmit("frm");
    comSubmit.setUrl("/insertRsrSet");
    comSubmit.addParam("menu_cd", menu_cd);
    comSubmit.addParam("menu_id", menu_id);
    comSubmit.addParam("bms_id", bms_id);
    comSubmit.addParam("bts_id", bts_id);
    comSubmit.addParam("cnn_typ", cnn_typ);
    comSubmit.addParam("sto_yon", sto_yon);
    comSubmit.addParam("cpr", $("#id_cpr option:selected").val());
    comSubmit.submit();
	
}

function fn_conn_test() {
	if (bms_id == "" || $("#hst_ip").val() == "" || $("#pot_num").val() == "" || $("#db_id").val() == "" || $("#db_pwd").val() == "") {
		openAlertModal("WARNING","인스턴스 정보를 확인하세요.");
		return;
	}
	
	var param = new Object();
	param.bms_id = bms_id;
	param.bts_ip = $("#hst_ip").val();
	param.bts_pot = $("#pot_num").val();
	param.bts_usr = $("#db_id").val();
	param.bts_pwd = $("#db_pwd").val();
    $.ajax({
        url : "/conn_test",
		data : param,
        type : "POST",
        async : false,
        success : function(data, status) {
        	loadingDiv.off();
        	var result = data.result;
        	if (result) {
        		openAlertModal("SUCCESS","접속 성공!!");
        	} else {
        		openAlertModal("WARNING","접속 실패!!");
        	}
        },
		error : function(request, status, error) {
			loadingDiv.off();
			if (request.status == 400) {
		    	var comSubmit = new ComSubmit("commonForm");
			    comSubmit.setUrl("/main");
			    comSubmit.submit();
			} else {
				openAlertModal("ERROR",error);
			}
		}
    });
}

function fn_ssh_conn_test() {
	if (bms_id == "" || $("#hst_ip").val() == "" || $("#os_id").val() == "" || $("#os_pwd").val() == "") {
		openAlertModal("WARNING","SSH 정보를 확인하세요.");
		return;
	}
	
	var param = new Object();
	param.bms_id = bms_id;
	//param.xtr_bny_log_pth = $("#xtr_bny_log_pth").val();
	param.os_id = $("#os_id").val();
	param.os_pwd = $("#os_pwd").val();
	param.bts_ip = $("#hst_ip").val();
    $.ajax({
        url : "/ssh_conn_test",
		data : param,
        type : "POST",
        async : false,
        success : function(data, status) {
        	loadingDiv.off();
        	var result = data.result;
        	if (result) {
        		openAlertModal("SUCCESS","접속 성공!!");
        	} else {
        		openAlertModal("WARNING","접속 실패!!");
        	}
        },
		error : function(request, status, error) {
			loadingDiv.off();
			if (request.status == 400) {
		    	var comSubmit = new ComSubmit("commonForm");
			    comSubmit.setUrl("/main");
			    comSubmit.submit();
			} else {
				openAlertModal("ERROR",error);
			}
		}
    });
}

</script>
<!-- 내용 -->
<div class="content">
<form id="frm" name="frm">
	<div class="box">
		<!-- Manager Server -->
		<div class="box_title">Manager Server</div>
		<dl class="box_content">
			<dt class="tooltip" title="Manager Server DB 'mysql' and 'mysqlbinlog' and 'mysqldump' File Path">DB Client Utilities Path<span class="required" >*</span></dt>
			<dd><input type="text" id="msq_clt_utl_pth" name="msq_clt_utl_pth" value="${frsmap.msq_clt_utl_pth }" style="ime-mode:disabled;" maxlength="100"></dd>
<!-- 			<dt class="tooltip" title="Manager Server DB Percona XtraBackup 'innobackupex' File Path">DB Backup Util Path<span class="required" >*</span></dt> -->
<%-- 			<dd><input type="text" id="rmt_msq_bny_pth" name="rmt_msq_bny_pth" value="${frsmap.rmt_msq_bny_pth }" style="ime-mode:disabled;" maxlength="100"></dd> --%>
			<dt class="tooltip" title="Temporary Directory to use for Recovery Operations on the Manager Server">임시 디렉토리<span class="required" >*</span></dt>
			<dd><input type="text" id="tmp_dir" name="tmp_dir" value="${frsmap.tmp_dir }" style="ime-mode:disabled;" maxlength="100"></dd>
		</dl>
	</div>
	
	<div class="box">
		<!-- Target DB -->
		<div class="box_title">Target 인스턴스</div>
		<dl class="box_content">
			<dt>Connection Type</dt>
			<dd><input type="radio" id="id_cnn_typ" name="id_cnn_typ" checked disabled value="port">Port</dd>
			<dt>Port Number<span class="required" >*</span></dt>
			<dd><input type="text" id="pot_num" name="pot_num" value="${frsmap.pot_num }" onkeypress="return fn_press(event, 'numbers');" style="ime-mode:disabled;" maxlength="5"></dd>
			<dt>Host IP<span class="required" >*</span></dt>
			<dd><input type="text" id="hst_ip" name="hst_ip" value="${frsmap.hst_ip }" style="ime-mode:disabled;" maxlength="15"></dd>
			<dt class="tooltip" title="Target DB DATA Directory Path">DATA Directory<span class="required" >*</span></dt>
			<dd><input type="text" id="rsr_dat_dir" name="rsr_dat_dir" value="${frsmap.rsr_dat_dir }" style="ime-mode:disabled;" maxlength="100"></dd>
			
			<c:if test="${not empty avt_nam && avt_nam eq 'M01'}">
			<dt class="tooltip" title="Target DB MariaBackup 'mariabackup' File Path">DB Backup Util Path<span class="required" >*</span></dt>
			<dd><input type="text" id="xtr_bny_log_pth" name="xtr_bny_log_pth" value="${frsmap.xtr_bny_log_pth }" style="ime-mode:disabled;" maxlength="100"></dd>
			</c:if>
			<dt class="tooltip" title="Supports data backup of recovery server when 'Y' is selected">데이터백업 여부</dt>
			<dd>
				<select id="id_sto_yon">
					<c:choose>
						<c:when test="${fn:length(rslist) > 0}">
							<c:forEach var="row" items="${rslist}" varStatus="status">
					<option value="${row.com_cod }" <c:if test="${row.com_cod eq frsmap.sto_yon}">selected</c:if>>${row.com_cod_nam }</option>
							</c:forEach>
						</c:when>
					</c:choose>
				</select>
			</dd>
			<dt class="tooltip" title="/etc/init.d/[service startup name]">서비스 기동 네임<span class="required" >*</span></dt>
			<dd><input type="text" id="svc_sta_nam" name="svc_sta_nam" value="${frsmap.svc_sta_nam }" style="ime-mode:disabled;" maxlength="50"></dd>
			<dt>OS 아이디<span class="required" >*</span></dt>
			<dd><input type="text" id="os_id" name="os_id" value="${frsmap.os_id }" style="ime-mode:disabled;" maxlength="50"></dd>
			<dt>OS 비밀번호<span class="required" >*</span></dt>
			<dd><input type="password" id="os_pwd" name="os_pwd" value="${frsmap.os_pwd }" style="ime-mode:disabled;" maxlength="50"><div class='floatR btnWrap'  id="db_ssh_test"><a href='#void' class='rad50 bgc_sky' onclick='fn_ssh_conn_test();'>SSH접속테스트</a></div></dd>
			<!--
			<dt>dt>SSL Option</dt>
			<dd><input type="text" class="db_ssh_test" id="ssl_opt" name="ssl_opt" value="${frsmap.ssl_opt }" style="ime-mode:disabled;" maxlength="100"></dd>
			-->
			<dt>DB 아이디<span class="required" >*</span></dt>
			<dd><input type="text" id="db_id" name="db_id" value="${frsmap.db_id }" style="ime-mode:disabled;" maxlength="50"></dd>
			<dt>DB 비밀번호<span class="required" >*</span></dt>
			<dd><input type="password" id="db_pwd" name="db_pwd" value="${frsmap.db_pwd }" style="ime-mode:disabled;" maxlength="100"><div class='floatR btnWrap' id="db_conn_test2"><a href='#void' class='rad50 bgc_sky' onclick='fn_conn_test();'>DB접속테스트</a></div></dd>
			<c:if test="${not empty avt_nam && avt_nam eq 'M01'}">
			<dt class="tooltip" title="Target DB Configuration File Path">Restore Default File<span class="required" >*</span></dt>
			<dd><input type="text" id="rsr_dft_fil" name="rsr_dft_fil" value="${frsmap.rsr_dft_fil }" style="ime-mode:disabled;" maxlength="100"></dd>
			</c:if>
			<dt class="tooltip" title="Select 'Y' when the backup is compressed.">Compress</dt>
			<dd>
				<select id="id_cpr">
					<option value="N" <c:if test="${frsmap.cpr eq 'N'}">selected</c:if>>N</option>
					<option value="Y" <c:if test="${frsmap.cpr eq 'Y'}">selected</c:if>>Y</option>
				</select>
			</dd>
			<c:if test="${bts_mlt_ins eq 'Y'}">
			<dt>Instance Number</dt>
			<dd><input type="text" id="ins_num" name="ins_num" value="${frsmap.ins_num }" style="ime-mode:disabled;" maxlength="10"></dd>
			</c:if>
		</dl>
	</div>
	
	<!-- 하단:버튼 -->
	<div class="bottom_btn">
		<a href="#void" class="save" id="rsset_save">저장</a>
	</div>

</form>	
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
