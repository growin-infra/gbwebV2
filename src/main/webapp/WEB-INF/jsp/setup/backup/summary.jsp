<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function(){
	
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
	
	var avt_nam = "<c:out value='${fbmmap.avt_nam}'/>";
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
}
</script>
<!-- 내용 -->
<div class="content">
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
