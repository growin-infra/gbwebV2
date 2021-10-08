package gb.schedule.service;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
	
	//List<Map<String, Object>> cronSelect(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> cronSelect() throws Exception;
	
	boolean cronDelete(Map<String, Object> map) throws Exception;
	
	String cronJob(Map<String, Object> list) throws Exception;
	
//	Map<String, Object> cronSelect(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> listDelSchTarget() throws Exception;
	
	String listSchDtTarget(Map<String,Object> map) throws Exception;
	
}
