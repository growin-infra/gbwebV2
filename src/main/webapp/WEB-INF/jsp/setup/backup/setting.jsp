<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function(){
	
	$("#set_save").on("click", function(e){
		e.preventDefault();
		fn_bs_save();
	});
	$("#id_bak_typ").change(function(){
		var idx = $("#id_bak_typ option:selected").index();
		$(".sourceType").removeClass("on").eq(idx).addClass("on");
		var bak_typ = $("#id_bak_typ option:selected").val();
		var bak_typ_itm = "<c:out value='${bsmap.bak_typ_itm}'/>";
		
		if ("BT01" != bak_typ) {
			fn_bt_dblist(bak_typ,bak_typ_itm);
		} else {
			$("#db_list").html("");
			$("#tb_list").html("");
			$("#tb_db_chk").html("");
		}
		
	});
	
	$("#tb_db_chk").change(function(){
		var bak_typ = $("#id_bak_typ option:selected").val();
		var bak_typ_itm = $("#tb_db_chk option:selected").val();
		var bak_typ_tbe = "<c:out value='${bsmap.bak_typ_tbe}'/>";
		fn_bt_tblist(bak_typ,bak_typ_itm,bak_typ_tbe);
	});
	
	fn_init();
	
	loadingDiv.off();
});

function fn_init() {
	var insert_result="<c:out value='${insert_result}'/>";
	if (insert_result) {
		openAlertModal("SUCCESS","백업설정이 저장되었습니다.");
		$("#pot_num").attr('disabled','disabled');
		$("#hst_ip").attr('disabled','disabled');
		$("#bts_usr").attr('disabled','disabled');
		$("#bts_pwd").attr('disabled','disabled');
	}
	
	var bak_typ = "<c:out value='${bsmap.bak_typ}'/>";
	var bak_typ_itm = "<c:out value='${bsmap.bak_typ_itm}'/>";
	var bak_typ_tbe = "<c:out value='${bsmap.bak_typ_tbe}'/>";
	if (bak_typ != "" && new String(bak_typ).valueOf() != "undefined") {
		if (bak_typ == "BT02") {
			$(".sourceType").removeClass("on").eq(1).addClass("on");
			fn_bt_dblist(bak_typ,bak_typ_itm);
		} else if (bak_typ == "BT03") {
			$(".sourceType").removeClass("on").eq(2).addClass("on");	
			fn_bt_tblist(bak_typ,bak_typ_itm,bak_typ_tbe);
		}
		
	}
}

function fn_bt_dblist(bak_typ,bak_typ_itm) {

	loadingDiv.on();

	var comAjax = new ComAjax();
	comAjax.setUrl("/set_bt_dblist");
	comAjax.setCallback("fn_bt_dblistload");
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("bak_typ", bak_typ);
	comAjax.addParam("bak_typ_itm", bak_typ_itm);
	comAjax.ajax();
}
function fn_bt_dblistload(data) {
	var bak_typ = data.bak_typ;
	var bak_typ_itm = data.bak_typ_itm;
	var str = "";
	$("#db_list").html("");
	$("#tb_list").html("");
	$("#tb_db_chk").html("");
	var dblist = data.dblist;
	if (bak_typ == "BT02") {
		if (dblist != null && dblist.length > 0) {
			for (var i=0; i<dblist.length; i++) {
				str += "<tr>";
				
				var sti = bak_typ_itm.split(",");
				var snam = "";
				for (var j=0; j<sti.length; j++) {
					if (sti[j] == dblist[i].table_schema) {
						snam = sti[j];
					}
				}
				if (snam == dblist[i].table_schema) {
					str += "<td><input name='db_chk' value='"+dblist[i].table_schema+"' type='checkbox' checked='checked' /></td>";
				} else {
					str += "<td><input name='db_chk' value='"+dblist[i].table_schema+"' type='checkbox' /></td>";
				}
				str += "<td>"+dblist[i].table_schema+"</td>";
				str += "<td>"+dblist[i].size+"</td>";
				str += "<td>"+dblist[i].cnt+"</td>";
				str += "</tr>";
			}
		} else {
			str = "<tr><td colspan='4' style='text-align:center;'>조회된 결과가 없습니다.</td></tr>";
		}
		$("#db_list").append(str);
	} else if (bak_typ == "BT03") {
		var dtstr = "<option>선택하세요</option>";
		if (dblist != null && dblist.length > 0) {
			for (var i=0; i<dblist.length; i++) {
				dtstr += "<option value='"+dblist[i].table_schema+"'>"+dblist[i].table_schema+"</option>";
			}
		}
		$("#tb_db_chk").append(dtstr);
		
		$("#tb_list").append("<tr><td colspan='5' style='text-align:center;'>조회된 결과가 없습니다.</td></tr>");
		
	}
	
}

