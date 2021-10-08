<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="ko"> 
<head>
<%@ include file="jstl.jsp" %>
<title>GINIAN</title>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> --%>
<%-- <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> --%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui" %>
<%-- <%@ page import="java.util.*"%> --%>
<%-- <c:set var="today" value="<%=new java.util.Date()%>" /> --%>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=yes, maximum_scale=1, minimum_scale=1.0 ,width=1240px, target_densitydpi=device-dpi">
<meta name="title" content="지니안">
<meta name="author" content="그로윈">
<meta name="keywords" content="지니안, ginian, MariaDB, MySQL, backup solution, 백업솔루션">
<meta name="subject" content="GINIAN">
<meta name="Description" content="GINIAN Recovery Manager For MariaDB & Mysql">
<meta name="classification" content="secutiry">	

<link rel="shortcut icon" href="/webdoc/images/ginian_fvc.ico" type="image/x-icon" />
<link rel="icon" href="/webdoc/images/ginian_fvc.ico" type="image/x-icon" />

<link href="//fonts.googleapis.com/earlyaccess/notosanskr.css" rel="stylesheet" type="text/css">
<link type="text/css" href="/webdoc/css/reset.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/common.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/layout.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/design.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/zTreeStyle.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/font-awesome.min.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/progress/bi-style.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/tipTip.css" rel="stylesheet">
<!-- <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"/> -->

<script type="text/javascript" src="/webdoc/script/jquery.1.12.0.min.js"></script>
<script type="text/javascript" src="/webdoc/script/jquery.ztree.core.js"></script>
<script type="text/javascript" src="/webdoc/script/jquery.ztree.exedit.js"></script>
<script type="text/javascript" src="/webdoc/script/jquery.blockUI.js"></script>
<script type="text/javascript" src="/webdoc/script/ebi.js"></script>
<script type="text/javascript" src="/webdoc/script/common.js"></script>
<script type="text/javascript" src="/webdoc/script/modal.js"></script>
<script type="text/javascript" src="/webdoc/script/barChart.js"></script>
<script type="text/javascript" src="/webdoc/script/jquery.tipTip.minified.js"></script>

<script type="text/javascript" src="/webdoc/script/jquery.knob.min.js"></script>
<script type="text/javascript" src="/webdoc/script/jquery.babypaunch.spinner.min.js"></script>

<script type="text/javascript" src="/webdoc/script/progress/jquery-barIndicator.js"></script>
<script type="text/javascript" src="/webdoc/script/progress/jquery.easing.1.3.js"></script>

<%-- <spring:url value="/WEB-INF/jsp/setup/tree/treeLoad.js" var="treeLoadJs" /> --%>
</head>
<body>
<div>
   	<img class="top_move_arrow" src="/webdoc/images/btn/top_arrow.png" />
</div>
<div class="grobal_Wrapper">
<%
request.setCharacterEncoding("UTF-8");
response.setContentType("text/html;charset=UTF-8");
%>
<%@ include file="session.jsp" %>
<!-- 로고 -->
<header>
	<h1><a href="#void" id="main"><img src="/webdoc/images/main/logo.png" alt="그로윈 로고"></a></h1>
	<% if (!"".equals(s_id) && s_id != null) { %>
	<div>
		<%
			if (!"".equals(remain_dt)) {
				if (Integer.valueOf(remain_dt) < 60) {
		%>
		<span><a class="license_day" href="#void" id="license">남은기간 : <%=remain_dt%>일</a></span>
		<%
				}
			}
		%>
		<span><a class="current_user" href="#void" id="member"><%=s_id%></a></span>
		<span><a class="log_out" href="#void" id="logout">LOGOUT</a></span>
	</div>
	<% } else { %>
<script type="text/javascript">
$(document).ready(function(){
	layerShow();
});
function fn_move() {
	layerClose();
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/main");
    comSubmit.submit();
}
</script>
	<% } %>
</header>
<!-- 로그아웃 레이어알림창 -->
<div class="layerPop">
	<div class="wrap">
		<div class="inner">
			<div class="title">
				<p>알림</p>
				<a href="javascript:fn_move();" class="layerClose">닫기</a>
			</div>
			<div class="explain">
				<p><b><font size="4px" id="b_id">세션이 종료되었습니다.</font></b></p>
			</div>		
		</div>
	</div>
</div>
<!-- 레이어알림창 -->
<div class="layerPop2">
	<div class="wrap">
		<div class="inner">
			<div class="title">
				<p>알림</p>
				<a href="javascript:fn_popclose();" class="layerClose">닫기</a>
			</div>
			<div class="explain">
				<p><b><font size="4px" id="lyr_msg"></font></b></p>
			</div>		
		</div>
	</div>
