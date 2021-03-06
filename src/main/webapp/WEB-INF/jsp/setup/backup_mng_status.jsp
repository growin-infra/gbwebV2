<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
var tSetting = {
		edit: {
			enable: true,
			showRemoveBtn: false,
			showRenameBtn: false
		},
		data: {
			keep: {
				parent:true,
				leaf:true
			},
			simpleData: {
				enable: true
			}
		},
		callback: {
			onClick: tClick
		}
	};

var tNodes =[];

$(document).ready(function() {
	$.fn.zTree.init($("#treeDemo"), tSetting, tNodes);
	$("#mng_svr_add").on("click", function(e){
		e.preventDefault();
		fn_bms_add();
	});
	$("#tgt_svr_add").on("click", function(e){
		e.preventDefault();
		fn_bts_add();
	});
	$("#bts_set").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_mv_set();
	});
	$("#bms_sve").bind("click", fn_bms_sve);
	$("#bms_del").bind("click", fn_bms_del);
	$("#bts_sve").bind("click", fn_bts_sve);
	$("#bts_del").bind("click", fn_bts_del);
	
	fn_init();
	
});

function fn_init() {
	
	$(".server_list").css("display","");
	$(".tit_list").css("display","none");
	$(".sub_list").css("display","none");
	
	bms_name = "";
	bts_name = "";
	
	fn_loadtree();
	
}

function tClick(event, treeId, treeNode, clickFlag) {
	if (treeNode.pId != null) {
		fn_treesearch(treeNode.id,treeNode.pId,treeNode.name,treeNode.name2);
	} else {
		fn_listtree(treeNode.id,treeNode.name2);
	}
}

function fn_loadtree() {
	loadingDiv.on();
	var comAjax = new ComAjax();
	comAjax.setUrl("/findTree");
	comAjax.setCallback("fn_treeload");
	comAjax.ajax();
}

function fn_treeload(data) {
	
	var list = data.list;
	$(".server_list").html("");
	var str = "";
	var list_str = "";
	if (list != null) {
		str = "[";
		list_str = "<dl>";
		for (var i=0; i<list.length; i++) {
			str += "{";
			var ip = "";
			if (list[i].ip != null && list[i].ip != "") {
				ip = list[i].ip
			}
			str += "'id':"+list[i].bmt_id+",'pId':"+list[i].bmt_pid+",'name':'"+list[i].bmt_nam+(ip != "" ? " ("+ip+")":"")+"','name2':'"+list[i].bmt_nam+"'";
			if (i == 0) str += ",'open':'true','isParent':'true'";
			
			if (list[i].bmt_lvl == 0) {
				list_str +="<dt>&middot; "+list[i].bmt_nam+(ip != "" ? " ("+ip+")":"")+"</dt>";
				str += ",'isParent':'true'";
			}
			
			str += "}";
			if ((i+1 != list.length)) str += ",";
		}
		str += "]";
		list_str += "</dl>";
	} else {
		str = "[{'id':1,'pId':0,'name':'????????????','open':'true','isParent':'true'}]";
	}
	$.fn.zTree.init($("#treeDemo"), tSetting, eval(str));
	
	$(".server_list").html(list_str);
	$('.change_text').html('???????????? ?????????');
	
}

function fn_listtree(pid,name) {
	loadingDiv.on();
	
	if (new String(name).valueOf() == "undefined") {
		$('.change_text').html("???????????? ?????????");
	} else {
		bms_name = name;
		$('.change_text').html(name +" > ???????????? ?????????");
	}
	
	$("#bms_pid").val(pid);
	
	var comAjax = new ComAjax();
	comAjax.setUrl("/findTree");
	comAjax.setCallback("fn_listtreeload");
	comAjax.addParam("bmt_pid", pid);
	comAjax.addParam("bmt_lvl", 1);
	comAjax.ajax();
}
function fn_listtreeload(data) {
	
	$(".server_list").css("display","");
	$(".tit_list").css("display","none");
	$(".sub_list").css("display","none");
	
	bms_name = "";
	bts_name = "";
	
	$(".server_list").html("");
	var list = data.list;
	var list_str = "";
	if (list != null) {
		list_str = "<dl>";
		for (var i=0; i<list.length; i++) {
			var ip = "";
			if (list[i].ip != null && list[i].ip != "") {
				ip = list[i].ip
			}
			if (list[i].bmt_lvl == 1) {
				list_str +="<dt>&middot; "+list[i].bmt_nam+(ip != "" ? " ("+ip+")":"")+"</dt>";
			}
		}
		list_str += "</dl>";
	}
	if (list_str == "<dl></dl>") {
		$(".server_list").html("<dt>????????? ????????? ????????????.</dt>");
	} else {
		$(".server_list").html(list_str);
	}
	
	if (data.msmap != null) {
		var map = data.msmap;
		$("#bms_dir_sve").val(map.ms_sve_dir);
	}
}


