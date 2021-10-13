<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
	$("#svr_refresh").on("click", function(e){
        e.preventDefault();
        svr_refresh(1);
    });
	fn_init();
});
function fn_init() {
	var inp_ms_nam = "<c:out value='${inp_ms_nam}'/>";
	var insert_result = "<c:out value='${insert_result}'/>";
	if (insert_result) {
		openAlertModal("SUCCESS", inp_ms_nam+" 관리서버가 저장되었습니다.");
	} else if (insert_result != "" && !insert_result) {
		openAlertModal("Fail",inp_ms_nam+" 관리서버 저장이 실패되었습니다.");
	}
	
	var update_result="<c:out value='${update_result}'/>";
	if (update_result) {
		openAlertModal("SUCCESS", inp_ms_nam+ " 관리서버가 수정되었습니다.");
	} else if (update_result != "" && !update_result) {
		openAlertModal("Fail",inp_ms_nam+" 관리서버 수정이 실패되었습니다.");
	}
	var delete_result="<c:out value='${delete_result}'/>";
	if (delete_result) {
		openAlertModal("SUCCESS", inp_ms_nam+" 관리서버가 삭제되었습니다.");
	} else if (delete_result != "" && !delete_result) {
		openAlertModal("Fail",inp_ms_nam+" 관리서버 삭제가 실패되었습니다.");
	}
	fn_clear();
}

function svr_refresh(pageNo) {
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/mng_svr");
    comSubmit.addParam("menu_cd", $("#menu_cd").val());
    comSubmit.addParam("currentPageNo", pageNo);
    comSubmit.submit();
}
function fn_svr_info(id,nam) {
	var comAjax = new ComAjax();
	comAjax.setUrl("/svr_info");
	comAjax.setCallback("svr_infoload");
	comAjax.addParam("ms_id", id);
	comAjax.addParam("ms_nam", nam);
	comAjax.ajax();
}
function svr_infoload(data) {
	
	var map = data.map;
	
	$("#ms_id_value").val(map.ms_id);
	$("#ms_nam").val(map.ms_nam);
	$("#ms_ip").val(map.ms_ip);
	$("#ms_port").val(map.ms_port);
	$("#ms_usr").val(map.ms_usr);
	$("#ms_pwd").val(map.ms_pwd);
// 	$("#ms_ssh_usr").val(map.ms_ssh_usr);
	$("#ms_sve_dir").val(map.ms_sve_dir);
	$("#ms_bny_pth").val(map.ms_bny_pth);
	
	$("#ms_nam").attr('disabled','disabled');
	$("#ms_sve_dir").attr('disabled','disabled');
	
	$("#svr_insert").attr("class","add btn_s");
	$("#svr_insert").text("추가");
	$("#svr_insert").attr("href","javascript:fn_clear();");
	$("#svr_modify").attr("class","modify btn_s");
	$("#svr_modify").attr("href","javascript:fn_modify();");
	$("#svr_delete").attr("class","delete btn_s");
	$("#svr_delete").attr("href","javascript:fn_delete();");
}

function fn_insert() {
	
	var ms_nam = $("#ms_nam");
	if (ms_nam.val() == "") {
		openAlertModal("WARNING","관리서버명을 입력하세요.");
		ms_nam.focus();
		return;
	}
	var ms_ip = $("#ms_ip");
	if (ms_ip.val() == "") {
		openAlertModal("WARNING","IP를 입력하세요.");
		ms_ip.focus();
		return;
	}
	var ms_port = $("#ms_port");
	if (ms_port.val() == "") {
		openAlertModal("WARNING","Port를 입력하세요.");
		ms_port.focus();
		return;
	}
	var ms_usr = $("#ms_usr");
	if (ms_usr.val() == "") {
		openAlertModal("WARNING","사용자를 입력하세요.");
		ms_usr.focus();
		return;
	}
	var ms_pwd = $("#ms_pwd");
	if (ms_pwd.val() == "") {
		openAlertModal("WARNING","비밀번호를 입력하세요.");
		ms_pwd.focus();
		return;
	}
// 	var ms_ssh_usr = $("#ms_ssh_usr");
// 	if (ms_ssh_usr.val() == "") {
// 		openAlertModal("WARNING","SSH 계정을 입력하세요.");
// 		ms_ssh_usr.focus();
// 		return;
// 	}
	var ms_sve_dir = $("#ms_sve_dir");
	if (ms_sve_dir.val() == "") {
		openAlertModal("WARNING","저장경로를 입력하세요.");
		ms_sve_dir.focus();
		return;
	}
	var ms_bny_pth = $("#ms_bny_pth");
	if (ms_bny_pth.val() == "") {
		openAlertModal("WARNING","Binary Path를 입력하세요.");
		ms_bny_pth.focus();
		return;
	}
	loadingDiv.on();
	var comSubmit = new ComSubmit("frm");
    comSubmit.setUrl("/insertSvr");
    comSubmit.addParam("menu_cd", menu_cd);
    comSubmit.addParam("menu_id", menu_id);
    comSubmit.addParam("bms_id", bms_id);
    comSubmit.addParam("bts_id", bts_id);
    comSubmit.submit();
}

