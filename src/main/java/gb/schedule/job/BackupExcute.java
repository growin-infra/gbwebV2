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

@SuppressWarnings({"rawtypes","unchecked","unused"})
public class BackupExcute implements Job {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private static BackupScheduler schedulerHand = null;

	Map paramMap = null;
	String bts_nam, log_file_path, bms_usr, bms_pwd, bms_ip;
	int port;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		try {
			JobDataMap dataMap = context.getMergedJobDataMap();
			paramMap = new HashMap();
			paramMap.putAll((Map) dataMap.get("svcParamMap"));
			bts_nam = paramMap.get("bts_nam").toString();
			
			jobProcess();
			schedulekill();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("execute Exception : ", e.fillInStackTrace());
		}
		
	}
	
	public void jobProcess() {
		
		long startTime = System.currentTimeMillis();
		
		String xtrarunCmd, chkpointCmd, appOptCmd, completeCmd, bts_nam;
		String bms_pot, backup_dir, log_file;
		Map<String, Object> xtrarunMap = null;
		Map<String, Object> chkpointMap = null;
		Map<String, Object> completeMap = null;
		
		if (paramMap != null) {
			
			bms_usr = paramMap.get("bms_usr").toString();
			bms_pwd = paramMap.get("bms_pwd").toString();
			bms_ip = paramMap.get("bms_ip").toString();
			backup_dir = paramMap.get("backup_dir").toString();
			log_file = paramMap.get("log_file").toString();
			bms_pot = paramMap.get("bms_pot").toString();
			port = Integer.valueOf(bms_pot);
			log_file_path = backup_dir+"/"+log_file;
			xtrarunCmd = paramMap.get("xtrarunCmd").toString();
			chkpointCmd = paramMap.get("chkpointCmd").toString();
			appOptCmd = paramMap.get("appOptCmd").toString();
			completeCmd = paramMap.get("completeCmd").toString();
			xtrarunMap = new HashMap<String, Object>();
			StringBuilder startCmt = new StringBuilder();
			try {
				startCmt.append(Constants.DATA_NEW_LINE);
				startCmt.append("echo \"=================================================\" >> "+log_file_path);
				startCmt.append(Constants.DATA_NEW_LINE);
				startCmt.append("echo `date +%Y-%m-%d' '%H:%M:%S` BACKUP START >> "+log_file_path);
				startCmt.append(Constants.DATA_NEW_LINE);
				startCmt.append("echo \"=================================================\" >> "+log_file_path);
				startCmt.append(Constants.DATA_NEW_LINE);
				xtrarunMap = Ssh2Agent.backupRun(startCmt.toString()+xtrarunCmd, bms_usr, bms_pwd, bms_ip, port);
//				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobProcess Exception : ", e.fillInStackTrace());
			}
			
			chkpointMap = new HashMap<String, Object>();
			try {
				chkpointMap = Ssh2Agent.getData(chkpointCmd, bms_usr, bms_pwd, bms_ip, port);
				
				Map<String, Object> appOptMap = new HashMap<String, Object>();
				if (chkpointMap.get("value") != null) {
					if ("ok".equals(chkpointMap.get("value").toString())) {
						appOptMap = Ssh2Agent.getData(appOptCmd, bms_usr, bms_pwd, bms_ip, port);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobProcess Exception : ", e.fillInStackTrace());
			}
			
			completeMap = new HashMap<String, Object>();
			try {
				completeMap = Ssh2Agent.getData(completeCmd, bms_usr, bms_pwd, bms_ip, port);
				
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
						resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` BACKUP END >> "+log_file_path);
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
						resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` BACKUP END >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo \"=================================================\" >> "+log_file_path);
					}
					resultMap = Ssh2Agent.getData(resultCmd.toString(), bms_usr, bms_pwd, bms_ip, port);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobProcess Exception : ", e.fillInStackTrace());
			}
			
		}
		
	}
	
	public void schedulekill() {
		try {
			schedulerHand = BackupScheduler.getInstance();
			schedulerHand.deleteJob(Constants.JOB_FULL_BACKUP_EXCUTE+"::"+bts_nam,Constants.GROUP_BACKUP+"::"+bts_nam);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("schedulekill Exception : ", e.fillInStackTrace());
		}
	}

}
