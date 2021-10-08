<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<%@ include file="/WEB-INF/include/jstl.jsp" %>
<title>GINIAN</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="user-scalable=yes, maximum_scale=1, minimum_scale=1.0 ,width=1240px, target_densitydpi=device-dpi">
<meta name="title" content="GINIAN">
<meta name="author" content="그로윈">
<meta name="keywords" content="지니안, ginian, MariaDB, mysql, backup solution, 백업솔루션">
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

<script type="text/javascript" src="/webdoc/script/jquery.1.12.0.min.js"></script>
<script type="text/javascript" src="/webdoc/script/ebi.js"></script>
<script type="text/javascript" src="/webdoc/script/common.js"></script>
<script type="text/javascript" src="/webdoc/script/modal.js"></script>
<script type="text/javascript" src="/webdoc/script/jquery.blockUI.js"></script>
<script type="text/javascript">
<%-- var ml = "<%=login_dupl%>"; --%>
var loadingDiv = {
	on:function() {
	   $.blockUI({
	      message: "<image src='/webdoc/images/loading/Spinner.gif'></image>",
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
$(document).ready(function(){
	var userInputId = fn_getCookie("userInputId");
	    $("#id").val(userInputId); 
	
    if($("#id").val() != ""){
        $("#auto_input").attr("checked", true); 
        $("#pw").focus();
    }else{
    	$("#id").focus();
    }
    	
	$("#login").on("click", function(e){
        e.preventDefault();
        fn_login();
    });
	
	//로그인-키보드
	$("#id, #pw").keydown(function(e) {
		if (e.keyCode == 13) {
			fn_login();
		}
	});
	//로그인-키보드
	$("#find_pass").on("click", function(e) {
		e.preventDefault();
	});
	
});

function fn_before_chk(d) {
	var result = true;
	var param = new Object();
	param.dvd = d;
    $.ajax({
        url : "/login_lcn_chk",
		data : param,
        type : "POST",
        async : false,
        success : function(data, status) {
        	loadingDiv.off();
        	var map = data.lcnInfoMap;
        	if (map != null) {
        		if (map.remainDt != "") {
        			remainDt = map.remainDt;
        		}
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

var remainDt;
function fn_login() {
	
	if (!fn_before_chk("date")) return;
	
	if ($("#id").val() == "") {
		openAlertModal("WARNING","아이디를 입력하세요.");
		$("#id").focus();
		return false;
	}
	if ($("#pw").val() == "") {
		openAlertModal("WARNING","패스워드를 입력하세요.");
		$("#pw").focus();
		return false;
	}
     
    if($("#auto_input").is(":checked")){ 
        var userInputId = $("#id").val();
        fn_setCookie("userInputId", userInputId, 7); 
    }else{
        fn_deleteCookie("userInputId");
    }
    
	var comAjax = new ComAjax();
	comAjax.setUrl("/login");
	comAjax.setCallback("fn_callbacklogin");
	comAjax.addParam("id", $("#id").val());
	comAjax.addParam("pw", $("#pw").val());
	comAjax.addParam("remainDt", remainDt);
	comAjax.ajax();
}
function fn_callbacklogin(data) {
	var result = data.result;
	var menu_cd = data.lst_pag;
	if (result) {
		if (remainDt != "") {
			var alertyon = false;
			var adt = [60,50,40,30,20,10,9,8,7,6,5,4,3,2,1,0];
			for (var i=0; i<adt.length; i++) {
				if (parseInt(remainDt) == adt[i]) {
					alertyon = true;
					break;
				}
			}
			if (alertyon) {
				openModalStop("WARNING", "라이선스 기간이 ["+remainDt+"]일 남았습니다.", function(){
			 		fn_pagemove(menu_cd);
				});
			} else {
				fn_pagemove(menu_cd);
			}
		} else {
			fn_pagemove(menu_cd);
		}
	} else {
		openAlertModal("WARNING","사용자 정보를 확인하세요.");
	}
}

function fn_pagemove(menu_cd) {
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/smy_rbs");
    comSubmit.addParam("menu_cd", menu_cd);
    comSubmit.addParam("currentPageNo", 1);
    comSubmit.submit();
}

//ID 저장 쿠키셋
function fn_setCookie(cookieName, value, exdays){
  var exdate = new Date();
  exdate.setDate(exdate.getDate() + exdays);
  var cookieValue = escape(value) + ((exdays==null) ? "" : "; expires=" + exdate.toGMTString());
  document.cookie = cookieName + "=" + cookieValue;
}
function fn_deleteCookie(cookieName){
    var expireDate = new Date();
    expireDate.setDate(expireDate.getDate() - 1);
    document.cookie = cookieName + "= " + "; expires=" + expireDate.toGMTString();
}
 
function fn_getCookie(cookieName) {
    cookieName = cookieName + '=';
    var cookieData = document.cookie;
    var start = cookieData.indexOf(cookieName);
    var cookieValue = '';
    if(start != -1){
        start += cookieName.length;
        var end = cookieData.indexOf(';', start);
        if(end == -1)end = cookieData.length;
        cookieValue = cookieData.substring(start, end);
    }
    return unescape(cookieValue);
}

</script>
</head>
<body>
<form id="commonForm" name="commonForm"></form>
<input type="hidden" id="menu_id" value="A01">
<%
	String browser = "";
	String userAgent = request.getHeader("User-Agent");
	userAgent = userAgent.toUpperCase();
	if (userAgent.indexOf("MSIE") > 0) {
		browser = "IE"; // IE 10 이하
	} else if (userAgent.indexOf("TRIDENT") > 0) {
		browser = "IE11"; // IE 11 이상
	} else if (userAgent.indexOf("OPERA") > 0 || userAgent.indexOf("OPR") > 0) {
		browser = "Opera";
	} else if (userAgent.indexOf("FIREFOX") > 0) {
		browser = "Firefox";
	} else if (userAgent.indexOf("SAFARI") > 0) {
		if (userAgent.indexOf("CHROME") > 0) {
			browser = "Chrome";
	 	} else {
	  		browser = "Safari";
	 	}
	}
	
	if ((browser != "") && ( browser == "IE" || browser == "IE11" || browser == "Firefox" || browser == "Chrome")) {
%>
<div class="login_whole">
	<section class="login_wrap">
		<div class="login_box">
			<!-- 상단:타이틀 -->
			<div class="login_top">
				<span>GINIAN Management Login page</span>
			</div>
			<div class="login_part">
				<!-- 왼쪽:폼영역 -->
				<div class="login_left">
					<div class="title">
						<img src="/webdoc/images/login/title_txt.png" alt="">
					</div>
					<ul class="form">
						<li><label for="id">아이디(ID)</label><input type="text" id="id" placeholder="아이디를 입력하세요." maxlength="30"></li>
						<li><label for="pw">패스워드(PASSWORD)</label><input type="password" id="pw" placeholder="패스워드를 입력하세요." maxlength="100" value=""></li>
					</ul>
					<div class="btn">
						<div class="auto_search">
							<span><input type="checkbox" id="auto_input"> <label for="auto_input">아이디 저장</label></span>
						</div>
						<div class="login" id="login">
							<a>LOGIN</a>
						</div>
					</div>
				</div>
				<!-- 오른쪽:이미지 -->
				<div class="login_right">
					<div class="top"><img src="/webdoc/images/login/zmanda.jpg" alt="zmanda"></div>
					<div class="bottom">
						<p>Ginian Properties</p>
						<div>
							<span><img src="/webdoc/images/login/ginian_01.png" alt="Ginian network"></span>
							<span><img src="/webdoc/images/login/ginian_02.png" alt="Ginian forum"></span>
							<span><img src="/webdoc/images/login/ginian_03.png" alt="Ginian wiki"></span>
						</div>
					</div>
				</div>
			</div>
			<div class="login_bottom"></div>
			<!-- 하단:카피라이트 -->
			<div class="login_footer">
				<span>Copyright ⓒ GROWIN Co., Ltd.<span>All right reserved.</span></span>
			</div>
		</div><!-- // login_box -->
	</section><!-- // login_wrap -->
</div><!-- // login_whole -->

<!-- 알림창 -->
<div class="layerPop">
	<div class="wrap">
		<div class="inner">
			<div class="title">
				<p>알림</p>
				<a href="javascript:layerClose();" class="layerClose">닫기</a>
			</div>
			<div class="explain">
				<p>The user name &quot;<b><font size="4px" id="b_id"></font></b>&quot; and/or password is incorrect</p>
			</div>		
		</div>
	</div>
</div>
<%
	} else {
%>
<div class="login_whole">
	<section class="login_wrap">
		<div class="login_box">
			<!-- 상단:타이틀 -->
			<div class="login_top">
				<span>GINIAN Management Login page</span>
			</div>
			<div class="login_part">
				<div class="login_center">
				<br><br><br><br><br><br>
				<font size="7"><%=browser%> 브라우저는 사용하실 수 없습니다.</font><br>
				<font size="7">IE 브라우저에서 다시 시도 하십시오.</font>
				<br><br><br><br><br><br><br><br>
				</div>
			</div>
			<div class="login_bottom"></div>
			<!-- 하단:카피라이트 -->
			<div class="login_footer">
				<span>Copyright ⓒ GROWIN Co., Ltd.<span>All right reserved.</span></span>
			</div>
		</div><!-- // login_box -->
	</section><!-- // login_wrap -->
</div><!-- // login_whole -->
<%
	}
%>
</body>
</html>