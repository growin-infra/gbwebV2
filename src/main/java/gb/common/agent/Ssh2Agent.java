package gb.common.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class Ssh2Agent {
	
	static Logger ssh2log = Logger.getLogger(Ssh2Agent.class);
	
	public static Map<String, Object> getData(String command, String id, String pw, String ip, int port, int timeout) throws Exception {
		Map<String, Object> map = null;
		Session sess = null;
		Connection conn = null;
		try {
			map = new HashMap<String, Object>();
			
			conn = new Connection(ip, port);
			conn.connect();
			
			boolean isAuthenticated = conn.authenticateWithPassword(id, pw);

			if (isAuthenticated == false) {
				ssh2log.error("Authentication failed.");
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
//			if (sess.getExitStatus() != null) {
//			}
			
		} catch (IOException e) {
			map.put("Exception", e.getMessage());
			e.printStackTrace();
//			throw new Exception(e.fillInStackTrace().toString());
			ssh2log.error("getData IOException : ",e.fillInStackTrace());
		} catch (Exception e) {
			map.put("Exception", e.getMessage());
			ssh2log.error("getData Exception : ",e.fillInStackTrace());
		} finally {
			if (sess != null) sess.close();
			if (conn != null) conn.close();
		}
		return map;
	}
	
	public static Map<String, Object> getData(String command, String id, String pw, String ip, int port) throws Exception {
		Map<String, Object> map = null;
		Session sess = null;
		Connection conn = null;
		try {
			map = new HashMap<String, Object>();
			
			conn = new Connection(ip, port);
			conn.connect();
			
			boolean isAuthenticated = conn.authenticateWithPassword(id, pw);
			
			if (isAuthenticated == false) {
				ssh2log.error("Authentication failed.");
				throw new Exception("Authentication failed.");
			}
			
			sess = conn.openSession();
			sess.execCommand(command);
			
			InputStream stdout = sess.getStdout(); 
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
//			byte[] buffer = new byte[8192]; 
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				map.put("value", line);
			}
			/* Show exit status, if available (otherwise "null") */
			map.put("ExitCode", sess.getExitStatus());
//			if (sess.getExitStatus() != null) {
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
//			throw new Exception(e.fillInStackTrace().toString());
			ssh2log.error("getData IOException : ",e.fillInStackTrace());
		} finally {
			if (sess != null) sess.close();
			if (conn != null) conn.close();
		}
		return map;
	}
	
	public static Map<String, Object> backupRun(String command, String id, String pw, String ip, int port) throws Exception {
		Map<String, Object> map = null;
		Session sess = null;
		Connection conn = null;
		try {
			map = new HashMap<String, Object>();
			
			conn = new Connection(ip, port);
			conn.connect();
			
			boolean isAuthenticated = conn.authenticateWithPassword(id, pw);
			
			if (isAuthenticated == false) {
				ssh2log.error("Authentication failed.");
				throw new Exception("Authentication failed.");
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
//			if (sess.getExitStatus() != null) {
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
//			throw new Exception(e.fillInStackTrace().toString());
			ssh2log.error("backupRun IOException : ",e.fillInStackTrace());
		} finally {
			if (sess != null) sess.close();
			if (conn != null) conn.close();
		}
		return map;
	}
	
	public static Map<String, Object> restoreRun(String command, String id, String pw, String ip, int port) throws Exception {
		Map<String, Object> map = null;
		Session sess = null;
		Connection conn = null;
		try {
			map = new HashMap<String, Object>();
			
			conn = new Connection(ip, port);
			conn.connect();
			
			boolean isAuthenticated = conn.authenticateWithPassword(id, pw);
			
			if (isAuthenticated == false) {
//				throw new Exception("Authentication failed.");
				ssh2log.error("Authentication failed.");
			}
			
			sess = conn.openSession();
			sess.execCommand(command);
			
			InputStream stdout = sess.getStdout(); 
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
//			byte[] buffer = new byte[8192]; 
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
//			if (sess.getExitStatus() != null) {
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
//			throw new Exception(e.fillInStackTrace().toString());
			ssh2log.error("restoreRun IOException : ",e.fillInStackTrace());
		} finally {
			if (sess != null) sess.close();
			if (conn != null) conn.close();
		}
		return map;
	}
	
	public static Map<String, Object> getList(String command, String id, String pw, String ip, int port, int timeout) throws Exception {
		Map<String, Object> map = null;
		List<String> list = null;
		Session sess = null;
		Connection conn = null;
		try {
			map = new HashMap<String, Object>();
			
			conn = new Connection(ip, port);
			conn.connect();
			
			boolean isAuthenticated = conn.authenticateWithPassword(id, pw);

			if (isAuthenticated == false) {
//				throw new IOException("Authentication failed.");
				ssh2log.error("Authentication failed.");
			}
			
			sess = conn.openSession();
			sess.execCommand(command);
			
			InputStream stdout = sess.getStdout(); 
			InputStream stderr = sess.getStderr(); 
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
//			byte[] buffer = new byte[8192]; 
			list = new ArrayList<String>();
			while (true) {
				if ((stdout.available() == 0) && (stderr.available() == 0)) {
					int conditions = sess.waitForCondition(ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA 
							| ChannelCondition.EOF, timeout*1000);
					if ((conditions & ChannelCondition.TIMEOUT) != 0) { 
						/* A timeout occured. */ 
						map.put("value", "Timeout while waiting for data from peer.");
						break;
					}
				}
				String line = br.readLine();
				if (line == null)
					break;
				list.add(line);
			}
			map.put("value", list);
			/* Show exit status, if available (otherwise "null") */
//			if (sess.getExitStatus() != null) {
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
//			throw new Exception(e.fillInStackTrace().toString());
			ssh2log.error("getList IOException : ",e.fillInStackTrace());
		} finally {
			if (sess != null) sess.close();
			if (conn != null) conn.close();
		}
		return map;
	}
	
	public static Map<String, Object> getList(String command, String id, String pw, String ip, int port) throws Exception {
		Map<String, Object> map = null;
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
				ssh2log.error("Authentication failed.");
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
//			if (sess.getExitStatus() != null) {
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
//			throw new Exception(e.fillInStackTrace().toString());
			ssh2log.error("getList IOException : ",e.fillInStackTrace());
		} finally {
			if (sess != null) sess.close();
			if (conn != null) conn.close();
		}
		return map;
	}
	
	
	@SuppressWarnings("resource")
	public static Map<String, Object> getLog(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> resutlMap = null;
		Session sess = null;
		Connection conn = null;
		RandomAccessFile file = null;
		long startPoint = 0;
		long endPoint = 0;
		
		String command = "", filename = "";
		String id = "", pw = "", ip = "";
		int port;
		long preEndPoint = 0;
		
		try {
			resutlMap = new HashMap<String, Object>();
			
			command = paramMap.get("command").toString();
			filename = paramMap.get("filename").toString();
			id = paramMap.get("id").toString();
			pw = paramMap.get("pw").toString();
			ip = paramMap.get("ip").toString();
			port = Integer.parseInt(paramMap.get("port").toString());
			preEndPoint = Long.parseLong(paramMap.get("preEndPoint").toString());
			
			conn = new Connection(ip, port);
			conn.connect();
			
			boolean isAuthenticated = conn.authenticateWithPassword(id, pw);

			if (isAuthenticated == false) {
//				throw new IOException("Authentication failed.");
				ssh2log.error("Authentication failed.");
			}
			
			sess = conn.openSession();
			sess.execCommand(command);
			
			file = new RandomAccessFile(filename, "r");
			endPoint = file.length();
			startPoint = preEndPoint > 0 ? preEndPoint : endPoint < 2000 ? 0 : endPoint - 2000; 
			
			file.seek(startPoint);
			
			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			StringBuilder sb = new StringBuilder();
			
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				sb.append(line);
				sb.append("\n");
				
				//화면에 로그 보내기
				//ExcutePop.setpJTA03(sb.toString());
				if (line.matches("(.*)BACKUP COMPLETE(.*)") || line.matches("(.*)BACKUP FAILED(.*)")
						|| line.matches("(.*)BACKUP END(.*)")
						) {
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception(e.fillInStackTrace().toString());
		} finally {
			if (sess != null) sess.close();
			if (conn != null) conn.close();
		}
		return resutlMap;
	}
	
	//was 직접호출
	public static Map<String, Object> executeCmd(String cmd) throws Exception {
		Map<String, Object> map = null;
		try {
			map = new HashMap<String, Object>();
			
			String szLine;
			
			// 응용 프로그램의 Runtime 객체를 얻는다
			Runtime runtime = Runtime.getRuntime();
			
			// 응용 프로그램의 서브 프로세스인 Process 객체를 얻는다
//			Process process = runtime.exec("ls -al");
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
		}
		
		return map;
	}
	
	//was 직접호출
	public static Map<String, Object> executeListCmd(String cmd) throws Exception {
		Map<String, Object> map = null;
		List<String> list = null;
		try {
			map = new HashMap<String, Object>();
			
			String szLine;
			
			// 응용 프로그램의 Runtime 객체를 얻는다
			Runtime runtime = Runtime.getRuntime();
			
			// 응용 프로그램의 서브 프로세스인 Process 객체를 얻는다
//			Process process = runtime.exec("ls -al");
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
		}
		return map;
	}
	
	
	public static boolean isconnect(String id, String pw, String ip, int port) {
		boolean result = false;
		Connection conn = null;
		try {
			conn = new Connection(ip, port);
			conn.connect();
			result = conn.authenticateWithPassword(id, pw);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) conn.close();
		}
		return result;
	}
	
}
