package gb.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
 
@Service("ssh2Service")
public class Ssh2ServiceImpl implements Ssh2Service{
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Value("#{config['local.env']}") private String local_env;
	
	public Map<String, Object> getData(String command, String id, String pw, String ip, int port, int timeout) throws Exception {
		
		Map<String, Object> map = null;
		
		if ("dev".equals(local_env)) {

			Session sess = null;
			Connection conn = null;
			try {
				
				map = new HashMap<String, Object>();
				
				conn = new Connection(ip, port);
				conn.connect();
				
				boolean isAuthenticated = conn.authenticateWithPassword(id, pw);

				if (isAuthenticated == false) {
					map.put("auth", "Authentication failed.");
					log.error("Authentication failed.");
				}
				
				sess = conn.openSession();
				sess.execCommand(command);
				
				InputStream stdout = sess.getStdout(); 
				InputStream stderr = sess.getStderr(); 
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				while (true) {
					if ((stdout.available() == 0) && (stderr.available() == 0)) {
						int conditions = sess.waitForCondition(ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA 
								| ChannelCondition.EOF, timeout*1000);
						if ((conditions & ChannelCondition.TIMEOUT) != 0) { 
							/* A timeout occured. */ 
							//throw new IOException("Timeout while waiting for data from peer."); 
							map.put("value", "Timeout while waiting for data from peer.");
							break;
						}
					}
					String line = br.readLine();
					if (line == null)
						break;
					map.put("value", line);
				}
				/* Show exit status, if available (otherwise "null") */
				map.put("ExitCode", sess.getExitStatus());
				
			} catch (IOException e) {
				map.put("Exception", e.getMessage());
				e.printStackTrace();
				log.error("getData IOException : "+e.fillInStackTrace());
			} catch (Exception e) {
				map.put("Exception", e.getMessage());
				log.error("getData Exception : "+e.fillInStackTrace());
			} finally {
				if (sess != null) sess.close();
				if (conn != null) conn.close();
			}
			
			
		} else if ("mtn".equals(local_env)) {
			
			map = new HashMap<String, Object>();
			map = executeCmd(command);
			
		}
		
		return map;
	}
	
	public Map<String, Object> getData(String command, String id, String pw, String ip, int port) throws Exception {
		Map<String, Object> map = null;
		
		if ("dev".equals(local_env)) {
			
			Session sess = null;
			Connection conn = null;
			try {
				map = new HashMap<String, Object>();
				
				conn = new Connection(ip, port);
				conn.connect();
				
				boolean isAuthenticated = conn.authenticateWithPassword(id, pw);
				
				if (isAuthenticated == false) {
					map.put("auth", "Authentication failed.");
					log.error("Authentication failed.");
				}
				
				sess = conn.openSession();
				sess.execCommand(command);
				
				InputStream stdout = sess.getStdout(); 
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				while (true) {
					String line = br.readLine();
					if (line == null)
						break;
					map.put("value", line);
				}
				/* Show exit status, if available (otherwise "null") */
				map.put("ExitCode", sess.getExitStatus());
				
			} catch (IOException e) {
				e.printStackTrace();
				log.error("getData IOException : "+e.fillInStackTrace());
			} finally {
				if (sess != null) sess.close();
				if (conn != null) conn.close();
			}
			
		} else if ("mtn".equals(local_env)) {
			
			map = new HashMap<String, Object>();
			map = executeCmd(command);
			
		}
		return map;
	}
	
	public Map<String, Object> backupRun(String command, String id, String pw, String ip, int port) throws Exception {
		Map<String, Object> map = null;
		
		if ("dev".equals(local_env)) {
			
			Session sess = null;
			Connection conn = null;
			try {
				map = new HashMap<String, Object>();
				
				conn = new Connection(ip, port);
				conn.connect();
				
				boolean isAuthenticated = conn.authenticateWithPassword(id, pw);
				
				if (isAuthenticated == false) {
					map.put("auth", "Authentication failed.");
					log.error("Authentication failed.");
				}
				
				sess = conn.openSession();
				sess.execCommand(command);
				
				InputStream stdout = sess.getStdout(); 
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				while (true) {
					String line = br.readLine();
					if (line == null)
						break;
					map.put("value", line);
					if (line.matches("(.*)BACKUP COMPLETE(.*)") || line.matches("(.*)BACKUP FAILED(.*)")
							|| line.matches("(.*)BACKUP END(.*)")
							) {
						break;
					}
				}
				/* Show exit status, if available (otherwise "null") */
				
			} catch (IOException e) {
				e.printStackTrace();
				log.error("backupRun IOException : "+e.fillInStackTrace());
			} finally {
				if (sess != null) sess.close();
				if (conn != null) conn.close();
			}
			
		} else if ("mtn".equals(local_env)) {
			map = new HashMap<String, Object>();
			map = executeBackupRunCmd(command);
		}
		
		return map;
	}
	
