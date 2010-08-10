/*
 * $Author$
 * $Revision$
 * $Date$
 * 
 * Edia Project edia-sakai-utils
 * Copyright (C) 2007 Roland, Edia Educatie Technologie
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package nl.edia.masla.sakai.tool;

/**
 * Edia educatie technologie
 */

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import nl.edia.sakai.tool.util.SakaiUtils;
import nl.edia.spring.web.interceptor.PageVisitInterceptor;

import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;

public class WebappToolHelperServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3707711960037110847L;
	protected static final String HELPER_ID = "sakai.tool.helper.id";
	/** The special panel name for the main. */
	protected final String MAIN_PANEL = "Main";
	/** The parameter for paneld. */
	public final static String PARAM_PANEL = "panel";
	public WebappToolHelperServlet() {
	}

	protected void service(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String contextPath = request.getContextPath();
		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);
		HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {

			public String getContextPath() {
				return contextPath;
			}

		};
		String myFirstPage = (String)SakaiUtils.popToolSessionAttribute(PageVisitInterceptor.SESSION_ATTRIBUTE_NAME);
		if (myFirstPage == null) {
			myFirstPage = getInitParameter("first-page");
		}
		String myPathInfo = request.getPathInfo();
		if (myPathInfo == null && myFirstPage != null && !myFirstPage.equals("/")) {
			if (!myFirstPage.startsWith("/")) {
				myFirstPage = "/" + myFirstPage;
			}
			response.sendRedirect(contextPath + myFirstPage);
		} else if (myPathInfo != null && (myPathInfo.startsWith("/WEB-INF/") || myPathInfo.equals("/WEB-INF"))) {
			response.sendRedirect(contextPath + "/");
		} else {
			doNormalService(response, wrappedRequest, request, myPathInfo);
		}
		request.removeAttribute(Tool.NATIVE_URL);

	}

	protected void doNormalService(HttpServletResponse response, HttpServletRequest request, HttpServletRequest origrequest, String pathInfo) throws ServletException, IOException {

		if (doHelperReturn(response)) {
			return;
		}
		
		RequestDispatcher dispatcher;
		if (pathInfo == null)
			dispatcher = request.getRequestDispatcher("");
		else
			dispatcher = request.getRequestDispatcher(pathInfo);
		dispatcher.forward(request, response);
	}

	private boolean doHelperReturn(HttpServletResponse response) {
		ToolSession toolSession = SessionManager.getCurrentToolSession();
		Tool tool = ToolManager.getCurrentTool();
		Object done = toolSession.getAttribute(tool.getId() + "-done");
		if (done instanceof Boolean && ((Boolean)done).booleanValue()) {
			toolSession.removeAttribute(tool.getId() + "-done");
		
			String url = (String) toolSession.getAttribute(tool.getId() + Tool.HELPER_DONE_URL);
		
			toolSession.removeAttribute(tool.getId() + Tool.HELPER_DONE_URL);
		
			try
			{
				response.sendRedirect(url);  // TODO
			}
			catch (IOException e)
			{
				// Log.warn("chef", this + " : ", e);
			}
			return true;
		}
	    return false;
    }


	public static final String FIRST_PAGE = "first-page";
}