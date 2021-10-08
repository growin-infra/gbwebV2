<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/include/header.jsp" %>
<script type="text/javascript">
$(document).ready(function(){
	
	//기간설정
	$("#id_pod_set").change(function(){
		var idx = $("#id_pod_set option:selected").index();
		psComboset(idx,"");
	});
	
	//보관기간
	$("#id_kep_pod").change(function(){
		//var idx = $("#id_kep_pod option:selected").index();
		var val = $("#id_kep_pod option:selected").val();
		kpComboset(val,"");
	});
	
	//스케줄정보
	$("#id_scd_nam").change(function(){
		//var idx = $("#id_scd_nam option:selected").index();
		var val = $("#id_scd_nam").val();
		if (val != "" && val != "신규 생성") {
			fn_sch(val);
		} else {
			fn_btn_init("s");
			fn_combo_init();
		}
	});
	
	$("#pod_set_itm_week_sall").click(function(){
		if ($("#pod_set_itm_week_sall").prop("checked")) {
			$("input:checkbox[name='pod_set_itm_week']").prop("checked",true);
		} else {
			$("input:checkbox[name='pod_set_itm_week']").prop("checked",false);
		}
	});
	
	$("#pod_set_itm_mon_sall").click(function(){
		if ($("#pod_set_itm_mon_sall").prop("checked")) {
			$("input:checkbox[name='pod_set_itm_mon']").prop("checked",true);
		} else {
			$("input:checkbox[name='pod_set_itm_mon']").prop("checked",false);
		}
	});
	
// 	$("#scd_save").on("click", function(e){
// 		e.preventDefault();
// 		fn_scd_save();
// 	});
	
	fn_init();
	
});
function fn_init() {
	
	fn_btn_init("s");
	
	fn_combo_init();
	
	var ps_idx = $("#id_pod_set option:selected").index();
	psComboset(ps_idx,"");
		
	var val = $("#id_kep_pod").val();
	kpComboset(val,"");
	
}

function psComboset(idx,itm) {
	
	if (idx == 1) {	//Daily
	 	$("#pod_select_div").attr("disabled","disabled");
		$("#pod_select_div").hide();
	} else if (idx == 2) {	//Weekly
		$("#pod_select_div").removeAttr("disabled");
		$(".box_title").html("주단위 선택");
		$("#pod_select_div").show();
		
		$("#id_week").show();
		$("#id_month").hide();
		
		if (itm != null && itm != "") {
			$("input:checkbox[name='pod_set_itm_week']").prop("checked",false);
			var itmarr = itm.split(",");
			for (var j=0; j<itmarr.length; j++) {
				$('input:checkbox[name="pod_set_itm_week"]').each(function() {
					if (this.value == itmarr[j]) {
						this.checked = true;
				    }
				});
			}
		}
		
	} else if (idx == 3) {	//Monthly
		$("#pod_select_div").removeAttr("disabled");
		$(".box_title").html("일단위 선택");
		$("#pod_select_div").show();
		
		$("#id_week").hide();
		$("#id_month").show();
		
		if (itm != null && itm != "") {
			$("input:checkbox[name='pod_set_itm_mon']").prop("checked",false);
			var itmarr = itm.split(",");
			for (var j=0; j<itmarr.length; j++) {
				$('input:checkbox[name="pod_set_itm_mon"]').each(function() {
					if (this.value == itmarr[j]) {
						this.checked = true;
				    }
				});
			}
		}
		
	} else {
		$("#id_pod_set option:eq(0)").prop("selected", true);
	 	$("#pod_select_div").attr("disabled","disabled");
		$("#pod_select_div").hide();
	}
}

function kpComboset(val,itm) {
	if (val == "0") {	//직접입력
		$("#id_kep_pod_itm").prop("disabled",false);
		$("#id_kep_pod_itm").val(itm);
		$("#id_kep_pod_itm").focus();
	} else if (val == "") {
		$("#id_kep_pod_itm").prop("disabled",true);
		$("#id_kep_pod_itm").val("");
	} else {
		$("#id_kep_pod").val(val);
		$("#id_kep_pod_itm").prop("disabled",true);
		$("#id_kep_pod_itm").val("");
	}
}

