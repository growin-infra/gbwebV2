<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<title>MariaBackup Options Help</title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="user-scalable=yes, maximum_scale=1, minimum_scale=1.0 ,width=1240px, target_densitydpi=device-dpi">
<meta name="title" content="GINIAN">
<meta name="author" content="그로윈">
<meta name="keywords" content="지니안, ginian, MariaDB, mysql, backup solution, 백업솔루션">
<meta name="subject" content="GINIAN">
<meta name="Description" content="GINIAN Recovery Manager For MariaDB & Mysql">
<meta name="classification" content="secutiry">
<link type="text/css" href="/webdoc/css/w3c.css" rel="stylesheet">
<script type="text/javascript" src="/webdoc/script/jquery.1.12.0.min.js"></script>
<style type="text/css">
h2.anchored_heading{
	background: #f5f5f5;
	padding: 5px;
	border: 1px solid #d8d8d8;
}
h3.anchored_heading{
	background: #d4f3fd;
	padding: 5px;
	border-color: #94bbeb;
	border: 1px solid #94bbeb;
}
a{
	text-decoration: none;
	background: #d8d8d8;
	color: #3377c0;
}
h3.anchored_heading code{
	color: #3377c0;
	padding: 3px;
}
code a{
	color: #3377c0;
	/* background: #d8d8d8;*/
	padding: 3px;
}
p a{
	color: #3377c0;
	background: #d8d8d8;
	padding: 3px;
}
pre{
	background: #f5f5f5;
	padding: 5px;
	border: 1px solid #d8d8d8;
}
table {
	font-family: Arial, Helvetica, sans-serif;
	border-collapse: collapse;
	width: 100%;
}
td, th {
	border: 1px solid #ddd;
	padding: 8px;
}
th {
	padding-top: 12px;
	padding-bottom: 12px;
	text-align: left;
	background-color: #f5f5f5;
}
body{
	margin: 16px;
}
</style>
</head>
<body>
	<a name="top"></a>
	<div>
		<div class="node creole">
			<div class="answer formatted">
				<h1 class="anchored_heading" id="mariabackup-options">Mariabackup Options</h1>
				<p>
					There are a number of options available in
					<code>Mariabackup</code>
					.
				</p>
				<h2 class="anchored_heading" id="list-of-options">List of
					Options</h2>
				<h3 class="anchored_heading" id="-apply-log">
					<code>--apply-log</code>
				</h3>
				<p>
					Prepares an existing backup to restore to the MariaDB Server. This
					is only valid in <code>innobackupex</code> mode, which can be enabled with the <code><a href="#-innobackupex">--innobackupex</a></code> option.
				</p>
				<p>
					Files that Mariabackup generates during <code><a href="#-backup">--backup</a></code> operations in the target directory are not ready for use on the
					Server. Before you can restore the data to MariaDB, you first need
					to prepare the backup.
				</p>
				<p>
					In the case of full backups, the files are not point in time
					consistent, since they were taken at different times. If you try to
					restore the database without first preparing the data, InnoDB
					rejects the new data as corrupt. Running Mariabackup with the <code>--prepare</code> command readies the data so you can restore it to MariaDB Server.
					When working with incremental backups, you need to use the <code>--prepare</code> command and the <code><a href="#-incremental-dir">--incremental-dir</a></code> option to update the base backup with the deltas from an
					incremental backup.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --innobackupex --apply-log</pre>
				<p>
					Once the backup is ready, you can use the <code><a href="#-copy-back">--copy-back</a></code> or the <code><a href="#-move-back">--move-back</a></code> commands to restore the backup to the server.
				</p>
				<h3 class="anchored_heading" id="-apply-log-only">
					<code>--apply-log-only</code>
				</h3>
				<p>
					If this option is used when preparing a backup, then only the redo
					log apply stage will be performed, and other stages of crash
					recovery will be ignored. This option is used with incremental backups.
				</p>
				<p>
					This option is only supported in MariaDB 10.1. In MariaDB 10.2 and later, this option is not needed or supported.
				</p>
				<h3 class="anchored_heading" id="-backup">
					<code>--backup</code>
				</h3>
				<p>Backs up your databases.</p>
				<p>
					Using this command option, Mariabackup performs a backup operation
					on your database or databases. The backups are written to the
					target directory, as set by the <code><a href="#-target-dir">--target-dir</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --target-dir /path/to/backup \
   --user user_name --password user_passwd</pre>
				<p>
					Mariabackup can perform full and incremental backups. A full backup
					creates a snapshot of the database in the target directory. An
					incremental backup checks the database against a previously taken
					full backup, (defined by the<code><a href="#-incremental-basedir">--incremental-basedir</a></code> option) and creates delta files for these changes.
				</p>
				<p>
					In order to restore from a backup, you first need to run
					Mariabackup with the <code><a href="#-prepare">--prepare</a></code> command option, to make a full backup point-in-time consistent or
					to apply incremental backup deltas to base. Then you can run
					Mariabackup again with either the <code><a href="#-copy-back">--copy-back</a></code> or <code><a href="#-move-back">--move-back</a></code> commands to restore the database.
				</p>
				<p>
					For more information, see Full Backup and Restore and Incremental Backup and Restore.
				</p>
				<h3 class="anchored_heading" id="-binlog-info">
					<code>--binlog-info</code>
				</h3>
				<p>Defines how Mariabackup retrieves the binary log coordinates
					from the server.</p>
				<pre class="fixed" data-language="sql">--binlog-info[=OFF | ON | LOCKLESS | AUTO]</pre>
				<p>
					The <code>--binlog-info</code> option supports the following retrieval methods. When no retrieval
					method is provided, it defaults to
					<code>AUTO</code>
					.
				</p>
				<div class="cstm-style darkheader-nospace-borders">
					<table>
						<tr>
							<th>Option</th>
							<th>Description</th>
						</tr>
						<tr>
							<td><code>OFF</code></td>
							<td>Disables the retrieval of binary log information</td>
						</tr>
						<tr>
							<td><code>ON</code></td>
							<td>Enables the retrieval of binary log information,
								performs locking where available to ensure consistency</td>
						</tr>
						<tr>
							<td><code>LOCKLESS</code></td>
							<td>Unsupported option</td>
						</tr>
						<tr>
							<td><code>AUTO</code></td>
							<td>Enables the retrieval of binary log information using <code>ON</code>
								or <code>LOCKLESS</code> where supported
							</td>
						</tr>
					</table>
				</div>
				<p>Using this option, you can control how Mariabackup retrieves
					the server's binary log coordinates corresponding to the backup.</p>
				<p>
					When enabled, whether using <code>ON</code> or <code>AUTO</code>, Mariabackup retrieves information from the binlog during the
					backup process. When disabled with <code>OFF</code>, Mariabackup runs without attempting to retrieve binary log
					information. You may find this useful when you need to copy data
					without metadata like the binlog or replication coordinates.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --binlog-info --backup</pre>
				<p>
					Currently, the
					<code>LOCKLESS</code>
					option depends on features unsupported by MariaDB Server. See the
					description of the <code>xtrabackup_binlog_pos_innodb</code> file for more information. If you attempt to run Mariabackup with
					this option, then it causes the utility to exit with an error.
				</p>
				<h3 class="anchored_heading" id="-close-files">
					<code>--close-files</code>
				</h3>
				<p>Defines whether you want to close file handles.</p>
				<p>Using this option, you can tell Mariabackup that you want to
					close file handles. Without this option, Mariabackup keeps files
					open in order to manage DDL operations. When working with
					particularly large tablespaces, closing the file can make the
					backup more manageable. However, it can also lead to inconsistent
					backups. Use at your own risk.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --close-files --prepare</pre>
				<h3 class="anchored_heading" id="-compress">
					<code>--compress</code>
				</h3>
				<p>
					Defines the compression algorithm for backup files. Deprecated. It
					is recommended to backup to stream (stdout), and use a 3rd party
					compression library to compress the stream, as described in Using Encryption and Compression Tools With Mariabackup.
				</p>
				<pre class="fixed" data-language="sql">--compress[=compression_algorithm]</pre>
				<p>
					The <code>--compress</code> option supports the following algorithms. When no algorithm is provided, it defaults to<code>quicklz</code>.
				</p>
				<div class="cstm-style darkheader-nospace-borders">
					<table>
						<tr>
							<th>Option</th>
							<th>Description</th>
						</tr>
						<tr>
							<td><code>quicklz</code></td>
							<td>Uses the QuickLZ compression algorithm</td>
						</tr>
					</table>
				</div>
				<p>Using this option, you can tell Mariabackup to compress its
					backup files before writing them to disk. You may find this useful
					when backing up particularly large databases.</p>
				<p>You can optionally pass a value to this option, defining what
					compression algorithm you want to use for this process. However,
					currently, Mariabackup only supports the QuickLZ algorithm, which
					is the default value.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --compress --backup</pre>
				<p>
					To further configure backup compression, see the <code><a href="#-compress-threads">--compress-threads</a></code> and <code><a href="#-compress-chunk-size">--compress-chunk-size</a></code> options.
				</p>
				<p>
					If a backup is compressed, then Mariabackup will record that detail
					in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-compress-chunk-size">
					<code>--compress-chunk-size</code>
				</h3>
				<p>Defines the working buffer size for compression threads.</p>
				<pre class="fixed" data-language="sql">--compress-chunk-size=#</pre>
				<p>Mariabackup can perform compression operations on the backup
					files before writing them to disk. It can also use multiple threads
					for parallel data compression during this process. Using this
					option, you can set the chunk size each thread uses during
					compression. It defaults to 64K.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup --compress \
   --compress-threads=12 --compress-chunk-size=5M</pre>
				<p>
					To further configure backup compression, see the <code><a href="#-compress">--compress</a></code> and <code><a href="#-compress-threads">--compress-threads</a></code> options.
				</p>
				<h3 class="anchored_heading" id="-compress-threads">
					<code>--compress-threads</code>
				</h3>
				<p>Defines the number of threads to use in compression.</p>
				<pre class="fixed" data-language="sql">--compress-threads=#</pre>
				<p>Mariabackup can perform compression operations on the backup
					files before writing them to disk. Using this option, you can
					define the number of threads you want to use for this operation.
					You may find this useful in speeding up the compression of
					particularly large databases. It defaults to single-threaded.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --compress --compress-threads=12 --backup</pre>
				<p>
					To further configure backup compression, see the <code><a href="#-compress">--compress</a></code> and <code><a href="#-compress-chunk-size">--compress-chunk-size</a></code> options.
				</p>
				<h3 class="anchored_heading" id="-copy-back">
					<code>--copy-back</code>
				</h3>
				<p>Restores the backup to the data directory.</p>
				<p>
					Using this command, Mariabackup copies the backup from the target
					directory to the data directory, as defined by the <code><a href="#-h-datadir">--datadir</a></code> option. You must stop the MariaDB Server before running this
					command. The data directory must be empty. If you want to overwrite
					the data directory with the backup, use the <code><a href="#-force-non-empty-directories">--force-non-empty-directories</a></code> option.
				</p>
				<p>
					Bear in mind, before you can restore a backup, you first need to
					run Mariabackup with the <code><a href="#-prepare">--prepare</a></code> option. In the case of full backups, this makes the files
					point-in-time consistent. With incremental backups, this applies
					the deltas to the base backup. Once the backup is prepared, you can
					run <code>--copy-back</code> to apply it to MariaDB Server.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --copy-back --force-non-empty-directories --backup</pre>
				<p>
					Running the <code>--copy-back</code> command copies the backup files to the data directory. Use this
					command if you want to save the backup for later. If you don't want
					to save the backup for later, use the <code><a href="#-move-back">--move-back</a></code> command.
				</p>
				<h3 class="anchored_heading" id="-core-file">
					<code>--core-file</code>
				</h3>
				<p>Defines whether to write a core file.</p>
				<p>Using this option, you can configure Mariabackup to dump its
					core to file in the event that it encounters fatal signals. You may
					find this useful for review and debugging purposes.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --core-file --backup</pre>
				<h3 class="anchored_heading" id="-databases">
					<code>--databases</code>
				</h3>
				<p>Defines the databases and tables you want to back up.</p>
				<pre class="fixed" data-language="sql">--databases="database[.table][ database[.table] ...]"</pre>
				<p>Using this option, you can define the specific database or
					databases you want to back up. In cases where you have a
					particularly large database or otherwise only want to back up a
					portion of it, you can optionally also define the tables on the
					database.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --databases="example.table1 example.table2"</pre>
				<p>
					In cases where you want to back up most databases on a server or
					tables on a database, but not all, you can set the specific
					databases or tables you don't want to back up using the <code><a href="#-databases-exclude">--databases-exclude</a></code> option.
				</p>
				<p>
					If a backup is a partial backup, then Mariabackup will record that detail in the <code>xtrabackup_info</code> file.
				</p>
				<p>
					In <code>innobackupex</code> mode, which can be enabled with the <code><a href="#-innobackupex">--innobackupex</a></code> option, the <code>--databases</code> option can be used as described above, or it can be used to refer to a file, just as the <code><a href="-databases-file">--databases-file</a></code> option can in the normal mode.
				</p>
				<h3 class="anchored_heading" id="-databases-exclude">
					<code>--databases-exclude</code>
				</h3>
				<p>Defines the databases you don't want to back up.</p>
				<pre class="fixed" data-language="sql">--databases-exclude="database[.table][ database[.table] ...]"</pre>
				<p>Using this option, you can define the specific database or
					databases you want to exclude from the backup process. You may find
					it useful when you want to back up most databases on the server or
					tables on a database, but would like to exclude a few from the
					process.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --databases="example" \
   --databases-exclude="example.table1 example.table2"</pre>
				<p>
					To include databases in the backup, see the <code><a href="#-databases">--databases</a></code> option option
				</p>
				<p>
					If a backup is a partial backup, then Mariabackup will record that detail in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-databases-file">
					<code>--databases-file</code>
				</h3>
				<p>Defines the path to a file listing databases and/or tables
					you want to back up.</p>
				<pre class="fixed" data-language="sql">--databases-file="/path/to/database-file"</pre>
				<p>Format the databases file to list one element per line, with the following syntax:</p>
				<pre class="fixed" data-language="sql">database[.table]</pre>
				<p>
					In cases where you need to back up a number of databases or
					specific tables in a database, you may find the syntax for the <code><a href="#-databases">--databases</a></code> and <code><a href="#-databases-exclude">--databases-exclude</a></code> options a little cumbersome. Using this option you can set the path
					to a file listing the databases or databases and tables you want to
					back up.
				</p>
				<p>
					For instance, imagine you list the databases and tables for a
					backup in a file called <code>main-backup</code>.
				</p>
				<pre class="fixed" data-language="sql">$ cat main-backup
