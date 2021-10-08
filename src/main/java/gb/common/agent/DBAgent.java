package gb.common.agent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;

import gb.common.util.DataBaseUtility;

public class DBAgent {

	public static Map<String, Object> getData(String url,String dc,String file,String id,String pw,String ip,String port,String sql) throws Exception {
		
		Map<String, Object> map = new HashMap<String, Object>();
		ResultSet rs = null;
		List<Map<String, String>> resultList = null;
		BasicDataSource dataSource = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		try {
			url = "jdbc:mysql://"+ip+":"+port+"/mysql?autoReconnect=true";
			
			//url,driverClass,id,pw,file
			dataSource = DataBaseUtility.getDataSource(url,dc,id,pw,file);
			connection = dataSource.getConnection();
			pstmt = connection.prepareStatement(sql);
			
			try {
				rs = pstmt.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				int colCnt = rsmd.getColumnCount();
				resultList = new ArrayList<Map<String, String>>();
				while (rs.next()) {
					LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();
					for(int i = 1; i <= colCnt; i++){  //i가 1부터 시작함에 유의
				        String columnName = rsmd.getColumnLabel(i).toUpperCase();
				        lhm.put(columnName, rs.getString(columnName));
				    }
				    resultList.add(lhm);
				}
			} catch (Exception e) {
				connection.rollback();
				e.printStackTrace();
			} finally {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
				if (connection != null) connection.close();
			}
			map.put("list", resultList);
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static Map<String, Object> getData(Map<String, Object> pmap) throws Exception {
		
		Map<String, Object> resultmap = new HashMap<String, Object>();
		ResultSet rs = null;
		List<Map<String, String>> resultList = null;
		
		BasicDataSource dataSource = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		
		try {
			String url = "";
			String ip = pmap.get("ip").toString();
			String port = pmap.get("port").toString();
			String dc = pmap.get("dc").toString();
			String id = pmap.get("id").toString();
			String pw = pmap.get("pw").toString();
			String sql = pmap.get("sql").toString();
			url = "jdbc:mysql://"+ip+":"+port+"/mysql?autoReconnect=true";
			
			//url,driverClass,id,pw,file
			dataSource = DataBaseUtility.getDataSource(dc,url,id,pw);
			connection = dataSource.getConnection();
			pstmt = connection.prepareStatement(sql);
			
			try {
				rs = pstmt.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				int colCnt = rsmd.getColumnCount();
				resultList = new ArrayList<Map<String, String>>();
				while (rs.next()) {
					LinkedHashMap<String, String> lhm = new LinkedHashMap<String, String>();
					for(int i = 1; i <= colCnt; i++){  //i가 1부터 시작함에 유의
						String columnName = rsmd.getColumnLabel(i).toUpperCase();
						lhm.put(columnName, rs.getString(columnName));
					}
					resultList.add(lhm);
				}
			} catch (Exception e) {
				//connection.rollback();
				e.printStackTrace();
			} finally {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
				if (connection != null) connection.close();
			}
			resultmap.put("list", resultList);
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return resultmap;
	}
	
	public static void getNoData(Map<String, Object> pmap) throws Exception {
		
		ResultSet rs = null;
		
		BasicDataSource dataSource = null;
		Connection connection = null;
		PreparedStatement pstmt = null;
		
		try {
			String url = "";
			String ip = pmap.get("ip").toString();
			String port = pmap.get("port").toString();
			String dc = pmap.get("dc").toString();
			String id = pmap.get("id").toString();
			String pw = pmap.get("pw").toString();
			String sql = pmap.get("sql").toString();
			url = "jdbc:mysql://"+ip+":"+port+"/mysql?autoReconnect=true";
			
			//url,driverClass,id,pw,file
			dataSource = DataBaseUtility.getDataSource(dc,url,id,pw);
			connection = dataSource.getConnection();
			pstmt = connection.prepareStatement(sql);
			
			try {
				rs = pstmt.executeQuery();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
				if (connection != null) connection.close();
			}
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}

}