	public Map<String, Object> restoreRun(String command, String id, String pw, String ip, int port) throws Exception {
		Map<String, Object> map = null;
		
		if ("dev".equals(local_env)) {
			Session sess = null;
			Connection conn = null;
			try {
				map = new HashMap<String, Object>();
				
				conn = new Connection(ip, port);
				conn.connect();
				
				boolean isAuthenticated = conn.authenticateWithPassword(id, pw);
				
				if (isAuthenticated == false) {
					map.put("auth", "Authentication failed.");
					log.error("Authentication failed.");
				}
				
				sess = conn.openSession();
				sess.execCommand(command);
				
				InputStream stdout = sess.getStdout(); 
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				while (true) {
					String line = br.readLine();
					if (line == null)
						break;
					map.put("value", line);
					if (line.matches("(.*)RESTORE COMPLETE(.*)") || line.matches("(.*)RESTORE FAILED(.*)")
							|| line.matches("(.*)RESTORE END(.*)")
							) {
						break;
					}
				}
				/* Show exit status, if available (otherwise "null") */
				
			} catch (IOException e) {
				e.printStackTrace();
				log.error("restoreRun IOException : "+e.fillInStackTrace());
			} finally {
				if (sess != null) sess.close();
				if (conn != null) conn.close();
			}
		} else if ("mtn".equals(local_env)) {
			
			map = new HashMap<String, Object>();
			map = executeRestoreRunCmd(command);
		}
		
		
		return map;
	}
	
	public Map<String, Object> getList(String command, String id, String pw, String ip, int port, int timeout) throws Exception {
		Map<String, Object> map = null;

		if ("dev".equals(local_env)) {
			List<String> list = null;
			Session sess = null;
			Connection conn = null;
			try {
				map = new HashMap<String, Object>();
				
				conn = new Connection(ip, port);
				conn.connect();
				
				boolean isAuthenticated = conn.authenticateWithPassword(id, pw);

				if (isAuthenticated == false) {
					map.put("auth", "Authentication failed.");
					log.error("Authentication failed.");
				}
				
				sess = conn.openSession();
				sess.execCommand(command);
				
				InputStream stdout = sess.getStdout(); 
				InputStream stderr = sess.getStderr(); 
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				list = new ArrayList<String>();
				while (true) {
					if ((stdout.available() == 0) && (stderr.available() == 0)) {
						int conditions = sess.waitForCondition(ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA 
								| ChannelCondition.EOF, timeout*1000);
						if ((conditions & ChannelCondition.TIMEOUT) != 0) { 
							/* A timeout occured. */ 
//							throw new IOException("Timeout while waiting for data from peer."); 
							log.error("Timeout while waiting for data from peer.");
						}
					}
					String line = br.readLine();
					if (line == null)
						break;
					list.add(line);
				}
				map.put("value", list);
				/* Show exit status, if available (otherwise "null") */
				
			} catch (IOException e) {
				e.printStackTrace();
//				throw new Exception(e.fillInStackTrace().toString());
				log.error("getList IOException : "+e.fillInStackTrace());
			} finally {
				if (sess != null) sess.close();
				if (conn != null) conn.close();
			}
		} else if ("mtn".equals(local_env)) {
			map = new HashMap<String, Object>();
			map = executeListCmd(command);
		}
		
		
		return map;
	}
	
	public Map<String, Object> getList(String command, String id, String pw, String ip, int port) throws Exception {
		Map<String, Object> map = null;
		
		if ("dev".equals(local_env)) {
			List<String> list = null;
			Session sess = null;
			Connection conn = null;
			try {
				map = new HashMap<String, Object>();
				
				conn = new Connection(ip, port);
				conn.connect();
				
				boolean isAuthenticated = conn.authenticateWithPassword(id, pw);
				
				if (isAuthenticated == false) {
					map.put("auth", "Authentication failed.");
					log.error("Authentication failed.");
				}
				
				sess = conn.openSession();
				sess.execCommand(command);
				
				InputStream stdout = sess.getStdout(); 
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				list = new ArrayList<String>();
				while (true) {
					String line = br.readLine();
					if (line == null)
						break;
					list.add(line);
				}
				map.put("list", list);
				/* Show exit status, if available (otherwise "null") */
				
			} catch (IOException e) {
				e.printStackTrace();
//				throw new Exception(e.fillInStackTrace().toString());
				log.error("getList IOException : "+e.fillInStackTrace());
			} finally {
				if (sess != null) sess.close();
				if (conn != null) conn.close();
			}
		} else if ("mtn".equals(local_env)) {
			map = new HashMap<String, Object>();
			map = executeListCmd(command);
		}
		
		
		return map;
	}
	
