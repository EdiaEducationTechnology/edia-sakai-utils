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

package nl.edia.masla.sakai.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import nl.edia.sakai.tool.util.SakaiUtils;

/**
 * Only displays the body if the body is available.
 * 
 * @author Roland
 * 
 */
public class PermissionCheckTag extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6748093916400478052L;

	String permission;

	public PermissionCheckTag() {
	}

	@Override
	public int doStartTag() throws JspException {
		if (SakaiUtils.hasPermission(permission)) {
			return EVAL_BODY_INCLUDE;
		}
		return SKIP_BODY;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

}
