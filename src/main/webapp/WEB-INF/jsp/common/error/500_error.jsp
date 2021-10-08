<%@ page language="java" isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>페이지 오류</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="//fonts.googleapis.com/earlyaccess/notosanskr.css" rel="stylesheet" type="text/css">
<link type="text/css" href="/webdoc/css/reset.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/common.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/layout.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/design.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/zTreeStyle.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/font-awesome.min.css" rel="stylesheet">
<link type="text/css" href="/webdoc/css/progress/bi-style.css" rel="stylesheet">
<script type="text/javascript" src="/webdoc/script/common.js"></script>
<script type="text/javascript" src="/webdoc/script/jquery.1.12.0.min.js"></script>
<script type="text/javascript">
function fn_move() {
	var comSubmit = new ComSubmit();
    comSubmit.setUrl("/main");
    comSubmit.submit();
}	
</script>
</head>
<body>
<form id="commonForm" name="commonForm"></form>
    <div class="error_whole">
        <section class="error_wrap">
            <div class="error_box">
                <div class="error_title_500" >
                	                	${msg}
                </div>
                <div class="error_top_500" >
                   	 이용에 불편을 드려 <b>대단히 죄송합니다.</b>
                </div>
                <div>
                <div class="error_middle">
                   	인터넷 창을 다시 띄우시거나 잠시 후 접속하여 주시기 바랍니다.<br> 이용에 불편을 드려 대단히 죄송합니다.
                </div>
                </div>
                <div class="error_bottom_500">
                <a onclick="javascript:fn_move();">GINIAN 메인으로 이동</a>
                </div>
            </div>
        </section>
    </div>
</body>
</html>
