package gb.manage.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import gb.common.dao.AbstractDAO;

@Repository("manageDAO")
public class ManageDAO extends AbstractDAO {
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findUsr(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectPagingList("manage.findUsr", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findUsrMap(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("manage.findUsrMap", map);
	}
	
	public boolean insertUsr(Map<String, Object> map) throws Exception {
		return insertB("manage.insertUsr", map);
	}
	
	public boolean updateUsr(Map<String, Object> map) throws Exception {
		return insertB("manage.updateUsr", map);
	}
	
	public boolean deleteUsr(Map<String, Object> map) throws Exception {
		return insertB("manage.deleteUsr", map);
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findDftBakSet(Map<String, Object> map) throws Exception{
	    return (Map<String, Object>)selectOne("manage.findDftBakSet", map);
	}
	
	public boolean insertDftBakSet(Map<String, Object> map) throws Exception {
		return updateB("manage.insertDftBakSet", map);
	}
	
	public boolean deleteDftBakSet(Map<String, Object> map) throws Exception {
		return deleteB("manage.deleteDftBakSet", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findDftBakMtd(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("manage.findDftBakMtd", map);
	}
	
	public boolean insertDftBakMtd(Map<String, Object> map) throws Exception {
		return updateB("manage.insertDftBakMtd", map);
	}
	
	public boolean deleteDftBakMtd(Map<String, Object> map) throws Exception {
		return deleteB("manage.deleteDftBakMtd", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findSvr(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectPagingList("manage.findSvr", map);
	}
	public boolean insertSvr(Map<String, Object> map) throws Exception {
		return insertB("manage.insertSvr", map);
	}
	
	public boolean updateSvr(Map<String, Object> map) throws Exception {
		return insertB("manage.updateSvr", map);
	}
	
	public boolean deleteSvr(Map<String, Object> map) throws Exception {
		return insertB("manage.deleteSvr", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findSvrList(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("manage.findSvrMap", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findSvrMap(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("manage.findSvrMap", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> listSvrAll() throws Exception{
		return (List<Map<String, Object>>)selectList("manage.listSvrAll");
	}
	
	
}
