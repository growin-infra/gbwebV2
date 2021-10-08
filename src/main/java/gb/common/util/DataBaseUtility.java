package gb.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