function fn_bt_tblist(bak_typ,bak_typ_itm,bak_typ_tbe) {
	var comAjax = new ComAjax();
	comAjax.setUrl("/set_bt_tblist");
	comAjax.setCallback("fn_bt_tblistload");
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("bak_typ", bak_typ);
	comAjax.addParam("bak_typ_itm", bak_typ_itm);
	if (bak_typ_tbe != "" && new String(bak_typ_tbe).valueOf() != "undefined") {
		comAjax.addParam("bak_typ_tbe", bak_typ_tbe);
	} else {
		comAjax.addParam("bak_typ_tbe", "<c:out value='${bsmap.bak_typ_tbe}'/>");
	}
	comAjax.ajax();
}
function fn_bt_tblistload(data) {
	var list = data.tblist;
	var bak_typ = data.bak_typ;
	var bak_typ_itm = data.bak_typ_itm;
	var bak_typ_tbe = data.bak_typ_tbe;
	var dblist = data.dblist;
	
	$("#tb_db_chk").html("");
	var dtstr = "<option>선택하세요</option>";
	if (dblist != null && dblist.length > 0) {
		for (var i=0; i<dblist.length; i++) {
			if (bak_typ_itm == dblist[i].table_schema) {
				dtstr += "<option value='"+dblist[i].table_schema+"' selected>"+dblist[i].table_schema+"</option>";
			} else {
				dtstr += "<option value='"+dblist[i].table_schema+"'>"+dblist[i].table_schema+"</option>";
			}
		}
	}
	$("#tb_db_chk").append(dtstr);
	
	var str = "";
	$("#tb_list").html("");
	if (list != null && list.length > 0) {
		for (var i=0; i<list.length; i++) {
			str += "<tr>";
			var sti = bak_typ_tbe.split(",");
			var snam = "";
			for (var j=0; j<sti.length; j++) {
				if (sti[j] == list[i].table_name) {
					snam = sti[j];
				}
			}
			
			if (snam == list[i].table_name) {
				str += "<td><input name='tb_chk' value='"+list[i].table_name+"' type='checkbox' checked='checked'></td>";
			} else {
				str += "<td><input name='tb_chk' value='"+list[i].table_name+"' type='checkbox'></td>";
			}
			str += "<td>"+list[i].table_name+"</td>";
			str += "<td>"+list[i].size+"</td>";
			str += "<td>"+list[i].table_type+"</td>";
			str += "<td>"+list[i].cnt+"</td>";
			str += "</tr>";
		}
	} else {
		str = "<tr><td colspan='5' style='text-align:center;'>조회된 결과가 없습니다.</td></tr>";
	}
	$("#tb_list").append(str);
	
	loadingDiv.off();
}

function fn_popclose() {
// 	fn_init();
	layerClose2();
}

