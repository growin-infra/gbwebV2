package gb.schedule.job;

import gb.common.agent.Ssh2Agent;
import gb.common.util.Constants;
import gb.schedule.BackupScheduler;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class BackupIncreExcute implements Job {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private static BackupScheduler schedulerHand = null;
	
	Map<String,Object> paramMap = null;
	String bms_nam, bts_nam, log_file_path, err_log_file_path, exit_flag, wrk_dt;
	String ms_usr, ms_pwd, ms_ip, ms_port, ms_bny_pth, bms_pwd, bms_usr;
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
			
			jobProcess();
			schedulekill();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("execute Exception", e.fillInStackTrace());
		}
		
	}
	
	public void jobProcess() throws Exception {
		
		long startTime = System.currentTimeMillis();
		
		String flushCmd, binLogInfoCmd, binlog_path;
		String backup_dir, log_file, err_log_file, backup_pdir;
		String com_target_host, com_target_port, com_user, com_pass, msq_clt_utl_pth;
		
		Map<String, Object> completeMap = null;
		
		if (paramMap != null) {
			
			exit_flag = paramMap.get("exit_flag").toString();
			
			backup_dir = paramMap.get("backup_dir").toString();
			backup_pdir = paramMap.get("backup_pdir").toString();
			log_file = paramMap.get("log_file").toString();
			err_log_file = paramMap.get("err_log_file").toString();
			ms_usr = paramMap.get("ms_usr").toString();
			ms_pwd = paramMap.get("ms_pwd").toString();
			ms_ip = paramMap.get("ms_ip").toString();
			ms_port = paramMap.get("ms_port").toString();
			ms_bny_pth = paramMap.get("ms_bny_pth").toString();
			//ms_ssh_usr = paramMap.get("ms_ssh_usr").toString();
			bms_usr = paramMap.get("bms_usr").toString();
			bms_pwd = paramMap.get("bms_pwd").toString();
			port = Integer.valueOf(ms_port);
			log_file_path = backup_dir+"/"+log_file;
			err_log_file_path = backup_dir+"/"+err_log_file;
			
			com_target_host = paramMap.get("com_target_host").toString();
			com_target_port = paramMap.get("com_target_port").toString();
			com_user = paramMap.get("com_user").toString();
			com_pass = paramMap.get("com_pass").toString();
			msq_clt_utl_pth = paramMap.get("msq_clt_utl_pth").toString();
			
			flushCmd = paramMap.get("flushCmd").toString();
			binLogInfoCmd = paramMap.get("binLogInfoCmd").toString();
			binlog_path = paramMap.get("binlog_path").toString();
			
			Map<String, Object> startMap = new HashMap<String, Object>();
			StringBuilder startCmd = new StringBuilder();
			startCmd.append("echo \"=================================================\" >> "+log_file_path);
			startCmd.append(Constants.DATA_NEW_LINE);
			startCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` INCREMENT BACKUP START >> "+log_file_path);
			startCmd.append(Constants.DATA_NEW_LINE);
			startCmd.append("echo \"=================================================\" >> "+log_file_path);
			startCmd.append(Constants.DATA_NEW_LINE);
			log.debug(startCmd);
			startMap = Ssh2Agent.backupRun(startCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
			
			StringBuilder resultCmd = new StringBuilder();
			StringBuilder logRedCmd = new StringBuilder();
			Map<String, Object> resultMap = new HashMap<String, Object>();
			Map<String, Object> logRecMap = new HashMap<String, Object>();
			
			if (!"1".endsWith(exit_flag)) {
				long endTime = System.currentTimeMillis();
				String completeCmd = "if [ \"`cat "+err_log_file_path+" | egrep -i \"fail\"`\" != '' ];then echo no; else echo ok; fi";
				completeMap = new HashMap<String, Object>();
				log.debug(completeCmd);
				completeMap = Ssh2Agent.getData(completeCmd, ms_usr, ms_pwd, ms_ip, port);
				
				if (completeMap != null) {
					
					if ("ok".equals(completeMap.get("value").toString())) {
						
						String ck2 = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+com_target_host+" \"tail -1 "+binlog_path+".index\"";
						Map<String, Object> ck2Map = new HashMap<String, Object>();
						Map<String, Object> ckMap = new HashMap<String, Object>();
						ckMap = Ssh2Agent.getData(ck2, ms_usr, ms_pwd, ms_ip, port);
						if (ckMap != null) {
							if (ckMap.get("value") != null && !"".equals(ckMap.get("value"))) {
								StringBuilder cdcCmd = new StringBuilder();
								Map<String, Object> binLogCpMap = new HashMap<String, Object>();
								StringBuilder binLogCpCmd = new StringBuilder();
								
								if (ckMap.get("value").toString().charAt(0) == '.') {
									
									String result = binlog_path.substring(binlog_path.lastIndexOf("/")+1);
									String b = binlog_path.replace(result,"");
									
									binLogCpCmd.append("TARGET=`"+binLogInfoCmd+"`");
									binLogCpCmd.append(Constants.DATA_NEW_LINE);
									binLogCpCmd.append(flushCmd);
									binLogCpCmd.append(Constants.DATA_NEW_LINE);
									binLogCpCmd.append("for i in $TARGET; do");
									binLogCpCmd.append(Constants.DATA_NEW_LINE);
									//binLogCpCmd.append("sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+ssh_id+"@"+com_target_host+" \"rsync -av $i "+ms_ssh_usr+"@"+ms_ip+":"+backup_dir+"\" >> "+log_file_path);
									binLogCpCmd.append("rsync -av --rsh=\"sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no\" "+bms_usr+"@"+com_target_host+":"+StringUtils.substringBeforeLast(b, "/")+"$i "+backup_dir+" >> "+log_file_path);
									binLogCpCmd.append(Constants.DATA_NEW_LINE);
									binLogCpCmd.append("done");
									log.debug(binLogCpCmd);
									binLogCpMap = Ssh2Agent.getData(binLogCpCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
									
									String ck3 = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+com_target_host+" \"tail -1 "+binlog_path+".index\"";
									ck2Map = Ssh2Agent.getData(ck3, ms_usr, ms_pwd, ms_ip, port);
									cdcCmd.append("TO_BIN=`echo "+ck2Map.get("value").toString()+" | cut -b2-10000`");
									
									
								} else {
									
									binLogCpCmd.append("TARGET=`"+binLogInfoCmd+"`");
									binLogCpCmd.append(Constants.DATA_NEW_LINE);
									binLogCpCmd.append(flushCmd);
									binLogCpCmd.append(Constants.DATA_NEW_LINE);
									binLogCpCmd.append("for i in $TARGET; do");
									binLogCpCmd.append(Constants.DATA_NEW_LINE);
									//binLogCpCmd.append("sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+ssh_id+"@"+com_target_host+" \"rsync -av $i "+ms_ssh_usr+"@"+ms_ip+":"+backup_dir+"\" >> "+log_file_path);
									binLogCpCmd.append("rsync -av --rsh=\"sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no\" "+bms_usr+"@"+com_target_host+":$i "+backup_dir+" >> "+log_file_path);
									binLogCpCmd.append(Constants.DATA_NEW_LINE);
									binLogCpCmd.append("done");
									log.debug(binLogCpCmd);
									binLogCpMap = Ssh2Agent.getData(binLogCpCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
									
									String ck3 = "sshpass -p"+bms_pwd+" ssh -o StrictHostKeyChecking=no "+bms_usr+"@"+com_target_host+" \"tail -1 "+binlog_path+".index\"";
									ck2Map = Ssh2Agent.getData(ck3, ms_usr, ms_pwd, ms_ip, port);
									int count = StringUtils.countMatches(binlog_path, "/");
									cdcCmd.append("TO_BIN=`echo "+ck2Map.get("value").toString()+" | cut -d / -f "+(count+1)+"`");
								}
								cdcCmd.append(Constants.DATA_NEW_LINE);
								cdcCmd.append("echo $TO_BIN > "+backup_dir+"/next_bin.log");
								cdcCmd.append(Constants.DATA_NEW_LINE);
								cdcCmd.append("if [ \"`cat "+backup_dir+"/next_bin.log`\" != '' ];then echo ok; else echo no; fi");
								log.debug(cdcCmd);
								Map<String, Object> cdcMap = new HashMap<String, Object>();
								cdcMap = Ssh2Agent.getData(cdcCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
								if ("ok".equals(cdcMap.get("value").toString())) {
									
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append(flushCmd);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo Result : SUCCESS >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` INCREMENT BACKUP END >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo "+wrk_dt+" > "+backup_pdir+"/last_backup");
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
									
								} else {
									
									resultCmd.append("echo \"binary log (log bin) information could not be verified.\" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("cat "+err_log_file_path+" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo Result : FAIL >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` INCREMENT BACKUP END >> "+log_file_path);
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("echo \"=================================================\" >> "+log_file_path);
									
									logRedCmd.append("cat "+backup_dir+"/copybin.txt >> "+err_log_file_path);
									logRedCmd.append(Constants.DATA_NEW_LINE);
									logRedCmd.append("cat "+backup_dir+"/copybin.txt >> "+log_file_path);
									
									resultCmd.append(Constants.DATA_NEW_LINE);
									resultCmd.append("cat "+log_file_path+" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
									log.debug(resultCmd);
									resultMap = Ssh2Agent.getData(resultCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
									log.debug(logRedCmd);
									logRecMap = Ssh2Agent.getData(logRedCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
									
								}
							}
						}
						
					} else {
						resultCmd.append("cat "+err_log_file_path+" >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo \"=================================================\" >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo Result : FAIL >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` INCREMENT BACKUP END >> "+log_file_path);
						resultCmd.append(Constants.DATA_NEW_LINE);
						resultCmd.append("echo \"=================================================\" >> "+log_file_path);
						
						logRedCmd.append("cat "+backup_dir+"/copybin.txt >> "+err_log_file_path);
						logRedCmd.append(Constants.DATA_NEW_LINE);
						logRedCmd.append("cat "+backup_dir+"/copybin.txt >> "+log_file_path);
					}
					resultCmd.append(Constants.DATA_NEW_LINE);
					resultCmd.append("cat "+log_file_path+" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
					log.debug(resultCmd);
					resultMap = Ssh2Agent.getData(resultCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
					log.debug(logRedCmd);
					logRecMap = Ssh2Agent.getData(logRedCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
				}
				
			} else {
				long endTime = System.currentTimeMillis();
				resultCmd.append("cat "+err_log_file_path+" >> "+log_file_path);
				resultCmd.append(Constants.DATA_NEW_LINE);
				resultCmd.append("echo \"=================================================\" >> "+log_file_path);
				resultCmd.append(Constants.DATA_NEW_LINE);
				resultCmd.append("echo Result : FAIL >> "+log_file_path);
				resultCmd.append(Constants.DATA_NEW_LINE);
				resultCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
				resultCmd.append(Constants.DATA_NEW_LINE);
				resultCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` INCREMENT BACKUP END >> "+log_file_path);
				resultCmd.append(Constants.DATA_NEW_LINE);
				resultCmd.append("echo \"=================================================\" >> "+log_file_path);
				
				logRedCmd.append("cat "+backup_dir+"/copybin.txt >> "+err_log_file_path);
				logRedCmd.append(Constants.DATA_NEW_LINE);
				logRedCmd.append("cat "+backup_dir+"/copybin.txt >> "+log_file_path);
				
				resultCmd.append(Constants.DATA_NEW_LINE);
				resultCmd.append("cat "+log_file_path+" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
				log.debug(resultCmd);
				resultMap = Ssh2Agent.getData(resultCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
				log.debug(logRedCmd);
				logRecMap = Ssh2Agent.getData(logRedCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
			}
		}
	}
	
	public void schedulekill() {
		
		try {
			schedulerHand = BackupScheduler.getInstance();
			schedulerHand.deleteJob(Constants.JOB_INCRE_BACKUP_EXCUTE+"::"+bms_nam+bts_nam + "::" + wrk_dt,Constants.GROUP_BACKUP+"::"+bms_nam+bts_nam);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("schedulekill Exception : ", e.fillInStackTrace());
		}
	}

}
