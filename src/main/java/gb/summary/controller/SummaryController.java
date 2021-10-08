package gb.summary.controller;

import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;
import gb.common.common.CommandMap;
import gb.common.service.Ssh2Service;
import gb.common.util.ExcelManager;
import gb.common.util.LoginManager;
import gb.common.util.StringUtil;
import gb.manage.service.ManageService;
import gb.setup.service.SetupService;
import gb.summary.service.SummaryService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SummaryController {

	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "summaryService")
	private SummaryService summaryService;
	
	@Resource(name = "setupService")
	private SetupService setupService;
	
	@Resource(name = "manageService")
	private ManageService manageService;
	
	@Resource(name = "ssh2Service")
	private Ssh2Service ssh2Service;
	
	@Resource(name = "excelManager")
	private ExcelManager excelManager;
	
	private LoginManager loginManager = LoginManager.getInstance();
	
	@Value("#{ssh['ssh.XXIII.1']}") private String sshXXIII1;
	@Value("#{ssh['ssh.XXIII.3']}") private String sshXXIII3;
	
    @RequestMapping(value="/smy_rbs")
	public ModelAndView smy_rbs(CommandMap commandMap, HttpServletRequest request ,  HttpServletResponse response) throws Exception {
    	//response.sendError(HttpServletResponse.SC_NOT_FOUND);
    	
    	String menu_cd = "A010101", pet_cod = "A0101";
    	String menu_id = "/smy_rbs", rbsView="/summary/recent_backup_status";
		String lst_bms_id = "", lst_bts_id = "";
//		request.getSession().setAttribute("lst_pag", menu_cd);
		
		ModelAndView mv = new ModelAndView();
		if (!menu_cd.equals(commandMap.get("menu_cd")) && !"A01".equals(commandMap.get("menu_cd"))) {
			Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
			if (mcmap != null) {
				menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
				pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
				menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
				
//				String id = (String)request.getSession().getAttribute("id");
				String id = loginManager.getUserID(request.getSession());
				Map<String,Object> usrmap = summaryService.findUsrLstInfo(id);
				if (usrmap != null) {
					if (usrmap.get("lst_bms_id") != null) {
						lst_bms_id = usrmap.get("lst_bms_id").toString();
					}
					mv.addObject("bms_id", lst_bms_id);
					if (usrmap.get("lst_bts_id") != null) {
						lst_bts_id = usrmap.get("lst_bts_id").toString();
					}
					mv.addObject("bts_id", lst_bts_id);
				}
				
				rbsView = "redirect:"+menu_id;
			}
		} else {
	    	
			try {
				if ("1".equals(commandMap.get("currentPageNo"))) {
					updateMenuCd(menu_cd, request);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("smy_rbs Exception : ", e.fillInStackTrace());
			}
			
			commandMap.put("knd", "G01");
			/* 한페이지에 보여줄 Row수 지정할때 아래와같이 파라메터 넣어줌
			commandMap.put("PAGE_ROW", 15);
			*/
			commandMap.put("PAGE_ROW", 10);
			Map<String,Object> resultMap = new HashMap<String, Object>();
			resultMap = summaryService.findStt(commandMap.getMap());
		    mv.addObject("paginationInfo", (PaginationInfo)resultMap.get("paginationInfo"));
		    mv.addObject("list", resultMap.get("result"));
		    mv.addObject("cpno", commandMap.get("currentPageNo")!=null?commandMap.get("currentPageNo"):"1");
		    
		}
		mv.addObject("menu_cd", menu_cd);
		mv.addObject("pet_cod", pet_cod);
		mv.addObject("menu_id", menu_id);
		mv.setViewName(rbsView);
		
		return mv;
	}
	
    @RequestMapping(value = "smy_rbs_excel")
    public void smy_rbs_excel(CommandMap commandMap , HttpServletRequest request, HttpServletResponse response) {
    	
		try {
			commandMap.put("knd", "G01");
			Map<String,Object> result = new HashMap<String, Object>();
			result = summaryService.findStt(commandMap.getMap());
			ArrayList<Map<String , Object>>  list = (ArrayList<Map<String, Object>>) result.get("result");
			
			PaginationInfo paginationInfo = new PaginationInfo();
			paginationInfo = (PaginationInfo) result.get("paginationInfo");
			
			int total = paginationInfo.getTotalPageCount();
			String crr = commandMap.get("cpno").toString();
			
//			List<Map<String, Object>> list = summaryService.findSttAll();
			//시간 시/분/초로 변경
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> item = list.get(i);
				item.put("pas_tme", StringUtil.secondToStr(item.get("pas_tme").toString()));
				item.put("kep_pod", item.get("kep_pod")+"일");
			}
			
			Map<String, Object> title = new HashMap<String , Object>(); //Excel Title
//			title.put("title", commandMap.get("excel_title"));
			String t = commandMap.get("excel_title").toString();
			title.put("title", t+"   "+crr+" / "+total);
			Map<String , Object> beans = new HashMap<String , Object>();
	    	beans.put("result", list);
	    	beans.put("title", title);
	    	
	    	excelManager.download(request, response, beans, excelManager.get_Filename("Ginian_"), "templete.xlsx");
	    	
		} catch (Exception e) {
			e.printStackTrace();
			log.error("smy_rbs_excel Exception", e.fillInStackTrace());
		}
    }    
    
    
    @RequestMapping(value="/smy_disk_use")
    public ModelAndView smy_disk_use(CommandMap commandMap) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
