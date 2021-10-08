package gb.common.interceptor;

import gb.common.util.LoginManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class Interceptor extends HandlerInterceptorAdapter {
	
	protected Log log = LogFactory.getLog(Interceptor.class);
	
	private LoginManager loginManager = LoginManager.getInstance();
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		String id = loginManager.getUserID(request.getSession());
		
		String url = request.getRequestURI();
 		if (url.indexOf("/webdoc") != -1) {
 			return true;
 		}
 		if (url.indexOf("/start_job") != -1) {
 			log.debug("======================================          START         ======================================");
 			return true;
 		}
 		if (url.indexOf("/main") != -1) {
 			log.debug("======================================          START         ======================================");
 			return true;
 		}
 		if (url.indexOf("/login_lcn_chk") != -1) {
 			log.debug("======================================          START         ======================================");
 			return true;
 		}
		
 		if (url.indexOf("/login") != -1) {
 			log.debug("======================================          START         ======================================");
 			return true;
 		}
		boolean isAjaxReq = this.isAjaxReq(request);
		if (id == null) {
			if (isAjaxReq) {
				response.sendError(400);
				return false;
			} else {
				response.sendRedirect("/main");
				return false;
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug("======================================          START         ======================================");
			log.debug(" url \t:  "+url);
		}
		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("======================================           END          ======================================\n");
		}
	}
	
	public boolean isAjaxReq(HttpServletRequest request) {
		boolean check = false;
		if ("XMLHttpRequest".equals(request.getHeader("x-requested-with"))) {
			check = true;
		}
		return check;
	}
	
	
	
}