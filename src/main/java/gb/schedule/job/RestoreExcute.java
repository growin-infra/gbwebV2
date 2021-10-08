package gb.schedule.job;

import gb.common.agent.Ssh2Agent;
import gb.common.util.Constants;
import gb.schedule.BackupScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RestoreExcute implements Job {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private static BackupScheduler schedulerHand = null;
	
	private Map<String,Object> paramMap = null;
	private String bms_nam, bts_nam, wrk_dt, lvl, mtd;
	private String ms_usr, ms_pwd, ms_ip, ms_port;
	private String restore_binpath, restore_user, restore_pass, restore_host, restore_port, restore_db_start_name, restore_tmpdir;
	private String xtr_bny_log_pth, msq_clt_utl_pth, restore_decompress_yn, restore_instance_no = "";
	private String restore_default_file, restore_stop_opt, restore_os_user, restore_os_pwd;
	private String restore_logDir, log_file_path, err_log_file_path, restore_dir = "", data_dir = "";
	private String bms_pot, backup_dir, backup_pdir, lst_full_dir, lst_full_wrk_dt = "", restore_tmp;
	private String dbchkCmd;
	
	int port;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		try {
			JobDataMap dataMap = context.getMergedJobDataMap();
			paramMap = new HashMap<String,Object>();
			paramMap.putAll((Map<String, Object>) dataMap.get("svcParamMap"));
			bms_nam = paramMap.get("bms_nam").toString();
			bts_nam = paramMap.get("bts_nam").toString();
			wrk_dt = paramMap.get("wrk_dt").toString();
			
			ms_usr = paramMap.get("ms_usr").toString();
			ms_pwd = paramMap.get("ms_pwd").toString();
			ms_ip = paramMap.get("ms_ip").toString();
			ms_port = paramMap.get("ms_port").toString();
			port = Integer.valueOf(ms_port);
			
			wrk_dt = paramMap.get("wrk_dt").toString();
			lvl = paramMap.get("lvl").toString();
			mtd = paramMap.get("mtd").toString();
			
			if ("M02".equals(mtd)) {
				jobMProcess();
			} else if ("M01".equals(mtd)) {
				jobXProcess();
			}
			
			schedulekill();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("execute Exception", e.fillInStackTrace());
		}
		
	}
	
	public void jobXProcess() throws Exception {
		
		long startTime = System.currentTimeMillis();
		
		String BIN, BIN_POS;
		
		//Map<String, Object> dbchkMap = null;
		Map<String, Object> dbrunchkMap = null;
		Map<String, Object> dbStrMap = null;
		Map<String, Object> startMap = null;
		Map<String, Object> completeMap = null;
		Map<String, Object> logMap = null;
		StringBuilder dbstopCmd = null;
		Map<String, Object> tgchkMap = null;
		
		if (paramMap != null) {
			
//			xtra_bin_path = paramMap.get("ms_bny_pth").toString();
			restore_default_file = paramMap.get("restore_default_file").toString();
			
			backup_dir = paramMap.get("backup_dir").toString();
			backup_pdir = paramMap.get("backup_pdir").toString();
			lst_full_dir = paramMap.get("lst_full_dir").toString();
			restore_tmp = paramMap.get("restore_tmp").toString();
			lst_full_wrk_dt = paramMap.get("lst_full_wrk_dt").toString();
			
			restore_dir = paramMap.get("restore_dir").toString();
			restore_logDir = paramMap.get("restore_logDir").toString();
			log_file_path = paramMap.get("log_file_path").toString();
			err_log_file_path = paramMap.get("err_log_file_path").toString();
			
			msq_clt_utl_pth = paramMap.get("msq_clt_utl_pth").toString();
			restore_binpath = paramMap.get("restore_binpath").toString();
			xtr_bny_log_pth = paramMap.get("xtr_bny_log_pth").toString();
			restore_user = paramMap.get("restore_user").toString();
			restore_pass = paramMap.get("restore_pass").toString();
			restore_host = paramMap.get("restore_host").toString();
			restore_port = paramMap.get("restore_port").toString();
			restore_stop_opt = paramMap.get("restore_stop_opt").toString();
			restore_os_user = paramMap.get("restore_os_user").toString();
			restore_os_pwd = paramMap.get("restore_os_pwd").toString();
			restore_db_start_name = paramMap.get("restore_db_start_name").toString();
			restore_tmpdir = paramMap.get("restore_tmpdir").toString();
			restore_decompress_yn = paramMap.get("restore_decompress_yn").toString();
			if (paramMap.get("restore_instance_no") != null) {
				restore_instance_no = paramMap.get("restore_instance_no").toString();
			}
			
			dbchkCmd = paramMap.get("dbchkCmd").toString();
			BIN = paramMap.get("BIN").toString();
			BIN_POS = paramMap.get("BIN_POS").toString();
			
			/*
			if (paramMap.get("os_version") != null) {
				os_version = paramMap.get("os_version").toString();
			}
			*/
			//String msq_clt_utl_pth, String usr, String pw, String ip, String dbport
			boolean gtid = db_version(restore_os_user,restore_os_pwd,restore_user,restore_pass,restore_host,restore_port);
			startMap = new HashMap<String, Object>();
			StringBuilder startCmt = new StringBuilder();
			try {
				startCmt.append(Constants.DATA_NEW_LINE);
				startCmt.append("echo \"=================================================\" > "+log_file_path);
				startCmt.append(Constants.DATA_NEW_LINE);
				startCmt.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE START >> "+log_file_path);
				startCmt.append(Constants.DATA_NEW_LINE);
				startCmt.append("echo \"=================================================\" >> "+log_file_path);
				startCmt.append(Constants.DATA_NEW_LINE);
				log.debug(startCmt);
				startMap = Ssh2Agent.restoreRun(startCmt.toString(), ms_usr, ms_pwd, ms_ip, port);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobProcess startMap Exception", e.fillInStackTrace());
			}
			
			String rpmchkCmd = "sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"if [ \"`rpm -qa | grep -i '^sshpass'`\" == \"\" ];then echo no; else echo ok; fi\"";
			Map<String, Object> rpmchkMap = new HashMap<String, Object>();
			rpmchkMap = Ssh2Agent.getData(rpmchkCmd, ms_usr, ms_pwd, ms_ip, port);
			if (rpmchkMap != null) {
				if ("ok".equals(rpmchkMap.get("value").toString())) {
					
					StringBuilder logCmd = new StringBuilder();
					StringBuilder dbstoptryCmd = new StringBuilder();
					StringBuilder dbrunchkCmd = new StringBuilder();
					String dbrunchkmsg = "";
					
//					String ddataChkCmd = "ssh "+restore_os_user+"@"+restore_host+" \"if [ -d "+restore_dir+" ]; then echo ok; else echo no; fi\"";
					String ddataChkCmd = "sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"if [ -d "+restore_dir+" ]; then echo ok; else echo no; fi\"";
					log.debug(ddataChkCmd);
					
					Map<String, Object> ddataChkMap = new HashMap<String, Object>();
					ddataChkMap = Ssh2Agent.getData(ddataChkCmd, ms_usr, ms_pwd, ms_ip, port);
					if (ddataChkMap != null && "ok".equals(ddataChkMap.get("value").toString()) && !"".equals(restore_dir)) {
						
						String binlogChkCmd = "if [ -x "+msq_clt_utl_pth+"/mysqlbinlog ]; then echo ok; else echo no; fi";
						log.debug(binlogChkCmd);
						
						Map<String, Object> binlogChkMap = new HashMap<String, Object>();
						binlogChkMap = Ssh2Agent.getData(binlogChkCmd, ms_usr, ms_pwd, ms_ip, port);
						if (binlogChkMap != null && "ok".equals(binlogChkMap.get("value").toString())) {
							
//							String utilFileChkCmd = "ssh "+restore_os_user+"@"+restore_host+" \"if [ -x "+xtr_bny_log_pth+"/innobackupex ]; then echo ok; else echo no; fi\"";
							String utilFileChkCmd = "sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"if [ -x "+xtr_bny_log_pth+"/mariabackup ]; then echo ok; else echo no; fi\"";
							log.debug(utilFileChkCmd);
							
							Map<String, Object> utilFileChkMap = new HashMap<String, Object>();
							utilFileChkMap = Ssh2Agent.getData(utilFileChkCmd, ms_usr, ms_pwd, ms_ip, port);
							if (utilFileChkMap != null) {
								if ("ok".equals(utilFileChkMap.get("value").toString())) {
									
//									String initdChkCmd = "ssh "+restore_os_user+"@"+restore_host+" \"if [ -x /etc/init.d/"+restore_db_start_name+" ]; then echo ok; else echo no; fi\"";
									String initdChkCmd = "sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"if [ -x /etc/init.d/"+restore_db_start_name+" ]; then echo ok; else echo no; fi\"";
									log.debug(initdChkCmd);
									Map<String, Object> initdChkMap = new HashMap<String, Object>();
									initdChkMap = Ssh2Agent.getData(initdChkCmd, ms_usr, ms_pwd, ms_ip, port);
									if (initdChkMap != null) {
										if ("ok".equals(initdChkMap.get("value").toString())) {
											
											dbrunchkCmd = new StringBuilder();
											dbrunchkCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" status\"");
											log.debug(dbrunchkCmd);
											dbrunchkMap = new HashMap<String, Object>();
											dbrunchkMap = Ssh2Agent.getList(dbrunchkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
											if (dbrunchkMap != null) {
												
												List<String> dbrunchklist = new ArrayList<String>();
												dbrunchklist = (List) dbrunchkMap.get("list");
												if (dbrunchklist != null && dbrunchklist.size() > 0) {
													dbrunchkmsg = dbrunchklist.get(0).toString();
													if (dbrunchkmsg.matches("(.*)SUCCESS(.*)") || dbrunchkmsg.matches("(.*)OK(.*)")) {
														
														dbstopCmd = new StringBuilder();
														dbstopCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Trying to ShutDown DB ...\"\" >> "+log_file_path);
														dbstopCmd.append(Constants.DATA_NEW_LINE);
														dbstopCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" stop\" 2>> "+err_log_file_path);
														
														log.debug(dbstopCmd);
														
														Map<String, Object> dbstopMap = new HashMap<String, Object>();
														dbstopMap = Ssh2Agent.getList(dbstopCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
														
														if (dbstopMap != null) {
															List<String> list = new ArrayList<String>();
															list = (List) dbstopMap.get("list");
															if (list != null && list.size() > 0) {
																
																String m = list.get(0).toString();
																if (m.matches("(.*)SUCCESS(.*)") || m.matches("(.*)OK(.*)")) {
																	dbstoptryCmd = new StringBuilder();
																	dbstoptryCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"DB ShutDown SUCCESS.\"\" >> "+log_file_path);
																	
																	log.debug(dbstoptryCmd);
																	
																	Ssh2Agent.getData(dbstoptryCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																	
																} else {
																	dbstoptryCmd = new StringBuilder();
																	dbstoptryCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"It is already ShutDown.\"\" >> "+log_file_path);
																	
																	log.debug(dbstoptryCmd);
																	
																	Ssh2Agent.getData(dbstoptryCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																	
																}
															}
														}
													}
												}
											}
												
											//***************************************************************************************************
											// 
											// 2018.05.04 수정사항
											// 1. 엑스트라 풀백업일때는 디비가 죽었는지 살았는지 판단안하고 복구 진행
											// 2. 엑스트라 풀백업이 아닐때는 디비가 살았는지 판단하고 복구 진행
											// 
											//***************************************************************************************************
											if ("L01".equals(lvl)) {
												
												String backup_opt = "";
												/*
												 * 2018.04.19 데이터백업 옵션(Y,N)
												 */
												data_dir = restore_dir;
												if (StringUtils.countMatches(data_dir, "/") > 1) {
													if ((data_dir.lastIndexOf("/")+1) == data_dir.length()) {
														data_dir = data_dir.substring(0, data_dir.length()-1);
													}
												}
												if ("RS02".equals(restore_stop_opt)) {
													StringBuilder dataBakCmd = new StringBuilder();
													dataBakCmd.append("echo \"\" >> "+log_file_path);
													dataBakCmd.append(Constants.DATA_NEW_LINE);
													dataBakCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete target instance backup directory\"\" >> "+log_file_path);
													dataBakCmd.append(Constants.DATA_NEW_LINE);
													dataBakCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"_backup/*\" 2>> "+err_log_file_path);
													dataBakCmd.append(Constants.DATA_NEW_LINE);
													dataBakCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Copy the backup to the target instance backup directory\"\" >> "+log_file_path);
													dataBakCmd.append(Constants.DATA_NEW_LINE);
													dataBakCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"cp -r "+data_dir+"/* "+data_dir+"_backup/\" 2>> "+err_log_file_path);
													
													logCmd = new StringBuilder();
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"rm -rf "+data_dir+"_backup/*\\\"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"_backup/*\" 2>> "+err_log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"cp -r "+data_dir+"/* "+data_dir+"_backup/\\\"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"cp -r "+data_dir+"/* "+data_dir+"_backup/\" 2>> "+err_log_file_path);
													log.debug(logCmd);
													
													Map<String, Object> dataBakMap = new HashMap<String, Object>();
													dataBakMap = Ssh2Agent.getData(dataBakCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
												}
												
												if ("Y".equals(restore_decompress_yn)) {
													
													/* 압축 일때 */
													
													Map<String, Object> cpmMap = new HashMap<String, Object>();
													if (!"".equals(restore_default_file)) {
														backup_opt = "--defaults-file="+restore_default_file;
													}
													
													/****************************************************************************
													 * 2018.04.19 압축일때
													 * **************************************************************************
													 * 1. 데이터 디렉토리 삭제
													 * 2. 백업본 복사 - rsync
													 * **************************************************************************/
													StringBuilder rmDataCmd = new StringBuilder();
													rmDataCmd.append("echo \"\" >> "+log_file_path);
													rmDataCmd.append(Constants.DATA_NEW_LINE);
													rmDataCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete target instance data directory\"\" >> "+log_file_path);
													rmDataCmd.append(Constants.DATA_NEW_LINE);
													rmDataCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"/*\" 2>> "+err_log_file_path);
													rmDataCmd.append(Constants.DATA_NEW_LINE);
													rmDataCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Copy the backup to the target instance data directory\"\" >> "+log_file_path);
													rmDataCmd.append(Constants.DATA_NEW_LINE);
													//rmDataCmd.append("rsync -av --exclude='ginian*.log' "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+" &>> "+log_file_path);
													rmDataCmd.append("rsync -av --exclude='ginian*.log' --rsh=\"sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no\" "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+" &>> "+log_file_path);
													
													logCmd = new StringBuilder();
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"rm -rf "+data_dir+"/*\\\"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"/*\" 2>> "+err_log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: rsync -av --exclude='ginian*.log' "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													//logCmd.append("rsync -av --exclude='ginian*.log' "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+" &>> "+log_file_path);
													logCmd.append("rsync -av --exclude='ginian*.log' --rsh=\"sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no\" "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+" &>> "+log_file_path);
													
													log.debug(logCmd);
													
													Map<String, Object> rmDataMap = new HashMap<String, Object>();
													rmDataMap = Ssh2Agent.getData(rmDataCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													
													/****************************************************************************
													 * 3. 압축해제
													 * **************************************************************************/
													StringBuilder compressOptCmd = new StringBuilder();
													compressOptCmd.append("echo \"\" >> "+log_file_path);
													compressOptCmd.append(Constants.DATA_NEW_LINE);
													compressOptCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Unzip the copied compressed data file\"\" >> "+log_file_path);
													compressOptCmd.append(Constants.DATA_NEW_LINE);
													compressOptCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \""+xtr_bny_log_pth+"/mariabackup "+backup_opt+" --decompress "+data_dir+"\" &>> "+log_file_path);
													compressOptCmd.append(Constants.DATA_NEW_LINE);
													
													logCmd = new StringBuilder();
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t \\\""+restore_os_user+"@"+restore_host+" "+xtr_bny_log_pth+"/mariabackup "+backup_opt+" --decompress "+data_dir+"\\\"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \""+xtr_bny_log_pth+"/mariabackup "+backup_opt+" --decompress "+data_dir+"\" &>> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													log.debug(logCmd);
													
													cpmMap = Ssh2Agent.getData(compressOptCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													
													Map<String, Object> mchkMap = new HashMap<String, Object>();
													StringBuilder mchk = new StringBuilder();
													mchk.append("if [ \"`tail -5 "+log_file_path+" | grep -i \"completed OK\"`\" != '' ]; then echo ok; else echo no; fi");
													
													log.debug(mchk);
													
													mchkMap = new HashMap<String, Object>();
													mchkMap = Ssh2Agent.getData(mchk.toString(), ms_usr, ms_pwd, ms_ip, port);
													if (mchkMap != null) {
														
														if ("ok".equals(mchkMap.get("value").toString())) {
															
															/***************************************************************************
															 * 4. 압축파일 삭제
															 * **************************************************************************/
															StringBuilder applyOptCmd = new StringBuilder();
															applyOptCmd.append("echo \"\" >> "+log_file_path);
															applyOptCmd.append(Constants.DATA_NEW_LINE);
															applyOptCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete compressed data files after successful decompression\"\" >> "+log_file_path);
															applyOptCmd.append(Constants.DATA_NEW_LINE);
															applyOptCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"find "+data_dir+" -name \"*.qp\" -type f -exec rm {} \\;\" 2>> "+err_log_file_path);
															applyOptCmd.append(Constants.DATA_NEW_LINE);
															
															/***************************************************************************
															 * 5. apply log
															 * **************************************************************************/
															applyOptCmd.append("echo \"\" >> "+log_file_path);
															applyOptCmd.append(Constants.DATA_NEW_LINE);
															applyOptCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Execute target-instance data 'apply-log'\"\" >> "+log_file_path);
															applyOptCmd.append(Constants.DATA_NEW_LINE);
															applyOptCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \""+xtr_bny_log_pth+"/mariabackup --prepare --target-dir "+data_dir+"\" &>> "+log_file_path);
															
															logCmd = new StringBuilder();
															logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"find "+data_dir+" -name \\\"*.qp\\\" -type f -exec rm {} \\\\;\\\"\" >> "+log_file_path);
															logCmd.append(Constants.DATA_NEW_LINE);
															logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"find "+data_dir+" -name \"*.qp\" -type f -exec rm {} \\;\" 2>> "+err_log_file_path);
															logCmd.append(Constants.DATA_NEW_LINE);
															logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\""+xtr_bny_log_pth+"/mariabackup --prepare --target-dir "+data_dir+"\\\"\" >> "+log_file_path);
															logCmd.append(Constants.DATA_NEW_LINE);
															logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \""+xtr_bny_log_pth+"/mariabackup --prepare --target-dir "+data_dir+"\" &>> "+log_file_path);
															log.debug(logCmd);
															
															cpmMap = new HashMap<String, Object>();
															cpmMap = Ssh2Agent.getData(applyOptCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
															
															//여기서 체크
															mchk = new StringBuilder();
															mchk.append("if [ \"`tail -5 "+log_file_path+" | grep -i \"completed OK\"`\" != '' ]; then echo ok; else echo no; fi");
															
															log.debug(mchk);
															
															mchkMap = new HashMap<String, Object>();
															mchkMap = Ssh2Agent.getData(mchk.toString(), ms_usr, ms_pwd, ms_ip, port);
															if (mchkMap != null) {
																
																if ("ok".equals(mchkMap.get("value").toString())) {
																	
																	StringBuilder dbStrCmd = new StringBuilder();
																	logCmd = new StringBuilder();
																	if (!"".equals(restore_instance_no)) {
																		dbStrCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Trying to Starting DB...\"\" &>> "+log_file_path);
																		dbStrCmd.append(Constants.DATA_NEW_LINE);
																		dbStrCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" "+restore_instance_no+" 2>> "+err_log_file_path);
																		logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"sudo /etc/init.d/"+restore_db_start_name+" start\\\" "+restore_instance_no+"\" &>> "+log_file_path);
																		logCmd.append(Constants.DATA_NEW_LINE);
																		logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" "+restore_instance_no+" 2>> "+err_log_file_path);
																	} else {
																		dbStrCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Trying to Starting DB...\"\" &>> "+log_file_path);
																		dbStrCmd.append(Constants.DATA_NEW_LINE);
																		dbStrCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" 2>> "+err_log_file_path);
																		logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"sudo /etc/init.d/"+restore_db_start_name+" start\\\"\" &>> "+log_file_path);
																		logCmd.append(Constants.DATA_NEW_LINE);
																		logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" 2>> "+err_log_file_path);
																	}
																	log.debug(logCmd);
																	
																	dbStrMap = new HashMap<String, Object>();
																	dbStrMap = Ssh2Agent.getData(dbStrCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																	Thread.sleep(10000);
																	
																	dbrunchkMap = new HashMap<String, Object>();
																	dbrunchkCmd = new StringBuilder();
																	dbrunchkCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" status\"");
																	
																	log.debug(dbrunchkCmd);
																	
																	dbrunchkMap = Ssh2Agent.getList(dbrunchkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																	if (dbrunchkMap != null) {
																		List<String> dbrunchklist = new ArrayList<String>();
																		dbrunchklist = (List) dbrunchkMap.get("list");
																		if (dbrunchklist != null && dbrunchklist.size() > 0) {
																			dbrunchkmsg = dbrunchklist.get(0).toString();
																			if (dbrunchkmsg.matches("(.*)SUCCESS(.*)") || dbrunchkmsg.matches("(.*)OK(.*)")) {
																				
																				StringBuilder completeCmd = new StringBuilder();
																				
																				String errLogCmd = "if [ \"`cat "+err_log_file_path+" | grep -i error | grep -v statements_with_errors`\" != '' ] || [ \"`cat "+log_file_path+" | grep -i error | grep -v statements_with_errors`\" != '' ]; then echo no; else echo ok; fi";
																				
																				log.debug(errLogCmd);
																				
																				logCmd = new StringBuilder();
																				
																				Map<String, Object> errCatMap = new HashMap<String, Object>();
																				errCatMap = Ssh2Agent.getData(errLogCmd, ms_usr, ms_pwd, ms_ip, port);
																				if (errCatMap.get("value") != null) {
																					if ("ok".equals(errCatMap.get("value").toString())) {
																						StringBuilder flushCmd = new StringBuilder();
																						flushCmd.append("echo \"\" >> "+log_file_path);
																						flushCmd.append(Constants.DATA_NEW_LINE);
																						flushCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Execute flush logs\"\" >> "+log_file_path);
																						flushCmd.append(Constants.DATA_NEW_LINE);
																						flushCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
																						
																						logCmd = new StringBuilder();
																						logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p -h "+restore_host+" -P "+restore_port+" -e\\\"flush logs;\\\"\" >> "+log_file_path);
																						logCmd.append(Constants.DATA_NEW_LINE);
																						logCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
																						log.debug(logCmd);
																						
																						Ssh2Agent.getData(flushCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																						
																						long endTime = System.currentTimeMillis();
																						completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo Result : SUCCESS >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																						
																					} else {
																						
																						long endTime = System.currentTimeMillis();
																						completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo Result : FAIL >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																					}
																				}
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
																				
																				log.debug(completeCmd);
																				
																				completeMap = new HashMap<String, Object>();
																				completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																				
																			} else {
																				
																				Map<String, Object> lstDbChkMap = new HashMap<String, Object>();
																				StringBuilder cmd = new StringBuilder();
																				cmd.append(Constants.DATA_NEW_LINE);
																				cmd.append("echo '' >> "+log_file_path);
																				cmd.append(Constants.DATA_NEW_LINE);
																				cmd.append("echo 'DataBase Startup Failure!' >> "+log_file_path);
																				cmd.append(Constants.DATA_NEW_LINE);
																				cmd.append("echo '' >> "+log_file_path);
																				
																				log.debug(cmd);
																				
																				lstDbChkMap = Ssh2Agent.getData(cmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																				
																				long endTime = System.currentTimeMillis();
																				StringBuilder completeCmd = new StringBuilder();
																				completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("echo Result : FAIL >> "+log_file_path);
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
																				completeMap = new HashMap<String, Object>();
																				
																				log.debug(completeCmd);
																				
																				completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																			}
																		}
																	}
																	
																} else {
																	dbstopCmd = new StringBuilder();
																	dbstopCmd.append("echo \"An error occurred during Apply Log !!\" >> "+log_file_path);
																	dbstopCmd.append(Constants.DATA_NEW_LINE);
																	dbstopCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
																	
																	log.debug(dbstopCmd);
																	
																	Ssh2Agent.getData(dbstopCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																	
																	long endTime = System.currentTimeMillis();
																	StringBuilder completeCmd = new StringBuilder();
																	completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																	completeCmd.append(Constants.DATA_NEW_LINE);
																	completeCmd.append("echo Result : FAIL >> "+log_file_path);
																	completeCmd.append(Constants.DATA_NEW_LINE);
																	completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																	completeCmd.append(Constants.DATA_NEW_LINE);
																	completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																	completeCmd.append(Constants.DATA_NEW_LINE);
																	completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																	completeCmd.append(Constants.DATA_NEW_LINE);
																	completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
																	
																	log.debug(completeCmd);
																	
																	completeMap = new HashMap<String, Object>();
																	completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																	
																}
															}
															
															
														} else {
															dbstopCmd = new StringBuilder();
															dbstopCmd.append("echo \"An error occurred while decompressing !!\" >> "+log_file_path);
															dbstopCmd.append(Constants.DATA_NEW_LINE);
															dbstopCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
															
															log.debug(dbstopCmd);
															
															Ssh2Agent.getData(dbstopCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
															
															long endTime = System.currentTimeMillis();
															StringBuilder completeCmd = new StringBuilder();
															completeCmd.append("echo \"=================================================\" >> "+log_file_path);
															completeCmd.append(Constants.DATA_NEW_LINE);
															completeCmd.append("echo Result : FAIL >> "+log_file_path);
															completeCmd.append(Constants.DATA_NEW_LINE);
															completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
															completeCmd.append(Constants.DATA_NEW_LINE);
															completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
															completeCmd.append(Constants.DATA_NEW_LINE);
															completeCmd.append("echo \"=================================================\" >> "+log_file_path);
															completeCmd.append(Constants.DATA_NEW_LINE);
															completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
															
															log.debug(completeCmd);
															
															completeMap = new HashMap<String, Object>();
															completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
															
														}
													}
													/*//압축 일때 */
													
												} else {
													
													/* 압축이 아닐때 */
													StringBuilder tgchkCmd = new StringBuilder();
													tgchkCmd.append("echo \"\" >> "+log_file_path);
													tgchkCmd.append(Constants.DATA_NEW_LINE);
													tgchkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete target instance data directory\"\" >> "+log_file_path);
													tgchkCmd.append(Constants.DATA_NEW_LINE);
													tgchkCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"/*\" 2>> "+err_log_file_path);
													tgchkCmd.append(Constants.DATA_NEW_LINE);
													tgchkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Copy the backup to the target instance data directory\"\" >> "+log_file_path);
													tgchkCmd.append(Constants.DATA_NEW_LINE);
													//tgchkCmd.append("rsync -av --exclude='ginian*.log' "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+" &>> "+log_file_path);
													tgchkCmd.append("rsync -av --exclude='ginian*.log' --rsh=\"sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no\" "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+" &>> "+log_file_path);
													
													logCmd = new StringBuilder();
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"rm -rf "+data_dir+"/*\\\"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"/*\" 2>> "+err_log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: rsync -av --exclude='ginian*.log' "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("rsync -av --exclude='ginian*.log' --rsh=\"sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no\" "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+" &>> "+log_file_path);
													log.debug(logCmd);
													
													tgchkMap = new HashMap<String, Object>();
													tgchkMap = Ssh2Agent.getData(tgchkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													
													StringBuilder dbStrCmd = new StringBuilder();
													logCmd = new StringBuilder();
													if (!"".equals(restore_instance_no)) {
														dbStrCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Trying to Starting DB...\"\" &>> "+log_file_path);
														dbStrCmd.append(Constants.DATA_NEW_LINE);
														dbStrCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" "+restore_instance_no+" 2>> "+err_log_file_path);
														logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"sudo /etc/init.d/"+restore_db_start_name+" start\\\" "+restore_instance_no+"\" &>> "+log_file_path);
														logCmd.append(Constants.DATA_NEW_LINE);
														logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" "+restore_instance_no+" 2>> "+err_log_file_path);
													} else {
														dbStrCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Trying to Starting DB...\"\" &>> "+log_file_path);
														dbStrCmd.append(Constants.DATA_NEW_LINE);
														dbStrCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" 2>> "+err_log_file_path);
														logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"sudo /etc/init.d/"+restore_db_start_name+" start\\\"\" &>> "+log_file_path);
														logCmd.append(Constants.DATA_NEW_LINE);
														logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" 2>> "+err_log_file_path);
													}
													log.debug(logCmd);
													
													dbStrMap = new HashMap<String, Object>();
													dbStrMap = Ssh2Agent.getData(dbStrCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													Thread.sleep(10000);
													
													dbrunchkMap = new HashMap<String, Object>();
													dbrunchkCmd = new StringBuilder();
													dbrunchkCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" status\"");
													
													log.debug(dbrunchkCmd);
													
													dbrunchkMap = Ssh2Agent.getList(dbrunchkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													
													if (dbrunchkMap != null) {
														List<String> dbrunchklist = new ArrayList<String>();
														dbrunchklist = (List) dbrunchkMap.get("list");
														if (dbrunchklist != null && dbrunchklist.size() > 0) {
															dbrunchkmsg = dbrunchklist.get(0).toString();
															if (dbrunchkmsg.matches("(.*)SUCCESS(.*)") || dbrunchkmsg.matches("(.*)OK(.*)")) {
																
																StringBuilder completeCmd = new StringBuilder();
																
																String errLogCmd = "if [ \"`cat "+err_log_file_path+" | grep -i error | grep -v statements_with_errors`\" != '' ] || [ \"`cat "+log_file_path+" | grep -i error | grep -v statements_with_errors`\" != '' ]; then echo no; else echo ok; fi";
																
																log.debug(errLogCmd);
																
																logCmd = new StringBuilder();
																
																Map<String, Object> errCatMap = new HashMap<String, Object>();
																errCatMap = Ssh2Agent.getData(errLogCmd, ms_usr, ms_pwd, ms_ip, port);
																if (errCatMap.get("value") != null) {
																	if ("ok".equals(errCatMap.get("value").toString())) {
																		StringBuilder flushCmd = new StringBuilder();
																		flushCmd.append("echo \"\" >> "+log_file_path);
																		flushCmd.append(Constants.DATA_NEW_LINE);
																		flushCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Execute flush logs\"\" >> "+log_file_path);
																		flushCmd.append(Constants.DATA_NEW_LINE);
																		flushCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
																		
																		logCmd = new StringBuilder();
																		logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p -h "+restore_host+" -P "+restore_port+" -e\\\"flush logs;\\\"\" >> "+log_file_path);
																		logCmd.append(Constants.DATA_NEW_LINE);
																		logCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
																		log.debug(logCmd);
																		
																		Ssh2Agent.getData(flushCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																		
																		long endTime = System.currentTimeMillis();
																		completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo Result : SUCCESS >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																	} else {
																		
																		long endTime = System.currentTimeMillis();
																		completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo Result : FAIL >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																	}
																}
																
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
																
																log.debug(completeCmd);
																
																completeMap = new HashMap<String, Object>();
																completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																
															} else {
																Map<String, Object> lstDbChkMap = new HashMap<String, Object>();
																StringBuilder cmd = new StringBuilder();
																cmd.append("echo '' >> "+log_file_path);
																cmd.append(Constants.DATA_NEW_LINE);
																cmd.append("echo 'DataBase Startup Failure!' >> "+log_file_path);
																cmd.append(Constants.DATA_NEW_LINE);
																cmd.append("echo '' >> "+log_file_path);
																log.debug(cmd);
																
																lstDbChkMap = Ssh2Agent.getData(cmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																
																long endTime = System.currentTimeMillis();
																StringBuilder completeCmd = new StringBuilder();
																completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo Result : FAIL >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
																log.debug(completeCmd);
																completeMap = new HashMap<String, Object>();
																completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
															}
														}
													}
													/*//압축이 아닐때 */
												}
												
											} else if ("L02".equals(lvl)) {
												
												String backup_opt = "";
												/*
												 * 2018.04.19 데이터백업 옵션(Y,N)
												 */
												data_dir = restore_dir;
												if (StringUtils.countMatches(data_dir, "/") > 1) {
													if ((data_dir.lastIndexOf("/")+1) == data_dir.length()) {
														data_dir = data_dir.substring(0, data_dir.length()-1);
													}
												}
												if ("RS02".equals(restore_stop_opt)) {
													StringBuilder dataBakCmd = new StringBuilder();
													dataBakCmd.append("echo \"\" >> "+log_file_path);
													dataBakCmd.append(Constants.DATA_NEW_LINE);
													dataBakCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete target instance backup directory\"\" >> "+log_file_path);
													dataBakCmd.append(Constants.DATA_NEW_LINE);
													dataBakCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"_backup/*\" 2>> "+err_log_file_path);
													dataBakCmd.append(Constants.DATA_NEW_LINE);
													dataBakCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Copy the backup to the target instance backup directory\"\" >> "+log_file_path);
													dataBakCmd.append(Constants.DATA_NEW_LINE);
													dataBakCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"cp -r "+data_dir+"/* "+data_dir+"_backup/\" 2>> "+err_log_file_path);
													
													logCmd = new StringBuilder();
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"rm -rf "+data_dir+"_backup/*\\\"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"_backup/*\" 2>> "+err_log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"cp -r "+data_dir+"/* "+data_dir+"_backup/\\\"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"cp -r "+data_dir+"/* "+data_dir+"_backup/\" 2>> "+err_log_file_path);
													log.debug(logCmd);
													
													Map<String, Object> dataBakMap = new HashMap<String, Object>();
													dataBakMap = Ssh2Agent.getData(dataBakCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
												}
												
												if ("Y".equals(restore_decompress_yn)) {
													
													/* 압축 일때 */
													
													Map<String, Object> cpmMap = new HashMap<String, Object>();
													if (!"".equals(restore_default_file)) {
														backup_opt = "--defaults-file="+restore_default_file;
													}
													
													/****************************************************************************
													 * 2018.04.19 압축일때
													 * **************************************************************************
													 * 1. 데이터 디렉토리 삭제
													 * 2. 백업본 복사 - rsync
													 * **************************************************************************/
													StringBuilder rmDataCmd = new StringBuilder();
													rmDataCmd.append("echo \"\" >> "+log_file_path);
													rmDataCmd.append(Constants.DATA_NEW_LINE);
													rmDataCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete target instance data directory\"\" >> "+log_file_path);
													rmDataCmd.append(Constants.DATA_NEW_LINE);
													rmDataCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"/*\" 2>> "+err_log_file_path);
													rmDataCmd.append(Constants.DATA_NEW_LINE);
													rmDataCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Copy the backup to the target instance data directory\"\" >> "+log_file_path);
													rmDataCmd.append(Constants.DATA_NEW_LINE);
													rmDataCmd.append("rsync -av --exclude='ginian*.log' --rsh=\"sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no\" "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+" &>> "+log_file_path);
													
													logCmd = new StringBuilder();
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"rm -rf "+data_dir+"/*\\\"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"/*\" 2>> "+err_log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: rsync -av --exclude='ginian*.log' "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("rsync -av --exclude='ginian*.log' --rsh=\"sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no\" "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+" &>> "+log_file_path);
													
													log.debug(logCmd);
													
													Map<String, Object> rmDataMap = new HashMap<String, Object>();
													rmDataMap = Ssh2Agent.getData(rmDataCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													
													/****************************************************************************
													 * 3. 압축해제
													 * **************************************************************************/
													StringBuilder compressOptCmd = new StringBuilder();
													compressOptCmd.append("echo \"\" >> "+log_file_path);
													compressOptCmd.append(Constants.DATA_NEW_LINE);
													compressOptCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Unzip the copied compressed data file\"\" >> "+log_file_path);
													compressOptCmd.append(Constants.DATA_NEW_LINE);
													compressOptCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \""+xtr_bny_log_pth+"/mariabackup "+backup_opt+" --decompress "+data_dir+"\" &>> "+log_file_path);
													compressOptCmd.append(Constants.DATA_NEW_LINE);
													
													logCmd = new StringBuilder();
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t \\\""+restore_os_user+"@"+restore_host+" "+xtr_bny_log_pth+"/mariabackup "+backup_opt+" --decompress "+data_dir+"\\\"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \""+xtr_bny_log_pth+"/mariabackup "+backup_opt+" --decompress "+data_dir+"\" &>> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													log.debug(logCmd);
													
													cpmMap = Ssh2Agent.getData(compressOptCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													
													Map<String, Object> mchkMap = new HashMap<String, Object>();
													StringBuilder mchk = new StringBuilder();
													mchk.append("if [ \"`tail -5 "+log_file_path+" | grep -i \"completed OK\"`\" != '' ]; then echo ok; else echo no; fi");
													log.debug(mchk);
													
													mchkMap = new HashMap<String, Object>();
													mchkMap = Ssh2Agent.getData(mchk.toString(), ms_usr, ms_pwd, ms_ip, port);
													if (mchkMap != null) {
														
														if ("ok".equals(mchkMap.get("value").toString())) {
															
															/***************************************************************************
															 * 4. 압축파일 삭제
															 * **************************************************************************/
															StringBuilder applyOptCmd = new StringBuilder();
															applyOptCmd.append("echo \"\" >> "+log_file_path);
															applyOptCmd.append(Constants.DATA_NEW_LINE);
															applyOptCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete compressed data files after successful decompression\"\" >> "+log_file_path);
															applyOptCmd.append(Constants.DATA_NEW_LINE);
															applyOptCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"find "+data_dir+" -name \"*.qp\" -type f -exec rm {} \\;\" 2>> "+err_log_file_path);
															applyOptCmd.append(Constants.DATA_NEW_LINE);
															
															/***************************************************************************
															 * 5. apply log
															 * **************************************************************************/
															applyOptCmd.append("echo \"\" >> "+log_file_path);
															applyOptCmd.append(Constants.DATA_NEW_LINE);
															applyOptCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Execute target-instance data 'apply-log'\"\" >> "+log_file_path);
															applyOptCmd.append(Constants.DATA_NEW_LINE);
															applyOptCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \""+xtr_bny_log_pth+"/mariabackup --prepare --target-dir "+data_dir+"\" &>> "+log_file_path);
															
															logCmd = new StringBuilder();
															logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"find "+data_dir+" -name \\\"*.qp\\\" -type f -exec rm {} \\\\;\\\"\" >> "+log_file_path);
															logCmd.append(Constants.DATA_NEW_LINE);
															logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"find "+data_dir+" -name \"*.qp\" -type f -exec rm {} \\;\" 2>> "+err_log_file_path);
															logCmd.append(Constants.DATA_NEW_LINE);
															logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\""+xtr_bny_log_pth+"/mariabackup --prepare --target-dir "+data_dir+"\\\"\" >> "+log_file_path);
															logCmd.append(Constants.DATA_NEW_LINE);
															logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \""+xtr_bny_log_pth+"/mariabackup --prepare --target-dir "+data_dir+"\" &>> "+log_file_path);
															log.debug(logCmd);
															
															cpmMap = new HashMap<String, Object>();
															cpmMap = Ssh2Agent.getData(applyOptCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
															
															//여기서 체크
															mchk = new StringBuilder();
															mchk.append("if [ \"`tail -5 "+log_file_path+" | grep -i \"completed OK\"`\" != '' ]; then echo ok; else echo no; fi");
															log.debug(mchk);
															
															mchkMap = new HashMap<String, Object>();
															mchkMap = Ssh2Agent.getData(mchk.toString(), ms_usr, ms_pwd, ms_ip, port);
															if (mchkMap != null) {
																
																if ("ok".equals(mchkMap.get("value").toString())) {
																	
																	//********************************************
																	//증분체크
																	//********************************************
																	StringBuilder increChkCmd = new StringBuilder();
																	increChkCmd.append("echo \"\" >> "+log_file_path);
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Create incremental temporary file\"\" >> "+log_file_path);
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append(BIN);
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append(BIN_POS);
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("for i in `ls "+backup_pdir+" | grep -v \"last_*\" | grep -v \"manual\"`");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("do");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("if [ $i -gt "+lst_full_wrk_dt+" ] && [ $i -le "+wrk_dt+" ]; then");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	
																	increChkCmd.append("if [ \"`echo $BIN`\" != \"\" ]; then ");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("BIN_CUT=`echo $BIN | cut -d\".\" -f1`");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("BIN_FIRST=`ls "+backup_pdir+"/$i/* | grep \"$BIN\"`");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\" | grep -v \"$BIN\"`");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("else");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("BIN_FIRST=''");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\"`");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("fi");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	
																	increChkCmd.append("if [ ! -z \"$BIN_FIRST\" ]; then ");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	//increChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"--start-position=$BIN_POS $BIN_FIRST >> "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
																	increChkCmd.append(msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"--start-position=$BIN_POS $BIN_FIRST >> "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("fi");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	
																	increChkCmd.append("if [ ! -z \"$BIN_LOG\" ]; then ");
																	//increChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append(msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("fi");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("fi");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("done");
																	increChkCmd.append(Constants.DATA_NEW_LINE);
																	increChkCmd.append("sed -ie \"s/, @@session.check_constraint_checks=1//g\" "+restore_tmpdir+"/"+wrk_dt+".sql");
																	
																	logCmd = new StringBuilder();
																	logCmd.append(BIN);
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append(BIN_POS);
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("for i in `ls "+backup_pdir+" | grep -v \"last_*\" | grep -v \"manual\"`");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("do");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("if [ $i -gt "+lst_full_wrk_dt+" ] && [ $i -le "+wrk_dt+" ]; then");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("if [ \"`echo $BIN`\" != \"\" ]; then ");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("BIN_CUT=`echo $BIN | cut -d\".\" -f1`");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("BIN_FIRST=`ls "+backup_pdir+"/$i/* | grep \"$BIN\"`");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\" | grep -v \"$BIN\"`");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("else");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("BIN_FIRST=''");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\"`");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("fi");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("if [ ! -z \"$BIN_FIRST\" ]; then ");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"--start-position=$BIN_POS $BIN_FIRST >> "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append(msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"--start-position=$BIN_POS $BIN_FIRST >> "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("fi");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("if [ ! -z \"$BIN_LOG\" ]; then ");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append(msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("fi");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("fi");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("done");
																	logCmd.append(Constants.DATA_NEW_LINE);
																	logCmd.append("sed -ie \"s/, @@session.check_constraint_checks=1//g\" "+restore_tmpdir+"/"+wrk_dt+".sql");
																	log.debug(logCmd);
																	
																	Map<String, Object> increChkMap = new HashMap<String, Object>();
																	increChkMap = Ssh2Agent.getData(increChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																	//********************************************
																	// //증분체크
																	//********************************************
																	
																	StringBuilder dbStrCmd = new StringBuilder();
																	logCmd = new StringBuilder();
																	if (!"".equals(restore_instance_no)) {
																		dbStrCmd.append("echo \"\" >> "+log_file_path);
																		dbStrCmd.append(Constants.DATA_NEW_LINE);
																		dbStrCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Trying to Starting DB...\"\" &>> "+log_file_path);
																		dbStrCmd.append(Constants.DATA_NEW_LINE);
																		dbStrCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" "+restore_instance_no+" 2>> "+err_log_file_path);
																		logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"sudo /etc/init.d/"+restore_db_start_name+" start\\\" "+restore_instance_no+"\" &>> "+log_file_path);
																		logCmd.append(Constants.DATA_NEW_LINE);
																		logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" "+restore_instance_no+" 2>> "+err_log_file_path);
																	} else {
																		dbStrCmd.append("echo \"\" >> "+log_file_path);
																		dbStrCmd.append(Constants.DATA_NEW_LINE);
																		dbStrCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Trying to Starting DB...\"\" &>> "+log_file_path);
																		dbStrCmd.append(Constants.DATA_NEW_LINE);
																		dbStrCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" 2>> "+err_log_file_path);
																		logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"sudo /etc/init.d/"+restore_db_start_name+" start\\\"\" &>> "+log_file_path);
																		logCmd.append(Constants.DATA_NEW_LINE);
																		logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" 2>> "+err_log_file_path);
																	}
																	log.debug(logCmd);
																	
																	dbStrMap = new HashMap<String, Object>();
																	dbStrMap = Ssh2Agent.getData(dbStrCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																	Thread.sleep(10000);
																	
																	dbrunchkCmd = new StringBuilder();
																	dbrunchkCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" status\"");
																	log.debug(dbrunchkCmd);
																	dbrunchkMap = new HashMap<String, Object>();
																	dbrunchkMap = Ssh2Agent.getList(dbrunchkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																	if (dbrunchkMap != null) {
																		List<String> dbrunchklist = new ArrayList<String>();
																		dbrunchklist = (List) dbrunchkMap.get("list");
																		if (dbrunchklist != null && dbrunchklist.size() > 0) {
																			dbrunchkmsg = dbrunchklist.get(0).toString();
																			if (dbrunchkmsg.matches("(.*)SUCCESS(.*)") || dbrunchkmsg.matches("(.*)OK(.*)")) {
																				
																				StringBuilder completeCmd = new StringBuilder();
																				
																				StringBuilder fileChkCmd = new StringBuilder();
																				fileChkCmd.append("if [ -f "+restore_tmpdir+"/"+wrk_dt+".sql ]; then ");
																				fileChkCmd.append(Constants.DATA_NEW_LINE);
																				fileChkCmd.append("echo \"\" >> "+log_file_path);
																				fileChkCmd.append(Constants.DATA_NEW_LINE);
																				fileChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Apply incremental temporary file generated\"\" >> "+log_file_path);
																				fileChkCmd.append(Constants.DATA_NEW_LINE);
																				fileChkCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" --init-command=\"set sql_log_bin=0;\" < "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
																				fileChkCmd.append(Constants.DATA_NEW_LINE);
																				fileChkCmd.append("echo \"\" >> "+log_file_path);
																				fileChkCmd.append(Constants.DATA_NEW_LINE);
																				fileChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete Temporary File\"\" >> "+log_file_path);
																				fileChkCmd.append(Constants.DATA_NEW_LINE);
																				fileChkCmd.append("rm -rf "+restore_tmpdir+"/"+wrk_dt+".sql");
																				fileChkCmd.append(Constants.DATA_NEW_LINE);
																				fileChkCmd.append("fi");
																				
																				logCmd = new StringBuilder();
																				logCmd.append("if [ -f "+restore_tmpdir+"/"+wrk_dt+".sql ]; then ");
																				logCmd.append(Constants.DATA_NEW_LINE);
																				logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p -h "+restore_host+" -P "+restore_port+" --init-command=\\\"set sql_log_bin=0;\\\" < "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
																				logCmd.append(Constants.DATA_NEW_LINE);
																				logCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" --init-command=\"set sql_log_bin=0;\" < "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
																				logCmd.append(Constants.DATA_NEW_LINE);
																				logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: rm -rf "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
																				logCmd.append(Constants.DATA_NEW_LINE);
																				logCmd.append("rm -rf "+restore_tmpdir+"/"+wrk_dt+".sql");
																				logCmd.append(Constants.DATA_NEW_LINE);
																				logCmd.append("fi");
																				log.debug(logCmd);
																				
																				Map<String, Object> fileChkMap = new HashMap<String, Object>();
																				fileChkMap = Ssh2Agent.getData(fileChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																				
																				String errLogCmd = "if [ \"`cat "+err_log_file_path+" | grep -i error | grep -v statements_with_errors`\" != '' ] || [ \"`cat "+log_file_path+" | grep -i error | grep -v statements_with_errors`\" != '' ]; then echo no; else echo ok; fi";
																				log.debug(errLogCmd);
																				Map<String, Object> errCatMap = new HashMap<String, Object>();
																				errCatMap = Ssh2Agent.getData(errLogCmd, ms_usr, ms_pwd, ms_ip, port);
																				
																				if (errCatMap.get("value") != null) {
																					if ("ok".equals(errCatMap.get("value").toString())) {
																						StringBuilder flushCmd = new StringBuilder();
																						flushCmd.append("echo \"\" >> "+log_file_path);
																						flushCmd.append(Constants.DATA_NEW_LINE);
																						flushCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Execute flush logs\"\" >> "+log_file_path);
																						flushCmd.append(Constants.DATA_NEW_LINE);
																						flushCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
																						
																						logCmd = new StringBuilder();
																						logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p -h "+restore_host+" -P "+restore_port+" -e\\\"flush logs;\\\"\" >> "+log_file_path);
																						logCmd.append(Constants.DATA_NEW_LINE);
																						logCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
																						log.debug(logCmd);
																						
																						Ssh2Agent.getData(flushCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																						
																						long endTime = System.currentTimeMillis();
																						completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo Result : SUCCESS >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																						
																					} else {
																						long endTime = System.currentTimeMillis();
																						completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo Result : FAIL >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																						completeCmd.append(Constants.DATA_NEW_LINE);
																						completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																					}
																				}
																				
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
																				log.debug(completeCmd);
																				completeMap = new HashMap<String, Object>();
																				completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																				
																			} else {
																				
																				Map<String, Object> lstDbChkMap = new HashMap<String, Object>();
																				StringBuilder cmd = new StringBuilder();
																				cmd.append("echo '' >> "+log_file_path);
																				cmd.append(Constants.DATA_NEW_LINE);
																				cmd.append("echo 'Please check your DB account.' >> "+log_file_path);
																				cmd.append(Constants.DATA_NEW_LINE);
																				cmd.append("echo 'DataBase Startup Failure!' >> "+log_file_path);
																				cmd.append(Constants.DATA_NEW_LINE);
																				cmd.append("echo '' >> "+log_file_path);
																				log.debug(cmd);
																				lstDbChkMap = Ssh2Agent.getData(cmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																				
																				long endTime = System.currentTimeMillis();
																				StringBuilder completeCmd = new StringBuilder();
																				completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("echo Result : FAIL >> "+log_file_path);
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																				completeCmd.append(Constants.DATA_NEW_LINE);
																				completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
																				completeMap = new HashMap<String, Object>();
																				log.debug(completeCmd);
																				completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																				
																			}
																		}
																	}
																	
																} else {
																	dbstopCmd = new StringBuilder();
																	dbstopCmd.append("echo \"An error occurred during Apply Log !!\" >> "+log_file_path);
																	dbstopCmd.append(Constants.DATA_NEW_LINE);
																	dbstopCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
																	log.debug(dbstopCmd);
																	Ssh2Agent.getData(dbstopCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																	
																	long endTime = System.currentTimeMillis();
																	StringBuilder completeCmd = new StringBuilder();
																	completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																	completeCmd.append(Constants.DATA_NEW_LINE);
																	completeCmd.append("echo Result : FAIL >> "+log_file_path);
																	completeCmd.append(Constants.DATA_NEW_LINE);
																	completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																	completeCmd.append(Constants.DATA_NEW_LINE);
																	completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																	completeCmd.append(Constants.DATA_NEW_LINE);
																	completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																	completeCmd.append(Constants.DATA_NEW_LINE);
																	completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
																	completeMap = new HashMap<String, Object>();
																	log.debug(completeCmd);
																	completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																	
																}
															}
															
															
														} else {
															dbstopCmd = new StringBuilder();
															dbstopCmd.append("echo \"An error occurred while decompressing !!\" >> "+log_file_path);
															dbstopCmd.append(Constants.DATA_NEW_LINE);
															dbstopCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
															log.debug(dbstopCmd);
															Ssh2Agent.getData(dbstopCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
															
															long endTime = System.currentTimeMillis();
															StringBuilder completeCmd = new StringBuilder();
															completeCmd.append("echo \"=================================================\" >> "+log_file_path);
															completeCmd.append(Constants.DATA_NEW_LINE);
															completeCmd.append("echo Result : FAIL >> "+log_file_path);
															completeCmd.append(Constants.DATA_NEW_LINE);
															completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
															completeCmd.append(Constants.DATA_NEW_LINE);
															completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
															completeCmd.append(Constants.DATA_NEW_LINE);
															completeCmd.append("echo \"=================================================\" >> "+log_file_path);
															completeCmd.append(Constants.DATA_NEW_LINE);
															completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
															completeMap = new HashMap<String, Object>();
															log.debug(completeCmd);
															completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
															
														}
													}
													/*//압축 일때 */
													
												} else {
													
													/* 압축이 아닐때 */
													StringBuilder tgchkCmd = new StringBuilder();
													tgchkCmd.append("echo \"\" >> "+log_file_path);
													tgchkCmd.append(Constants.DATA_NEW_LINE);
													tgchkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete target instance data directory\"\" >> "+log_file_path);
													tgchkCmd.append(Constants.DATA_NEW_LINE);
													tgchkCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"/*\" 2>> "+err_log_file_path);
													tgchkCmd.append(Constants.DATA_NEW_LINE);
													tgchkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Copy the backup to the target instance data directory\"\" >> "+log_file_path);
													tgchkCmd.append(Constants.DATA_NEW_LINE);
													tgchkCmd.append("rsync -av --exclude='ginian*.log' --rsh=\"sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no\" "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+" &>> "+log_file_path);
													
													logCmd = new StringBuilder();
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"rm -rf "+data_dir+"/*\\\"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"/*\" 2>> "+err_log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: rsync -av --exclude='ginian*.log' "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+"\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("rsync -av --exclude='ginian*.log' --rsh=\"sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no\" "+lst_full_dir+"/* "+restore_os_user+"@"+restore_host+":"+data_dir+" &>> "+log_file_path);
													log.debug(logCmd);
													
													tgchkMap = new HashMap<String, Object>();
													tgchkMap = Ssh2Agent.getData(tgchkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													
													//********************************************
													//증분체크
													//********************************************
													StringBuilder increChkCmd = new StringBuilder();
													increChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Create incremental temporary file\"\" >> "+log_file_path);
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append(BIN);
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append(BIN_POS);
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("for i in `ls "+backup_pdir+" | grep -v \"last_*\" | grep -v \"manual\"`");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("do");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("if [ $i -gt "+lst_full_wrk_dt+" ] && [ $i -le "+wrk_dt+" ]; then");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													
													increChkCmd.append("if [ \"`echo $BIN`\" != \"\" ]; then ");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("BIN_CUT=`echo $BIN | cut -d\".\" -f1`");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("BIN_FIRST=`ls "+backup_pdir+"/$i/* | grep \"$BIN\"`");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\" | grep -v \"$BIN\"`");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("else");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("BIN_FIRST=''");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\"`");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("fi");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													
													increChkCmd.append("if [ ! -z \"$BIN_FIRST\" ]; then ");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													//increChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"--start-position=$BIN_POS $BIN_FIRST >> "+restore_tmpdir+"/"+wrk_dt+".sql\" &>> "+log_file_path);
													increChkCmd.append(msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"--start-position=$BIN_POS $BIN_FIRST >> "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("fi");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													
													increChkCmd.append("if [ ! -z \"$BIN_LOG\" ]; then ");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													//increChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql\" &>> "+log_file_path);
													increChkCmd.append(msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("fi");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("fi");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("done");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("sed -ie \"s/, @@session.check_constraint_checks=1//g\" "+restore_tmpdir+"/"+wrk_dt+".sql");
													
													logCmd = new StringBuilder();
													logCmd.append(BIN);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append(BIN_POS);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("for i in `ls "+backup_pdir+" | grep -v \"last_*\" | grep -v \"manual\"`");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("do");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("if [ $i -gt "+lst_full_wrk_dt+" ] && [ $i -le "+wrk_dt+" ]; then");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("if [ \"`echo $BIN`\" != \"\" ]; then ");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("BIN_CUT=`echo $BIN | cut -d\".\" -f1`");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("BIN_FIRST=`ls "+backup_pdir+"/$i/* | grep \"$BIN\"`");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\" | grep -v \"$BIN\"`");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("else");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("BIN_FIRST=''");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\"`");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("fi");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("if [ ! -z \"$BIN_FIRST\" ]; then ");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"--start-position=$BIN_POS $BIN_FIRST >> "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append(msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"--start-position=$BIN_POS $BIN_FIRST >> "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("fi");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("if [ ! -z \"$BIN_LOG\" ]; then ");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append(msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("fi");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("fi");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("done");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sed -ie \"s/, @@session.check_constraint_checks=1//g\" "+restore_tmpdir+"/"+wrk_dt+".sql");
													log.debug(logCmd);
													
													Map<String, Object> increChkMap = new HashMap<String, Object>();
													increChkMap = Ssh2Agent.getData(increChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													//********************************************
													// //증분체크
													//********************************************
														
													StringBuilder dbStrCmd = new StringBuilder();
													logCmd = new StringBuilder();
													if (!"".equals(restore_instance_no)) {
														dbStrCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Trying to Starting DB...\"\" &>> "+log_file_path);
														dbStrCmd.append(Constants.DATA_NEW_LINE);
														dbStrCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" "+restore_instance_no+" 2>> "+err_log_file_path);
														logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"sudo /etc/init.d/"+restore_db_start_name+" start\\\" "+restore_instance_no+"\" &>> "+log_file_path);
														logCmd.append(Constants.DATA_NEW_LINE);
														logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" "+restore_instance_no+" 2>> "+err_log_file_path);
													} else {
														dbStrCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Trying to Starting DB...\"\" &>> "+log_file_path);
														dbStrCmd.append(Constants.DATA_NEW_LINE);
														dbStrCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" 2>> "+err_log_file_path);
														logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"sudo /etc/init.d/"+restore_db_start_name+" start\\\"\" &>> "+log_file_path);
														logCmd.append(Constants.DATA_NEW_LINE);
														logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" start\" 2>> "+err_log_file_path);
													}
													log.debug(logCmd);
													
													dbStrMap = new HashMap<String, Object>();
													dbStrMap = Ssh2Agent.getData(dbStrCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													Thread.sleep(10000);
													
													
													dbrunchkMap = new HashMap<String, Object>();
													dbrunchkCmd = new StringBuilder();
													dbrunchkCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"sudo /etc/init.d/"+restore_db_start_name+" status\"");
													log.debug(dbrunchkCmd);
													dbrunchkMap = Ssh2Agent.getList(dbrunchkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													if (dbrunchkMap != null) {
														List<String> dbrunchklist = new ArrayList<String>();
														dbrunchklist = (List) dbrunchkMap.get("list");
														if (dbrunchklist != null && dbrunchklist.size() > 0) {
															dbrunchkmsg = dbrunchklist.get(0).toString();
															if (dbrunchkmsg.matches("(.*)SUCCESS(.*)") || dbrunchkmsg.matches("(.*)OK(.*)")) {
																
																StringBuilder completeCmd = new StringBuilder();
																
																StringBuilder fileChkCmd = new StringBuilder();
																fileChkCmd.append("if [ -f "+restore_tmpdir+"/"+wrk_dt+".sql ]; then ");
																fileChkCmd.append(Constants.DATA_NEW_LINE);
																fileChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Apply incremental temporary file generated\"\" >> "+log_file_path);
																fileChkCmd.append(Constants.DATA_NEW_LINE);
																fileChkCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" --init-command=\"set sql_log_bin=0;\" < "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
																fileChkCmd.append(Constants.DATA_NEW_LINE);
																fileChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete Temporary File\"\" >> "+log_file_path);
																fileChkCmd.append(Constants.DATA_NEW_LINE);
																fileChkCmd.append("rm -rf "+restore_tmpdir+"/"+wrk_dt+".sql");
																fileChkCmd.append(Constants.DATA_NEW_LINE);
																fileChkCmd.append("fi");
																
																logCmd = new StringBuilder();
																logCmd.append("if [ -f "+restore_tmpdir+"/"+wrk_dt+".sql ]; then ");
																logCmd.append(Constants.DATA_NEW_LINE);
																logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p -h "+restore_host+" -P "+restore_port+" --init-command=\\\"set sql_log_bin=0;\\\" < "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
																logCmd.append(Constants.DATA_NEW_LINE);
																logCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" --init-command=\"set sql_log_bin=0;\" < "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
																logCmd.append(Constants.DATA_NEW_LINE);
																logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: rm -rf "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
																logCmd.append(Constants.DATA_NEW_LINE);
																logCmd.append("rm -rf "+restore_tmpdir+"/"+wrk_dt+".sql");
																logCmd.append(Constants.DATA_NEW_LINE);
																logCmd.append("fi");
																log.debug(logCmd);
																
																Map<String, Object> fileChkMap = new HashMap<String, Object>();
																fileChkMap = Ssh2Agent.getData(fileChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																
																String errLogCmd = "if [ \"`cat "+err_log_file_path+" | grep -i error | grep -v statements_with_errors`\" != '' ] || [ \"`cat "+log_file_path+" | grep -i error | grep -v statements_with_errors`\" != '' ]; then echo no; else echo ok; fi";
																log.debug(errLogCmd);
																Map<String, Object> errCatMap = new HashMap<String, Object>();
																errCatMap = Ssh2Agent.getData(errLogCmd, ms_usr, ms_pwd, ms_ip, port);
																if (errCatMap.get("value") != null) {
																	if ("ok".equals(errCatMap.get("value").toString())) {
																		StringBuilder flushCmd = new StringBuilder();
																		flushCmd.append("echo \"\" >> "+log_file_path);
																		flushCmd.append(Constants.DATA_NEW_LINE);
																		flushCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Execute flush logs\"\" >> "+log_file_path);
																		flushCmd.append(Constants.DATA_NEW_LINE);
																		flushCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
																		
																		logCmd = new StringBuilder();
																		logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p -h "+restore_host+" -P "+restore_port+" -e\\\"flush logs;\\\"\" >> "+log_file_path);
																		logCmd.append(Constants.DATA_NEW_LINE);
																		logCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
																		log.debug(logCmd);
																		
																		Ssh2Agent.getData(flushCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																		
																		long endTime = System.currentTimeMillis();
																		completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo Result : SUCCESS >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																	} else {
																		long endTime = System.currentTimeMillis();
																		completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo Result : FAIL >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																		completeCmd.append(Constants.DATA_NEW_LINE);
																		completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																	}
																}
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
																log.debug(completeCmd);
																completeMap = new HashMap<String, Object>();
																completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																
															} else {
																
																Map<String, Object> lstDbChkMap = new HashMap<String, Object>();
																StringBuilder cmd = new StringBuilder();
																cmd.append("echo '' >> "+log_file_path);
																cmd.append(Constants.DATA_NEW_LINE);
																cmd.append("echo 'DataBase Startup Failure!' >> "+log_file_path);
																cmd.append(Constants.DATA_NEW_LINE);
																cmd.append("echo '' >> "+log_file_path);
																log.debug(cmd);
																lstDbChkMap = Ssh2Agent.getData(cmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																
																long endTime = System.currentTimeMillis();
																StringBuilder completeCmd = new StringBuilder();
																completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo Result : FAIL >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
																log.debug(completeCmd);
																completeMap = new HashMap<String, Object>();
																completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																
															}
														}
													}
													/*//압축이 아닐때 */
												}
											}
											
										} else {
											
											dbstopCmd = new StringBuilder();
											dbstopCmd.append("echo \"The '/etc/init.d/"+restore_db_start_name+"' file path is not valid !!\" >> "+log_file_path);
											dbstopCmd.append(Constants.DATA_NEW_LINE);
											dbstopCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
											log.debug(dbstopCmd);
											Ssh2Agent.getData(dbstopCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
											
											long endTime = System.currentTimeMillis();
											StringBuilder completeCmd = new StringBuilder();
											completeCmd.append("echo \"=================================================\" >> "+log_file_path);
											completeCmd.append(Constants.DATA_NEW_LINE);
											completeCmd.append("echo Result : FAIL >> "+log_file_path);
											completeCmd.append(Constants.DATA_NEW_LINE);
											completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
											completeCmd.append(Constants.DATA_NEW_LINE);
											completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
											completeCmd.append(Constants.DATA_NEW_LINE);
											completeCmd.append("echo \"=================================================\" >> "+log_file_path);
											completeCmd.append(Constants.DATA_NEW_LINE);
											completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
											completeMap = new HashMap<String, Object>();
											log.debug(completeCmd);
											completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
										}
									}
									
								} else {

									dbstopCmd = new StringBuilder();
									dbstopCmd.append("echo \"The 'mariabackup' file path is not valid !!\" >> "+log_file_path);
									dbstopCmd.append(Constants.DATA_NEW_LINE);
									dbstopCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
									log.debug(dbstopCmd);
									Ssh2Agent.getData(dbstopCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
									
									long endTime = System.currentTimeMillis();
									StringBuilder completeCmd = new StringBuilder();
									completeCmd.append("echo \"=================================================\" >> "+log_file_path);
									completeCmd.append(Constants.DATA_NEW_LINE);
									completeCmd.append("echo Result : FAIL >> "+log_file_path);
									completeCmd.append(Constants.DATA_NEW_LINE);
									completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
									completeCmd.append(Constants.DATA_NEW_LINE);
									completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
									completeCmd.append(Constants.DATA_NEW_LINE);
									completeCmd.append("echo \"=================================================\" >> "+log_file_path);
									completeCmd.append(Constants.DATA_NEW_LINE);
									completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
									completeMap = new HashMap<String, Object>();
									log.debug(completeCmd);
									completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
								}
							} else {
								dbstopCmd = new StringBuilder();
								dbstopCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
								log.debug(dbstopCmd);
								Ssh2Agent.getData(dbstopCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
								
								long endTime = System.currentTimeMillis();
								StringBuilder completeCmd = new StringBuilder();
								completeCmd.append("echo \"=================================================\" >> "+log_file_path);
								completeCmd.append(Constants.DATA_NEW_LINE);
								completeCmd.append("echo Result : FAIL >> "+log_file_path);
								completeCmd.append(Constants.DATA_NEW_LINE);
								completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
								completeCmd.append(Constants.DATA_NEW_LINE);
								completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
								completeCmd.append(Constants.DATA_NEW_LINE);
								completeCmd.append("echo \"=================================================\" >> "+log_file_path);
								completeCmd.append(Constants.DATA_NEW_LINE);
								completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
								completeMap = new HashMap<String, Object>();
								log.debug(completeCmd);
								completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
							}
							
						} else {

							dbstopCmd = new StringBuilder();
							dbstopCmd.append("echo \"The 'mysqlbinlog' file path is not valid !!\" >> "+log_file_path);
							dbstopCmd.append(Constants.DATA_NEW_LINE);
							dbstopCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
							log.debug(dbstopCmd);
							Ssh2Agent.getData(dbstopCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
							
							long endTime = System.currentTimeMillis();
							StringBuilder completeCmd = new StringBuilder();
							completeCmd.append("echo \"=================================================\" >> "+log_file_path);
							completeCmd.append(Constants.DATA_NEW_LINE);
							completeCmd.append("echo Result : FAIL >> "+log_file_path);
							completeCmd.append(Constants.DATA_NEW_LINE);
							completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
							completeCmd.append(Constants.DATA_NEW_LINE);
							completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
							completeCmd.append(Constants.DATA_NEW_LINE);
							completeCmd.append("echo \"=================================================\" >> "+log_file_path);
							completeCmd.append(Constants.DATA_NEW_LINE);
							completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
							completeMap = new HashMap<String, Object>();
							log.debug(completeCmd);
							completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
						
						}
						
					} else {
						dbstopCmd = new StringBuilder();
						dbstopCmd.append("echo \"The 'DATA' directory path is not valid !!\" >> "+log_file_path);
						dbstopCmd.append(Constants.DATA_NEW_LINE);
						dbstopCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
						log.debug(dbstopCmd);
						Ssh2Agent.getData(dbstopCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
						
						long endTime = System.currentTimeMillis();
						StringBuilder completeCmd = new StringBuilder();
						completeCmd.append("echo \"=================================================\" >> "+log_file_path);
						completeCmd.append(Constants.DATA_NEW_LINE);
						completeCmd.append("echo Result : FAIL >> "+log_file_path);
						completeCmd.append(Constants.DATA_NEW_LINE);
						completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
						completeCmd.append(Constants.DATA_NEW_LINE);
						completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
						completeCmd.append(Constants.DATA_NEW_LINE);
						completeCmd.append("echo \"=================================================\" >> "+log_file_path);
						completeCmd.append(Constants.DATA_NEW_LINE);
						completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
						completeMap = new HashMap<String, Object>();
						log.debug(completeCmd);
						completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
					}
					
					
					
				} else {
					
					dbstopCmd = new StringBuilder();
					dbstopCmd.append("echo \"'sshpass' RPM is not installed !!\" >> "+log_file_path);
					dbstopCmd.append(Constants.DATA_NEW_LINE);
					dbstopCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
					log.debug(dbstopCmd);
					Ssh2Agent.getData(dbstopCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
					
					long endTime = System.currentTimeMillis();
					StringBuilder completeCmd = new StringBuilder();
					completeCmd.append("echo \"=================================================\" >> "+log_file_path);
					completeCmd.append(Constants.DATA_NEW_LINE);
					completeCmd.append("echo Result : FAIL >> "+log_file_path);
					completeCmd.append(Constants.DATA_NEW_LINE);
					completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
					completeCmd.append(Constants.DATA_NEW_LINE);
					completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
					completeCmd.append(Constants.DATA_NEW_LINE);
					completeCmd.append("echo \"=================================================\" >> "+log_file_path);
					completeCmd.append(Constants.DATA_NEW_LINE);
					completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
					completeMap = new HashMap<String, Object>();
					log.debug(completeCmd);
					completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
					
				}
			}
			
			
		}
	}
	
	public void jobMProcess() throws Exception {
		
		long startTime = System.currentTimeMillis();
		
		String BIN;
		
		Map<String, Object> dbchkMap = null;
		Map<String, Object> dbStrMap = null;
		Map<String, Object> startMap = null;
		Map<String, Object> completeMap = null;
		Map<String, Object> envChkMap = null;
		StringBuilder envChkCmd = null;
		
		if (paramMap != null) {
			
//			xtra_bin_path = paramMap.get("ms_bny_pth").toString();
			restore_default_file = paramMap.get("restore_default_file").toString();
			
			backup_dir = paramMap.get("backup_dir").toString();
			backup_pdir = paramMap.get("backup_pdir").toString();
			lst_full_dir = paramMap.get("lst_full_dir").toString();
			restore_tmp = paramMap.get("restore_tmp").toString();
			lst_full_wrk_dt = paramMap.get("lst_full_wrk_dt").toString();
			
			restore_dir = paramMap.get("restore_dir").toString();
			restore_logDir = paramMap.get("restore_logDir").toString();
			log_file_path = paramMap.get("log_file_path").toString();
			err_log_file_path = paramMap.get("err_log_file_path").toString();
			
			msq_clt_utl_pth = paramMap.get("msq_clt_utl_pth").toString();
			restore_binpath = paramMap.get("restore_binpath").toString();
			restore_user = paramMap.get("restore_user").toString();
			restore_pass = paramMap.get("restore_pass").toString();
			restore_host = paramMap.get("restore_host").toString();
			restore_port = paramMap.get("restore_port").toString();
			restore_stop_opt = paramMap.get("restore_stop_opt").toString();
			restore_os_user = paramMap.get("restore_os_user").toString();
			restore_os_pwd = paramMap.get("restore_os_pwd").toString();
			restore_db_start_name = paramMap.get("restore_db_start_name").toString();
			restore_tmpdir = paramMap.get("restore_tmpdir").toString();
			restore_decompress_yn = paramMap.get("restore_decompress_yn").toString();
			if (paramMap.get("restore_instance_no") != null) {
				restore_instance_no = paramMap.get("restore_instance_no").toString();
			}
			
			dbchkCmd = paramMap.get("dbchkCmd").toString();
			BIN = paramMap.get("BIN").toString();
			
			/*
			if (paramMap.get("os_version") != null) {
				os_version = paramMap.get("os_version").toString();
			}
			*/
			boolean gtid = db_version(restore_os_user,restore_os_pwd,restore_user,restore_pass,restore_host,restore_port);
			
			startMap = new HashMap<String, Object>();
			StringBuilder startCmt = new StringBuilder();
			try {
				startCmt.append(Constants.DATA_NEW_LINE);
				startCmt.append("echo \"=================================================\" > "+log_file_path);
				startCmt.append(Constants.DATA_NEW_LINE);
				startCmt.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE START >> "+log_file_path);
				startCmt.append(Constants.DATA_NEW_LINE);
				startCmt.append("echo \"=================================================\" >> "+log_file_path);
				startCmt.append(Constants.DATA_NEW_LINE);
				log.debug(startCmt);
				startMap = Ssh2Agent.restoreRun(startCmt.toString(), ms_usr, ms_pwd, ms_ip, port);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("jobProcess startMap Exception", e.fillInStackTrace());
			}
			
			String rpmchkCmd = "sshpass -p"+restore_os_pwd+" ssh -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"if [ \"`rpm -qa | grep -i '^sshpass'`\" == \"\" ];then echo no; else echo ok; fi\"";
			Map<String, Object> rpmchkMap = new HashMap<String, Object>();
			rpmchkMap = Ssh2Agent.getData(rpmchkCmd, ms_usr, ms_pwd, ms_ip, port);
			if (rpmchkMap != null) {
				if ("ok".equals(rpmchkMap.get("value").toString())) {
					
					StringBuilder logCmd = new StringBuilder();
					log.debug(dbchkCmd);
					dbchkMap = new HashMap<String, Object>();
					dbchkMap = Ssh2Agent.getData(dbchkCmd, ms_usr, ms_pwd, ms_ip, port);
					if (dbchkMap != null) {
						if ("ok".equals(dbchkMap.get("value").toString())) {
							
							String binlogChkCmd = "if [ -x "+msq_clt_utl_pth+"/mysqlbinlog ]; then echo ok; else echo no; fi";
							log.debug(binlogChkCmd);
							Map<String, Object> binlogChkMap = new HashMap<String, Object>();
							binlogChkMap = Ssh2Agent.getData(binlogChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
							if (binlogChkMap != null) {
								if ("ok".equals(binlogChkMap.get("value").toString()) && !"".equals(restore_dir)) {
									envChkCmd = new StringBuilder();
									envChkCmd.append("if [ -x "+msq_clt_utl_pth+"/mysql ]; then echo ok; else echo no; fi");
									log.debug(envChkCmd);
									envChkMap = new HashMap<String, Object>();
									envChkMap = Ssh2Agent.getData(envChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
									if (envChkMap != null) {
										if ("ok".equals(envChkMap.get("value").toString())) {
											data_dir = restore_dir;
											if (StringUtils.countMatches(data_dir, "/") > 1) {
												if ((data_dir.lastIndexOf("/")+1) == data_dir.length()) {
													data_dir = data_dir.substring(0, data_dir.length()-1);
												}
											}
											
											/*
											 * 데이터백업 옵션(Y,N)
											 */
											if ("Y".equals(restore_stop_opt)) {
												StringBuilder dataBakCmd = new StringBuilder();
												dataBakCmd.append("echo \"\" >> "+log_file_path);
												dataBakCmd.append(Constants.DATA_NEW_LINE);
												dataBakCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete target instance backup directory\"\" >> "+log_file_path);
												dataBakCmd.append(Constants.DATA_NEW_LINE);
												dataBakCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"_backup/*\" 2>> "+err_log_file_path);
												dataBakCmd.append(Constants.DATA_NEW_LINE);
												dataBakCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Copy the backup to the target instance backup directory\"\" >> "+log_file_path);
												dataBakCmd.append(Constants.DATA_NEW_LINE);
												dataBakCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"cp -r "+data_dir+"/* "+data_dir+"_backup/\" 2>> "+err_log_file_path);
												
												logCmd = new StringBuilder();
												logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"rm -rf "+data_dir+"_backup/*\\\"\" >> "+log_file_path);
												logCmd.append(Constants.DATA_NEW_LINE);
												logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"rm -rf "+data_dir+"_backup/*\" 2>> "+err_log_file_path);
												logCmd.append(Constants.DATA_NEW_LINE);
												logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: ssh -t -t "+restore_os_user+"@"+restore_host+" \\\"cp -r "+data_dir+"/* "+data_dir+"_backup/\\\"\" >> "+log_file_path);
												logCmd.append(Constants.DATA_NEW_LINE);
												logCmd.append("sshpass -p"+restore_os_pwd+" ssh -t -t -o StrictHostKeyChecking=no "+restore_os_user+"@"+restore_host+" \"cp -r "+data_dir+"/* "+data_dir+"_backup/\" 2>> "+err_log_file_path);
												log.debug(logCmd);
												
												Map<String, Object> dataBakMap = new HashMap<String, Object>();
												dataBakMap = Ssh2Agent.getData(dataBakCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
											}
											
											StringBuilder bnyRfCmd = new StringBuilder();
											bnyRfCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Load full backup dump data\"\" >> "+log_file_path);
											bnyRfCmd.append(Constants.DATA_NEW_LINE);
											bnyRfCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" --init-command=\"set sql_log_bin=0;\" < "+lst_full_dir+"/"+lst_full_wrk_dt+".sql >> "+log_file_path);
											bnyRfCmd.append(Constants.DATA_NEW_LINE);
											bnyRfCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Execute flush logs\"\" >> "+log_file_path);
											bnyRfCmd.append(Constants.DATA_NEW_LINE);
											bnyRfCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
											bnyRfCmd.append(Constants.DATA_NEW_LINE);
											bnyRfCmd.append("RESTOREBIN=`"+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -BNse\"show master status;\" | awk '{print $1}'`");
											bnyRfCmd.append(Constants.DATA_NEW_LINE);
											bnyRfCmd.append("echo $RESTOREBIN > "+backup_pdir+"/"+wrk_dt+"/next_bin.log");
											
											logCmd = new StringBuilder();
											logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p -h "+restore_host+" -P "+restore_port+" --init-command=\\\"set sql_log_bin=0;\\\" < "+lst_full_dir+"/"+lst_full_wrk_dt+".sql\" >> "+log_file_path);
											logCmd.append(Constants.DATA_NEW_LINE);
											logCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" --init-command=\"set sql_log_bin=0;\" < "+lst_full_dir+"/"+lst_full_wrk_dt+".sql >> "+log_file_path);
											logCmd.append(Constants.DATA_NEW_LINE);
											logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p -h "+restore_host+" -P "+restore_port+" -e\\\"flush logs;\\\"\" >> "+log_file_path);
											logCmd.append(Constants.DATA_NEW_LINE);
											logCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
											logCmd.append(Constants.DATA_NEW_LINE);
											logCmd.append("RESTOREBIN=`"+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -BNse\"show master status;\" | awk '{print $1}'`");
											logCmd.append(Constants.DATA_NEW_LINE);
											logCmd.append("echo $RESTOREBIN > "+backup_pdir+"/"+wrk_dt+"/next_bin.log");
											log.debug(logCmd);
											
											Map<String, Object> bnyRfMap = new HashMap<String, Object>();
											bnyRfMap = Ssh2Agent.getData(bnyRfCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
											
											if (!"".equals(restore_dir)) {
												
												//증분체크
												if ("L02".equals(lvl)) {
													StringBuilder increChkCmd = new StringBuilder();
													increChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Create incremental temporary file\"\" >> "+log_file_path);
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append(BIN);
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("for i in `ls "+backup_pdir+" | grep -v \"last_*\" | grep -v \"manual\"`");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("do");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("if [ $i -gt "+lst_full_wrk_dt+" ] && [ $i -le "+wrk_dt+" ]; then");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													
													increChkCmd.append("if [ \"`echo $BIN`\" != \"\" ]; then ");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("BIN_CUT=`echo $BIN | cut -d\".\" -f1`");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\"`");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("else");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\"`");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("fi");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													
													increChkCmd.append("if [ ! -z \"$BIN_LOG\" ]; then ");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													//increChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql\" &>> "+log_file_path);
													increChkCmd.append(msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+err_log_file_path);
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("fi");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("fi");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("done");
													increChkCmd.append(Constants.DATA_NEW_LINE);
													increChkCmd.append("sed -ie \"s/, @@session.check_constraint_checks=1//g\" "+restore_tmpdir+"/"+wrk_dt+".sql");
													log.debug(increChkCmd);
													
													logCmd = new StringBuilder();
													logCmd.append(BIN);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("for i in `ls "+backup_pdir+" | grep -v \"last_*\" | grep -v \"manual\"`");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("do");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("if [ $i -gt "+lst_full_wrk_dt+" ] && [ $i -le "+wrk_dt+" ]; then");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("if [ \"`echo $BIN`\" != \"\" ]; then ");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("BIN_CUT=`echo $BIN | cut -d\".\" -f1`");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\"`");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("else");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("BIN_LOG=`ls "+backup_pdir+"/$i/* | grep \"$BIN_CUT\"`");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("fi");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("if [ ! -z \"$BIN_LOG\" ]; then ");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append(msq_clt_utl_pth+"/mysqlbinlog "+(gtid?"--skip-gtids ":"")+"`echo $BIN_LOG` >> "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("fi");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("fi");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("done");
													logCmd.append(Constants.DATA_NEW_LINE);
													logCmd.append("sed -ie \"s/, @@session.check_constraint_checks=1//g\" "+restore_tmpdir+"/"+wrk_dt+".sql");
													log.debug(logCmd);
													
													Map<String, Object> increChkMap = new HashMap<String, Object>();
													increChkMap = Ssh2Agent.getData(increChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
												}
												
												dbchkMap = new HashMap<String, Object>();
												log.debug(dbchkCmd);
												dbchkMap = Ssh2Agent.getData(dbchkCmd, ms_usr, ms_pwd, ms_ip, port);
												if (dbchkMap != null) {
													
													StringBuilder completeCmd = new StringBuilder();
													
													if ("ok".equals(dbchkMap.get("value").toString())) {
														
														StringBuilder fileChkCmd = new StringBuilder();
														fileChkCmd.append("if [ -f "+restore_tmpdir+"/"+wrk_dt+".sql ]; then ");
														fileChkCmd.append(Constants.DATA_NEW_LINE);
														fileChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Apply incremental temporary file generated\"\" >> "+log_file_path);
														fileChkCmd.append(Constants.DATA_NEW_LINE);
														fileChkCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" --init-command=\"set sql_log_bin=0;\" < "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
														fileChkCmd.append(Constants.DATA_NEW_LINE);
														fileChkCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Delete Temporary File\"\" >> "+log_file_path);
														fileChkCmd.append(Constants.DATA_NEW_LINE);
														fileChkCmd.append("rm -rf "+restore_tmpdir+"/"+wrk_dt+".sql");
														fileChkCmd.append(Constants.DATA_NEW_LINE);
														fileChkCmd.append("fi");
														
														logCmd = new StringBuilder();
														logCmd.append("if [ -f "+restore_tmpdir+"/"+wrk_dt+".sql ]; then ");
														logCmd.append(Constants.DATA_NEW_LINE);
														logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p -h "+restore_host+" -P "+restore_port+" --init-command=\\\"set sql_log_bin=0;\\\" < "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
														logCmd.append(Constants.DATA_NEW_LINE);
														logCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" --init-command=\"set sql_log_bin=0;\" < "+restore_tmpdir+"/"+wrk_dt+".sql 2>> "+log_file_path);
														logCmd.append(Constants.DATA_NEW_LINE);
														logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: rm -rf "+restore_tmpdir+"/"+wrk_dt+".sql\" >> "+log_file_path);
														logCmd.append(Constants.DATA_NEW_LINE);
														logCmd.append("rm -rf "+restore_tmpdir+"/"+wrk_dt+".sql");
														logCmd.append(Constants.DATA_NEW_LINE);
														logCmd.append("fi");
														log.debug(logCmd);
														
														Map<String, Object> fileChkMap = new HashMap<String, Object>();
														fileChkMap = Ssh2Agent.getData(fileChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
														
														String errLogCmd = "if [ \"`cat "+err_log_file_path+" | grep -i error | grep -v statements_with_errors`\" != '' ] || [ \"`cat "+log_file_path+" | grep -i error | grep -v statements_with_errors`\" != '' ]; then echo no; else echo ok; fi";
														log.debug(errLogCmd);
														Map<String, Object> errCatMap = new HashMap<String, Object>();
														errCatMap = Ssh2Agent.getData(errLogCmd, ms_usr, ms_pwd, ms_ip, port);
														if (errCatMap.get("value") != null) {
															if ("ok".equals(errCatMap.get("value").toString())) {

																StringBuilder flushCmd = new StringBuilder();
																flushCmd.append("echo \"\" >> "+log_file_path);
																flushCmd.append(Constants.DATA_NEW_LINE);
																flushCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` \"Execute flush logs\"\" >> "+log_file_path);
																flushCmd.append(Constants.DATA_NEW_LINE);
																flushCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
																
																logCmd = new StringBuilder();
																logCmd.append("echo \"`date \"+%y%m%d %H:%M:%S\"` execute :: "+msq_clt_utl_pth+"/mysql -u"+restore_user+" -p -h "+restore_host+" -P "+restore_port+" -e\\\"flush logs;\\\"\" >> "+log_file_path);
																logCmd.append(Constants.DATA_NEW_LINE);
																logCmd.append(msq_clt_utl_pth+"/mysql -u"+restore_user+" -p"+restore_pass+" -h "+restore_host+" -P "+restore_port+" -e\"flush logs;\"");
																log.debug(logCmd);
																
																Ssh2Agent.getData(flushCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
																
																long endTime = System.currentTimeMillis();
																completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo Result : SUCCESS >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																
															} else {
																
																long endTime = System.currentTimeMillis();
																completeCmd.append("echo \"=================================================\" >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo Result : FAIL >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
																completeCmd.append(Constants.DATA_NEW_LINE);
																completeCmd.append("echo \"=================================================\" >> "+log_file_path);
															}
														}
														
														completeCmd.append(Constants.DATA_NEW_LINE);
														completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
														log.debug(completeCmd);
														completeMap = new HashMap<String, Object>();
														completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
														
													} else {
														Map<String, Object> lstDbChkMap = new HashMap<String, Object>();
														StringBuilder cmd = new StringBuilder();
														cmd.append("echo '' >> "+log_file_path);
														cmd.append(Constants.DATA_NEW_LINE);
														cmd.append("echo 'DataBase Startup Failure!' >> "+log_file_path);
														cmd.append(Constants.DATA_NEW_LINE);
														cmd.append("echo '' >> "+log_file_path);
														log.debug(cmd);
														lstDbChkMap = Ssh2Agent.getData(cmd.toString(), ms_usr, ms_pwd, ms_ip, port);
														
														long endTime = System.currentTimeMillis();
														completeCmd.append("echo \"=================================================\" >> "+log_file_path);
														completeCmd.append(Constants.DATA_NEW_LINE);
														completeCmd.append("echo Result : FAIL >> "+log_file_path);
														completeCmd.append(Constants.DATA_NEW_LINE);
														completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
														completeCmd.append(Constants.DATA_NEW_LINE);
														completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
														completeCmd.append(Constants.DATA_NEW_LINE);
														completeCmd.append("echo \"=================================================\" >> "+log_file_path);
														completeCmd.append(Constants.DATA_NEW_LINE);
														completeCmd.append("cat "+log_file_path+" | grep -v \"ginian*\" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
														log.debug(completeCmd);
														completeMap = new HashMap<String, Object>();
														completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
													}
												}
											}
											
										} else {
											
											envChkCmd = new StringBuilder();
											envChkCmd.append("echo \"The 'mysql' file path is not valid !!\" >> "+log_file_path);
											envChkCmd.append(Constants.DATA_NEW_LINE);
											envChkCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
											log.debug(envChkCmd);
											Ssh2Agent.getData(envChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
											
											long endTime = System.currentTimeMillis();
											StringBuilder completeCmd = new StringBuilder();
											completeCmd.append("echo \"=================================================\" >> "+log_file_path);
											completeCmd.append(Constants.DATA_NEW_LINE);
											completeCmd.append("echo Result : FAIL >> "+log_file_path);
											completeCmd.append(Constants.DATA_NEW_LINE);
											completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
											completeCmd.append(Constants.DATA_NEW_LINE);
											completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
											completeCmd.append(Constants.DATA_NEW_LINE);
											completeCmd.append("echo \"=================================================\" >> "+log_file_path);
											completeCmd.append(Constants.DATA_NEW_LINE);
											completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
											log.debug(completeCmd);
											completeMap = new HashMap<String, Object>();
											completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
										}
									}
									
								} else {
									envChkCmd = new StringBuilder();
									envChkCmd.append("echo \"The 'mysqlbinlog' file path is not valid !!\" >> "+log_file_path);
									envChkCmd.append(Constants.DATA_NEW_LINE);
									envChkCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
									log.debug(envChkCmd);
									Ssh2Agent.getData(envChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
									
									long endTime = System.currentTimeMillis();
									StringBuilder completeCmd = new StringBuilder();
									completeCmd.append("echo \"=================================================\" >> "+log_file_path);
									completeCmd.append(Constants.DATA_NEW_LINE);
									completeCmd.append("echo Result : FAIL >> "+log_file_path);
									completeCmd.append(Constants.DATA_NEW_LINE);
									completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
									completeCmd.append(Constants.DATA_NEW_LINE);
									completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
									completeCmd.append(Constants.DATA_NEW_LINE);
									completeCmd.append("echo \"=================================================\" >> "+log_file_path);
									completeCmd.append(Constants.DATA_NEW_LINE);
									completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
									log.debug(completeCmd);
									completeMap = new HashMap<String, Object>();
									completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
								}
							}
							
						} else {
							
							envChkCmd = new StringBuilder();
							envChkCmd.append("echo \"MySQL Server has been gone\" >> "+log_file_path);
							envChkCmd.append(Constants.DATA_NEW_LINE);
							log.debug(envChkCmd);
							Ssh2Agent.getData(envChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
							
							long endTime = System.currentTimeMillis();
							StringBuilder completeCmd = new StringBuilder();
							completeCmd.append("echo \"=================================================\" >> "+log_file_path);
							completeCmd.append(Constants.DATA_NEW_LINE);
							completeCmd.append("echo Result : FAIL >> "+log_file_path);
							completeCmd.append(Constants.DATA_NEW_LINE);
							completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
							completeCmd.append(Constants.DATA_NEW_LINE);
							completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
							completeCmd.append(Constants.DATA_NEW_LINE);
							completeCmd.append("echo \"=================================================\" >> "+log_file_path);
							completeCmd.append(Constants.DATA_NEW_LINE);
							completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
							log.debug(completeCmd);
							completeMap = new HashMap<String, Object>();
							completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
						}
					}
					
				} else {
					
					envChkCmd = new StringBuilder();
					envChkCmd.append("echo \"'sshpass' RPM is not installed !!\" >> "+log_file_path);
					envChkCmd.append(Constants.DATA_NEW_LINE);
					envChkCmd.append("echo \"Check The Environment Setting\" >> "+log_file_path);
					log.debug(envChkCmd);
					Ssh2Agent.getData(envChkCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
					
					long endTime = System.currentTimeMillis();
					StringBuilder completeCmd = new StringBuilder();
					completeCmd.append("echo \"=================================================\" >> "+log_file_path);
					completeCmd.append(Constants.DATA_NEW_LINE);
					completeCmd.append("echo Result : FAIL >> "+log_file_path);
					completeCmd.append(Constants.DATA_NEW_LINE);
					completeCmd.append("echo ELAPSED TIME : "+((endTime - startTime) / 1000.0f)+" >> "+log_file_path);
					completeCmd.append(Constants.DATA_NEW_LINE);
					completeCmd.append("echo `date +%Y-%m-%d' '%H:%M:%S` RESTORE END >> "+log_file_path);
					completeCmd.append(Constants.DATA_NEW_LINE);
					completeCmd.append("echo \"=================================================\" >> "+log_file_path);
					completeCmd.append(Constants.DATA_NEW_LINE);
					completeCmd.append("cat "+log_file_path+" | grep -v \"statements_with_errors\" | egrep -i \"error|fail|kill\" >> "+err_log_file_path);
					completeMap = new HashMap<String, Object>();
					log.debug(completeCmd);
					completeMap = Ssh2Agent.getData(completeCmd.toString(), ms_usr, ms_pwd, ms_ip, port);
					
				}
			}
			
		}
	}
	
	public void schedulekill() {
		
		try {
			schedulerHand = BackupScheduler.getInstance();
			schedulerHand.deleteJob(Constants.JOB_RESTORE_EXCUTE+"::"+bms_nam+bts_nam + "::" + wrk_dt,Constants.GROUP_RESTORE+"::"+bms_nam+bts_nam);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("schedulekill Exception", e.fillInStackTrace());
		}
	}
	
	public boolean db_version(String sshid, String ospwd, String usr, String pw, String ip, String dbport) {
		boolean gtid = false;
		try {
			Map<String, Object> verMap = new HashMap<String, Object>();
//			String vercmd = "ssh "+sshid+"@"+ip+" \"mysql -u"+usr+" -p"+pw+" -BNe 'select @@version;'\"";
			String vercmd = "sshpass -p"+ospwd+" ssh -o StrictHostKeyChecking=no "+sshid+"@"+ip+" \"mysql -u"+usr+" -p"+pw+" -BNe 'select @@version;'\"";
			verMap = Ssh2Agent.getData(vercmd, ms_usr, ms_pwd, ms_ip, port, 5);
			if (verMap != null) {
				if (verMap.get("value") != null) {
					String version = verMap.get("value").toString();
					if (!version.matches("(.*)maria(.*)")) {
						int idx1 = version.indexOf(".");
						int idx2 = version.indexOf(".",3);
						String vmi = version.substring(idx1+1, idx2);
						if (Integer.valueOf(vmi) > 5) {
							gtid = true;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gtid;
	}

}