</div>
<script type="text/javascript">
window.history.forward();
var menu_cd = "<%=menu_cd%>";
var pet_cod = "<%=pet_cod%>";
var menu_id = "<%=menu_id%>";
var menu_nm = "<%=menu_nm%>";
var bms_id = "<%=bms_id%>";
var bts_id = "<%=bts_id%>";
var bmt_id = "<%=bmt_id%>";
var remain_dt = "<%=remain_dt%>";

// Internet Explorer 버전 체크
function isIE(){
	var ieVer = navigator.userAgent.toLowerCase();
	return (ieVer.indexOf('msie') != -1) ? parseInt(ieVer.split('msie')[1]) : true;
}
var ieVersion = isIE();

var loadingDivChecker = "";
if(ieVersion == true){
	loadingDivChecker = "<div class='loading'><span></span><span></span><span></span><span></span></div>";
}else{
	loadingDivChecker = "<image src='/webdoc/images/loading/Spinner.gif'></image>";
}
	
var loadingDiv = {
        on:function() {
           $.blockUI({
              message: loadingDivChecker,
              css: {
                 border:'0px solid #FFFFFF',
                 cursor:'wait',
                 backgroundColor:'rgba(0,0,0,0.0)'
               },
               overlayCSS: { 
                  backgroundColor: '#000',
                  opacity:0.5,
                  cursor:'wait'
               }
           });
        },
        off:function() {
           $.unblockUI();//Loading Stop
        },
};

$(document).ready(function() {
	$('.top_move_arrow').hide();
    $(window).scroll(function() {
    	if ($(this).scrollTop() > 200) {
    		$('.top_move_arrow').fadeIn();
        } else {
        	$('.top_move_arrow').fadeOut();
        }
    });
    $('.top_move_arrow').click(function() {
    	$('html, body').animate({scrollTop:0}, 400);
    	return false;
    });
	
	$("#logout").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        logout();
    });
	
	$("#member").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        fn_usr();
    });
	$("#license").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        fn_ml();
    });
// 	$("#board").on("click",function(e){
// 		e.preventDefault();
// 		board();
// 	});
	
	$("#main").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        fn_rbs();
    });
	$("#smy").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        fn_rbs();
    });
	$("#smy_rbs").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        fn_rbs();
    });
	$("#smy_rrs").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        fn_rrs();
    });
	$("#smy_ss").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        fn_ss($("#bms_id").val(),$("#bts_id").val());
    });
	
	$("#set").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        fn_bms();
    });
	$("#set_bms").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        fn_bms();
    });
	
	$("#set_b_bak").on("click", function(e){
		loadingDiv.on();
        e.preventDefault();
        fn_b_rec($("#bms_id").val(),$("#bts_id").val());
    });
	$("#set_b_rec").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_b_rec($("#bms_id").val(),$("#bts_id").val());
	});
	$("#set_bak").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_b_rec($("#bms_id").val(),$("#bts_id").val());
	});
	$("#set_b_set").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_b_set();
	});
	$("#set_b_mtd").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_b_mtd();
	});
	$("#set_b_scd").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_b_scd();
	});
	$("#set_b_smy").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_b_smy();
	});
	$("#set_b_run").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_b_run();
	});
	$("#set_rcy").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_r_rb();
	});
	$("#set_r_rb").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_r_rb();
	});
	$("#set_r_rec").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_r_rec($("#bms_id").val(),$("#bts_id").val());
	});
	$("#set_r_set").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_r_set();
	});
	$("#set_r_smy").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_r_smy();
	});
	$("#set_r_run").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_r_run();
	});
	
	$("#mng").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
		fn_ds();
    });
	$("#mng_usr").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
        fn_usr();
    });
	$("#mng_dft").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
        fn_ds();
    });
	$("#mng_svr").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
        fn_ms();
    });
	$("#mng_lcn").on("click", function(e){
		loadingDiv.on();
		e.preventDefault();
        fn_ml();
    });
	
	$("#spin").spinner({
		color: "black"
		, background: "rgba(0,0,0,0.7)"
		, html: "<image src='/webdoc/images/loading/gloading.gif'></image>"
		, spin: false
	});
	
	gnbLoad();
	
	$(".tooltip").tipTip();
});

/**
 * 더블클릭 방지용
 */
var doubleSubmitFlag = false;
function doubleSubmitCheck(){
    if(doubleSubmitFlag){
        return doubleSubmitFlag;
    }else{
        doubleSubmitFlag = true;
        return false;
    }
}

