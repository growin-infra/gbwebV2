package gb.common.controller;

import gb.common.common.CommandMap;
import gb.common.util.LoginManager;
import gb.summary.service.SummaryService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "summaryService")
	private SummaryService summaryService;
	
	private LoginManager loginManager = LoginManager.getInstance();
	
	@RequestMapping(value = "/error/404")
	public String error404(Locale locale, HttpServletRequest request, HttpServletResponse response, HttpSession session , Model model, CommandMap commandMap) throws Exception {
		response.setStatus(HttpServletResponse.SC_OK);
		boolean isAjaxReq = this.isAjaxReq(request);
		if (!isAjaxReq) {
			commandMap.put("lst_pag", "A01");
			updateMenuCd(commandMap.getMap(), request);
		}
		return "/common/error/404_error";
	}
	
	@RequestMapping(value = "/error/500")
	public String error500(Locale locale, HttpServletRequest request, HttpServletResponse response, HttpSession session,  Model model, CommandMap commandMap) throws Exception {
		response.setStatus(HttpServletResponse.SC_OK);
		model.addAttribute("msg", "500 Error");
		
		//ajax 유무
		boolean isAjaxReq = this.isAjaxReq(request);
		if (!isAjaxReq) {
			commandMap.put("lst_pag", "A010101");
			updateMenuCd(commandMap.getMap(), request);
		} else {
			return "오류발생";
		}
		return "/common/error/500_error";
	}
	
	
	@RequestMapping(value = "/error/null")
	public String errorNull(Locale locale, HttpServletRequest request, HttpServletResponse response, HttpSession session,  Model model, CommandMap commandMap) throws Exception {
		response.setStatus(HttpServletResponse.SC_OK);
		model.addAttribute("msg", "Exception");
		//ajax 유무
		boolean isAjaxReq = this.isAjaxReq(request);
		if (!isAjaxReq) {
			commandMap.put("lst_pag", "A010101");
			updateMenuCd(commandMap.getMap(), request);
		}
		
		return "/common/error/500_error";
	}
	
	
    public boolean updateMenuCd(Map<String, Object> map, HttpServletRequest request) throws Exception {
    	String usr_id = loginManager.getUserID(request.getSession());
    	Map<String,Object> param = new HashMap<String, Object>();
    	param.put("lst_pag", map.get("lst_pag"));
    	param.put("lst_bms_id", map.get("lst_bms_id"));
    	param.put("lst_bts_id", map.get("lst_bts_id"));
    	param.put("usr_id", usr_id);
    	return summaryService.updateMenuCd(param);
    }
	
	public boolean isAjaxReq(HttpServletRequest request) {
		boolean check = false;
		if ("XMLHttpRequest".equals(request.getHeader("x-requested-with"))) {
			check = true;
		}
		return check;
	}

}
