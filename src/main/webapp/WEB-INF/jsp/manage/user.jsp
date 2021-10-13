<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function() {
	$("#usr_refresh").on("click", function(e){
        e.preventDefault();
        usr_refresh(1);
    });
	fn_init();
});
function fn_init() {
	$("#usr_id").attr('disabled','disabled');
	var inp_usr_id = "<c:out value='${inp_usr_id}'/>";
	var insert_result = "<c:out value='${insert_result}'/>";
	if (insert_result) {
		openAlertModal("SUCCESS", inp_usr_id+" 사용자가 저장되었습니다.");
	} else if (insert_result != "" && !insert_result) {
		openAlertModal("Fail",inp_usr_id+" 사용자 저장이 실패되었습니다.");
	}
	
	var update_result="<c:out value='${update_result}'/>";
	if (update_result) {
		openAlertModal("SUCCESS", inp_usr_id+" 사용자가 수정되었습니다.");
	} else if (update_result != "" && !update_result) {
		openAlertModal("Fail",inp_usr_id+" 사용자 수정이 실패되었습니다.");
	}
	
	var delete_result="<c:out value='${delete_result}'/>";
	if (delete_result) {
		openAlertModal("SUCCESS",inp_usr_id+" 사용자가 삭제되었습니다.");
	} else if (delete_result != "" && !delete_result) {
		openAlertModal("Fail",inp_usr_id+" 사용자 삭제가 실패되었습니다.");
	}
	
	fn_clear();
}

function usr_refresh(pageNo) {
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/mng_usr");
    comSubmit.addParam("menu_cd", $("#menu_cd").val());
    comSubmit.addParam("currentPageNo", pageNo);
    comSubmit.submit();
}

function fn_usr_info(id) {
	var comAjax = new ComAjax();
	comAjax.setUrl("/usr_info");
	comAjax.setCallback("usr_infoload");
	comAjax.addParam("id", id);
	comAjax.ajax();
// 	$("input[name=usr_id]").prop("readonly",true);
}
function usr_infoload(data) {
	
	var map = data.map;
	
	$("#usr_id").val(map.usr_id);
	$("#usr_ema").val(map.usr_ema);
	$("#usr_pwd").val(map.pw);
	
	$("#usr_pwd_vrf").val(map.pw);
	$("input[name='n_usr_ath'][value="+map.usr_ath+"]").prop("checked", true);
	
	$("#usr_id").attr('disabled','disabled');
	$("#usr_insert").attr("class","add btn_s");
	$("#usr_insert").text("추가");
	$("#usr_insert").attr("href","javascript:fn_clear();");
	$("#usr_modify").attr("class","modify btn_s");
	$("#usr_modify").attr("href","javascript:fn_modify();");
	$("#usr_delete").attr("class","delete btn_s");
	$("#usr_delete").attr("href","javascript:fn_delete();");
	
	var del_yon = map.del_yon;
	if (del_yon == "N") {
		$("#usr_delete").attr("class","delete_disable btn_s");
		$("#usr_delete").attr("href","#void");
	}
}

