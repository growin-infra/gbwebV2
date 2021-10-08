<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="org.apache.commons.lang.StringUtils"%>
<%@ page import="gb.common.util.LoginManager"%>
<%
	LoginManager loginManager = LoginManager.getInstance();
	loginManager.printloginUsers();
	
	String s_id = loginManager.getUserID(session);
	String s_pw = (String) session.getAttribute("pw");
	String usr_ath = (String) session.getAttribute("usr_ath");
	String remain_dt = (String) session.getAttribute("remainDt");
	String menu_cd = (String) request.getAttribute("menu_cd");
	String pet_cod = (String) request.getAttribute("pet_cod");
	String menu_id = (String) request.getAttribute("menu_id");
	String menu_nm = StringUtils.defaultIfEmpty((String) request.getAttribute("menu_nm"), "");
	String bms_id = StringUtils.defaultIfEmpty((String) request.getAttribute("bms_id"), "");
	String bts_id = StringUtils.defaultIfEmpty((String) request.getAttribute("bts_id"), "");
	String bmt_id = StringUtils.defaultIfEmpty((String) request.getAttribute("bmt_id"), "");
%>