package gb.manage.service;

import gb.common.service.Ssh2Service;
import gb.manage.dao.ManageDAO;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service("manageService")
public class ManageServiceImpl implements ManageService {
	
	@Resource(name="manageDAO")
    private ManageDAO manageDAO;
	
	@Resource(name = "ssh2Service")
	private Ssh2Service ssh2Service;

	@Resource(name = "manageService")
	private ManageService manageService;
    
	@Override
	public Map<String, Object> findUsr(Map<String, Object> map) throws Exception {
		return manageDAO.findUsr(map);
	}
	
	@Override
	public List<Map<String, Object>> findUsr2(Map<String, Object> map) throws Exception {
		return manageDAO.findUsr2(map);
	}
	
	@Override
	public Map<String, Object> findUsrMap(Map<String, Object> map) throws Exception {
		return manageDAO.findUsrMap(map);
	}
	
	@Override
	public boolean insertUsr(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return manageDAO.insertUsr(map);
	}
	
	@Override
	public boolean updateUsr(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return manageDAO.updateUsr(map);
	}

	@Override
	public boolean deleteUsr(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return manageDAO.deleteUsr(map);
	}

	
	@Override
	public Map<String, Object> findDftBakSet(Map<String, Object> map) throws Exception {
		return manageDAO.findDftBakSet(map);
	}

	@Override
	public boolean insertDftBakSet(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return manageDAO.insertDftBakSet(map);
	}

	@Override
	public boolean deleteDftBakSet(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return manageDAO.deleteDftBakSet(map);
	}
	
	@Override
	public Map<String, Object> findDftBakMtd(Map<String, Object> map) throws Exception {
		return manageDAO.findDftBakMtd(map);
	}
	
	@Override
	public boolean insertDftBakMtd(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return manageDAO.insertDftBakMtd(map);
	}
	
	@Override
	public boolean deleteDftBakMtd(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return manageDAO.deleteDftBakMtd(map);
	}
	
	@Override
	public Map<String, Object> findSvr(Map<String, Object> map) throws Exception {
		return manageDAO.findSvr(map);
	}
	
	@Override
	public boolean insertSvr(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return manageDAO.insertSvr(map);
	}
	
	@Override
	public boolean updateSvr(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return manageDAO.updateSvr(map);
	}

	@Override
	public boolean deleteSvr(Map<String, Object> map, HttpServletRequest request) throws Exception {
		return manageDAO.deleteSvr(map);
	}
	
	@Override
	public List<Map<String, Object>> findSvrList(Map<String, Object> map) throws Exception {
		return manageDAO.findSvrList(map);
	}

	@Override
	public Map<String, Object> findSvrMap(Map<String, Object> map) throws Exception {
		return manageDAO.findSvrMap(map);
	}
	
	@Override
	public List<Map<String, Object>> listSvrAll() throws Exception {
		return manageDAO.listSvrAll();
	}
	
	
}
