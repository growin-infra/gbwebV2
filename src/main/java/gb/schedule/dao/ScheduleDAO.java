package gb.schedule.dao;

import gb.common.dao.AbstractDAO;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository("scheduleDAO")
public class ScheduleDAO extends AbstractDAO {
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> cronSelect() throws Exception{
		return (List<Map<String, Object>>)selectList("schedule.cronSelect");
	}
	
	public boolean cronDelete(Map<String, Object> map) throws Exception {
		return deleteB("schedule.cronDelete", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> listDelSchTarget() throws Exception{
		return (List<Map<String, Object>>)selectList("schedule.listDelSchTarget");
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> listSchDtTarget(Map<String, Object> map) throws Exception{
		return (Map<String, Object>)selectOne("schedule.listSchDtTarget", map);
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> listSchPidTarget(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("schedule.listSchPidTarget", map);
	}
	
	public boolean deleteIncreRun(Map<String, Object> map) throws Exception {
		return deleteB("schedule.deleteIncreRun", map);
	}
	
	public boolean deleteIDRun(Map<String, Object> map) throws Exception {
		return deleteB("schedule.deleteIDRun", map);
	}
	
}
