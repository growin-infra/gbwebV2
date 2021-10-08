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

public class ScheduleFullBackup implements Job {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private static BackupScheduler schedulerHand = null;

	Map<String,Object> paramMap = null;
	String bms_nam, bts_nam, log_file_path, avt_nam, wrk_dt;
	String ms_usr, ms_pwd, ms_ip, ms_port;
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
			
			if ("M01".equals(avt_nam)) {
				jobXProcess();
			} else if ("M02".equals(avt_nam)) {
				jobSProcess();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("execute Exception", e.fillInStackTrace());
		}
		
	}
	
	public void jobXProcess() {
		
		long startTime = System.currentTimeMillis();
		
		String xtrarunCmd, chkpointCmd, appOptCmd, completeCmd;
		String backup_dir, log_file;
		Map<String, Object> xtrarunMap = null;
		Map<String, Object> chkpointMap = null;
		Map<String, Object> completeMap = null;
		
		if (paramMap != null) {
			
			backup_dir = paramMap.get("backup_dir").toString();
			log_file = paramMap.get("log_file").toString();
			ms_usr = paramMap.get("ms_usr").toString();
			ms_pwd = paramMap.get("ms_pwd").toString();
			ms_ip = paramMap.get("ms_ip").toString();
			ms_port = paramMap.get("ms_port").toString();
			port = Integer.valueOf(ms_port);
			log_file_path = backup_dir+"/"+log_file;
			xtrarunCmd = paramMap.get("xtrarunCmd").toString();
			chkpointCmd = paramMap.get("chkpointCmd").toString();
			appOptCmd = paramMap.get("appOptCmd").toString();
			completeCmd = paramMap.get("completeCmd").toString();
			xtrarunMap = new HashMap<String, Object>();
			StringBuilder startCmd = new StringBuilder();
			try {
				startCmd.append(Constants.DATA_NEW_LINE);
				startCmd.append("echo \"=================================================\" >> "+log_file_path);
				startCmd.append(Constants.DATA_NEW_LINE);
				startCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` FULL BACKUP START >> "+log_file_path);
				startCmd.append(Constants.DATA_NEW_LINE);
				startCmd.append("echo \"=================================================\" >> "+log_file_path);
				startCmd.append(Constants.DATA_NEW_LINE);
				xtrarunMap = Ssh2Agent.backupRun(startCmd.toString()+xtrarunCmd, ms_usr, ms_pwd, ms_ip, port);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobXProcess startCmd Exception", e.fillInStackTrace());
			}
			
			chkpointMap = new HashMap<String, Object>();
			try {
				chkpointMap = Ssh2Agent.getData(chkpointCmd, ms_usr, ms_pwd, ms_ip, port);
				
				Map<String, Object> appOptMap = new HashMap<String, Object>();
				if (chkpointMap.get("value") != null) {
					if ("ok".equals(chkpointMap.get("value").toString())) {
						appOptMap = Ssh2Agent.getData(appOptCmd, ms_usr, ms_pwd, ms_ip, port);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobXProcess chkpointMap Exception", e.fillInStackTrace());
			}
			
			completeMap = new HashMap<String, Object>();
			try {
				completeMap = Ssh2Agent.getData(completeCmd, ms_usr, ms_pwd, ms_ip, port);
				
				Map<String, Object> resultMap = new HashMap<String, Object>();
				if (completeMap.get("value") != null) {
					
					long endTime = System.currentTimeMillis();
					
					StringBuilder resultCmd = new StringBuilder();
					if ("ok".equals(completeMap.get("value").toString())) {
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
					} else {
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
					}
					resultMap = Ssh2Agent.getData(resultCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobXProcess completeMap Exception", e.fillInStackTrace());
			}
			
		}
		
	}
	
	public void jobSProcess() {
		
		long startTime = System.currentTimeMillis();
		
		String meCmd, cdcCmd, completeCmd;
		String backup_dir, log_file;
		
		if (paramMap != null) {
			
			backup_dir = paramMap.get("backup_dir").toString();
			log_file = paramMap.get("log_file").toString();
			
			ms_usr = paramMap.get("ms_usr").toString();
			ms_pwd = paramMap.get("ms_pwd").toString();
			ms_ip = paramMap.get("ms_ip").toString();
			ms_port = paramMap.get("ms_port").toString();
			port = Integer.valueOf(ms_port);
			
			log_file_path = backup_dir+"/"+log_file;
			meCmd = paramMap.get("meCmd").toString();
			cdcCmd = paramMap.get("cdcCmd").toString();
			completeCmd = paramMap.get("completeCmd").toString();
			
			Map<String, Object> startMap = new HashMap<String, Object>();
			StringBuilder startCmd = new StringBuilder();
			try {
				startCmd.append(Constants.DATA_NEW_LINE);
				startCmd.append("echo \"=================================================\" >> "+log_file_path);
				startCmd.append(Constants.DATA_NEW_LINE);
				startCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` FULL BACKUP START >> "+log_file_path);
				startCmd.append(Constants.DATA_NEW_LINE);
				startCmd.append("echo \"=================================================\" >> "+log_file_path);
				startCmd.append(Constants.DATA_NEW_LINE);
				startMap = Ssh2Agent.backupRun(startCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobSProcess startCmd Exception", e.fillInStackTrace());
			}
			
			Map<String, Object> meMap = new HashMap<String, Object>();
			try {
				meMap = Ssh2Agent.getData(meCmd, ms_usr, ms_pwd, ms_ip, port);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobSProcess meMap Exception", e.fillInStackTrace());
			}
			
			Map<String, Object> cdcMap = new HashMap<String, Object>();
			try {
				cdcMap = Ssh2Agent.getData(cdcCmd, ms_usr, ms_pwd, ms_ip, port);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobSProcess cdcMap Exception", e.fillInStackTrace());
			}
			
			Map<String, Object> completeMap = new HashMap<String, Object>();
			try {
				completeMap = Ssh2Agent.getData(completeCmd, ms_usr, ms_pwd, ms_ip, port);
				
				Map<String, Object> resultMap = new HashMap<String, Object>();
				if (completeMap.get("value") != null) {
					
					long endTime = System.currentTimeMillis();
					
					StringBuilder resultCmd = new StringBuilder();
					if ("ok".equals(completeMap.get("value").toString())) {
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
					} else {
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
					}
					resultMap = Ssh2Agent.getData(resultCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobSProcess completeMap Exception", e.fillInStackTrace());
			}
			
		}
		
	}
	
	public void schedulekill() {
		
		try {
			schedulerHand = BackupScheduler.getInstance();
			schedulerHand.deleteJob(Constants.JOB_FULL_BACKUP_EXCUTE+"::"+bms_nam+bts_nam + "::" + wrk_dt,Constants.GROUP_BACKUP+"::"+bms_nam+bts_nam);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("schedulekill Exception", e.fillInStackTrace());
		}
	}

}
