<%@ page language="java" isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>페이지를 찾을 수 없습니다</title>
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
    <div class="error_whole">
        <section class="error_wrap">
            <div class="error_box">
               <div class="error_title_404" >
                	404
                </div>
                <div class="error_top" >
                    죄송합니다. 현재 찾을 수 없는 페이지를 요청 하셨습니다.
                </div>
                <div>
                <div class="error_middle">
                    존재하지 않는 주소를 입력하셨거나,<br> 요청하신 페이지의 주소가 변경, 삭제되어 찾을 수 없습니다.<br><br> 궁금한 점이 있으시면 고객센터를 통해 문의해 주시기 바랍니다.<br><br> 감사합니다.
                </div>
                </div>
                <div class="error_bottom">
                    <a href="#" onclick="javascript:fn_move();" >이전페이지</a>
                </div>
            </div>
        </section>
    </div>
</body>
</body>
</html>
