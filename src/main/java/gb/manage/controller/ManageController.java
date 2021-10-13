package gb.manage.controller;

import gb.common.common.CommandMap;
import gb.common.service.CommonService;
import gb.common.util.EncryptUtil;
import gb.common.util.LoginManager;
import gb.manage.service.ManageService;
import gb.setup.service.SetupService;
import gb.summary.service.SummaryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ManageController {

	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "manageService")
	private ManageService manageService;

	@Resource(name = "summaryService")
	private SummaryService summaryService;
	
	@Resource(name = "setupService")
	private SetupService setupService;

	@Resource(name="commonService")
	private CommonService commonService;
	
	@Autowired
	ServletContext servletContext;
	
	private LoginManager loginManager = LoginManager.getInstance();
	
	@RequestMapping(value="/mng_usr")
	public ModelAndView mng_usr(CommandMap commandMap, HttpServletRequest request) throws Exception {
		
    	ModelAndView mv = new ModelAndView("/manage/user");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
    	
    	Map<String, Object> pmap = new HashMap<String, Object>();
		pmap.put("menu_cd", menu_cd);
		pmap.put("lst_bms_id", commandMap.get("bms_id"));
		pmap.put("lst_bts_id", commandMap.get("bts_id"));
		updateMenuCd(pmap, request);
    	
    	mv.addObject("menu_cd", menu_cd);
    	mv.addObject("pet_cod", pet_cod);
    	mv.addObject("menu_id", menu_id);
    	mv.addObject("menu_nm", menu_nm);
		mv.addObject("bms_id", commandMap.get("bms_id"));
		mv.addObject("bts_id", commandMap.get("bts_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvrNam(commandMap.getMap());
		if (btmap != null && btmap.get("bts_nam") != null) {
			mv.addObject("bts_nam", btmap.get("bts_nam"));
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "AT");
		List<Map<String,Object>> usrAthlist = setupService.findType(pmap);
		if (usrAthlist != null && usrAthlist.size() > 0) {
			mv.addObject("usrAthlist", usrAthlist);
		}
		
		mv.addObject("inp_usr_id", commandMap.get("inp_usr_id"));
		mv.addObject("insert_result", commandMap.get("insert_result"));
		mv.addObject("update_result", commandMap.get("update_result"));
		mv.addObject("delete_result", commandMap.get("delete_result"));
		
		/* 한페이지에 보여줄 Row수 지정할때 아래와같이 파라메터 넣어줌 */
		commandMap.put("pageCount", 5);
		commandMap.put("displayRow", 5);
		commandMap.put("TABLE", "USRTB");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap = manageService.findUsr(commandMap.getMap());
//	    mv.addObject("paginationInfo", (PaginationInfo)resultMap.get("paginationInfo"));
	    mv.addObject("pagination", resultMap.get("pagination"));
	    mv.addObject("list", resultMap.get("result"));
	    
		return mv;
	}
	
	@RequestMapping(value="/usr_info")
	public ModelAndView usr_info(CommandMap commandMap, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("jsonView");
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap = manageService.findUsrMap(commandMap.getMap());
		if (resultMap != null) {
			resultMap.put("pw", EncryptUtil.dec(resultMap.get("usr_pwd").toString()));
			mv.addObject("map", resultMap);
		}
		
		return mv;
	}
	
	
	@RequestMapping(value="/insertUsr")
	public ModelAndView insertUsr(CommandMap commandMap, HttpServletRequest request) throws Exception {
		
		ModelAndView mv = new ModelAndView("redirect:/mng_usr");
    	
    	String pw = EncryptUtil.enc(commandMap.get("usr_pwd").toString());
		commandMap.put("usr_pwd_crypt", pw);
    	boolean result = manageService.insertUsr(commandMap.getMap(), request);
    	mv.addObject("insert_result", result);
    	
    	mv.addObject("inp_usr_id", commandMap.get("usr_id"));
    	mv.addObject("menu_cd", commandMap.get("menu_cd"));
    	mv.addObject("menu_id", commandMap.get("menu_id"));
    	mv.addObject("bms_id", commandMap.get("bms_id"));
    	mv.addObject("bts_id", commandMap.get("bts_id"));
    	
		return mv;
    }
	
	@RequestMapping(value="/updateUsr")
	public ModelAndView updateUsr(CommandMap commandMap, HttpServletRequest request) throws Exception {
		
		ModelAndView mv = new ModelAndView("redirect:/mng_usr");
		
		String pw = EncryptUtil.enc(commandMap.get("usr_pwd").toString());
		commandMap.put("usr_pwd_crypt", pw);
		
		boolean result = manageService.updateUsr(commandMap.getMap(), request);
		mv.addObject("update_result", result);
		
		mv.addObject("inp_usr_id", commandMap.get("usr_id"));
		mv.addObject("menu_cd", commandMap.get("menu_cd"));
		mv.addObject("menu_id", commandMap.get("menu_id"));
		mv.addObject("bms_id", commandMap.get("bms_id"));
		mv.addObject("bts_id", commandMap.get("bts_id"));
		
		return mv;
	}
	
	@RequestMapping(value="/deleteUsr")
	public ModelAndView deleteUsr(CommandMap commandMap, HttpServletRequest request) throws Exception {
		
		ModelAndView mv = new ModelAndView("redirect:/mng_usr");
		mv.addObject("inp_usr_id", commandMap.get("usr_id"));
		boolean result = manageService.deleteUsr(commandMap.getMap(), request);
		mv.addObject("delete_result", result);
		mv.addObject("menu_cd", commandMap.get("menu_cd"));
		mv.addObject("menu_id", commandMap.get("menu_id"));
		mv.addObject("bms_id", commandMap.get("bms_id"));
		mv.addObject("bts_id", commandMap.get("bts_id"));
		
		return mv;
	}
	
    
    @RequestMapping(value="/mng_dft")
    public ModelAndView mng_dft(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/manage/default_set");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
    	
		Map<String, Object> pmap = new HashMap<String, Object>();
		pmap.put("menu_cd", menu_cd);
		pmap.put("lst_bms_id", commandMap.get("bms_id"));
		pmap.put("lst_bts_id", commandMap.get("bts_id"));
		updateMenuCd(pmap, request);
    	
    	mv.addObject("menu_cd", menu_cd);
    	mv.addObject("pet_cod", pet_cod);
    	mv.addObject("menu_id", menu_id);
    	mv.addObject("menu_nm", menu_nm);
		mv.addObject("bms_id", commandMap.get("bms_id"));
		mv.addObject("bts_id", commandMap.get("bts_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvrNam(commandMap.getMap());
		if (btmap != null && btmap.get("bts_nam") != null) {
			mv.addObject("bts_nam", btmap.get("bts_nam"));
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "M");
		List<Map<String,Object>> mlist = setupService.findType(pmap);
		if (mlist != null && mlist.size() > 0) {
			mv.addObject("mList", mlist);
		}
		
		pmap = new HashMap<String, Object>();
		pmap.put("pet_cod", "BT");
		List<Map<String,Object>> tylist = setupService.findType(pmap);
		if (tylist != null && tylist.size() > 0) {
			mv.addObject("tylist", tylist);
		}
		
		pmap = new HashMap<String, Object>();
		pmap.put("pet_cod", "CS");
		List<Map<String,Object>> list = setupService.findType(pmap);
		if (list != null && list.size() > 0) {
			mv.addObject("csList", list);
		}
		
		pmap = new HashMap<String, Object>();
		pmap.put("pet_cod", "UM");
		List<Map<String,Object>> umlist = setupService.findType(pmap);
		if (umlist != null && umlist.size() > 0) {
			mv.addObject("umlist", umlist);
		}
		
		pmap = new HashMap<String, Object>();
		pmap.put("pet_cod", "PR");
		List<Map<String,Object>> prlist = setupService.findType(pmap);
		if (prlist != null && prlist.size() > 0) {
			mv.addObject("prlist", prlist);
		}
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "DB");
		List<Map<String,Object>> dblist = setupService.findType(pmap);
		if (dblist != null && dblist.size() > 0) {
			mv.addObject("dblist", dblist);
		}
		pmap = new HashMap<String, Object>();
		pmap.put("pet_cod", "TR");
		List<Map<String,Object>> trlist = setupService.findType(pmap);
		if (trlist != null && trlist.size() > 0) {
			mv.addObject("trlist", trlist);
		}
		
    	return mv;
    }
    
    @RequestMapping(value="/findDftBakSet")
	public ModelAndView findDftBakSet(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	String usr_id = loginManager.getUserID(request.getSession());
    	commandMap.put("usr_id", usr_id);
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	Map<String,Object> dbsmap = manageService.findDftBakSet(commandMap.getMap());
		if (dbsmap != null) {
			mv.addObject("dbsmap", dbsmap);
		}
		
		return mv;
    }
    
    @RequestMapping(value="/insertDftBakSet")
	public ModelAndView insertDftBakSet(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	String usr_id = loginManager.getUserID(request.getSession());
    	commandMap.put("usr_id", usr_id);
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	boolean result = manageService.insertDftBakSet(commandMap.getMap(), request);
    	mv.addObject("result", result);
		
		return mv;
    }
    
    @RequestMapping(value="/deleteDftBakSet")
    public ModelAndView deleteDftBakSet(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	String usr_id = loginManager.getUserID(request.getSession());
    	commandMap.put("usr_id", usr_id);
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	boolean result = manageService.deleteDftBakSet(commandMap.getMap(), request);
    	mv.addObject("result", result);
    	
    	return mv;
    }
    
    @RequestMapping(value="/findDftBakMtd")
	public ModelAndView findDftBakMtd(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	String usr_id = loginManager.getUserID(request.getSession());
    	commandMap.put("usr_id", usr_id);
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	Map<String,Object> dbsmap = manageService.findDftBakMtd(commandMap.getMap());
		if (dbsmap != null) {
			mv.addObject("dbmmap", dbsmap);
		}
		
		return mv;
    }
    
    @RequestMapping(value="/insertDftBakMtd")
    public ModelAndView insertDftBakMtd(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	String usr_id = loginManager.getUserID(request.getSession());
    	commandMap.put("usr_id", usr_id);
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	boolean result = manageService.insertDftBakMtd(commandMap.getMap(), request);
    	mv.addObject("result", result);
    	
    	return mv;
    }
    
    @RequestMapping(value="/deleteDftBakMtd")
    public ModelAndView deleteDftBakMtd(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	String usr_id = loginManager.getUserID(request.getSession());
    	commandMap.put("usr_id", usr_id);
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	boolean result = manageService.deleteDftBakMtd(commandMap.getMap(), request);
    	mv.addObject("result", result);
    	
    	return mv;
    }

	@RequestMapping(value="/mng_svr")
	public ModelAndView mng_svr(CommandMap commandMap, HttpServletRequest request) throws Exception {
		
    	ModelAndView mv = new ModelAndView("/manage/server");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
    	
    	Map<String, Object> pmap = new HashMap<String, Object>();
		pmap.put("menu_cd", menu_cd);
		pmap.put("lst_bms_id", commandMap.get("bms_id"));
		pmap.put("lst_bts_id", commandMap.get("bts_id"));
		updateMenuCd(pmap, request);
    	
    	mv.addObject("menu_cd", menu_cd);
    	mv.addObject("pet_cod", pet_cod);
    	mv.addObject("menu_id", menu_id);
    	mv.addObject("menu_nm", menu_nm);
		mv.addObject("bms_id", commandMap.get("bms_id"));
		mv.addObject("bts_id", commandMap.get("bts_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvrNam(commandMap.getMap());
		if (btmap != null && btmap.get("bts_nam") != null) {
			mv.addObject("bts_nam", btmap.get("bts_nam"));
		}
		
		mv.addObject("inp_ms_nam", commandMap.get("inp_ms_nam"));
		mv.addObject("insert_result", commandMap.get("insert_result"));
		mv.addObject("update_result", commandMap.get("update_result"));
		mv.addObject("delete_result", commandMap.get("delete_result"));
		
		/* 한페이지에 보여줄 Row수 지정할때 아래와같이 파라메터 넣어줌
		commandMap.put("PAGE_ROW", 15);
		*/
		commandMap.put("pageCount", 5);
		commandMap.put("displayRow", 5);
		commandMap.put("TABLE", "MNGSVRTB");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		resultMap = manageService.findSvr(commandMap.getMap());
//	    mv.addObject("paginationInfo", (PaginationInfo)resultMap.get("paginationInfo"));
		mv.addObject("pagination", resultMap.get("pagination"));
	    mv.addObject("list", resultMap.get("result"));
		
		return mv;
	}
	
	@RequestMapping(value="/svr_info")
	public ModelAndView svr_info(CommandMap commandMap, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("jsonView");
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		
		resultMap = manageService.findSvrMap(commandMap.getMap());
		if (resultMap != null) {
			mv.addObject("map", resultMap);
		}
		
		return mv;
	}
	
	@RequestMapping(value="/insertSvr")
	public ModelAndView insertSvr(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
		ModelAndView mv = new ModelAndView("redirect:/mng_svr");
    	mv.addObject("inp_ms_nam", commandMap.get("ms_nam"));
    	
    	boolean result = manageService.insertSvr(commandMap.getMap(), request);
    	mv.addObject("insert_result", result);
    	mv.addObject("menu_cd", commandMap.get("menu_cd"));
    	mv.addObject("menu_id", commandMap.get("menu_id"));
    	mv.addObject("bms_id", commandMap.get("bms_id"));
    	mv.addObject("bts_id", commandMap.get("bts_id"));
    	
		return mv;
    }
	
	@RequestMapping(value="/updateSvr")
	public ModelAndView updateSvr(CommandMap commandMap, HttpServletRequest request) throws Exception {
		
		ModelAndView mv = new ModelAndView("redirect:/mng_svr");
		mv.addObject("inp_ms_nam", commandMap.get("ms_nam"));
		
		boolean result = manageService.updateSvr(commandMap.getMap(), request);
		mv.addObject("update_result", result);
		mv.addObject("menu_cd", commandMap.get("menu_cd"));
		mv.addObject("menu_id", commandMap.get("menu_id"));
		mv.addObject("bms_id", commandMap.get("bms_id"));
		mv.addObject("bts_id", commandMap.get("bts_id"));
		
		return mv;
	}
	
	@RequestMapping(value="/deleteSvrInfo")
	public ModelAndView deleteSvrInfo(CommandMap commandMap, HttpServletRequest request) throws Exception {
		
		ModelAndView mv = new ModelAndView("jsonView");
		List<Map<String, Object>> bmmap = setupService.findMngSvrInfo(commandMap.getMap());
		if(bmmap.size() == 0){
			mv.addObject("result", "doDelete");
			mv.addObject("delete_result", true);
		}else{
			mv.addObject("result", "");
		}
		
		return mv;
	}
	
	@RequestMapping(value="/deleteSvr")
	public ModelAndView deleteSvr(CommandMap commandMap, HttpServletRequest request) throws Exception {
		
		ModelAndView mv = new ModelAndView("redirect:/mng_svr");
		mv.addObject("inp_ms_nam", commandMap.get("ms_nam"));
		boolean result = manageService.deleteSvr(commandMap.getMap(), request);
		mv.addObject("delete_result", result);
		mv.addObject("menu_cd", commandMap.get("menu_cd"));
		mv.addObject("menu_id", commandMap.get("menu_id"));
		mv.addObject("bms_id", commandMap.get("bms_id"));
		mv.addObject("bts_id", commandMap.get("bts_id"));
		
		return mv;
	}
	
	
	@Value("#{config['license.file']}") private String lcn_dir;
	
	@RequestMapping(value="/mng_lcn")
	public ModelAndView mng_lcn(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/manage/license");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
    	
		Map<String, Object> pmap = new HashMap<String, Object>();
		pmap.put("menu_cd", menu_cd);
		pmap.put("lst_bms_id", commandMap.get("bms_id"));
		pmap.put("lst_bts_id", commandMap.get("bts_id"));
		updateMenuCd(pmap, request);
    	
    	mv.addObject("menu_cd", menu_cd);
    	mv.addObject("pet_cod", pet_cod);
    	mv.addObject("menu_id", menu_id);
    	mv.addObject("menu_nm", menu_nm);
		mv.addObject("bms_id", commandMap.get("bms_id"));
		mv.addObject("bts_id", commandMap.get("bts_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvrNam(commandMap.getMap());
		if (btmap != null && btmap.get("bts_nam") != null) {
			mv.addObject("bts_nam", btmap.get("bts_nam"));
		}
		
		Map<String, Object> lcnInfoMap = new HashMap<String, Object>();
//		String fileUrl = Thread.currentThread().getContextClassLoader().getResource(lcn_dir).getFile();
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
    
    
    
    
    
	
    public boolean updateMenuCd(String menu_cd, HttpServletRequest request) throws Exception {
    	String usr_id = loginManager.getUserID(request.getSession());
    	Map<String,Object> param = new HashMap<String, Object>();
    	param.put("lst_pag", menu_cd);
    	param.put("usr_id", usr_id);
    	return summaryService.updateMenuCd(param);
    }
    
    public boolean updateMenuCd(Map<String, Object> map, HttpServletRequest request) throws Exception {
    	String usr_id = loginManager.getUserID(request.getSession());
    	Map<String,Object> param = new HashMap<String, Object>();
    	param.put("lst_pag", map.get("menu_cd"));
    	param.put("lst_bms_id", map.get("lst_bms_id"));
    	param.put("lst_bts_id", map.get("lst_bts_id"));
    	param.put("usr_id", usr_id);
    	return summaryService.updateMenuCd(param);
    }
}