function fn_bs_save() {
	
	var msq_clt_utl_pth = $("#msq_clt_utl_pth");
	var pot_num = $("#pot_num");
	var hst_ip = $("#hst_ip");
	var tmp_dir = $("#tmp_dir");
	var bak_typ = $("#id_bak_typ option:selected").val();
	var sou_db = $("#source_db option:selected").val();
	
    if (sou_db == "" || new String(sou_db).valueOf() == "undefined") {
		openAlertModal("WARNING","Source DB 를 선택하세요.\nSource DB 명이 안보일 경우 접속정보를 확인하세요.");
		return;
	}
    
    if ($.trim(tmp_dir.val()) == "") {
		openAlertModal("WARNING","임시 디렉토리 정보를 입력하세요.");
		return;
	}
    
    $("#pot_num").removeAttr('disabled');
	$("#hst_ip").removeAttr('disabled');
	$("#bts_usr").removeAttr('disabled');
	$("#bts_pwd").removeAttr('disabled');
    
	var bak_typ_itm = "";
	if (bak_typ == "BT02") {
		$("input[name='db_chk']:checked").each(function() {
			bak_typ_itm += $(this).val()+"," ;
		});
		bak_typ_itm = bak_typ_itm.substring(0,bak_typ_itm.lastIndexOf(",")); //맨끝 콤마 지우기
	} else if (bak_typ == "BT03") {
		bak_typ_itm = $("#tb_db_chk option:selected").val();
	}
	var bak_typ_tbe = "";
	if (bak_typ == "BT03") {
		$("input[name='tb_chk']:checked").each(function() {
			bak_typ_tbe += $(this).val()+"," ;
		});
		bak_typ_tbe = bak_typ_tbe.substring(0,bak_typ_tbe.lastIndexOf(",")); //맨끝 콤마 지우기
	}
	
	loadingDiv.on();	
	var comSubmit = new ComSubmit("frm");
    comSubmit.setUrl("/insertBakSet");
    comSubmit.addParam("menu_cd", menu_cd);
    comSubmit.addParam("menu_id", menu_id);
    comSubmit.addParam("bms_id", bms_id);
    comSubmit.addParam("bts_id", bts_id);
    comSubmit.addParam("bak_typ", bak_typ);
    comSubmit.addParam("sou_db", sou_db);
    comSubmit.addParam("bak_typ_itm", bak_typ_itm);
    comSubmit.addParam("bak_typ_tbe", bak_typ_tbe);
    comSubmit.submit();
}

