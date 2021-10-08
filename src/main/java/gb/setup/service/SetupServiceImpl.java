package gb.setup.service;

import gb.common.service.Ssh2Service;
import gb.common.util.StringUtil;
import gb.manage.service.ManageService;
import gb.setup.dao.SetupDAO;
import gb.summary.dao.SummaryDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service("setupService")
public class SetupServiceImpl implements SetupService {

	@Resource(name="setupDAO")
    private SetupDAO setupDAO;
	
	@Resource(name="summaryDAO")
	private SummaryDAO summaryDAO;
	
	@Resource(name = "ssh2Service")
	private Ssh2Service ssh2Service;

	@Resource(name = "manageService")
	private ManageService manageService;
	
	@Override
	public List<Map<String, Object>> findTree(Map<String, Object> map) throws Exception {
		return setupDAO.findTree(map);
	}

	@Override
	public Map<String, Object> findMngSvr(Map<String, Object> map) throws Exception {
		return setupDAO.findMngSvr(map);
	}

	@Override
	public Map<String, Object> findTgtSvr(Map<String, Object> map) throws Exception {
		return setupDAO.findTgtSvr(map);
	}

	@Override
	public Map<String, Object> findTgtSvrMaxId(Map<String, Object> map) throws Exception {
		return setupDAO.findTgtSvrMaxId(map);
	}

	@Override
	public boolean updateMngSvr(Map<String, Object> map, HttpServletRequest request) throws Exception {
		boolean result = false;
		
		if (map.get("bms_id") != null) {
			
			result = setupDAO.updateMngSvr(map);
			
			map.put("bmt_id", map.get("bms_id"));
			result = setupDAO.updateTree(map);
			
			if (result) {
				map.put("hst_ip", map.get("bms_ip"));
				result = setupDAO.updateBakSetIP(map);
				
				map.put("bts_ip", map.get("bms_ip"));
				map.put("bts_pid", map.get("bms_id"));
				result = setupDAO.updateTgtSvrIP(map);
			}
			
		} else {
			
			Map<String,Object> bmsmimap = setupDAO.findMngSvrMaxId(map);
			map.put("bms_id", bmsmimap.get("maxid"));
			map.put("bms_pid", bmsmimap.get("maxpid"));
			
			result = setupDAO.insertMngSvr(map);
			
			map.put("bmt_id", bmsmimap.get("maxid"));
			map.put("bmt_pid", bmsmimap.get("maxpid"));
			result = setupDAO.insertTree(map);
			
		}
		if (!result) throw new Exception("server error!!!");
		
		return result;
	}

	@Override
	public boolean updateTgtSvr(Map<String, Object> map, HttpServletRequest request) throws Exception {
		
		boolean result = false;
		
		String bms_id = map.get("bms_id").toString();
		if (map.get("bts_id") != null) {
			
			String bts_id = map.get("bts_id").toString();
			map.put("bts_id", bts_id);
			result = setupDAO.updateTgtSvr(map);
			
			map.put("bmt_id", bts_id);
			result = setupDAO.updateTree(map);
			
			if (result) {
				map.put("pot_num",  map.get("bts_pot"));
				setupDAO.updateBakSetTgt(map);
			}
	    	
		} else {
			Map<String, Object> param = new HashMap<String, Object>();
	    	param.put("bms_id_mix", Integer.valueOf(bms_id+"000"));
	    	param.put("bts_pid", bms_id);
	    	param.put("bmt_id", map.get("bmt_id"));
	    	Map<String,Object> btsmimap = setupDAO.findTgtSvrMaxId(param);
	    	
	    	map.put("bts_id", btsmimap.get("maxid"));
	    	map.put("bts_pid", bms_id);
	    	result = setupDAO.insertTgtSvr(map);
			
	    	map.put("bmt_id", btsmimap.get("maxid"));
	    	map.put("bmt_pid", bms_id);
	    	result = setupDAO.insertTree(map);
		}
		
//		if (!result) throw new Exception("server error!!!");
    	
		return result;
	}

