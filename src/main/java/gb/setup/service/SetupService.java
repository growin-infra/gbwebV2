package gb.setup.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface SetupService {

	List<Map<String, Object>> findTree(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findMngSvr(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> findMngSvrInfo(Map<String, Object> map) throws Exception;

	Map<String, Object> findTgtSvr(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findTgtSvrMaxId(Map<String, Object> map) throws Exception;
	
	boolean updateMngSvr(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	boolean updateTgtSvr(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	String deleteMngSvr(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	String deleteTgtSvr(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	List<Map<String, Object>> findIsLog(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> findLogMM(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findLogDetail(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findMngSvrNam(Map<String, Object> map) throws Exception;

	Map<String, Object> findTgtSvrNam(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findBakSet(Map<String, Object> map) throws Exception;

	List<Map<String, Object>> findType(Map<String, Object> map) throws Exception;
	
	boolean insertBakSet(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	Map<String, Object> findBakMtd(Map<String, Object> map) throws Exception;
	
	boolean insertBakMtd(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	Map<String, Object> findBakScd(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> findBakScdNam(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> findBakScdNamList(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> findMngbtsId(Map<String, Object> map) throws Exception;
	
	boolean insertBakScd(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	boolean updateBakScd(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	boolean deleteBakScd(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	Map<String, Object> findLogLst(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findRsrSet(Map<String, Object> map) throws Exception;
	
	boolean insertRsrSet(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	boolean insertBakRun(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findTypeMap(Map<String, Object> map) throws Exception;
	
//	Map<String, Object> bak_run_job(Map<String, Object> map) throws Exception;

	boolean insertLog(Map<String, Object> map, HttpServletRequest request) throws Exception;
	
	Map<String, Object> findLogDetailMap(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> findLogList(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> findBakRun(Map<String, Object> map) throws Exception;
	
//	Map<String, Object> findBakRunMap(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findBakRunLst(Map<String, Object> map) throws Exception;
	
	List<Map<String, Object>> findBakScdList() throws Exception;
	
//	Map<String, Object> findTgtSvr(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findLstFullbak(Map<String, Object> map) throws Exception;
	
	boolean deleteRun(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findMngSvrNamSea(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findTgtSvrNamSea(Map<String, Object> map) throws Exception;
	
	Map<String, Object> findLogDt(Map<String, Object> map) throws Exception;

	
}