function fn_insert() {
	
	var usr_id = $("#usr_id");
	if (usr_id.val() == "") {
		openAlertModal("WARNING","사용자ID를 입력하세요.");
		usr_id.focus();
		return;
	}
	
	var usr_ema = $("#usr_ema");
	if (usr_ema.val() == "") {
		openAlertModal("WARNING","이메일 주소를 입력하세요.");
		usr_ema.focus();
		return;
	}else if(usr_ema.val() != ""){
		if(!validateEmail(usr_ema.val())){
			 openAlertModal("WARNING","이메일을 정확히 입력하세요.");
			 $("#usr_ema").focus();
			 return;
		 }
	}
	
	var usr_pwd = $("#usr_pwd");
	if (usr_pwd.val() == "") {
		openAlertModal("WARNING","비밀번호를 입력하세요.");
		usr_pwd.focus();
		return;
	}
	var usr_pwd_vrf = $("#usr_pwd_vrf");
	if (usr_pwd_vrf.val() == "" || (usr_pwd.val() != usr_pwd_vrf.val())) {
		openAlertModal("WARNING","비밀번호가 일치하지 않습니다.");
		usr_pwd_vrf.focus();
		return;
	}
	
	usr_id.removeAttr('disabled');
	
	var usr_ath = $(":input:radio[name=n_usr_ath]:checked").val();
	loadingDiv.on();
	var comSubmit = new ComSubmit("frm");
    comSubmit.setUrl("/insertUsr");
    comSubmit.addParam("menu_cd", menu_cd);
    comSubmit.addParam("menu_id", menu_id);
    comSubmit.addParam("bms_id", bms_id);
    comSubmit.addParam("bts_id", bts_id);
    comSubmit.addParam("usr_ath", usr_ath);
    comSubmit.submit();
}
function fn_modify() {
// 	$("input[name=usr_id]").prop("readonly",true);
	
	var usr_id = $("#usr_id");
	if (usr_id.val() == "") {
		openAlertModal("WARNING","사용자ID를 입력하세요.");
		//$("#usr_id").attr('disabled','disabled');
		usr_id.focus();
		return;
	}
	var usr_ema = $("#usr_ema");
	if (usr_ema.val() == "") {
		openAlertModal("WARNING","이메일 주소를 입력하세요.");
		usr_ema.focus();
		return;
	}else if(usr_ema.val() != "" && !validateEmail(usr_ema.val())){
		 openAlertModal("WARNING","이메일을 정확히 입력하세요.");
		 return;
	}
	
	var usr_pwd = $("#usr_pwd");
	if (usr_pwd.val() == "") {
		openAlertModal("WARNING","비밀번호를 입력하세요."+ usr_pwd.val());
		usr_pwd.focus();
		return;
	}
	var usr_pwd_vrf = $("#usr_pwd_vrf");
	if (usr_pwd_vrf.val() == "" || (usr_pwd.val() != usr_pwd_vrf.val())) {
		openAlertModal("WARNING","비밀번호 확인이 일치하지 않습니다." + usr_pwd_vrf.val());
		usr_pwd_vrf.focus();
		return;
	}
	
	usr_id.removeAttr('disabled'); 
	
	var usr_ath = $(":input:radio[name=n_usr_ath]:checked").val();
	
	loadingDiv.on();
	var comSubmit = new ComSubmit("frm");
    comSubmit.setUrl("/updateUsr");
    comSubmit.addParam("menu_cd", menu_cd);
    comSubmit.addParam("menu_id", menu_id);
    comSubmit.addParam("bms_id", bms_id);
    comSubmit.addParam("bts_id", bts_id);
    comSubmit.addParam("usr_ath", usr_ath);
    comSubmit.submit();
}

function fn_delete() {
	
	var usr_id = $("#usr_id");
	if (usr_id.val() == "") {
		openAlertModal("WARNING","사용자ID를 선택하세요.");
		//$("#usr_id").attr('disabled','disabled');
		usr_id.focus();
		return;
	}
	
	usr_id.removeAttr('disabled');
	
	openConfirmModal("CONFIRM", "삭제하시겠습니까?", function(){
		$(".modal").remove();
		loadingDiv.on();
		var comSubmit = new ComSubmit();
	    comSubmit.setUrl("/deleteUsr");
	    comSubmit.addParam("menu_cd", menu_cd);
	    comSubmit.addParam("menu_id", menu_id);
	    comSubmit.addParam("bms_id", bms_id);
	    comSubmit.addParam("bts_id", bts_id);
	    comSubmit.addParam("usr_id", usr_id.val());
	    comSubmit.submit();
	});
}

function fn_clear() {
	
	$("#usr_id").removeAttr('disabled'); 
	$("#usr_id").val("");
	$("#usr_ema").val("");
	$("#usr_pwd").val("");
	$("#usr_pwd_vrf").val("");
	$("input[name='n_usr_ath'][value='2']").prop("checked", true);
	$("#usr_insert").text("저장");
	$("#usr_insert").attr("class","save btn_s");
	$("#usr_insert").attr("href","javascript:fn_insert();");
	$("#usr_modify").attr("class","modify_disable btn_s");
	$("#usr_modify").attr("href","#void");
	$("#usr_delete").attr("class","delete_disable btn_s");
	$("#usr_delete").attr("href","#void");
}

function fn_popclose() {
	layerClose2();
}


