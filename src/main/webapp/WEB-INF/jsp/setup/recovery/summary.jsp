<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function(){
	fn_init();
});
function fn_init() {
	
	$("#id_blank_rsr").hide();
	$("#id_lst_rsr").hide();
	$("#id_blank_set").hide();
	$("#id_rsr_set").hide();
	
	var rsr_lst_info = "<c:out value='${rsr_lst_info}'/>";
	if (!rsr_lst_info) {
		$("#id_lst_rsr").show();
	} else {
		$("#id_blank_rsr").show();
	}
	var rsr_set_blank = "<c:out value='${rsr_set_blank}'/>";
	if (!rsr_set_blank) {
		$("#id_rsr_set").show();
	} else {
		$("#id_blank_set").show();
	}
	
	var wdt = "<c:out value='${llmap.wrk_dt }'/>";
	$("#wrk_dt").html("");
	if (wdt != "") {
		$("#wrk_dt").append(strToCustomFormat(wdt));
	}
	
}
</script>
<!-- 내용 -->
<div class="content">
	
	<!-- 마지막 복구 정보 -->
	<div class="box">
		
		<!-- 마지막복구정보 blank -->
		<div id="id_blank_rsr">
		<div class="box_title">마지막 복구정보</div>
		<dl class="box_content view">
			<dt>마지막 복구정보가 없습니다.</dt>
		</dl>
		</div>
		
		<div id="id_lst_rsr">
		<div class="box_title">마지막 복구 정보</div>
		<dl class="box_content view">
			<dt>복구시점</dt>
			<dd id="wrk_dt"></dd>
			<dt>복구DB</dt>
			<dd>${llmap.typ }</dd>
			<dt>복구방법</dt>
			<dd>${llmap.mtd }</dd>
		</dl>
		</div>
	</div>
	
	<div class="box" id="id_blank_set">
		<!-- 복구설정 blank -->
		<div id="id_blank_mtd">
		<div class="box_title">복구설정 정보</div>
		<dl class="box_content view">
			<dt>복구설정 정보가 없습니다.</dt>
		</dl>
		</div>
	</div>
	
	<div id="id_rsr_set">
	<div class="box">
		<!-- Manager Server -->
		<div class="box_title">Manager Server</div>
		<dl class="box_content view">
			<dt>DB Client Utilities Path</dt>
			<dd>${frsmap.msq_clt_utl_pth }</dd>
<!-- 			<dt>MySQL Binary Path</dt> -->
<%-- 			<dd>${frsmap.rmt_msq_bny_pth }</dd> --%>
			<dt>임시 디렉토리</dt>
			<dd>${frsmap.tmp_dir }</dd>
		</dl>
	</div>
		
	<div class="box">
		<!-- Target DB -->
		<div class="box_title">Target 인스턴스</div>
		<dl class="box_content view">
			<dt>Connection Type</dt>
			<dd>${frsmap.cnn_typ }</dd>
			<dt>Port Number</dt>
			<dd>${frsmap.pot_num }</dd>
			<dt>Host IP</dt>
			<dd>${frsmap.hst_ip }</dd>
			<dt>DATA Directory</dt>
			<dd>${frsmap.rsr_dat_dir }</dd>
			<c:if test="${not empty avt_nam && avt_nam eq 'M01'}">
			<dt>Backup Util Binary Path</dt>
			<dd>${frsmap.xtr_bny_log_pth }</dd>
			</c:if>
			<dt>데이터백업 여부</dt>
			<dd>${frsmap.sto_yon_cdname }</dd>
			<dt>서비스 기동 네임</dt>
			<dd>${frsmap.svc_sta_nam }</dd>
			<dt>OS 아이디</dt>
			<dd>${frsmap.os_id }</dd>
			<dt>DB 아이디</dt>
			<dd>${frsmap.db_id }</dd>
			<c:if test="${not empty avt_nam && avt_nam eq 'M01'}">
			<dt>Restore Default File</dt>
			<dd>${frsmap.rsr_dft_fil }</dd>
			</c:if>
			<dt>Compress</dt>
			<dd>${frsmap.cpr }</dd>
			<c:if test="${bts_mlt_ins eq 'Y'}">
			<dt>Instance Number</dt>
			<dd>${frsmap.ins_num }</dd>
			</c:if>
		</dl>
	</div>
	</div>
	
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