example1
example2.table1
example2.table2

$ mariabackup --backup --databases-file=main-backup</pre>
				<p>
					If a backup is a partial backup, then Mariabackup will record that detail in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-h-datadir">
					<code>-h, --datadir</code>
				</h3>
				<p>Defines the path to the database root.</p>
				<pre class="fixed" data-language="sql">--datadir=PATH</pre>
				<p>
					Using this option, you can define the path to the source directory.
					This is the directory that Mariabackup reads for the data it backs
					up. It should be the same as the MariaDB Server <code>datadir</code> system variable.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup -h /var/lib64/mysql</pre>
				<h3 class="anchored_heading" id="-debug-sleep-before-unlock">
					<code>--debug-sleep-before-unlock</code>
				</h3>
				<p>This is a debug-only option used by the Xtrabackup test
					suite.</p>
				<h3 class="anchored_heading" id="-decompress">
					<code>--decompress</code>
				</h3>
				<p>
					Defines whether you want to decompress previously compressed backup
					files. Deprecated. It is recommended to backup to stream (stdout),
					and use a 3rd party compression library to compress the stream, as
					described in Using Encryption and Compression Tools With Mariabackup.
				</p>
				<p>
					When you run Mariabackup with the <code><a href="#-compress">--compress</a></code> option, it compresses the subsequent backup files, using the
					QuickLZ algorithm by default, (which is currently the only
					available compression algorithm). Using this option, Mariabackup
					decompresses the compressed files from a previous backup.
				</p>
				<p>For instance, run a backup with compression,</p>
				<pre class="fixed" data-language="sql">$ mariabackup --compress --backup</pre>
				<p>Then decompress the backup,</p>
				<pre class="fixed" data-language="sql">$ mariabackup --decompress</pre>
				<p>
					You can enable the decryption of multiple files at a time using the <code><a href="#-parallel">--parallel</a></code> option. By default, Mariabackup does not remove the compressed
					files from the target directory. If you want to delete these files, use the <code><a href="#-remove-original">--remove-original</a></code> option.
				</p>
				<p>
					This option requires that you have the
					<code>qpress</code>
					utility installed on your system.
				</p>
				<h3 class="anchored_heading" id="-debug-sync">
					<code>--debug-sync</code>
				</h3>
				<p>Defines the debug sync point. This option is only used by the
					Mariabackup test suite.</p>
				<h3 class="anchored_heading" id="-defaults-extra-file">
					<code>--defaults-extra-file</code>
				</h3>
				<p>
					Defines the path to an extra default option file.
				</p>
				<pre class="fixed" data-language="sql">--defaults-extra-file=/path/to/config</pre>
				<p>
					Using this option, you can define an extra default option file for Mariabackup. Unlike <code><a href="#-defaults-file">--defaults-file</a></code>, this file is read after the default option file are read, allowing you to only overwrite the existing defaults.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --defaults-file-extra=addition-config.cnf \
   --defaults-file=config.cnf</pre>
				<h3 class="anchored_heading" id="-defaults-file">
					<code>--defaults-file</code>
				</h3>
				<p>
					Defines the path to the default option file.
				</p>
				<pre class="fixed" data-language="sql">--defaults-file=/path/to/config</pre>
				<p>
					Using this option, you can define a default option file for Mariabackup. Unlike the <code><a href="#-defaults-extra-file">--defaults-extra-file</a></code> option, when this option is provided, it completely replaces all default option files.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --defaults-file="config.cnf</pre>
				<h3 class="anchored_heading" id="-defaults-group">
					<code>--defaults-group</code>
				</h3>
				<p>
					Defines the option group to read in the option file.
				</p>
				<pre class="fixed" data-language="sql">--defaults-group="name"</pre>
				<p>
					In situations where you find yourself using certain Mariabackup
					options consistently every time you call it, you can set the
					options in an option file. The <code>--defaults-group</code> option defines what option group Mariabackup reads for its options.
				</p>
				<p>
					Options you define from the command-line can be set in the
					configuration file using minor formatting changes. For instance, if
					you find yourself perform compression operations frequently, you
					might set <code><a href="#-compress-threads">--compress-threads</a></code>
					and <code><a href="#-compress-chunk-size">--compress-chunk-size</a></code> options in this way:
				</p>
				<pre class="fixed" data-language="sql">[mariabackup]
