<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="ko"> 
<head>
<title>XtraBackup Options Help</title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="user-scalable=yes, maximum_scale=1, minimum_scale=1.0 ,width=1240px, target_densitydpi=device-dpi">
<meta name="title" content="GINIAN">
<meta name="author" content="그로윈">
<meta name="keywords" content="지니안, ginian, MariaDB, mysql, backup solution, 백업솔루션">
<meta name="subject" content="GINIAN">
<meta name="Description" content="GINIAN Recovery Manager For MariaDB & Mysql">
<meta name="classification" content="secutiry">
<script type="text/javascript" src="/webdoc/script/jquery.1.12.0.min.js"></script>

<style type="text/css">
    body {font-size:14px; font-family:"Noto Sans KR","돋움","굴림","맑은고딕",sans-serif; line-height:1.6;}
    h1 {font-family:"Noto Sans KR","돋움","굴림","맑은고딕",sans-serif; line-height:1.6; font-size:20px; color:#00007f}
    h2 {font-family:"Noto Sans KR","돋움","굴림","맑은고딕",sans-serif; line-height:1.6; font-size:14px; color:#00007f}
    p {font-family:"Noto Sans KR","돋움","굴림","맑은고딕",sans-serif; line-height:1.6; font-size:14px; color:#000000}
    b {font-family:"Noto Sans KR","돋움","굴림","맑은고딕",sans-serif; line-height:1.6; font-size:14px; color:#007f7f ;cursor:pointer}
    td {font-family:"Noto Sans KR","돋움","굴림","맑은고딕",sans-serif; line-height:1.6; font-size:14px; color:#00000; vertical-align:top; text-align:left; padding:3px}
    th {font-family:"Noto Sans KR","돋움","굴림","맑은고딕",sans-serif; line-height:1.6; font-size:14px; color:#00000; vertical-align:top; text-align:left; padding:3px}
    a {text-decoration:none;}
	a:link {color:#555;}
	a:visited {color:#555;}
	a:hover {color:#000; cursor:pointer;}
	a:active {color:#000;}
	a {text-decoration:none !important; outline:none !important; selector-dummy:expression(this.hideFocus=true) !important;}
</style>
  </head>
  <body>
    <a name="top"></a>
    <h1>
      XtraBackup Option Manual
    </h1>
<!-- XtraBackup -->
    <h2>
      0. Content
    </h2>
    <ul>
      <li>
        <a href="#Introduction">1. Introduction</a>
      </li>
      <li>
        <a href="#Options List">2. Options List</a>
      </li>
    </ul>
    <a name="Introduction"></a> 
    <h2>
      1. Introduction
    </h2>
    <p>
Percona XtraBackup is the world’s only open-source, free MySQL hot backup software that performs non-blocking backups for InnoDB and XtraDB databases. With Percona XtraBackup, you can achieve the following benefits:
<ul>
    <li>Backups that complete quickly and reliably</li>
    <li>Uninterrupted transaction processing during backups</li>
    <li>Savings on disk space and network bandwidth</li>
    <li>Automatic backup verification</li>
    <li>Higher uptime due to faster restore time</li>
</ul>
<br />
Percona XtraBackup makes MySQL hot backups for all versions of Percona Server, MySQL, and MariaDB. It performs streaming, compressed, and incremental MySQL backups.
<br /><br />
Percona XtraBackup works with MySQL, MariaDB, and Percona Server. It supports completely non-blocking backups of InnoDB, XtraDB, and HailDB storage engines. In addition, it can back up the following storage engines by briefly pausing writes at the end of the backup: MyISAM, Merge, and Archive, including partitioned tables, triggers, and database options. 
    </p>
    <p>
      <a href="#top">[top]</a>
    </p>
    <a name="Options List"></a> 
    <h2>
      2. Options List
    </h2>
	<ul id="ulList">
		<li><b>--apply-log</b><br />
			Prepare a backup in BACKUP-DIR by applying the transaction log file
			named &quot;xtrabackup_logfile&quot; located in the same directory. Also,
			create new transaction logs. The InnoDB configuration is read from
			the file &quot;backup-my.cnf&quot;.
		</li><br />
		<li><b>--backup-locks</b><br />
			This option controls if backup locks should be used instead of FLUSH
			TABLES WITH READ LOCK on the backup stage. The option has no effect
			when backup locks are not supported by the server. This option is
			enabled by default, disable with --no-backup-locks.
		</li><br />
		<li><b>--close-files</b><br />
			Do not keep files opened. This option is passed directly to
			xtrabackup. Use at your own risk.
		</li><br />
		<li><b>--compact</b><br />
			Create a compact backup with all secondary index pages omitted. This
			option is passed directly to xtrabackup. See xtrabackup
			documentation for details.
		</li><br />
		<li><b>--compress</b><br />
			This option instructs xtrabackup to compress backup copies of InnoDB
			data files. It is passed directly to the xtrabackup child process.
			Try 'xtrabackup --help' for more details.
		</li><br />
		<li><b>--compress-threads</b><br />
			This option specifies the number of worker threads that will be used
			for parallel compression. It is passed directly to the xtrabackup
			child process. Try 'xtrabackup --help' for more details.
		</li><br />
		<li><b>--compress-chunk-size</b><br />
			This option specifies the size of the internal working buffer for
			each compression thread, measured in bytes. It is passed directly to
			the xtrabackup child process. Try 'xtrabackup --help' for more
			details.
		</li><br />
		<li><b>--copy-back</b><br />
			Copy all the files in a previously made backup from the backup
			directory to their original locations.
		</li><br />
		<li><b>--databases=LIST</b><br />
			This option specifies the list of databases that innobackupex should
			back up. The option accepts a string argument or path to file that
			contains the list of databases to back up. The list is of the form
			&quot;databasename1[.table_name1] databasename2[.table_name2] . . .&quot;. If
			this option is not specified, all databases containing MyISAM and
			InnoDB tables will be backed up. Please make sure that --databases
			contains all of the InnoDB databases and tables, so that all of the
			innodb.frm files are also backed up. In case the list is very long,
			this can be specified in a file, and the full path of the file can
			be specified instead of the list. (See option --tables-file.)
		</li><br />
		<li><b>--decompress</b><br />
			Decompresses all files with the .qp extension in a backup previously
			made with the --compress option.
		</li><br />
		<li><b>--decrypt=ENCRYPTION-ALGORITHM</b><br />
			Decrypts all files with the .xbcrypt extension in a backup
			previously made with --encrypt option.
		</li><br />
		<li><b>--debug-sleep-before-unlock=SECONDS</b><br />
			This is a debug-only option used by the XtraBackup test suite.
		</b><br />
		<li><b>--defaults-file=[MY.CNF]</b><br />
			This option specifies what file to read the default MySQL options
			from. The option accepts a string argument. It is also passed
			directly to xtrabackup's --defaults-file option. See the xtrabackup
			documentation for details.
		</li><br />
		<li><b>--defaults-group=GROUP-NAME</b><br />
			This option specifies the group name in my.cnf which should be used.
			This is needed for mysqld_multi deployments.
		</li><br />
		<li><b>--defaults-extra-file=[MY.CNF]</b><br />
			This option specifies what extra file to read the default MySQL
			options from before the standard defaults-file. The option accepts a
			string argument. It is also passed directly to xtrabackup's
			--defaults-extra-file option. See the xtrabackup documentation for
			details.
		</li><br />
		<li><b>--encrypt=ENCRYPTION-ALGORITHM</b><br />
			This option instructs xtrabackup to encrypt backup copies of InnoDB
			data files using the algorithm specified in the
			ENCRYPTION-ALGORITHM. It is passed directly to the xtrabackup child
			process. Try 'xtrabackup --help' for more details.
		</li><br />
		<li><b>--encrypt-key=ENCRYPTION-KEY</b><br />
			This option instructs xtrabackup to use the given ENCRYPTION-KEY
			when using the --encrypt or --decrypt options. During backup it is
			passed directly to the xtrabackup child process. Try 'xtrabackup
			--help' for more details.
		</li><br />
		<li><b>--encrypt-key-file=ENCRYPTION-KEY-FILE</b><br />
			This option instructs xtrabackup to use the encryption key stored in
			the given ENCRYPTION-KEY-FILE when using the --encrypt or --decrypt
			options.
		<br /><br />
			Try 'xtrabackup --help' for more details.
		</li><br />
		<li><b>--encrypt-threads</b><br />
			This option specifies the number of worker threads that will be used
			for parallel encryption. It is passed directly to the xtrabackup
			child process. Try 'xtrabackup --help' for more details.
		</li><br />
		<li><b>--encrypt-chunk-size</b><br />
			This option specifies the size of the internal working buffer for
			each encryption thread, measured in bytes. It is passed directly to
			the xtrabackup child process. Try 'xtrabackup --help' for more
			details.
		</li><br />
		<li><b>--export</b><br />
			This option is passed directly to xtrabackup's --export option. It
			enables exporting individual tables for import into another server.
			See the xtrabackup documentation for details.
		</li><br />
		<li><b>--extra-lsndir=DIRECTORY</b><br />
			This option specifies the directory in which to save an extra copy
			of the &quot;xtrabackup_checkpoints&quot; file. The option accepts a string
			argument. It is passed directly to xtrabackup's --extra-lsndir
			option. See the xtrabackup documentation for details.
		<br /><br />
			==item --force-non-empty-directories
		<br /><br />
			This option, when specified, makes --copy-back or --move-back
			transfer files to non-empty directories. Note that no existing files
			will be overwritten. If --copy-back or --nove-back has to copy a
			file from the backup directory which already exists in the
			destination directory, it will still fail with an error.
		</li><br />
		<li><b>--galera-info</b><br />
			This options creates the xtrabackup_galera_info file which contains
			the local node state at the time of the backup. Option should be
			used when performing the backup of Percona-XtraDB-Cluster. Has no
			effect when backup locks are used to create the backup.
		</li><br />
		<li><b>--help</b><br />
			This option displays a help screen and exits.
		</li><br />
		<li><b>--history=NAME</b><br />
			This option enables the tracking of backup history in the
			PERCONA_SCHEMA.xtrabackup_history table. An optional history series
			name may be specified that will be placed with the history record
			for the current backup being taken.
		</li><br />
		<li><b>--host=HOST</b><br />
			This option specifies the host to use when connecting to the
			database server with TCP/IP. The option accepts a string argument.
			It is passed to the mysql child process without alteration. See
			mysql --help for details.
		</li><br />
		<li><b>--ibbackup=IBBACKUP-BINARY</b><br />
			This option specifies which xtrabackup binary should be used. The
			option accepts a string argument. IBBACKUP-BINARY should be the
			command used to run Percona XtraBackup. The option can be useful if
			the xtrabackup binary is not in your search path or working
			directory. If this option is not specified, innobackupex attempts to
			determine the binary to use automatically.
		</li><br />
		<li><b>--include=REGEXP</b><br />
			This option is a regular expression to be matched against table
			names in databasename.tablename format. It is passed directly to
			xtrabackup's --tables option. See the xtrabackup documentation for
			details.
		</li><br />
		<li><b>--incremental</b><br />
			This option tells xtrabackup to create an incremental backup, rather
			than a full one. It is passed to the xtrabackup child process. When
			this option is specified, either --incremental-lsn or
			--incremental-basedir can also be given. If neither option is given,
			option --incremental-basedir is passed to xtrabackup by default, set
			to the first timestamped backup directory in the backup base
			directory.
		</li><br />
		<li><b>--incremental-basedir=DIRECTORY</b><br />
			This option specifies the directory containing the full backup that
			is the base dataset for the incremental backup. The option accepts a
			string argument. It is used with the --incremental option.
		</li><br />
		<li><b>--incremental-dir=DIRECTORY</b><br />
			This option specifies the directory where the incremental backup
			will be combined with the full backup to make a new full backup. The
			option accepts a string argument. It is used with the --incremental
			option.
		</li><br />
		<li><b>--incremental-history-name=NAME</b><br />
			This option specifies the name of the backup series stored in the
			PERCONA_SCHEMA.xtrabackup_history history record to base an
			incremental backup on. Xtrabackup will search the history table
			looking for the most recent (highest innodb_to_lsn), successful
			backup in the series and take the to_lsn value to use as the
			starting lsn for the incremental backup. This will be mutually
			exclusive with --incremental-history-uuid, --incremental-basedir and
			--incremental-lsn. If no valid lsn can be found (no series by that
			name, no successful backups by that name) xtrabackup will return
			with an error. It is used with the --incremental option.
		</li><br />
		<li><b>--incremental-history-uuid=UUID</b><br />
			This option specifies the UUID of the specific history record stored
			in the PERCONA_SCHEMA.xtrabackup_history to base an incremental
			backup on. --incremental-history-name, --incremental-basedir and
			--incremental-lsn. If no valid lsn can be found (no success record
			with that uuid) xtrabackup will return with an error. It is used
			with the --incremental option.
		</li><br />
		<li><b>--incremental-force-scan</b><br />
			This options tells xtrabackup to perform full scan of data files for
			taking an incremental backup even if full changed page bitmap data
			is available to enable the backup without the full scan.
		</li><br />
		<li><b>--log-copy-interval</b><br />
			This option specifies time interval between checks done by log
			copying thread in milliseconds.
		</li><br />
		<li><b>--incremental-lsn</b><br />
			This option specifies the log sequence number (LSN) to use for the
			incremental backup. The option accepts a string argument. It is used
			with the --incremental option. It is used instead of specifying
			--incremental-basedir. For databases created by MySQL and Percona
			Server 5.0-series versions, specify the LSN as two 32-bit integers
			in high:low format. For databases created in 5.1 and later, specify
			the LSN as a single 64-bit integer.
		</li><br />
		<li><b>--kill-long-queries-timeout=SECONDS</b><br />
			This option specifies the number of seconds innobackupex waits
			between starting FLUSH TABLES WITH READ LOCK and killing those
			queries that block it. Default is 0 seconds, which means
			innobackupex will not attempt to kill any queries.
		</li><br />
		<li><b>--kill-long-query-type=all|update</b><br />
			This option specifies which types of queries should be killed to
			unblock the global lock. Default is &quot;all&quot;.
		</li><br />
		<li><b>--lock-wait-timeout=SECONDS</b><br />
			This option specifies time in seconds that innobackupex should wait
			for queries that would block FTWRL before running it. If there are
			still such queries when the timeout expires, innobackupex terminates
			with an error. Default is 0, in which case innobackupex does not
			wait for queries to complete and starts FTWRL immediately.
		</li><br />
		<li><b>--lock-wait-threshold=SECONDS</b><br />
			This option specifies the query run time threshold which is used by
			innobackupex to detect long-running queries with a non-zero value of
			--lock-wait-timeout. FTWRL is not started until such long-running
			queries exist. This option has no effect if --lock-wait-timeout is
			0. Default value is 60 seconds.
		</li><br />
		<li><b>--lock-wait-query-type=all|update</b><br />
			This option specifies which types of queries are allowed to complete
			before innobackupex will issue the global lock. Default is all.
		</li><br />
		<li><b>--move-back</b><br />
			Move all the files in a previously made backup from the backup
			directory to the actual datadir location. Use with caution, as it
			removes backup files.
		</li><br />
		<li><b>--no-lock</b><br />
			Use this option to disable table lock with &quot;FLUSH TABLES WITH READ
			LOCK&quot;. Use it only if ALL your tables are InnoDB and you DO NOT CARE
			about the binary log position of the backup. This option shouldn't
			be used if there are any DDL statements being executed or if any
			updates are happening on non-InnoDB tables (this includes the system
			MyISAM tables in the mysql database), otherwise it could lead to an
			inconsistent backup. If you are considering to use --no-lock because
			your backups are failing to acquire the lock, this could be because
			of incoming replication events preventing the lock from succeeding.
			Please try using --safe-slave-backup to momentarily stop the
			replication slave thread, this may help the backup to succeed and
			you then don't need to resort to using this option.
		</li><br />
		<li><b>--no-timestamp</b><br />
			This option prevents creation of a time-stamped subdirectory of the
			BACKUP-ROOT-DIR given on the command line. When it is specified, the
			backup is done in BACKUP-ROOT-DIR instead.
		</li><br />
		<li><b>--no-version-check</b><br />
			This option disables the version check which is enabled by the
			--version-check option.
		</li><br />
		<li><b>--parallel=NUMBER-OF-THREADS</b><br />
			On backup, this option specifies the number of threads the
			xtrabackup child process should use to back up files concurrently.
			The option accepts an integer argument. It is passed directly to
			xtrabackup's --parallel option. See the xtrabackup documentation for
			details.
		<br /><br />
			On --decrypt or --decompress it specifies the number of parallel
			forks that should be used to process the backup files.
		</li><br />
		<li><b>--password=WORD</b><br />
			This option specifies the password to use when connecting to the
			database. It accepts a string argument. It is passed to the mysql
			child process without alteration. See mysql --help for details.
		</li><br />
		<li><b>--port=PORT</b><br />
			This option specifies the port to use when connecting to the
			database server with TCP/IP. The option accepts a string argument.
			It is passed to the mysql child process. It is passed to the mysql
			child process without alteration. See mysql --help for details.
		</li><br />
		<li><b>--rebuild-indexes</b><br />
			This option only has effect when used together with the --apply-log
			option and is passed directly to xtrabackup. When used, makes
			xtrabackup rebuild all secondary indexes after applying the log.
			This option is normally used to prepare compact backups. See the
			XtraBackup manual for more information.
		</li><br />
		<li><b>--rebuild-threads</b><br />
			This option only has effect when used together with the --apply-log
			and --rebuild-indexes option and is passed directly to xtrabackup.
			When used, xtrabackup processes tablespaces in parallel with the
			specified number of threads when rebuilding indexes. See the
			XtraBackup manual for more information.
		</li><br />
		<li><b>--redo-only</b><br />
			This option should be used when preparing the base full backup and
			when merging all incrementals except the last one. This option is
			passed directly to xtrabackup's --apply-log-only option. This forces
			xtrabackup to skip the &quot;rollback&quot; phase and do a &quot;redo&quot; only. This
			is necessary if the backup will have incremental changes applied to
			it later. See the xtrabackup documentation for details.
		</li><br />
		<li><b>--rsync</b><br />
			Uses the rsync utility to optimize local file transfers. When this
			option is specified, innobackupex uses rsync to copy all non-InnoDB
			files instead of spawning a separate cp for each file, which can be
			much faster for servers with a large number of databases or tables.
			This option cannot be used together with --stream.
		</li><br />
		<li><b>--safe-slave-backup</b><br />
			Stop slave SQL thread and wait to start backup until
			Slave_open_temp_tables in &quot;SHOW STATUS&quot; is zero. If there are no
			open temporary tables, the backup will take place, otherwise the SQL
			thread will be started and stopped until there are no open temporary
			tables. The backup will fail if Slave_open_temp_tables does not
			become zero after --safe-slave-backup-timeout seconds. The slave SQL
			thread will be restarted when the backup finishes.
		</li><br />
		<li><b>--safe-slave-backup-timeout</b><br />
			How many seconds --safe-slave-backup should wait for
			Slave_open_temp_tables to become zero. (default 300)
		</li><br />
		<li><b>--slave-info</b><br />
			This option is useful when backing up a replication slave server. It
			prints the binary log position and name of the master server. It
			also writes this information to the &quot;xtrabackup_slave_info&quot; file as
			a &quot;CHANGE MASTER&quot; command. A new slave for this master can be set up
			by starting a slave server on this backup and issuing a &quot;CHANGE
			MASTER&quot; command with the binary log position saved in the
			&quot;xtrabackup_slave_info&quot; file.
		</li><br />
		<li><b>--socket=SOCKET</b><br />
			This option specifies the socket to use when connecting to the local
			database server with a UNIX domain socket. The option accepts a
			string argument. It is passed to the mysql child process without
			alteration. See mysql --help for details.
		</li><br />
		<li><b>--stream=STREAMNAME</b><br />
			This option specifies the format in which to do the streamed backup.
			The option accepts a string argument. The backup will be done to
			STDOUT in the specified format. Currently, the only supported
			formats are tar and xbstream. This option is passed directly to
			xtrabackup's --stream option.
		</li><br />
		<li><b>--tables-file=FILE</b><br />
			This option specifies the file in which there are a list of names of
			the form database. The option accepts a string argument.table, one
			per line. The option is passed directly to xtrabackup's
			--tables-file option.
		</li><br />
		<li><b>--throttle=IOS</b><br />
			This option specifies a number of I/O operations (pairs of
			read+write) per second. It accepts an integer argument. It is passed
			directly to xtrabackup's --throttle option.
		</li><br />
		<li><b>--tmpdir=DIRECTORY</b><br />
			This option specifies the location where a temporary file will be
			stored. The option accepts a string argument. It should be used when
			--stream is specified. For these options, the transaction log will
			first be stored to a temporary file, before streaming. This option
			specifies the location where that temporary file will be stored. If
			the option is not specified, the default is to use the value of
			tmpdir read from the server configuration.
		</li><br />
		<li><b>--use-memory=B</b><br />
			This option accepts a string argument that specifies the amount of
			memory in bytes for xtrabackup to use for crash recovery while
			preparing a backup. Multiples are supported providing the unit (e.g.
			1MB, 1GB). It is used only with the option --apply-log. It is passed
			directly to xtrabackup's --use-memory option. See the xtrabackup
			documentation for details.
		</li><br />
		<li><b>--user=NAME</b><br />
			This option specifies the MySQL username used when connecting to the
			server, if that's not the current user. The option accepts a string
			argument. It is passed to the mysql child process without
			alteration. See mysql --help for details.
		</li><br />
		<li><b>--version</b><br />
			This option displays the xtrabackup version and copyright notice and
			then exits.
		</li><br />
		<li><b>--version-check</b><br />
			This option controls if the version check should be executed by
			innobackupex after connecting to the server on the backup stage.
			This option is enabled by default, disable with --no-version-check.
		</li>
	</ul>    
    <p>
      <a href="#top">[top]</a>
    </p>
    <script type="text/javascript">
  		$("#ulList li b").click(function() {
	    	opener.setChildValue($(this).text(),"xtra");
		});
 	</script> 
  </body>
</html>