function fn_schlist() {
	
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_b_scd");
    comSubmit.addParam("menu_cd", "A01020204");
    comSubmit.addParam("bms_id", bms_id);
    comSubmit.addParam("bts_id", bts_id);
    comSubmit.submit();
}

function fn_sch(val) {
	
	var comAjax = new ComAjax();
	comAjax.setUrl("/set_b_scdmap");
	comAjax.setCallback("fn_schload");
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("scd_nam", val);
	comAjax.ajax();
}
function fn_schload(data) {
	var map = data.scdmap;
	if (map != null && map != "" && new String(map).valueOf() != "undefined") {
		var list = data.scdlist;
		var tmp = "";
		var o = $("#id_scd_nam");
		o.html("");
		if (list != null) {
			tmp = "<option value='''>신규 생성</option>";
			for (var i=0; i<list.length; i++) {
				tmp += "<option value='"+list[i].scd_nam+"' >"+list[i].scd_nam+"</option>";
			}
		}
		o.append(tmp);
		$("#id_scd_nam").val(map.scd_nam);
		
		//백업레벨
		$("#id_scd_lvl").val(map.scd_lvl);
		
		//기간설정
		if (map.pod_set != "" && map.pod_set != null && new String(map.pod_set).valueOf() != "undefined") {
			$("#id_pod_set").val(map.pod_set);
			var idx = $("#id_pod_set option:selected").index();
			psComboset(idx,map.pod_set_itm == "" ? "":map.pod_set_itm);
		}
		
		//시간설정
		if (map.tme_set == "" || map.tme_set == null || new String(map.tme_set).valueOf() == "undefined") {
			$("#id_tme_set_h option:eq(0)").prop("selected", true);
			$("#id_tme_set_m option:eq(0)").prop("selected", true);
		} else {
			var h = map.tme_set.substr(0,2);
			var m = map.tme_set.substr(2,2);
			$("#id_tme_set_h").val(h);
			$("#id_tme_set_m").val(m);
		}
		
		//보관기간
		var kep_pod = map.kep_pod;
		if (kep_pod == "" || kep_pod == null || new String(kep_pod).valueOf() == "undefined") {
			$("#id_kep_pod_itm").attr("disabled","disabled");
			$("#id_kep_pod option:eq(0)").prop("selected", true);
		} else {
			var c = false;
			var iKepPodarr = [1,3,7,30,90,365];
			for (var i=0; i<iKepPodarr.length; i++) {
				if (iKepPodarr[i] == kep_pod) {
					c = true;
				}
			}
			if (c) {
				kpComboset(kep_pod,map.kep_pod_itm);
			} else {
				$("#id_kep_pod").val("0");
				$("#id_kep_pod_itm").prop("disabled",false);
				$("#id_kep_pod_itm").val(kep_pod);
			}
			
			
		}
		
		fn_btn_init("m");
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
function fn_scd_save() {
	
	fn_setChk();
	
	if (!is_set_yn) {
		openAlertModal("WARNING","백업설정및 백업방법 정보가 없습니다.");
		return;
	}
	
	var cron_name = "";
	
	//백업레벨
	var scd_lvl = $("#id_scd_lvl").val();
	if (scd_lvl == "") {
		openAlertModal("WARNING","백업레벨을 선택하세요");
		$("#id_scd_lvl").focus();
		return;
	} else {
		if (scd_lvl == "L01") {
			cron_name = "full";
		} else if (scd_lvl == "L02") {
			cron_name = "incr";
		}
	}
	
	
	//기간설정, 기간설정 아이템
	//id_pod_set, 
	var pod_set = $("#id_pod_set").val();
	if (pod_set == "") {
		openAlertModal("WARNING","기간설정을 선택하세요");
		$("#id_pod_set").focus();
		return;
	}
	
	//시간설정
	var tme_set_h = $("#id_tme_set_h").val();
	var tme_set_h_c = tme_set_h.replace(/(^0+)/, "");
	tme_set_h_c = tme_set_h_c == "" ? "0" : tme_set_h_c;
	
	
	var tme_set_m = $("#id_tme_set_m").val();
	var tme_set_m_c = tme_set_m.replace(/(^0+)/, "");
	tme_set_m_c = tme_set_m_c == "" ? "0" : tme_set_m_c;
	
	var tme_set = tme_set_h + tme_set_m;
    var tme_set_fom = tme_set_h+"시 " + tme_set_m+"분";
	cron_name += "_" + tme_set_h_c + "_" +tme_set_m_c;
	
	var scd_con = "";
	var pod_set_itm = "";
	var pod_set_itm_name = "";
	if (pod_set == "SP01") {
		scd_con = tme_set_m_c+" "+tme_set_h_c+" * * *";
		cron_name += "_0_00" ;
	} else if (pod_set == "SP02") {
		cron_name += "_1_";
		$("input[name='pod_set_itm_week']:checked").each(function() {
			pod_set_itm += $(this).val()+"," ;
			if ($(this).val() == 0) {
				cron_name += "00";
				pod_set_itm_name += "일," ;
			} else if ($(this).val() == 1) {
				cron_name += "01";
				pod_set_itm_name += "월," ;
			} else if ($(this).val() == 2) {
				cron_name += "02";
				pod_set_itm_name += "화," ;
			} else if ($(this).val() == 3) {
				cron_name += "03";
				pod_set_itm_name += "수," ;
			} else if ($(this).val() == 4) {
				cron_name += "04";
				pod_set_itm_name += "목," ;
			} else if ($(this).val() == 5) {
				cron_name += "05";
				pod_set_itm_name += "금," ;
			} else if ($(this).val() == 6) {
				cron_name += "06";
				pod_set_itm_name += "토," ;
			}
		});
		pod_set_itm = pod_set_itm.substring(0,pod_set_itm.lastIndexOf(","));
		if(pod_set_itm == ""){
			openAlertModal("WARNING","요일을 선택하세요");
			return;
		};
		scd_con = +tme_set_m_c+" "+tme_set_h_c+" * * "+pod_set_itm;
	} else if (pod_set == "SP03") {
		cron_name += "_2_"
		$("input[name='pod_set_itm_mon']:checked").each(function() {
			cron_name += $(this).val() ;
			pod_set_itm += $(this).val()+"," ;
		});
		
		pod_set_itm = pod_set_itm.substring(0,pod_set_itm.lastIndexOf(","));
		scd_con = tme_set_m_c+" "+tme_set_h_c+" "+pod_set_itm+" * *";
		if(pod_set_itm == ""){
			openAlertModal("WARNING","날짜를 선택하세요.")
			return;
		}
		
	}
	pod_set_itm_name = pod_set_itm_name.substring(0,pod_set_itm_name.lastIndexOf(","));
	
	//보관기간
	var kep_pod = $("#id_kep_pod").val();
	var kep_pod_itm = $("#id_kep_pod_itm").val();
	
	if (kep_pod == "") {
		if (kep_pod_itm == "") {
			openAlertModal("WARNING","보관기간을 선택하세요");
	    	return;
		}
	} else {
		if (parseInt(kep_pod) == 0) {
			if (parseInt(kep_pod_itm) == 0 || kep_pod_itm == "") {
				openAlertModal("WARNING","보관기간을 선택하세요");
		    	return;
			}
		}
	}
	
	//스케줄명
	var scd_nam = $("#id_scd_lvl option:selected").text();
	scd_nam += " "+tme_set_h+":"+tme_set_m;
	scd_nam += " "+$("#id_pod_set option:selected").text();
	if (pod_set == "SP02") {
		scd_nam += " "+pod_set_itm_name;
	} else if (pod_set == "SP03") {
		scd_nam += " "+pod_set_itm;
	}
	
	var bts_nam = "<c:out value='${bts_nam}'/>";
	var bms_nam = "<c:out value='${bms_nam}'/>";
	
	var comAjax = new ComAjax();
	comAjax.setUrl("/insertBakScd");
	comAjax.setCallback("fn_cb_insert");
	comAjax.addParam("menu_cd", menu_cd);
	comAjax.addParam("menu_id", menu_id);
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("bts_nam", bts_nam);
	comAjax.addParam("bms_nam", bms_nam);
	comAjax.addParam("exe_tme", tme_set_fom);
	comAjax.addParam("exe_pod", pod_set);
	comAjax.addParam("pod_set", pod_set);
	comAjax.addParam("lvl", scd_lvl);
	comAjax.addParam("scd_lvl", scd_lvl);
	comAjax.addParam("pod_set_itm", pod_set_itm);
	comAjax.addParam("tme_set", tme_set);
	if (kep_pod != "" && kep_pod != "0") {
		comAjax.addParam("kep_pod", kep_pod);
	}
	if (kep_pod_itm != "" && kep_pod_itm != "0") {
		comAjax.addParam("kep_pod", kep_pod_itm);
	}
	comAjax.addParam("scd_nam", scd_nam);
	comAjax.addParam("scd_con", scd_con);
	comAjax.addParam("cron_name", cron_name);
	comAjax.ajax();
}
function fn_cb_insert(data) {
	var result = data.result;
	var name = data.scd_nam;
	if (result) {
		openAlertModal("SUCCESS",name+" 스케줄정보가 저장되었습니다.");
 		$("#id_scd_nam").append("<option value='"+name+"'>"+name+"</option>");
		fn_sch(name);
	} else {
		openAlertModal("FAIL",name+" 스케줄정보 저장이 실패 되었습니다.");
	}
}

function fn_scd_modify() {
	var cron_name = "";
	
	var scd_nam = $("#id_scd_nam").val();
	
	//백업레벨
	var scd_lvl = $("#id_scd_lvl").val();
	if (scd_lvl == "") {
		openAlertModal("SUCCESS","백업레벨을 선택하세요.");
		$("#id_scd_lvl").focus();
		return;
	}else {
		if (scd_lvl == "L01") {
			cron_name = "full";
		} else if (scd_lvl == "L02") {
			cron_name = "incr";
		}
	}
	
	//기간설정, 기간설정 아이템
	var pod_set = $("#id_pod_set").val();
	if (pod_set == "") {
		openAlertModal("SUCCESS","기간설정을 선택하세요.");
		$("#id_pod_set").focus();
		return;
	}
	
	//시간설정
	var tme_set_h = $("#id_tme_set_h").val();
	var tme_set_h_c = tme_set_h.replace(/(^0+)/, "");
	tme_set_h_c = tme_set_h_c == "" ? "0" : tme_set_h_c;
	
	var tme_set_m = $("#id_tme_set_m").val();
	var tme_set_m_c = tme_set_m.replace(/(^0+)/, "");
	tme_set_m_c = tme_set_m_c == "" ? "0" : tme_set_m_c;
	
	var tme_set = tme_set_h + tme_set_m;
	var tme_set_fom = tme_set_h+"시 " + tme_set_m+"분";
	cron_name += "_" + tme_set_h_c + "_" +tme_set_m_c;
	
	var scd_con = "";
	var pod_set_itm = "";
	var pod_set_itm_name = "";
	if (pod_set == "SP01") {
		cron_name += "_0_00" ;
		scd_con = tme_set_m_c+" "+tme_set_h_c+" * * *";
	} else if (pod_set == "SP02") {
		cron_name += "_1_";
		$("input[name='pod_set_itm_week']:checked").each(function() {
			pod_set_itm += $(this).val()+"," ;
			if ($(this).val() == 0) {
				cron_name += "00";
				pod_set_itm_name += "일," ;
			} else if ($(this).val() == 1) {
				cron_name += "01";
				pod_set_itm_name += "월," ;
			} else if ($(this).val() == 2) {
				cron_name += "02";
				pod_set_itm_name += "화," ;
			} else if ($(this).val() == 3) {
				cron_name += "03";
				pod_set_itm_name += "수," ;
			} else if ($(this).val() == 4) {
				cron_name += "04";
				pod_set_itm_name += "목," ;
			} else if ($(this).val() == 5) {
				cron_name += "05";
				pod_set_itm_name += "금," ;
			} else if ($(this).val() == 6) {
				cron_name += "06";
				pod_set_itm_name += "토," ;
			}
		});
		pod_set_itm = pod_set_itm.substring(0,pod_set_itm.lastIndexOf(","));
		scd_con = tme_set_m_c+" "+tme_set_h_c+" * * "+pod_set_itm;
		if(pod_set_itm == ""){
			openAlertModal("WARNING","요일를 선택하세요.");
			return;
		};
	} else if (pod_set == "SP03") {
		cron_name += "_2_";
		$("input[name='pod_set_itm_mon']:checked").each(function() {
			pod_set_itm += $(this).val()+"," ;
			cron_name += $(this).val();
		});
		
		pod_set_itm = pod_set_itm.substring(0,pod_set_itm.lastIndexOf(","));
		scd_con = tme_set_m_c+" "+tme_set_h_c+" "+pod_set_itm+" * *";
		if(pod_set_itm == ""){
			openAlertModal("WARNING","날짜를 선택하세요.");
			return;
		};
	}
	pod_set_itm_name = pod_set_itm_name.substring(0,pod_set_itm_name.lastIndexOf(","));
	

	//보관기간
	var kep_pod = $("#id_kep_pod").val();
	var kep_pod_itm = $("#id_kep_pod_itm").val();
	
	var kep_pod = $("#id_kep_pod").val();
	var kep_pod_itm = $("#id_kep_pod_itm").val();
	
	if (kep_pod == "") {
		if (kep_pod_itm == "") {
			openAlertModal("WARNING","보관기간을 선택하세요");
	    	return;
		}
	} else {
		if (parseInt(kep_pod) == 0) {
			if (parseInt(kep_pod_itm) == 0 || kep_pod_itm == "") {
				openAlertModal("WARNING","보관기간을 선택하세요");
		    	return;
			}
		}
	}
	
	//스케줄명
	var new_scd_nam = $("#id_scd_lvl option:selected").text();
	new_scd_nam += " "+tme_set_h+":"+tme_set_m;
	new_scd_nam += " "+$("#id_pod_set option:selected").text();
	if (pod_set == "SP02") {
		new_scd_nam += " "+pod_set_itm_name;
	} else if (pod_set == "SP03") {
		new_scd_nam += " "+pod_set_itm;
	}
	
	var bts_nam = "<c:out value='${bts_nam}'/>";
	var bms_nam = "<c:out value='${bms_nam}'/>";
	
	var comAjax = new ComAjaxAsync();
	comAjax.setUrl("/updateBakScd");
	comAjax.setCallback("fn_cb_update");
	comAjax.addParam("menu_cd", menu_cd);
	comAjax.addParam("cron_name", cron_name);
	comAjax.addParam("menu_id", menu_id);
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.addParam("bts_nam", bts_nam);
	comAjax.addParam("bms_nam", bms_nam);
	comAjax.addParam("exe_tme", tme_set_fom);
	comAjax.addParam("pod_set", pod_set);
	comAjax.addParam("exe_pod", pod_set);
	comAjax.addParam("lvl", scd_lvl);
	comAjax.addParam("scd_lvl", scd_lvl);
	comAjax.addParam("pod_set_itm", pod_set_itm);
	comAjax.addParam("tme_set", tme_set);
	comAjax.addParam("scd_con", scd_con);
	if (kep_pod != "" && kep_pod != "0") {
		comAjax.addParam("kep_pod", kep_pod);
	}
	if (kep_pod_itm != "" && kep_pod_itm != "0") {
		comAjax.addParam("kep_pod", kep_pod_itm);
	}
	comAjax.addParam("scd_nam", scd_nam);
	comAjax.addParam("old_scd_nam", scd_nam);
	comAjax.addParam("new_scd_nam", new_scd_nam);
	comAjax.ajax();
}
function fn_cb_update(data) {
	var result = data.result;
	var old_name = data.old_scd_nam;
	var new_name = data.new_scd_nam;
	if (result) {
		openAlertModal("SUCCESS","\""+old_name+"\" 스케줄정보가 \""+new_name+"\" 로 수정되었습니다.");
		
		$("#id_scd_nam").val(old_name);
		var idx = $("#id_scd_nam option:selected").index();
		$("#id_scd_nam option:eq("+idx+")").remove();
		$("#id_scd_nam").append("<option value='"+new_name+"'>"+new_name+"</option>");
		fn_sch(new_name);
	} else {
		openAlertModal("FAIL","\""+old_name+"\" 스케줄 정보 수정이 실패 되었습니다.");
	}
}

function fn_scd_delete() {
	openConfirmModal("CONFIRM", "삭제하시겠습니까?", function(){
		$(".modal").remove();
	 	var scd_nam = $("#id_scd_nam").val();
		var comAjax = new ComAjaxAsync();
	 	comAjax.setUrl("/deleteBakScd");
	 	comAjax.setCallback("fn_cb_delete");
	 	comAjax.addParam("bms_id", bms_id);
	 	comAjax.addParam("bts_id", bts_id);
	 	comAjax.addParam("scd_nam", scd_nam);
	 	comAjax.ajax();
	});
}
function fn_cb_delete(data) {
	loadingDiv.off();
	var result = data.result;
	var name = data.scd_nam; 
	if (result) {
		openAlertModal("SUCCESS","\""+name+"\" 스케줄 정보가 삭제되었습니다.");
		var idx = $("#id_scd_nam option:selected").index();
		$("#id_scd_nam option:eq("+idx+")").remove();
		
		fn_btn_init("s");
		fn_combo_init();
		
	} else {
		openAlertModal("FAIL","\""+name+"\" 스케줄 정보 삭제가 실패 되었습니다.");
	}
}


function fn_btn_init(flag) {
	if (flag == "s") {
		$("#scd_save").attr("class","save");
		$("#scd_save").attr("href","javascript:fn_scd_save();");
		$("#scd_modify").attr("class","modify_disable");
		$("#scd_modify").attr("href","#void");
		$("#scd_delete").attr("class","delete_disable");
		$("#scd_delete").attr("href","#void");
	} else if (flag == "m") {
		$("#scd_save").attr("class","save_disable");
		$("#scd_save").attr("href","#void");
		$("#scd_modify").attr("class","modify");
		$("#scd_modify").attr("href","javascript:fn_scd_modify();");
		$("#scd_delete").attr("class","delete");
		$("#scd_delete").attr("href","javascript:fn_scd_delete();");
	}
}

function fn_combo_init() {
	$("#id_scd_lvl option:eq(0)").prop("selected", true);
	psComboset(0,"");
	$("#id_tme_set_h option:eq(0)").prop("selected", true);
	$("#id_tme_set_m option:eq(0)").prop("selected", true);
	$("#id_kep_pod option:eq(0)").prop("selected", true);
	kpComboset("","");
	$("#pod_set_itm_week_sall").prop("checked",false);
	$("input:checkbox[name='pod_set_itm_week']").prop("checked",false);
	$("#pod_set_itm_mon_sall").prop("checked",false);
	$("input:checkbox[name='pod_set_itm_mon']").prop("checked",false);
	
}

function fn_popclose() {
	layerClose2();
}

</script>
<!-- 내용 -->
<div class="content">
<form id='commonFormAsync' name='commonFormAsync'></form>
	<div class="box">
		<!-- untitle -->
		<div class="untitle"></div>
		<dl class="box_content">
			<dt>스케줄 정보</dt>
			<dd>
				<select id="id_scd_nam">
					<option value="">신규 생성</option>
				<c:choose>
					<c:when test="${fn:length(scdlist) > 0}">
						<c:forEach var="row" items="${scdlist}" varStatus="status">
					<option value="${row.scd_nam }">${row.scd_nam }</option>
						</c:forEach>
					</c:when>
				</c:choose>	
				</select>
			</dd>
			<dt>백업 레벨</dt>
			<dd>
				<select id="id_scd_lvl">
					<option value="">선택하세요</option>
				<c:choose>
					<c:when test="${fn:length(bllist) > 0}">
						<c:forEach var="row" items="${bllist}" varStatus="status">
					<option value="${row.com_cod }">${row.com_cod_nam }</option>
						</c:forEach>
					</c:when>
				</c:choose>	
				</select>
			</dd>
			<dt>기간 설정</dt>
			<dd>
				<select id="id_pod_set">
					<option value="">선택하세요</option>
				<c:choose>
					<c:when test="${fn:length(splist) > 0}">
						<c:forEach var="row" items="${splist}" varStatus="status">
					<option value="${row.com_cod }">${row.com_cod_nam }</option>
						</c:forEach>
					</c:when>
				</c:choose>	
				</select>
			</dd>
			<dt>시간 설정</dt>
			<dd>
				<select class="w10" id="id_tme_set_h">
					<%
						for (int h=0; h<24; h++) {
							String str = "";
							if (h < 10) str = "0"+String.valueOf(h);
							else str = String.valueOf(h);
					%>
					<option value="<%=str%>"><%=str%></option>
					<% } %>
				</select>
				&nbsp;시&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<select class="w10" id="id_tme_set_m">
					<%
						for (int m=0; m<60; m++) {
							String str = "";
							if (m < 10) str = "0"+String.valueOf(m);
							else str = String.valueOf(m);
					%>
					<option value="<%=str%>"><%=str%></option>
					<% } %>
				</select>
				&nbsp;분
<!-- 				<input type="number" min="1" max="24" class="w13"> 시 <input type="number" min="0" max="59" class="w13"> 분  -->
			</dd>
			<dt>보관 기간</dt>
			<dd>
				<select class="w13" id="id_kep_pod">
					<option value="">선택하세요</option>
					<option value="0">직접입력</option>
					<% 
						int iKepPodarr[] = {1,3,7,30,90,365};
						for (int i=0; i<iKepPodarr.length; i++) {
					%>
					<option value="<%=iKepPodarr[i]%>"><%=iKepPodarr[i]%>일</option>
					<% } %>
				
				</select>
				<input type="number" min="1" class="w15" id="id_kep_pod_itm" onkeypress="return fn_press(event, 'numbers');" style="ime-mode:disabled;" max="3"> 일
			</dd>
			<!-- 
			<dt>백업 타입</dt>
			<dd id="id_sch_typ"></dd>
			<dt>백업 방법</dt>
			<dd id="id_scd_mtd"></dd>
			-->
		</dl>
	</div>
	
	<!-- 요일단위 Backup Schedule -->
	<div class="box" id="pod_select_div">
		<div class="box_title">주단위 선택</div>
		<ul class="backup_schedule" id="id_week">
			<li><input type="checkbox" id="pod_set_itm_week_sall"><span>Select All</span></li>
			<% 
				String sWeekarr[] = {"일","월","화","수","목","금","토"};
				for (int j=0; j<sWeekarr.length; j++) {
			%>
			<li><input type="checkbox" name="pod_set_itm_week" value="<%=j%>"><%=sWeekarr[j]%></li>
			<% } %>
		</ul>
		<ul class="backup_schedule" id="id_month">
			<li><input type="checkbox" id="pod_set_itm_mon_sall"><span>Select All</span></li>
			<%
				Calendar calendar = Calendar.getInstance();
				int lastday = calendar.getMaximum(Calendar.DAY_OF_MONTH);
				for (int i=1; i<=lastday; i++) {
					String str = "";
					if (i < 10) str = "0"+String.valueOf(i);
					else str = String.valueOf(i);
			%>
			<li><input type="checkbox" name="pod_set_itm_mon" value="<%=str%>"><%=str%></li>
			<% } %>
		</ul>
	</div>
	
	<!-- 하단:버튼 -->
	<div class="bottom_btn">
		<a href="javascript:fn_scd_save();" class="save" id="scd_save">저장</a>
		<a href="javascript:fn_scd_modify();" class="modify" id="scd_modify">수정</a>
		<a href="javascript:fn_scd_delete();" class="delete" id="scd_delete">삭제</a>
	</div>

</form>	
</div><!-- //내용 -->

<!-- footer -->
<%@ include file="/WEB-INF/include/footer.jsp" %>
