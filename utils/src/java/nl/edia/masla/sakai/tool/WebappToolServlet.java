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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import nl.edia.sakai.tool.util.SakaiUtils;
import nl.edia.spring.web.interceptor.PageVisitInterceptor;

import org.sakaiproject.tool.api.ActiveTool;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolException;
import org.sakaiproject.tool.cover.ActiveToolManager;
import org.sakaiproject.util.Web;

public class WebappToolServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3707711960037110847L;
	protected static final String HELPER_ID = "sakai.tool.helper.id";
	/** The special panel name for the main. */
	protected final String MAIN_PANEL = "Main";
	/** The parameter for paneld. */
	public final static String PARAM_PANEL = "panel";

	public WebappToolServlet() {
	}

	protected void service(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String contextPath = request.getContextPath();
		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);
		if (request.getAttribute("oiginalRequest") == null) {
			request.setAttribute("oiginalRequest", request);
			request.setAttribute("oiginalRequestUrl", request.getRequestURL());
		}
		HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {

			public String getContextPath() {
				return contextPath;
			}

		};
		String myFirstPage = (String) SakaiUtils.popToolSessionAttribute(PageVisitInterceptor.SESSION_ATTRIBUTE_NAME);
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

		if (doHelper(response, origrequest, pathInfo)) {
			return;
		}

		RequestDispatcher dispatcher;
		if (pathInfo == null)
			dispatcher = request.getRequestDispatcher("");
		else
			dispatcher = request.getRequestDispatcher(pathInfo);
		dispatcher.forward(request, response);
	}

	private boolean doHelper(HttpServletResponse response, HttpServletRequest request, String pathInfo) throws ToolException {
		Pattern myPattern = Pattern.compile(".*/([^/]+)\\.helper(?:/.*)?");
		Matcher myMatcher = myPattern.matcher(pathInfo);
		if (myMatcher.matches()) {
			String myHelperId = myMatcher.group(1);
			ActiveTool helperTool = ActiveToolManager.getActiveTool(myHelperId);

			// String panel = request.getParameter(PARAM_PANEL);
			// if (panel == null || panel.equals("") || panel.equals("null"))
			// panel = MAIN_PANEL;
			// String helperId = HELPER_ID + panel;
			// ToolSession toolSession = SessionManager.getCurrentToolSession();
			// toolSession.setAttribute(helperId, helperTool.getId());

			String[] parts = pathInfo.split("/");
			// /portal/tool/e1477edc-a133-4dbb-0073-68eee38670f1/sakai.filepicker.helper
			String context = "/portal/tool/" + SakaiUtils.getCurrentPlacementId() + "/" + helperTool.getId() + ".helper";
			String toolPath = Web.makePath(parts, 2, parts.length);
			request.setAttribute("sakai.filtered", "sakai.filtered");
			request.removeAttribute(Tool.NATIVE_URL);
			helperTool.help(request, response, context, toolPath);
			request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);
			return true;
		}
		return false;
	}

	public static final String FIRST_PAGE = "first-page";
}
