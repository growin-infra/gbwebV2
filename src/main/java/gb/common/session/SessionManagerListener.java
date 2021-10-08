package gb.common.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SessionManagerListener implements HttpSessionListener {
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		// nothing
		log.info("sessionCreated !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		log.info("sessionDestroyed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		// session
		HttpSession session = se.getSession();
		String usr_id = (String) se.getSession().getAttribute("id");
		String lst_pag = (String) se.getSession().getAttribute("lst_pag");
		if (usr_id != null && !"".equals(usr_id)) {
			new SessionManagerService(session);
		}
		// spring web application context
		ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(se.getSession().getServletContext());
		
		// security context
		SecurityContext sc = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		if (sc != null) {
			log.info("SecurityContext not null !!!!!!!!!!!!!!!!!!!!!!!");
		} else {
			log.info("SecurityContext null !!!!!!!!!!!!!!!!!!!!!!!");
		}

	}

}