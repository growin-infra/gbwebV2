package gb.common.property;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertyPrinter {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Value("#{config['jdbc.driverClass']}")
    private String jdbcDriverClass;
     
    @Value("#{config['jdbc.url']}")
    private String jdbcUrl;
     
    @Value("#{config['jdbc.username']}")
    private String jdbcUserName;
     
    @Value("#{config['jdbc.password']}")
    private String jdbcPassword;
     
    
    @Value("#{config['dbms.pool.enable']}")
    private boolean dbmsPollEnable;
     
    @Value("#{config['dbms.maxIdle']}")
    private int dbmsMaxIdle;
     
    @Value("#{config['dbms.minIdle']}")
    private int dbmsMinIdle;
     
    @Value("#{config['dbms.maxWait']}")
    private int dbmsMaxWait;
     
    @Value("#{config['dbms.driver']}")
    private String dbmsdriver;
     
    @Value("#{config['dbms.testWhileIdle']}")
    private boolean dbmsTestWhileIdle;
    
    @Value("#{config['dbms.timeBetweenEvictionRunsMillis']}")
    private int dbmsTimeBetweenEvictionRunsMillis;
    
    @Value("#{config['dbms.validationQuery']}")
    private String dbmsValidationQuery;
    
    @Value("#{config['dbms.removeAbandonedTimeout']}")
    private int dbmsRemoveAbandonedTimeout;
    
    @Value("#{config['project.home']}")
    private String projectHome;
    
    public String getProjectHome() {
		return projectHome;
	}
    
    
    @Autowired
    private Properties config;
    
    public String get(String key) {
        return config.getProperty(key);
    }

    
    public void print() {
        log.debug("jdbc.driverClass 		: " + get("jdbc.driverClass"));
        log.debug("jdbc.url 				: " + get("jdbc.url"));
        log.debug("jdbc.username 			: " + get("jdbc.username"));
        log.debug("jdbc.password 			: " + get("jdbc.password"));
        log.debug("jdbc.dbcp.maxActive 		: " + get("jdbc.dbcp.maxActive"));
        log.debug("jdbc.dbcp.minIdle 		: " + get("jdbc.dbcp.minIdle"));
        log.debug("jdbc.dbcp.maxIdle 		: " + get("jdbc.dbcp.maxIdle"));
        log.debug("jdbc.dbcp.initialSize 	: " + get("jdbc.dbcp.initialSize"));
        log.debug("jdbc.prop1 				: " + get("jdbc.prop1"));
        log.debug("jdbc.prop2 				: " + get("jdbc.prop2"));
         
    }


	

}
