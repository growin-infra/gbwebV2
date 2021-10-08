package gb.login.service;

import java.util.List;
import java.util.Map;

public interface LoginService {
	
	List<Map<String, Object>> login(Map<String, Object> map) throws Exception;

}
