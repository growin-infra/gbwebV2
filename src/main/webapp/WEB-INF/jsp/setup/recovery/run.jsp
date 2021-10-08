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

var tNodes =[{'id':1,'pId':0,'name':'복구시점','open':'true','isParent':'true'}];

$(document).ready(function() {
	$.fn.zTree.init($("#treeDemo"), tSetting, tNodes);
	
	$("#rsr_run").on("click", function(e) {
		e.preventDefault();
		if ($("#id_blank_set").is(":visible")) {
			openAlertModal("WARNING", "설정정보가 없습니다.");
		} else {
			if (run_id != null && run_pid != null) {
				if (typ != "BT01") {
					return;
				}
				fn_rsr_run();
			} else {
				openAlertModal("WARNING", "복구시점을 선택하세요.");
			}
		}
	});
	
	fn_init();
});
var rsr_set_blank;
function fn_init() {
	
	$("#id_blank_set").hide();
	$("#id_rsr_set").hide();
	
	rsr_set_blank = "<c:out value='${rsr_set_blank}'/>";
	if (!rsr_set_blank) {
		$("#id_rsr_set").show();
	} else {
		$("#id_blank_set").show();
	}
	
	fn_targetList();
}

var run_id, run_pid, wrk_dt, mtd, lvl, typ, kep_pod;
function tClick(event, treeId, treeNode, clickFlag) {
	$("#id_targer").html("");
	if (treeNode.id != 1) {
		var str = "";
		if (treeNode.typ == "BT02") {
			str += "데이터베이스 단위 백업은 수동 복구를 진행해 주십시오.";
			str += "&emsp;&emsp;&emsp;";
		} else if (treeNode.typ == "BT03") {
			str += "테이블 단위 백업은 수동 복구를 진행해 주십시오.";
			str += "&emsp;&emsp;&emsp;";
		}
		if (treeNode.lvl == "L01") {
			str += "풀백업";
		} else {
			str += "증분백업";
		}
		str += " : ";
		str += strToCustomFormat(treeNode.wrk_dt);
		$("#id_targer").html(str);
		
		run_id = treeNode.id;
		run_pid = treeNode.pId;
		wrk_dt = treeNode.wrk_dt;
		mtd = treeNode.mtd;
		lvl = treeNode.lvl;
		typ = treeNode.typ;
		kep_pod = treeNode.kep_pod;
		
	} else {
		run_id = null;
		run_pid = null;
		wrk_dt = null;
		mtd = null;
		lvl = null;
		typ = null;
		kep_pod = null;
	}
}

function fn_targetList() {
	var comAjax = new ComAjax();
	comAjax.setUrl("/set_r_target");
	comAjax.setCallback("fn_targetListload");
	comAjax.addParam("bms_id", bms_id);
	comAjax.addParam("bts_id", bts_id);
	comAjax.ajax();
}
function fn_targetListload(data) {
	var list = data.list;
	var str = "";
	if (list != null) {
		str = "[{'id':1,'pId':0,'name':'복구시점','open':'true','isParent':'true'},";
		for (var i=0; i<list.length; i++) {
			str += "{";
			str += "'id':"+list[i].run_id+",'pId':"+list[i].run_pid+",'name':'"+list[i].run_nam+"','wrk_dt':'"+list[i].wrk_dt+"','lvl':'"+list[i].lvl+"','mtd':'"+list[i].mtd+"','typ':'"+list[i].typ+"','kep_pod':'"+list[i].kep_pod+"'";
			//if (i == 0) str += ",'open':'true','isParent':'true'";
			if (list[i].run_pid == 1) {
				str += ",'isParent':'true'";
			}
			str += "}";
			if ((i+1 != list.length)) str += ",";
		}
		str += "]";
	} else {
		str = "[{'id':1,'pId':0,'name':'복구시점','open':'true','isParent':'true'}]";
	}
	$.fn.zTree.init($("#treeDemo"), tSetting, eval(str));
	
}

function fn_rsr_run() {
	
	if (new String(run_id).valueOf() == "undefined" || run_id == null
			|| new String(run_pid).valueOf() == "undefined" || run_pid == null
			|| new String(wrk_dt).valueOf() == "undefined" || wrk_dt == null
			) {
		openAlertModal("WARNING", "복구시점을 선택하세요.");
		return;
	}
	
	if (rsr_set_blank) {
		openAlertModal("WARNING" , "복구설정 정보가 없습니다.");
		return;
	}
	
	openConfirmModal("CONFIRM", "복구실행 하시겠습니까?", function(){
		$(".modal").remove();
		var comSubmit = new ComSubmit();
	    comSubmit.setUrl("/set_rsr_run");
	    comSubmit.addParam("menu_cd", menu_cd);
	    comSubmit.addParam("menu_id", menu_id);
	    comSubmit.addParam("bms_id", bms_id);
	    comSubmit.addParam("bts_id", bts_id);
	    comSubmit.addParam("bms_nam", $("#bms_nam").val());
	    comSubmit.addParam("bts_nam", $("#bts_nam").val());
	    comSubmit.addParam("wrk_dt", wrk_dt);
	    comSubmit.addParam("lvl", lvl);
	    comSubmit.addParam("mtd", mtd);
	    comSubmit.addParam("typ", typ);
	    comSubmit.addParam("kep_pod", kep_pod);
	    comSubmit.addParam("run_id", run_id);
	    comSubmit.addParam("cpr", $("#cpr").val());
	    comSubmit.addParam("hst_ip", $("#hst_ip").val());
		comSubmit.submit();
	});
}
</script>
<!-- 내용 -->
<input type="hidden" id="bms_nam" value="${bms_nam }" />
<input type="hidden" id="bts_nam" value="${bts_nam }" />
<input type="hidden" id="cpr" name="cpr" value="${frsmap.cpr }" />
<input type="hidden" id="hst_ip" name="hst_ip" value="${frsmap.hst_ip }" />
<div class="content">
	<div class="box">
		<!-- 복구시점 -->
		<div class="box_title">
			<span>복구대상</span>&emsp;&emsp;&emsp;
			<span class="targer" id="id_targer"></span>
		</div>
		<ul class="server box_server">
			<li>
				<div>
					<ul id="treeDemo" class="ztree2"></ul>
				</div>
			</li>
		</ul>
	</div><!-- //box -->
	
	
	<!-- 하단:버튼 -->
	<div class="bottom_btn">
		<a href="#void" class="action" id="rsr_run">복구실행</a>
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
			<dt>Host</dt>
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
