package gb.common.session;

import gb.summary.service.SummaryService;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class SessionManagerService {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Resource(name = "summaryService")
	private SummaryService summaryService;
	
	public SessionManagerService(HttpSession session) {
		
		String usr_id = (String) session.getAttribute("id");
		if (usr_id != null && !"".equals(usr_id)) {
			String lst_pag = (String) session.getAttribute("lst_pag");
			if (lst_pag != null && !"".equals(lst_pag)) {
				try {
					Map<String,Object> param = new HashMap<String, Object>();
					param.put("lst_pag", lst_pag);
					param.put("usr_id", usr_id);
					boolean result = summaryService.updateMenuCd(param);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("SessionManagerService Exception : ", e.fillInStackTrace());
				}
			}
		}
	}
	
}