function gnbLoad() {
	
	$("#smy_img").attr("src","/webdoc/images/common/dep1_summary.png");
	$("#set_img").attr("src","/webdoc/images/common/dep1_set.png");
	$("#mng_img").attr("src","/webdoc/images/common/dep1_admin.png");
	$("#A0101").attr("class","dep1");
	$("#A010101").removeClass("on");
	$("#A010102").removeClass("on");
	$("#A010103").removeClass("on");
	
	$("#A0102").attr("class","dep1");
	$("#A010201").removeClass("on");
	
	$("#A0103").attr("class","dep1");
	$("#A010301").removeClass("on");
	$("#A010302").removeClass("on");
	$("#A010303").removeClass("on");
	
	if (pet_cod.substr(0,5) != "A0102") {
		if (pet_cod == "A0101") {
			$("#smy_img").attr("src","/webdoc/images/common/dep1_summary_on.png");
		} else if (pet_cod == "A0103") {
			$("#mng_img").attr("src","/webdoc/images/common/dep1_admin_on.png");
		}
		
		$("#"+pet_cod).attr("class","dep1 on");
		$("#"+menu_cd).attr("class","on");
		
	} else {
		
		$("#set_img").attr("src","/webdoc/images/common/dep1_set_on.png");
		if (menu_cd == "A010201") {
			$("#"+pet_cod).attr("class","dep1 on");
			$("#"+menu_cd).attr("class","on");
		} else {
			$("#sub_location").html("설정 &gt; 백업복구설정 &gt; 백업 &gt; "+menu_nm)
			if (menu_cd >= "A01020207") {
				$("#sub_location").html("설정 &gt; 백업복구설정 &gt; 복구 &gt; "+menu_nm)
			}
			$("#A01020201").removeClass("on");
			$("#A01020202").removeClass("on");
			$("#A01020203").removeClass("on");
			$("#A01020204").removeClass("on");
			$("#A01020205").removeClass("on");
			$("#A01020206").removeClass("on");
			
			$("#A01020207").removeClass("on");
			$("#A01020208").removeClass("on");
			$("#A01020209").removeClass("on");
			$("#A01020210").removeClass("on");
			$("#A01020211").removeClass("on");
			
			$("#"+pet_cod.substr(0,5)).attr("class","dep1 on");
			$("#"+pet_cod).attr("class","on");
			$("#"+menu_cd).attr("class","on")
		}
	}
}

// function fn_main() {
//     var comSubmit = new ComSubmit();
//     comSubmit.setUrl("/smy_rbs");
//     comSubmit.submit();
// }

function logout() {
	if (doubleSubmitCheck()) return;
    var comSubmit = new ComSubmit();
    comSubmit.setUrl("/logout");
    comSubmit.submit();
}

function board(){
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/boardList");
    comSubmit.addParam("menu_cd", menu_cd);
    comSubmit.addParam("pet_cod", pet_cod);
    comSubmit.addParam("menu_id", menu_id);
    comSubmit.addParam("menu_nm", menu_nm);
    comSubmit.addParam("bms_id", bms_id);
    comSubmit.addParam("bts_id", bts_id);
    comSubmit.addParam("bmt_id", bmt_id);
    comSubmit.submit();
}
function fn_rbs() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/smy_rbs");
    comSubmit.addParam("menu_cd", "A010101");
    comSubmit.addParam("currentPageNo", 1);
    comSubmit.submit();
}
function fn_rrs() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/smy_rrs");
    comSubmit.addParam("menu_cd", "A010102");
    comSubmit.submit();
}
function fn_ss(bmsid,btsid) {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/smy_ss");
    comSubmit.addParam("menu_cd", "A010103");
    comSubmit.submit();
}

function fn_bms() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_bms");
    comSubmit.addParam("menu_cd", "A010201");
    comSubmit.submit();
}

function fn_b_rec() {
	if (doubleSubmitCheck()) return;
	if (new String($("#bms_id").val()).valueOf() == "undefined" && $("#bms_id").val() != null && $("#bms_id").val() != "") {
		var comSubmit = new ComSubmit();
	    comSubmit.setUrl("/set_b_rec");
	    comSubmit.addParam("menu_cd", "A01020201");
	    comSubmit.addParam("bms_id", $("#bms_id").val());
	    comSubmit.addParam("bts_id", $("#bts_id").val());
	    comSubmit.submit();
	} else {
		openAlertModal("WARNING","대상서버를 먼저 등록하십시오.");
		fn_bms();
	}
}

