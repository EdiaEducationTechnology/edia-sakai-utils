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
package nl.edia.masla.spring.webflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.edia.sakai.tool.util.SakaiUtils;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Redirects the session to a session id, if any
 * 
 * @author Roland
 * 
 */
public class SessionContinuationController implements Controller {
	String view;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Object myExecutionKey = SakaiUtils.getToolSessionAttribute("_flowExecutionKey");
		if (myExecutionKey != null) {
			return new ModelAndView("redirect:" + view + "?_flowExecutionKey=" + myExecutionKey);
		}
		return new ModelAndView("redirect:" + view);
	}

	public String getView() {
		return view;
	}

	public void setView(String path) {
		this.view = path;
	}

}