var bms_name = "",bts_name = "";
function fn_treesearch(id,pid,name,name2) {
	
	loadingDiv.on();
	var comAjax = new ComAjax();
	
	if (pid < 100) {	//1 level
		
		bms_name = name2;
		
		$('.change_text').html("???????????? : "+name2+"<div class='floatR btnWrap'><a href='#void' class='rad50 bgc_sky' onclick='fn_sconn_test();'>SSH???????????????</a></div>");
		
		comAjax.setUrl("/findTreeLv1");
		comAjax.setCallback("fn_treelv1");
		comAjax.addParam("id", id);
		comAjax.addParam("bms_id", id);
		comAjax.addParam("bms_pid", pid);
	
	} else {	//2 level
		
		bts_name = name2;
	
		$('.change_text').html("???????????? : "+name2+"<div class='floatR btnWrap'><a href='#void' class='rad50 bgc_sky' onclick='fn_conn_test();'>DB???????????????</a></div>");
		
		comAjax.setUrl("/findTreeLv2");
		comAjax.setCallback("fn_treelv2");
		comAjax.addParam("id", id);
		comAjax.addParam("bmt_id", pid);
		comAjax.addParam("bmt_lvl", 1);
		comAjax.addParam("bts_id", id);
		comAjax.addParam("bts_pid", pid);
		comAjax.addParam("bms_id", pid);
	}
	comAjax.ajax();
}

function fn_treelv1(data) {
	$("#bts_ip").val("");
	$('.change_text').html("???????????? : "+bms_name+"<div class='floatR btnWrap'><a href='#void' class='rad50 bgc_sky' onclick='fn_sconn_test();'>SSH???????????????</a></div>");

	if (data.bmmap != null) {
		var map = data.bmmap;
		$("#bms_nam").val(map.bms_nam);
		$("#bms_ip").val(map.bms_ip);
		$("#bms_pot").val(map.bms_pot);
		$("#bms_usr").val(map.bms_usr);
		$("#bms_pwd").val(map.bms_pwd);
		$("#bts_ip").val(map.bms_ip);
		if (data.msmap != null) {
			var sve = data.msmap;
			$("#bms_dir_sve").val(sve.ms_sve_dir);
		}
		
		$("#bms_id").val(map.bms_id);
		$("#bms_pid").val(map.bms_pid);
		
		if ($("#bms_nam").val() != "" && $("#bms_nam").val() != null) {
			$("#bms_nam").attr('disabled','disabled');
		}
		$("#bms_dir_sve").attr('disabled','disabled');
		
	} else {
		fn_initinput("bms");
	}
	
	$(".server_list").css("display","none");
	$(".tit_list").css("display","");
	$(".sub_list").css("display","none");
	
}

function fn_treelv2(data) {
	
	var list = data.list;
	if (list != null) {
		$('.change_text').html("???????????? : "+list[0].bmt_nam+" > "+bts_name+"<div class='floatR btnWrap'><a href='#void' class='rad50 bgc_sky' onclick='fn_conn_test();'>DB???????????????</a></div>");
		bms_name = list[0].bmt_nam;
	}
	if (data.btmap != null) {
		var map = data.btmap;
		$("#bts_nam").val(map.bts_nam);
		$("#bts_ip").val(map.bts_ip);
		$("#bts_pot").val(map.bts_pot);
		$("#bts_usr").val(map.bts_usr);
		$("#bts_pwd").val(map.bts_pwd);
		if (map.bts_mlt_ins == "Y") {
			$("input:radio[name='mul_ins'][value='Y']").prop('checked', true);
		} else {
			$("input:radio[name='mul_ins'][value='N']").prop('checked', true);
		}
		
		$("#bts_id").val(map.bts_id);
		$("#bts_pid").val(map.bts_pid);
		$("#bms_id").val(map.bts_pid);
		if (data.ms_id != null) {
			$("#bmt_id").val(data.ms_id);
		}
		if (data.bms_nam != null) {
			bms_name = data.bms_nam;
		}
		
		if ($("#bts_nam").val() != "" && $("#bts_nam").val() != null) {
			$("#bts_nam").attr('disabled','disabled');
		}
		
		$("#bts_set").attr("class","modify btn_s");
		$("#bts_del").attr("class","delete btn_s");
	} else {
		fn_initinput("bts");
	}
	
	$(".server_list").css("display","none");
	$(".tit_list").css("display","none");
	$(".sub_list").css("display","");
	
}