	@Override
	public String deleteMngSvr(Map<String, Object> map, HttpServletRequest request) throws Exception {
		
		boolean result = false;
		
		//기존 CronName 가져오기
		Map<String, Object> item = new HashMap<String,Object>();
		item.put("bms_id", map.get("bms_id"));
		//기존 클론네임
		List<Map<String, Object>> cronNameMap = findBakScdNamList(item);
		
		result = setupDAO.deleteMngSvr(map);
		result = setupDAO.deleteTree(map);
		List<Map<String,Object>> list = setupDAO.findTgtSvrList(map);
		if (list != null && list.size() > 0) {
			result = setupDAO.deleteTgtSvrPid(map);
			result = setupDAO.deleteTreePid(map);
			result = summaryDAO.deleteStt(map);
			result = setupDAO.deleteBakScd(map);
			
			//2018.01.03 설정 삭제
			Map<String, Object> delparam = new HashMap<String,Object>();
			delparam.put("bms_id", map.get("bms_id"));
			result = setupDAO.deleteBakSet(delparam);
			result = setupDAO.deleteBakMtd(delparam);
			result = setupDAO.deleteRsrSet(delparam);
			
			//log delete
			result = setupDAO.deleteLogLv1(delparam);
			
			//backup run delete
			result = setupDAO.deleteBakRunLv1(delparam);
			
			String bms_nam = map.get("bms_nam").toString();
			
			//접속정보 조회
        	Map<String,Object> param = new HashMap<String,Object>();
        	param.put("ms_id", map.get("bms_id").toString().charAt(0));
        	
        	//was os 접속정보
        	Map<String,Object> msmap = manageService.findSvrMap(param);
    		String ms_ip = "";
    		int ms_port = 0;
    		String ms_usr = "";
    		String ms_pwd = "";
    		String ms_sve_dir = "";
    		if (msmap != null) {
    			ms_ip = msmap.get("ms_ip").toString();
    			ms_port = Integer.valueOf(msmap.get("ms_port").toString());
    			ms_usr = msmap.get("ms_usr").toString();
    			ms_pwd = msmap.get("ms_pwd").toString();
    			ms_sve_dir = msmap.get("ms_sve_dir").toString();
    			
    			if(cronNameMap.size() != 0 && cronNameMap != null){
    				
    				//crontab 삭제
    				for (int i = 0; i < cronNameMap.size(); i++) {
    					Map<String, Object> items = cronNameMap.get(i);
    					String oldCronName = items.get("cron_nam").toString();
    					
    					String cmd = "crontab -l > cron_maria\n"+
    							"perl -pi -e " + "\"s/.*" + oldCronName + ".*//s\"" + " cron_maria\n"+
    							"crontab -i cron_maria\n";
    					
    					Map<String,Object> cronRegMap = new HashMap<String,Object>();
    					cronRegMap = ssh2Service.getData(cmd, ms_usr, ms_pwd, ms_ip, ms_port);
    					
    				}
    			}
    			
    			//2018.01.03 백업디렉토리 삭제
    			String dirDelCmd = "rm -rf "+ms_sve_dir+"/"+bms_nam;
    			Map<String,Object> dirDelMap = new HashMap<String,Object>();
    			dirDelMap = ssh2Service.getData(dirDelCmd, ms_usr, ms_pwd, ms_ip, ms_port);
    			
    		}
			
		}
		
//		if (!result) throw new Exception("server error!!!");
		
		return "success";
	}

