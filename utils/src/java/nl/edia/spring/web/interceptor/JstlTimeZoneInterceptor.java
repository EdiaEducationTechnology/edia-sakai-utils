package nl.edia.spring.web.interceptor;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;

import nl.edia.sakai.tool.util.SakaiUtils;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class JstlTimeZoneInterceptor extends HandlerInterceptorAdapter {
	 private static final java.lang.String REQUEST_SCOPE_SUFFIX = ".request";
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		TimeZone myTimeZone = SakaiUtils.getTimeZone();
		// for JSTL implementations that stick to the config names (e.g. Resin's)
		request.setAttribute(Config.FMT_TIME_ZONE, myTimeZone);
		// for JSTL implementations that append the scope to the config names (e.g. Jakarta's)
		request.setAttribute(Config.FMT_TIME_ZONE + REQUEST_SCOPE_SUFFIX, myTimeZone);
	}
}
