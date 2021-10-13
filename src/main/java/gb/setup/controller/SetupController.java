package gb.setup.controller;

import gb.common.agent.DBAgent;
import gb.common.agent.Ssh2Agent;
import gb.common.common.CommandMap;
import gb.common.interceptor.Interceptor;
import gb.common.service.CommonService;
import gb.common.service.Ssh2Service;
import gb.common.util.Base64Util;
import gb.common.util.Constants;
import gb.common.util.LoginManager;
import gb.common.util.StringUtil;
import gb.manage.service.ManageService;
import gb.schedule.BackupScheduler;
import gb.setup.service.SetupService;
import gb.summary.service.SummaryService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SuppressWarnings({"rawtypes","unchecked","unused"})
public class SetupController {

	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "setupService")
	private SetupService setupService;
	
	@Resource(name = "summaryService")
	private SummaryService summaryService;
	
	@Resource(name = "manageService")
	private ManageService manageService;
	
	@Resource(name = "ssh2Service")
	private Ssh2Service ssh2Service;
	
	@Resource(name = "base64Util")
	private Base64Util base64Util;

	@Resource(name="commonService")
	private CommonService commonService;
	
	@Autowired
	ServletContext servletContext;
	
	private LoginManager loginManager = LoginManager.getInstance();
	
    @RequestMapping(value="/set_bms")
	public ModelAndView set_bms(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
		ModelAndView mv = new ModelAndView("/setup/backup_mng_status");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		updateMenuCd(menu_cd, request);
		
		mv.addObject("menu_cd", menu_cd);
		mv.addObject("pet_cod", pet_cod);
		mv.addObject("menu_id", menu_id);
		mv.addObject("menu_nm", menu_nm);
		
		return mv;
	}
    
    @RequestMapping(value="/findTree")
	public ModelAndView findTree(CommandMap commandMap) throws Exception {
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	List<Map<String,Object>> list = setupService.findTree(commandMap.getMap());
		if (list != null && list.size() > 0) {
			mv.addObject("list", list);
		}
		
		if (commandMap.get("bmt_pid") != null) {
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("ms_id", commandMap.get("bmt_pid"));
			Map<String,Object> msmap = manageService.findSvrMap(param);
			if (msmap != null) {
				mv.addObject("msmap", msmap);
			}
		}
		
		return mv;
    }
    
    @RequestMapping(value="/findTreeLv1")
	public ModelAndView findTreeLv1(CommandMap commandMap) throws Exception {
    	ModelAndView mv = new ModelAndView("jsonView");
    	
		Map<String,Object> bmmap = setupService.findMngSvr(commandMap.getMap());
		if (bmmap != null) {
			mv.addObject("bmmap", bmmap);
			if (bmmap.get("bms_pid") != null) {
				Map<String,Object> param = new HashMap<String,Object>();
				param.put("ms_id", bmmap.get("bms_pid"));
				Map<String,Object> msmap = manageService.findSvrMap(param);
				if (msmap != null) {
					mv.addObject("msmap", msmap);
				}
			}
		}
		return mv;
    }
    
    @RequestMapping(value="/findTreeLv2")
	public ModelAndView findTreeLv2(CommandMap commandMap) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	Map<String,Object> param = new HashMap<String,Object>();
    	if (commandMap.get("id") != null) {
    		param.put("ms_id", commandMap.get("id").toString().substring(0, 1));
    		Map<String,Object> msmap = manageService.findSvrMap(param);
    		if (msmap != null) {
    			mv.addObject("ms_id", msmap.get("ms_id"));
    		}
    	}
    	
    	param = new HashMap<String,Object>();
    	param.put("bms_id", commandMap.get("bms_id"));
    	param.put("bms_pid", commandMap.get("bms_id").toString().substring(0, 1));
    	Map<String,Object> bmmap = setupService.findMngSvr(param);
		if (bmmap != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
    	
		Map<String,Object> btmap = setupService.findTgtSvr(commandMap.getMap());
		if (btmap != null) {
			mv.addObject("btmap", btmap);
		}
		
		return mv;
    }
    
    @Value("#{config['license.file']}") private String lcn_dir;
    @RequestMapping(value="/lcn_chk")
	public ModelAndView lcn_chk(CommandMap commandMap) throws Exception {
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
    
    @RequestMapping(value="/insertTreeLv1")
    public ModelAndView insertTreeLv1(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	mv.addObject("nodename", commandMap.get("bms_nam"));
    	
    	boolean result = setupService.updateMngSvr(commandMap.getMap(), request);
    	mv.addObject("result", result);
    	
    	return mv;
    }
    
    @RequestMapping(value="/insertTreeLv2")
	public ModelAndView insertTreeLv2(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	mv.addObject("nodename", commandMap.get("bts_nam"));
    	
    	boolean result = setupService.updateTgtSvr(commandMap.getMap(), request);
    	mv.addObject("result", result);
    	
		return mv;
    }
    
    @RequestMapping(value="/deleteTreeLv1")
	public ModelAndView deleteTreeLv1(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	mv.addObject("nodename", commandMap.get("bms_nam"));
    	String result = setupService.deleteMngSvr(commandMap.getMap(), request);
    	mv.addObject("result", result);
    	mv.addObject("scd_nam", commandMap.get("scd_nam"));
    	return mv;
	
    }
    
    @RequestMapping(value="/deleteTreeLv2")
	public ModelAndView deleteTreeLv2(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	ModelAndView mv = new ModelAndView("jsonView");
    	mv.addObject("nodename", commandMap.get("bts_nam"));
    	
    	String result = setupService.deleteTgtSvr(commandMap.getMap(), request);
    	mv.addObject("result", result);
    	mv.addObject("scd_nam", commandMap.get("scd_nam"));
    	return mv;
    }
    
    @RequestMapping(value="/conn_test")
    public ModelAndView conn_test(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	ModelAndView mv = new ModelAndView("jsonView");
		
		String bts_usr = commandMap.get("bts_usr").toString();
		String bts_pwd = commandMap.get("bts_pwd").toString();
		String bts_ip = commandMap.get("bts_ip").toString();
		String bts_pot = commandMap.get("bts_pot").toString();
		
		//관리서버
    	Map<String,Object> param = new HashMap<String,Object>();
		param.put("ms_id", commandMap.get("bms_id").toString().charAt(0));
		Map<String,Object> msmap = manageService.findSvrMap(param);
		String ms_ip = "";
		int ms_port = 0;
		String ms_usr = "";
		String ms_pwd = "";
		String ms_bny_pth = "";
		if (msmap != null) {
			ms_ip = msmap.get("ms_ip").toString();
			ms_port = Integer.valueOf(msmap.get("ms_port").toString());
			ms_usr = msmap.get("ms_usr").toString();
			ms_pwd = msmap.get("ms_pwd").toString();
			ms_bny_pth = msmap.get("ms_bny_pth").toString();
			
			String tbscmd = ms_bny_pth+"/mysql -u"+bts_usr+" -p"+bts_pwd+" -h "+bts_ip+" -P "+bts_pot+" -BNse\"select 1\"";
			Map<String,Object> tdbsMap = new HashMap<String,Object>();
			tdbsMap = ssh2Service.getData(tbscmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
			boolean result = false;
			if (tdbsMap != null && tdbsMap.get("value") != null) {
				if ("1".equals(tdbsMap.get("value"))) {
					result = true;
				}
			}
			mv.addObject("result", result);
		}
    	return mv;
    }
    
    @RequestMapping(value="/ssh_conn_test")
    public ModelAndView ssh_conn_test(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	ModelAndView mv = new ModelAndView("jsonView");
		
		String os_id = commandMap.get("os_id").toString();
		String os_pwd = commandMap.get("os_pwd").toString();
		String bts_ip = commandMap.get("bts_ip").toString();
		
		//관리서버
    	Map<String,Object> param = new HashMap<String,Object>();
		param.put("ms_id", commandMap.get("bms_id").toString().charAt(0));
		Map<String,Object> msmap = manageService.findSvrMap(param);
		String ms_ip = "";
		int ms_port = 0;
		String ms_usr = "";
		String ms_pwd = "";
		String ms_bny_pth = "";
		if (msmap != null) {
			ms_ip = msmap.get("ms_ip").toString();
			ms_port = Integer.valueOf(msmap.get("ms_port").toString());
			ms_usr = msmap.get("ms_usr").toString();
			ms_pwd = msmap.get("ms_pwd").toString();
			ms_bny_pth = msmap.get("ms_bny_pth").toString();
			
//			String tbscmd = "ssh -t -t "+os_id+"@"+bts_ip+" \"echo 1\"";
			String tbscmd = "sshpass -p"+os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+os_id+"@"+bts_ip+" \"echo 1\"";
			Map<String,Object> tdbsMap = new HashMap<String,Object>();
//			System.out.println("=========tbscmd==========");
//			System.out.println(tbscmd);
//			System.out.println("=========/tdbsMap=========");
			tdbsMap = ssh2Service.getData(tbscmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
			boolean result = false;
			if (tdbsMap != null && tdbsMap.get("value") != null) {
				if ("1".equals(tdbsMap.get("value"))) {
					result = true;
				}
			}
			mv.addObject("result", result);
		}
    	return mv;
    }
    
    @RequestMapping(value="/ssh_conn_test2")
    public ModelAndView ssh_conn_test2(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	ModelAndView mv = new ModelAndView("jsonView");
		
		String id = commandMap.get("id").toString();
		String pwd = commandMap.get("pwd").toString();
		String ip = commandMap.get("ip").toString();
		String pot = commandMap.get("pot").toString();
		
		//관리서버
    	Map<String,Object> param = new HashMap<String,Object>();
		param.put("ms_id", commandMap.get("bms_id").toString().charAt(0));
		Map<String,Object> msmap = manageService.findSvrMap(param);
		String ms_ip = "";
		int ms_port = 0;
		String ms_usr = "";
		String ms_pwd = "";
		String ms_bny_pth = "";
		if (msmap != null) {
			ms_ip = msmap.get("ms_ip").toString();
			ms_port = Integer.valueOf(msmap.get("ms_port").toString());
			ms_usr = msmap.get("ms_usr").toString();
			ms_pwd = msmap.get("ms_pwd").toString();
			ms_bny_pth = msmap.get("ms_bny_pth").toString();
			
			String tbscmd = "sshpass -p"+pwd+" ssh -t -t -o StrictHostKeyChecking=no "+id+"@"+ip+" -p"+pot+" \"echo 1\"";
			Map<String,Object> tdbsMap = new HashMap<String,Object>();
			tdbsMap = ssh2Service.getData(tbscmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
			boolean result = false;
			if (tdbsMap != null && tdbsMap.get("value") != null) {
				if ("1".equals(tdbsMap.get("value"))) {
					result = true;
				}
			}
			mv.addObject("result", result);
		}
    	return mv;
    }
    
    
    @RequestMapping(value="/set_b_rec")
    public ModelAndView set_b_rec(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/backup/history");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		Map<String,Object> pmap = new HashMap<String,Object>();
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
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvrNam(commandMap.getMap());
		if (btmap != null && btmap.get("bts_nam") != null) {
			mv.addObject("bts_nam", btmap.get("bts_nam"));
		}
		
		commandMap.put("knd", "G01");
    	List<Map<String,Object>> isList = setupService.findIsLog(commandMap.getMap());
		if (isList != null && isList.size() > 0) {
			mv.addObject("isList", isList);
		}
    	return mv;
    }
    
    @RequestMapping(value="/set_b_rec_log")
	public ModelAndView set_b_rec_log(CommandMap commandMap) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	if (commandMap.get("day") != null && !"".equals(commandMap.get("day"))) {
    		String sDay = StringUtil.dateFormatStr(commandMap.get("day").toString(),"yyyy-MM-dd");
    		if (StringUtil.toDayCompare(commandMap.get("day").toString())) {
    			sDay += " (오늘)";
    		}
    		mv.addObject("sDay", sDay);
    	}
    	
    	commandMap.put("knd", "G01");
    	List<Map<String,Object>> list = setupService.findLogMM(commandMap.getMap());
		mv.addObject("list", list);
    	
		return mv;
    }
    
    @RequestMapping(value="/set_b_rec_logdetail")
	public ModelAndView set_b_rec_logdetail(CommandMap commandMap) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	commandMap.put("knd", "G01");
    	Map<String,Object> logmap = setupService.findLogDetail(commandMap.getMap());
		StringBuilder resmap = new StringBuilder();
		if (logmap != null) {
			StringBuilder line = new StringBuilder();
			line.append(logmap.get("log"));
			if (line.toString().split("\n").length > 5000) {
				String[] lines = line.toString().split("\n");
				for (int i=0; i<5000; i++) {
					resmap.append(lines[i]);
					if (i != 4999) {
						resmap.append("\n");
					} else {
						resmap.append("\n");
						resmap.append("................................................... the last part omitted");
					}
				}
			} else {
				resmap.append(logmap.get("log"));
			}
			mv.addObject("logmap", resmap);
		}
		
		return mv;
    }
    
    @Value("#{config['dbms.driver']}") private String dc;
    @Value("#{sql['dbms.sql5']}") private String sql5;
    @Value("#{sql['dbms.sql6']}") private String sql6;
    
    @RequestMapping(value="/set_b_set")
    public ModelAndView set_b_set(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/backup/setting");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		Map<String,Object> pmap = new HashMap<String,Object>();
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
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvr(commandMap.getMap());
		if (bmmap != null) {
			mv.addObject("bmmap", bmmap);
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvr(commandMap.getMap());
		if (btmap != null) {
			mv.addObject("dbsvrmap", btmap);
			mv.addObject("bts_nam", btmap.get("bts_nam"));
		}
		
		Map<String,Object> bsmap = setupService.findBakSet(commandMap.getMap());
		if (bsmap != null) {
			mv.addObject("bsmap", bsmap);
		}else{
			String usr_id = loginManager.getUserID(request.getSession());
	    	commandMap.put("usr_id", usr_id);
	    	Map<String,Object> dbsmap = manageService.findDftBakSet(commandMap.getMap());
	    	
			if (dbsmap != null && usr_id.equals(dbsmap.get("usr_id"))) {
				mv.addObject("bsmap", dbsmap);
			}
		}
		
//		Map<String,Object> dbsvrmap = setupService.findTgtSvr(commandMap.getMap());
//		if (dbsvrmap != null) {
//			mv.addObject("dbsvrmap", dbsvrmap);
//		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "BT");
		List<Map<String,Object>> tylist = setupService.findType(pmap);
		if (tylist != null && tylist.size() > 0) {
			mv.addObject("tylist", tylist);
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "DB");
		List<Map<String,Object>> dblist = setupService.findType(pmap);
		if (dblist != null && dblist.size() > 0) {
			mv.addObject("dblist", dblist);
		}
		
		mv.addObject("insert_result", commandMap.get("insert_result"));
		
    	return mv;
    }
    
    @RequestMapping(value="/set_bt_dblist")
	public ModelAndView set_bt_dblist(CommandMap commandMap) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	mv.addObject("bak_typ", commandMap.get("bak_typ"));
    	mv.addObject("bak_typ_itm", commandMap.get("bak_typ_itm"));
    	
    	Map<String,Object> dbsvrmap = setupService.findTgtSvr(commandMap.getMap());
		if (dbsvrmap != null) {
			
			String bts_usr = dbsvrmap.get("bts_usr").toString();
			String bts_pwd = dbsvrmap.get("bts_pwd").toString();
			String bts_ip = dbsvrmap.get("bts_ip").toString();
			String bts_pot = dbsvrmap.get("bts_pot").toString();
			
			//관리서버
	    	Map<String,Object> param = new HashMap<String,Object>();
			param.put("ms_id", commandMap.get("bms_id").toString().charAt(0));
			Map<String,Object> msmap = manageService.findSvrMap(param);
			String ms_ip = "";
			int ms_port = 0;
			String ms_usr = "";
			String ms_pwd = "";
			String ms_bny_pth = "";
			if (msmap != null) {
				ms_ip = msmap.get("ms_ip").toString();
				ms_port = Integer.valueOf(msmap.get("ms_port").toString());
				ms_usr = msmap.get("ms_usr").toString();
				ms_pwd = msmap.get("ms_pwd").toString();
				ms_bny_pth = msmap.get("ms_bny_pth").toString();
				
				String sql = "SELECT table_schema, size, (SELECT CASE WHEN cnt REGEXP '[^0-9]' THEN cnt ELSE FORMAT(cnt, 0) END) cnt FROM ( SELECT table_schema, CONCAT(round(sum(data_length+index_length)/(1024*1024),1),' MB') size , COUNT(*) cnt FROM information_schema.tables GROUP BY table_schema) A";
				String dbscmd = ms_bny_pth+"/mysql -u"+bts_usr+" -p"+bts_pwd+" -h "+bts_ip+" -P "+bts_pot+" -BNse\""+sql+"\"";
				Map<String,Object> dbsMap = new HashMap<String,Object>();
				dbsMap = ssh2Service.getList(dbscmd, ms_usr, ms_pwd, ms_ip, ms_port);
				if (dbsMap != null) {
					List<String> list = new ArrayList<String>();
					List<Object> putlist = new ArrayList<Object>();
					list = (List) dbsMap.get("list");
					if (list != null && list.size() > 0) {
						for (int i=0; i<list.size(); i++) {
							String txt = list.get(i).toString();
							String arr[] = txt.split("\t");
							Map<String,String> map = new HashMap<String,String>();
							map.put("table_schema", arr[0]);
							map.put("size", arr[1]);
							map.put("cnt", arr[2]);
							putlist.add(map);
						}
					}
					mv.addObject("dblist",putlist);
				}
				
			}
		}
		return mv;
    }
    
    @RequestMapping(value="/set_bt_tblist")
    public ModelAndView set_bt_tblist(CommandMap commandMap) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	mv.addObject("bak_typ", commandMap.get("bak_typ"));
    	mv.addObject("bak_typ_itm", commandMap.get("bak_typ_itm"));
    	mv.addObject("bak_typ_tbe", commandMap.get("bak_typ_tbe"));
    	String agentTblistSql = "";
    	
    	Map<String,Object> dbsvrmap = setupService.findTgtSvr(commandMap.getMap());
    	if (dbsvrmap != null) {
    		
    		String bts_usr = dbsvrmap.get("bts_usr").toString();
			String bts_pwd = dbsvrmap.get("bts_pwd").toString();
			String bts_ip = dbsvrmap.get("bts_ip").toString();
			String bts_pot = dbsvrmap.get("bts_pot").toString();
    		
    		//관리서버
	    	Map<String,Object> param = new HashMap<String,Object>();
			param.put("ms_id", commandMap.get("bms_id").toString().charAt(0));
			Map<String,Object> msmap = manageService.findSvrMap(param);
			String ms_ip = "";
			int ms_port = 0;
			String ms_usr = "";
			String ms_pwd = "";
			String ms_sve_dir = "";
			//String ms_ssh_usr = "";
			String ms_bny_pth = "";
			if (msmap != null) {
				ms_ip = msmap.get("ms_ip").toString();
				ms_port = Integer.valueOf(msmap.get("ms_port").toString());
				ms_usr = msmap.get("ms_usr").toString();
				ms_pwd = msmap.get("ms_pwd").toString();
				ms_sve_dir = msmap.get("ms_sve_dir").toString();
				//ms_ssh_usr = msmap.get("ms_ssh_usr").toString();
				ms_bny_pth = msmap.get("ms_bny_pth").toString();
				
				String dbssql = "SELECT table_schema, size, (SELECT CASE WHEN cnt REGEXP '[^0-9]' THEN cnt ELSE FORMAT(cnt, 0) END) cnt FROM ( SELECT table_schema, CONCAT(round(sum(data_length+index_length)/(1024*1024),1),' MB') size , COUNT(*) cnt FROM information_schema.tables GROUP BY table_schema) A";
				String dbscmd = ms_bny_pth+"/mysql -u"+bts_usr+" -p"+bts_pwd+" -h "+bts_ip+" -P "+bts_pot+" -BNse\""+dbssql+"\"";
				Map<String,Object> dbsMap = new HashMap<String,Object>();
				dbsMap = ssh2Service.getList(dbscmd, ms_usr, ms_pwd, ms_ip, ms_port);
				if (dbsMap != null) {
					List<String> dblist = new ArrayList<String>();
					List<Object> putdblist = new ArrayList<Object>();
					dblist = (List) dbsMap.get("list");
					if (dblist != null && dblist.size() > 0) {
						for (int i=0; i<dblist.size(); i++) {
							String txt = dblist.get(i).toString();
							String arr[] = txt.split("\t");
							Map<String,String> map = new HashMap<String,String>();
							map.put("table_schema", arr[0]);
							map.put("size", arr[1]);
							map.put("cnt", arr[2]);
							putdblist.add(map);
						}
					}
					mv.addObject("dblist",putdblist);
					
					String tbssql = "SELECT table_name,CONCAT(ROUND((data_length+index_length)/(1024*1024),1),' MB') size,(CASE table_type WHEN 'BASE TABLE' THEN 'TABLE' ELSE 'VIEW' END) table_type, IFNULL((SELECT CASE WHEN table_rows REGEXP '[^0-9]' THEN table_rows ELSE FORMAT(table_rows, 0) END),0) cnt FROM information_schema.tables WHERE table_schema ="+"'"+commandMap.get("bak_typ_itm")+"'";
					String tbscmd = ms_bny_pth+"/mysql -u"+bts_usr+" -p"+bts_pwd+" -h "+bts_ip+" -P "+bts_pot+" -BNse\""+tbssql+"\"";
					Map<String,Object> tbsMap = new HashMap<String,Object>();
					tbsMap = ssh2Service.getList(tbscmd, ms_usr, ms_pwd, ms_ip, ms_port);
					List<String> tblist = new ArrayList<String>();
					List<Object> puttblist = new ArrayList<Object>();
					tblist = (List) tbsMap.get("list");
					if (tblist != null && tblist.size() > 0) {
						for (int i=0; i<tblist.size(); i++) {
							String txt = tblist.get(i).toString();
							String arr[] = txt.split("\t");
							Map<String,String> map = new HashMap<String,String>();
							map.put("table_name", arr[0]);
							map.put("size", arr[1]);
							map.put("table_type", arr[2]);
							map.put("cnt", arr[3]);
							puttblist.add(map);
						}
					}
					mv.addObject("tblist", puttblist);
				}
			}
    	}
    	return mv;
    }
    
    @RequestMapping(value="/insertBakSet")
	public ModelAndView insertBakSet(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("redirect:/set_b_set");
    	
    	boolean result = setupService.insertBakSet(commandMap.getMap(), request);
    	mv.addObject("insert_result", result);
    	mv.addObject("menu_cd", commandMap.get("menu_cd"));
    	mv.addObject("menu_id", commandMap.get("menu_id"));
    	mv.addObject("bms_id", commandMap.get("bms_id"));
    	mv.addObject("bts_id", commandMap.get("bts_id"));
    	
		return mv;
    }
    
    @RequestMapping(value="/set_b_mtd")
    public ModelAndView set_b_mtd(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/backup/method");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		Map<String,Object> pmap = new HashMap<String,Object>();
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
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvr(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvr(commandMap.getMap());
		if (btmap != null) {
			if (btmap.get("bts_nam") != null) {
				mv.addObject("bts_nam", btmap.get("bts_nam"));
			}
			if (btmap.get("bts_mlt_ins") != null) {
				mv.addObject("bts_mlt_ins", btmap.get("bts_mlt_ins"));
			}
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "M");
		List<Map<String,Object>> mlist = setupService.findType(pmap);
		if (mlist != null && mlist.size() > 0) {
			mv.addObject("mList", mlist);
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "CS");
		List<Map<String,Object>> list = setupService.findType(pmap);
		if (list != null && list.size() > 0) {
			mv.addObject("csList", list);
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "UM");
		List<Map<String,Object>> umlist = setupService.findType(pmap);
		if (umlist != null && umlist.size() > 0) {
			mv.addObject("umlist", umlist);
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "PR");
		List<Map<String,Object>> prlist = setupService.findType(pmap);
		if (prlist != null && prlist.size() > 0) {
			mv.addObject("prlist", prlist);
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "TR");
		List<Map<String,Object>> trlist = setupService.findType(pmap);
		if (trlist != null && trlist.size() > 0) {
			mv.addObject("trlist", trlist);
		}
//		
//		Map<String,Object> dbsvrmap = setupService.findTgtSvr(commandMap.getMap());
//		if (dbsvrmap != null) {
//			mv.addObject("bts_mlt_ins", dbsvrmap.get("bts_mlt_ins"));
//		}
		
		Map<String,Object> fbmmap = setupService.findBakMtd(commandMap.getMap());
		if (fbmmap != null) {
			mv.addObject("fbmmap", fbmmap);
		}else{
			String usr_id = loginManager.getUserID(request.getSession());
	    	commandMap.put("usr_id", usr_id);
	    	Map<String,Object> dbmmap = manageService.findDftBakMtd(commandMap.getMap());
			if (dbmmap != null && usr_id.equals(dbmmap.get("usr_id"))) {
				mv.addObject("fbmmap", dbmmap);
			}
		}
		
		mv.addObject("insert_result", commandMap.get("insert_result"));
		
    	return mv;
    }
    
    @RequestMapping(value="/insertBakMtd")
	public ModelAndView insertBakMtd(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("redirect:/set_b_mtd");
    	boolean result = setupService.insertBakMtd(commandMap.getMap(), request);
    	mv.addObject("insert_result", result);
    	mv.addObject("menu_cd", commandMap.get("menu_cd"));
    	mv.addObject("menu_id", commandMap.get("menu_id"));
    	mv.addObject("bms_id", commandMap.get("bms_id"));
    	mv.addObject("bts_id", commandMap.get("bts_id"));
    	
		return mv;
    }
    
	@RequestMapping(value = "/help_xtra")
	public ModelAndView help_xtra(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("/setup/backup/help/mariaBackup");
		return mv;
	}
	
	@RequestMapping(value = "/help_mysql")
	public ModelAndView help_mysql(CommandMap commandMap) throws Exception {
		ModelAndView mv = new ModelAndView("/setup/backup/help/mysqlDump");
		return mv;
	}
    
    
    @RequestMapping(value="/set_b_scd")
    public ModelAndView set_b_scd(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/backup/schedule");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		Map<String,Object> pmap = new HashMap<String,Object>();
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
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvrNam(commandMap.getMap());
		if (btmap != null && btmap.get("bts_nam") != null) {
			mv.addObject("bts_nam", btmap.get("bts_nam"));
		}
		
		List<Map<String,Object>> list = setupService.findBakScdNam(commandMap.getMap());
		if (list != null && list.size() > 0) {
			mv.addObject("scdlist", list);
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "SP");
		List<Map<String,Object>> splist = setupService.findType(pmap);
		if (splist != null && splist.size() > 0) {
			mv.addObject("splist", splist);
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "L");
		List<Map<String,Object>> bllist = setupService.findType(pmap);
		if (bllist != null && bllist.size() > 0) {
			mv.addObject("bllist", bllist);
		}
		
    	return mv;
    }
    
    @RequestMapping(value="/is_set_yn")
    public ModelAndView is_set_yn(CommandMap commandMap) throws Exception {
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	//백업설정
		Map<String,Object> bsmap = setupService.findBakSet(commandMap.getMap());
		//백업방법
		Map<String,Object> fbmmap = setupService.findBakMtd(commandMap.getMap());
		
		boolean yn = false;
		if (bsmap != null && fbmmap != null) {
			yn = true;
		}
		mv.addObject("is_set_yn", yn);
		
    	return mv;
    }
    
    
    
    @RequestMapping(value="/set_b_scdmap")
    public ModelAndView set_b_scdmap(CommandMap commandMap) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	List<Map<String,Object>> list = setupService.findBakScdNam(commandMap.getMap());
		if (list != null && list.size() > 0) {
			mv.addObject("scdlist", list);
		}
    	
    	Map<String,Object> scdmap = setupService.findBakScd(commandMap.getMap());
		if (scdmap != null) {
			mv.addObject("scdmap", scdmap);
		}
    	return mv;
    }
    
    @RequestMapping(value="/insertBakScd")
	public ModelAndView insertBakScd(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");

    	//Crontab 등록될 이름
    	String bms_id = commandMap.get("bms_id").toString(); //$11
    	String bts_id = commandMap.get("bts_id").toString(); //$12
    	String bms_nam = commandMap.get("bms_nam").toString(); //$2
    	String bts_nam = commandMap.get("bts_nam").toString(); //$3
    	String cron_name = commandMap.get("cron_name").toString(); //$13
    	String crontabNm = bms_id+"_"+bts_id+"_"+cron_name; // $1
    	String level = commandMap.get("lvl").toString();
    	String kep_pod = commandMap.get("kep_pod").toString();//$15
    	
    	mv.addObject("scd_nam", commandMap.get("scd_nam"));
    	
    	//접속정보 조회 2017.11.25
    	Map<String,Object> param = new HashMap<String,Object>();
    	param.put("ms_id", commandMap.get("bms_id").toString().charAt(0));
    	
    	//was os 접속정보
    	Map<String,Object> msmap = manageService.findSvrMap(param);
		String ms_ip = "";
		int ms_port = 0;
		String ms_usr = "";
		String ms_pwd = "";
		String ms_sve_dir = "";
		//String ms_ssh_usr = "";
		String ms_bny_pth = "empty";
		String ms_bny_pth_crypt = "";
		if (msmap != null) {
			ms_ip = msmap.get("ms_ip").toString();
			ms_port = Integer.valueOf(msmap.get("ms_port").toString());
			ms_usr = msmap.get("ms_usr").toString();
			ms_pwd = msmap.get("ms_pwd").toString();
			ms_sve_dir = msmap.get("ms_sve_dir").toString();
			//ms_ssh_usr = msmap.get("ms_ssh_usr").toString();
			ms_bny_pth = msmap.get("ms_bny_pth").toString();
		}
		ms_bny_pth_crypt = base64Util.encode(ms_bny_pth+"\n");
		
		
		String con_time = commandMap.get("scd_con").toString();
		commandMap.put("cron_nam", crontabNm);
		
		//대상서버,인스턴스
		String sh_nm = "";
		if ("L01".equals(level)) {
			sh_nm = Constants.CRONTAB_FULL_BACKUP;
		} else if ("L02".equals(level)) {
			sh_nm = Constants.CRONTAB_INCR_BACKUP;
		}
		
		//백업서버
		Map<String,Object> bmmap = setupService.findMngSvr(commandMap.getMap());
		String bms_pwd = "", bms_usr = "";
		if (bmmap != null) {
			bms_pwd = bmmap.get("bms_pwd").toString();
			bms_usr = bmmap.get("bms_usr").toString();
		}
		
		//DB서버
		Map<String,Object> btmap = setupService.findTgtSvr(commandMap.getMap());
		
		//백업설정
		Map<String,Object> bsmap = setupService.findBakSet(commandMap.getMap());
		
		//백업방법
		Map<String,Object> fbmmap = setupService.findBakMtd(commandMap.getMap());
		String strDate = StringUtil.getToday("yyyyMMddHHmmss");
		
		String backup_dir = ms_sve_dir;//$4
		String com_user = bsmap.get("bts_usr").toString();//$5
		//암호화 2018.01.05
		String com_user_crypt = "";
		if (!"".equals(com_user)) {
			com_user_crypt = base64Util.encode(com_user+"\n");
		}
		String com_pass = bsmap.get("bts_pwd").toString();//$6
		//암호화 2018.01.05
		String com_pass_crypt = "";
		if (!"".equals(com_pass)) {
			com_pass_crypt = base64Util.encode(com_pass+"\n");
		}
		
		String com_target_host = bsmap.get("hst_ip").toString();//$7
		String com_target_port = bsmap.get("pot_num").toString();//$8
		String com_target_port_crypt = "";
		if (!"".equals(com_target_port)) {
			com_target_port_crypt = base64Util.encode(com_target_port+"\n");
		}
		
//		String ssh_id = bsmap.get("ssh_usr").toString();//$9
		//암호화 2018.01.05
//		String ssh_id_crypt = "";
		String bms_usr_crypt = "";
		if (!"".equals(bms_usr)) {
			bms_usr_crypt = base64Util.encode(bms_usr+"\n");
		}
		String lvl = commandMap.get("lvl").toString();
		String avt_nam = fbmmap.get("avt_nam").toString();//$10
		String bak_typ = bsmap.get("bak_typ").toString();
		commandMap.put("typ", bak_typ);
		commandMap.put("mtd", avt_nam);
		
		//add param 2018.05.08
		String mng_xtr_bny_log_pth = "";
		if (bsmap.get("mng_xtr_bny_log_pth") != null) {
			mng_xtr_bny_log_pth = bsmap.get("mng_xtr_bny_log_pth").toString();
		}
		
		String xtra_default_file = "empty";//$17
		String xtra_defaults_group = "empty";//$18
		String com_temp_dir = "empty";//$19
		String com_target_db = "empty";//$20
		String xtra_usemem = "";//$21
		String xtra_parallel = "";//$22
		String xtra_throttle = "";//$23
		String xtra_comp = "";//$24
		String xtra_bak_opt = "empty";//$25
		String xtra_bin_path = "empty";//$26
		String dump_flush_logs = "";//$27
		String dump_lock_opt = "";//$28
		String dump_char_set = "";//$29
		String com_mysql_path = !"".equals(bsmap.get("msq_clt_utl_pth").toString())?bsmap.get("msq_clt_utl_pth").toString():"empty";//$30
		String dump_binlog_path = "empty";//$31
		
		if ("L01".equals(lvl)) {
			
			//xtra_defaults_group(MULTI INSTANCE ) $18
			if (btmap.get("bts_mlt_ins") != null) {
				if ("Y".equals(btmap.get("bts_mlt_ins").toString())) {
					if (fbmmap.get("dft_grp") != null) {
						xtra_defaults_group = fbmmap.get("dft_grp").toString();
					}
				}
			}
			
			if (avt_nam != null && "M01".equals(avt_nam)) {
				
				if (fbmmap.get("dft_fil") != null) {
					xtra_default_file = fbmmap.get("dft_fil").toString();
				}
//				if (bsmap.get("tmp_dir") != null) {
//					com_temp_dir = bsmap.get("tmp_dir").toString();
//				}
				String itm = "\\\"";
				if ("BT02".equals(bak_typ)) {
					if (bsmap.get("bak_typ_itm") != null) {
						String str = bsmap.get("bak_typ_itm").toString();
						String[] strarr = str.split(",");
						for (int i=0; i<strarr.length; i++) {
							itm += strarr[i]+" ";
						}
						itm = itm.substring(0,itm.lastIndexOf(" "));
					}
					
				} else if ("BT03".equals(bak_typ)) {
					if (bsmap.get("bak_typ_itm") != null && bsmap.get("bak_typ_tbe") != null) {
						String str1 = bsmap.get("bak_typ_itm").toString();
						String str = bsmap.get("bak_typ_tbe").toString();
						String[] strarr = str.split(",");
						for (int i=0; i<strarr.length; i++) {
							itm += str1+"."+strarr[i]+" ";
						}
						itm = itm.substring(0,itm.lastIndexOf(" "));
					}
				}
				
				//com_target_db $20 
				if (!"BT01".equals(bak_typ)) {
					com_target_db = " --databases="+itm+"\\\"";
				}

				//xtra_usemem $21
				if (fbmmap.get("use_mmy_cdname") != null) {
					xtra_usemem = fbmmap.get("use_mmy_cdname").toString();
				}
				//xtra_parallel $22
				if (fbmmap.get("pel_cdname") != null) {
					xtra_parallel = fbmmap.get("pel_cdname").toString();
				}
				//xtra_throttle $23
				if (fbmmap.get("trt_cdname") != null) {
					xtra_throttle = fbmmap.get("trt_cdname").toString();
				}
				//xtra_comp $24
				if (fbmmap.get("cpr") != null) {
					xtra_comp = fbmmap.get("cpr").toString();
				}
				//xtra_bak_opt $25
				if (fbmmap.get("xtr_opt") != null && !"".equals(fbmmap.get("xtr_opt").toString())) {
					xtra_bak_opt = fbmmap.get("xtr_opt").toString();
				}
				//xtra_bin_path $26
				if (fbmmap.get("bny_pth") != null) {
					xtra_bin_path = fbmmap.get("bny_pth").toString();
				}
			} else if (avt_nam != null && "M02".equals(avt_nam)) {
				
				com_mysql_path = fbmmap.get("rmt_msq_bny_pth").toString();
				
				//dump_flush_logs //$27
				if (fbmmap.get("fsh_log") != null) {
					dump_flush_logs = fbmmap.get("fsh_log").toString();
				}
				//dump_lock_opt //$28
				if (fbmmap.get("lck_opt") != null) {
					dump_lock_opt = fbmmap.get("lck_opt").toString();
				}

				//com_target_db //$20
				if (bak_typ != null) {
					if ("BT01".equals(bak_typ)) {
						com_target_db = " --all-databases";
					} else {
						String itm = "\\\"";
						if ("BT02".equals(bak_typ)) {
							if (bsmap.get("bak_typ_itm") != null) {
								String str = bsmap.get("bak_typ_itm").toString();
								String[] strarr = str.split(",");
								for (int i=0; i<strarr.length; i++) {
									itm += strarr[i]+" ";
								}
								itm = itm.substring(0,itm.lastIndexOf(" "));
							}
							
						} else if ("BT03".equals(bak_typ)) {
							if (bsmap.get("bak_typ_itm") != null && bsmap.get("bak_typ_tbe") != null) {
								String str1 = bsmap.get("bak_typ_itm").toString();
								String str = bsmap.get("bak_typ_tbe").toString();
								String[] strarr = str.split(",");
								for (int i=0; i<strarr.length; i++) {
									itm += str1+"."+strarr[i]+" ";
								}
								itm = itm.substring(0,itm.lastIndexOf(" "));
							}
						}
						com_target_db = " --databases="+itm+"\\\"";
					}
				}
				//dump_char_set //$29
				if (fbmmap.get("cha_set_cdname") != null) {
//					dump_char_set = " --default-character-set="+fbmmap.get("cha_set_cdname");
					dump_char_set = fbmmap.get("cha_set_cdname").toString();
				}
				
				//dump_binlog_path //$31
				if (fbmmap.get("msq_bny_log_pth") != null) {
					dump_binlog_path = fbmmap.get("msq_bny_log_pth").toString();
				}
			}
//			# $1  : schedule_name   # $2  : server_nm		    # $3  : db_server_nm		# $4  : backup_dir		# $5  : com_user
//			# $6  : com_pass		# $7  : com_target_host		# $8  : com_target_port		# $9  : bms_usr	    	# $10 : avt_nam
//			# $11 : bms_id	    	# $12 : bts_id		        # $13 : cron_nam	    	# $14 : typ		        # $15 : kep_pod
//			# $16 : ms_bny_pth		# $17 : xtra_default_file	# $18 : xtra_defaults_group # $19 : com_temp_dir	# $20 : com_target_db
//			# $21 : xtra_usemem		# $22 : xtra_parallel		# $23 : xtra_throttle		# $24 : xtra_comp		# $25 : xtra_bak_opt
//			# $26 : xtra_bin_path	# $27 : dump_flush_logs		# $28 : dump_lock_opt		# $29 : dump_char_set	# $30 : com_mysql_path
//			# $31 : dump_binlog_path# $32 : 관리서버IP				# $33 : 관리서버SSH			# $34 : mng_xtr_bny_log_pth # $35 : bms_pwd
//			# $36 : ms_pwd
			
			bms_usr_crypt = base64Util.encode(bms_usr+"\n");
			
			String xtra_default_file_crypt = base64Util.encode(xtra_default_file+"\n");//$17
			String com_temp_dir_crypt = base64Util.encode(com_temp_dir+"\n");//$19
			String com_target_db_crypt = base64Util.encode(com_target_db+"\n");//$20
			String xtra_bak_opt_crypt = base64Util.encode(xtra_bak_opt+"\n");//$25
			String xtra_bin_path_crypt = base64Util.encode(xtra_bin_path+"\n");//$26
			String com_mysql_path_crypt = base64Util.encode(com_mysql_path+"\n");//$30
			String dump_binlog_path_crypt = base64Util.encode(dump_binlog_path+"\n");//$31
			
			StringBuilder fullParam = new StringBuilder();
			fullParam.append(crontabNm);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bts_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(backup_dir);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_user_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_pass_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_target_host);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_target_port_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_usr_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(avt_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_id);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bts_id);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(cron_name);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bak_typ);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(kep_pod);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(ms_bny_pth_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(xtra_default_file_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(xtra_defaults_group);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_temp_dir_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_target_db_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(xtra_usemem.trim())? xtra_usemem : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(xtra_parallel.trim())? xtra_parallel : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(xtra_throttle.trim())? xtra_throttle : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(xtra_comp.trim())? xtra_comp : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(xtra_bak_opt_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(xtra_bin_path_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(dump_flush_logs.trim())? dump_flush_logs : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(dump_lock_opt.trim())? dump_lock_opt : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(dump_char_set.trim())? dump_char_set : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_mysql_path_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(dump_binlog_path_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(ms_ip+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			//fullParam.append(base64Util.encode(ms_ssh_usr+"\n"));
			fullParam.append(base64Util.encode(ms_usr+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(mng_xtr_bny_log_pth.trim())? mng_xtr_bny_log_pth : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(bms_pwd+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(ms_pwd+"\n"));
			
	    	String cmd = "crontab -l > cron_maria\n" + 
	    				 "echo \"" +con_time+" sh /ginian/sh/"+sh_nm+" "+ fullParam.toString() +"\""+" >> cron_maria\n" + 
	    				 "crontab -i cron_maria";
	    	//DB Insert
	    	commandMap.put("cmd", cmd);
	    	commandMap.put("ms_usr", ms_usr);
	    	commandMap.put("ms_pwd", ms_pwd);
	    	commandMap.put("ms_ip", ms_ip);
	    	commandMap.put("ms_port", ms_port);
	    	boolean result = setupService.insertBakScd(commandMap.getMap(), request);
	    	mv.addObject("result", result);
	    	
		} else if ("L02".equals(lvl)) {
						
			String binlog_path = "empty";
			
			commandMap.put("suc_yon", "S01");
			
			//dump_binlog_path //$17
			if (fbmmap.get("msq_bny_log_pth") != null) {
				dump_binlog_path = fbmmap.get("msq_bny_log_pth").toString();
			}
			
			//xtr_bny_log_pth //$18
			if (avt_nam != null && "M01".equals(avt_nam)) {
				binlog_path = fbmmap.get("xtr_bny_log_pth").toString();
			} else if (avt_nam != null && "M02".equals(avt_nam)) {
				
				binlog_path = fbmmap.get("msq_bny_log_pth").toString();
			}
			
			//xtra_comp //$19
			if (fbmmap.get("cpr") != null) {
				xtra_comp = fbmmap.get("cpr").toString();
			}
			
			String dump_binlog_path_crypt = base64Util.encode(dump_binlog_path+"\n");//$31
			String binlog_path_crypt = base64Util.encode(binlog_path+"\n");//$31
			
			StringBuilder fullParam = new StringBuilder();
			fullParam.append(crontabNm);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bts_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(backup_dir);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_user_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_pass_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_target_host);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_target_port_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_usr_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(avt_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_id);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bts_id);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(cron_name);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bak_typ);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(kep_pod);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(ms_bny_pth_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(dump_binlog_path_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(binlog_path_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(xtra_comp.trim())? xtra_comp : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(ms_ip+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			//fullParam.append(base64Util.encode(ms_ssh_usr+"\n"));
			fullParam.append(base64Util.encode(ms_usr+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(bms_pwd+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(ms_pwd+"\n"));

	    	String cmd = "crontab -l > cron_maria\n" + 
	    				 "echo \"" +con_time+" sh /ginian/sh/"+sh_nm+" "+ fullParam.toString() +"\""+" >> cron_maria\n" + 
	    				 "crontab -i cron_maria";
	    	//DB Insert
	    	commandMap.put("cmd", cmd);
	    	commandMap.put("ms_usr", ms_usr);
	    	commandMap.put("ms_pwd", ms_pwd);
	    	commandMap.put("ms_ip", ms_ip);
	    	commandMap.put("ms_port", ms_port);
	    	boolean result = setupService.insertBakScd(commandMap.getMap(), request);
	    	mv.addObject("result", result);
		}
    	return mv;
    }
    
    @RequestMapping(value="/updateBakScd")
	public ModelAndView updateBakScd(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	mv.addObject("old_scd_nam", commandMap.get("old_scd_nam"));
    	mv.addObject("new_scd_nam", commandMap.get("new_scd_nam"));
    	
    	String bms_nam = commandMap.get("bms_nam").toString(); //$2
    	String bts_nam = commandMap.get("bts_nam").toString(); //$3
    	
    	//업데이트 될 클론네임
    	String bms_id = commandMap.get("bms_id").toString();//$11
    	String bts_id = commandMap.get("bts_id").toString();//$12
    	String cron_name = commandMap.get("cron_name").toString();//$13
    	String newCronName = bms_id + "_" + bts_id + "_" + cron_name; // $1
    	commandMap.put("cron_nam", newCronName);
    	
    	String kep_pod = commandMap.get("kep_pod").toString();//$15
    	
    	//기존 CronName 가져오기
    	Map<String, Object> item = new HashMap<String,Object>();
    	item.put("bms_id", commandMap.get("bms_id"));
    	item.put("bts_id", commandMap.get("bts_id"));
    	item.put("scd_nam", commandMap.get("scd_nam"));
    	
    	//기존 클론네임
    	Map<String, Object> cronNameMap = setupService.findBakScd(item);
    	String oldCronName = cronNameMap.get("cron_nam").toString();
    	//DB update 시 필요 파라메터
    	commandMap.put("old_cron_nam", oldCronName);
    	
    	//클론 스케줄 시간
    	String con_time = commandMap.get("scd_con").toString();
    	
    	String level = commandMap.get("lvl").toString();
    	
    	mv.addObject("scd_nam", commandMap.get("scd_nam"));
    	
    	//접속정보 조회 2017.11.25
    	Map<String,Object> param = new HashMap<String,Object>();
    	param.put("ms_id", commandMap.get("bms_id").toString().charAt(0));
    	
    	//was os 접속정보
    	Map<String,Object> msmap = manageService.findSvrMap(param);
		String ms_ip = "";
		int ms_port = 0;
		String ms_usr = "";
		String ms_pwd = "";
		String ms_sve_dir = "";
		//String ms_ssh_usr = "";
		String ms_bny_pth = "empty";
		String ms_bny_pth_crypt = "";
		if (msmap != null) {
			ms_ip = msmap.get("ms_ip").toString();
			ms_port = Integer.valueOf(msmap.get("ms_port").toString());
			ms_usr = msmap.get("ms_usr").toString();
			ms_pwd = msmap.get("ms_pwd").toString();
			ms_sve_dir = msmap.get("ms_sve_dir").toString();
			//ms_ssh_usr = msmap.get("ms_ssh_usr").toString();
			ms_bny_pth = msmap.get("ms_bny_pth").toString();
			ms_bny_pth_crypt = base64Util.encode(ms_bny_pth+"\n");
		}
		
		//대상서버,인스턴스
		String sh_nm = "";
		if ("L01".equals(level)) {
			sh_nm = Constants.CRONTAB_FULL_BACKUP;
		} else if ("L02".equals(level)) {
			sh_nm = Constants.CRONTAB_INCR_BACKUP;
		}
		
		//백업서버
		Map<String,Object> bmmap = setupService.findMngSvr(commandMap.getMap());
		String bms_usr = "", bms_pwd = "";
		if (bmmap != null) {
			bms_usr = bmmap.get("bms_usr").toString();
			bms_pwd = bmmap.get("bms_pwd").toString();
		}
    	
		//DB서버
		Map<String,Object> btmap = setupService.findTgtSvr(commandMap.getMap());
		
		//백업설정
		Map<String,Object> bsmap = setupService.findBakSet(commandMap.getMap());
		
		//백업방법
		Map<String,Object> fbmmap = setupService.findBakMtd(commandMap.getMap());
		String strDate = StringUtil.getToday("yyyyMMddHHmmss");
		
		String backup_dir = ms_sve_dir;
		String com_user = bsmap.get("bts_usr").toString();//$5
		String com_user_crypt = "";
		if (!"".equals(com_user)) {
			com_user_crypt = base64Util.encode(com_user+"\n");
		}
		
		String com_pass = bsmap.get("bts_pwd").toString();//$6
		//암호화 2018.01.05
		String com_pass_crypt = "";
		if (!"".equals(com_pass)) {
			com_pass_crypt = base64Util.encode(com_pass+"\n");
		}
		
		String com_target_host = bsmap.get("hst_ip").toString();//$7
		String com_target_port = bsmap.get("pot_num").toString();//$8
		String com_target_port_crypt = "";
		if (!"".equals(com_target_port)) {
			com_target_port_crypt = base64Util.encode(com_target_port+"\n");
		}
		
//		String ssh_id = bsmap.get("ssh_usr").toString();//$9
//		String ssh_id_crypt = "";
		String bms_usr_crypt = "";
		if (!"".equals(bms_usr)) {
			bms_usr_crypt = base64Util.encode(bms_usr+"\n");
		}
		
		String lvl = commandMap.get("lvl").toString();
		String avt_nam = fbmmap.get("avt_nam").toString();//$10
		String bak_typ = bsmap.get("bak_typ").toString();
		
		commandMap.put("typ", bak_typ);
		commandMap.put("mtd", avt_nam);
		
		//add param 2018.05.08
		String mng_xtr_bny_log_pth = "";
		if (bsmap.get("mng_xtr_bny_log_pth") != null) {
			mng_xtr_bny_log_pth = bsmap.get("mng_xtr_bny_log_pth").toString();
		}
		
		String xtra_default_file = "empty";//$17
		String xtra_defaults_group = "empty";//$18
		String com_temp_dir = "empty";//$19
		String com_target_db = "empty";//$20
		String xtra_usemem = "";//$21
		String xtra_parallel = "";//$22
		String xtra_throttle = "";//$23
		String xtra_comp = "";//$24
		String xtra_bak_opt = "empty";//$25
		String xtra_bin_path = "empty";//$26
		String dump_flush_logs = "";//$27
		String dump_lock_opt = "";//$28
		String dump_char_set = "";//$29
		String com_mysql_path = !"".equals(bsmap.get("msq_clt_utl_pth").toString())?bsmap.get("msq_clt_utl_pth").toString():"empty";//$30
		String dump_binlog_path = "empty";//$31
	
		String cmd = "";
		///////// 추가Command///////////////////////////////////////////////////////////
		//crontab -l > cron_maria
		//echo "30 1 * * * sh /home/maria/sh/full_test.sh #100_100100_" >>  cron_maria
		//crontab -i cron_maria
		///////////////////////////////////////////////////////////////////////////////
		//Crontab 스케줄 추가 Command
		if ("L01".equals(lvl)) {
			
			//xtra_defaults_group(MULTI INSTANCE ) $18
			if (btmap.get("bts_mlt_ins") != null) {
				if ("Y".equals(btmap.get("bts_mlt_ins").toString())) {
					if (fbmmap.get("dft_grp") != null) {
						xtra_defaults_group = fbmmap.get("dft_grp").toString();
					}
				}
			}
			
			if (avt_nam != null && "M01".equals(avt_nam)) {
				//xtra_default_file $17
				if (fbmmap.get("dft_fil") != null) {
					xtra_default_file = fbmmap.get("dft_fil").toString();
				}
				//com_temp_dir //$19
//				if (bsmap.get("tmp_dir") != null) {
//					com_temp_dir = bsmap.get("tmp_dir").toString();
//				}
				//com_target_db $20
				String itm = "\\\"";
				if ("BT02".equals(bak_typ)) {
					if (bsmap.get("bak_typ_itm") != null) {
						String str = bsmap.get("bak_typ_itm").toString();
						String[] strarr = str.split(",");
						for (int i=0; i<strarr.length; i++) {
							itm += strarr[i]+" ";
						}
						itm = itm.substring(0,itm.lastIndexOf(" "));
					}
					
				} else if ("BT03".equals(bak_typ)) {
					if (bsmap.get("bak_typ_itm") != null && bsmap.get("bak_typ_tbe") != null) {
						String str1 = bsmap.get("bak_typ_itm").toString();
						String str = bsmap.get("bak_typ_tbe").toString();
						String[] strarr = str.split(",");
						for (int i=0; i<strarr.length; i++) {
							itm += str1+"."+strarr[i]+" ";
						}
						itm = itm.substring(0,itm.lastIndexOf(" "));
					}
				}
				
				//com_target_db $20 
				if (!"BT01".equals(bak_typ)) {
					com_target_db = " --databases="+itm+"\\\"";
				}

				//xtra_usemem $21
				if (fbmmap.get("use_mmy_cdname") != null) {
					xtra_usemem = fbmmap.get("use_mmy_cdname").toString();
				}
				//xtra_parallel $22
				if (fbmmap.get("pel_cdname") != null) {
					xtra_parallel = fbmmap.get("pel_cdname").toString();
				}
				//xtra_throttle $23
				if (fbmmap.get("trt_cdname") != null) {
					xtra_throttle = fbmmap.get("trt_cdname").toString();
				}
				//xtra_comp $24
				if (fbmmap.get("cpr") != null) {
					xtra_comp = fbmmap.get("cpr").toString();
				}
				//xtra_bak_opt $25
				if (fbmmap.get("xtr_opt") != null && !"".equals(fbmmap.get("xtr_opt").toString())) {
					xtra_bak_opt = fbmmap.get("xtr_opt").toString();
				}
				//xtra_bin_path $26
				if (fbmmap.get("bny_pth") != null) {
					xtra_bin_path = fbmmap.get("bny_pth").toString();
				}
			} else if (avt_nam != null && "M02".equals(avt_nam)) {
				
				com_mysql_path = fbmmap.get("rmt_msq_bny_pth").toString();
				
				//dump_flush_logs //$27
				if (fbmmap.get("fsh_log") != null) {
					dump_flush_logs = fbmmap.get("fsh_log").toString();
				}
				//dump_lock_opt //$28
				if (fbmmap.get("lck_opt") != null) {
					dump_lock_opt = fbmmap.get("lck_opt").toString();
				}

				//com_target_db //$20
				if (bak_typ != null) {
					if ("BT01".equals(bak_typ)) {
						com_target_db = " --all-databases";
					} else {
						String itm = "\\\"";
						if ("BT02".equals(bak_typ)) {
							if (bsmap.get("bak_typ_itm") != null) {
								String str = bsmap.get("bak_typ_itm").toString();
								String[] strarr = str.split(",");
								for (int i=0; i<strarr.length; i++) {
									itm += strarr[i]+" ";
								}
								itm = itm.substring(0,itm.lastIndexOf(" "));
							}
							
						} else if ("BT03".equals(bak_typ)) {
							if (bsmap.get("bak_typ_itm") != null && bsmap.get("bak_typ_tbe") != null) {
								String str1 = bsmap.get("bak_typ_itm").toString();
								String str = bsmap.get("bak_typ_tbe").toString();
								String[] strarr = str.split(",");
								for (int i=0; i<strarr.length; i++) {
									itm += str1+"."+strarr[i]+" ";
								}
								itm = itm.substring(0,itm.lastIndexOf(" "));
							}
						}
						com_target_db = " --databases="+itm+"\\\"";
					}
				}
				//dump_char_set //$29
				if (fbmmap.get("cha_set_cdname") != null) {
					dump_char_set = fbmmap.get("cha_set_cdname").toString();
				}
				
				//dump_binlog_path //$31
				if (fbmmap.get("msq_bny_log_pth") != null) {
					dump_binlog_path = fbmmap.get("msq_bny_log_pth").toString();
				}
			}
//					# $1  : schedule_name   # $2  : server_nm		    # $3  : db_server_nm		# $4  : backup_dir		# $5  : com_user
//					# $6  : com_pass		# $7  : com_target_host		# $8  : com_target_port		# $9  : bms_usr	    	# $10 : avt_nam
//					# $11 : bms_id	    	# $12 : bts_id		        # $13 : cron_nam	    	# $14 : typ		        # $15 : kep_pod
//					# $16 : ms_bny_pth		# $17 : xtra_default_file	# $18 : xtra_defaults_group # $19 : com_temp_dir	# $20 : com_target_db
//					# $21 : xtra_usemem		# $22 : xtra_parallel		# $23 : xtra_throttle		# $24 : xtra_comp		# $25 : xtra_bak_opt
//					# $26 : xtra_bin_path	# $27 : dump_flush_logs		# $28 : dump_lock_opt		# $29 : dump_char_set	# $30 : com_mysql_path
//					# $31 : dump_binlog_path
			
			String xtra_default_file_crypt = base64Util.encode(xtra_default_file+"\n");//$17
			String com_temp_dir_crypt = base64Util.encode(com_temp_dir+"\n");//$19
			String com_target_db_crypt = base64Util.encode(com_target_db+"\n");//$20
			String xtra_bak_opt_crypt = base64Util.encode(xtra_bak_opt+"\n");//$25
			String xtra_bin_path_crypt = base64Util.encode(xtra_bin_path+"\n");//$26
			String com_mysql_path_crypt = base64Util.encode(com_mysql_path+"\n");//$30
			String dump_binlog_path_crypt = base64Util.encode(dump_binlog_path+"\n");//$31
			
			StringBuilder fullParam = new StringBuilder();
			fullParam.append(newCronName);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bts_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(backup_dir);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_user_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_pass_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_target_host);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_target_port_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_usr_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(avt_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_id);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bts_id);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(cron_name);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bak_typ);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(kep_pod);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(ms_bny_pth_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(xtra_default_file_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(xtra_defaults_group);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_temp_dir_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_target_db_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(xtra_usemem.trim())? xtra_usemem : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(xtra_parallel.trim())? xtra_parallel : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(xtra_throttle.trim())? xtra_throttle : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(xtra_comp.trim())? xtra_comp : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(xtra_bak_opt_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(xtra_bin_path_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(dump_flush_logs.trim())? dump_flush_logs : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(dump_lock_opt.trim())? dump_lock_opt : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(dump_char_set.trim())? dump_char_set : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_mysql_path_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(dump_binlog_path_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(ms_ip+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			//fullParam.append(base64Util.encode(ms_ssh_usr+"\n"));
			fullParam.append(base64Util.encode(ms_usr+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(mng_xtr_bny_log_pth.trim())? mng_xtr_bny_log_pth : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(bms_pwd+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(ms_pwd+"\n"));
			
	    	cmd = "crontab -l > cron_maria\n"+
  				  "perl -pi -e " + "\"s/.*" + oldCronName + ".*//s\"" + " cron_maria\n"+
  				  "crontab -i cron_maria\n"+
  				  "echo \"" +con_time+" sh /ginian/sh/"+sh_nm+" "+ fullParam.toString() +"\""+" >> cron_maria\n" + 
  				  "crontab -i cron_maria";
		} else if ("L02".equals(lvl)) {
						
			String binlog_path = "empty";
			
			//dump_binlog_path //$17
			if (fbmmap.get("msq_bny_log_pth") != null) {
				dump_binlog_path = fbmmap.get("msq_bny_log_pth").toString();
			}
			
			//xtr_bny_log_pth //$18
			if (avt_nam != null && "M01".equals(avt_nam)) {
				binlog_path = fbmmap.get("xtr_bny_log_pth").toString();
			} else if (avt_nam != null && "M02".equals(avt_nam)) {
				
				binlog_path = fbmmap.get("msq_bny_log_pth").toString();
			}
			
			//xtra_comp //$19
			if (fbmmap.get("cpr") != null) {
				xtra_comp = fbmmap.get("cpr").toString();
			}
			
			String dump_binlog_path_crypt = base64Util.encode(dump_binlog_path+"\n");//$31
			String binlog_path_crypt = base64Util.encode(binlog_path+"\n");//$31
			
			StringBuilder fullParam = new StringBuilder();
			fullParam.append(newCronName);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bts_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(backup_dir);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_user_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_pass_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_target_host);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(com_target_port_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_usr_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(avt_nam);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bms_id);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bts_id);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(cron_name);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(bak_typ);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(kep_pod);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(ms_bny_pth_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(dump_binlog_path_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(binlog_path_crypt);
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(!"".equals(xtra_comp.trim())? xtra_comp : "empty");
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(ms_ip+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			//fullParam.append(base64Util.encode(ms_ssh_usr+"\n"));
			fullParam.append(base64Util.encode(ms_usr+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(bms_pwd+"\n"));
			fullParam.append(Constants.BACKUP_SEPARATOR);
			fullParam.append(base64Util.encode(ms_pwd+"\n"));
			
	    	//crontab 수정 시 command (기존 삭제 > 신규 추가)
	    	cmd = "crontab -l > cron_maria\n"+
	    				  "perl -pi -e " + "\"s/.*" + oldCronName + ".*//s\"" + " cron_maria\n"+
	    				  "crontab -i cron_maria\n"+
	    				  "echo \"" +con_time+" sh /ginian/sh/"+sh_nm+" "+ fullParam.toString() +"\""+" >> cron_maria\n" + 
	    				  "crontab -i cron_maria";

		}
		
		//DB Update
		commandMap.put("cmd", cmd);
    	commandMap.put("ms_usr", ms_usr);
    	commandMap.put("ms_pwd", ms_pwd);
    	commandMap.put("ms_ip", ms_ip);
    	commandMap.put("ms_port", ms_port);
    	boolean result = setupService.updateBakScd(commandMap.getMap(), request);
    	mv.addObject("result", result);
		
    	return mv;
    }
    
    @RequestMapping(value="/deleteBakScd")
	public ModelAndView deleteBakScd(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	//기존 CronName 가져오기
    	Map<String, Object> item = new HashMap<String,Object>();
    	item.put("bms_id", commandMap.get("bms_id"));
    	item.put("bts_id", commandMap.get("bts_id"));
    	item.put("scd_nam", commandMap.get("scd_nam"));
    	
    	//기존 클론네임
    	Map<String, Object> cronNameMap = setupService.findBakScd(item);
    	String oldCronName = cronNameMap.get("cron_nam").toString();
    	
    	//접속정보 조회
    	Map<String,Object> param = new HashMap<String,Object>();
    	param.put("ms_id", commandMap.get("bms_id").toString().charAt(0));
    	
    	//was os 접속정보
    	Map<String,Object> msmap = manageService.findSvrMap(param);
		String ms_ip = "";
		int ms_port = 0;
		String ms_usr = "";
		String ms_pwd = "";
		String ms_sve_dir = "";
		//String ms_ssh_usr = "";
		String ms_bny_pth = "";
		if (msmap != null) {
			ms_ip = msmap.get("ms_ip").toString();
			ms_port = Integer.valueOf(msmap.get("ms_port").toString());
			ms_usr = msmap.get("ms_usr").toString();
			ms_pwd = msmap.get("ms_pwd").toString();
			ms_sve_dir = msmap.get("ms_sve_dir").toString();
			//ms_ssh_usr = msmap.get("ms_ssh_usr").toString();
			ms_bny_pth = msmap.get("ms_bny_pth").toString();
		}
		
		///////// 삭제 Command///////////////////////////////////////////////////////////
    	//crontab -l > cron_maria
    	//perl -pi -e "s/.*30 1 01,08,15 * * 100 100100.*//s" cron_maria
    	//crontab -i cron_maria
    	///////////////////////////////////////////////////////////////////////////////
    	//crontab 삭제 시 command (기존 삭제 > 신규 추가)
    	String cmd = "crontab -l > cron_maria\n"+
    				  "perl -pi -e " + "\"s/.*" + oldCronName + ".*//s\"" + " cron_maria\n"+
    				  "crontab -i cron_maria\n";
    	mv.addObject("scd_nam", commandMap.get("scd_nam"));
    	
    	//DB 
    	commandMap.put("cmd", cmd);
    	commandMap.put("ms_usr", ms_usr);
    	commandMap.put("ms_pwd", ms_pwd);
    	commandMap.put("ms_ip", ms_ip);
    	commandMap.put("ms_port", ms_port);
    	boolean result = setupService.deleteBakScd(commandMap.getMap(), request);
    	mv.addObject("result", result);
    	
		return mv;
    }
    
    
    
    @RequestMapping(value="/set_b_smy")
    public ModelAndView set_b_smy(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/backup/summary");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		Map<String,Object> pmap = new HashMap<String,Object>();
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
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvr(commandMap.getMap());
		if (btmap != null) {
			if (btmap.get("bts_nam") != null) {
				mv.addObject("bts_nam", btmap.get("bts_nam"));
			}
			if (btmap.get("bts_mlt_ins") != null) {
				mv.addObject("bts_mlt_ins", btmap.get("bts_mlt_ins"));
			}
		}
		
		/* 백업설정조회 */
		Map<String,Object> bsmap = setupService.findBakSet(commandMap.getMap());
		if (bsmap != null) {
			mv.addObject("bsmap", bsmap);
		} else {
			mv.addObject("bak_set_blank", true);
		}
		
		/* 백업방법조회 */
		Map<String,Object> fbmmap = setupService.findBakMtd(commandMap.getMap());
		if (fbmmap != null) {
			mv.addObject("fbmmap", fbmmap);
		}
    	return mv;
    }
    
    @RequestMapping(value="/set_b_run")
    public ModelAndView set_b_run(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/backup/run");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		Map<String,Object> pmap = new HashMap<String,Object>();
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
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvr(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
			mv.addObject("bms_usr", bmmap.get("bms_usr"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvrNam(commandMap.getMap());
		if (btmap != null) {
			if (btmap.get("bts_nam") != null) {
				mv.addObject("bts_nam", btmap.get("bts_nam"));
			}
			if (btmap.get("bts_mlt_ins") != null) {
				mv.addObject("bts_mlt_ins", btmap.get("bts_mlt_ins"));
			}
		}
		
		/* 백업설정조회 */
		Map<String,Object> bsmap = setupService.findBakSet(commandMap.getMap());
		if (bsmap != null) {
			mv.addObject("bsmap", bsmap);
		} else {
			mv.addObject("bak_set_blank", true);
		}
		
		/* 백업방법조회 */
		Map<String,Object> fbmmap = setupService.findBakMtd(commandMap.getMap());
		if (fbmmap != null) {
			mv.addObject("fbmmap", fbmmap);
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "L");
		List<Map<String,Object>> bllist = setupService.findType(pmap);
		if (bllist != null && bllist.size() > 0) {
			mv.addObject("bllist", bllist);
		}
		
    	return mv;
    }
    
    @RequestMapping(value="/set_imd_run")
    public ModelAndView set_imd_run(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/backup/exe_run");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		mv.addObject("menu_cd", menu_cd);
		mv.addObject("pet_cod", pet_cod);
		mv.addObject("menu_id", menu_id);
		mv.addObject("menu_nm", menu_nm);
		mv.addObject("bms_id", commandMap.get("bms_id"));
		mv.addObject("bms_usr", commandMap.get("bms_usr"));
		mv.addObject("bts_id", commandMap.get("bts_id"));
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		mv.addObject("bms_nam", commandMap.get("bms_nam"));
		mv.addObject("bts_nam", commandMap.get("bts_nam"));
		mv.addObject("imd_lvl", commandMap.get("imd_lvl"));
		mv.addObject("imd_kep_pod", commandMap.get("imd_kep_pod"));
		mv.addObject("avt_nam", commandMap.get("avt_nam"));
		mv.addObject("cpr", commandMap.get("cpr"));
		
    	return mv;
    }
    
    static BackupScheduler schedulerHand = null;
    
    @RequestMapping(value="/bak_run_exe")
	public ModelAndView bak_run_exe(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	// 스케줄로 넘겨줄 파라메터 2017.07.31
    	Map<String,Object> schParamMap = new HashMap<String,Object>();
    	schParamMap.put("interval", Constants.INTERVAL_1DAY);
    	
    	String strDate = StringUtil.getToday("yyyyMMddHHmmss");
    	
    	String lvl = commandMap.get("imd_lvl").toString();
    	mv.addObject("lvl", lvl);
    	String kep_pod = commandMap.get("imd_kep_pod").toString();
    	mv.addObject("kep_pod", kep_pod);
    	String bms_nam = commandMap.get("bms_nam").toString();
    	String bts_nam = commandMap.get("bts_nam").toString();
    	
    	schParamMap.put("bms_nam", bms_nam);
    	schParamMap.put("bts_nam", bts_nam);
    	/*
    	schParamMap.put("bms_id", commandMap.get("bms_id"));
    	schParamMap.put("bts_id", commandMap.get("bts_id"));
    	schParamMap.put("wrk_dt", strDate);
    	*/
    	mv.addObject("wrk_dt", strDate);
    	schParamMap.put("wrk_dt", strDate);
    	mv.addObject("bms_nam", bms_nam);
    	mv.addObject("bts_nam", bts_nam);
    	
    	String log_file = "ginian.log";
    	String err_log_file = "ginian_error.log";
    	String log_file_path = "";
    	
    	schParamMap.put("log_file", log_file);
    	schParamMap.put("err_log_file", err_log_file);
    	
    	//관리서버
    	Map<String,Object> param = new HashMap<String,Object>();
		param.put("ms_id", commandMap.get("bms_id").toString().charAt(0));
		Map<String,Object> msmap = manageService.findSvrMap(param);
		String ms_ip = "";
		int ms_port = 0;
		String ms_usr = "";
		String ms_pwd = "";
		String ms_sve_dir = "";
		//String ms_ssh_usr = "";
		String ms_bny_pth = "";
		if (msmap != null) {
			ms_ip = msmap.get("ms_ip").toString();
			ms_port = Integer.valueOf(msmap.get("ms_port").toString());
			ms_usr = msmap.get("ms_usr").toString();
			ms_pwd = msmap.get("ms_pwd").toString();
			ms_sve_dir = msmap.get("ms_sve_dir").toString();
			//ms_ssh_usr = msmap.get("ms_ssh_usr").toString();1
			ms_bny_pth = msmap.get("ms_bny_pth").toString();
			
			mv.addObject("ms_ip", ms_ip);
			mv.addObject("ms_port", ms_port);
			mv.addObject("ms_usr", ms_usr);
			mv.addObject("ms_pwd", ms_pwd);
			mv.addObject("ms_sve_dir", ms_sve_dir);
			mv.addObject("ms_bny_pth", ms_bny_pth);
			
			schParamMap.put("ms_ip", ms_ip);
			schParamMap.put("ms_port", String.valueOf(ms_port));
			schParamMap.put("ms_usr", ms_usr);
			schParamMap.put("ms_pwd", ms_pwd);
			schParamMap.put("ms_sve_dir", ms_sve_dir);
			schParamMap.put("ms_bny_pth", ms_bny_pth);
			//schParamMap.put("ms_ssh_usr", ms_ssh_usr);
		}
    	//백업서버
		Map<String,Object> bmmap = setupService.findMngSvr(commandMap.getMap());
		String bms_pwd = "", bms_usr = "";
		if (bmmap != null) {
			bms_pwd = bmmap.get("bms_pwd").toString();
			bms_usr = bmmap.get("bms_usr").toString();
			mv.addObject("bms_usr", bms_usr);
			mv.addObject("bms_pwd", bms_pwd);
			schParamMap.put("bms_usr", bms_usr);
			schParamMap.put("bms_pwd", bms_pwd);
		}
		
		//DB서버
		Map<String,Object> btmap = setupService.findTgtSvr(commandMap.getMap());
		
		//백업설정
		Map<String,Object> bsmap = setupService.findBakSet(commandMap.getMap());
		
		//백업방법
		Map<String,Object> fbmmap = setupService.findBakMtd(commandMap.getMap());
		
		String com_target_host = "";
		String com_target_port = "";
		String com_user = "";
		String com_pass = "";
		String msq_clt_utl_pth = "";
		String mng_xtr_bny_log_pth = "";
//		String ssh_id = "";
		String bak_typ = "";
		if (bsmap != null) {
			com_user = bsmap.get("bts_usr").toString();
			com_pass = bsmap.get("bts_pwd").toString();
			com_target_host = bsmap.get("hst_ip").toString();
			com_target_port = bsmap.get("pot_num").toString();
//			ssh_id = bsmap.get("ssh_usr").toString();
			msq_clt_utl_pth = bsmap.get("msq_clt_utl_pth").toString();
			if (bsmap.get("mng_xtr_bny_log_pth") != null) {
				mng_xtr_bny_log_pth = bsmap.get("mng_xtr_bny_log_pth").toString();
			}
			
			bak_typ = bsmap.get("bak_typ").toString();
			
//			mv.addObject("ssh_usr", ssh_id);
			mv.addObject("hst_ip", com_target_host);
			mv.addObject("pot_num", com_target_port);
			mv.addObject("bts_usr", com_user);
			mv.addObject("bts_pwd", com_pass);
			mv.addObject("bak_typ", bak_typ);
			
//			schParamMap.put("ssh_id", ssh_id);
			schParamMap.put("com_target_host", com_target_host);
			schParamMap.put("com_target_port", com_target_port);
			schParamMap.put("com_user", com_user);
			schParamMap.put("com_pass", com_pass);
			schParamMap.put("bak_typ", bak_typ);
			schParamMap.put("msq_clt_utl_pth", msq_clt_utl_pth);
			schParamMap.put("mng_xtr_bny_log_pth", mng_xtr_bny_log_pth);
		}
		
		String backup_dir = "";
		String backup_pdir = "";
		StringBuilder dir_makcmd = new StringBuilder();
		if (ms_sve_dir != null && !"".equals(ms_sve_dir)) {
			long startTime = System.currentTimeMillis();
			backup_pdir = ms_sve_dir+"/"+bms_nam+"/"+bts_nam+"/BACKUP";
			if (!"BT01".equals(bak_typ)) {
				if ("BT02".equals(bak_typ)) {
					backup_pdir = ms_sve_dir+"/"+bms_nam+"/"+bts_nam+"/BACKUP/manual/databases";
				} else if ("BT03".equals(bak_typ)) {
					backup_pdir = ms_sve_dir+"/"+bms_nam+"/"+bts_nam+"/BACKUP/manual/tables";
				}
			}
			
			backup_dir = backup_pdir+"/"+strDate;
			dir_makcmd.append("if [ ! -d "+backup_dir+" ]; then");
			dir_makcmd.append(" mkdir -p "+backup_dir+";");
			dir_makcmd.append(" touch "+backup_dir+"/"+err_log_file);
			dir_makcmd.append(Constants.DATA_NEW_LINE);
			dir_makcmd.append(" touch "+backup_dir+"/"+log_file+"; fi");
			Map mkdirMap = new HashMap();
			log.debug(dir_makcmd);
//			System.out.println("=========dir_makcmd==========");
//			System.out.println(dir_makcmd.toString());
//			System.out.println("=========/dir_makcmd=========");
			mkdirMap = ssh2Service.getData(dir_makcmd.toString(), ms_usr, ms_pwd, ms_ip, ms_port, 5);
			if (mkdirMap != null) {
				if (!"0".equals(mkdirMap.get("ExitCode").toString())) {
					log.error("디렉토리 생성중 오류가 발생하였습니다.");
					mv.addObject("run_result", false);
					throw new Exception("디렉토리 생성중 오류가 발생하였습니다.");
				} else {
					
					long endTime = System.currentTimeMillis();
					
					mv.addObject("backup_dir", backup_dir);
					mv.addObject("backup_pdir", backup_pdir);
					
					schParamMap.put("backup_dir", backup_dir);
					schParamMap.put("backup_pdir", backup_pdir);
					
					//xtra full backup command 2017.07.31
					StringBuilder xtrarunCmd = new StringBuilder();
					
					String avt_nam = fbmmap.get("avt_nam").toString();
					schParamMap.put("avt_nam", avt_nam);
					mv.addObject("avt_nam", avt_nam);
					
					String dft_fil = "";
					if (!"".equals(fbmmap.get("dft_fil")) && fbmmap.get("dft_fil") != null) {
						dft_fil = fbmmap.get("dft_fil").toString();
					}
					String exit_flag = "0";
					
					if ("L01".equals(lvl)) {
						//* =============================================================================================================
						//* 풀백업 start
						//* =============================================================================================================
						StringBuilder backup_opt = new StringBuilder();
						
						mv.addObject("rmt_msq_bny_pth", fbmmap.get("rmt_msq_bny_pth"));
						
						String completeCmd = "";
						if (avt_nam != null && "M01".equals(avt_nam)) {
							//* xtrabackup start
							//xtra_default_file
//							if (fbmmap.get("dft_fil") != null) {
//								backup_opt.append("--defaults-file="+dft_fil);
//							}
							
							backup_opt.append(" --user="+com_user);
							backup_opt.append(" --password="+com_pass);
							backup_opt.append(" --host="+com_target_host);
							backup_opt.append(" --port="+com_target_port);
							
							//xtra_defaults_group(MULTI INSTANCE )
							if (btmap.get("bts_mlt_ins") != null) {
								if ("Y".equals(btmap.get("bts_mlt_ins").toString())) {
									if (fbmmap.get("dft_grp") != null) {
										backup_opt.append(" --defaults-group="+fbmmap.get("dft_grp").toString());
									}
								}
							}
							
							//com_temp_dir
							if (bsmap.get("tmp_dir") != null) {
								//backup_opt.append(" --tmpdir="+bsmap.get("tmp_dir"));
							}
							
							//com_target_db
							String itm = "\\\"";
							if ("BT02".equals(bak_typ)) {
								if (bsmap.get("bak_typ_itm") != null) {
									String str = bsmap.get("bak_typ_itm").toString();
									String[] strarr = str.split(",");
									for (int i=0; i<strarr.length; i++) {
										itm += strarr[i]+" ";
									}
									itm = itm.substring(0,itm.lastIndexOf(" "));
								}
								
							} else if ("BT03".equals(bak_typ)) {
								if (bsmap.get("bak_typ_itm") != null && bsmap.get("bak_typ_tbe") != null) {
									String str1 = bsmap.get("bak_typ_itm").toString();
									String str = bsmap.get("bak_typ_tbe").toString();
									String[] strarr = str.split(",");
									for (int i=0; i<strarr.length; i++) {
										itm += str1+"."+strarr[i]+" ";
									}
									itm = itm.substring(0,itm.lastIndexOf(" "));
								}
							}
							
							if (!"BT01".equals(bak_typ)) {
								backup_opt.append(" --databases="+itm+"\\\"");
							}
							//xtra_usemem
							if (fbmmap.get("use_mmy_cdname") != null) {
								if (!"0".equals(fbmmap.get("use_mmy_cdname"))) {
									backup_opt.append(" --use-memory="+fbmmap.get("use_mmy_cdname"));
								}
							}
							//xtra_parallel
							if (fbmmap.get("pel_cdname") != null) {
								if (!"1".equals(fbmmap.get("pel_cdname"))) {
									backup_opt.append(" --parallel="+fbmmap.get("pel_cdname"));
								}
							}
							//xtra_throttle
							if (fbmmap.get("trt_cdname") != null) {
								if (!"0".equals(fbmmap.get("trt_cdname"))) {
									backup_opt.append(" --throttle="+fbmmap.get("trt_cdname"));
								}
							}
							//xtra_comp
							if (fbmmap.get("cpr") != null) {
								if (!"N".equals(fbmmap.get("cpr").toString())) {
									backup_opt.append(" --compress");
								}
							}
							//xtra_bak_opt
							if (fbmmap.get("xtr_opt") != null) {
								backup_opt.append(" "+fbmmap.get("xtr_opt"));
							}
						
							if (fbmmap.get("bny_pth") != null) {
								xtrarunCmd.append("sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+com_target_host);
								xtrarunCmd.append(Constants.BACKUP_SEPARATOR);
								xtrarunCmd.append("\""+fbmmap.get("bny_pth")+"/mariabackup --backup");
								xtrarunCmd.append(Constants.BACKUP_SEPARATOR);
								xtrarunCmd.append(backup_opt);
								xtrarunCmd.append(Constants.BACKUP_SEPARATOR);
								//xtrarunCmd.append(bsmap.get("tmp_dir"));
								//xtrarunCmd.append(Constants.BACKUP_SEPARATOR);
								xtrarunCmd.append("--stream=mbstream | ");
								xtrarunCmd.append(Constants.BACKUP_SEPARATOR);
								//xtrarunCmd.append("sshpass -p"+ms_pwd+" ssh -o StrictHostKeyChecking=no "+ms_ssh_usr+"@"+ms_ip+" \"mbstream -x -C ");
								xtrarunCmd.append("sshpass -p"+ms_pwd+" ssh -o StrictHostKeyChecking=no "+ms_usr+"@"+ms_ip+" \"mbstream -x -C ");
								xtrarunCmd.append(Constants.BACKUP_SEPARATOR);
								xtrarunCmd.append(backup_dir+"\"\" &>> "+backup_dir+"/"+log_file);
								schParamMap.put("xtrarunCmd", xtrarunCmd.toString());
							}
							
							String chkpointCmd = "if [ -e "+backup_dir+"/xtrabackup_checkpoints ]; then echo ok; else echo no; fi";
							schParamMap.put("chkpointCmd", chkpointCmd);
							
							if (fbmmap.get("cpr") != null) {
								StringBuilder apply_opt = new StringBuilder();
								if ("N".equals(fbmmap.get("cpr"))) {
									if (bsmap.get("tmp_dir") != null) {
										if (!"/tmp".equals(fbmmap.get("cpr"))) {
											//apply_opt.append(" --tmpdir="+bsmap.get("tmp_dir"));
										}
									}
									if (fbmmap.get("use_mmy_cdname") != null) {
										if (!"0".equals(fbmmap.get("use_mmy_cdname"))) {
											apply_opt.append(" --use-memory="+fbmmap.get("use_mmy_cdname"));
										}
									}
									
									StringBuilder appOptCmd = new StringBuilder();
									if (!"".equals(mng_xtr_bny_log_pth)) {
										appOptCmd.append(mng_xtr_bny_log_pth+"/mariabackup ");
										appOptCmd.append(apply_opt+" --prepare --target-dir ");
										appOptCmd.append(backup_dir+" &>>"+backup_dir+"/"+log_file);
									}
									schParamMap.put("appOptCmd", appOptCmd.toString());
									
									completeCmd = "if [ -e "+backup_dir+"/ib_logfile0 ];then echo ok; else echo no; fi";
									
								} else {
									completeCmd = "if [ -e "+backup_dir+"/ibdata1 ] || [ -e "+backup_dir+"/ibdata1.qp ];then echo ok; else echo no; fi";
								}
							}
							//* xtrabackup end
							
						} else if (avt_nam != null && "M02".equals(avt_nam)) {
							
							String rmt_msq_bny_pth = "";
							if (fbmmap.get("rmt_msq_bny_pth") != null) {
								rmt_msq_bny_pth = fbmmap.get("rmt_msq_bny_pth").toString();
							}
							
							//* mysqldump start
							backup_opt.append("--user="+com_user);
							backup_opt.append(" --password="+com_pass);
							backup_opt.append(" --host="+com_target_host);
							backup_opt.append(" --port="+com_target_port);
							
							//dump_flush_logs
							if (fbmmap.get("fsh_log") != null) {
								if ("Y".equals(fbmmap.get("fsh_log")))
								backup_opt.append(" --flush-logs");
							}
							
							//dump_lock_opt
							if (fbmmap.get("lck_opt") != null) {
								if ("single".equals(fbmmap.get("lck_opt"))) {
									backup_opt.append(" --single-transaction");
								}
							}
							
							//com_target_db
							if (bak_typ != null) {
								if ("BT01".equals(bak_typ)) {
									backup_opt.append(" --all-databases");
								} else {
									String itm = "\\\"";
									if ("BT02".equals(bak_typ)) {
										if (bsmap.get("bak_typ_itm") != null) {
											String str = bsmap.get("bak_typ_itm").toString();
											String[] strarr = str.split(",");
											for (int i=0; i<strarr.length; i++) {
												itm += strarr[i]+" ";
											}
											itm = itm.substring(0,itm.lastIndexOf(" "));
										}
										
									} else if ("BT03".equals(bak_typ)) {
										if (bsmap.get("bak_typ_itm") != null && bsmap.get("bak_typ_tbe") != null) {
											String str1 = bsmap.get("bak_typ_itm").toString();
											String str = bsmap.get("bak_typ_tbe").toString();
											String[] strarr = str.split(",");
											for (int i=0; i<strarr.length; i++) {
												itm += str1+"."+strarr[i]+" ";
											}
											itm = itm.substring(0,itm.lastIndexOf(" "));
										}
									}
									backup_opt.append(" --databases="+itm+"\\\"");
								}
							}
							
							//dump_char_set
							if (fbmmap.get("cha_set_cdname") != null) {
								backup_opt.append(" --default-character-set="+fbmmap.get("cha_set_cdname"));
							}
							
							String dump_binlog_path = "";
							if (fbmmap.get("msq_bny_log_pth") != null) {
								dump_binlog_path = fbmmap.get("msq_bny_log_pth").toString();
							}
							
							StringBuilder meCmd = new StringBuilder();
							meCmd.append("sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+com_target_host);
							meCmd.append(" \""+rmt_msq_bny_pth+"/mysqldump --opt --extended-insert --create-options -v ");
							meCmd.append(" "+backup_opt+" | ");
							//meCmd.append("sshpass -p"+ms_pwd+" ssh -o StrictHostKeyChecking=no "+ms_ssh_usr+"@"+ms_ip);
							meCmd.append("sshpass -p"+ms_pwd+" ssh -o StrictHostKeyChecking=no "+ms_usr+"@"+ms_ip);
							meCmd.append(" \"cat - > "+backup_dir+"/"+strDate+".sql \" \" 2>> "+backup_dir+"/"+log_file);
							schParamMap.put("meCmd", meCmd.toString());
							
							//StringBuilder cdcCmd = new StringBuilder();
							//cdcCmd.append("CHECK_DUMP_COMPLETE=`tail -1 "+backup_dir+"/"+strDate+".sql |awk '{print $2\" \"$3}'`");
							//schParamMap.put("cdcCmd", cdcCmd.toString());
							//completeCmd = "if [ \"`cat "+backup_dir+"/next_bin.log`\" != '' ];then echo ok; else echo no; fi";
							completeCmd = "CHECK_DUMP_COMPLETE=`tail -1 "+backup_dir+"/"+strDate+".sql |awk '{print $2\" \"$3}'`";
							completeCmd += Constants.DATA_NEW_LINE;
							completeCmd += "if [ \"Dump completed\" == \"$CHECK_DUMP_COMPLETE\" ];then echo ok; else echo no; fi";
							
							StringBuilder cdcCmd = new StringBuilder();
							//cdcCmd.append("if [ \"Dump completed\" == \"$CHECK_DUMP_COMPLETE\" ];then");
							//cdcCmd.append(Constants.DATA_NEW_LINE);
							
							String ck2 = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+com_target_host+" \"tail -1 "+dump_binlog_path+".index\"";
							Map ckMap = new HashMap();
//							System.out.println("=========ck2==========");
//							System.out.println(ck2);
//							System.out.println("=========/ck2=========");
							ckMap = ssh2Service.getData(ck2, ms_usr, ms_pwd, ms_ip, ms_port, 5);
							if (ckMap != null) {
								
								if (ckMap.get("value") != null && !"".equals(ckMap.get("value"))) {
									
									if (ckMap.get("value").toString().charAt(0) == '.') {
										cdcCmd.append("TO_BIN=`echo "+ckMap.get("value").toString()+" | cut -b2-10000 2> "+backup_dir+"/copybin.txt`");
									} else {
										int count = StringUtils.countMatches(dump_binlog_path, "/");
										cdcCmd.append("TO_BIN=`echo "+ckMap.get("value").toString()+" | cut -d / -f "+(count+1)+" 2> "+backup_dir+"/copybin.txt`");
									}
									cdcCmd.append(Constants.DATA_NEW_LINE);
									cdcCmd.append("echo $TO_BIN > "+backup_dir+"/next_bin.log");
									cdcCmd.append(Constants.DATA_NEW_LINE);
									cdcCmd.append("if [ \"`cat "+backup_dir+"/next_bin.log`\" != '' ];then echo ok; else echo no; fi");
									
								}
							}
							schParamMap.put("cdcCmd", cdcCmd.toString());
							
							//* mysqldump end
						}
						schParamMap.put("completeCmd", completeCmd);//최종****
						
				    	//* schedule start
				    	schedulerHand = BackupScheduler.getInstance();
				    	if (schedulerHand.isScheduleRegJob(Constants.JOB_FULL_BACKUP_EXCUTE+"::"+bms_nam+bts_nam+"::" + strDate,Constants.GROUP_BACKUP+"::"+bms_nam+bts_nam)) {
				    		schedulerHand.deleteJob(Constants.JOB_FULL_BACKUP_EXCUTE+"::"+bms_nam+bts_nam + "::" + strDate,Constants.GROUP_BACKUP+"::"+bms_nam+bts_nam);
				    	}
//				    	System.out.println("=========schParamMap==========");
//				    	System.out.println(schParamMap);
//				    	System.out.println("=========/schParamMap==========");
				    	schedulerHand.startScheduler(schParamMap);
				    	schedulerHand.regiSchedulerFullBackup();
				    	//* schedule end
						
						//* =============================================================================================================
						//* 풀백업 end
						//* =============================================================================================================
					} else if ("L02".equals(lvl)) {
						//* =============================================================================================================
						//* 증분백업
						//* =============================================================================================================
						
						String binlog_path = "";
				    	
						Map<String,Object> blstparam = new HashMap<String,Object>();
						blstparam.put("knd", "G01");
						blstparam.put("suc_yon", "S01");
						blstparam.put("mtd", avt_nam);
						blstparam.put("bms_id", commandMap.get("bms_id"));
						blstparam.put("bts_id", commandMap.get("bts_id"));
						Map<String,Object> fbrl = setupService.findBakRunLst(commandMap.getMap());
						
						String dest_dir = "";
						String lst_lvl = "";
						String lst_wrk_dt = "";
						if (fbrl != null) {
							lst_lvl = fbrl.get("lvl").toString();
							lst_wrk_dt = fbrl.get("wrk_dt").toString();
							dest_dir = backup_pdir+"/"+lst_wrk_dt;
						}
						
						String BIN = "";
						StringBuilder binInfoCmd = new StringBuilder();
						if (avt_nam != null && "M01".equals(avt_nam)) {
							
							if ("L01".equals(lst_lvl)) {
								//압축일때 압축해제 먼저하고 진행
								if (fbmmap.get("cpr") != null) {
									if ("Y".equals(fbmmap.get("cpr").toString())) {
										binInfoCmd.append("qpress -d "+dest_dir+"/xtrabackup_binlog_info.qp "+dest_dir);
										binInfoCmd.append(Constants.DATA_NEW_LINE);
									}
								}
								binInfoCmd.append("head -1 "+dest_dir+"/xtrabackup_binlog_info  | awk '{print $1}'");
								
							} else if ("L02".equals(lst_lvl)) {
								binInfoCmd.append("cat "+dest_dir+"/next_bin.log");
							}
							
							binlog_path = fbmmap.get("xtr_bny_log_pth").toString();
						} else if (avt_nam != null && "M02".equals(avt_nam)) {
							binInfoCmd.append("cat "+dest_dir+"/next_bin.log");
							
							binlog_path = fbmmap.get("msq_bny_log_pth").toString();
						}
						schParamMap.put("binlog_path", binlog_path);
						
						Map<String,Object> binInfoMap = new HashMap<String,Object>();
						log.debug(binInfoCmd);
//						System.out.println("=========binInfoCmd==========");
//						System.out.println(binInfoCmd.toString());
//						System.out.println("=========/binInfoCmd=========");
						binInfoMap = ssh2Service.getList(binInfoCmd.toString(), ms_usr, ms_pwd, ms_ip, ms_port);
						
						List<String> binInfolist = new ArrayList<String>();
						String binLogInfoCmd = "";
						if (binInfoMap.get("list") != null) {
							binInfolist = (List) binInfoMap.get("list");
							
							if (binInfolist != null && binInfolist.size() > 0) {
								BIN = binInfolist.get(0).toString();
								
								String ck2 = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+com_target_host+" \"tail -1 "+binlog_path+".index\"";
								Map ckMap = new HashMap();
//								System.out.println("=========ck2==========");
//								System.out.println(ck2);
//								System.out.println("=========/ck2=========");
								ckMap = ssh2Service.getData(ck2, ms_usr, ms_pwd, ms_ip, ms_port, 5);
								if (ckMap != null && ckMap.get("value") != null) {
									
									if (!"".equals(ckMap.get("value"))) {
										
										if (ckMap.get("value").toString().charAt(0) == '.') {
											binLogInfoCmd = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+com_target_host+" \"cat "+binlog_path+".index | grep -A 99999 "+BIN+" | cut -b2-10000 \"";
										} else {
											binLogInfoCmd = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+com_target_host+" \"cat "+binlog_path+".index | grep -A 99999 "+BIN+"\"";
										}
									}
								} else {
									String errLogCmd = "echo \"ERROR :: Check the binary log path information.\" >> "+backup_dir+"/"+err_log_file;
									log.debug(errLogCmd);
//									System.out.println("=========errLogCmd==========");
//									System.out.println(errLogCmd);
//									System.out.println("=========/errLogCmd=========");
									ssh2Service.getData(errLogCmd, ms_usr, ms_pwd, ms_ip, ms_port);
									exit_flag = "1";
									
								}
							}
						}
						String flushCmd = ms_bny_pth+"/mysql -u"+com_user+" -p"+com_pass+" -h "+com_target_host+" -P "+com_target_port+" -e\"flush logs;\"";
						schParamMap.put("binLogInfoCmd", binLogInfoCmd);
						schParamMap.put("flushCmd", flushCmd);
						
						if ("".equals(BIN)) {
							String errLogCmd = "echo \"BACKUP TARGET NOT EXISTS\" >> "+backup_dir+"/"+err_log_file;
							log.debug(errLogCmd);
//							System.out.println("=========errLogCmd==========");
//							System.out.println(errLogCmd);
//							System.out.println("=========/errLogCmd=========");
							ssh2Service.getData(errLogCmd, ms_usr, ms_pwd, ms_ip, ms_port);
							exit_flag = "1";
						}
						schParamMap.put("exit_flag", exit_flag);
				    	
						//* schedule start
				    	schedulerHand = BackupScheduler.getInstance();
				    	if (schedulerHand.isScheduleRegJob(Constants.JOB_INCRE_BACKUP_EXCUTE+"::"+bms_nam+bts_nam + "::" + strDate,Constants.GROUP_BACKUP+"::"+bms_nam+bts_nam)) {
				    		schedulerHand.deleteJob(Constants.JOB_INCRE_BACKUP_EXCUTE+"::"+bms_nam+bts_nam + "::" + strDate,Constants.GROUP_BACKUP+"::"+bms_nam+bts_nam);
				    	}
//				    	System.out.println("=========schParamMap==========");
//				    	System.out.println(schParamMap);
//				    	System.out.println("=========/schParamMap==========");
				    	schedulerHand.startScheduler(schParamMap);
				    	schedulerHand.regiSchedulerIncreBackup();
				    	//* schedule end
						
						//* =============================================================================================================
						//* 증분백업 end
						//* =============================================================================================================
					}
					mv.addObject("run_result", true);
				}
			}
		}
    	
    	return mv;
    }
    
    @RequestMapping(value="/bak_run_log")
	public ModelAndView bak_run_log(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
		Map<String,Object> logmap = setupService.findLogDetailMap(commandMap.getMap());
		StringBuilder resmap = new StringBuilder();
		if (logmap != null) {
			StringBuilder line = new StringBuilder();
			line.append(logmap.get("log"));
			if (line.toString().split("\n").length > 5000) {
				String[] lines = line.toString().split("\n");
				for (int i=0; i<5000; i++) {
					resmap.append(lines[i]);
					if (i != 4999) {
						resmap.append("\n");
					} else {
						resmap.append("\n");
						resmap.append("................................................... the last part omitted");
					}
				}
			} else {
				resmap.append(logmap.get("log"));
			}
			mv.addObject("logmap", resmap);
		}
    	return mv;
    }
    
    @Value("#{ssh['ssh.XXIII.1']}") private String sshXXIII1;
    @RequestMapping(value="/bak_run_endChk")
	public ModelAndView bak_run_endChk(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	String backup_dir = commandMap.get("backup_dir").toString();
    	String lvl = commandMap.get("lvl").toString();
		String avt_nam = commandMap.get("avt_nam").toString();
		String wrk_dt = commandMap.get("wrk_dt").toString();
		String bms_nam = commandMap.get("bms_nam").toString();
		String bts_nam = commandMap.get("bts_nam").toString();
		String kep_pod = commandMap.get("kep_pod").toString();
		String bak_typ = commandMap.get("bak_typ").toString();
		
		String ms_usr = commandMap.get("ms_usr").toString();
		String ms_pwd = commandMap.get("ms_pwd").toString();
		String ms_ip = commandMap.get("ms_ip").toString();
		int ms_port = Integer.parseInt(commandMap.get("ms_port").toString());
		
		String completeCmd = "";
		Map<String,Object> completeMap = new HashMap<String,Object>();
		
		completeCmd = "if [ \"`cat "+backup_dir+"/ginian.log | grep BACKUP\\ END`\" != '' ]; then echo ok; else echo no; fi";
		completeMap = ssh2Service.getData(completeCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
		if (completeMap.get("value") != null) {
			mv.addObject("complete", completeMap.get("value"));
			
			String completeResult = completeMap.get("value").toString();
			
			if ("ok".equals(completeResult)) {
				schedulerHand = BackupScheduler.getInstance();
				if (!schedulerHand.isScheduleRegJob(Constants.JOB_FULL_BACKUP_EXCUTE+"::"+bms_nam+bts_nam + "::" + wrk_dt,Constants.GROUP_BACKUP+"::"+bms_nam+bts_nam)) {
					Map<String,Object> ginianLogMap = new HashMap<String,Object>();
					String ginianLogCmd = "cat "+backup_dir+"/ginian.log";
					ginianLogMap = ssh2Service.getList(ginianLogCmd, ms_usr, ms_pwd, ms_ip, ms_port);
					if (ginianLogMap != null) {
						List<String> list = new ArrayList<String>();
						list = (List) ginianLogMap.get("list");
						if (list != null && list.size() > 0) {
							StringBuilder strb = new StringBuilder();
							boolean bakRlt = false;
							String success_result = "Result : SUCCESS";
							String pas_tme = "";
							for (int i=0; i<list.size(); i++) {
								String list_msg = list.get(i).toString();
								if (strb.length() > 0) strb.append(Constants.DATA_NEW_LINE);
								if (list_msg.equals(success_result)) {
									bakRlt = true;
								}
								if (list.get(i).matches("(.*)ELAPSED TIME :(.*)")) {
									pas_tme = list.get(i).replace("ELAPSED TIME :", "").trim();
								}
								strb.append(list.get(i));
							}
							Map<String,Object> paramMap = new HashMap<String,Object>();
							paramMap.put("knd", "G01");
							paramMap.put("wrk_dt", wrk_dt);
							paramMap.put("bts_nam", bts_nam);
							paramMap.put("bms_id", commandMap.get("bms_id"));
							paramMap.put("bts_id", commandMap.get("bts_id"));
							paramMap.put("suc_yon", bakRlt == true ? "S01" : "S02");
							paramMap.put("typ", bak_typ);
							paramMap.put("lvl", lvl);
							paramMap.put("mtd", avt_nam);
							paramMap.put("pas_tme", pas_tme);
							paramMap.put("kep_pod", kep_pod);
							paramMap.put("log", strb.toString());
							if (bakRlt) {
								if ("BT01".equals(bak_typ)) {
									boolean runtb = setupService.insertBakRun(paramMap);
								}
							}
					    	boolean result = setupService.insertLog(paramMap, request);
							mv.addObject("complete_msg", bakRlt == true ? "백업이 완료되었습니다." : "백업이 실패되었습니다!!!");
						}
					}
				}
			}
		}
		
    	return mv;
    }
    
    @RequestMapping(value="/bak_run_progress")
    public ModelAndView bak_run_progress(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	String backup_dir = commandMap.get("backup_dir").toString();
    	String lvl = commandMap.get("lvl").toString();
    	String bms_ip = commandMap.get("bms_ip").toString();
    	int bms_pot = Integer.parseInt(commandMap.get("bms_pot").toString());
    	String bms_usr = commandMap.get("bms_usr").toString();
    	String bms_pwd = commandMap.get("bms_pwd").toString();
    	String avt_nam = commandMap.get("avt_nam").toString();
    	String ssh_usr = commandMap.get("ssh_usr").toString();
    	String hst_ip = commandMap.get("hst_ip").toString();
    	
    	if ("L01".equals(lvl) && "M01".equals(avt_nam)) {
    		String targetDirCmd = "du "+backup_dir+" | tail -1 | awk '{print $1}'";
    		Map<String,Object> targetDirMap = new HashMap<String,Object>();
    		targetDirMap = ssh2Service.getData(targetDirCmd, bms_usr, bms_pwd, bms_ip, bms_pot);
    		String targetDir = "0";
    		if (targetDirMap != null) {
    			targetDir = targetDirMap.get("value").toString();
    		}
    		
    		Map<String,Object> pmap = new HashMap<String,Object>();
    		pmap.put("dc", dc);
    		pmap.put("sql", sql6);
    		pmap.put("id", bms_usr);
    		pmap.put("pw", bms_pwd);
    		pmap.put("ip", bms_ip);
    		pmap.put("port", String.valueOf(bms_pot));
			//dbMaxAllowedPacketSet(pmap);
    		
    		pmap = new HashMap<String,Object>();
    		pmap.put("dc", dc);
    		pmap.put("sql", sql5);
    		pmap.put("id", bms_usr);
    		pmap.put("pw", bms_pwd);
    		pmap.put("ip", bms_ip);
    		pmap.put("port", String.valueOf(bms_pot));
    		Map<String,Object> dbMap = DBAgent.getData(pmap);
    		String dbDir = "0";
    		String sourceDir = "0";
    		if (dbMap != null) {
    			List<Map<String,Object>> list = (List) dbMap.get("list");
    			if (list != null && list.size() > 0) {
    				Map<String,Object> map = list.get(0);
    				dbDir = map.get("VALUE").toString();
    				
    				String sourceDirCmd = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+ssh_usr+"@"+hst_ip+" du "+dbDir+" | tail -1 | awk '{print $1}'";
    				Map<String,Object> sourceDirMap = new HashMap<String,Object>();
    				sourceDirMap = ssh2Service.getData(sourceDirCmd, bms_usr, bms_pwd, bms_ip, bms_pot);
    				
    				if (sourceDirMap != null) {
    					sourceDir = sourceDirMap.get("value").toString();
    				}
    			}
    		}
    		
    		String processVal = "0";
    		if (!"0".equals(sourceDir) && !"0".equals(targetDir)) {
    			double target = Double.parseDouble(targetDir);
    			double source = Double.parseDouble(sourceDir);
    			double processVald = Math.ceil(Double.parseDouble(String.valueOf((target/source)*100)));
    			processVal = (String.format("%.0f" , processVald));
    			if (StringUtil.isNumber(processVal)) {
    				mv.addObject("processVal", processVal+"%");
    			}
    		}
    		
    	}
    	
    	return mv;
    }
    
    @RequestMapping(value="/bak_run_cpu")
    public ModelAndView bak_run_cpu(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	String ssh_usr = commandMap.get("ssh_usr").toString();
    	String hst_ip = commandMap.get("hst_ip").toString();
    	String bms_ip = commandMap.get("bms_ip").toString();
    	int bms_pot = Integer.parseInt(commandMap.get("bms_pot").toString());
    	String bms_usr = commandMap.get("bms_usr").toString();
    	String bms_pwd = commandMap.get("bms_pwd").toString();
    	
    	String cpuUseCmd = "LANG=C; sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+ssh_usr+"@"+hst_ip+" LANG=C;echo \"100 - `sar -P ALL 1 1 | grep all | head -1 | awk '{print $8}'`\" | bc -l";
    	Map<String,Object> cpuUseMap = new HashMap<String,Object>();
    	cpuUseMap = ssh2Service.getData(cpuUseCmd, bms_usr, bms_pwd, bms_ip, bms_pot);
    	String cpuUseVal = "0";
    	if (cpuUseMap != null) {
    		if (StringUtil.isNumber(cpuUseMap.get("value").toString())) {
    			cpuUseVal = cpuUseMap.get("value").toString();
    		}
    	}
    	mv.addObject("cpuUseVal", cpuUseVal+"%");
    	
    	return mv;
    }
    
    @RequestMapping(value="/bak_run_mem")
    public ModelAndView bak_run_mem(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	String ssh_usr = commandMap.get("ssh_usr").toString();
    	String hst_ip = commandMap.get("hst_ip").toString();
    	String bms_ip = commandMap.get("bms_ip").toString();
    	int bms_pot = Integer.parseInt(commandMap.get("bms_pot").toString());
    	String bms_usr = commandMap.get("bms_usr").toString();
    	String bms_pwd = commandMap.get("bms_pwd").toString();
    	
    	String memUseCmd = "LANG=C; sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+ssh_usr+"@"+hst_ip+" LANG=C;sar -r 1 1 | tail -2 | head -1 | awk '{print $8}'";
    	Map<String,Object> memUseMap = new HashMap<String,Object>();
    	memUseMap = ssh2Service.getData(memUseCmd, bms_usr, bms_pwd, bms_ip, bms_pot);
    	String memUseVal = "0";
    	if (memUseMap != null) {
    		if (StringUtil.isNumber(memUseMap.get("value").toString())) {
    			memUseVal = memUseMap.get("value").toString();
    		}
    	}
    	mv.addObject("memUseVal", memUseVal+"%");
    	
    	return mv;
    }
    
    @RequestMapping(value="/bak_run_io")
    public ModelAndView bak_run_io(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	String ssh_usr = commandMap.get("ssh_usr").toString();
    	String hst_ip = commandMap.get("hst_ip").toString();
    	String bms_ip = commandMap.get("bms_ip").toString();
    	int bms_pot = Integer.parseInt(commandMap.get("bms_pot").toString());
    	String bms_usr = commandMap.get("bms_usr").toString();
    	String bms_pwd = commandMap.get("bms_pwd").toString();
    	
    	Map<String,Object> pmap = new HashMap<String,Object>();
		pmap.put("dc", dc);
		pmap.put("sql", sql5);
		pmap.put("id", bms_usr);
		pmap.put("pw", bms_pwd);
		pmap.put("ip", bms_ip);
		pmap.put("port", String.valueOf(bms_pot));
		Map<String,Object> dbDirMap = DBAgent.getData(pmap);
		String ioUseVal = "0";
		if (dbDirMap != null) {
			List<Map<String,Object>> list = (List) dbDirMap.get("list");
			if (list != null && list.size() > 0) {
				Map<String,Object> map = list.get(0);
				String dbDir = map.get("VALUE").toString();
				
				String ioUseCmd = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+ssh_usr+"@"+hst_ip+"\n";
				ioUseCmd += "for i in `LANG=C;sar -dp 1 1 | grep -v \"Average\" | grep -v \"Linux\" | grep -v \"DEV\" | awk '{print $2}'`; do if [ ! -z `df -h "+dbDir+" | head -2 | tail -1 | grep $i | awk '{print $1}'` ]; then LANG=C;sar -dp 1 1 | grep $i | head -1 | awk '{print $10}' ; fi; done";
				Map<String,Object> ioUseMap = new HashMap<String,Object>();
				ioUseMap = ssh2Service.getData(ioUseCmd, bms_usr, bms_pwd, bms_ip, bms_pot);
				if (ioUseMap != null) {
					if (StringUtil.isNumber(ioUseMap.get("value").toString())) {
						ioUseVal = ioUseMap.get("value").toString();
					}
				}
			}
		}
		mv.addObject("ioUseVal", ioUseVal+"%");
    	
    	
    	return mv;
    }
    
    @RequestMapping(value="/bak_run_disk")
    public ModelAndView bak_run_disk(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	String backup_dir = commandMap.get("backup_dir").toString();
    	String bms_ip = commandMap.get("bms_ip").toString();
    	int bms_pot = Integer.parseInt(commandMap.get("bms_pot").toString());
    	String bms_usr = commandMap.get("bms_usr").toString();
    	String bms_pwd = commandMap.get("bms_pwd").toString();
    	
    	String diskUseCmd = MessageFormat.format(sshXXIII1, backup_dir);
    	Map<String,Object> diskUseMap = new HashMap<String,Object>();
    	diskUseMap = ssh2Service.getData(diskUseCmd, bms_usr, bms_pwd, bms_ip, bms_pot);
    	String diskUseVal = "0";
    	if (diskUseMap != null) {
    		diskUseVal = diskUseMap.get("value").toString();
    	}
    	mv.addObject("diskUseVal", diskUseVal);
    	
    	return mv;
    }
    
    @RequestMapping(value="/bak_run_status")
    public ModelAndView bak_run_status(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");

    	String backup_dir = commandMap.get("backup_dir").toString();
    	String backup_pdir = commandMap.get("backup_pdir").toString();
    	String lvl = commandMap.get("lvl").toString();
    	String avt_nam = commandMap.get("avt_nam").toString();
    	//String ssh_usr = commandMap.get("ssh_usr").toString();
    	String hst_ip = commandMap.get("hst_ip").toString();
    	String rmt_msq_bny_pth = commandMap.get("rmt_msq_bny_pth").toString();

    	String bms_usr = commandMap.get("bms_usr").toString();
    	String bms_pwd = commandMap.get("bms_pwd").toString();
    	String bts_usr = commandMap.get("bts_usr").toString();
    	String bts_pwd = commandMap.get("bts_pwd").toString();
    	String pot_num = commandMap.get("pot_num").toString();
    	
		String ms_usr = commandMap.get("ms_usr").toString();
		String ms_pwd = commandMap.get("ms_pwd").toString();
		String ms_ip = commandMap.get("ms_ip").toString();
		int ms_port = Integer.parseInt(commandMap.get("ms_port").toString());
		String ms_bny_pth = commandMap.get("ms_bny_pth").toString();
    	
		try {
	    	if ("L01".equals(lvl) && "M01".equals(avt_nam)) {
	    		String targetDirCmd = "du "+backup_dir+" | tail -1 | awk '{print $1}'";
	    		Map<String,Object> targetDirMap = new HashMap<String,Object>();
	    		targetDirMap = ssh2Service.getData(targetDirCmd, ms_usr, ms_pwd, ms_ip, ms_port, 3);
	    		String targetDir = "0";
	    		if (targetDirMap != null) {
	    			if (targetDirMap.get("value") != null) {
	    				targetDir = targetDirMap.get("value").toString();
	    			}
	    		}
	    		
	    		String dbDirCmd = ms_bny_pth+"/mysql -u"+bts_usr+" -p"+bts_pwd+" -h "+hst_ip+" -P "+pot_num+" -BNse\"show global variables like '%datadir%';\" | awk '{print $2}'";
	    		Map<String,Object> dbDirMap = new HashMap<String,Object>();
	    		dbDirMap = ssh2Service.getData(dbDirCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
	    		String dbDir = "";
	    		String sourceDir = "0";
	    		if (dbDirMap != null) {
	    			if (dbDirMap.get("value") != null) {
	    				dbDir = dbDirMap.get("value").toString();
	    				String sourceDirCmd = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+hst_ip+" du "+dbDir+" | tail -1 | awk '{print $1}'";
	    				
	    				Map<String,Object> sourceDirMap = new HashMap<String,Object>();
	    				sourceDirMap = ssh2Service.getData(sourceDirCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
	    				if (sourceDirMap != null) {
	    					if (sourceDirMap.get("value") != null) {
	    						sourceDir = sourceDirMap.get("value").toString();
	    						if (!StringUtil.isNumber(sourceDir)) {
	    							sourceDir = "0";
	    						}
	    					}
	    				}
	    			}
	    		}
	    		
	    		String processVal = "0";
	    		if (StringUtil.isNumber(sourceDir) && StringUtil.isNumber(targetDir)) {
	    			if (!"0".equals(sourceDir) && !"0".equals(targetDir)) {
	    				double target = Double.parseDouble(targetDir);
	    				double source = Double.parseDouble(sourceDir);
	    				double processVald = Math.ceil(Double.parseDouble(String.valueOf((target/source)*100)));
	    				processVal = (String.format("%.0f" , processVald));
	    				if (!StringUtil.isNumber(processVal)) {
	    					processVal = "0";
	    				}
	    			}
	    		}
	    		mv.addObject("processVal", processVal);
	    	}
		} catch (Exception e) {
			log.error("processVal Exception : ", e.fillInStackTrace());
		}
    	
    	try {
	    	String cpuUseCmd = "LANG=C; sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+hst_ip+" LANG=C;echo \"100 - `sar -P ALL 1 1 | grep all | head -1 | awk '{print $8}'`\" | bc -l";
	    	Map<String,Object> cpuUseMap = new HashMap<String,Object>();
	    	cpuUseMap = ssh2Service.getData(cpuUseCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
	    	String cpuUseVal = "0";
	    	if (cpuUseMap != null) {
	    		if (cpuUseMap.get("value") != null) {
	    			cpuUseVal = cpuUseMap.get("value").toString();
	    			if (!StringUtil.isNumber(cpuUseVal)) {
	    				cpuUseVal = "0";
	    			}
	    		}
	    	}
	    	mv.addObject("cpuUseVal", cpuUseVal);
    	} catch (Exception e) {
    		log.error("cpuUse Exception : ", e.fillInStackTrace());
		}
    	
    	try {
	    	String memUseCmd = "LANG=C; sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+hst_ip+" LANG=C;sar -r 1 1 | tail -2 | head -1 | awk '{print $8}'";
	    	Map<String,Object> memUseMap = new HashMap<String,Object>();
	    	memUseMap = ssh2Service.getData(memUseCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
	    	String memUseVal = "0";
	    	if (memUseMap != null) {
	    		if (memUseMap.get("value") != null) {
	    			memUseVal = memUseMap.get("value").toString();
	    			if (!StringUtil.isNumber(memUseVal)) {
	    				memUseVal = "0";
	    			}
	    		}
	    	}
	    	mv.addObject("memUseVal", memUseVal);
    	} catch (Exception e) {
    		log.error("memUse Exception : ", e.fillInStackTrace());
		}
    	
    	try {
	    	String diskUseCmd = MessageFormat.format(sshXXIII1, backup_pdir);
	    	Map<String,Object> diskUseMap = new HashMap<String,Object>();
	    	diskUseMap = ssh2Service.getData(diskUseCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
	    	String diskUseVal = "0";
	    	if (diskUseMap != null) {
	    		if (diskUseMap.get("value") != null) {
	    			diskUseVal = diskUseMap.get("value").toString().replaceAll("%", "");
	    			if (!StringUtil.isNumber(diskUseVal)) {
	    				diskUseVal = "0";
	    			}
	    		}
	    	}
	    	mv.addObject("diskUseVal", diskUseVal);
    	} catch (Exception e) {
    		log.error("diskUse Exception : ", e.fillInStackTrace());
		}
    	
	    try {
	    	String swapCmd = "SWAP=`sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+hst_ip+" \"free | grep -i swap\"`;echo \"scale=2;`echo $SWAP | awk '{print $3}'` / `echo $SWAP | awk '{print $2}'` * 100\" | bc -l";
	    	Map<String,Object> swapMap = new HashMap<String,Object>();
	    	swapMap = ssh2Service.getData(swapCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
	    	String swapVal = "0";
	    	if (swapMap != null) {
	    		if (swapMap.get("value") != null) {
	    			swapVal = swapMap.get("value").toString();
	    			if (StringUtil.isNumber(swapVal)) {
	    				swapVal = (String.format("%.0f" , Double.parseDouble(swapVal)));
	    			}
	    		}
	    	}
	    	mv.addObject("swapVal", swapVal);
	    } catch (Exception e) {
	    	log.error("swapVal Exception : ", e.fillInStackTrace());
		}
	    
    	return mv;
    }
    
    /**
     * 복구 - 마지막 백업
     * @param commandMap
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/set_r_rb")
    public ModelAndView set_r_rb(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/recovery/recent_backup");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		Map<String,Object> pmap = new HashMap<String,Object>();
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
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvrNam(commandMap.getMap());
		if (btmap != null && btmap.get("bts_nam") != null) {
			mv.addObject("bts_nam", btmap.get("bts_nam"));
		}
		
		commandMap.put("knd", "G01");
		Map<String,Object> llmap = setupService.findLogLst(commandMap.getMap());
		if (llmap != null) {
			mv.addObject("llmap", llmap);
		}
		
    	return mv;
    }
    
    /**
     * 복구 - 이력
     * @param commandMap
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/set_r_rec")
    public ModelAndView set_r_rec(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/recovery/history");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
    	String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
    	String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
    	String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
    	String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		Map<String,Object> pmap = new HashMap<String,Object>();
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
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvrNam(commandMap.getMap());
		if (btmap != null && btmap.get("bts_nam") != null) {
			mv.addObject("bts_nam", btmap.get("bts_nam"));
		}
		
		commandMap.put("knd", "G02");
    	List<Map<String,Object>> isList = setupService.findIsLog(commandMap.getMap());
		if (isList != null && isList.size() > 0) {
			mv.addObject("isList", isList);
		}
		
    	return mv;
    }
    
    @RequestMapping(value="/set_r_rec_log")
	public ModelAndView set_r_rec_log(CommandMap commandMap) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	if (commandMap.get("day") != null && !"".equals(commandMap.get("day"))) {
    		String sDay = StringUtil.dateFormatStr(commandMap.get("day").toString(),"yyyy-MM-dd");
    		if (StringUtil.toDayCompare(commandMap.get("day").toString())) {
    			sDay += " (오늘)";
    		}
    		mv.addObject("sDay", sDay);
    	}
    	
    	commandMap.put("knd", "G02");
    	List<Map<String,Object>> list = setupService.findLogMM(commandMap.getMap());
		if (list != null && list.size() > 0) {
			mv.addObject("list", list);
		}
    	
		return mv;
    }
    
    @RequestMapping(value="/set_r_rec_logdetail")
	public ModelAndView set_r_rec_logdetail(CommandMap commandMap) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	commandMap.put("knd", "G02");
    	Map<String,Object> logmap = setupService.findLogDetail(commandMap.getMap());
    	StringBuilder resmap = new StringBuilder();
		if (logmap != null) {
			StringBuilder line = new StringBuilder();
			line.append(logmap.get("log"));
			if (line.toString().split("\n").length > 5000) {
				String[] lines = line.toString().split("\n");
				for (int i=0; i<5000; i++) {
					resmap.append(lines[i]);
					if (i != 4999) {
						resmap.append("\n");
					} else {
						resmap.append("\n");
						resmap.append("................................................... the last part omitted");
					}
				}
			} else {
				resmap.append(logmap.get("log"));
			}
			mv.addObject("logmap", resmap);
		}
		return mv;
    }
    
    @RequestMapping(value="/set_r_set")
    public ModelAndView set_r_set(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/recovery/setting");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
    	String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
    	String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
    	String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
    	String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		Map<String,Object> pmap = new HashMap<String,Object>();
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
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		
		Map<String,Object> btmap = setupService.findTgtSvr(commandMap.getMap());
		if (btmap != null) {
			if (btmap.get("bts_nam") != null) {
				mv.addObject("bts_nam", btmap.get("bts_nam"));
			}
			if (btmap.get("bts_mlt_ins") != null) {
				mv.addObject("bts_mlt_ins", btmap.get("bts_mlt_ins"));
			}
			
		}
		
		//백업방법
		Map<String,Object> fbmmap = setupService.findBakMtd(commandMap.getMap());
		if (fbmmap != null && fbmmap.get("avt_nam") != null) {
			mv.addObject("avt_nam", fbmmap.get("avt_nam"));
		}
		
		pmap = new HashMap<String,Object>();
		pmap.put("pet_cod", "RS");
		List<Map<String,Object>> rslist = setupService.findType(pmap);
		if (rslist != null && rslist.size() > 0) {
			mv.addObject("rslist", rslist);
		}
    	
		Map<String,Object> frsmap = setupService.findRsrSet(commandMap.getMap());
		if (frsmap != null) {
			mv.addObject("frsmap", frsmap);
		}
		
		mv.addObject("insert_result", commandMap.get("insert_result"));
		
    	return mv;
    }

    @RequestMapping(value="/insertRsrSet")
	public ModelAndView insertRsrSet(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("redirect:/set_r_set");
    	
    	boolean result = setupService.insertRsrSet(commandMap.getMap(), request);
    	mv.addObject("insert_result", result);
    	mv.addObject("menu_cd", commandMap.get("menu_cd"));
    	mv.addObject("menu_id", commandMap.get("menu_id"));
    	mv.addObject("bms_id", commandMap.get("bms_id"));
    	mv.addObject("bts_id", commandMap.get("bts_id"));
    	
		return mv;
    }
    
    @RequestMapping(value="/set_r_smy")
    public ModelAndView set_r_smy(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/recovery/summary");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
    	String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
    	String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
    	String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
    	String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		Map<String,Object> pmap = new HashMap<String,Object>();
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
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvr(commandMap.getMap());
		if (btmap != null) {
			if (btmap.get("bts_nam") != null) {
				mv.addObject("bts_nam", btmap.get("bts_nam"));
			}
			if (btmap.get("bts_mlt_ins") != null) {
				mv.addObject("bts_mlt_ins", btmap.get("bts_mlt_ins"));
			}
		}
		
		//백업방법
		Map<String,Object> fbmmap = setupService.findBakMtd(commandMap.getMap());
		if (fbmmap != null && fbmmap.get("avt_nam") != null) {
			mv.addObject("avt_nam", fbmmap.get("avt_nam"));
		}
    	
		commandMap.put("knd", "G02");
		Map<String,Object> llmap = setupService.findLogLst(commandMap.getMap());
		if (llmap != null) {
			mv.addObject("llmap", llmap);
		} else {
			mv.addObject("rsr_lst_info", true);
		}
		
		Map<String,Object> frsmap = setupService.findRsrSet(commandMap.getMap());
		if (frsmap != null) {
			mv.addObject("frsmap", frsmap);
		} else {
			mv.addObject("rsr_set_blank", true);
		}
    	return mv;
    }
    
    @RequestMapping(value="/set_r_run")
    public ModelAndView set_r_run(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/recovery/run");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
    	String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
    	String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
    	String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
    	String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		Map<String,Object> pmap = new HashMap<String,Object>();
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
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		
		Map<String,Object> bmmap = setupService.findMngSvrNam(commandMap.getMap());
		if (bmmap != null && bmmap.get("bms_nam") != null) {
			mv.addObject("bms_nam", bmmap.get("bms_nam"));
		}
		
		Map<String,Object> btmap = setupService.findTgtSvr(commandMap.getMap());
		if (btmap != null) {
			if (btmap.get("bts_nam") != null) {
				mv.addObject("bts_nam", btmap.get("bts_nam"));
			}
			if (btmap.get("bts_mlt_ins") != null) {
				mv.addObject("bts_mlt_ins", btmap.get("bts_mlt_ins"));
			}
		}
		
		//백업방법
		Map<String,Object> fbmmap = setupService.findBakMtd(commandMap.getMap());
		if (fbmmap != null && fbmmap.get("avt_nam") != null) {
			mv.addObject("avt_nam", fbmmap.get("avt_nam"));
		}
		
		Map<String,Object> frsmap = setupService.findRsrSet(commandMap.getMap());
		if (frsmap != null) {
			mv.addObject("frsmap", frsmap);
		} else {
			mv.addObject("rsr_set_blank", true);
		}
		
    	return mv;
    }
    
    @RequestMapping(value="/set_r_target")
    public ModelAndView set_r_target(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	commandMap.put("knd", "G01");
    	commandMap.put("suc_yon", "S01");
    	List<Map<String,Object>> list = setupService.findBakRun(commandMap.getMap());
    	if (list != null && list.size() > 0) {
    		mv.addObject("list", list);
    	}
    	
    	return mv;
    }
    
    @RequestMapping(value="/set_rsr_run")
    public ModelAndView set_rsr_run(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("/setup/recovery/exe_run");
    	
    	Map<String,Object> mcmap = summaryService.menu_id(commandMap.getMap());
		String menu_cd = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod"), "");
		String pet_cod = StringUtils.defaultIfEmpty((String) mcmap.get("pet_cod"), "");
		String menu_id = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_cmt"), "");
		String menu_nm = StringUtils.defaultIfEmpty((String) mcmap.get("com_cod_nam"), "");
		
		mv.addObject("menu_cd", menu_cd);
		mv.addObject("pet_cod", pet_cod);
		mv.addObject("menu_id", menu_id);
		mv.addObject("menu_nm", menu_nm);
		mv.addObject("bms_id", commandMap.get("bms_id"));
		mv.addObject("bts_id", commandMap.get("bts_id"));
		mv.addObject("bmt_id", commandMap.get("bmt_id"));
		mv.addObject("bms_nam", commandMap.get("bms_nam"));
		mv.addObject("bts_nam", commandMap.get("bts_nam"));
		mv.addObject("wrk_dt", commandMap.get("wrk_dt"));
		mv.addObject("lvl", commandMap.get("lvl"));
		mv.addObject("mtd", commandMap.get("mtd"));
		mv.addObject("typ", commandMap.get("typ"));
		mv.addObject("kep_pod", commandMap.get("kep_pod"));
		mv.addObject("run_id", commandMap.get("run_id"));
		mv.addObject("cpr", commandMap.get("cpr"));
		mv.addObject("hst_ip", commandMap.get("hst_ip"));
		
    	return mv;
    }
    
    
    @RequestMapping(value="/rsr_run_exe")
	public ModelAndView rsr_run_exe(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	Map<String,Object> schParamMap = new HashMap<String,Object>();
    	schParamMap.put("interval", Constants.INTERVAL_1DAY);
    	
    	String log_file = "";
    	String log_file_path = "";
    	String err_log_file = "";
    	String err_log_file_path = "";
    	String strDate = StringUtil.getToday("yyyyMMddHHmmss");
    	
    	String bms_nam = commandMap.get("bms_nam").toString();
    	String bts_nam = commandMap.get("bts_nam").toString();
    	String wrk_dt = commandMap.get("wrk_dt").toString();
    	String lvl = commandMap.get("lvl").toString();
    	String mtd = commandMap.get("mtd").toString();
    	String typ = commandMap.get("typ").toString();
    	String kep_pod = commandMap.get("kep_pod").toString();
    	String run_id = commandMap.get("run_id").toString();
    	String hst_ip = commandMap.get("hst_ip").toString();
    	String backup_pdir = "", backup_dir = "", restore_dir = "";
    	
    	//관리서버
    	Map<String,Object> param = new HashMap<String,Object>();
		param.put("ms_id", commandMap.get("bms_id").toString().charAt(0));
		Map<String,Object> msmap = manageService.findSvrMap(param);
		String ms_ip = "";
		int ms_port = 0;
		String ms_usr = "";
		String ms_pwd = "";
		String ms_sve_dir = "";
		//String ms_ssh_usr = "";
		String ms_bny_pth = "";
		if (msmap != null) {
			ms_ip = msmap.get("ms_ip").toString();
			ms_port = Integer.valueOf(msmap.get("ms_port").toString());
			ms_usr = msmap.get("ms_usr").toString();
			ms_pwd = msmap.get("ms_pwd").toString();
			ms_sve_dir = msmap.get("ms_sve_dir").toString();
			//ms_ssh_usr = msmap.get("ms_ssh_usr").toString();
			ms_bny_pth = msmap.get("ms_bny_pth").toString();
			
			mv.addObject("ms_ip", ms_ip);
			mv.addObject("ms_port", ms_port);
			mv.addObject("ms_usr", ms_usr);
			mv.addObject("ms_pwd", ms_pwd);
			
			schParamMap.put("ms_ip", ms_ip);
			schParamMap.put("ms_port", String.valueOf(ms_port));
			schParamMap.put("ms_usr", ms_usr);
			schParamMap.put("ms_pwd", ms_pwd);
			schParamMap.put("ms_bny_pth", ms_bny_pth);
		}
		if (!"".equals(ms_sve_dir)) {
			backup_pdir = ms_sve_dir+"/"+bms_nam+"/"+bts_nam+"/BACKUP";
			backup_dir = backup_pdir+"/"+wrk_dt;
			
			mv.addObject("backup_pdir", backup_pdir);
			mv.addObject("backup_dir", backup_dir);
			
			schParamMap.put("backup_pdir", backup_pdir);
			schParamMap.put("backup_dir", backup_dir);
		}
		
		mv.addObject("bms_nam", bms_nam);
		mv.addObject("bts_nam", bts_nam);
		mv.addObject("wrk_dt", wrk_dt);
		mv.addObject("lvl", lvl);
		mv.addObject("mtd", mtd);
		mv.addObject("typ", typ);
		mv.addObject("kep_pod", kep_pod);
		mv.addObject("run_id", run_id);
		mv.addObject("hst_ip", hst_ip);
		
		schParamMap.put("bms_nam", bms_nam);
		schParamMap.put("bts_nam", bts_nam);
    	schParamMap.put("wrk_dt", wrk_dt);
    	schParamMap.put("lvl", lvl);
    	schParamMap.put("mtd", mtd);

    	String restore_logDir = "";
		String restore_tmp = "";
		if (!"".equals(ms_sve_dir)) {
			long startTime = System.currentTimeMillis();
			restore_logDir = ms_sve_dir+"/"+bms_nam+"/"+bts_nam+"/RESTORE/"+wrk_dt;
			restore_tmp = ms_sve_dir+"/"+bms_nam+"/"+bts_nam+"/RESTORE/tmp";
			err_log_file = "error_"+strDate+".log";
			log_file = strDate+".log";
			
			StringBuilder dir_makcmd = new StringBuilder();
			dir_makcmd.append("if [ ! -d "+restore_logDir+" ]; then");
			dir_makcmd.append(" mkdir -p "+restore_logDir+";");
			dir_makcmd.append(Constants.DATA_NEW_LINE);
			dir_makcmd.append(" touch "+restore_logDir+"/"+log_file);
			dir_makcmd.append(Constants.DATA_NEW_LINE);
			dir_makcmd.append(" touch "+restore_logDir+"/"+err_log_file+"; fi");
			dir_makcmd.append(Constants.DATA_NEW_LINE);
			dir_makcmd.append("if [ ! -d "+restore_tmp+" ]; then mkdir -p "+restore_tmp+"; fi");
			Map<String,Object> dirmkMap = new HashMap<String,Object>();
			log.debug(dir_makcmd);
			dirmkMap = ssh2Service.getData(dir_makcmd.toString(), ms_usr, ms_pwd, ms_ip, ms_port, 5);
			long endTime = System.currentTimeMillis();
			
			log_file_path = restore_logDir+"/"+log_file;
			err_log_file_path = restore_logDir+"/"+err_log_file;
			
		}
		mv.addObject("restore_logDir", restore_logDir);
		mv.addObject("log_file_path", log_file_path);
		mv.addObject("err_log_file_path", err_log_file_path);
		
		schParamMap.put("restore_logDir", restore_logDir);
		schParamMap.put("log_file_path", log_file_path);
		schParamMap.put("err_log_file_path", err_log_file_path);
		schParamMap.put("restore_tmp", restore_tmp);
    	
		//복구설정 조회
		Map<String,Object> frsmap = setupService.findRsrSet(commandMap.getMap());
		String msq_clt_utl_pth = "";
		String restore_binpath = "";
		String xtr_bny_log_pth = "";
		String restore_user = "";
		String restore_pass = "";
		String restore_host = "";
		String restore_port = "";
		String restore_stop_opt = "";
		String restore_os_user = "";
		String restore_os_pwd = "";
		String restore_db_start_name = "";
		String restore_instance_no = "";
		String restore_tmpdir = "";
		String restore_decompress_yn = "";
		String restore_default_file = "";
		if (frsmap != null) {
			if (frsmap.get("rsr_dat_dir") != null) {
				restore_dir = frsmap.get("rsr_dat_dir").toString();
			}
			if (frsmap.get("msq_clt_utl_pth") != null) {
				msq_clt_utl_pth = frsmap.get("msq_clt_utl_pth").toString();
			}
			if (frsmap.get("rmt_msq_bny_pth") != null) {
				restore_binpath = frsmap.get("rmt_msq_bny_pth").toString();
			}
			if (frsmap.get("xtr_bny_log_pth") != null) {
				xtr_bny_log_pth = frsmap.get("xtr_bny_log_pth").toString();
			}
			if (frsmap.get("db_id") != null) {
				restore_user = frsmap.get("db_id").toString();
			}
			if (frsmap.get("db_pwd") != null) {
				restore_pass = frsmap.get("db_pwd").toString();
			}
			if (frsmap.get("hst_ip") != null) {
				restore_host = frsmap.get("hst_ip").toString();
			}
			if (frsmap.get("pot_num") != null) {
				restore_port = frsmap.get("pot_num").toString();
			}
			if (frsmap.get("sto_yon") != null) {
				restore_stop_opt = frsmap.get("sto_yon").toString();
			}
			if (frsmap.get("os_id") != null) {
				restore_os_user = frsmap.get("os_id").toString();
			}
			if (frsmap.get("os_pwd") != null) {
				restore_os_pwd = frsmap.get("os_pwd").toString();
			}
			if (frsmap.get("svc_sta_nam") != null) {
				restore_db_start_name = frsmap.get("svc_sta_nam").toString();
			}
			if (frsmap.get("tmp_dir") != null) {
				restore_tmpdir = frsmap.get("tmp_dir").toString();
			}
			if (frsmap.get("cpr") != null) {
				restore_decompress_yn = frsmap.get("cpr").toString();
			}
			if (frsmap.get("ins_num") != null) {
				restore_instance_no = frsmap.get("ins_num").toString();
			}
			if (frsmap.get("rsr_dft_fil") != null) {
				restore_default_file = frsmap.get("rsr_dft_fil").toString();
			}
		}
		schParamMap.put("msq_clt_utl_pth", msq_clt_utl_pth);
		schParamMap.put("restore_binpath", restore_binpath);
		schParamMap.put("xtr_bny_log_pth", xtr_bny_log_pth);
		schParamMap.put("restore_user", restore_user);
		schParamMap.put("restore_pass", restore_pass);
		schParamMap.put("restore_host", restore_host);
		schParamMap.put("restore_port", restore_port);
		schParamMap.put("restore_stop_opt", restore_stop_opt);
		schParamMap.put("restore_os_user", restore_os_user);
		schParamMap.put("restore_os_pwd", restore_os_pwd);
		schParamMap.put("restore_db_start_name", restore_db_start_name);
		schParamMap.put("restore_tmpdir", restore_tmpdir);
		schParamMap.put("restore_decompress_yn", restore_decompress_yn);
		schParamMap.put("restore_default_file", restore_default_file);
		mv.addObject("cpr", restore_decompress_yn);
		mv.addObject("os_usr", restore_os_user);
		mv.addObject("os_pas", restore_os_pwd);
		if (restore_instance_no != null && !"".equals(restore_instance_no)) {
			schParamMap.put("restore_instance_no", restore_instance_no);
		}
		
		//Map<String,Object> dbchkMap = new HashMap<String,Object>();
		String dbchkCmd = "";
		if (!"".equals(restore_user) 
				&& !"".equals(restore_pass) 
				&& !"".equals(restore_host) 
				&& !"".equals(restore_port)) {
			dbchkCmd = "if [ `"+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -BNse\"select 1;\"` == '1' ];then echo ok; else echo no; fi";
		}
		schParamMap.put("dbchkCmd", dbchkCmd);
		
		/*
		//OS 버전 확인
		Map<String,Object> osverMap = new HashMap<String,Object>();
		String osver = "ssh "+restore_os_user+"@"+restore_host+" \"cat /etc/redhat-release\"";
		osverMap = ssh2Service.getData(osver, ms_usr, ms_pwd, ms_ip, ms_port);
		String str = "";
		if (osverMap != null) {
			String r = osverMap.get("value").toString();
			log.debug("OS 버전 확인["+r+"]");
			int idx1 = r.indexOf("release");
			str = r.substring(idx1+8, idx1+9);
			schParamMap.put("os_version", str);
		}
		*/
		
		//데이터 디렉토리 정보 확인
//		String rsrDirCmd = msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -BNse\"show global variables like '%datadir%';\" | awk '{print $2}'";
//		Map<String,Object> rsrDirMap = new HashMap<String,Object>();
//		System.out.println("rsrDirCmd["+rsrDirCmd+"]");
//		rsrDirMap = ssh2Service.getData(rsrDirCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
//		System.out.println("rsrDirMap["+rsrDirMap+"]");
//		if (rsrDirMap.get("value") != null) {
//			restore_dir = rsrDirMap.get("value").toString();
//		}
//		mv.addObject("restore_dir", restore_dir);
//		schParamMap.put("restore_dir", restore_dir);
		mv.addObject("restore_dir", restore_dir);
		schParamMap.put("restore_dir", restore_dir);
		//String dataDirCmd = "ssh "+restore_os_user+"@"+restore_host+" \""+restore_binpath+"/my_print_defaults -c "+restore_default_file+" mysqld | grep datadir\" | awk -F \"=\" '{print $2}'";
		
		//마지막 풀백업 정보
		String lst_full_dir = "";
		String lst_lvl = "";
		String lst_full_wrk_dt = "";
		String BIN = "";
		String BIN_POS = "";
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("knd", "G01");
		paramMap.put("lvl", "L01");
		paramMap.put("suc_yon", "S01");
		paramMap.put("bms_id", commandMap.get("bms_id"));
		paramMap.put("bts_id", commandMap.get("bts_id"));
		Map<String,Object> fbrl = setupService.findBakRunLst(paramMap);
		if (fbrl != null) {
			lst_lvl = fbrl.get("lvl").toString();
			lst_full_wrk_dt = fbrl.get("wrk_dt").toString();
			lst_full_dir = backup_pdir+"/"+lst_full_wrk_dt;
		}
		
		//2018.05.02
		if ("M01".equals(mtd)) {
			BIN = "BIN=`head -1 "+lst_full_dir+"/xtrabackup_binlog_info | awk '{print $1}'`";
			BIN_POS = "BIN_POS=`head -1 "+lst_full_dir+"/xtrabackup_binlog_info  | awk '{print $2}'`";
		} else if ("M02".equals(mtd)) {
			BIN = "BIN=`head -1 "+lst_full_dir+"/next_bin.log | awk '{print $1}'`";
		}
		schParamMap.put("BIN", BIN);
		schParamMap.put("BIN_POS", BIN_POS);
		
		schParamMap.put("lst_full_dir", lst_full_dir);
		schParamMap.put("lst_full_wrk_dt", lst_full_wrk_dt);
//    	System.out.println("=========schParamMap===========\n");
//    	System.out.println(schParamMap);
//    	System.out.println("=========/schParamMap==========");
		
		boolean connection_fail = true;
		/* 4. 복구 실행 */
//		if (dbchkMap.get("value") != null) {
//			if ("ok".equals(dbchkMap.get("value").toString())) {

				//* =============================================================================
				//* schedule start
				//* =============================================================================
				schedulerHand = BackupScheduler.getInstance();
				if (schedulerHand.isScheduleRegJob(Constants.JOB_RESTORE_EXCUTE+"::"+bms_nam+bts_nam + "::" + wrk_dt,Constants.GROUP_RESTORE+"::"+bms_nam+bts_nam)) {
					schedulerHand.deleteJob(Constants.JOB_RESTORE_EXCUTE+"::"+bms_nam+bts_nam + "::" + wrk_dt,Constants.GROUP_RESTORE+"::"+bms_nam+bts_nam);
				}
				schedulerHand.startScheduler(schParamMap);
				schedulerHand.regiSchedulerRestore();
				//* =============================================================================
				//* schedule end
				//* =============================================================================
				
//			}
//			else {
//				connection_fail = false;
//				//throw new Exception("DB 접속 실패!!!!!!!!!!!");
//			}
//		}
		mv.addObject("connection_fail", connection_fail);
    	
    	return mv;
    }
    
    @RequestMapping(value="/rsr_run_endChk")
	public ModelAndView rsr_run_endChk(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	String restore_dir = commandMap.get("restore_dir").toString();
    	String lvl = commandMap.get("lvl").toString();
		String mtd = commandMap.get("mtd").toString();
		String wrk_dt = commandMap.get("wrk_dt").toString();
		String bms_nam = commandMap.get("bms_nam").toString();
		String bts_nam = commandMap.get("bts_nam").toString();
		String hst_ip = "";
		if (commandMap.get("hst_ip") != null) {
			hst_ip = commandMap.get("hst_ip").toString();
		}
		String kep_pod = commandMap.get("kep_pod").toString();
		String typ = commandMap.get("typ").toString();
		String run_id = commandMap.get("run_id").toString();
		String log_file_path = commandMap.get("log_file_path").toString();
		String err_log_file_path = commandMap.get("err_log_file_path").toString();
    	
		String ms_usr = commandMap.get("ms_usr").toString();
		String ms_pwd = commandMap.get("ms_pwd").toString();
		String ms_ip = commandMap.get("ms_ip").toString();
		int ms_port = Integer.parseInt(commandMap.get("ms_port").toString());
		
		Map<String,Object> completeMap = new HashMap<String,Object>();
		String completeCmd = "if [ \"`cat "+log_file_path+" | grep RESTORE\\ END`\" != '' ]; then echo ok; else echo no; fi";
		completeMap = ssh2Service.getData(completeCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
		if (completeMap.get("value") != null) {
			mv.addObject("complete", completeMap.get("value"));
			
			String completeResult = completeMap.get("value").toString();
			
			if ("ok".equals(completeResult)) {
				schedulerHand = BackupScheduler.getInstance();
				if (!schedulerHand.isScheduleRegJob(Constants.JOB_RESTORE_EXCUTE+"::"+bms_nam+bts_nam + "::" + wrk_dt,Constants.GROUP_RESTORE+"::"+bms_nam+bts_nam)) {
					//DB 저장
					Map<String,Object> ginianLogMap = new HashMap<String,Object>();
					String ginianLogCmd = "cat "+log_file_path;
					ginianLogMap = ssh2Service.getList(ginianLogCmd, ms_usr, ms_pwd, ms_ip, ms_port);
					if (ginianLogMap != null) {
						List<String> list = new ArrayList<String>();
						list = (List) ginianLogMap.get("list");
						if (list != null && list.size() > 0) {
							StringBuilder strb = new StringBuilder();
							boolean bakRlt = false;
							String success_result = "Result : SUCCESS";
							String pas_tme = "";
							for (int i=0; i<list.size(); i++) {
								String list_msg = list.get(i).toString();
								if (strb.length() > 0) strb.append(Constants.DATA_NEW_LINE);
								if (success_result.equals(list_msg)) {
									bakRlt = true;
								}
								if (list.get(i).matches("(.*)ELAPSED TIME :(.*)")) {
									pas_tme = list.get(i).replace("ELAPSED TIME :", "").trim();
								}
								strb.append(list.get(i));
							}
							Map<String,Object> paramMap = new HashMap<String,Object>();
							paramMap.put("knd", "G02");
							paramMap.put("wrk_dt", wrk_dt);
							paramMap.put("bms_id", commandMap.get("bms_id"));
							paramMap.put("bts_id", commandMap.get("bts_id"));
							paramMap.put("suc_yon", bakRlt == true ? "S01" : "S02");
							paramMap.put("typ", typ);
							paramMap.put("lvl", lvl);
							paramMap.put("mtd", mtd);
							paramMap.put("pas_tme", pas_tme);
							paramMap.put("kep_pod", kep_pod);
							paramMap.put("log", strb.toString());
							paramMap.put("run_id", run_id);
							paramMap.put("hst_ip", hst_ip);
							boolean result = setupService.insertLog(paramMap, request);
							mv.addObject("complete_msg", bakRlt == true ? "복구가 완료되었습니다." : "복구가 실패되었습니다!!!");
						}
					}
				}
			}
		}
    	return mv;
    }
    
    @RequestMapping(value="/rsr_run_status")
    public ModelAndView rsr_run_status(CommandMap commandMap, HttpServletRequest request) throws Exception {
    	
    	ModelAndView mv = new ModelAndView("jsonView");
    	
    	String backup_dir = commandMap.get("backup_dir").toString();
    	String backup_pdir = commandMap.get("backup_pdir").toString();
    	String lvl = commandMap.get("lvl").toString();
    	String mtd = commandMap.get("mtd").toString();
    	String wrk_dt = commandMap.get("wrk_dt").toString();
    	String bts_nam = commandMap.get("bts_nam").toString();
    	String kep_pod = commandMap.get("kep_pod").toString();
    	
		String ms_usr = commandMap.get("ms_usr").toString();
		String ms_pwd = commandMap.get("ms_pwd").toString();
		String ms_ip = commandMap.get("ms_ip").toString();
		int ms_port = Integer.parseInt(commandMap.get("ms_port").toString());
    	
    	//복구설정 조회
		Map<String,Object> frsmap = setupService.findRsrSet(commandMap.getMap());
		String msq_clt_utl_pth = "";
//		String rmt_msq_bny_pth = "";
		String rsr_dft_fil = "";
		String restore_user = "";
		String restore_pass = "";
		String restore_host = "";
		String restore_port = "";
		String restore_stop_opt = "";
		String restore_os_user = "";
		String restore_os_pwd = "";
		String source_dir = "";
		if (frsmap != null) {
			if (frsmap.get("msq_clt_utl_pth") != null) {
				msq_clt_utl_pth = frsmap.get("msq_clt_utl_pth").toString();
			}
//			if (frsmap.get("rmt_msq_bny_pth") != null) {
//				rmt_msq_bny_pth = frsmap.get("rmt_msq_bny_pth").toString();
//			}
			if (frsmap.get("rsr_dft_fil") != null) {
				rsr_dft_fil = frsmap.get("rsr_dft_fil").toString();
			}
			if (frsmap.get("db_id") != null) {
				restore_user = frsmap.get("db_id").toString();
			}
			if (frsmap.get("db_pwd") != null) {
				restore_pass = frsmap.get("db_pwd").toString();
			}
			if (frsmap.get("hst_ip") != null) {
				restore_host = frsmap.get("hst_ip").toString();
			}
			if (frsmap.get("pot_num") != null) {
				restore_port = frsmap.get("pot_num").toString();
			}
			if (frsmap.get("sto_yon") != null) {
				restore_stop_opt = frsmap.get("sto_yon").toString();
			}
			if (frsmap.get("os_id") != null) {
				restore_os_user = frsmap.get("os_id").toString();
			}
			if (frsmap.get("os_pwd") != null) {
				restore_os_pwd = frsmap.get("os_pwd").toString();
			}
			if (frsmap.get("rsr_dat_dir") != null) {
				source_dir = frsmap.get("rsr_dat_dir").toString();
			}
		}
    	
    	if ("L01".equals(lvl) && "M01".equals(mtd)) {
    		String targetDirCmd = "du "+backup_dir+" | tail -1 | awk '{print $1}'";
    		Map<String,Object> targetDirMap = new HashMap<String,Object>();
    		targetDirMap = ssh2Service.getData(targetDirCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
    		String targetDir = "0";
    		if (targetDirMap != null) {
    			if (targetDirMap.get("value") != null) {
    				targetDir = targetDirMap.get("value").toString();
    			}
    		}
    		
//    		String source_dir = "";
//    		String sourceDir = "0";
//    		String sourceDirCmd = "sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \""+msq_clt_utl_pth+"/my_print_defaults -c "+rsr_dft_fil+" mysqld | grep datadir\"";
//    		sourceDirCmd += " | awk -F \"=\" '{print $2}'";
//    		Map<String,Object> sourceDirMap = new HashMap<String,Object>();
//    		sourceDirMap = ssh2Service.getData(sourceDirCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
//    		if (sourceDirMap != null) {
//    			if (sourceDirMap.get("value") != null) {
//    				source_dir = sourceDirMap.get("value").toString();
//    				String svCmd = "sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" du "+source_dir+" | tail -1 | awk '{print $1}'";
//    				Map<String,Object> svMap = new HashMap<String,Object>();
//    				svMap = ssh2Service.getData(svCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
//    				if (svMap != null) {
//    					if (svMap.get("value") != null) {
//    						sourceDir = svMap.get("value").toString();
//    						if (!StringUtil.isNumber(sourceDir)) {
//    							sourceDir = "0";
//    						}
//    					}
//    				}
//    			}
//    		}
    		String sourceDir = "0";
//			String svCmd = "sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"du "+source_dir+" | tail -1 | awk '{print $1}'\"";
			String svCmd = "sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" du "+source_dir+" | tail -1 | awk '{print $1}'";
			Map<String,Object> svMap = new HashMap<String,Object>();
			svMap = ssh2Service.getData(svCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
			if (svMap != null) {
				if (svMap.get("value") != null) {
					sourceDir = svMap.get("value").toString();
					if (!StringUtil.isNumber(sourceDir)) {
						sourceDir = "0";
					}
				}
			}
    		
    		String processVal = "0";
    		if (StringUtil.isNumber(sourceDir) && StringUtil.isNumber(targetDir)) {
    			if (!"0".equals(sourceDir) && !"0".equals(targetDir)) {
    				double target = Double.parseDouble(targetDir);
    				double source = Double.parseDouble(sourceDir);
    				double processVald = Math.ceil(Double.parseDouble(String.valueOf((source/target)*100)));
    				processVal = (String.format("%.0f" , processVald));
    				if (!StringUtil.isNumber(processVal)) {
    					processVal = "0";
    				}
    			}
    		}
    		mv.addObject("processVal", processVal);
    	}
    	
    	String cpuUseCmd = "LANG=C; sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" LANG=C;echo \"100 - `sar -P ALL 1 1 | grep all | head -1 | awk '{print $8}'`\" | bc -l";
    	Map<String,Object> cpuUseMap = new HashMap<String,Object>();
    	cpuUseMap = ssh2Service.getData(cpuUseCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
    	String cpuUseVal = "0";
    	if (cpuUseMap != null) {
    		if (cpuUseMap.get("value") != null) {
    			cpuUseVal = cpuUseMap.get("value").toString();
    			if (!StringUtil.isNumber(cpuUseVal)) {
    				cpuUseVal = "0";
    			}
    		}
    	}
    	mv.addObject("cpuUseVal", cpuUseVal);
    	
    	String memUseCmd = "LANG=C; sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" LANG=C;sar -r 1 1 | tail -2 | head -1 | awk '{print $8}'";
    	Map<String,Object> memUseMap = new HashMap<String,Object>();
    	memUseMap = ssh2Service.getData(memUseCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
    	String memUseVal = "0";
    	if (memUseMap != null) {
    		if (memUseMap.get("value") != null) {
    			memUseVal = memUseMap.get("value").toString();
    			if (!StringUtil.isNumber(memUseVal)) {
    				memUseVal = "0";
    			}
    		}
    	}
    	mv.addObject("memUseVal", memUseVal);
    	
    	String diskUseCmd = MessageFormat.format(sshXXIII1, backup_pdir);
    	Map<String,Object> diskUseMap = new HashMap<String,Object>();
    	diskUseMap = ssh2Service.getData(diskUseCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
    	String diskUseVal = "0";
    	if (diskUseMap != null) {
    		if (diskUseMap.get("value") != null) {
    			diskUseVal = diskUseMap.get("value").toString().replaceAll("%", "");
    			if (!StringUtil.isNumber(diskUseVal)) {
    				diskUseVal = "0";
    			}
    		}
    	}
    	mv.addObject("diskUseVal", diskUseVal);
    	
    	String swapCmd = "SWAP=`sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"free | grep -i swap\"`;echo \"scale=2;`echo $SWAP | awk '{print $3}'` / `echo $SWAP | awk '{print $2}'` * 100\" | bc -l";
    	Map<String,Object> swapMap = new HashMap<String,Object>();
    	swapMap = ssh2Service.getData(swapCmd, ms_usr, ms_pwd, ms_ip, ms_port, 5);
    	String swapVal = "0";
    	if (swapMap != null) {
    		if (swapMap.get("value") != null) {
    			swapVal = swapMap.get("value").toString();
    			if (StringUtil.isNumber(swapVal)) {
    				swapVal = (String.format("%.0f" , Double.parseDouble(swapVal)));
    			}
    		}
    	}
    	mv.addObject("swapVal", swapVal);
    	
    	return mv;
    }
    
    @RequestMapping(value="/rsr_run_log")
	public ModelAndView rsr_run_log(CommandMap commandMap, HttpServletRequest request) throws Exception {

    	ModelAndView mv = new ModelAndView("jsonView");
    	
		Map<String,Object> logmap = setupService.findLogDetailMap(commandMap.getMap());
		StringBuilder resmap = new StringBuilder();
		if (logmap != null) {
			StringBuilder line = new StringBuilder();
			line.append(logmap.get("log"));
			if (line.toString().split("\n").length > 5000) {
				String[] lines = line.toString().split("\n");
				for (int i=0; i<5000; i++) {
					resmap.append(lines[i]);
					if (i != 4999) {
						resmap.append("\n");
					} else {
						resmap.append("\n");
						resmap.append("................................................... the last part omitted");
					}
				}
			} else {
				resmap.append(logmap.get("log"));
			}
			mv.addObject("logmap", resmap);
		}
    	return mv;
    }
    
    
    
    @RequestMapping(value="/framePage1")
	public ModelAndView framePage1() throws Exception {
    	
		ModelAndView mv = new ModelAndView("/setup/include/loading");
		
		return mv;
	}
    
    
    
    public boolean updateMenuCd(String menu_cd, HttpServletRequest request) throws Exception {
    	String usr_id = loginManager.getUserID(request.getSession());
    	Map<String,Object> param = new HashMap<String,Object>();
    	param.put("lst_pag", menu_cd);
    	param.put("usr_id", usr_id);
    	return summaryService.updateMenuCd(param);
    }
    
    public boolean updateMenuCd(Map<String,Object> map, HttpServletRequest request) throws Exception {
    	String usr_id = loginManager.getUserID(request.getSession());
    	Map<String,Object> param = new HashMap<String,Object>();
    	param.put("lst_pag", map.get("menu_cd"));
    	param.put("lst_bms_id", map.get("lst_bms_id"));
    	param.put("lst_bts_id", map.get("lst_bts_id"));
    	param.put("usr_id", usr_id);
    	return summaryService.updateMenuCd(param);
    }
    

}
