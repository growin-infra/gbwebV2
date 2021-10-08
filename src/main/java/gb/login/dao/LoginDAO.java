package gb.login.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import gb.common.dao.AbstractDAO;

@Repository("loginDAO")
public class LoginDAO extends AbstractDAO {
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> login(Map<String, Object> map) throws Exception{
		return (List<Map<String, Object>>)selectList("login.login", map);
	}
	
}
