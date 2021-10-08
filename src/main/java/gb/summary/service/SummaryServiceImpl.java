package gb.summary.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import gb.summary.dao.SummaryDAO;

@Service("summaryService")
public class SummaryServiceImpl implements SummaryService {

	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name="summaryDAO")
    private SummaryDAO summaryDAO;
	
	public Map<String, Object> menu_id(Map<String, Object> map) throws Exception {
		return summaryDAO.menu_id(map);
	}

	@Override
//	public void updateMenuCd(Map<String, Object> map, HttpServletRequest request) throws Exception{
//		summaryDAO.updateMenuCd(map);
//	}
	public boolean updateMenuCd(Map<String, Object> map) throws Exception{
		return summaryDAO.updateMenuCd(map);
	}
	
	@Override
	public Map<String, Object> findSttTC(Map<String, Object> map) throws Exception {
	    return summaryDAO.findSttTC(map);
	}

	@Override
//	public List<Map<String, Object>> findStt(Map<String, Object> map) throws Exception {
//		return summaryDAO.findStt(map);
//	}
	public Map<String, Object> findStt(Map<String, Object> map) throws Exception {
		return summaryDAO.findStt(map);
	}
	
	public List<Map<String, Object>> findSttAll() throws Exception {
		return summaryDAO.findSttAll();
	}
	
	public Map<String, Object> findScd(Map<String, Object> map) throws Exception {
		return summaryDAO.findScd(map);
	}
	
	public List<Map<String, Object>> findScdAll() throws Exception {
		return summaryDAO.findScdAll();
	}
	
	@Override
	public Map<String, Object> findUsrLstInfo(String str) throws Exception {
		return summaryDAO.findUsrLstInfo(str);
	}
	
	public boolean updateStt(Map<String, Object> map) throws Exception{
		return summaryDAO.updateStt(map);
	}
	
	public boolean insertSmyStt(Map<String, Object> map) throws Exception{
		return summaryDAO.insertSmyStt(map);
	}
}