	//was 직접호출
	public Map<String, Object> executeCmd(String cmd) throws Exception {
		Map<String, Object> map = null;
		try {
			map = new HashMap<String, Object>();
			
			String szLine;
			
			// 응용 프로그램의 Runtime 객체를 얻는다
			Runtime runtime = Runtime.getRuntime();
			
			// 응용 프로그램의 서브 프로세스인 Process 객체를 얻는다
			Process process = runtime.exec(cmd);
			
			// 응용 프로그램의 입력 스트림을 구한다.
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			// 응용 프로그램의 입력 스트림에서 한 라인씩 읽어서 응용 프로그램의 표준 출력으로 보낸다
			while (true) {
				szLine = br.readLine();
				if (szLine == null) break;
				map.put("value", szLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("executeCmd Exception : "+e.fillInStackTrace());
		}
		
		return map;
	}
	
	//was 직접호출
	public Map<String, Object> executeBackupRunCmd(String cmd) throws Exception {
		Map<String, Object> map = null;
		try {
			map = new HashMap<String, Object>();
			
			// 응용 프로그램의 Runtime 객체를 얻는다
			Runtime runtime = Runtime.getRuntime();
			
			// 응용 프로그램의 서브 프로세스인 Process 객체를 얻는다
			Process process = runtime.exec(cmd);
			
			// 응용 프로그램의 입력 스트림을 구한다.
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			// 응용 프로그램의 입력 스트림에서 한 라인씩 읽어서 응용 프로그램의 표준 출력으로 보낸다
			while (true) {
				String line = br.readLine();
				if (line == null) break;
				map.put("value", line);
				if (line.matches("(.*)RESTORE COMPLETE(.*)") || line.matches("(.*)RESTORE FAILED(.*)")
						|| line.matches("(.*)RESTORE END(.*)")
						) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("executeBackupRunCmd Exception : "+e.fillInStackTrace());
		}
		
		return map;
	}
	
	//was 직접호출
	public Map<String, Object> executeRestoreRunCmd(String cmd) throws Exception {
		Map<String, Object> map = null;
		try {
			map = new HashMap<String, Object>();
			
			// 응용 프로그램의 Runtime 객체를 얻는다
			Runtime runtime = Runtime.getRuntime();
			
			// 응용 프로그램의 서브 프로세스인 Process 객체를 얻는다
			Process process = runtime.exec(cmd);
			
			// 응용 프로그램의 입력 스트림을 구한다.
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			// 응용 프로그램의 입력 스트림에서 한 라인씩 읽어서 응용 프로그램의 표준 출력으로 보낸다
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				map.put("value", line);
				if (line.matches("(.*)RESTORE COMPLETE(.*)") || line.matches("(.*)RESTORE FAILED(.*)")
						|| line.matches("(.*)RESTORE END(.*)")
						) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("executeRestoreRunCmd Exception : "+e.fillInStackTrace());
		}
		
		return map;
	}
	
	//was 직접호출
	public Map<String, Object> executeListCmd(String cmd) throws Exception {
		Map<String, Object> map = null;
		List<String> list = null;
		try {
			map = new HashMap<String, Object>();
			
			String szLine;
			
			// 응용 프로그램의 Runtime 객체를 얻는다
			Runtime runtime = Runtime.getRuntime();
			
			// 응용 프로그램의 서브 프로세스인 Process 객체를 얻는다
			Process process = runtime.exec(cmd);
			
			// 응용 프로그램의 입력 스트림을 구한다.
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			// 응용 프로그램의 입력 스트림에서 한 라인씩 읽어서 응용 프로그램의 표준 출력으로 보낸다
			list = new ArrayList<String>();
			while (true) {
				szLine = br.readLine();
				if (szLine == null) break;
				list.add(szLine);
			}
			map.put("value", list);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("executeListCmd Exception : "+e.fillInStackTrace());
		}
		
		return map;
	}
	
	
	
	public boolean isconnect(String id, String pw, String ip, int port) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = new Connection(ip, port);
			conn.connect();
			result = conn.authenticateWithPassword(id, pw);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("isconnect Exception : "+e.fillInStackTrace());
		} finally {
			if (conn != null) conn.close();
		}
		return result;
	}
}