compress_threads = 12
compress_chunk_size = 64K</pre>
				<p>
					Now whenever you run a backup with the <code><a href="#-compress">--compress</a></code> option, it always performs the compression using 12 threads and 64K chunks.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --compress --backup</pre>
				<p>
					See Mariabackup Overview: Server Option Groups and Mariabackup Overview: Client Option Groups for a list of the option groups read by Mariabackup by default.
				</p>
				<h3 class="anchored_heading" id="-encrypted-backup">
					<code>--encrypted-backup</code>
				</h3>
				<p>
					When this option is used with <code>--backup</code>, if Mariabackup encounters a page that has a non-zero <code>key_version</code> value, then Mariabackup assumes that the page is encrypted.
				</p>
				<p>
					Use <code>--skip-encrypted-backup</code> instead to allow Mariabackup to copy unencrypted tables that were originally created before MySQL 5.1.48.
				</p>
				<p>
					This option was added in MariaDB 10.2.22, MariaDB 10.3.13, and MariaDB 10.4.2.
				</p>
				<h3 class="anchored_heading" id="-export">
					<code>--export</code>
				</h3>
				<p>
					If this option is provided during the <code>--prepare</code> stage, then it tells Mariabackup to create <code>.cfg</code> files for each InnoDB file-per-table tablespace. These <code>.cfg</code> files are used to import transportable tablespaces</a> in the process of restoring partial backups and restoring individual tables and partitions.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --prepare --export</pre>
				<div class="mariadb_to_10_2_8 mariadb to_10_2_8 product">
					<strong class="product_title">MariaDB until 10.2.8</strong>
					<p>
						In MariaDB 10.2.8 and before, Mariabackup did not support the <code><a href="index#-export">--export</a></code> option. See MDEV-13466 about that. In earlier versions of MariaDB, this means that Mariabackup could not create <code>.cfg</code> files for InnoDB file-per-table tablespaces during the <code>--prepare</code> stage. You can still import file-per-table tablespaces without the <code>.cfg</code> files in many cases, so it may still be possible in those versions to restore partial backups or to restore individual tables and partitions with just the <code>.ibd</code> files. If you have a full backup and you need to create <code>.cfg</code> files for InnoDB file-per-table tablespaces, then you can do so by preparing the backup as usual without the <code>--export</code> option, and then restoring the backup, and then starting the server. At that point, you can use the server's built-in features to copy the transportable tablespaces.
					</p>
				</div>
				<h3 class="anchored_heading" id="-extra-lsndir">
					<code>--extra-lsndir</code>
				</h3>
				<p>
					Saves an extra copy of the <code>xtrabackup_checkpoints</code> and <code>xtrabackup_info</code> files into the given directory.
				</p>
				<pre class="fixed" data-language="sql">--extra-lsndir=PATH</pre>
				<p>
					When using the <code><a href="#-backup">--backup</a></code> command option, Mariabackup produces a number of backup files in
					the target directory. Using this option, you can have Mariabackup
					produce additional copies of the <code>xtrabackup_checkpoints</code> and <code>xtrabackup_info# files in the given directory.</code>
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --extra-lsndir=extras/ --backup</pre>
				<h3 class="anchored_heading" id="-force-non-empty-directories">
					<code>--force-non-empty-directories</code>
				</h3>
				<p>
					Allows <code><a href="#-copy-back">--copy-back</a></code> or <code><a href="#-move-back">--move-back</a></code> command options to use non-empty target directories.
				</p>
				<p>
					When using Mariabackup with the <code><a href="#-copy-back">--copy-back</a></code> or <code><a href="#-move-back">--move-back</a></code> command options, they normally require a non-empty target directory
					to avoid conflicts. Using this option with either of command allows
					Mariabackup to use a non-empty directory.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --force-on-empty-directories --copy-back</pre>
				<p>Bear in mind that this option does not enable overwrites.
					When copying or moving files into the target directory, if
					Mariabackup finds that the target file already exists, it fails
					with an error.</p>
				<h3 class="anchored_heading" id="-ftwrl-wait-query-type">
					<code>--ftwrl-wait-query-type</code>
				</h3>
				<p>Defines the type of query allowed to complete before
					Mariabackup issues the global lock.</p>
				<pre class="fixed" data-language="sql">--ftwrl-wait-query-type=[ALL | UPDATE | SELECT]</pre>
				<p>
					The <code>--ftwrl-wait-query-type</code> option supports the following query types. The default value is <code>ALL</code>.
				</p>
				<div class="cstm-style darkheader-nospace-borders">
					<table>
						<tr>
							<th>Option</th>
							<th>Description</th>
						</tr>
						<tr>
							<td><code>ALL</code></td>
							<td>Waits until all queries complete before issuing the
								global lock</td>
						</tr>
						<tr>
							<td><code>SELECT</code></td>
							<td>Waits until <code>SELECT</code> statements complete before issuing the global lock
							</td>
						</tr>
						<tr>
							<td><code>UPDATE</code></td>
							<td>Waits until <code>UPDATE</code> statements complete before issuing the global lock
							</td>
						</tr>
					</table>
				</div>
				<p>
					When Mariabackup runs, it issues a global lock to prevent data from
					changing during the backup process. When it encounters a statement
					in the process of executing, it waits until the statement is
					finished before issuing the global lock. Using this option, you can
					modify this default behavior to ensure that it waits only for
					certain query types, such as for <code>SELECT</code> and <code>UPDATE</code> statements.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup  \
   --ftwrl-wait-query-type=UPDATE</pre>
				<h3 class="anchored_heading" id="-ftwrl-wait-threshold">
					<code>--ftwrl-wait-threshold</code>
				</h3>
				<p>Defines the minimum threshold for identifying long-running
					queries for FTWRL.</p>
				<pre class="fixed" data-language="sql">--ftwrl-wait-threshold=#</pre>
				<p>
					When Mariabackup runs, it issues a global lock to prevent data from
					changing during the backup process and ensure a consistent record.
					If it encounters statements still in the process of executing, it
					waits until they complete before setting the lock. Using this
					option, you can set the threshold at which Mariabackup engages
					FTWRL. When it <code><a href="#-ftwrl-wait-timeout">--ftwrl-wait-timeout</a></code> is not 0 and a statement has run for at least the amount of time
					given this argument, Mariabackup waits until the statement completes or until the <code><a href="#-ftwrl-wait-timeout">--ftwrl-wait-timeout</a></code> expires before setting the global lock and starting the backup.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --ftwrl-wait-timeout=90 \
   --ftwrl-wait-threshold=30</pre>
				<h3 class="anchored_heading" id="-ftwrl-wait-timeout">
					<code>--ftwrl-wait-timeout</code>
				</h3>
				<p>Defines the timeout to wait for FTWRL queries before setting
					the global lock.</p>
				<pre class="fixed" data-language="sql">--ftwrl-wait-timeout=#</pre>
				<p>When Mariabackup runs, it issues a global lock to prevent
					data from changing during the backup process and ensure a
					consistent record. If it encounters statements still in the process
					of executing, it waits until they complete before setting the lock.
					Using this option, you can set the number of seconds it waits, to
					prevent long-running queries from blocking the backup process. The
					default value is 0, which indicates that Mariabackup does not wait
					for queries to complete.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --ftwrl-wait-query-type=UPDATE \
   --ftwrl-wait-timeout=5</pre>
				<h3 class="anchored_heading" id="-galera-info">
					<code>--galera-info</code>
				</h3>
				<p>
					Defines whether you want to back up information about a Galera Cluster node's state.
				</p>
				<p>
					When this option is used, Mariabackup creates an additional file called <code>xtrabackup_galera_info</code>, which records information about a Galera Cluster node's state. It records the values of the <code>wsrep_local_state_uuid</code> and <code>wsrep_last_committed</code> status variables.
				</p>
				<p>
					You should only use this option when backing up a Galera Cluster node. If the server is
					not a Galera Cluster node, then this
					option has no effect.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup --galera-info</pre>
				<h3 class="anchored_heading" id="-history">
					<code>--history</code>
				</h3>
				<p>
					Defines whether you want to track backup history in the <code>PERCONA_SCHEMA.xtrabackup_history</code> table.
				</p>
				<pre class="fixed" data-language="sql">--history[=name]</pre>
				<p>When using this option, Mariabackup records its operation in
					a table on the MariaDB Server. Passing a name to this option allows
					you group backups under arbitrary terms for later processing and
					analysis.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup --history=backup_all</pre>
				<p>
					Currently, the table it uses is named <code>PERCONA_SCHEMA.xtrabackup_history</code>, but expect that name to change in future releases. See MDEV-19246 for more information.
				</p>
				<p>
					Mariabackup will also record this in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-h-host">
					<code>-H, --host</code>
				</h3>
				<p>Defines the host for the MariaDB Server you want to backup.</p>
				<pre class="fixed" data-language="sql">--host=name</pre>
				<p>Using this option, you can define the host to use when
					connecting to a MariaDB Server over TCP/IP. By default, Mariabackup
					attempts to connect to the local host.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --host="example.com"</pre>
				<h3 class="anchored_heading" id="-include">
					<code>--include</code>
				</h3>
				<p>
					This option is a regular expression to be matched against table
					names in databasename.tablename format. It is equivalent to the <code><a href="#-tables">--tables</a></code> option. This is only valid in <code>innobackupex</code> mode, which can be enabled with the <code><a href="#-innobackupex">--innobackupex</a></code> option.
				</p>
				<h3 class="anchored_heading" id="-incremental">
					<code>--incremental</code>
				</h3>
				<p>
					Defines whether you want to take an increment backup, based on
					another backup. This is only valid in
					<code>innobackupex</code>
					mode, which can be enabled with the <code><a href="#-innobackupex">--innobackupex</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">mariabackup --innobackupex --incremental</pre>
				<p>
					Using this option with the
					<code><a href="#-backup">--backup</a></code> command option makes the operation incremental rather than a complete overwrite. When this option is specified, either the <code><a href="#-incremental-lsn">--incremental-lsn</a> or</code> <a href="#-incremental-basedir">--incremental-basedir</a><code> options can also be given. If neither option is given, option </code><a href="#-incremental-basedir">--incremental-basedir</a><code> is used by default, set to the first timestamped backup directory in the backup base directory.</code>
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --innobackupex --backup --incremental \
   --incremental-basedir=/data/backups \
   --target-dir=/data/backups</pre>
				<p>
					If a backup is a incremental backup, then Mariabackup will record that detail in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-incremental-basedir">
					<code>--incremental-basedir</code>
				</h3>
				<p>Defines whether you want to take an incremental backup, based
					on another backup.</p>
				<pre class="fixed" data-language="sql">--incremental-basedir=PATH</pre>
				<p>
					Using this option with the <code><a href="#-backup">--backup</a></code> command option makes the operation incremental rather than a complete overwrite. Mariabackup will only copy pages from <code>.ibd</code> files if they are newer than the backup in the specified directory.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --incremental-basedir=/data/backups \
   --target-dir=/data/backups</pre>
				<p>
					If a backup is a incremental backup, then Mariabackup will record that detail in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-incremental-dir">
					<code>--incremental-dir</code>
				</h3>
				<p>Defines whether you want to take an incremental backup, based
					on another backup.</p>
				<pre class="fixed" data-language="sql">--increment-dir=PATH</pre>
				<p>
					Using this option with <code><a href="#-prepare">--prepare</a></code> command option makes the operation incremental rather than a complete overwrite. Mariabackup will apply <code>.delta</code> files and log files into the target directory.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --prepare \
   --increment-dir=backups/</pre>
				<p>
					If a backup is a incremental backup, then Mariabackup will record that detail in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-incremental-force-scan">
					<code>--incremental-force-scan</code>
				</h3>
				<p>Defines whether you want to force a full scan for incremental
					backups.</p>
				<p>
					When using Mariabackup to perform an incremental backup, this
					option forces it to also perform a full scan of the data pages
					being backed up, even when there's bitmap data on the changes. MariaDB 10.2 and later
					does not support changed page bitmaps, so this option is useless in
					those versions. See MDEV-18985 for more information.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --incremental-basedir=/path/to/target \
   --incremental-force-scan</pre>
				<h3 class="anchored_heading" id="-incremental-history-name">
					<code>--incremental-history-name</code>
				</h3>
				<p>Defines a logical name for the backup.</p>
				<pre class="fixed" data-language="sql">--incremental-history-name=name</pre>
				<p>Mariabackup can store data about its operations on the
					MariaDB Server. Using this option, you can define the logical name
					it uses in identifying the backup.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --incremental-history-name=morning_backup</pre>
				<p>
					Currently, the table it uses is named <code>PERCONA_SCHEMA.xtrabackup_history</code>, but expect that name to change in future releases. See MDEV-19246 for more information.
				</p>
				<p>
					Mariabackup will also record this in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-incremental-history-uuid">
					<code>--incremental-history-uuid</code>
				</h3>
				<p>Defines a UUID for the backup.</p>
				<pre class="fixed" data-language="sql">--incremental-history-uuid=name</pre>
				<p>
					Mariabackup can store data about its operations on the MariaDB
					Server. Using this option, you can define the UUID it uses in
					identifying a previous backup to increment from. It checks <code><a href="#-incremental-history-name">--incremental-history-name</a></code>, <code><a href="#-incremental-basedir">--incremental-basedir</a></code>, and <code><a href="#-incremental-lsn">--incremental-lsn</a></code>. If Mariabackup fails to find a valid lsn, it generates an error.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --incremental-history-uuid=main-backup012345678</pre>
				<p>
					Currently, the table it uses is named <code>PERCONA_SCHEMA.xtrabackup_history</code>, but expect that name to change in future releases. See MDEV-19246 for more information.
				</p>
				<p>
					Mariabackup will also record this in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-incremental-lsn">
					<code>--incremental-lsn</code>
				</h3>
				<p>Defines the sequence number for incremental backups.</p>
				<pre class="fixed" data-language="sql">--incremental-lsn=name</pre>
				<p>
					Using this option, you can define the sequence number (LSN) value
					for <code><a href="#-backup">--backup</a></code> operations. During backups, Mariabackup only copies <code>.ibd</code> pages newer than the specified values.
				</p>
				<p>
					<strong>WARNING</strong>: Incorrect LSN values can make the backup
					unusable. It is impossible to diagnose this issue.
				</p>
				<h3 class="anchored_heading" id="-innobackupex">
					<code>--innobackupex</code>
				</h3>
				<p>
					Enables
					<code>innobackupex</code>
					mode, which is a compatibility mode.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --innobackupex</pre>
				<p>
					In <code>innobackupex</code> mode, Mariabackup has the following differences:
				</p>
				<ul start="1">
					<li>To prepare a backup, the <a href="#-apply-log">--apply-log</a></code> option is used instead of the <code><a href="#-prepare">--prepare</a></code> option.
					</li>
					<li>To create an incremental backup, the <code><a href="#-incremental">--incremental</a></code> option is supported.
					</li>
					<li>The <code><a href="#-no-timestamp">--no-timestamp</a></code> option is supported.
					</li>
					<li>To create a partial backup, the <code><a href="#--include">--include</a></code> option is used instead of the <code><a href="#-tables">--tables</a></code> option.
					</li>
					<li>To create a partial backup, the <code><a href="#--databases">--databases</a></code> option can still be used, but it's behavior changes slightly.
					</li>
					<li>The <code><a href="#-target-dir">--target-dir</a></code> option is not used to specify the backup directory. The backup
						directory should instead be specified as a standalone argument.
					</li>
				</ul>
				<p>
					The primary purpose of <code>innobackupex</code> mode is to allow scripts and tools to more easily migrate to
					Mariabackup if they were originally designed to use the <code>innobackupex</code> utility that is included with Percona XtraBackup.
					It is not recommended to use this mode in new scripts, since it is
					not guaranteed to be supported forever. See MDEV-20552 for more information.
				</p>
				<h3 class="anchored_heading" id="-innodb">
					<code>--innodb</code>
				</h3>
				<p>This option has no effect. Set only for MySQL option
					compatibility.</p>
				<h3 class="anchored_heading" id="-innodb-adaptive-hash-index">
					<code>--innodb-adaptive-hash-index</code>
				</h3>
				<p>Enables InnoDB Adaptive Hash Index.</p>
				<p>
					Mariabackup initializes its own embedded instance of InnoDB using
					the same configuration as defined in the configuration file. Using
					this option you can explicitly enable the InnoDB Adaptive Hash
					Index. This feature is enabled by default for Mariabackup. If you
					want to disable it, use <code>--skip-innodb-adaptive-hash-index</code>.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-adaptive-hash-index</pre>
				<h3 class="anchored_heading" id="-innodb-autoextend-increment">
					<code>--innodb-autoextend-increment</code>
				</h3>
				<p>Defines the increment in megabytes for auto-extending the
					size of tablespace file.</p>
				<pre class="fixed" data-language="sql">--innodb-autoextend-increment=36</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can set the increment in megabytes for
					automatically extending the size of tablespace data file in InnoDB.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-autoextend-increment=35</pre>
				<h3 class="anchored_heading" id="-innodb-buffer-pool-filename">
					<code>--innodb-buffer-pool-filename</code>
				</h3>
				<p>Using this option has no effect. It is available to provide
					compatibility with the MariaDB Server.</p>
				<h3 class="anchored_heading" id="-innodb-buffer-pool-size">
					<code>--innodb-buffer-pool-size</code>
				</h3>
				<p>Defines the memory buffer size InnoDB uses the cache data and
					indexes of the table.</p>
				<pre class="fixed" data-language="sql">--innodb-buffer-pool-size=124M</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can configure the buffer pool for InnoDB
					operations.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-buffer-pool-size=124M</pre>
				<h3 class="anchored_heading" id="-innodb-checksum-algorithm">
					<code>--innodb-checksum-algorithm</code>
				</h3>
				<p>Defines the checksum algorithm.</p>
				<pre class="fixed" data-language="sql">--innodb-checksum-algorithm=crc32
                           | strict_crc32
                           | innodb
                           | strict_innodb
                           | none
                           | strict_none</pre>
				<p>
					Mariabackup initializes its own embedded instance of InnoDB using
					the same configuration as defined in the configuration file. Using
					this option, you can specify the algorithm Mariabackup uses when
					checksumming on InnoDB tables. Currently, MariaDB supports the
					following algorithms <code>CRC32</code>, <code>STRICT_CRC32</code> <code>INNODB</code>, <code>STRICT_INNODB</code>, <code>NONE</code>, <code>STRICT_NONE</code>.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   ---innodb-checksum-algorithm=strict_innodb</pre>
				<h3 class="anchored_heading" id="-innodb-data-file-path">
					<code>--innodb-data-file-path</code>
				</h3>
				<p>Defines the path to individual data files.</p>
				<pre class="fixed" data-language="sql">--innodb-data-file-path=/path/to/file</pre>
				<p>
					Mariabackup initializes its own embedded instance of InnoDB using
					the same configuration as defined in the configuration file. Using
					this option you can define the path to InnoDB data files. Each path
					is appended to the <code><a href="#-innodb-data-home-dir">--innodb-data-home-dir</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-data-file-path=ibdata1:13M:autoextend \
   --innodb-data-home-dir=/var/dbs/mysql/data</pre>
				<h3 class="anchored_heading" id="-innodb-data-home-dir">
					<code>--innodb-data-home-dir</code>
				</h3>
				<p>Defines the home directory for InnoDB data files.</p>
				<pre class="fixed" data-language="sql">--innodb-data-home-dir=PATH</pre>
				<p>
					Mariabackup initializes its own embedded instance of InnoDB using
					the same configuration as defined in the configuration file. Using
					this option you can define the path to the directory containing
					InnoDB data files. You can specific the files using the <code><a href="#-innodb-data-file-path">--innodb-data-file-path</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-data-file-path=ibdata1:13M:autoextend \
   --innodb-data-home-dir=/var/dbs/mysql/data</pre>
				<h3 class="anchored_heading" id="-innodb-doublewrite">
					<code>--innodb-doublewrite</code>
				</h3>
				<p>Enables doublewrites for InnoDB tables.</p>
				<p>
					Mariabackup initializes its own embedded instance of InnoDB using
					the same configuration as defined in the configuration file. When
					using this option, Mariabackup improves fault tolerance on InnoDB
					tables with a doublewrite buffer. By default, this feature is
					enabled. Use this option to explicitly enable it. To disable
					doublewrites, use the <code><a href="#-skip-innodb-doublewrite">--skip-innodb-doublewrite</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-doublewrite</pre>
				<h3 class="anchored_heading" id="-innodb-encrypt-log">
					<code>--innodb-encrypt-log</code>
				</h3>
				<p>Defines whether you want to encrypt InnoDB logs.</p>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can tell Mariabackup that you want to
					encrypt logs from its InnoDB activity.</p>
				<h3 class="anchored_heading" id="-innodb-file-io-threads">
					<code>--innodb-file-io-threads</code>
				</h3>
				<p>Defines the number of file I/O threads in InnoDB.</p>
				<pre class="fixed" data-language="sql">--innodb-file-io-threads=#</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can define the number of file I/O threads
					Mariabackup uses on InnoDB tables.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-file-io-threads=5</pre>
				<h3 class="anchored_heading" id="-innodb-file-per-table">
					<code>--innodb-file-per-table</code>
				</h3>
				<p>
					Defines whether you want to store each InnoDB table as an
					<code>.ibd</code>
					file.
				</p>
				<p>
					Mariabackup initializes its own embedded instance of InnoDB using
					the same configuration as defined in the configuration file. Using
					this option causes Mariabackup to store each InnoDB table as an
					<code>.ibd</code>
					file in the target directory.
				</p>
				<h3 class="anchored_heading" id="-innodb-flush-method">
					<code>--innodb-flush-method</code>
				</h3>
				<p>Defines the data flush method.</p>
				<pre class="fixed" data-language="sql">--innodb-flush-method=fdatasync 
                     | O_DSYNC 
                     | O_DIRECT 
                     | O_DIRECT_NO_FSYNC 
                     | ALL_O_DIRECT</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can define the data flush method Mariabackup
					uses with InnoDB tables.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-flush-method==_DIRECT_NO_FSYNC</pre>
				<p>
					Note, the <code>0_DIRECT_NO_FSYNC</code> method is only available with MariaDB 10.0 and later.
					The <code>ALL_O_DIRECT</code> method available with version 5.5 and later, but only with tables using the XtraDB storage engine.
				</p>
				<h3 class="anchored_heading" id="-innodb-io-capacity">
					<code>--innodb-io-capacity</code>
				</h3>
				<p>Defines the number of IOP's the utility can perform.</p>
				<pre class="fixed" data-language="sql">--innodb-io-capacity=#</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can limit the I/O activity for InnoDB
					background tasks. It should be set around the number of I/O
					operations per second that the system can handle, based on drive or
					drives being used.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-io-capacity=200</pre>
				<h3 class="anchored_heading" id="-innodb-log-checksums">
					<code>--innodb-log-checksums</code>
				</h3>
				<p>Defines whether to include checksums in the InnoDB logs.</p>
				<p>
					Mariabackup initializes its own embedded instance of InnoDB using
					the same configuration as defined in the configuration file. Using
					this option, you can explicitly set Mariabackup to include
					checksums in the InnoDB logs. The feature is enabled by default. To
					disable it, use the <code><a href="#-skip-innodb-log-checksums">--skip-innodb-log-checksums</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-log-checksums</pre>
				<h3 class="anchored_heading" id="-innodb-log-buffer-size">
					<code>--innodb-log-buffer-size</code>
				</h3>
				<p>This option has no functionality in Mariabackup. It exists
					for MariaDB Server compatibility.</p>
				<h3 class="anchored_heading" id="-innodb-log-files-in-group">
					<code>--innodb-log-files-in-group</code>
				</h3>
				<p>This option has no functionality in Mariabackup. It exists
					for MariaDB Server compatibility.</p>
				<h3 class="anchored_heading" id="-innodb-log-group-home-dir">
					<code>--innodb-log-group-home-dir</code>
				</h3>
				<p>Defines the path to InnoDB log files.</p>
				<pre class="fixed" data-language="sql">--innodb-log-group-home-dir=PATH</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can define the path to InnoDB log files.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-log-group-home-dir=/path/to/logs</pre>
				<h3 class="anchored_heading" id="-innodb-max-dirty-pages-pct">
					<code>--innodb-max-dirty-pages-pct</code>
				</h3>
				<p>Defines the percentage of dirty pages allowed in the InnoDB
					buffer pool.</p>
				<pre class="fixed" data-language="sql">--innodb-max-dirty-pages-pct=#</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can define the maximum percentage of dirty,
					(that is, unwritten) pages that Mariabackup allows in the InnoDB
					buffer pool.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-max-dirty-pages-pct=80</pre>
				<h3 class="anchored_heading" id="-innodb-open-files">
					<code>--innodb-open-files</code>
				</h3>
				<p>Defines the number of files kept open at a time.</p>
				<pre class="fixed" data-language="sql">--innodb-open-files=#</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can set the maximum number of files InnoDB
					keeps open at a given time during backups.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-open-files=10</pre>
				<h3 class="anchored_heading" id="-innodb-page-size">
					<code>--innodb-page-size</code>
				</h3>
				<p>Defines the universal page size.</p>
				<pre class="fixed" data-language="sql">--innodb-page-size=#</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can define the universal page size in bytes
					for Mariabackup.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-page-size=16k</pre>
				<h3 class="anchored_heading" id="-innodb-read-io-threads">
					<code>--innodb-read-io-threads</code>
				</h3>
				<p>Defines the number of background read I/O threads in InnoDB.</p>
				<pre class="fixed" data-language="sql">--innodb-read-io-threads=#</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can set the number of I/O threads MariaDB
					uses when reading from InnoDB.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-read-io-threads=4</pre>
				<h3 class="anchored_heading" id="-innodb-undo-directory">
					<code>--innodb-undo-directory</code>
				</h3>
				<p>Defines the directory for the undo tablespace files.</p>
				<pre class="fixed" data-language="sql">--innodb-undo-directory=PATH</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can define the path to the directory where
					you want MariaDB to store the undo tablespace on InnoDB tables. The
					path can be absolute.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-undo-directory=/path/to/innodb_undo</pre>
				<h3 class="anchored_heading" id="-innodb-undo-tablespaces">
					<code>--innodb-undo-tablespaces</code>
				</h3>
				<p>Defines the number of undo tablespaces to use.</p>
				<pre class="fixed" data-language="sql">--innodb-undo-tablespaces=#</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can define the number of undo tablespaces
					you want to use during the backup.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-undo-tablespaces=10</pre>
				<h3 class="anchored_heading" id="-innodb-use-native-aio">
					<code>--innodb-use-native-aio</code>
				</h3>
				<p>Defines whether you want to use native AI/O.</p>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can enable the use of the native
					asynchronous I/O subsystem. It is only available on Linux operating
					systems.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-use-native-aio</pre>
				<h3 class="anchored_heading" id="-innodb-write-io-threads">
					<code>--innodb-write-io-threads</code>
				</h3>
				<p>Defines the number of background write I/O threads in InnoDB.</p>
				<pre class="fixed" data-language="sql">--innodb-write-io-threads=#</pre>
				<p>Mariabackup initializes its own embedded instance of InnoDB
					using the same configuration as defined in the configuration file.
					Using this option, you can set the number of background write I/O
					threads Mariabackup uses.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --innodb-write-io-threads=4</pre>
				<h3 class="anchored_heading" id="-kill-long-queries-timeout">
					<code>--kill-long-queries-timeout</code>
				</h3>
				<p>Defines the timeout for blocking queries.</p>
				<pre class="fixed" data-language="sql">--kill-long-queries-timeout=#</pre>
				<p>
					When Mariabackup runs, it issues a
					<code>FLUSH TABLES WITH READ LOCK</code>
					statement. It then identifies blocking queries. Using this option
					you can set a timeout in seconds for these blocking queries. When
					the time runs out, Mariabackup kills the queries.
				</p>
				<p>The default value is 0, which causes Mariabackup to not
					attempt killing any queries.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --kill-long-queries-timeout=10</pre>
				<h3 class="anchored_heading" id="-kill-long-query-type">
					<code>--kill-long-query-type</code>
				</h3>
				<p>Defines the query type the utility can kill to unblock the
					global lock.</p>
				<pre class="fixed" data-language="sql">--kill-long-query-type=ALL | UPDATE | SELECT</pre>
				<p>
					When Mariabackup encounters a query that sets a global lock, it can
					kill the query in order to free up MariaDB Server for the backup.
					Using this option, you can choose the types of query it kills: <code>SELECT</code>, <code>UPDATE</code>, or both set with<code>ALL</code>. The default is <code>ALL</code>.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --kill-long-query-type=UPDATE</pre>
				<h3 class="anchored_heading" id="-lock-ddl-per-table">
					<code>--lock-ddl-per-table</code>
				</h3>
				<p>
					Prevents DDL for each table to be backed up by acquiring MDL lock
					on that. NOTE: Unless --no-lock option was also specified,
					conflicting DDL queries , will be killed at the end of backup This
					is done avoid deadlock between "FLUSH TABLE WITH READ LOCK", user's
					DDL query (ALTER, RENAME), and MDL lock on table. Only available in
					MariaDB 10.2.9 and later.
				</p>
				<h3 class="anchored_heading" id="-log">
					<code>--log</code>
				</h3>
				<p>This option has no functionality. It is set to ensure
					compatibility with MySQL.</p>
				<h3 class="anchored_heading" id="-log-bin">
					<code>--log-bin</code>
				</h3>
				<p>Defines the base name for the log sequence.</p>
				<pre class="fixed" data-language="sql">--log-bin[=name]</pre>
				<p>Using this option you, you can set the base name for
					Mariabackup to use in log sequences.</p>
				<h3 class="anchored_heading" id="-log-copy-interval">
					<code>--log-copy-interval</code>
				</h3>
				<p>Defines the copy interval between checks done by the log
					copying thread.</p>
				<pre class="fixed" data-language="sql">--log-copy-interval=#</pre>
				<p>Using this option, you can define the copy interval
					Mariabackup uses between checks done by the log copying thread. The
					given value is in milliseconds.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --log-copy-interval=50</pre>
				<h3 class="anchored_heading" id="-move-back">
					<code>--move-back</code>
				</h3>
				<p>Restores the backup to the data directory.</p>
				<p>
					Using this command, Mariabackup moves the backup from the target
					directory to the data directory, as defined by the <code><a href="#-h-datadir">--datadir</a></code> option. You must stop the MariaDB Server before running this
					command. The data directory must be empty. If you want to overwrite
					the data directory with the backup, use the <code><a href="#-force-non-empty-directories">--force-non-empty-directories</a></code> option.
				</p>
				<p>
					Bear in mind, before you can restore a backup, you first need to
					run Mariabackup with the <code><a href="#-prepare">--prepare</a></code> option. In the case of full backups, this makes the files
					point-in-time consistent. With incremental backups, this applies
					the deltas to the base backup. Once the backup is prepared, you can
					run <code>--move-back</code> to apply it to MariaDB Server.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --move-back \
   --datadir=/var/mysql</pre>
				<p>
					Running the
					<code>--move-back</code>
					command moves the backup files to the data directory. Use this
					command if you don't want to save the backup for later. If you do
					want to save the backup for later, use the <code><a href="#-copy-back">--copy-back</a></code> command.
				</p>
				<h3 class="anchored_heading" id="-mysqld">
					<code>--mysqld</code>
				</h3>
				<p>Used internally to prepare a backup.</p>
				<h3 class="anchored_heading" id="-no-backup-locks">
					<code>--no-backup-locks</code>
				</h3>
				<p>Mariabackup locks the database by default when it runs. This
					option disables support for Percona Server's backup locks.</p>
				<p>
					When backing up Percona Server, Mariabackup would use backup locks
					by default. To be specific, backup locks refers to the
					<code>LOCK TABLES FOR BACKUP</code>
					and
					<code>LOCK BINLOG FOR BACKUP</code>
					statements. This option can be used to disable support for Percona
					Server's backup locks. This option has no effect when the server
					does not support Percona's backup locks.
				</p>
				<p>
					This option may eventually be removed. See MDEV-19753
					for more information.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup --no-backup-locks</pre>
				<h3 class="anchored_heading" id="-no-lock">
					<code>--no-lock</code>
				</h3>
				<p>
					Disables table locks with the <code>FLUSH TABLE WITH READ LOCK</code> statement.
				</p>
				<p>
					Using this option causes Mariabackup to disable table locks with
					the <code>FLUSH TABLE WITH READ LOCK</code> statement. Only use this option if:
				</p>
				<ul start="1">
					<li>You are not executing DML statements on non-InnoDB tables
						during the backup. This includes the <code>mysql</code> database
						system tables (which are MyISAM).
					</li>
					<li>You are not executing any DDL statements during the
						backup.</li>
					<li>You <strong>do not care</strong> if the binary log
						position included with the backup in <code>xtrabackup_binlog_info</code> is consistent with the data.
					</li>
					<li><strong>All</strong> tables you're backing up use the
						InnoDB storage engine.</li>
				</ul>
				<pre class="fixed" data-language="sql">$ mariabackup --backup --no-lock</pre>
				<p>
					If you're considering <code>--no-lock</code> due to backups failing to acquire locks, this may be due to
					incoming replication events preventing the lock. Consider using the <code><a href="#-safe-slave-backup">--safe-slave-backup</a></code> option to momentarily stop the replication slave thread. This
					alternative may help the backup to succeed without resorting to <code>--no-lock</code>.
				</p>
				<h3 class="anchored_heading" id="-no-timestamp">
					<code>--no-timestamp</code>
				</h3>
				<p>
					This option prevents creation of a time-stamped subdirectory of the
					BACKUP-ROOT-DIR given on the command line. When it is specified,
					the backup is done in BACKUP-ROOT-DIR instead. This is only valid
					in <code>innobackupex</code> mode, which can be enabled with the <code><a href="#-innobackupex">--innobackupex</a></code> option.
				</p>
				<h3 class="anchored_heading" id="-no-version-check">
					<code>--no-version-check</code>
				</h3>
				<p>Disables version check.</p>
				<p>
					Using this option, you can disable Mariabackup version check. If
					you would like to enable the version check, use the <code><a href="#-version-check">--version-check</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup --no-version-check</pre>
				<h3 class="anchored_heading" id="-open-files-limit">
					<code>--open-files-limit</code>
				</h3>
				<p>Defines the maximum number of file descriptors.</p>
				<pre class="fixed" data-language="sql">--open-file-limit=#</pre>
				<p>
					Using this option, you can define the maximum number of file
					descriptors Mariabackup reserves with <code>setrlimit()</code>.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --open-file-limit=</pre>
				<h3 class="anchored_heading" id="-parallel">
					<code>--parallel</code>
				</h3>
				<p>Defines the number of threads to use for parallel data file
					transfer.</p>
				<pre class="fixed" data-language="sql">--parallel=#</pre>
				<p>Using this option, you can set the number of threads
					Mariabackup uses for parallel data file transfers. By default, it
					is set to 1.</p>
				<h3 class="anchored_heading" id="-p-password">
					<code>-p, --password</code>
				</h3>
				<p>Defines the password to use to connect to MariaDB Server.</p>
				<pre class="fixed" data-language="sql">--password=passwd</pre>
				<p>
					When you run Mariabackup, it connects to MariaDB Server in order to
					access and back up the databases and tables. Using this option, you
					can set the password Mariabackup uses to access the server. To set
					the user, use the <code><a href="#-u-user">--user</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --user=root \
   --password=root_password</pre>
				<h3 class="anchored_heading" id="-plugin-dir">
					<code>--plugin-dir</code>
				</h3>
				<p>Defines the directory for server plugins.</p>
				<pre class="fixed" data-language="sql">--plugin-dir=PATH</pre>
				<p>
					Using this option, you can define the path Mariabackup reads for
					MariaDB Server plugins. It only uses it during the <code><a href="#-prepare">--prepare</a></code> phase to load the encryption plugin. It defaults to the <code>plugin_dir</code> server system variable.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --plugin-dir=/var/mysql/lib/plugin</pre>
				<h3 class="anchored_heading" id="-plugin-load">
					<code>--plugin-load</code>
				</h3>
				<p>Defines the encryption plugins to load.</p>
				<pre class="fixed" data-language="sql">--plugin-load=name</pre>
				<p>
					Using this option, you can define the encryption plugin you want to
					load. It is only used during the <code><a href="#-prepare">--prepare</a></code> phase to load the encryption plugin. It defaults to the server <code>--plugin-load</code> option.
				</p>
				<p>
					The option was removed starting from MariaDB 10.2.18
				</p>
				<h3 class="anchored_heading" id="-p-port">
					<code>-P, --port</code>
				</h3>
				<p>Defines the server port to connect to.</p>
				<pre class="fixed" data-language="sql">--port=#</pre>
				<p>
					When you run Mariabackup, it connects to MariaDB Server in order to
					access and back up your databases and tables. Using this option,
					you can set the port the utility uses to access the server over
					TCP/IP. To set the host, see the <code><a href="#-h-host">--host</a></code> option. Use <code>mysql --help</code> for more details.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --host=192.168.11.1 \
   --port=3306</pre>
				<h3 class="anchored_heading" id="-prepare">
					<code>--prepare</code>
				</h3>
				<p>Prepares an existing backup to restore to the MariaDB Server.</p>
				<p>
					Files that Mariabackup generates during <code><a href="#-backup">--backup</a></code> operations in the target directory are not ready for use on the
					Server. Before you can restore the data to MariaDB, you first need
					to prepare the backup.
				</p>
				<p>
					In the case of full backups, the files are not point in time
					consistent, since they were taken at different times. If you try to
					restore the database without first preparing the data, InnoDB
					rejects the new data as corrupt. Running Mariabackup with the <code>--prepare</code> command readies the data so you can restore it to MariaDB Server.
					When working with incremental backups, you need to use the <code>--prepare</code> command and the <code><a href="#-incremental-dir">--incremental-dir</a></code> option to update the base backup with the deltas from an
					incremental backup.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --prepare</pre>
				<p>
					Once the backup is ready, you can use the <code><a href="#-copy-back">--copy-back</a></code> or the <code><a href="#-move-back">--move-back</a></code> commands to restore the backup to the server.
				</p>
				<h3 class="anchored_heading" id="-print-defaults">
					<code>--print-defaults</code>
				</h3>
				<p>Prints the utility argument list, then exits.</p>
				<p>Using this argument, MariaDB prints the argument list to
					stdout and then exits. You may find this useful in debugging to see
					how the options are set for the utility.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --print-defaults</pre>
				<h3 class="anchored_heading" id="-print-param">
					<code>--print-param</code>
				</h3>
				<p>Prints the MariaDB Server options needed for copyback.</p>
				<p>
					Using this option, Mariabackup prints to stdout the MariaDB Server
					options that the utility requires to run the <code><a href="#-copy-back">--copy-back</a></code> command option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --print-param</pre>
				<h3 class="anchored_heading" id="-rsync">
					<code>--rsync</code>
				</h3>
				<p>Defines whether to use rsync.</p>
				<p>
					During normal operation, Mariabackup transfers local non-InnoDB
					files using a separate call to
					<code>cp</code>
					for each file. Using this option, you can optimize this process by
					performing this transfer with rsync, instead.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup --rsync</pre>
				<p>
					This option is not compatible with the <code><a href="#-stream">--stream</a></code> option.
				</p>
				<h3 class="anchored_heading" id="-safe-slave-backup">
					<code>--safe-slave-backup</code>
				</h3>
				<p>Stops slave SQL threads for backups.</p>
				<p>
					When running Mariabackup on a server that uses replication, you may
					occasionally encounter locks that block backups. Using this option,
					it stops slave SQL threads and waits until the <code>Slave_open_temp_tables</code> in the <code>SHOW STATUS</code> statement is zero. If there are no open temporary tables, the
					backup runs, otherwise the SQL thread starts and stops until there are no open temporary tables.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --safe-slave-backup \
   --safe-slave-backup-timeout=500</pre>
				<p>
					The backup fails if the <code>Slave_open_temp_tables</code> doesn't reach zero after the timeout period set by the <code><a href="#-safe-slave-backup-timeout">--safe-slave-backup-timeout</a></code> option.
				</p>
				<h3 class="anchored_heading" id="-safe-slave-backup-timeout">
					<code>--safe-slave-backup-timeout</code>
				</h3>
				<p>Defines the timeout for slave backups.</p>
				<pre class="fixed" data-language="sql">--safe-slave-backup-timeout=#</pre>
				<p>
					When running Mariabackup on a server that uses replication, you may occasionally encounter locks that block backups. With the <code><a href="#-safe-slave-backup">--safe-slave-backup</a></code> option, it waits until the <code>Slave_open_temp_tables</code> in the <code>SHOW STATUS</code> statement reaches zero. Using this option, you set how long it waits. It defaults to 300.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --safe-slave-backup \
   --safe-slave-backup-timeout=500</pre>
				<h3 class="anchored_heading" id="-secure-auth">
					<code>--secure-auth</code>
				</h3>
				<p>Refuses client connections to servers using the older
					protocol.</p>
				<p>
					Using this option, you can set it explicitly to refuse client
					connections to the server when using the older protocol, from
					before 4.1.1. This feature is enabled by default. Use the <code><a href="#-skip-secure-auth">--skip-secure-auth</a></code> option to disable it.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup --secure-auth</pre>
				<h3 class="anchored_heading" id="-skip-innodb-adaptive-hash-index">
					<code>--skip-innodb-adaptive-hash-index</code>
				</h3>
				<p>Disables InnoDB Adaptive Hash Index.</p>
				<p>
					Mariabackup initializes its own embedded instance of InnoDB using
					the same configuration as defined in the configuration file. Using
					this option you can explicitly disable the InnoDB Adaptive Hash
					Index. This feature is enabled by default for Mariabackup. If you
					want to explicitly enable it, use <code><a href="#-innodb-adaptive-hash-index">--innodb-adaptive-hash-index</a></code>.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --skip-innodb-adaptive-hash-index</pre>
				<h3 class="anchored_heading" id="-skip-innodb-doublewrite">
					<code>--skip-innodb-doublewrite</code>
				</h3>
				<p>Disables doublewrites for InnoDB tables.</p>
				<p>
					Mariabackup initializes its own embedded instance of InnoDB using
					the same configuration as defined in the configuration file. When
					doublewrites are enabled, InnoDB improves fault tolerance with a
					doublewrite buffer. By default this feature is turned on. Using
					this option you can disable it for Mariabackup. To explicitly
					enable doublewrites, use the <code><a href="#-innodb-doublewrite">--innodb-doublewrite</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --skip-innodb-doublewrite</pre>
				<h3 class="anchored_heading" id="-skip-innodb-log-checksums">
					<code>--skip-innodb-log-checksums</code>
				</h3>
				<p>Defines whether to exclude checksums in the InnoDB logs.</p>
				<p>
					Mariabackup initializes its own embedded instance of InnoDB using
					the same configuration as defined in the configuration file. Using
					this option, you can set Mariabackup to exclude checksums in the
					InnoDB logs. The feature is enabled by default. To explicitly
					enable it, use the <code><a href="#-innodb-log-checksums">--innodb-log-checksums</a></code> option.
				</p>
				<h3 class="anchored_heading" id="-skip-secure-auth">
					<code>--skip-secure-auth</code>
				</h3>
				<p>Refuses client connections to servers using the older
					protocol.</p>
				<p>
					Using this option, you can set it accept client connections to the
					server when using the older protocol, from before 4.1.1. By
					default, it refuses these connections. Use the <code><a href="#-secure-auth">--secure-auth</a></code> option to explicitly enable it.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup --skip-secure-auth</pre>
				<h3 class="anchored_heading" id="-slave-info">
					<code>--slave-info</code>
				</h3>
				<p>Prints the binary log position and the name of the master
					server.</p>
				<p>
					If the server is a replication slave, then this option causes Mariabackup to print the hostname
					of the slave's replication master and the binary log file and position of the slave's SQL thread to <code>stdout</code>.
				</p>
				<p>
					This option also causes Mariabackup to record this information as a <code>CHANGE MASTER</code> command that can be used to set up a new server as a slave of the
					original server's master after the backup has been restored. This
					information will be written to to the <code>xtrabackup_slave_info</code> file.
				</p>
				<p>
					Mariabackup does <strong>not</strong> check if GTIDs are being used in replication. It
					takes a shortcut and assumes that if the <code>gtid_slave_pos</code> system variable is non-empty, then it writes the <code>CHANGE MASTER</code> command with the <code>MASTER_USE_GTID</code> option set to <code>slave_pos</code>. Otherwise, it writes the <code>CHANGE MASTER</code> command with the <code>MASTER_LOG_FILE</code> and <code>MASTER_LOG_POS</code> options using the master's binary log file and position. See MDEV-19264 for more information.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --slave-info</pre>
				<h3 class="anchored_heading" id="-s-socket">
					<code>-S, --socket</code>
				</h3>
				<p>Defines the socket for connecting to local database.</p>
				<pre class="fixed" data-language="sql">--socket=name</pre>
				<p>
					Using this option, you can define the UNIX domain socket you want
					to use when connecting to a local database server. The option
					accepts a string argument. For more information, see the <code>mysql --help</code> command.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --socket=/var/mysql/mysql.sock</pre>
				<h3 class="anchored_heading" id="-ssl">
					<code>--ssl</code>
				</h3>
				<p>
					Enables TLS. By
					using this option, you can explicitly configure Mariabackup to to
					encrypt its connection with TLS when communicating with the server. You may find this useful when
					performing backups in environments where security is extra
					important or when operating over an insecure network.
				</p>
				<p>TLS is also enabled even without setting this option when
					certain other TLS options are set. For example, see the
					descriptions of the following options:</p>
				<ul start="1">
					<li><code><a href="#-ssl-ca">--ssl-ca</a></code></li>
					<li><code><a href="#-ssl-capath">--ssl-capath</a></code></li>
					<li><code><a href="#-ssl-cert">--ssl-cert</a></code></li>
					<li><code><a href="#-ssl-cipher">--ssl-cipher</a></code></li>
					<li><code><a href="#-ssl-key">--ssl-key</a></code></li>
				</ul>
				<h3 class="anchored_heading" id="-ssl-ca">
					<code>--ssl-ca</code>
				</h3>
				<p>
					Defines a path to a PEM file that should contain one or more X509
					certificates for trusted Certificate Authorities (CAs) to use for TLS. This option
					requires that you use the absolute path, not a relative path. For
					example:
				</p>
				<pre class="fixed" data-language="sql">--ssl-ca=/etc/my.cnf.d/certificates/ca.pem</pre>
				<p>This option is usually used with other TLS options. For
					example:</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --ssl-cert=/etc/my.cnf.d/certificates/client-cert.pem \
   --ssl-key=/etc/my.cnf.d/certificates/client-key.pem \
   --ssl-ca=/etc/my.cnf.d/certificates/ca.pem</pre>
				<p>
					See Secure Connections Overview: Certificate Authorities (CAs) for more information.
				</p>
				<p>
					This option implies the <code><a href="#-ssl">--ssl</a></code> option.
				</p>
				<h3 class="anchored_heading" id="-ssl-capath">
					<code>--ssl-capath</code>
				</h3>
				<p>
					Defines a path to a directory that contains one or more PEM files
					that should each contain one X509 certificate for a trusted
					Certificate Authority (CA) to use for TLS. This option
					requires that you use the absolute path, not a relative path. For
					example:
				</p>
				<pre class="fixed" data-language="sql">--ssl-capath=/etc/my.cnf.d/certificates/ca/</pre>
				<p>This option is usually used with other TLS options. For
					example:</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --ssl-cert=/etc/my.cnf.d/certificates/client-cert.pem \
   --ssl-key=/etc/my.cnf.d/certificates/client-key.pem \
   --ssl-ca=/etc/my.cnf.d/certificates/ca.pem \
   --ssl-capath=/etc/my.cnf.d/certificates/ca/</pre>
				<p>
					The directory specified by this option needs to be run through the <code>rehash</code> command.
				</p>
				<p>
					See Secure Connections Overview: Certificate Authorities (CAs) for more information
				</p>
				<p>
					This option implies the <code><a href="#-ssl">--ssl</a></code> option.
				</p>
				<h3 class="anchored_heading" id="-ssl-cert">
					<code>--ssl-cert</code>
				</h3>
				<p>
					Defines a path to the X509 certificate file to use for TLS. This option
					requires that you use the absolute path, not a relative path. For
					example:
				</p>
				<pre class="fixed" data-language="sql">--ssl-cert=/etc/my.cnf.d/certificates/client-cert.pem</pre>
				<p>This option is usually used with other TLS options. For
					example:</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --ssl-cert=/etc/my.cnf.d/certificates/client-cert.pem \
   --ssl-key=/etc/my.cnf.d/certificates/client-key.pem \
   --ssl-ca=/etc/my.cnf.d/certificates/ca.pem</pre>
				<p>
					This option implies the <code><a href="#-ssl">--ssl</a></code> option.
				</p>
				<h3 class="anchored_heading" id="-ssl-cipher">
					<code>--ssl-cipher</code>
				</h3>
				<p>
					Defines the list of permitted ciphers or cipher suites to use for TLS. For example:
				</p>
				<pre class="fixed" data-language="sql">--ssl-cipher=name</pre>
				<p>This option is usually used with other TLS options. For
					example:</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --ssl-cert=/etc/my.cnf.d/certificates/client-cert.pem \
   --ssl-key=/etc/my.cnf.d/certificates/client-key.pem \
   --ssl-ca=/etc/my.cnf.d/certificates/ca.pem
   --ssl-cipher=TLSv1.2</pre>
				<p>
					To determine if the server restricts clients to specific ciphers, check the <code>ssl_cipher</code> system variable.
				</p>
				<p>
					This option implies the <code><a href="#-ssl">--ssl</a></code> option.
				</p>
				<h3 class="anchored_heading" id="-ssl-crl">
					<code>--ssl-crl</code>
				</h3>
				<p>
					Defines a path to a PEM file that should contain one or more
					revoked X509 certificates to use for TLS. This option
					requires that you use the absolute path, not a relative path. For
					example:
				</p>
				<pre class="fixed" data-language="sql">--ssl-crl=/etc/my.cnf.d/certificates/crl.pem</pre>
				<p>This option is usually used with other TLS options. For
					example:</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --ssl-cert=/etc/my.cnf.d/certificates/client-cert.pem \
   --ssl-key=/etc/my.cnf.d/certificates/client-key.pem \
   --ssl-ca=/etc/my.cnf.d/certificates/ca.pem \
   --ssl-crl=/etc/my.cnf.d/certificates/crl.pem</pre>
				<p>
					See Secure Connections Overview: Certificate Revocation Lists (CRLs) for more information.
				</p>
				<p>
					This option is only supported if Mariabackup was built with
					OpenSSL. If Mariabackup was built with yaSSL, then this option is
					not supported. See TLS and Cryptography Libraries Used by MariaDB for more information about which libraries are used on which platforms.
				</p>
				<h3 class="anchored_heading" id="-ssl-crlpath">
					<code>--ssl-crlpath</code>
				</h3>
				<p>
					Defines a path to a directory that contains one or more PEM files
					that should each contain one revoked X509 certificate to use for TLS. This option
					requires that you use the absolute path, not a relative path. For
					example:
				</p>
				<pre class="fixed" data-language="sql">--ssl-crlpath=/etc/my.cnf.d/certificates/crl/</pre>
				<p>This option is usually used with other TLS options. For example:</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --ssl-cert=/etc/my.cnf.d/certificates/client-cert.pem \
   --ssl-key=/etc/my.cnf.d/certificates/client-key.pem \
   --ssl-ca=/etc/my.cnf.d/certificates/ca.pem \
   --ssl-crlpath=/etc/my.cnf.d/certificates/crl/ </pre>
				<p>
					The directory specified by this option needs to be run through the <code>openssl rehash</code> command.
				</p>
				<p>
					See Secure Connections Overview: Certificate Revocation Lists (CRLs) for more information.
				</p>
				<p>
					This option is only supported if Mariabackup was built with
					OpenSSL. If Mariabackup was built with yaSSL, then this option is
					not supported. See TLS and Cryptography Libraries Used by MariaDB for more information about which libraries are used on which platforms.
				</p>
				<h3 class="anchored_heading" id="-ssl-key">
					<code>--ssl-key</code>
				</h3>
				<p>
					Defines a path to a private key file to use for TLS. This option
					requires that you use the absolute path, not a relative path. For
					example:
				</p>
				<pre class="fixed" data-language="sql">--ssl-key=/etc/my.cnf.d/certificates/client-key.pem</pre>
				<p>This option is usually used with other TLS options. For example:</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --ssl-cert=/etc/my.cnf.d/certificates/client-cert.pem \
   --ssl-key=/etc/my.cnf.d/certificates/client-key.pem \
   --ssl-ca=/etc/my.cnf.d/certificates/ca.pem</pre>
				<p>
					This option implies the <code><a href="#-ssl">--ssl</a></code> option.
				</p>
				<h3 class="anchored_heading" id="-ssl-verify-server-cert">
					<code>--ssl-verify-server-cert</code>
				</h3>
				<p>
					Enables server certificate verification. This option is disabled by default.
				</p>
				<p>This option is usually used with other TLS options. For example:</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --ssl-cert=/etc/my.cnf.d/certificates/client-cert.pem \
   --ssl-key=/etc/my.cnf.d/certificates/client-key.pem \
   --ssl-ca=/etc/my.cnf.d/certificates/ca.pem \
   --ssl-verify-server-cert</pre>
				<h3 class="anchored_heading" id="-stream">
					<code>--stream</code>
				</h3>
				<p>Streams backup files to stdout.</p>
				<pre class="fixed" data-language="sql">--stream=xbstream</pre>
				<p>
					Using this command option, you can set Mariabackup to stream the
					backup files to stdout in the given format. Currently, the
					supported format is <code>xbstream</code>.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --stream=xbstream &gt; backup.xb</pre>
				<p>
					To extract all files from the xbstream archive into a directory use the <code>mbstream</code> utility
				</p>
				<pre class="fixed" data-language="sql">$ mbstream  -x &lt; backup.xb</pre>
				<p>
					If a backup is streamed, then Mariabackup will record the format in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-tables">
					<code>--tables</code>
				</h3>
				<p>Defines the tables you want to include in the backup.</p>
				<pre class="fixed" data-language="sql">--tables=REGEX</pre>
				<p>
					Using this option, you can define what tables you want Mariabackup
					to back up from the database. The table values are defined using
					Regular Expressions. To define the tables you want to exclude from
					the backup, see the <code><a href="#-tables-exclude">--tables-exclude</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --databases=example
   --tables=nodes_* \
   --tables-exclude=nodes_tmp</pre>
				<p>
					If a backup is a partial backup, then Mariabackup will record that detail in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-tables-exclude">
					<code>--tables-exclude</code>
				</h3>
				<p>Defines the tables you want to exclude from the backup.</p>
				<pre class="fixed" data-language="sql">--tables-exclude=REGEX</pre>
				<p>
					Using this option, you can define what tables you want Mariabackup
					to exclude from the backup. The table values are defined using
					Regular Expressions. To define the tables you want to include from
					the backup, see the <code><a href="#-tables">--tables</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --databases=example
   --tables=nodes_* \
   --tables-exclude=nodes_tmp</pre>
				<p>
					If a backup is a partial backup, then Mariabackup will record that detail in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-tables-file">
					<code>--tables-file</code>
				</h3>
				<p>Defines path to file with tables for backups.</p>
				<pre class="fixed" data-language="sql">--tables-file=/path/to/file</pre>
				<p>
					Using this option, you can set a path to a file listing the tables
					you want to back up. Mariabackup iterates over each line in the
					file. The format is <code>database.table</code>.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --databases=example \
   --tables-file=/etc/mysql/backup-file</pre>
				<p>
					If a backup is a partial backup, then Mariabackup will record that detail in the <code>xtrabackup_info</code> file.
				</p>
				<h3 class="anchored_heading" id="-target-dir">
					<code>--target-dir</code>
				</h3>
				<p>Defines the destination directory.</p>
				<pre class="fixed" data-language="sql">--target-dir=/path/to/target</pre>
				<p>Using this option you can define the destination directory
					for the backup. Mariabackup writes all backup files to this
					directory. Mariabackup will create the directory, if it does not
					exist (but it will not create the full path recursively, i.e. at
					least parent directory if the --target-dir must exist=</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --target-dir=/data/backups</pre>
				<h3 class="anchored_heading" id="-throttle">
					<code>--throttle</code>
				</h3>
				<p>Defines the limit for I/O operations per second in IOS
					values.</p>
				<pre class="fixed" data-language="sql">--throttle=#</pre>
				<p>
					Using this option, you can set a limit on the I/O operations
					Mariabackup performs per second in IOS values. It is only used
					during the <code><a href="#-backup">--backup</a></code> command option.
				</p>
				<h3 class="anchored_heading" id="-tls-version">
					<code>--tls-version</code>
				</h3>
				<p>This option accepts a comma-separated list of TLS protocol
					versions. A TLS protocol version will only be enabled if it is
					present in this list. All other TLS protocol versions will not be
					permitted. For example:</p>
				<pre class="fixed" data-language="sql">--tls-version="TLSv1.2,TLSv1.3"</pre>
				<p>This option is usually used with other TLS options. For example:</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --ssl-cert=/etc/my.cnf.d/certificates/client-cert.pem \
   --ssl-key=/etc/my.cnf.d/certificates/client-key.pem \
   --ssl-ca=/etc/my.cnf.d/certificates/ca.pem \
   --tls-version="TLSv1.2,TLSv1.3"</pre>
				<p>
					This option was added in MariaDB 10.4.6.
				</p>
				<p>
					See Secure Connections Overview: TLS Protocol Versions for more information.
				</p>
				<h3 class="anchored_heading" id="-t-tmpdir">
					<code>-t, --tmpdir</code>
				</h3>
				<p>Defines path for temporary files.</p>
				<pre class="fixed" data-language="sql">--tmpdir=/path/tmp[;/path/tmp...]</pre>
				<p>
					Using this option, you can define the path to a directory
					Mariabackup uses in writing temporary files. If you want to use
					more than one, separate the values by a semicolon (that is, <code>;</code>). When passing multiple temporary directories, it cycles through
					them using round-robin.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --tmpdir=/data/tmp;/tmp</pre>
				<h3 class="anchored_heading" id="-use-memory">
					<code>--use-memory</code>
				</h3>
				<p>Defines the buffer pool size.</p>
				<pre class="fixed" data-language="sql">--use-memory=124M</pre>
				<p>
					Using this option, you can define the buffer pool size for
					Mariabackup. Use it instead of <code>buffer_pool_size</code>.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --use-memory=124M</pre>
				<h3 class="anchored_heading" id="-user">
					<code>--user</code>
				</h3>
				<p>Defines the username for connecting to the MariaDB Server.</p>
				<pre class="fixed" data-language="sql">--user=name -u name</pre>
				<p>When Mariabackup runs it connects to the specified MariaDB
					Server to get its backups. Using this option, you can define the
					database user uses for authentication.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup \
   --user=root \
   --password=root_passwd</pre>
				<h3 class="anchored_heading" id="-version">
					<code>--version</code>
				</h3>
				<p>Prints version information.</p>
				<p>Using this option, you can print the Mariabackup version
					information to stdout.</p>
				<pre class="fixed" data-language="sql">$ mariabackup --version</pre>
				<h3 class="anchored_heading" id="-version-check">
					<code>--version-check</code>
				</h3>
				<p>Enables version check.</p>
				<p>
					Using this option, you can enable Mariabackup version check. If you
					would like to disable the version check, use the <code><a href="#-no-version-check">--no-version-check</a></code> option.
				</p>
				<pre class="fixed" data-language="sql">$ mariabackup --backup --version-check</pre>
			</div>
		</div>
		<div id="content_disclaimer" class="graybox">Content reproduced
			on this site is the property of its respective owners, and this
			content is not reviewed in advance by MariaDB. The views, information
			and opinions expressed by this content do not necessarily represent
			those of MariaDB or any other party.</div>
	</div>

	<p>
		<a href="#top">[top]</a>
	</p>
	<script type="text/javascript">
		$("#ulList li b").click(function() {
			opener.setChildValue($(this).text(), "xtra");
		});
	</script>
</body>
</html>