	@Override
	public String deleteTgtSvr(Map<String, Object> map, HttpServletRequest request) throws Exception {
		
		boolean result = false;
		
		//기존 CronName 가져오기
		Map<String, Object> item = new HashMap<String,Object>();
		item.put("bts_id", map.get("bts_id"));
		List<Map<String, Object>> cronNameMap = findMngbtsId(item);
		
		result = setupDAO.deleteTgtSvr(map);
		result = setupDAO.deleteTree(map);
		result = summaryDAO.deleteStt(map);
		result = setupDAO.deleteBakScd(map);
		
		//2018.01.03 설정 삭제
		Map<String, Object> delparam = new HashMap<String,Object>();
		delparam.put("bms_id", map.get("bms_id"));
		delparam.put("bts_id", map.get("bts_id"));
		result = setupDAO.deleteBakSet(delparam);
		result = setupDAO.deleteBakMtd(delparam);
		result = setupDAO.deleteRsrSet(delparam);
		
		//log delete
		result = setupDAO.deleteLogLv2(delparam);
		
		//backup run delete
		result = setupDAO.deleteBakRunLv2(delparam);
		
		String bms_nam = map.get("bms_nam").toString();
		String bts_nam = map.get("bts_nam").toString();
		
		
		//접속정보 조회
    	Map<String,Object> param = new HashMap<String,Object>();
    	param.put("ms_id", map.get("bts_id").toString().charAt(0));
    	
    	//was os 접속정보
    	Map<String,Object> msmap = manageService.findSvrMap(param);
		String ms_ip = "";
		int ms_port = 0;
		String ms_usr = "";
		String ms_pwd = "";
		String ms_sve_dir = "";
		if (msmap != null) {
			ms_ip = msmap.get("ms_ip").toString();
			ms_port = Integer.valueOf(msmap.get("ms_port").toString());
			ms_usr = msmap.get("ms_usr").toString();
			ms_pwd = msmap.get("ms_pwd").toString();
			ms_sve_dir = msmap.get("ms_sve_dir").toString();
			
			if(cronNameMap.size() != 0 && cronNameMap != null){
				
				//crontab 삭제
				for (int i = 0; i < cronNameMap.size(); i++) {
					Map<String, Object> items = cronNameMap.get(i);
					String oldCronName = items.get("cron_nam").toString();
					
					String cmd = "crontab -l > cron_maria\n"+
							"perl -pi -e " + "\"s/.*" + oldCronName + ".*//s\"" + " cron_maria\n"+
							"crontab -i cron_maria\n";
					
					Map<String,Object> cronRegMap = new HashMap<String,Object>();
					cronRegMap = ssh2Service.getData(cmd, ms_usr, ms_pwd, ms_ip, ms_port);
				}
			}
			
			//2018.01.03 백업디렉토리 삭제
			String dirDelCmd = "rm -rf "+ms_sve_dir+"/"+bms_nam+"/"+bts_nam;
			Map<String,Object> dirDelMap = new HashMap<String,Object>();
			dirDelMap = ssh2Service.getData(dirDelCmd, ms_usr, ms_pwd, ms_ip, ms_port);
			
		}
		
		return "success";
	}
	
	@Override
	public List<Map<String, Object>> findIsLog(Map<String, Object> map) throws Exception {
		return setupDAO.findIsLog(map);
	}
	
	@Override
	public List<Map<String, Object>> findLogMM(Map<String, Object> map) throws Exception {
		return setupDAO.findLogMM(map);
	}

	@Override
	public Map<String, Object> findLogDetail(Map<String, Object> map) throws Exception {
		return setupDAO.findLogDetail(map);
	}

	@Override
	public Map<String, Object> findMngSvrNam(Map<String, Object> map) throws Exception {
		return setupDAO.findMngSvrNam(map);
	}
	
	@Override
	public Map<String, Object> findTgtSvrNam(Map<String, Object> map) throws Exception {
		return setupDAO.findTgtSvrNam(map);
	}
	
	@Override
	public Map<String, Object> findBakSet(Map<String, Object> map) throws Exception {
		return setupDAO.findBakSet(map);
	}
	
	@Override
	public List<Map<String, Object>> findType(Map<String, Object> map) throws Exception {
		return setupDAO.findType(map);
	}

	@Override
	public boolean insertBakSet(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return setupDAO.insertBakSet(map);
	}
	
	@Override
	public Map<String, Object> findBakMtd(Map<String, Object> map) throws Exception {
		return setupDAO.findBakMtd(map);
	}
	
