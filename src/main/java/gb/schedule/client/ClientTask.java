package gb.schedule.client;

import gb.common.service.Ssh2Service;
import gb.common.util.Constants;
import gb.manage.service.ManageService;
import gb.schedule.service.ScheduleService;
import gb.setup.service.SetupService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings({"unused","unchecked"})
public class ClientTask {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "scheduleService")
	private ScheduleService scheduleService;
	
	@Resource(name = "manageService")
	private ManageService manageService;
	
	@Resource(name = "setupService")
	private SetupService setupService;
	
	@Resource(name = "ssh2Service")
	private Ssh2Service ssh2Service;
	
	/**
	 * 등록된 스케줄 정리 (매분)
	 * @throws Exception
	 */
	@Scheduled(cron="0 0-59 * * * ?")
	public void clientTaskShell() throws Exception {
		List<Map<String,Object>> list = null;
		try {
			
			list = scheduleService.cronSelect();
			
			//리스트가 있으면 로직처리
			if (list.size() != 0 && list != null) {
				for (int i = 0; i < list.size(); i++) {
					Map<String,Object> item = (Map<String, Object>) list.get(i);
			    	
					String wrk_dt = item.get("wrk_dt").toString();
					String typ = item.get("typ").toString();
					String bms_nam = "";
					String bts_nam = "";
					
					Map<String,Object> bmmap = setupService.findMngSvrNam(item);
					if (bmmap != null && bmmap.get("bms_nam") != null) {
						bms_nam = bmmap.get("bms_nam").toString();
					}
					
					Map<String,Object> btmap = setupService.findTgtSvrNam(item);
					if (btmap != null && btmap.get("bts_nam") != null) {
						bts_nam = btmap.get("bts_nam").toString();
					}
					
			    	//was os 접속정보
					Map<String,Object> param = new HashMap<String,Object>();
					param.put("ms_id", item.get("bms_id").toString().charAt(0));
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
						
						if (!"".equals(bms_nam) && !"".equals(bts_nam)) {
							
							item.put("knd", "G01");
			    			item.put("bts_nam", bts_nam);
							
							//log get command
			    			String cmd = "cat "+ms_sve_dir + "/" + bms_nam + "/" + bts_nam + "/BACKUP/" + wrk_dt + "/ginian.log";
			    			if (!"BT01".equals(typ)) {
			    				if ("BT02".equals(typ)) {
			    					cmd = "cat "+ms_sve_dir + "/" + bms_nam + "/" + bts_nam + "/BACKUP/manual/databases/" + wrk_dt + "/ginian.log";
			    				} else if ("BT03".equals(typ)) {
			    					cmd = "cat "+ms_sve_dir + "/" + bms_nam + "/" + bts_nam + "/BACKUP/manual/tables/" + wrk_dt + "/ginian.log";
			    				}
			    			}
					    	
					    	Map<String,Object> cronLogMap = new HashMap<String,Object>();
					    	cronLogMap = ssh2Service.getList(cmd, ms_usr, ms_pwd, ms_ip, ms_port);
					    	
					    	if (cronLogMap != null) {
					    		List<String> logList = new ArrayList<String>();
					    		logList = (List<String>) cronLogMap.get("list");
					    		if(logList != null && logList.size() >0){
					    			StringBuilder strb = new StringBuilder();
					    			for (int j=0; j<logList.size(); j++) {
					    				if (strb.length() > 0) strb.append(Constants.DATA_NEW_LINE);
						    			strb.append(logList.get(j));
					    			}
					    			item.put("log", strb.toString());
					    			
					    			String result = scheduleService.cronJob(item);
					    		} else {
						    		//이미삭제되었거나 쓰레기 데이터
					    			boolean result = scheduleService.cronDelete(item);
						    	}
					    	}
						}
					}
				}
			}
//			else {
//				log.debug("----------------no list--------------------");
//			}

		} catch(Exception e) {
			e.printStackTrace();
			log.error("clientTaskShell Exception : "+e.fillInStackTrace());
		}
	}
	
	/**
	 * 배치 : 백업보관기간 정리 (매시 5분)
	 * @throws Exception
	 */
	@Scheduled(cron="0 5 0-23 * * ?")
	public void clientTaskBackup() throws Exception {
		
		List<Map<String,Object>> list = null;
		String sh = "sh /ginian/sh/delbackup.sh";
		
		try {
			list = scheduleService.listDelSchTarget();
			if (list.size() != 0 && list != null) {
				for (int i = 0; i < list.size(); i++) {
					Map<String,Object> item = (Map<String, Object>) list.get(i);
			    	
					String run_id = item.get("run_id").toString();
					String run_pid = item.get("run_pid").toString();
					String wrk_dt = item.get("wrk_dt").toString();
					String bms_nam = item.get("bms_nam").toString();
					String bms_id = item.get("bms_id").toString();
					String bts_nam = item.get("bts_nam").toString();
					String bts_id = item.get("bts_id").toString();
					String typ = item.get("typ").toString();
					String kep_pod = item.get("kep_pod").toString();
					String lvl = item.get("lvl").toString();
					
			    	//was os 접속정보
					Map<String,Object> param = new HashMap<String,Object>();
					param.put("ms_id", bms_id.charAt(0));
			    	Map<String,Object> msmap = manageService.findSvrMap(param);
					
			    	String ms_ip = "";
					String ms_port = "";
					String ms_usr = "";
					String ms_pwd = "";
					String ms_sve_dir = "";
					if (msmap != null) {
						ms_ip = msmap.get("ms_ip").toString();
						ms_port = msmap.get("ms_port").toString();
						ms_usr = msmap.get("ms_usr").toString();
						ms_pwd = msmap.get("ms_pwd").toString();
						ms_sve_dir = msmap.get("ms_sve_dir").toString();
						
						StringBuffer targer_dir = new StringBuffer();
						targer_dir.append(ms_sve_dir);
						targer_dir.append("/");
						targer_dir.append(bms_nam);
						targer_dir.append("/");
						targer_dir.append(bts_nam);
						targer_dir.append("/BACKUP/");
						if ("BT02".equals(typ)) {
							targer_dir.append("manual/databases/");
						} else if ("BT03".equals(typ)) {
							targer_dir.append("manual/tables/");
						}
						targer_dir.append(wrk_dt);
						
						StringBuffer cmd = new StringBuffer();
						cmd.append(sh);
						cmd.append(Constants.BACKUP_SEPARATOR);
						cmd.append(targer_dir.toString());
						cmd.append(Constants.BACKUP_SEPARATOR);
						cmd.append(wrk_dt);
						cmd.append(Constants.BACKUP_SEPARATOR);
						cmd.append(kep_pod);
						
						Map<String,Object> cronLogMap = new HashMap<String,Object>();
						cronLogMap = ssh2Service.getData(cmd.toString(), ms_usr, ms_pwd, ms_ip, Integer.valueOf(ms_port));
						if (cronLogMap != null) {
							String rt = cronLogMap.get("value").toString();
							if ("none".equals(rt.trim())) {	//디렉토리가 삭제된 쓰레기 데이터
								Map<String,Object> dp = new HashMap<String,Object>();
								dp.put("bms_id", bms_id);
								dp.put("bts_id", bts_id);
								dp.put("wrk_dt", wrk_dt);
								boolean delrunrt = setupService.deleteRun(dp);
							}
							else if ("delete_run".equals(rt.trim())) {	//디렉토리 삭제
								
								if ("L01".equals(lvl)) {	//2018.01.10 풀백업이 삭제되면 풀백업 아래 증분백업도 삭제
									Map<String,Object> dparam = new HashMap<String,Object>();
									dparam.put("run_id", run_id);
									dparam.put("run_pid", run_pid);
									dparam.put("wrk_dt", wrk_dt);
									dparam.put("bms_id", bms_id);
									dparam.put("bts_id", bts_id);
									dparam.put("ms_ip", ms_ip);
									dparam.put("ms_port", ms_port);
									dparam.put("ms_usr", ms_usr);
									dparam.put("ms_pwd", ms_pwd);
									dparam.put("ms_sve_dir", ms_sve_dir);
									dparam.put("typ", typ);
									String dtdelresult = scheduleService.listSchDtTarget(dparam);
								}
								
							}
						}
					}
				}
			} else {
				Map<String,Object> msmap = manageService.findSvrMap(null);
				String ms_ip = "";
				String ms_port = "";
				String ms_usr = "";
				String ms_pwd = "";
				String ms_sve_dir = "";
				if (msmap != null) {
					ms_ip = msmap.get("ms_ip").toString();
					ms_port = msmap.get("ms_port").toString();
					ms_usr = msmap.get("ms_usr").toString();
					ms_pwd = msmap.get("ms_pwd").toString();
					ms_sve_dir = msmap.get("ms_sve_dir").toString();
					
					Map<String,Object> dbparam = null;
					StringBuffer cmd1 = new StringBuffer();
					cmd1.append("ls "+ms_sve_dir);
					Map<String,Object> bmsChkMap = new HashMap<String,Object>();
					bmsChkMap = ssh2Service.getList(cmd1.toString(), ms_usr, ms_pwd, ms_ip, Integer.valueOf(ms_port));
					if (bmsChkMap != null) {
						List<String> bmslist = new ArrayList<String>();
						bmslist = (List) bmsChkMap.get("list");
						for (int i=0; i<bmslist.size(); i++) {
							String bms_nam = bmslist.get(i).toString();
							String bms_id = "";
							Map<String,Object> param1 = new HashMap<String,Object>();
							param1.put("bms_nam", bms_nam);
							Map<String,Object> bmsmap = setupService.findMngSvrNamSea(param1);
							if (bmsmap != null) {
								bms_id = bmsmap.get("bms_id").toString();
								
								StringBuffer cmd2 = new StringBuffer();
								cmd2.append("ls "+ms_sve_dir+"/"+bms_nam);
								Map<String,Object> btsChkMap = new HashMap<String,Object>();
								btsChkMap = ssh2Service.getList(cmd2.toString(), ms_usr, ms_pwd, ms_ip, Integer.valueOf(ms_port));
								if (btsChkMap != null) {
									List<String> btslist = new ArrayList<String>();
									btslist = (List) btsChkMap.get("list");
									for (int j=0; j<btslist.size(); j++) {
										String bts_nam = btslist.get(j).toString();
										String bts_id = "";
										Map<String,Object> param2 = new HashMap<String,Object>();
										param2.put("bts_pid", bms_id);
										param2.put("bts_nam", bts_nam);
										Map<String,Object> btsmap = setupService.findTgtSvrNamSea(param2);
										if (btsmap != null) {
											bts_id = btsmap.get("bts_id").toString();
											
											StringBuffer cmd3 = new StringBuffer();
											cmd3.append("ls "+ms_sve_dir+"/"+bms_nam+"/"+bts_nam+"/BACKUP | egrep -v \"last_backup|last_fullbackup\"");
											Map<String,Object> targetMap = new HashMap<String,Object>();
											targetMap = ssh2Service.getList(cmd3.toString(), ms_usr, ms_pwd, ms_ip, Integer.valueOf(ms_port));
											if (targetMap != null) {
												List<String> targetlist = new ArrayList<String>();
												targetlist = (List) targetMap.get("list");
												for (int k=0; k<targetlist.size(); k++) {
													String target_dt = targetlist.get(k).toString();
													Map<String,Object> param3 = new HashMap<String,Object>();
													param3.put("knd", "G01");
													param3.put("wrk_dt", target_dt);
													param3.put("bms_id", bms_id);
													param3.put("bts_id", bts_id);
													Map<String,Object> dtinfo = setupService.findLogDt(param3);
													if (dtinfo != null) {
														String r_pcy = dtinfo.get("kep_pod").toString();
														String tg_dir = ms_sve_dir+"/"+bms_nam+"/"+bts_nam+"/BACKUP/"+target_dt;
														StringBuffer cmd4 = new StringBuffer();
														cmd4.append(sh);
														cmd4.append(Constants.BACKUP_SEPARATOR);
														cmd4.append(tg_dir);
														cmd4.append(Constants.BACKUP_SEPARATOR);
														cmd4.append(target_dt);
														cmd4.append(Constants.BACKUP_SEPARATOR);
														cmd4.append(r_pcy);
														Map<String,Object> cronLogMap = new HashMap<String,Object>();
														cronLogMap = ssh2Service.getData(cmd4.toString(), ms_usr, ms_pwd, ms_ip, Integer.valueOf(ms_port));
													} else {
														String r_pcy = "1";
														String tg_dir = ms_sve_dir+"/"+bms_nam+"/"+bts_nam+"/BACKUP/"+target_dt;
														StringBuffer cmd4 = new StringBuffer();
														cmd4.append(sh);
														cmd4.append(Constants.BACKUP_SEPARATOR);
														cmd4.append(tg_dir);
														cmd4.append(Constants.BACKUP_SEPARATOR);
														cmd4.append(target_dt);
														cmd4.append(Constants.BACKUP_SEPARATOR);
														cmd4.append(r_pcy);
														Map<String,Object> cronLogMap = new HashMap<String,Object>();
														cronLogMap = ssh2Service.getData(cmd4.toString(), ms_usr, ms_pwd, ms_ip, Integer.valueOf(ms_port));
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
			log.error("clientTaskBackup Exception : "+e.fillInStackTrace());
		}
	
	}

		
}
