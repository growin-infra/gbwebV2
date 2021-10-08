<%@ page language="java" isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>NULL 에러</title>
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
</head>
<body>
	<button onclick="history.back()">Back to Previous Page</button>
	<h1>서비스 처리 과정에서 널(NULL) 에러가 발생하였습니다.</h1>
	<br />

	<p>
		<b>Error code:</b> ${pageContext.errorData.statusCode}
	</p>
	<p>
		<b>Request URI:</b>
		${pageContext.request.scheme}://${header.host}${pageContext.errorData.requestURI}
	</p>
	<br />
</body>
</html>