	@Override
	public boolean insertBakMtd(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return setupDAO.insertBakMtd(map);
	}
	
	@Override
	public Map<String, Object> findBakScd(Map<String, Object> map) throws Exception {
		return setupDAO.findBakScd(map);
	}
	
	@Override
	public List<Map<String, Object>> findBakScdNam(Map<String, Object> map) throws Exception {
		return setupDAO.findBakScdNam(map);
	}

	@Override
	public List<Map<String, Object>> findBakScdNamList(Map<String, Object> map) throws Exception {
		return setupDAO.findBakScdNamList(map);
	}
	
	@Override
	public List<Map<String, Object>> findMngbtsId(Map<String, Object> map) throws Exception {
		return setupDAO.findMngbtsId(map);
	}
	
	@Override
	public boolean insertBakScd(Map<String, Object> map, HttpServletRequest request) throws Exception {
		boolean result = false;
		result = setupDAO.insertBakScd(map);
		if (result) {
			String cmd = map.get("cmd").toString();
			String ms_usr = map.get("ms_usr").toString();
			String ms_pwd = map.get("ms_pwd").toString();
			String ms_ip = map.get("ms_ip").toString();
			int ms_port = Integer.valueOf(map.get("ms_port").toString());
	    	Map<String,Object> cronRegMap = new HashMap<String,Object>();
	    	cronRegMap = ssh2Service.getData(cmd, ms_usr, ms_pwd, ms_ip, ms_port);
    	}
		return result;
	}
	
	@Override
	public boolean updateBakScd(Map<String, Object> map, HttpServletRequest request) throws Exception {
		boolean result = false;
		result = setupDAO.updateBakScd(map);
		if (result) {
			String cmd = map.get("cmd").toString();
			String ms_usr = map.get("ms_usr").toString();
			String ms_pwd = map.get("ms_pwd").toString();
			String ms_ip = map.get("ms_ip").toString();
			int ms_port = Integer.valueOf(map.get("ms_port").toString());
	    	Map<String,Object> cronRegMap = new HashMap<String,Object>();
	    	cronRegMap = ssh2Service.getData(cmd, ms_usr, ms_pwd, ms_ip, ms_port);
    	}
		return result;
	}
	
	@Override
	public boolean deleteBakScd(Map<String, Object> map, HttpServletRequest request) throws Exception {
		boolean result = false;
		result = setupDAO.deleteBakScd(map);
		if (result) {
			String cmd = map.get("cmd").toString();
			String ms_usr = map.get("ms_usr").toString();
			String ms_pwd = map.get("ms_pwd").toString();
			String ms_ip = map.get("ms_ip").toString();
			int ms_port = Integer.valueOf(map.get("ms_port").toString());
        	Map<String,Object> cronRegMap = new HashMap<String,Object>();
        	cronRegMap = ssh2Service.getData(cmd, ms_usr, ms_pwd, ms_ip, ms_port);
    	}
		return result;
	}
	
	@Override
	public Map<String, Object> findLogLst(Map<String, Object> map) throws Exception {
		return setupDAO.findLogLst(map);
	}
	
	@Override
	public Map<String, Object> findRsrSet(Map<String, Object> map) throws Exception {
		return setupDAO.findRsrSet(map);
	}
	
	@Override
	public boolean insertRsrSet(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return setupDAO.insertRsrSet(map);
	}
	
