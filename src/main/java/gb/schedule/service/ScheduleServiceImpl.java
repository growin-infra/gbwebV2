package gb.schedule.service;

import gb.common.service.Ssh2Service;
import gb.common.util.Constants;
import gb.common.util.StringUtil;
import gb.schedule.dao.ScheduleDAO;
import gb.setup.dao.SetupDAO;
import gb.setup.service.SetupService;
import gb.summary.dao.SummaryDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service("scheduleService")
public class ScheduleServiceImpl implements ScheduleService {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name="scheduleDAO")
    private ScheduleDAO scheduleDAO;

	@Resource(name="setupDAO")
    private SetupDAO setupDAO;
	
	@Resource(name="setupService")
	private SetupService setupService;
	
	@Resource(name="summaryDAO")
	private SummaryDAO summaryDAO;
	
	@Resource(name = "ssh2Service")
	private Ssh2Service ssh2Service;
	
//	@Override
//	public List<Map<String, Object>> cronSelect() throws Exception {
//		return crontabDAO.cronSelect();
//	}
	
	@Override
	public boolean cronDelete(Map<String, Object> map) throws Exception {
		return scheduleDAO.cronDelete(map);
	}
	
	@Override
	public String cronJob(Map<String, Object> map) throws Exception {
		boolean result = false;
		if ("S01".equals(map.get("suc_yon"))) {
			if ("BT01".equals(map.get("typ"))) {
				result = setupService.insertBakRun(map);
			}
		}
		result = setupDAO.insertLog(map);
		result = setupDAO.insertLogDetail(map);
		result = summaryDAO.updateStt(map);
		result = cronDelete(map);
		return "success";
	}
	
//	public boolean insertBakRun(Map<String, Object> map) throws Exception {
//		boolean result = false;
//		String lvl = map.get("lvl").toString();
//		String mtd = map.get("mtd").toString();
//		String kep_pod = map.get("kep_pod").toString();
//		int run_id = 0, run_pid = 0, run_lvl = 0;
//		String wrk_dt = map.get("wrk_dt").toString();
//		
//		Map<String,Object> maxidMap = new HashMap<String, Object>();
//		if ("L01".equals(lvl)) {
//			maxidMap = setupDAO.findRunFllMaxId(map);
//			run_lvl = 1;
//		} else if ("L02".equals(lvl)) {
//			run_lvl = 2;
//			Map<String,Object> lstLogMap = setupDAO.findLstLog(map);
//			String lstLvl = lstLogMap.get("lvl").toString();
//			if ("L01".equals(lstLvl)) {
//				maxidMap = setupDAO.findRunIncMaxId1(map);
//			} else if ("L02".equals(lstLvl)) {
//				maxidMap = setupDAO.findRunIncMaxId2(map);
//			}
//		}
//		if (maxidMap != null) {
//			run_id = Integer.valueOf(maxidMap.get("max_run_id").toString());
//			run_pid = Integer.valueOf(maxidMap.get("max_run_pid").toString());
//		}
//		
//		Map<String,Object> pmap = new HashMap<String, Object>();
//		pmap.put("com_cod", mtd);
//		Map<String,Object> mtdMap = setupDAO.findTypeMap(pmap);
//		if (mtdMap != null) {
//			mtd = mtdMap.get("com_cod_nam").toString();
//		}
//		pmap = new HashMap<String, Object>();
//		pmap.put("com_cod", lvl);
//		Map<String,Object> lvlMap = setupDAO.findTypeMap(pmap);
//		if (lvlMap != null) {
//			lvl = lvlMap.get("com_cod_nam").toString();
//		}
//		
//		map.put("run_id", run_id);
//		map.put("run_pid", run_pid);
//		map.put("run_nam", StringUtil.dateConvert(wrk_dt,true)+" / "+mtd+" / "+lvl+" / "+kep_pod+"일");
//		map.put("run_lvl", run_lvl);
//		
//		result = setupDAO.insertBakRun(map);
//		
//		return result;
//	}

	@Override
	public List<Map<String, Object>> cronSelect() throws Exception {
		return scheduleDAO.cronSelect();
	}
	
	@Override
	public List<Map<String, Object>> listDelSchTarget() throws Exception {
		return scheduleDAO.listDelSchTarget();
	}
	
	@Override
	public String listSchDtTarget(Map<String,Object> map) throws Exception {
		
		String run_id = map.get("run_id").toString();
		String bms_id = map.get("bms_id").toString();
		String bts_id = map.get("bts_id").toString();
		String ms_ip = map.get("ms_ip").toString();
		String ms_port = map.get("ms_port").toString();
		String ms_usr = map.get("ms_usr").toString();
		String ms_pwd = map.get("ms_pwd").toString();
		String ms_sve_dir = map.get("ms_sve_dir").toString(); 
		String typ = map.get("typ").toString(); 
		
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("bms_id", bms_id);
		param.put("bts_id", bts_id);
		param.put("run_pid", run_id);
		List<Map<String, Object>> tlist = scheduleDAO.listSchPidTarget(param);
		if (tlist != null && tlist.size() > 0) {
			for (int i=0; i<tlist.size(); i++) {
				Map<String,Object> item = tlist.get(i);
				String lbms_id = item.get("bms_id").toString();
				String lbts_id = item.get("bts_id").toString();
				String bms_nam = item.get("bms_nam").toString();
				String bts_nam = item.get("bts_nam").toString();
				String wrk_dt = item.get("wrk_dt").toString();
				String drun_id = item.get("run_id").toString();
				Map<String,Object> delparam = new HashMap<String,Object>();
				delparam.put("bms_id", lbms_id);
				delparam.put("bts_id", lbts_id);
				delparam.put("run_id", drun_id);
				boolean result = scheduleDAO.deleteIDRun(delparam);
				if (result) {
					StringBuffer deleteDirCmd = new StringBuffer();
					deleteDirCmd.append("rm -rf");
					deleteDirCmd.append(Constants.BACKUP_SEPARATOR);
					deleteDirCmd.append(ms_sve_dir);
					deleteDirCmd.append("/");
					deleteDirCmd.append(bms_nam);
					deleteDirCmd.append("/");
					deleteDirCmd.append(bts_nam);
					deleteDirCmd.append("/BACKUP/");
					if ("BT02".equals(typ)) {
						deleteDirCmd.append("manual/databases/");
					} else if ("BT03".equals(typ)) {
						deleteDirCmd.append("manual/tables/");
					}
					deleteDirCmd.append(wrk_dt);
					Map<String,Object> deleteDirMap = new HashMap<String,Object>();
					deleteDirMap = ssh2Service.getData(deleteDirCmd.toString(), ms_usr, ms_pwd, ms_ip, Integer.valueOf(ms_port));
				}
			}
		}
		return "success";
	}

}
