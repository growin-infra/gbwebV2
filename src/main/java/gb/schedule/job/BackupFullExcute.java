package gb.schedule.job;

import gb.common.agent.Ssh2Agent;
import gb.common.util.Constants;
import gb.schedule.BackupScheduler;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class BackupFullExcute implements Job {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private static BackupScheduler schedulerHand = null;

	Map<String,Object> paramMap = null;
	String bms_nam, bts_nam, log_file_path, err_log_file_path, log_file, err_log_file, avt_nam, wrk_dt;
	String ms_usr, ms_pwd, ms_ip, ms_port, bms_pwd, bms_usr;
	String com_target_host, backup_dir, backup_pdir, bak_typ;
	int port;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		try {
			JobDataMap dataMap = context.getMergedJobDataMap();
			paramMap = new HashMap<String,Object>();
			paramMap.putAll((Map) dataMap.get("svcParamMap"));
			bms_nam = paramMap.get("bms_nam").toString();
			bts_nam = paramMap.get("bts_nam").toString();
			wrk_dt = paramMap.get("wrk_dt").toString();
			avt_nam = paramMap.get("avt_nam").toString();
			
			backup_dir = paramMap.get("backup_dir").toString();
			backup_pdir = paramMap.get("backup_pdir").toString();
			bak_typ = paramMap.get("bak_typ").toString();
			
			log_file = paramMap.get("log_file").toString();
			err_log_file = paramMap.get("err_log_file").toString();
			ms_usr = paramMap.get("ms_usr").toString();
			ms_pwd = paramMap.get("ms_pwd").toString();
			ms_ip = paramMap.get("ms_ip").toString();
			ms_port = paramMap.get("ms_port").toString();
			port = Integer.valueOf(ms_port);
			bms_pwd = paramMap.get("bms_pwd").toString();
			bms_usr = paramMap.get("bms_usr").toString();
			com_target_host = paramMap.get("com_target_host").toString();
			
			log_file_path = backup_dir+"/"+log_file;
			err_log_file_path = backup_dir+"/"+err_log_file;
			
			if ("M01".equals(avt_nam)) {
				jobXProcess();
			} else if ("M02".equals(avt_nam)) {
				jobSProcess();
			}
			schedulekill();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("execute Exception : ", e.fillInStackTrace());
		}
		
	}
	
	public void jobXProcess() {
		
		long startTime = System.currentTimeMillis();
		
		String xtrarunCmd, chkpointCmd, appOptCmd = "", completeCmd;
		
		Map<String, Object> startMap = null;
		Map<String, Object> rpmchkMap = null;
		Map<String, Object> xtrarunMap = null;
		Map<String, Object> chkpointMap = null;
		Map<String, Object> completeMap = null;
		Map<String, Object> resultMap = null;
		
		if (paramMap != null) {
			
			xtrarunCmd = paramMap.get("xtrarunCmd").toString();
			chkpointCmd = paramMap.get("chkpointCmd").toString();
			if (paramMap.get("appOptCmd") != null) {
				appOptCmd = paramMap.get("appOptCmd").toString();
			}
			completeCmd = paramMap.get("completeCmd").toString();
			startMap = new HashMap<String, Object>();
			
			StringBuilder startCmd = new StringBuilder();
			StringBuilder logCmd = null;
			
			try {
				startCmd.append("echo \"=================================================\" >> "+log_file_path);
				startCmd.append(Constants.DATA_NEW_LINE);
				startCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` FULL BACKUP START >> "+log_file_path);
				startCmd.append(Constants.DATA_NEW_LINE);
				startCmd.append("echo \"=================================================\" >> "+log_file_path);
				startMap = Ssh2Agent.backupRun(startCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobXProcess startCmd Exception : ", e.fillInStackTrace());
			}
			
			String rpmchkCmd = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+com_target_host+" \"if [ \"`rpm -qa | grep -i '^sshpass'`\" == \"\" ];then echo no; else echo ok; fi\"";
			rpmchkMap = new HashMap<String, Object>();
			try {
				log.debug(rpmchkCmd);
				rpmchkMap = Ssh2Agent.getData(rpmchkCmd, ms_usr, ms_pwd, ms_ip, port);
				
				StringBuilder resultCmd = new StringBuilder();
				
				if (rpmchkMap != null && rpmchkMap.get("value") != null) {
					if ("ok".equals(rpmchkMap.get("value").toString())) {
						
						xtrarunMap = new HashMap<String, Object>();
						try {
							log.debug(xtrarunCmd);
							xtrarunMap = Ssh2Agent.backupRun(xtrarunCmd, ms_usr, ms_pwd, ms_ip, port);
						} catch (Exception e) {
							e.printStackTrace();
							log.error("jobXProcess xtrarunCmd Exception : ", e.fillInStackTrace());
						}
						
						chkpointMap = new HashMap<String, Object>();
						try {
							log.debug(chkpointCmd);
							chkpointMap = Ssh2Agent.getData(chkpointCmd, ms_usr, ms_pwd, ms_ip, port);
							Map<String, Object> appOptMap = new HashMap<String, Object>();
							if (chkpointMap.get("value") != null) {
								if ("ok".equals(chkpointMap.get("value").toString()) && !"".equals(appOptCmd)) {
									log.debug(appOptCmd);
									appOptMap = Ssh2Agent.getData(appOptCmd, ms_usr, ms_pwd, ms_ip, port);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							log.error("jobXProcess chkpointMap Exception : ", e.fillInStackTrace());
						}
						
						completeMap = new HashMap<String, Object>();
						try {
							log.debug(completeCmd);
							completeMap = Ssh2Agent.getData(completeCmd, ms_usr, ms_pwd, ms_ip, port);
							
							resultMap = new HashMap<String, Object>();
							if (completeMap.get("value") != null) {
								
								long endTime = System.currentTimeMillis();
								
								if ("ok".equals(completeMap.get("value").toString())) {
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo Result : SUCCESS >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` FULL BACKUP END >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo "+wrk_dt+" > "+backup_pdir+"/last_fullbackup");
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo "+wrk_dt+" > "+backup_pdir+"/last_backup");
								} else {
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo Result : FAIL >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` FULL BACKUP END >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
								}
								resultCmd.append(Constants.DATA_NEW_LINE);
								resultCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors_or_warnings\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
								log.debug(resultCmd);
								resultMap = Ssh2Agent.getData(resultCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
							log.error("jobXProcess completeMap Exception : ", e.fillInStackTrace());
						}
						
					} else {
						
						resultMap = new HashMap<String, Object>();
						
						long endTime = System.currentTimeMillis();
						resultCmd.append("echo \"'sshpass' RPM is not installed !!\" >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo \"=================================================\" >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo Result : FAIL >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` FULL BACKUP END >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo \"=================================================\" >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors_or_warnings\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
						log.debug(resultCmd);
						resultMap = Ssh2Agent.getData(resultCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
						
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobXProcess rpmchkMap Exception : ", e.fillInStackTrace());
			}
		}
	}
	
	public void jobSProcess() {
		
		long startTime = System.currentTimeMillis();
		
		String meCmd, cdcCmd, completeCmd;
		String backup_dir, backup_pdir;
		
		Map<String, Object> rpmchkMap = null;
		Map<String, Object> resultMap = null;
		
		if (paramMap != null) {
			
			backup_dir = paramMap.get("backup_dir").toString();
			backup_pdir = paramMap.get("backup_pdir").toString();
			log_file = paramMap.get("log_file").toString();
			
			ms_usr = paramMap.get("ms_usr").toString();
			ms_pwd = paramMap.get("ms_pwd").toString();
			ms_ip = paramMap.get("ms_ip").toString();
			ms_port = paramMap.get("ms_port").toString();
			port = Integer.valueOf(ms_port);
			
			log_file_path = backup_dir+"/"+log_file;
			err_log_file_path = backup_dir+"/"+err_log_file;
			meCmd = paramMap.get("meCmd").toString();
			cdcCmd = paramMap.get("cdcCmd").toString();
			completeCmd = paramMap.get("completeCmd").toString();
			
			Map<String, Object> startMap = new HashMap<String, Object>();
			StringBuilder startCmd = new StringBuilder();
			try {
				startCmd.append("echo \"=================================================\" >> "+log_file_path);
				startCmd.append(Constants.DATA_NEW_LINE);
				startCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` FULL BACKUP START >> "+log_file_path);
				startCmd.append(Constants.DATA_NEW_LINE);
				startCmd.append("echo \"=================================================\" >> "+log_file_path);
				startCmd.append(Constants.DATA_NEW_LINE);
				log.debug(startCmd);
				startMap = Ssh2Agent.backupRun(startCmd.toString(), ms_usr, ms_pwd, ms_ip, port);//결과****
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobSProcess startCmd Exception : ", e.fillInStackTrace());
			}
			
			String rpmchkCmd = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+com_target_host+" \"if [ \"`rpm -qa | grep -i '^sshpass'`\" == \"\" ];then echo no; else echo ok; fi\"";
			rpmchkMap = new HashMap<String, Object>();
			try {
				log.debug(rpmchkCmd);
				rpmchkMap = Ssh2Agent.getData(rpmchkCmd, ms_usr, ms_pwd, ms_ip, port);
				
				StringBuilder resultCmd = new StringBuilder();
				
				if (rpmchkMap != null) {
					if ("ok".equals(rpmchkMap.get("value").toString())) {
						
						Map<String, Object> meMap = new HashMap<String, Object>();
						try {
							log.debug(meCmd);
							meMap = Ssh2Agent.getData(meCmd, ms_usr, ms_pwd, ms_ip, port);
						} catch (Exception e) {
							e.printStackTrace();
							log.error("jobSProcess meMap Exception : ", e.fillInStackTrace());
						}
						
						Map<String, Object> completeMap = new HashMap<String, Object>();
						try {
							log.debug(completeCmd);
							completeMap = Ssh2Agent.getData(completeCmd, ms_usr, ms_pwd, ms_ip, port);
							
							resultMap = new HashMap<String, Object>();
							if (completeMap.get("value") != null) {
								
								long endTime = System.currentTimeMillis();
								
								resultCmd = new StringBuilder();
								if ("ok".equals(completeMap.get("value").toString())) {
									/* 2019.02.18 */
									if (!"".equals(cdcCmd)) {
										Map<String, Object> cdcMap = new HashMap<String, Object>();
										log.debug(cdcCmd);
										cdcMap = Ssh2Agent.getData(cdcCmd, ms_usr, ms_pwd, ms_ip, port);
										if (!"ok".equals(cdcMap.get("value").toString())) {
											resultCmd.append("echo \"\" >> "+log_file_path);
											resultCmd.append(Constants.DATA_NEW_LINE);
											resultCmd.append("echo \"Info : binary log (log bin) information could not be verified.\" >> "+log_file_path);
											resultCmd.append(Constants.DATA_NEW_LINE);
											resultCmd.append("echo \"\" >> "+log_file_path);
										}
									} else {
										resultCmd.append("echo \"\" >> "+log_file_path);
										resultCmd.append(Constants.DATA_NEW_LINE);
										resultCmd.append("echo \"Info : binary log (log bin) information could not be verified.\" >> "+log_file_path);
										resultCmd.append(Constants.DATA_NEW_LINE);
										resultCmd.append("echo \"\" >> "+log_file_path);
									}
									/*//2019.02.18 */
									
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo Result : SUCCESS >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` FULL BACKUP END >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo "+wrk_dt+" > "+backup_pdir+"/last_fullbackup");
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo "+wrk_dt+" > "+backup_pdir+"/last_backup");
								} else {
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo Result : FAIL >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` FULL BACKUP END >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
								}
								resultCmd.append(Constants.DATA_NEW_LINE);
								resultCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors_or_warnings\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
								log.debug(resultCmd);
								resultMap = Ssh2Agent.getData(resultCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
							log.error("jobSProcess completeMap Exception : ", e.fillInStackTrace());
						}
						
						
					} else {
						
						long endTime = System.currentTimeMillis();
						resultMap = new HashMap<String, Object>();
						resultCmd = new StringBuilder();
						
						resultCmd.append("echo \"'sshpass' RPM is not installed !!\" >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo \"=================================================\" >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo Result : FAIL >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` FULL BACKUP END >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo \"=================================================\" >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors_or_warnings\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
						log.debug(resultCmd);
						resultMap = Ssh2Agent.getData(resultCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobSProcess rpmchkMap Exception : ", e.fillInStackTrace());
			}
			
		}
	}
	
	public void schedulekill() {
		
		try {
			schedulerHand = BackupScheduler.getInstance();
			schedulerHand.deleteJob(Constants.JOB_FULL_BACKUP_EXCUTE+"::"+bms_nam+bts_nam + "::" + wrk_dt,Constants.GROUP_BACKUP+"::"+bms_nam+bts_nam);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("schedulekill Exception : ", e.fillInStackTrace());
		}
	}

}