function fn_modify() {
	
	var ms_nam = $("#ms_nam");
	if (ms_nam.val() == "") {
		openAlertModal("WARNING","관리서버명을 입력하세요.");
		//$("#ms_nam").attr('disabled','disabled');
		ms_nam.focus();
		return;
	}
	
	var ms_sve_dir = $("#ms_sve_dir");
	if (ms_sve_dir.val() == "") {
		openAlertModal("WARNING","저장경로를 입력하세요.");
		//$("#ms_sve_dir").attr('disabled','disabled');
		ms_sve_dir.focus();
		return;
	}
	
	var ms_ip = $("#ms_ip");
	if (ms_ip.val() == "") {
		openAlertModal("WARNING","IP를 입력하세요.");
		ms_ip.focus();
		return;
	}
	var ms_port = $("#ms_port");
	if (ms_port.val() == "") {
		openAlertModal("WARNING","Port를 입력하세요.");
		ms_port.focus();
		return;
	}
	var ms_usr = $("#ms_usr");
	if (ms_usr.val() == "") {
		openAlertModal("WARNING","사용자를 입력하세요.");
		ms_usr.focus();
		return;
	}
	var ms_pwd = $("#ms_pwd");
	if (ms_pwd.val() == "") {
		openAlertModal("WARNING","비밀번호를 입력하세요.");
		ms_pwd.focus();
		return;
	}
// 	var ms_ssh_usr = $("#ms_ssh_usr");
// 	if (ms_ssh_usr.val() == "") {
// 		openAlertModal("WARNING","SSH 계정을 입력하세요.");
// 		ms_ssh_usr.focus();
// 		return;
// 	}
	var ms_bny_pth = $("#ms_bny_pth");
	if (ms_bny_pth.val() == "") {
		openAlertModal("WARNING","Binary Path를 입력하세요.");
		ms_bny_pth.focus();
		return;
	}
	
	ms_nam.removeAttr('disabled');
	ms_sve_dir.removeAttr('disabled');
	
	loadingDiv.on();
	var comSubmit = new ComSubmit("frm");
    comSubmit.setUrl("/updateSvr");
    comSubmit.addParam("menu_cd", menu_cd);
    comSubmit.addParam("menu_id", menu_id);
    comSubmit.addParam("ms_id", $("#ms_id_value").val())
    comSubmit.addParam("bms_id", bms_id);
    comSubmit.addParam("bts_id", bts_id);
    comSubmit.submit();
}


function fn_delete(){
	
	var ms_nam = $("#ms_nam");
	if (ms_nam.val() == "") {
		openAlertModal("WARNING","관리서버명을 선택하세요.");
		ms_nam.focus();
		return;
	}
	
	var id = $("#ms_id_value").val()
	var comAjax = new ComAjax();
	comAjax.setUrl("/deleteSvrInfo");
	comAjax.setCallback("svr_deleteSvrInfo");
	comAjax.addParam("bms_pid", id);
	comAjax.ajax();
}
function svr_deleteSvrInfo(data) {

	if(data.result == "doDelete"){
		openConfirmModal("CONFIRM", "삭제하시겠습니까?", function() {
			$(".modal").remove();
			loadingDiv.on();
			var ms_nam = $("#ms_nam");
			var comSubmit = new ComSubmit();
		    comSubmit.setUrl("/deleteSvr");
		    comSubmit.addParam("menu_cd", menu_cd);
		    comSubmit.addParam("menu_id", menu_id);
		    comSubmit.addParam("bms_id", bms_id);
		    comSubmit.addParam("bts_id", bts_id);
		    comSubmit.addParam("ms_id", $("#ms_id_value").val())
		    comSubmit.addParam("ms_nam", ms_nam.val());
		    comSubmit.submit();
		});
	}else{
		openAlertModal("WARNING","대상서버가 등록되어 있습니다. 대상서버 삭제  후 진행할 수 있습니다");
	}
	
}

function fn_clear() {
	
	$("#ms_nam").removeAttr('disabled');
	$("#ms_sve_dir").removeAttr('disabled');
	
	$("#ms_nam").val("");
	$("#ms_ip").val("");
	$("#ms_port").val("");
	$("#ms_usr").val("");
	$("#ms_pwd").val("");
// 	$("#ms_ssh_usr").val("");
	$("#ms_sve_dir").val("");
	$("#ms_bny_pth").val("");
	
	$("#svr_insert").attr("class","save btn_s");
	$("#svr_insert").text("저장");
	$("#svr_insert").attr("href","javascript:fn_insert();");
	$("#svr_modify").attr("class","modify_disable btn_s");
	$("#svr_modify").attr("href","#void");
	$("#svr_delete").attr("class","delete_disable btn_s");
	$("#svr_delete").attr("href","#void");
}

function fn_popclose() {
	layerClose2();
}

</script>
<!-- 내용 -->
<input type="hidden" id="ms_id_value"/>
<div class="content">
	<!-- Create ZMC User -->
	<div class="box">
		<div class="box_title">관리서버 정보</div>
