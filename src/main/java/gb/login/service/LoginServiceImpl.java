package gb.login.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import gb.login.dao.LoginDAO;

@Service("loginService")
public class LoginServiceImpl implements LoginService {

	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name="loginDAO")
    private LoginDAO loginDAO;

	@Override
	public List<Map<String, Object>> login(Map<String, Object> map) throws Exception {
		return loginDAO.login(map);
	}

}
