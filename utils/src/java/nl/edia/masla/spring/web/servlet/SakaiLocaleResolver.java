package nl.edia.masla.spring.web.servlet;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.edia.sakai.tool.util.SakaiUtils;

import org.springframework.web.servlet.LocaleResolver;

public class SakaiLocaleResolver implements LocaleResolver {
	public Locale resolveLocale(HttpServletRequest request) {
		return SakaiUtils.getLocale();
	}

	public void setLocale(HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		// ignore
	}


}
