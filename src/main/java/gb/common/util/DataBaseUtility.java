package gb.common.util;

import org.apache.commons.dbcp2.BasicDataSource;

public class DataBaseUtility {

	private static BasicDataSource dataSource;
	
	public static BasicDataSource getDataSource(String dc, String url, String id, String pw, String file) {
		if (dataSource == null) {
			BasicDataSource ds = new BasicDataSource();
			ds.setDriverClassName(dc);
			ds.setUrl(url);
			ds.setUsername(id);
			ds.setPassword(pw);
			ds.setMaxIdle(50);
			ds.setMinIdle(25);
			ds.setValidationQuery("SELECT 1");
			ds.setValidationQueryTimeout(5000);
			dataSource = ds;
		}
		
		return dataSource;
	}
	
	public static BasicDataSource getDataSource(String dc, String url, String id, String pw) {
		
		if (dataSource == null) {
			BasicDataSource ds = new BasicDataSource();
			ds.setDriverClassName(dc);
			ds.setUrl(url);
			ds.setUsername(id);
			ds.setPassword(pw);
			ds.setMaxWaitMillis(5000);
			ds.setMaxIdle(10);
			ds.setMinIdle(5);
			ds.setValidationQuery("SELECT 1");
			ds.setValidationQueryTimeout(5000);
			
			dataSource = ds;
		}
		
		return dataSource;
	}

}
