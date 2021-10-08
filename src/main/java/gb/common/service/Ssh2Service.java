package gb.common.service;

import java.util.Map;

public interface Ssh2Service {
	
	Map<String, Object> getData(String command, String id, String pw, String ip, int port, int timeout) throws Exception;
	
	Map<String, Object> getData(String command, String id, String pw, String ip, int port) throws Exception;
	
	Map<String, Object> backupRun(String command, String id, String pw, String ip, int port) throws Exception;
	
	Map<String, Object> restoreRun(String command, String id, String pw, String ip, int port) throws Exception;

	Map<String, Object> getList(String command, String id, String pw, String ip, int port, int timeout) throws Exception;
	
	Map<String, Object> getList(String command, String id, String pw, String ip, int port) throws Exception;
	
	Map<String, Object> executeCmd(String cmd) throws Exception;
	
	Map<String, Object> executeListCmd(String cmd) throws Exception;
	
	boolean isconnect(String id, String pw, String ip, int port) throws Exception;
	
}