//	    //백업서버
//	    /* ※ 디스크 사용률
//    	 * 	1. "XXIII" : 백업디렉토리 ==> 디스크 사용량 조회
//    	 * 		df \"%s\" | tail -1 | awk '{print $(NF-1)}'
//    	 */
	    List<Map<String,Object>> list = manageService.findSvrList(null);
	    Map<String, Object> diskUseMap = null;
	    if (list != null && list.size() > 0) {
	    	diskUseMap = new HashMap<String, Object>();
	    	diskUseMap.put("list", list);
	    }
	    mv.addObject("disklist", list);
	    mv.addObject("disklistMap", diskUseMap);
    	
    	return mv;
    }
    
    
    
    @RequestMapping(value="/smy_rrs")
	public ModelAndView smy_rrs(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	String menu_cd = "A010102", pet_cod = "A0101", menu_id = "/smy_rrs";
//    	request.getSession().setAttribute("lst_pag", menu_cd);
    	try {
    		if ("1".equals(commandMap.get("currentPageNo"))) {
    			updateMenuCd(menu_cd, request);
    		}
		} catch (Exception e) {
			log.error("smy_rrs Exception : ", e.fillInStackTrace());
		}
    	
    	ModelAndView mv = new ModelAndView("/summary/recent_recovery_status");
		
		mv.addObject("menu_cd", menu_cd);
		mv.addObject("pet_cod", pet_cod);
		mv.addObject("menu_id", menu_id);
		
		commandMap.put("knd", "G02");
		/* 한페이지에 보여줄 Row수 지정할때 아래와같이 파라메터 넣어줌
		commandMap.put("PAGE_ROW", 15);
		*/
		commandMap.put("PAGE_ROW", 10);
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap = summaryService.findStt(commandMap.getMap());
	    mv.addObject("paginationInfo", (PaginationInfo)resultMap.get("paginationInfo"));
	    mv.addObject("list", resultMap.get("result"));
	    mv.addObject("cpno", commandMap.get("currentPageNo")!=null?commandMap.get("currentPageNo"):"1");
	    
		return mv;
	}
    @RequestMapping(value = "smy_rrs_excel")
    public void smy_rrs_excel(CommandMap commandMap , HttpServletRequest request, HttpServletResponse response) {
    	
		try {
			commandMap.put("knd", "G02");
			Map<String,Object> result = new HashMap<String, Object>();
			result = summaryService.findStt(commandMap.getMap());
			ArrayList<Map<String , Object>>  list = (ArrayList<Map<String, Object>>) result.get("result");
			
			PaginationInfo paginationInfo = new PaginationInfo();
			paginationInfo = (PaginationInfo) result.get("paginationInfo");
			
			int total = paginationInfo.getTotalPageCount();
			String crr = commandMap.get("cpno").toString();
			
//			List<Map<String, Object>> list = summaryService.findSttAll();
			//시간 시/분/초로 변경
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> item = list.get(i);
				item.put("pas_tme", StringUtil.secondToStr(item.get("pas_tme").toString()));
				item.put("kep_pod", item.get("kep_pod")+"일");
			}
			
			Map<String, Object> title = new HashMap<String , Object>(); //Excel Title
//			title.put("title", commandMap.get("excel_title"));
			String t = commandMap.get("excel_title").toString();
			title.put("title", t+"   "+crr+" / "+total);
			Map<String , Object> beans = new HashMap<String , Object>();
	    	beans.put("result", list);
	    	beans.put("title", title);
	    	
	    	excelManager.download(request, response, beans, excelManager.get_Filename("Ginian_"), "templete.xlsx");
	    	
		} catch (Exception e) {
			e.printStackTrace();
			log.error("smy_rrs_excel Exception", e.fillInStackTrace());
		}
    }    

    
    
    
    @RequestMapping(value="/smy_ss")
    public ModelAndView smy_ss(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	String menu_cd = "A010103", pet_cod = "A0101", menu_id = "/smy_ss";
//    	request.getSession().setAttribute("lst_pag", menu_cd);
    	try {
    		if ("1".equals(commandMap.get("currentPageNo"))) {
    			updateMenuCd(menu_cd, request);
    		}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("smy_ss Exception : ", e.fillInStackTrace());
		}
    	
    	ModelAndView mv = new ModelAndView("/summary/schedule_status");
    	
		mv.addObject("menu_cd", menu_cd);
		mv.addObject("pet_cod", pet_cod);
		mv.addObject("menu_id", menu_id);
		
		/* 한페이지에 보여줄 Row수 지정할때 아래와같이 파라메터 넣어줌
		commandMap.put("PAGE_ROW", 15);
		*/
		commandMap.put("PAGE_ROW", 10);
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap = summaryService.findScd(commandMap.getMap());
	    mv.addObject("paginationInfo", (PaginationInfo)resultMap.get("paginationInfo"));
	    mv.addObject("list", resultMap.get("result"));
	    mv.addObject("cpno", commandMap.get("currentPageNo")!=null?commandMap.get("currentPageNo"):"1");
		
    	return mv;
    }
	
    @RequestMapping(value = "smy_ss_excel")
    public void smy_ss_excel(CommandMap commandMap , HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String,Object> result = new HashMap<String, Object>();
			result = summaryService.findScd(commandMap.getMap());
			ArrayList<Map<String , Object>>  list = (ArrayList<Map<String, Object>>) result.get("result");
			
			PaginationInfo paginationInfo = new PaginationInfo();
			paginationInfo = (PaginationInfo) result.get("paginationInfo");
			
			int total = paginationInfo.getTotalPageCount();
			String crr = commandMap.get("cpno").toString();
			
//			List<Map<String, Object>> list = summaryService.findScdAll();
			//시간 시/분/초로 변경
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> item = list.get(i);
				item.put("tme_set", StringUtil.numberToTime(item.get("tme_set").toString()));
				item.put("kep_pod", item.get("kep_pod")+"일");
			}
			
			String t = commandMap.get("excel_title").toString();
			Map<String, Object> title = new HashMap<String , Object>(); //Excel Title
			title.put("title", t+"   "+crr+" / "+total);
			Map<String , Object> beans = new HashMap<String , Object>();
	    	beans.put("result", list);
	    	beans.put("title", title);
	    	
	    	excelManager.download(request, response, beans, excelManager.get_Filename("Ginian_"), "templete_scd.xlsx");
	    	
		} catch (Exception e) {
			e.printStackTrace();
			log.error("smy_ss_excel Exception", e.fillInStackTrace());
		}
    }    
    
    
    @RequestMapping(value="/disk_use")
    public ModelAndView disk_use(CommandMap commandMap) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	String dir = commandMap.get("dir").toString();
    	String ip = commandMap.get("ip").toString();
    	int port = Integer.parseInt(commandMap.get("port").toString());
    	String usr = commandMap.get("usr").toString();
    	String pwd = commandMap.get("pwd").toString();
    	String nam = commandMap.get("nam").toString();
    	mv.addObject("nam", nam);
    	
    	/* ※ 디스크 사용률
    	 * 	1. "XXIII" : 백업디렉토리 ==> 디스크 사용량 조회
    	 * 		df \"%s\" | tail -1 | awk '{print $(NF-1)}'
    	 */
    	String diskUseCmd = MessageFormat.format(sshXXIII1, dir);
    	Map<String, Object> diskUseMap = new HashMap<String, Object>();
    	diskUseMap = ssh2Service.getData(diskUseCmd, usr, pwd, ip, port, 3);
    	String diskUseVal = "0";
    	if (diskUseMap.get("value") != null) {
    		diskUseVal = diskUseMap.get("value").toString();
    	}
    	mv.addObject("diskUseVal", diskUseVal.replace("%", ""));
    	
    	/* ※ 디스크 용량 (사용량 / 전체)
    	 * 	df /MARIA_BACKUP | tail -1 | awk '{print $(NF-3)"_"$(NF-2)}'
    	 */
    	String diskInfoCmd = MessageFormat.format(sshXXIII3, dir);
    	Map<String, Object> diskInfoMap = new HashMap<String, Object>();
    	diskInfoMap = ssh2Service.getData(diskInfoCmd, usr, pwd, ip, port, 3);
    	String value = null;
    	String used = "0", total = "0"; 
    	if (diskInfoMap != null) {
    		value = diskInfoMap.get("value").toString();
    		String arrVal[] = value.split("_");
    		used = arrVal[0];
    		total = arrVal[1];
    	}
    	mv.addObject("diskTotal", total);
    	mv.addObject("diskUsed", used);
    	
    	return mv;
    }
    
    
    
    public boolean updateMenuCd(String menu_cd, HttpServletRequest request) throws Exception {
//    	String usr_id = (String) request.getSession().getAttribute("id");
    	String usr_id = loginManager.getUserID(request.getSession());
    	Map<String,Object> param = new HashMap<String, Object>();
    	param.put("lst_pag", menu_cd);
    	param.put("usr_id", usr_id);
    	return summaryService.updateMenuCd(param);
    }

}
