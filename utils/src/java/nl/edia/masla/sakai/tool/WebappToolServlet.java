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

public class WebappToolServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3707711960037110847L;

	public WebappToolServlet() {
	}

	protected void service(final HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		final String contextPath = request.getContextPath();
		request.setAttribute("sakai.request.native.url", "sakai.request.native.url");
		HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {

			public String getContextPath() {
				return contextPath;
			}

		};
		String myFirstPage = getInitParameter("first-page");
		if (request.getPathInfo() == null && myFirstPage != null
				&& !myFirstPage.equals("/")) {
			response.sendRedirect(contextPath + myFirstPage);
		} else if (request.getPathInfo() != null
				&& (request.getPathInfo().startsWith("/WEB-INF/") || request.getPathInfo().equals("/WEB-INF"))) {
			response.sendRedirect(contextPath + "/");
		} else {
			RequestDispatcher dispatcher;
			if (request.getPathInfo() == null)
				dispatcher = request.getRequestDispatcher("");
			else
				dispatcher = request.getRequestDispatcher(request.getPathInfo());
			dispatcher.forward(wrappedRequest, response);
		}
	}

	public static final String FIRST_PAGE = "first-page";
}
