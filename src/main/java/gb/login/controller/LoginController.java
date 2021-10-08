package gb.login.controller;

import gb.common.common.CommandMap;
import gb.common.service.CommonService;
import gb.common.util.EncryptUtil;
import gb.common.util.LoginManager;
import gb.login.service.LoginService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private LoginManager loginManager = LoginManager.getInstance();
	
	@Resource(name="loginService")
    private LoginService loginService;

	@Resource(name="commonService")
	private CommonService commonService;

	@RequestMapping(value = "/main")
	public ModelAndView main(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("/main");
		return mv;
	}
	
	@Autowired
	ServletContext servletContext;
	
	@Value("#{config['license.file']}") private String lcn_dir;
	@RequestMapping(value="/login_lcn_chk")
	public ModelAndView login_lcn_chk(CommandMap commandMap) throws Exception {
    	ModelAndView mv = new ModelAndView("jsonView");
    	Map<String, Object> lcnInfoMap = new HashMap<String, Object>();
    	String fileUrl = servletContext.getRealPath(lcn_dir);
    	if (fileUrl != null && !"".equals(fileUrl)) {
    		commandMap.put("fileUrl", fileUrl);
    		lcnInfoMap = commonService.lcnInfo(commandMap.getMap());
    	} else {
    		lcnInfoMap.put("msg", "라이선스 정보가 없습니다.");
    	}
    	mv.addObject("lcnInfoMap", lcnInfoMap);
		return mv;
    }
	
	@RequestMapping(value="/login")
    public ModelAndView login(CommandMap commandMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
		ModelAndView mv = new ModelAndView("jsonView");
		String pw = EncryptUtil.enc(commandMap.get("pw").toString());
		commandMap.put("usr_pwd", pw);
		List<Map<String,Object>> list = loginService.login(commandMap.getMap());
		if (list != null && list.size() > 0) {
			if (loginManager.getUserCount() > 0) {
				mv.addObject("session_msg", list.get(0).get("usr_id").toString());
				loginManager.removeSession(list.get(0).get("usr_id").toString());
			}
			
			loginManager.setSession(request.getSession(), list.get(0).get("usr_id").toString());
			
			request.getSession().setAttribute("pw", list.get(0).get("usr_pwd"));
			request.getSession().setAttribute("lst_pag", list.get(0).get("lst_pag"));
			request.getSession().setAttribute("usr_ath", list.get(0).get("usr_ath"));
			request.getSession().setAttribute("remainDt", commandMap.get("remainDt").toString());
			
			mv.addObject("result", true);
			mv.addObject("lst_pag", list.get(0).get("lst_pag"));
			mv.addObject("usr_ath", list.get(0).get("usr_ath"));
			
		} else {
			mv.addObject("result", false);
		}
		
    	return mv;
    }
	
	@RequestMapping(value="/logout")
	public ModelAndView logout(CommandMap commandMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView("/main");
		request.getSession().invalidate();
		return mv;
	}

}


