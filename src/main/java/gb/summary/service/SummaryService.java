package gb.summary.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface SummaryService {
	
	Map<String, Object> menu_id(Map<String, Object> map) throws Exception;
	
//	void updateMenuCd(Map<String, Object> map, HttpServletRequest request) throws Exception;
	boolean updateMenuCd(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findSttTC(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findStt(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> findSttAll() throws Exception;
	
	Map<String, Object> findScd(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> findScdAll() throws Exception;
	
	Map<String, Object> findUsrLstInfo(String str) throws Exception;
	
	boolean updateStt(Map<String, Object> map) throws Exception;

	boolean insertSmyStt(Map<String, Object> map) throws Exception;
}
