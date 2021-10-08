package gb.setup.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import gb.common.dao.AbstractDAO;

@Repository("setupDAO")
public class SetupDAO extends AbstractDAO {

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findTree(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findTree", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findMngSvr(Map<String, Object> map) throws Exception{
	    return (Map<String, Object>)selectOne("setup.findMngSvr", map);
	}
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findMngSvrInfo(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findMngSvrInfo", map);
	}
	@SuppressWarnings("unchecked")
	public Map<String, Object> findTgtSvr(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findTgtSvr", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findTgtSvrList(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findTgtSvr", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findMngSvrMaxId(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findMngSvrMaxId", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findTgtSvrMaxId(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findTgtSvrMaxId", map);
	}
	
	public boolean insertTree(Map<String, Object> map) throws Exception {
		return insertB("setup.insertTree", map);
	}
	
	public boolean insertMngSvr(Map<String, Object> map) throws Exception {
		return insertB("setup.insertMngSvr", map);
	}
	
	public boolean insertTgtSvr(Map<String, Object> map) throws Exception {
		return insertB("setup.insertTgtSvr", map);
	}
	
	public boolean updateTree(Map<String, Object> map) throws Exception {
		return updateB("setup.updateTree", map);
	}
	
	public boolean updateMngSvr(Map<String, Object> map) throws Exception {
		return updateB("setup.updateMngSvr", map);
	}
	
	public boolean updateTgtSvr(Map<String, Object> map) throws Exception {
		return updateB("setup.updateTgtSvr", map);
	}
	
	public boolean deleteTree(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteTree", map);
	}
	
	public boolean deleteMngSvr(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteMngSvr", map);
	}
	
	public boolean deleteTgtSvr(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteTgtSvr", map);
	}
	
	public boolean deleteTreePid(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteTreePid", map);
	}
	
	public boolean deleteMngSvrPid(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteMngSvrPid", map);
	}
	
	public boolean deleteTgtSvrPid(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteTgtSvrPid", map);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findIsLog(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findIsLog", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findLogMM(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findLogMM", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findLogDetail(Map<String, Object> map) throws Exception{
	    return (Map<String, Object>)selectOne("setup.findLogDetail", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findMngSvrNam(Map<String, Object> map) throws Exception{
	    return (Map<String, Object>)selectOne("setup.findMngSvrNam", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findTgtSvrNam(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findTgtSvrNam", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findBakSet(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findBakSet", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findType(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findType", map);
	}
	
	public boolean insertBakSet(Map<String, Object> map) throws Exception {
		return updateB("setup.insertBakSet", map);
	}
	
	public boolean updateBakSetIP(Map<String, Object> map) throws Exception {
		return updateB("setup.updateBakSetIP", map);
	}
	
	public boolean updateTgtSvrIP(Map<String, Object> map) throws Exception {
		return updateB("setup.updateTgtSvrIP", map);
	}
	
	public boolean updateBakSetTgt(Map<String, Object> map) throws Exception {
		return updateB("setup.updateBakSetTgt", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findBakMtd(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findBakMtd", map);
	}
	
	public boolean insertBakMtd(Map<String, Object> map) throws Exception {
		return updateB("setup.insertBakMtd", map);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findBakScdNam(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findBakScdNam", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findBakScdNamList(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findBakScdNamList", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findMngbtsId(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findMngbtsId", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findBakScd(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findBakScd", map);
	}
	
	public boolean insertBakScd(Map<String, Object> map) throws Exception {
		return insertB("setup.insertBakScd", map);
	}
	public boolean updateBakScd(Map<String, Object> map) throws Exception {
		return updateB("setup.updateBakScd", map);
	}
	public boolean deleteBakScd(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteBakScd", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findLogLst(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findLogLst", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findRsrSet(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findRsrSet", map);
	}
	
	public boolean insertRsrSet(Map<String, Object> map) throws Exception {
		return updateB("setup.insertRsrSet", map);
	}
	
	public boolean insertBakRun(Map<String, Object> map) throws Exception {
		return updateB("setup.insertBakRun", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findRunFllMaxId(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findRunFllMaxId", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findRunIncMaxId1(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findRunIncMaxId1", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findRunIncMaxId2(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findRunIncMaxId2", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findTypeMap(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findType", map);
	}
	
	public boolean insertLog(Map<String, Object> map) throws Exception {
		return insertB("setup.insertLog", map);
	}
	
	public boolean insertLogDetail(Map<String, Object> map) throws Exception {
		return insertB("setup.insertLogDetail", map);
	}
	
	public boolean updateLog(Map<String, Object> map) throws Exception {
		return updateB("setup.updateLog", map);
	}
	
	public boolean updateBakRunSucYon(Map<String, Object> map) throws Exception {
		return updateB("setup.updateBakRunSucYon", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findLogDetailMap(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findLogDetailMap", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findLstLog(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findLstLog", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findLogList(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findLogList", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findBakRun(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findBakRun", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findBakRunMap(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findBakRun", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findBakRunLst(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findBakRunLst", map);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findBakScdList() throws Exception{
		return (List<Map<String, Object>>)selectList("setup.findBakScdList");
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findLstFullbak(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findLstFullbak", map);
	}
	
	public boolean deleteRun(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteRun", map);
	}
	
	public boolean deleteBakSet(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteBakSet", map);
	}
	
	public boolean deleteBakMtd(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteBakMtd", map);
	}
	
	public boolean deleteRsrSet(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteRsrSet", map);
	}
	
	public boolean deleteLogLv1(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteLogLv1", map);
	}
	
	public boolean deleteLogLv2(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteLogLv2", map);
	}
	
	public boolean deleteBakRunLv1(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteBakRunLv1", map);
	}
	
	public boolean deleteBakRunLv2(Map<String, Object> map) throws Exception {
		return deleteB("setup.deleteBakRunLv2", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findMngSvrNamSea(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findMngSvrNamSea", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findTgtSvrNamSea(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findTgtSvrNamSea", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findLogDt(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("setup.findLogDt", map);
	}
	
}
