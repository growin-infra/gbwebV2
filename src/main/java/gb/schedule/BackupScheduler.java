package gb.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import gb.common.util.Constants;
import gb.schedule.job.BackupFullExcute;
import gb.schedule.job.BackupIncreExcute;
import gb.schedule.job.RestoreExcute;
import gb.schedule.job.ScheduleEndChk;
import gb.schedule.job.ScheduleFullBackup;

public class BackupScheduler {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private SchedulerFactory schedFact;
	private Scheduler sched = null;
	private static BackupScheduler schedulerHandlerInstance = null;
	
	/**
	 * 생성자
	 * 
	 * @throws Exception
	 */
	public BackupScheduler() throws Exception {
		schedFact = new StdSchedulerFactory();
		try {
			sched = schedFact.getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("BackupScheduler SchedulerException : ", e.fillInStackTrace());
		}
	}
	
	/**
	 * Instance 반환
	 * 
	 * @throws Exception
	 */
	public static synchronized BackupScheduler getInstance() throws Exception {
		if (schedulerHandlerInstance == null)
			schedulerHandlerInstance = new BackupScheduler();
		return schedulerHandlerInstance;
	}

	/**
	 * Scheduler 시작
	 * 
	 * @throws Exception
	 */
	public void startScheduler() throws Exception {
		try {
			sched.start();
		} catch (SchedulerException se) {
			se.printStackTrace();
			log.error("startScheduler SchedulerException : ", se.fillInStackTrace());
			//throw new ScheduleException("startScheduler Exception : " + se.getMessage());
		}
	}

	Map<String,Object> svcParamMap = null;

	public void startScheduler(Map<String,Object> cmd) throws Exception {
		try {
			svcParamMap = new HashMap<String,Object>();
			svcParamMap.putAll(cmd);
			sched.start();
		} catch (SchedulerException se) {
			se.printStackTrace();
			log.error("startScheduler SchedulerException : ", se.fillInStackTrace());
//			throw new ScheduleException("startScheduler Exception : " + se.getMessage());
		}
	}

	public void standbyScheduler() throws Exception {
		try {
			sched.standby();
		} catch (SchedulerException se) {
			se.printStackTrace();
			log.error("standbyScheduler SchedulerException : ", se.fillInStackTrace());
//			throw new ScheduleException("startScheduler Exception : " + se.getMessage());
		}
	}

	/**
	 * Scheduler 정지
	 * 
	 * @throws Exception
	 */
	public void stopScheduler() throws Exception {

		try {
			// Scheduler 정지
			if (!sched.isShutdown()) {
				sched.shutdown();
			}
		} catch (SchedulerException se) {
			se.printStackTrace();
			log.error("stopScheduler SchedulerException : ", se.fillInStackTrace());
//			throw new ScheduleException("stopScheduler Exception : " + se.getMessage());
		}
	}

	/**
	 * Job 삭제
	 * 
	 * @param JobName
	 * @param GroupName
	 * @throws Exception
	 */
	public void deleteJob(String jobNm, String grpNm) throws Exception {

		try {
			sched.deleteJob(jobNm, grpNm);
		} catch (SchedulerException ex) {
			ex.printStackTrace();
			log.error("deleteJob SchedulerException : ", ex.fillInStackTrace());
//			throw new ScheduleException("Unable to delete job", ex);
		}
	}

	/**
	 * Group Job 삭제
	 * 
	 * @param JobName
	 * @param GroupName
	 * @throws Exception
	 */
	public void deleteGroupJob(String grpNm) throws Exception {

		try {
			List<Map<String, String>> list = searchScheduleRegJob(grpNm);
			if (list != null && list.size() > 0) {
				Map<String, String> map = new HashMap<String, String>();
				String jobNm = "";
				for (int i = 0; i < list.size(); i++) {
					map = (Map<String, String>) list.get(i);
					jobNm = (String) map.get("JOB_NAME");
					sched.deleteJob(jobNm, grpNm);
				}
			}
		} catch (SchedulerException ex) {
			ex.printStackTrace();
			log.error("deleteGroupJob SchedulerException : ", ex.fillInStackTrace());
//			throw new ScheduleException("Unable to delete job", ex);
		}
	}

