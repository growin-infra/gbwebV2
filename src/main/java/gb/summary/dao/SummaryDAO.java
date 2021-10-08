package gb.summary.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import gb.common.dao.AbstractDAO;

@Repository("summaryDAO")
public class SummaryDAO extends AbstractDAO {

	@SuppressWarnings("unchecked")
	public Map<String, Object> menu_id(Map<String, Object> map) throws Exception {
		return (Map<String, Object>) selectOne("summary.menu_id", map);
	}
	
	public boolean updateMenuCd(Map<String, Object> map) throws Exception {
		return updateB("summary.updateMenuCd", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findSttTC(Map<String, Object> map) throws Exception{
	    return (Map<String, Object>)selectOne("summary.findSttTC", map);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findStt(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectPagingList("summary.findStt", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findSttAll() throws Exception{
		return (List<Map<String, Object>>)selectList("summary.findSttAll");
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findScd(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectPagingList("summary.findScd", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> findScdAll() throws Exception{
		return (List<Map<String, Object>>)selectList("summary.findScdAll");
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findUsrLstInfo(String str) throws Exception{
		return (Map<String, Object>)selectOne("summary.findUsrLstInfo", str);
	}
	
	public boolean updateStt(Map<String, Object> map) throws Exception {
		return updateB("summary.updateStt", map);
	}
	
	public boolean deleteStt(Map<String, Object> map) throws Exception {
		return deleteB("summary.deleteStt", map);
	}

	public boolean insertSmyStt(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		return insertB("summary.insertSmyStt", map);
	}
	
}