</script>
<!-- 내용 -->
<div class="content">
	<div class="box">
		<div class="box_title">사용자 정보</div>
		<form id="frm" name="frm">
		<dl class="box_content">
			<dt>사용자 ID<span class="required" >*</span></dt>
			<dd><input type="text" id="usr_id" name="usr_id" value="" required style="ime-mode:disabled;" maxlength="30" > </dd>
			<dt>비밀번호<span class="required">*</span></dt>
			<dd><input type="password" id="usr_pwd" name="usr_pwd" value="" required style="ime-mode:disabled;" maxlength="100"></dd>
			<dt>비밀번호 확인<span class="required">*</span></dt>
			<dd><input type="password" id="usr_pwd_vrf" name="usr_pwd_vrf" value="" required style="ime-mode:disabled;" maxlength="100"></dd>
			<dt>이메일 주소<span class="required" >*</span></dt>
			<dd><input type="text" id="usr_ema" name="usr_ema" value="" required style="ime-mode:disabled;" maxlength="50"></dd>
			<dt>사용자 권한</dt>
			<dd>
		<c:if test="${!empty usrAthlist && fn:length(usrAthlist) > 0}">
			<c:forEach var="row" items="${usrAthlist}" varStatus="status">
				<c:choose>
					<c:when test="${status.count eq fn:length(usrAthlist)}">
						<input type="radio" name="n_usr_ath" value="${row.com_cod }" checked>${row.com_cod_nam }
					</c:when>
					<c:otherwise>
						<input type="radio" name="n_usr_ath" value="${row.com_cod }">${row.com_cod_nam }
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</c:if>
			</dd>
		</dl>
		</form>
		<!-- 하단:버튼 -->
		<div class="bottom_btn">
			<a href="javascript:fn_insert();" class="save btn_s" id="usr_insert">저장</a>
			<a href="javascript:fn_modify();" class="modify btn_s" id="usr_modify">수정</a>
			<a href="javascript:fn_delete();" class="delete btn_s" id="usr_delete">삭제</a>
		</div>
		
		<!-- 하위:그리드 -->
		<div class="box_lowlank2 admin">
			<div class="list_title">사용자 리스트</div>
			<table class="table3">
				<colgroup>
					<col style="width:15%">
					<col style="width:15%">
					<col style="width:30%">
					<col style="width:20%">
					<col style="width:20%">
				</colgroup>
				<thead>
					<th>사용자 ID</th>
					<th>권한</th>
					<th>이메일 주소</th>
					<th>생성일</th>
					<th>수정일</th>
				</thead>
				<tbody>
				<c:choose>
	                <c:when test="${fn:length(list) > 0}">
	                    <c:forEach var="row" items="${list}" varStatus="status">
                    	<tr onclick="fn_usr_info('${row.usr_id }')">
                            <td><a href="#void">${row.usr_id }<c:if test="${row.del_yon eq 'N'}"><span class="required">*</span></c:if></a></td>
                            <td><a href="#void">${row.usr_ath_cdname }</a></td>
                            <td><a href="#void">${row.usr_ema }</a></td>
                            <td><a href="#void">${row.reg_dt }</a></td>
                            <td><a href="#void">${row.upd_dt }</a></td>
                        </tr>
	                    </c:forEach>  
	                </c:when>
	                <c:otherwise>
	                    <tr>
	                        <td colspan="5" class="nodata">조회된 결과가 없습니다.</td>
	                    </tr>
	                </c:otherwise>
				</c:choose>
				</tbody>
			</table>
			
			<!-- 페이징 -->
			<div class="paginationframe">
				<div class="pagination">
<!-- 			<div class="col-lg-12 col-xs-12 center"> -->
<%-- 					<c:if test="${not empty paginationInfo}"> --%>
<%-- 				    	<ui:pagination paginationInfo = "${paginationInfo}" type="custom" jsFunction="usr_refresh"/> --%>
<%-- 					</c:if> --%>
<!-- 					<input type="hidden" id="currentPageNo" name="currentPageNo" /> -->
					<c:if test="${pagination.startPage-1 != 0 }">
						<a href="javascript:usr_refresh(${pagination.startPage-1})">&laquo;</a>
					</c:if>
					<c:forEach var="i" begin="${pagination.startPage }" end="${pagination.endPage }" varStatus="status">
						<a <c:if test="${i eq pagination.currentPage }">class="active"</c:if> href="javascript:usr_refresh(${i})">${i}</a></li>
					</c:forEach>
					<c:if test="${pagination.endPage % pagination.pageCount == 0 && pagination.lastPage > pagination.endPage}">
						<a href="javascript:usr_refresh(${pagination.endPage+1})">&raquo;</a>
					</c:if>
				</div>
			</div>
			<!-- //페이징 -->
		</div> <!-- //하위:그리드 -->
		
	
	</div>
	
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