	/**
	 * Scheduler에 등록된 모든 Job 조회
	 * 
	 * @param GroupName
	 *            : 'ALL' or 'all' 전체조회
	 * @return Scheduler에 등록된 모든 Job 조회결과
	 * @throws Exception
	 */
	public List<Map<String, String>> searchScheduleRegJob(String grpNm)
			throws Exception {

		List<Map<String, String>> rlist = new ArrayList<Map<String, String>>();

		try {
			for (String groupName : sched.getJobGroupNames()) {
				if ("all".equalsIgnoreCase(grpNm)) {
					for (String jobName : sched.getJobNames(groupName)) {
						Map<String, String> map = new Hashtable<String, String>();
						map.put("GROUP_NAME", groupName);
						map.put("JOB_NAME", jobName);
						rlist.add(map);
					}
				} else {
					if (groupName.equals(grpNm)) {
						for (String jobName : sched.getJobNames(groupName)) {
							Map<String, String> map = new Hashtable<String, String>();
							map.put("GROUP_NAME", groupName);
							map.put("JOB_NAME", jobName);
							rlist.add(map);
						}
						break;
					}
				}
			}
		} catch (SchedulerException ex) {
			ex.printStackTrace();
			log.error("searchScheduleRegJob SchedulerException : ", ex.fillInStackTrace());
//			throw new ScheduleException("Scheduler에 등록된 모든 Job 조회시 에러발생!", ex);
		}

		return rlist;
	}

	/**
	 * 해당 Job이 Scheduler에 존재하는지 조회
	 * 
	 * @param JobName
	 * @param GroupName
	 * @return 존재여부
	 * @throws Exception
	 */
	public boolean isScheduleRegJob(String jobNm, String grpNm)
			throws Exception {

		boolean rtnValue = false;

		try {

			for (String groupName : sched.getJobGroupNames()) {
				if (groupName.equals(grpNm)) {
					for (String jobName : sched.getJobNames(groupName)) {
						if (jobName.equals(jobNm)) {
							rtnValue = true;
							break;
						}
					}
				}
			}
		} catch (SchedulerException ex) {
			ex.printStackTrace();
			log.error("isScheduleRegJob SchedulerException : ", ex.fillInStackTrace());
//			throw new ScheduleException("Scheduler에 등록된 Job 존재여부 조회시 에러발생!", ex);
		}

		return rtnValue;
	}

	/**
	 * 해당 Job이 Scheduler에 존재하는지 조회
	 * 
	 * @param JobName
	 * @param GroupName
	 * @return 존재여부
	 * @throws Exception
	 */
	public boolean isScheduleRegGroup(String grpNm) throws Exception {

		boolean rtnValue = false;

		try {

			for (String groupName : sched.getJobGroupNames()) {
				if (groupName.equals(grpNm)) {
					rtnValue = true;
				}
			}
		} catch (SchedulerException ex) {
			ex.printStackTrace();
			log.error("isScheduleRegGroup SchedulerException : ", ex.fillInStackTrace());
//			throw new ScheduleException("Scheduler에 등록된 Job 존재여부 조회시 에러발생!", ex);
		}

		return rtnValue;
	}
	
