package gb.common.util;

public class Constants {

	/*
	 * Scheduler Periodic String CronTrigger Periodic
	 */
	public static final String PERIODIC_2SEC = "0/2 * * * * ?";
	public static final String PERIODIC_5SEC = "0/5 * * * * ?";
	public static final String PERIODIC_10SEC = "0/10 * * * * ?";
	public static final String PERIODIC_15SEC = "0/15 * * * * ?";
	public static final String PERIODIC_20SEC = "0/20 * * * * ?";
	public static final String PERIODIC_30SEC = "0/30 * * * * ?";
	public static final String PERIODIC_1MIN = "0 0-59 * * * ?";
	public static final String PERIODIC_5MIN = "0 0/5 * * * ?";
	public static final String PERIODIC_10MIN = "0 0/10 * * * ?";
	public static final String PERIODIC_1HOUR = "0 0 0-23 * * ?";
	public static final String PERIODIC_1DAY = "0 0 1-31 * * ?";

	/*
	 * Scheduler Periodic long SimpleTrigger Interval 24 hours * 60(minutes per
	 * hour) * 60(seconds per minute) * 1000(milliseconds per second)
	 */
	public static final long INTERVAL_5SEC = 5000L;
	public static final long INTERVAL_10SEC = 10L * 1000L;
	public static final long INTERVAL_1MIN = 60L * 1000L;
	public static final long INTERVAL_1HOUR = 60L * 60L * 1000L;
	public static final long INTERVAL_1DAY = 24L * 60L * 60L * 1000L;

	// 주기단위
	public static final int PERIODIC_UNIT_SECOND = 0;
	public static final int PERIODIC_UNIT_MINUTE = 1;
	public static final int PERIODIC_UNIT_HOUR = 2;
	public static final int PERIODIC_UNIT_DAY = 3;
	public static final int PERIODIC_UNIT_WEEK = 4;
	public static final int PERIODIC_UNIT_MONTH = 5;
	public static final int PERIODIC_UNIT_YEAR = 6;
	public static final int PERIODIC_UNIT_ONDEMAND = 7;

	// 구분자(Delimiter)
	public static final String BACKUP_SEPARATOR = " ";
	public static final String DATA_DELIMITER = "|";
	public static final String DATA_NEW_LINE = "\n";
	public static final String DATA_COMMA = ",";
	public static final String SYSTEM_SEPARATOR = System.getProperty("file.separator");

	// Scheduler Job Group
	public static final String GROUP_BACKUP = "GroupBackup";
	public static final String GROUP_RESTORE = "GroupRestore";

	// Scheduler Job
	public static final String JOB_FULL_BACKUP_EXCUTE = "JobFullBackupExcute";
	public static final String JOB_INCRE_BACKUP_EXCUTE = "JobIncreBackupExcute";
	public static final String JOB_RESTORE_EXCUTE = "JobRestoreExcute";
//	public static final String JOB_BACKUP_STATUS = "JobBackupStatus";
//	public static final String JOB_RESTORE_STATUS = "JobRestoreStatus";

	// Scheduler Trigger
	public static final String TRGG_FULL_BACKUP_EXCUTE = "TrggFullBackupExcute";
	public static final String TRGG_INCRE_BACKUP_EXCUTE = "TrggIncreBackupExcute";
	public static final String TRGG_RESTORE_EXCUTE = "TrggRestoreExcute";
//	public static final String TRGG_BACKUP_STATUS = "TrggBackupStatus";
//	public static final String TRGG_RESTORE_STATUS = "TrggRestoreStatus";
	
	//
	public static final String CRONTAB_FULL_BACKUP = "fullbackup.sh";
	public static final String CRONTAB_INCR_BACKUP = "incrbackup.sh";
}
