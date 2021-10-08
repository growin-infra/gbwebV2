package gb.manage.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface ManageService {
	
	Map<String, Object> findUsr(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findUsrMap(Map<String, Object> map) throws Exception;
	
	boolean insertUsr(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	boolean updateUsr(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	boolean deleteUsr(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	Map<String, Object> findDftBakSet(Map<String, Object> map) throws Exception;
	
	boolean insertDftBakSet(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	boolean deleteDftBakSet(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	Map<String, Object> findDftBakMtd(Map<String, Object> map) throws Exception;
	
	boolean insertDftBakMtd(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	boolean deleteDftBakMtd(Map<String, Object> map, HttpServletRequest request) throws Exception;

	Map<String, Object> findSvr(Map<String, Object> map) throws Exception;
	
	boolean insertSvr(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	boolean updateSvr(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	boolean deleteSvr(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	List<Map<String, Object>> findSvrList(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findSvrMap(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> listSvrAll() throws Exception;
	
	
}