<form id="frm" name="frm">
		<dl class="box_content">
			<dt>관리서버명<span class="required">*</span></dt>
			<dd><input type="text" id="ms_nam" name="ms_nam" required style="ime-mode:disabled;" maxlength="50"></dd>
			<dt>저장 경로<span class="required">*</span></dt>
			<dd><input type="text" id="ms_sve_dir" name="ms_sve_dir" style="ime-mode:disabled;" maxlength="100"></dd>
			<dt>IP<span class="required">*</span></dt>
			<dd><input type="text" id="ms_ip" name="ms_ip" required style="ime-mode:disabled;" maxlength="15"></dd>
			<dt>Port<span class="required">*</span></dt>
			<dd><input type="text" id="ms_port" name="ms_port" required onkeypress="return fn_press(event, 'numbers');" style="ime-mode:disabled;" maxlength="5"></dd>
			<dt>사용자<span class="required">*</span></dt>
			<dd><input type="text" id="ms_usr" name="ms_usr" required style="ime-mode:disabled;" maxlength="30"></dd>
			<dt>비밀번호<span class="required">*</span></dt>
			<dd><input type="password" id="ms_pwd" name="ms_pwd" required style="ime-mode:disabled;" maxlength="100"></dd>
<!-- 			<dt>SSH 계정<span class="required">*</span></dt> -->
<!-- 			<dd><input type="text" id="ms_ssh_usr" name="ms_ssh_usr" style="ime-mode:disabled;" maxlength="30"></dd> -->
			<dt class="tooltip" title="Manager Server DB 'mysql' File Path">Binary Path<span class="required">*</span></dt>
			<dd><input type="text" id="ms_bny_pth" name="ms_bny_pth" style="ime-mode:disabled;" maxlength="100"></dd>
		</dl>
</form>
		<!-- 하단:버튼 -->
		<div class="bottom_btn">
			<a href="javascript:fn_insert();" class="save btn_s" id="svr_insert">저장</a>
			<a href="javascript:fn_modify();" class="modify btn_s" id="svr_modify">수정</a>
			<a href="javascript:fn_delete();" class="delete btn_s" id="svr_delete">삭제</a>
		</div>
		
		<!-- 하위:그리드 -->
		<div class="box_lowlank2 admin">
			<div class="list_title">서버 리스트</div>
			<table class="table4">
				<colgroup>
					<col style="width:20%">
					<col style="width:12%">
					<col style="width:10%">
					<col style="width:12%">
					<col style="width:10%">
					<col style="width:12%">
					<col style="width:12%">
					<col style="width:12%">
				</colgroup>
				<thead>
					<th>서버</th>
					<th>IP</th>
					<th>Port</th>
					<th>사용자</th>
<!-- 					<th>SSH</th> -->
					<th>저장경로</th>
					<th>생성일</th>
					<th>수정일</th>
				</thead>
				<tbody>
				<c:choose>
	                <c:when test="${fn:length(list) > 0}">
	                    <c:forEach var="row" items="${list}" varStatus="status">
                    	<tr onclick="fn_svr_info('${row.ms_id }','${row.ms_nam }');">
                            <td class="align_left">${row.ms_nam }</td>
                            <td><a href="#void">${row.ms_ip }</a></td>
                            <td><a href="#void">${row.ms_port }</a></td>
                            <td><a href="#void">${row.ms_usr }</a></td>
<%--                             <td class="align_left"><a href="#void">${row.ms_ssh_usr }</a></td> --%>
                            <td><a href="#void">${row.ms_sve_dir }</a></td>
                            <td><a href="#void">${row.reg_dt }</a></td>
                            <td><a href="#void">${row.upd_dt }</a></td>
                        </tr>
	                    </c:forEach>  
	                </c:when>
	                <c:otherwise>
	                    <tr>
	                        <td colspan="8" class="nodata">조회된 결과가 없습니다.</td>
	                    </tr>
	                </c:otherwise>
				</c:choose>
				</tbody>
			</table>
			
			<!-- 페이징 -->
			<div class="page2">
				<ul class="pagination pagination-warning" style="margin:0px;">
					<c:if test="${pagination.startPage-1 != 0 }">
						<li><a href="javascript:svr_refresh(${pagination.startPage-1})">&laquo;</a></li>
					</c:if>
					<c:forEach var="i" begin="${pagination.startPage }" end="${pagination.endPage }" varStatus="status">
						<li <c:if test="${i eq pagination.currentPage }">class="active"</c:if>><a href="javascript:svr_refresh(${i})">${i}</a></li>
					</c:forEach>
					<c:if test="${pagination.endPage % pagination.pageCount == 0 && pagination.lastPage > pagination.endPage}">
						<li><a  href="javascript:svr_refresh(${pagination.endPage+1})">&raquo;</a></li>
					</c:if>
				</ul>
			</div>
			<!-- //페이징 -->
		</div> <!-- //하위:그리드 -->
		
	
	</div> <!-- //Create ZMC User -->
	
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