function fn_b_rec(bmsid,btsid,bmtid) {
	if (doubleSubmitCheck()) return;
	if (new String(bts_id).valueOf() == "undefined" != null != "") {
		var comSubmit = new ComSubmit();
	    comSubmit.setUrl("/set_b_rec");
	    comSubmit.addParam("menu_cd", "A01020201");
	    comSubmit.addParam("bms_id", bmsid);
	    comSubmit.addParam("bts_id", btsid);
	    comSubmit.addParam("bmt_id", bmtid);
	    comSubmit.submit();
	} else {
		openAlertModal("WARNING","대상서버를 먼저 등록하십시오.");
		fn_bms();
	}
}
function fn_b_set() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_b_set");
    comSubmit.addParam("menu_cd", "A01020202");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_b_mtd() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_b_mtd");
    comSubmit.addParam("menu_cd", "A01020203");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_b_scd() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_b_scd");
    comSubmit.addParam("menu_cd", "A01020204");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_b_scd_sm(bms,bts) {
	var bmsid = bms;
	var btsid = bts;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_b_scd");
    comSubmit.addParam("menu_cd", "A01020204");
	comSubmit.addParam("bms_id", bmsid);
    comSubmit.addParam("bts_id", btsid);
    comSubmit.submit();
}
function fn_b_smy() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_b_smy");
    comSubmit.addParam("menu_cd", "A01020205");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_b_run() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_b_run");
    comSubmit.addParam("menu_cd", "A01020206");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_r_rb() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_r_rb");
    comSubmit.addParam("menu_cd", "A01020207");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_r_rec() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_r_rec");
    comSubmit.addParam("menu_cd", "A01020208");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_r_rec_sm(bmsid,btsid) {
	loadingDiv.on();
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_r_rec");
    comSubmit.addParam("menu_cd", "A01020208");
    comSubmit.addParam("bms_id", bmsid);
    comSubmit.addParam("bts_id", btsid);
    comSubmit.submit();
}
function fn_r_set() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_r_set");
    comSubmit.addParam("menu_cd", "A01020209");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_r_smy() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_r_smy");
    comSubmit.addParam("menu_cd", "A01020210");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_r_run() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/set_r_run");
    comSubmit.addParam("menu_cd", "A01020211");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}

function fn_usr() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/mng_usr");
    comSubmit.addParam("menu_cd", "A010303");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_ds() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/mng_dft");
    comSubmit.addParam("menu_cd", "A010301");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_ms() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit("commonForm");
    comSubmit.setUrl("/mng_svr");
    comSubmit.addParam("menu_cd", "A010302");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_ml() {
	if (doubleSubmitCheck()) return;
	var comSubmit = new ComSubmit("commonForm");
    comSubmit.setUrl("/mng_lcn");
    comSubmit.addParam("menu_cd", "A010304");
    comSubmit.addParam("bms_id", $("#bms_id").val());
    comSubmit.addParam("bts_id", $("#bts_id").val());
    comSubmit.submit();
}
function fn_exceldown() {
	
}



function newpopup(flag) {
	if (flag == "xtra") {
		window.open("/help_xtra","new","scrollbars=yes,resizable=yes,width=500,height=500,top=100,left=100"); 
	} else if (flag == "mysql") {
		window.open("/help_mysql","new","scrollbars=yes,resizable=yes,width=500,height=500,top=100,left=100"); 
	}
}

</script>
<script type="text/javascript">
<!--
$(document).ready(function() {
	$(this).contextmenu(function(e) { 
		return false; 
	});
});
window.history.forward(1);

document.onkeydown = function(e) {
	var key = (e) ? e.keyCode : event.keyCode;
	if(key==8 || key==116) {
		if(e) {
			e.preventDefault();
		} else {
			event.keyCode = 0;
			event.returnValue = false;
		}
	}
}