	@Override
	public boolean insertBakRun(Map<String, Object> map) throws Exception {
		boolean result = false;
		String lvl = map.get("lvl").toString();
		String mtd = map.get("mtd").toString();
		String kep_pod = map.get("kep_pod").toString();
		int run_id = 0, run_pid = 0, run_lvl = 0;
		String wrk_dt = map.get("wrk_dt").toString();
		
		Map<String,Object> maxidMap = new HashMap<String, Object>();
		if ("L01".equals(lvl)) {
			maxidMap = setupDAO.findRunFllMaxId(map);
			run_lvl = 1;
		} else if ("L02".equals(lvl)) {
			run_lvl = 2;
			Map<String,Object> lstLogMap = setupDAO.findLstLog(map);
			String lstLvl = lstLogMap.get("lvl").toString();
			if ("L01".equals(lstLvl)) {
				maxidMap = setupDAO.findRunIncMaxId1(map);
			} else if ("L02".equals(lstLvl)) {
				maxidMap = setupDAO.findRunIncMaxId2(map);
			}
		}
		if (maxidMap != null) {
			run_id = Integer.valueOf(maxidMap.get("max_run_id").toString());
			run_pid = Integer.valueOf(maxidMap.get("max_run_pid").toString());
		}
		
		//map.put("mtd", mtd);
		
		Map<String,Object> pmap = new HashMap<String, Object>();
		pmap.put("com_cod", mtd);
		Map<String,Object> mtdMap = setupDAO.findTypeMap(pmap);
		if (mtdMap != null) {
			mtd = mtdMap.get("com_cod_nam").toString();
		}
		pmap = new HashMap<String, Object>();
		pmap.put("com_cod", lvl);
		Map<String,Object> lvlMap = setupDAO.findTypeMap(pmap);
		if (lvlMap != null) {
			lvl = lvlMap.get("com_cod_nam").toString();
		}
		
		map.put("run_id", run_id);
		map.put("run_pid", run_pid);
		map.put("run_nam", StringUtil.dateConvert(wrk_dt,true)+" / "+mtd+" / "+lvl+" / "+kep_pod+"일");
		map.put("run_lvl", run_lvl);
		
		result = setupDAO.insertBakRun(map);
		
		//result = insertLog(map,request);
		
		
		return result;
	}
	
	@Override
	public Map<String, Object> findTypeMap(Map<String, Object> map) throws Exception {
		return setupDAO.findTypeMap(map);
	}
	
	@Override
	public boolean insertLog(Map<String, Object> map, HttpServletRequest request) throws Exception {
		
		boolean result = false;
		
		result = setupDAO.insertLog(map);
		result = setupDAO.insertLogDetail(map);
		result = summaryDAO.updateStt(map);
		
		return result;
	}
	
	@Override
	public Map<String, Object> findLogDetailMap(Map<String, Object> map) throws Exception {
		return setupDAO.findLogDetailMap(map);
	}
	
	@Override
	public List<Map<String, Object>> findLogList(Map<String, Object> map) throws Exception {
		return setupDAO.findLogList(map);
	}
	
	@Override
	public List<Map<String, Object>> findBakRun(Map<String, Object> map) throws Exception {
		return setupDAO.findBakRun(map);
	}
	
	@Override
	public Map<String, Object> findBakRunLst(Map<String, Object> map) throws Exception {
		return setupDAO.findBakRunLst(map);
	}
	
	@Override
	public List<Map<String, Object>> findBakScdList() throws Exception {
		return setupDAO.findBakScdList();
	}
	
	@Override
	public Map<String, Object> findLstFullbak(Map<String, Object> map) throws Exception {
		return setupDAO.findLstFullbak(map);
	}
	
	@Override
	public boolean deleteRun(Map<String, Object> map) throws Exception {
		return setupDAO.deleteRun(map);
	}
	
	@Override
	public List<Map<String, Object>> findMngSvrInfo(Map<String, Object> map) throws Exception {
		return setupDAO.findMngSvrInfo(map);
	}
	
	@Override
	public Map<String, Object> findMngSvrNamSea(Map<String, Object> map) throws Exception {
		return setupDAO.findMngSvrNamSea(map);
	}
	
	@Override
	public Map<String, Object> findTgtSvrNamSea(Map<String, Object> map) throws Exception {
		return setupDAO.findTgtSvrNamSea(map);
	}
	
	@Override
	public Map<String, Object> findLogDt(Map<String, Object> map) throws Exception {
		return setupDAO.findLogDt(map);
	}
	
}