	public Map<String,Object> getSchedulerGroupName(String grpNm) {
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			for (String groupName : sched.getJobGroupNames()) {
				if (groupName.equals(grpNm)) {
					map.put("JobNames", grpNm);
				}
			}
			
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("getSchedulerGroupName SchedulerException : ", e.fillInStackTrace());
		}
		return map;
	}
	
	public List<String> getSchedulerJobName(String grpNm) {
		List<String> list = new ArrayList<String>();
		try {
			for (String groupName : sched.getJobGroupNames()) {
				if (groupName.equals(grpNm)) {
					for (String jobName : sched.getJobNames(groupName)) {
						list.add(jobName);
					}
				}
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("getSchedulerJobName SchedulerException : ", e.fillInStackTrace());
		}
		return list;
	}
	
	public List<String> getSchedulerJobGroups() {
		List<String> list = new ArrayList<String>();
		try {
			for (String groupName : sched.getJobGroupNames()) {
				list.add(groupName);
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("getSchedulerJobGroups SchedulerException : ", e.fillInStackTrace());
		}
		return list;
	}
	

	/**
	 * Scheduler 주기적감시작업 등록
	 * 
	 * @throws Exception
	 */
	public void regiSchedulerFullBackup() throws Exception {

		//CronTrigger trigger;
		JobDetail job;
		Trigger trgg;
		String bms_nam, bts_nam, wrk_dt;
		if (svcParamMap != null) {
			bms_nam = svcParamMap.get("bms_nam").toString();
			bts_nam = svcParamMap.get("bts_nam").toString();
			wrk_dt = svcParamMap.get("wrk_dt").toString();
			long interval = (long) svcParamMap.get("interval");
//			String interval = (String) svcParamMap.get("interval");
			String grpNm = Constants.GROUP_BACKUP + "::" + bms_nam+bts_nam;
			String jobNm, triggerNm;
			try {
				// 1.Excute
				triggerNm = Constants.TRGG_FULL_BACKUP_EXCUTE + "::" + bms_nam+bts_nam + "::" + wrk_dt;
				jobNm = Constants.JOB_FULL_BACKUP_EXCUTE + "::" + bms_nam+bts_nam + "::" + wrk_dt;
				trgg = new SimpleTrigger(triggerNm, grpNm);
				((SimpleTrigger) trgg).setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
//				trgg.setStartTime(new Date(System.currentTimeMillis() + 1000L));
				trgg.setStartTime(new Date());
//				((SimpleTrigger) trgg).setRepeatInterval(Constants.INTERVAL_1DAY);
				((SimpleTrigger) trgg).setRepeatInterval(interval);
				job = new JobDetail(jobNm, grpNm, BackupFullExcute.class);
				job.getJobDataMap().put("svcParamMap", svcParamMap);
				sched.scheduleJob(job, trgg);
				
			} catch (SchedulerException se) {
				se.printStackTrace();
				log.error("regiSchedulerFullBackup SchedulerException : ", se.fillInStackTrace());
			}
		}
	}
	public void regiSchedulerIncreBackup() throws Exception {
		
		//CronTrigger trigger;
		JobDetail job;
		Trigger trgg;
		String bms_nam, bts_nam, wrk_dt;
		if (svcParamMap != null) {
			bms_nam = svcParamMap.get("bms_nam").toString();
			bts_nam = svcParamMap.get("bts_nam").toString();
			wrk_dt = svcParamMap.get("wrk_dt").toString();
			long interval = (long) svcParamMap.get("interval");
			String grpNm = Constants.GROUP_BACKUP + "::" + bms_nam+bts_nam;
			String jobNm, triggerNm;
			try {
				// 1.Excute
				triggerNm = Constants.TRGG_INCRE_BACKUP_EXCUTE + "::" + bms_nam+bts_nam + "::" + wrk_dt;
				jobNm = Constants.JOB_INCRE_BACKUP_EXCUTE + "::" + bms_nam+bts_nam + "::" + wrk_dt;
				trgg = new SimpleTrigger(triggerNm, grpNm);
				((SimpleTrigger) trgg).setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
//				trgg.setStartTime(new Date(System.currentTimeMillis() + 1000L));
				trgg.setStartTime(new Date());
//				((SimpleTrigger) trgg).setRepeatInterval(Constants.INTERVAL_1DAY);
				((SimpleTrigger) trgg).setRepeatInterval(interval);
				job = new JobDetail(jobNm, grpNm, BackupIncreExcute.class);
				job.getJobDataMap().put("svcParamMap", svcParamMap);
				sched.scheduleJob(job, trgg);
				
			} catch (SchedulerException se) {
				se.printStackTrace();
				log.error("regiSchedulerIncreBackup SchedulerException : ", se.fillInStackTrace());
			}
		}
	}

	/**
	 * Scheduler 주기적감시작업 등록
	 * 
	 * @throws Exception
	 */
	public void regiSchedulerRestore() throws Exception {

//		CronTrigger trigger;
		JobDetail job;
		Trigger trgg;
		String bms_nam, bts_nam, wrk_dt;
		
		if (svcParamMap != null) {
			bms_nam = svcParamMap.get("bms_nam").toString();
			bts_nam = svcParamMap.get("bts_nam").toString();
			wrk_dt = svcParamMap.get("wrk_dt").toString();
			long interval = (long) svcParamMap.get("interval");
			String grpNm = Constants.GROUP_RESTORE + "::" + bms_nam+bts_nam;
			String jobNm, triggerNm;
			try {
				// 1.Excute
				triggerNm = Constants.TRGG_RESTORE_EXCUTE + "::" + bms_nam+bts_nam + "::" + wrk_dt;
				jobNm = Constants.JOB_RESTORE_EXCUTE + "::" + bms_nam+bts_nam + "::" + wrk_dt;
				trgg = new SimpleTrigger(triggerNm, grpNm);
				((SimpleTrigger) trgg).setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
//				trgg.setStartTime(new Date(System.currentTimeMillis() + 1000L));
				trgg.setStartTime(new Date());
//				((SimpleTrigger) trgg).setRepeatInterval(Constants.INTERVAL_1DAY);
				((SimpleTrigger) trgg).setRepeatInterval(interval);
				job = new JobDetail(jobNm, grpNm, RestoreExcute.class);
				job.getJobDataMap().put("svcParamMap", svcParamMap);
				sched.scheduleJob(job, trgg);
				
			} catch (SchedulerException se) {
				se.printStackTrace();
				log.error("regiSchedulerRestore SchedulerException : ", se.fillInStackTrace());
			}
		}

	}
	
	
	/*
	 * 스케줄 백업
	 */
	public void schFullBackup() throws Exception {

		//CronTrigger trigger;
		JobDetail job;
		Trigger trgg;
		String bms_nam, bts_nam, wrk_dt;
		if (svcParamMap != null) {
			bms_nam = svcParamMap.get("bms_nam").toString();
			bts_nam = svcParamMap.get("bts_nam").toString();
			wrk_dt = svcParamMap.get("wrk_dt").toString();
			String interval = (String) svcParamMap.get("interval");
			String grpNm = Constants.GROUP_BACKUP + "::" + bms_nam+bts_nam;
			String jobNm, triggerNm;
			try {
				// 1.Excute
				triggerNm = Constants.TRGG_FULL_BACKUP_EXCUTE + "::" + bms_nam+bts_nam + "::" + wrk_dt;
				jobNm = Constants.JOB_FULL_BACKUP_EXCUTE + "::" + bms_nam+bts_nam + "::" + wrk_dt;
				trgg = new CronTrigger(triggerNm, grpNm, interval);
//				trgg.setStartTime(new Date());
//				((SimpleTrigger) trgg).setRepeatInterval(Constants.INTERVAL_1DAY);
				job = new JobDetail(jobNm, grpNm, ScheduleFullBackup.class);
				job.getJobDataMap().put("svcParamMap", svcParamMap);
				sched.scheduleJob(job, trgg);
				
			} catch (SchedulerException se) {
				se.printStackTrace();
				log.error("schFullBackup SchedulerException : ", se.fillInStackTrace());
			}
		}
	}
	
	public void schEndChk() throws Exception {
		
		//CronTrigger trigger;
		JobDetail job;
		Trigger trgg;
		String bms_nam, bts_nam, wrk_dt;
		if (svcParamMap != null) {
			bms_nam = svcParamMap.get("bms_nam").toString();
			bts_nam = svcParamMap.get("bts_nam").toString();
			wrk_dt = svcParamMap.get("wrk_dt").toString();
			long interval = (long) svcParamMap.get("interval");
			String grpNm = Constants.GROUP_BACKUP + "::" + bms_nam+bts_nam;
			String jobNm, triggerNm;
			try {
				// 1.Excute
				triggerNm = Constants.TRGG_INCRE_BACKUP_EXCUTE + "::" + bms_nam+bts_nam + "::" + wrk_dt;
				jobNm = Constants.JOB_INCRE_BACKUP_EXCUTE + "::" + bms_nam+bts_nam + "::" + wrk_dt;
				trgg = new SimpleTrigger(triggerNm, grpNm);
				((SimpleTrigger) trgg).setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
//				trgg.setStartTime(new Date(System.currentTimeMillis() + 1000L));
				trgg.setStartTime(new Date());
//				((SimpleTrigger) trgg).setRepeatInterval(Constants.INTERVAL_1DAY);
				((SimpleTrigger) trgg).setRepeatInterval(interval);
				job = new JobDetail(jobNm, grpNm, ScheduleEndChk.class);
				job.getJobDataMap().put("svcParamMap", svcParamMap);
				sched.scheduleJob(job, trgg);
				
			} catch (SchedulerException se) {
				se.printStackTrace();
				log.error("schEndChk SchedulerException : ", se.fillInStackTrace());
			}
		}
	}
	
	public boolean isShutdown() {
		boolean rlt = false;
		try {
			rlt = sched.isShutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("isShutdown SchedulerException : ", e.fillInStackTrace());
		}
		return rlt;
	}

	public boolean isStarted() {
		boolean rlt = false;
		try {
			rlt = sched.isStarted();
		} catch (SchedulerException e) {
			e.printStackTrace();
			log.error("isStarted SchedulerException : ", e.fillInStackTrace());
		}
		return rlt;
	}
	
	
}