function fn_before_chk(d) {
	var result = true;
	var param = new Object();
	param.dvd = d;
    $.ajax({
        url : "/lcn_chk",
		data : param,
        type : "POST",
        async : false,
        success : function(data, status) {
        	loadingDiv.off();
        	var map = data.lcnInfoMap;
        	if (map != null) {
	        	if (map.msg != "") {
	        		openAlertModal("WARNING",map.msg);
	        		result = false;
	        	}
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
			result = false;
		}
    });
	return result;
}

function fn_sconn_test() {
	if ($("#bms_usr").val() == "" || $("#bms_pwd").val() == "" || $("#bms_ip").val() == "" || $("#bms_pot").val() == "") {
		openAlertModal("WARNING","?????? ????????? ???????????????.");
		return;
	}
	var param = new Object();
	param.bms_id = $("#bms_id").val();
	param.id = $("#bms_usr").val();
	param.pwd = $("#bms_pwd").val();
	param.ip = $("#bms_ip").val();
	param.pot = $("#bms_pot").val();
    $.ajax({
        url : "/ssh_conn_test2",
		data : param,
        type : "POST",
        async : false,
        success : function(data, status) {
        	loadingDiv.off();
        	var result = data.result;
        	if (result) {
        		openAlertModal("SUCCESS","?????? ??????!!");
        	} else {
        		openAlertModal("WARNING","?????? ??????!!");
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

function fn_conn_test() {
	if ($("#bts_pot").val() == "" || $("#bts_usr").val() == "" || $("#bts_pwd").val() == "") {
		openAlertModal("WARNING","???????????? ????????? ???????????????.");
		return;
	}
	
	var param = new Object();
	param.bms_id = $("#bms_id").val();
	param.bts_ip = $("#bts_ip").val();
	param.bts_pot = $("#bts_pot").val();
	param.bts_usr = $("#bts_usr").val();
	param.bts_pwd = $("#bts_pwd").val();
    $.ajax({
        url : "/conn_test",
		data : param,
        type : "POST",
        async : false,
        success : function(data, status) {
        	loadingDiv.off();
        	var result = data.result;
        	if (result) {
        		openAlertModal("SUCCESS","?????? ??????!!");
        	} else {
        		openAlertModal("WARNING","?????? ??????!!");
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

function fn_bms_add() {
	
	if ($("#bms_pid").val() != null && $("#bms_pid").val() != "") {
		$("#bms_dir_sve").attr('disabled','disabled');
		$(".server_list").css("display","none");
		$(".tit_list").css("display","");
		$(".sub_list").css("display","none");
		
		$("#bms_nam").removeAttr('disabled');
		$("#bms_dir_sve").removeAttr('disabled');
		
		$('.change_text').html('???????????? ??????');
		
		$("#bmt_id").val($("#bms_pid").val());
		
		fn_initinput("bms");
	} else {
		openAlertModal("WARNING","??????????????? ??????????????????.");
	}
}

function fn_bts_add() {
	
	if (!fn_before_chk("svr")) return;
	
	if (bms_name != null && bms_name != "") {
		$(".server_list").css("display","none");
		$(".tit_list").css("display","none");
		$(".sub_list").css("display","");
		$('.change_text').html(bms_name+' > ???????????? ??????'+"<div class='floatR btnWrap'><a href='#void' class='rad50 bgc_sky' onclick='fn_conn_test();'>DB???????????????</a></div>");
		$("#bmt_id").val($("#bms_pid").val());
		$("#bts_nam").removeAttr('disabled');
		fn_initinput("bts");
		
	} else {
		openAlertModal("WARNING","??????????????? ??????????????????.");
	}
}

function fn_bms_sve() {
	
	var bms_nam = $("#bms_nam");
	var bms_ip = $("#bms_ip");
	var bms_pot = $("#bms_pot");
	var bms_usr = $("#bms_usr");
	var bms_pwd = $("#bms_pwd");
	var bms_dir_sve = $("#bms_dir_sve");
	var bmt_id = $("#bmt_id");
	
	if (bms_nam.val() == "") {
		openAlertModal("WARNING","?????????????????? ???????????????.");
		bms_nam.focus();
		return;
	}
	
	loadingDiv.on();
	$("#bms_nam").removeAttr('disabled');
	
	$("#bms_dir_sve").removeAttr('disabled');
	
	var comAjax = new ComAjax();
	comAjax.setUrl("/insertTreeLv1");
	comAjax.setCallback("insertTreeLv1");
	comAjax.addParam("bmt_nam", bms_nam.val());
	comAjax.addParam("bmt_lvl", 1);
	if ($("#bms_id").val() != "") {
		comAjax.addParam("bms_id", $("#bms_id").val());
	}
	if ($("#bms_pid").val() != "") {
		comAjax.addParam("bms_pid", $("#bms_pid").val());
	}
	comAjax.addParam("bms_nam", bms_nam.val());
	comAjax.addParam("bms_ip", bms_ip.val());
	comAjax.addParam("bms_pot", bms_pot.val());
	comAjax.addParam("bms_usr", bms_usr.val());
	comAjax.addParam("bms_pwd", bms_pwd.val());
	comAjax.addParam("bms_dir_sve", bms_dir_sve.val());
	comAjax.addParam("bmt_id", bmt_id.val());
	comAjax.ajax();
}
function insertTreeLv1(data) {
	var result = data.result;
	var nodename = data.nodename;
	if (result) {
		openAlertModal("SUCCESS",nodename+" ??????????????? ?????????????????????.");
		fn_init();
	} else {
		openAlertModal("FAIL", nodename+" ???????????? ????????? ?????? ???????????????.");
	}
}

function fn_bms_del() {
	openConfirmModal("CONFIRM", "?????? ??????????????? ?????? ??? ??????????????? ?????? ?????????.\n?????????????????????????", function(){
		loadingDiv.on();
		$("#bms_nam").removeAttr('disabled');
		
		var comAjax = new ComAjax();
		comAjax.setUrl("/deleteTreeLv1");
		comAjax.setCallback("deleteTreeLv1");
		comAjax.addParam("bmt_id", $("#bms_id").val());
		comAjax.addParam("bms_id", $("#bms_id").val());
		comAjax.addParam("bts_pid", $("#bms_id").val());
		comAjax.addParam("bmt_pid", $("#bms_id").val());
		comAjax.addParam("bms_nam", $("#bms_nam").val());
		comAjax.addParam("knd", "G01");
		comAjax.ajax();
	});
}
function deleteTreeLv1(data) {
	$(".modal").remove();
	var result = data.result;
	var nodename = data.nodename;
	if (result == "success") {
		openAlertModal("SUCCESS",nodename+" ??????????????? ?????????????????????.");
		fn_init();
	} else {
		openAlertModal("FAIL", nodename+" ???????????? ????????? ?????? ???????????????.");
	}
}


function fn_bts_sve() {
// 	alert($("#bmt_id").val());
// 	return;
	loadingDiv.on();
	
	$("#bts_nam").removeAttr('disabled');
	//$("#bts_ip").removeAttr('disabled');
	
	var comAjax = new ComAjax();
	comAjax.setUrl("/insertTreeLv2");
	comAjax.setCallback("insertTreeLv2");
	if ($("#bts_id").val() != "") {
		comAjax.addParam("bts_id", $("#bts_id").val());
	}
	if ($("#bts_pid").val() != "") {
		comAjax.addParam("bts_pid", $("#bts_pid").val());
	}
	comAjax.addParam("bms_id", $("#bms_id").val());
	comAjax.addParam("bts_pid", $("#bms_id").val());
	comAjax.addParam("bmt_nam", $("#bts_nam").val());
	comAjax.addParam("bmt_lvl", 2);
	comAjax.addParam("bmt_id", $("#bmt_id").val());
	comAjax.addParam("bts_nam", $("#bts_nam").val());
	comAjax.addParam("bts_ip", $("#bts_ip").val());
	comAjax.addParam("bts_pot", $("#bts_pot").val());
	comAjax.addParam("bts_usr", $("#bts_usr").val());
	comAjax.addParam("bts_pwd", $("#bts_pwd").val());
	comAjax.addParam("bts_mlt_ins", $("input:radio[name='mul_ins']:checked").val());
	comAjax.ajax();
}
function insertTreeLv2(data) {
	var result = data.result;
	var nodename = data.nodename;
	if (result) {
		openAlertModal("SUCCESS",nodename+" ??????????????? ?????????????????????.");
		$("#bts_set").attr("class","modify btn_s");
		$("#bts_del").attr("class","delete btn_s");
		fn_init();
	} else {
		openAlertModal("FAIL", nodename+" ???????????? ????????? ?????? ???????????????.");
	}
}

function fn_bts_del() {
	
	openConfirmModal("CONFIRM", "?????? ??? ???????????? ?????? ?????? ????????? ???????????????.\n?????????????????????????", function(){
		loadingDiv.on();
		
		$("#bts_nam").removeAttr('disabled');
		
		var comAjax = new ComAjax();
		comAjax.setUrl("/deleteTreeLv2");
		comAjax.setCallback("deleteTreeLv2");
		comAjax.addParam("bmt_id", $("#bts_id").val());
		comAjax.addParam("bms_id", $("#bms_id").val());
		comAjax.addParam("bts_id", $("#bts_id").val());
		comAjax.addParam("bms_nam", bms_name);
		comAjax.addParam("bts_nam", $("#bts_nam").val());
		comAjax.addParam("knd", "G01");
		comAjax.ajax();
	});	
}

function fn_mv_set() {
	if ($("#bms_id").val() != "" && $("#bts_id").val() != "" && $("#bmt_id").val() != "") {
		fn_b_rec($("#bms_id").val(),$("#bts_id").val(),$("#bmt_id").val());
	}
}

function deleteTreeLv2(data) {
	$(".modal").remove();
	var nodename = data.nodename;
	var result = data.result;
	
	var nodename = data.nodename;
	if (result == "success") {
		openAlertModal("SUCCESS",nodename+" ??????????????? ?????????????????????.");
		fn_init();
	} else {
		openAlertModal("FAIL", nodename+" ???????????? ????????? ?????? ???????????????.");
	}
}

function fn_initinput(dvd) {
	if (dvd == "bms") {
		$("#bms_dir_sve").attr('disabled','disabled');
		$("#bms_nam").val("");
		$("#bms_ip").val("");
		$("#bms_pot").val("22");
		$("#bms_usr").val("");
		$("#bms_pwd").val("");
		
		$("#bms_id").val("");
		$("#bms_pid").val("");
	} else if (dvd == "bts") {
		$("#bts_nam").val("");
		//$("#bts_ip").attr('disabled','disabled');
		
		$("#bts_pot").val("3306");
		$("#bts_usr").val("");
		$("#bts_pwd").val("");
		$("input:radio[name='mul_ins'][value='N']").prop('checked', true);

		$("#bts_id").val("");
		$("#bts_pid").val("");
		
		$("#bts_set").attr("class","modify_disable btn_s");
		$("#bts_del").attr("class","delete_disable btn_s");
	}
}

function fn_popclose() {
	fn_init();
	layerClose2();
}

</script>
<!-- ?????? -->
<div class="content">
<input type="hidden" id="bts_ip" name="bts_ip" value="">
	<!-- ?????? -->
	<div class="title">
		<h2>??????????????????</h2>		
	</div>
	<!-- ??? -->
	<table class="table1 top" >
		<colgroup>
			<col style="width:50%;"> <!-- ???????????? -->
			<col style="width:50%;"> <!-- ???????????? -->
		</colgroup>
		<thead>
			<th>
				<div class="thpad">
					<div class="floatL">???????????? ??????</div>
					<div class="floatR btnWrap">
<% if ("AT01".equals(usr_ath)) { %>
						<a href="#void" class="rad50 bgc_gun" id="mng_svr_add">??????????????????</a>
						<a href="#void" class="rad50 bgc_sky" id="tgt_svr_add">??????????????????</a>
<% } %>
					</div>
				</div>
			</th>
			<th class="change_text" id="db_conn_test">???????????? ?????????</th>
		</thead>
		<tbody>
			<tr>
				<!-- ?????? ???????????? -->
				<td>
					<div class="zTreeDemoBackground left">
						<ul id="treeDemo" class="ztree"></ul>
					</div>
				</td>

				<!-- ?????? ????????? -->
				<td>
					<!-- ???????????? ?????? -->
					<div class="server_list">
					</div>
					<!-- ???????????? ?????? -->
					<div class="tit_list">
						<!-- ???????????? ?????? ?????? -->
						<div class="right_part">
							<div>???????????? ?????? ??????</div>
							<dl>
								<dt>???????????????<span class="required" >*</span></dt>
								<dd><input id="bms_nam" type="text" value="" style="ime-mode:disabled;" maxlength="30"></dd>
								<dt>???????????? IP</dt>
								<dd><input id="bms_ip" type="text" value="127.0.0.1" style="ime-mode:disabled;" maxlength="15"></dd>
								<dt>???????????? Port</dt>
								<dd><input id="bms_pot" type="text" value="1111" onkeypress="return fn_press(event, 'numbers');" style="ime-mode:disabled;" maxlength="5"></dd>
								<dt>?????????</dt>
								<dd><input id="bms_usr" type="text" value="abcd" style="ime-mode:disabled;" maxlength="30"></dd>
								<dt>????????????</dt>
								<dd><input id="bms_pwd" type="password" value="abcd" style="ime-mode:disabled;" maxlength="100"></dd>
							</dl>
						</div> <!-- //???????????? ?????? ?????? -->
						
						<!-- ???????????? DISK ?????? -->
						<div class="right_part">
							<div>???????????? DISK ??????</div>
							<dl>
								<dt>?????? ??????</dt>
								<dd><input id="bms_dir_sve" name="bms_dir_sve" type="text" value="" maxlength="100"></dd>
							</dl>
						</div> <!-- //???????????? DISK ?????? -->
						
						<!-- ??????:?????? -->
<% if ("AT01".equals(usr_ath)) { %>
						<div class="bottom_btn">
							<a href="#void" class="save btn_s" id="bms_sve">??????</a>
							<a href="#void" class="delete btn_s" id="bms_del">??????</a>
						</div> <!-- //??????:?????? -->
<% } %>
					</div> <!-- //???????????? ?????? -->
					
					<!-- DB?????? ?????? -->
					<div class="sub_list">
						<div class="right_part">
							<div>???????????? ??????</div>
							<dl>
								<dt>???????????????<span class="required" >*</span></dt>
								<dd><input id="bts_nam" type="text" value="db01" maxlength="100"></dd>
								<!--  
								<dt>???????????? IP</dt>
								<dd><input id="bts_ip" type="text" value="127.0.0.1" style="ime-mode:disabled;" maxlength="15" disabled></dd>
								-->
								
								<dt>???????????? Port</dt>
								<dd><input id="bts_pot" type="text" value="1111" onkeypress="return fn_press(event, 'numbers');" style="ime-mode:disabled;" maxlength="5"></dd>
								<dt>?????????</dt>
								<dd><input id="bts_usr" type="text" value="maria" style="ime-mode:disabled;" maxlength="30"></dd>
								<dt>????????????</dt>
								<dd><input id="bts_pwd" type="password" value="abcd" style="ime-mode:disabled;" maxlength="100"></dd>
							</dl>
						</div>
						<div class="right_part">
							<div class="untitle"></div>
							<dl>
<!-- 								<dt>???????????? Display</dt> -->
<!-- 								<dd><input type="radio" value="on" name="agree">ON <input type="radio" value="off" name="agree">OFF</dd> -->
								<dt>Multi Instance</dt>
								<dd><input type="radio" value="Y" name="mul_ins">Yes <input type="radio" value="N" name="mul_ins">No</dd>
							</dl>
						</div>
						
						<!-- ??????:?????? -->
						<div class="bottom_btn">
<% if ("AT01".equals(usr_ath)) { %>
							<a href="#void" class="save btn_s" id="bts_sve">??????</a>
							<a href="#void" class="delete btn_s" id="bts_del">??????</a>
<% } %>
							<a href="#void" class="modify btn_s" id="bts_set">??????</a>
						</div> <!-- //??????:?????? -->
					</div> <!-- //DB?????? ?????? -->
				</td>
				
			</tr>
		</tbody>
	</table><!-- //??? -->
</div><!-- //?????? -->

<input type="hidden" id="bts_id" name="bts_id" />
<input type="hidden" id="bts_pid" name="bts_pid" />
<input type="hidden" id="bms_id" name="bms_id" />
<input type="hidden" id="bms_pid" name="bms_pid" />
<input type="hidden" id="bmt_id" name="bmt_id" />

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