var i=0
window.document.onkeydown = protectKey;
function down() {
	window.footer_cart.scrollBy(0,31)
	return;
}
function up() {
	window.footer_cart.scrollBy(0,-31)
	return;
}
function protectKey() {
	//새로고침을 막는 스크립트.. F5 번키..
	if(event.keyCode == 116) {
		event.keyCode = 0;
		return false;
	}
	//CTRL + N 즉 새로 고침을 막는 스크립트....
	else if ((event.keyCode == 78) && (event.ctrlKey == true)) {
		event.keyCode = 0;
		return false;
	}
}
//-->
</script>
<form id="commonForm" name="commonForm"></form>
<input type="hidden" id="menu_cd" value="<%=menu_cd%>" />
<input type="hidden" id="bms_id" value="<%=bms_id%>" />
<input type="hidden" id="bts_id" value="<%=bts_id%>" />
<input type="hidden" id="bmt_id" value="<%=bmt_id%>" />
<!-- gnb -->
<div class="gnb">
	<ul>
		<li class="dep1" id="A0101">
			<a href="#void" id="smy"><span><img src="/webdoc/images/common/dep1_summary_on.png" alt="" id="smy_img">요약</span></a>
			<ul class="dep2">
				<li id="A010101"><a href="#void" id="smy_rbs">백업</a></li>
				<li id="A010102"><a href="#void" id="smy_rrs">복구</a></li>
				<li id="A010103"><a href="#void" id="smy_ss">스케줄</a></li>
			</ul>
		</li>
		<li class="dep1" id="A0102">
			<a href="#void" id="set"><img src="/webdoc/images/common/dep1_set.png" id="set_img">설정</a>
			<ul class="dep2">
				<li id="A010201"><a href="#void" id="set_bms">백업관리현황</a></li>
<% if (!"".equals(bms_id) && bms_id != null && !"".equals(bts_id) && bts_id != null) {%>
				<li id="A010202"><a href="#void" id="set_b_bak">백업복구설정</a></li>
<% } else { %>
				<li id="A010202">백업복구설정</li>
<% } %>
			</ul>
		</li>
		<li class="dep1" id="A0103">
			<a href="#void" id="mng"><img src="/webdoc/images/common/dep1_admin.png" id="mng_img">관리</a>
			<ul class="dep2">
				<li id="A010301"><a href="#void" id="mng_dft">설정</a></li>
<% if ("AT01".equals(usr_ath)) { %>
				<li id="A010302"><a href="#void" id="mng_svr">서버</a></li>
				<li id="A010303"><a href="#void" id="mng_usr">사용자</a></li>
<% } %>
				<li id="A010304"><a href="#void" id="mng_lcn">라이선스</a></li>
			</ul>
		</li>
	</ul>
</div>
<%
	if ("A0102".equals(pet_cod.substring(0, 5))) {
		if (!"A010201".equals(menu_cd)) {
%>
<!-- 서브메뉴 -->
<div class="sub_menu">
	<!-- 서브 : 타이틀 -->
	<div class="title_wrap">
		<div class="wrap">
			<div class="title" id="sub_title">${bms_nam} &gt; ${bts_nam}</div>
<!-- 			<div class="loaction" id="sub_location">설정 &gt; 백업복구설정 &gt; 백업 &gt; 이력</div> -->
			<div class="loaction" id="sub_location"></div>
		</div>
	</div>
	<div class="list_wrap">
		<div class="list">
			<!-- 서브메뉴 : 백업리스트 -->
			<ul class="backup dep3">
				<li><a href="#void" id="set_bak"><img src="/webdoc/images/summary/backup_icon.png" alt="">백업</a>
					<ul class="dep4">
						<li id="A01020201"><a href="#void" id="set_b_rec">이력</a></li>
<% if ("AT01".equals(usr_ath)) { %>
						<li id="A01020202"><a href="#void" id="set_b_set">백업설정</a></li>
						<li id="A01020203"><a href="#void" id="set_b_mtd">백업방법</a></li>
						<li id="A01020204"><a href="#void" id="set_b_scd">스케줄</a></li>
<% } %>
						<li id="A01020205"><a href="#void" id="set_b_smy">요약</a></li>
<% if ("AT01".equals(usr_ath)) { %>
						<li id="A01020206"><a href="#void" id="set_b_run">백업실행</a></li>
<% } %>
					</ul>
				</li>
			</ul>
			<!-- 서브메뉴 : 복구리스트 -->
			<ul class="recovery dep3">
				<li><a href="#void" id="set_rcy"><img src="/webdoc/images/summary/recovery_icon.png" alt="">복구</a>
					<ul class="dep4">
						<li id="A01020207"><a href="#void" id="set_r_rb">마지막백업</a></li>
						<li id="A01020208"><a href="#void" id="set_r_rec">이력</a></li>
<% if ("AT01".equals(usr_ath)) { %>
						<li id="A01020209"><a href="#void" id="set_r_set">복구설정</a></li>
<% } %>
						<li id="A01020210"><a href="#void" id="set_r_smy">요약</a></li>
<% if ("AT01".equals(usr_ath)) { %>
						<li id="A01020211"><a href="#void" id="set_r_run">복구실행</a></li>
<% } %>
					</ul>
				</li>
			</ul>
		</div><!-- //list -->
	</div><!-- //list_wrap -->
</div>
<%
		}
	}
%>

<div id="spin"></div>

