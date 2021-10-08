<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="ko"> 
<head>
<title>MySQLdump Options Help</title>

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
	    b {font-family:"Noto Sans KR","돋움","굴림","맑은고딕",sans-serif; line-height:1.6; font-size:14px; color:#007f7f;cursor:pointer}
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
      MySQLdump Option Manual
    </h1>
MySQLdump  Ver 10.15 Distrib 10.0.15-MariaDB, for Linux (x86_64)<br />
Copyright (c) 2000, 2014, Oracle, SkySQL Ab and others.
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
<!--         <ul> -->
<!--           <li> -->
<!--             <a href="#Creating_a_new_event">2.1 Creating a new event</a> -->
<!--           </li> -->
<!--           <li> -->
<!--             <a href="#Editing_a_event">2.2 Editing or Printing an event</a> -->
<!--           </li> -->
<!--           <li> -->
<!--             <a href="#Deleting_events">2.3 Deleting events</a> -->
<!--           </li> -->
<!--           <li> -->
<!--             <a href="#Alarm">2.4 Alarm</a> -->
<!--           </li> -->
<!--           <li> -->
<!--             <a href="#Searching_for_events">2.5 Searching for events</a> -->
<!--           </li> -->
<!--           <li> -->
<!--             <a href="#Predefined_colours">2.6 Predefined Colours</a> -->
<!--           </li> -->
<!--           <li> -->
<!--             <a href="#Export_and_Import">2.7 Export and Import events to/from file</a> -->
<!--           </li> -->
<!--           <li> -->
<!--             <a href="#Synchronisation">2.8 Synchronisation</a> -->
<!--           </li> -->
<!--           <li> -->
<!--             <a href="#Keyboard">2.9 Using the Keyboard</a> -->
<!--           </li> -->
<!--         </ul> -->
    </ul>
    <a name="Introduction"></a> 
    <h2>
      1. Introduction
    </h2>
    <p>
The MySQLdump client can be used to dump a database or a collection of
databases for backup or for transferring the data to another SQL server
(not necessarily a MySQL server). The dump contains SQL statements to
create the table and/or populate the table.    
    </p>
    <p>
      <a href="#top">[top]</a>
    </p>
    <a name="Options List"></a> 
    <h2>
      2. Options List
    </h2>
    <p>
Dumping structure and contents of MySQL databases and tables.<br />
Usage: MySQLdump [OPTIONS] database [tables]<br />
OR     MySQLdump [OPTIONS] --databases [OPTIONS] DB1 [DB2 DB3...]<br />
OR     MySQLdump [OPTIONS] --all-databases [OPTIONS]<br />
<br />
Default options are read from the following files in the given order:<br />
/etc/my.cnf ~/.my.cnf<br />
The following groups are read: MySQLdump client client-server client-mariadb.<br />
The following options may be given as the first argument:<br />
    </p>
	<ul id="ulList">
		<li><b>--print-defaults</b><br />        Print the program argument list and exit.</li><br />
		<li><b>--no-defaults</b><br />           Don't read default options from any option file.</li><br />
		<li><b>--defaults-file=#</b><br />       Only read default options from the given file #.</li><br />
		<li><b>--defaults-extra-file=#</b><br /> Read this file after the global files are read.</li><br />   
		<li><b>-A, --all-databases</b><br /> Dump all the databases. This will be same as --databases with all databases selected.</li><br />
		<li><b>-Y, --all-tablespaces</b><br />
		Dump all the tablespaces.</li><br />
		<li><b>-y, --no-tablespaces</b><br />
		Do not dump any tablespace information.</li><br />
		<li><b>--add-drop-database</b><br /> Add a DROP DATABASE before each create.</li><br />
		<li><b>--add-drop-table</b><br />    Add a DROP TABLE before each create.(Defaults to on; use --skip-add-drop-table to disable.)</li><br />
		<li><b>--add-locks</b><br />         Add locks around INSERT statements.(Defaults to on; use --skip-add-locks to disable.)</li><br />
		<li><b>--allow-keywords</b><br />    Allow creation of column names that are keywords.</li><br />
		<li><b>--apply-slave-statements</b><br />
		Adds 'STOP SLAVE' prior to 'CHANGE MASTER' and 'START SLAVE' to bottom of dump.</li><br />
		<li><b>--character-sets-dir=name</b><br />
		Directory for character set files.</li><br />
		<li><b>-i, --comments</b><br />      Write additional information.(Defaults to on; use --skip-comments to disable.)</li><br />
		<li><b>--compatible=name</b><br />   Change the dump to be compatible with a given mode. By
		default tables are dumped in a format optimized for MySQL. Legal modes are: ansi, mysql323, mysql40, postgresql, oracle, mssql, db2, maxdb, no_key_options, 
		no_table_options, no_field_options. One can use several modes separated by commas. Note: Requires MySQL server 
		version 4.1.0 or higher. This option is ignored with earlier server versions.</li><br />
		<li><b>--compact</b><br />           Give less verbose output (useful for debugging). Disables structure comments and header/footer constructs.  Enables 
		options --skip-add-drop-table --skip-add-locks --skip-comments --skip-disable-keys --skip-set-charset.</li><br />
		<li><b>-c, --complete-insert</b><br />
		Use complete insert statements.</li><br />
		<li><b>-C, --compress</b><br />      Use compression in server/client protocol.</li><br />
		<li><b>-a, --create-options</b><br />
		Include all MySQL specific create options.(Defaults to on; use --skip-create-options to disable.)</li><br />
		<li><b>-B, --databases</b><br />     Dump several databases. Note the difference in usage; in
		this case no tables are given. All name arguments are regarded as database names. 'USE db_name;' will be included in the output.</li><br />
		<li><b>-#, --debug[=#]</b><br />     This is a non-debug version. Catch this and exit.</li><br />
		<li><b>--debug-check</b><br />       Check memory and open file usage at exit.</li><br />
		<li><b>--debug-info</b><br />        Print some debug info at exit.</li><br />
		<li><b>--default-character-set=name</b><br />
		Set the default character set.</li><br />
		<li><b>--delayed-insert</b><br />    Insert rows with INSERT DELAYED.</li><br />
		<li><b>--delete-master-logs</b><br />
		Delete logs on master after backup. This automatically enables --master-data.</li><br />
		<li><b>-K, --disable-keys</b><br />  '/*!40000 ALTER TABLE tb_name DISABLE KEYS */; and <br />'/*!40000 ALTER TABLE tb_name ENABLE KEYS */; will be put 
		in the output.(Defaults to on; use --skip-disable-keys to disable.)</li><br />
		<li><b>--dump-slave[=#]</b><br />    This causes the binary log position and filename of the 
		master to be appended to the dumped data output. Setting 
		the value to 1, will printit as a CHANGE MASTER command 
		in the dumped data output; if equal to 2, that command 
		will be prefixed with a comment symbol. This option will 
		turn --lock-all-tables on, unless --single-transaction is 
		specified too (in which case a global read lock is only 
		taken a short time at the beginning of the dump - don't 
		forget to read about --single-transaction below). In all 
		cases any action on logs will happen at the exact moment 
		of the dump.Option automatically turns --lock-tables off.</li><br />
		<li><b>-E, --events</b><br />        Dump events.</li><br />
		<li><b>-e, --extended-insert</b><br />
		Use multiple-row INSERT syntax that include several VALUES lists.
		(Defaults to on; use --skip-extended-insert to disable.)</li><br />
		<li><b>--fields-terminated-by=name</b><br />
		Fields in the output file are terminated by the given string.</li><br />
		<li><b>--fields-enclosed-by=name</b><br />
		Fields in the output file are enclosed by the given character.</li><br />
		<li><b>--fields-optionally-enclosed-by=name</b><br />
		Fields in the output file are optionally enclosed by the given character.</li><br />
		<li><b>--fields-escaped-by=name</b><br />
		Fields in the output file are escaped by the given character.</li><br />
		<li><b>-F, --flush-logs</b><br />    Flush logs file in server before starting dump. Note that 
		if you dump many databases at once (using the option --databases= or --all-databases), the logs will be
		flushed for each database dumped. The exception is when 
		using --lock-all-tables or --master-data: in this case 
		the logs will be flushed only once, corresponding to the 
		moment all tables are locked. So if you want your dump 
		and the log flush to happen at the same exact moment you 
		should use --lock-all-tables or --master-data with 
		--flush-logs.</li><br />
		<li><b>--flush-privileges</b><br />  Emit a FLUSH PRIVILEGES statement after dumping the mysql 
		database.  This option should be used any time the dump 
		contains the mysql database and any other database that 
		depends on the data in the mysql database for proper restore.</li><br />
		<li><b>-f, --force</b><br />         Continue even if we get an SQL error.</li><br />
		<li><b>--galera-sst-mode</b><br />   This mode should normally be used in MySQLdump snapshot 
		state transfer (SST) in a Galera cluster. If enabled, 
		MySQLdump additionally dumps commands to turn off binary 
		logging and SET global gtid_binlog_state with the current 
		value. Note: RESET MASTER needs to be executed on the 
		server receiving the resulting dump.</li><br />
		<li><b>--gtid</b><br />              Used together with --master-data=1 or --dump-slave=1.When 
		enabled, the output from those options will set the GTID 
		position instead of the binlog file and offset; the 
		file/offset will appear only as a comment. When disabled, 
		the GTID position will still appear in the output, but 
		only commented.</li><br />
		<li><b>-?, --help</b><br />          Display this help message and exit.</li><br />
		<li><b>--hex-blob</b><br />          Dump binary strings (BINARY, VARBINARY, BLOB) in hexadecimal format.</li><br />
		<li><b>-h, --host=name</b><br />     Connect to host.</li><br />
		<li><b>--ignore-table=name</b><br /> Do not dump the specified table. To specify more than one 
		table to ignore, use the directive multiple times, once 
		for each table.  Each table must be specified with both 
		database and table names, e.g., 
		--ignore-table=database.table.</li><br />
		<li><b>--include-master-host-port</b><br />
		Adds 'MASTER_HOST=&lt;host&gt;, MASTER_PORT=&lt;port&gt;' to 'CHANGE MASTER TO..' in dump produced with --dump-slave.</li><br />
		<li><b>--insert-ignore</b><br />     Insert rows with INSERT IGNORE.</li><br />
		<li><b>--lines-terminated-by=name</b><br />
		Lines in the output file are terminated by the given string.</li><br />
		<li><b>-x, --lock-all-tables</b><br />
		Locks all tables across all databases. This is achieved 
		by taking a global read lock for the duration of the 
		whole dump. Automatically turns --single-transaction and 
		--lock-tables off.</li><br />
		<li><b>-l, --lock-tables</b><br />   Lock all tables for read.(Defaults to on; use --skip-lock-tables to disable.)</li><br />
		<li><b>--log-error=name</b><br />    Append warnings and errors to given file.</li><br />
		<li><b>--master-data[=#]</b><br />   This causes the binary log position and filename to be 
		appended to the output. If equal to 1, will print it as a 
		CHANGE MASTER command; if equal to 2, that command will 
		be prefixed with a comment symbol. This option will turn 
		--lock-all-tables on, unless --single-transaction is 
		specified too (on servers before MariaDB 5.3 this will 
		still take a global read lock for a short time at the 
		beginning of the dump; don't forget to read about 
		--single-transaction below). In all cases, any action on 
		logs will happen at the exact moment of the dump. Option 
		automatically turns --lock-tables off.</li><br />
		<li><b>--max-allowed-packet=#</b><br />
		The maximum packet length to send to or receive from server.</li><br />
		<li><b>--net-buffer-length=#</b><br />
		The buffer size for TCP/IP and socket communication.</li><br />
		<li><b>--no-autocommit</b><br />     Wrap tables with autocommit/commit statements.</li><br />
		<li><b>-n, --no-create-db</b><br />  Suppress the CREATE DATABASE ... IF EXISTS statement that normally is output for each dumped database if 
		--all-databases or --databases is given.</li><br />
		<li><b>-t, --no-create-info</b><br />
		Don't write table creation info.</li><br />
		<li><b>-d, --no-data</b><br />       No row information.</li><br />
		<li><b>-N, --no-set-names</b><br />  Same as --skip-set-charset.</li><br />
		<li><b>--opt</b><br />               Same as --add-drop-table, --add-locks, --create-options, 
		--quick, --extended-insert, --lock-tables, --set-charset, 
		and --disable-keys. Enabled by default, disable with --skip-opt.</li><br />
		<li><b>--order-by-primary </b><br /> Sorts each table's rows by primary key, or first unique 
		key, if such a key exists.  Useful when dumping a MyISAM 
		table to be loaded into an InnoDB table, but will make 
		the dump itself take considerably longer.</li><br />
		<li><b>-p, --password[=name]</b><br />
		Password to use when connecting to server. If password is not given it's solicited on the tty.</li><br />
		<li><b>-P, --port=#</b><br />        Port number to use for connection.</li><br />
		<li><b>--protocol=name</b><br />     The protocol to use for connection (tcp, socket, pipe, memory).</li><br />
		<li><b>-q, --quick</b><br />         Don't buffer query, dump directly to stdout. 
		(Defaults to on; use --skip-quick to disable.)</li><br />
		<li><b>-Q, --quote-names</b><br />   Quote table and column names with backticks (`).(Defaults to on; use --skip-quote-names to disable.)</li><br />
		<li><b>--replace</b><br />           Use REPLACE INTO instead of INSERT INTO.</li><br />
		<li><b>-r, --result-file=name</b><br />
		Direct output to a given file. This option should be used 
		in systems (e.g., DOS, Windows) that use carriage-return 
		linefeed pairs (\r\n) to separate text lines. This option 
		ensures that only a single newline is used.</li><br />
		<li><b>-R, --routines</b><br />      Dump stored routines (functions and procedures).</li><br />
		<li><b>--set-charset</b><br />       Add 'SET NAMES default_character_set' to the output.(Defaults to on; use --skip-set-charset to disable.)</li><br />
		<li><b>--single-transaction</b><br />
		Creates a consistent snapshot by dumping all tables in a 
		single transaction. Works ONLY for tables stored in 
		storage engines which support multiversioning (currently only InnoDB does); the dump is NOT guaranteed to be 
		consistent for other storage engines. While a 
		--single-transaction dump is in process, to ensure a 
		valid dump file (correct table contents and binary log 
		position), no other connection should use the following 
		statements: ALTER TABLE, DROP TABLE, RENAME TABLE, 
		TRUNCATE TABLE, as consistent snapshot is not isolated 
		from them. Option automatically turns off --lock-tables.</li><br />
		<li><b>--dump-date</b><br />         Put a dump date to the end of the output.(Defaults to on; use --skip-dump-date to disable.)</li><br />
		<li><b>--skip-opt</b><br />          Disable --opt. Disables --add-drop-table, --add-locks, 
		--create-options, --quick, --extended-insert, 
		--lock-tables, --set-charset, and --disable-keys.</li><br />
		<li><b>-S, --socket=name</b><br />   The socket file to use for connection.</li><br />
		<li><b>--ssl</b><br />               Enable SSL for connection (automatically enabled with other flags).</li><br />
		<li><b>--ssl-ca=name</b><br />       CA file in PEM format (check OpenSSL docs, implies --ssl).</li><br />
		<li><b>--ssl-capath=name</b><br />   CA directory (check OpenSSL docs, implies --ssl).</li><br />
		<li><b>--ssl-cert=name</b><br />     X509 cert in PEM format (implies --ssl).</li><br />
		<li><b>--ssl-cipher=name</b><br />   SSL cipher to use (implies --ssl).</li><br />
		<li><b>--ssl-key=name</b><br />      X509 key in PEM format (implies --ssl).</li><br />
		<li><b>--ssl-crl=name</b><br />      Certificate revocation list (implies --ssl).</li><br />
		<li><b>--ssl-crlpath=name</b><br />  Certificate revocation list path (implies --ssl).</li><br />
		<li><b>--ssl-verify-server-cert</b><br />
		Verify server's &quot;Common Name&quot; in its cert against hostname used when connecting. This option is disabled by default.</li><br />
		<li><b>-T, --tab=name</b><br />      Create tab-separated textfile for each table to given path. (Create .sql and .txt files.) NOTE: This only works 
		if MySQLdump is run on the same machine as the mysqld server.</li><br />
		<li><b>--tables</b><br />            Overrides option --databases (-B).</li><br />
		<li><b>--triggers</b><br />          Dump triggers for each dumped table.(Defaults to on; use --skip-triggers to disable.)</li><br />
		<li><b>--tz-utc</b><br />            SET TIME_ZONE='+00:00' at top of dump to allow dumping of 
		TIMESTAMP data when a server has data in different time 
		zones or data is being moved between servers with different time zones.
		(Defaults to on; use --skip-tz-utc to disable.)</li><br />
		<li><b>-u, --user=name</b><br />     User for login if not current user.</li><br />
		<li><b>-v, --verbose</b><br />       Print info about the various stages.</li><br />
		<li><b>-V, --version</b><br />       Output version information and exit.</li><br />
		<li><b>-w, --where=name</b><br />    Dump only selected records. Quotes are mandatory.</li><br />
		<li><b>-X, --xml</b><br />           Dump a database as well formed XML.</li><br />
		<li><b>--plugin-dir=name</b><br />   Directory for client-side plugins.</li><br />
		<li><b>--default-auth=name</b><br /> Default authentication client-side plugin to use.</li>
	</ul>    
    <p>
      <a href="#top">[top]</a>
    </p>
    <script type="text/javascript">
  		$("#ulList li b").click(function() {
	    	opener.setChildValue($(this).text(), "mysql");
		});
 	</script> 
</body>
</html>