</script>
<!-- 내용 -->
<div class="content">
<form id="frm" name="frm">
<input type="hidden" id="bak_typ" value="${bsmap.bak_typ}" />
<input type="hidden" id="bak_typ_itm" value="${bsmap.bak_typ_itm}" />
	<div class="box">
		<!-- Manager Server -->
		<div class="box_title">Manager Server</div>
		<dl class="box_content">
			<dt class="tooltip" title="Manager Server DB 'mysql' File Path">DB Client Utilities Path<span class="required" >*</span></dt>
			<dd><input type="text" value="${bsmap.msq_clt_utl_pth }" id="msq_clt_utl_pth" name="msq_clt_utl_pth" style="ime-mode:disabled;" maxlength="100"></dd>
			<dt class="tooltip" title="Manager Server DB MariaBackup 'mariabackup' File Path">DB Backup Util Path</dt>
			<dd><input type="text" value="${bsmap.mng_xtr_bny_log_pth }" id="mng_xtr_bny_log_pth" name="mng_xtr_bny_log_pth" style="ime-mode:disabled;" maxlength="100"></dd>
		</dl>
	</div>
	
	<!-- Source DB -->
	<div class="box">
		
		<div class="box_title">Source 인스턴스</div>
		<dl class="box_content">
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
			<dt>Connection Type</dt>
			<dd><input type="radio" checked disabled>Port</dd>
			<dt>Port Number<span class="required" >*</span></dt>
			<dd><input type="text" value="<c:choose><c:when test="${empty bsmap.pot_num }">${dbsvrmap.bts_pot }</c:when><c:otherwise>${bsmap.pot_num }</c:otherwise></c:choose>" id="pot_num" name="pot_num" disabled onkeypress="return fn_press(event, 'numbers');" style="ime-mode:disabled;" maxlength="5"></dd>
			<dt>Host IP<span class="required" >*</span></dt>
			<dd><input type="text" value="<c:choose><c:when test="${empty bsmap.hst_ip }">${dbsvrmap.bts_ip }</c:when><c:otherwise>${bsmap.hst_ip }</c:otherwise></c:choose>" id="hst_ip" name="hst_ip" disabled style="ime-mode:disabled;" maxlength="15"></dd>
			<dt>DB 아이디<span class="required" >*</span></dt>
			<dd><input type="text" value="<c:choose><c:when test="${empty bsmap.bts_usr }">${dbsvrmap.bts_usr }</c:when><c:otherwise>${bsmap.bts_usr }</c:otherwise></c:choose>" id="bts_usr" name="bts_usr" disabled style="ime-mode:disabled;" maxlength="50"></dd>
			<dt>DB 비밀번호<span class="required" >*</span></dt>
			<dd><input type="password" value="<c:choose><c:when test="${empty bsmap.bts_pwd }">${dbsvrmap.bts_pwd }</c:when><c:otherwise>${bsmap.bts_pwd }</c:otherwise></c:choose>" id="bts_pwd" name="bts_pwd" disabled style="ime-mode:disabled;" maxlength="100"></dd>
			<!--
			<dt>SSH 아이디<span class="required" >*</span></dt>
			<dd><input type="text" value="${bsmap.ssh_usr }" id="ssh_usr" name="ssh_usr" style="ime-mode:disabled;" maxlength="30"></dd>
			-->
			<dt class="tooltip" title="Temporary Directory to use for Backup Operations on the Source Server">임시 디렉토리<span class="required" >*</span></dt>
			<dd><input type="text" value="${bsmap.tmp_dir }" id="tmp_dir" name="tmp_dir" style="ime-mode:disabled;" maxlength="100"></dd>
			<dt class="tooltip" title="If You Set Database(s) or Tabale(s), Recovery should Proceed Manually !!!">Backup Source Type</dt>
			<dd>
				<select class="backup_select" id="id_bak_typ">
					<c:choose>
						<c:when test="${fn:length(tylist) > 0}">
							<c:forEach var="row" items="${tylist}" varStatus="status">
					<option value="${row.com_cod }" <c:if test="${row.com_cod eq bsmap.bak_typ}">selected</c:if>>${row.com_cod_nam }</option>
							</c:forEach>
						</c:when>
					</c:choose>
				</select>
			</dd>
		</dl>
		
		<div class="sourceType"></div>
		<div class="sourceType option1_list box_lowlank" id="bt02">
			<div class="list_title">Database List</div>
			<table class="table3">
				<colgroup>
					<col style="width:20%">
					<col style="">
					<col style="width:20%">
					<col style="width:20%">
				</colgroup>
				<thead>
					<th>Check</th>
					<th>Database</th>
					<th>Size</th>
					<th>TB CNT</th>
				</thead>
				<tbody id="db_list">
					<tr>
						<td><input type="checkbox"></td>
						<td>afterschool</td>
						<td>155.9MB</td>
						<td>215</td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>performance_schema</td>
						<td>0.0MB</td>
						<td>1</td>
					</tr>
				</tbody>
			</table>
		</div>

		<div class="sourceType  box_lowlank option2_list" id="bt03">
			<div class="list_title">Table List</div>
			<dl class="list_sub">
				<dt>Select Database</dt>
				<dd>
					<select id="tb_db_chk">
						<option>선택하세요</option>
					</select>
				</dd>
			</dl>
			<table class="table3">
				<colgroup>
					<col style="width:20%">
					<col style="">
					<col style="width:20%">
					<col style="width:20%">
					<col style="width:20%">
				</colgroup>
				<thead>
					<th>Check</th>
					<th>Table</th>
					<th>Size</th>
					<th>Type</th>
					<th>Row CNT</th>
				</thead>
				<tbody id="tb_list">
					<tr>
						<td><input type="checkbox"></td>
						<td>adm_division</td>
						<td>0.0MB</td>
						<td>TABLE</td>
						<td>248</td>
					</tr>
					<tr>
						<td><input type="checkbox"></td>
						<td>ccl_apply_bak201</td>
						<td>27.0MB</td>
						<td>TABLE</td>
						<td>150,999</td>
					</tr>
				</tbody>
			</table> 
		</div> 
		
	</div>
	
	<!-- 하단:버튼 -->
	<div class="bottom_btn">
		<a href="#void" class="save" id="set_save">저장</a>
	</div>
</form>
</div><!-- //내용 -->
<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
