/**
 * Copyright 2011 Edia (www.edia.nl)
 * 
 * Licensed under the Educational Community License version 1.0
 * 
 * This Original Work, including software, source code, documents,
 * or other related items, is being provided by the copyright holder(s)
 * subject to the terms of the Educational Community License. By
 * obtaining, using and/or copying this Original Work, you agree that you
 * have read, understand, and will comply with the following terms and
 * conditions of the Educational Community License:
 * 
 * Permission to use, copy, modify, merge, publish, distribute, and
 * sublicense this Original Work and its documentation, with or without
 * modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the
 * following on ALL copies of the Original Work or portions thereof,
 * including modifications or derivatives, that you make:
 * 
 * - The full text of the Educational Community License in a location viewable to
 * users of the redistributed or derivative work.
 * 
 * - Any pre-existing intellectual property disclaimers, notices, or terms and
 * conditions.
 * 
 * - Notice of any changes or modifications to the Original Work, including the
 * date the changes were made.
 * 
 * 
 * Any modifications of the Original Work must be distributed in such a manner as
 * to avoid any confusion with the Original Work of the copyright holders.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * The name and trademarks of copyright holder(s) may NOT be used
 * in advertising or publicity pertaining to the Original or Derivative
 * Works without specific, written prior permission. Title to copyright in
 * the Original Work and any associated documentation will at all times
 * remain with the copyright holders.  
 */
package nl.edia.sakai.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.GroupProvider;

/**
 * The ChainedGroupProvider is a GroupProvider facade
 * that can be configured with a chain of actual GroupProviders. 
 * 
 * @author Roland Groen 
 * @author Maarten van Hoof
 *
 */
public class ChainedGroupProvider implements GroupProvider {

	private static final Log log = LogFactory.getLog(ChainedGroupProvider.class);
	List<GroupProvider> chain = new ArrayList<GroupProvider>();
	GroupProvider defaultGroupProvider;
	boolean firstWins = true;

	public Map<String, String> getGroupRolesForUser(String userEid) {
		Map<String, String> rv = new HashMap<String, String>();
		for (GroupProvider provider : chain) {
			Map<String, String> userRolesForGroup = getGroupRolesForUser(userEid, provider);
			if (userRolesForGroup != null) {
				rv.putAll(userRolesForGroup);
			}
		}
		return rv;
	}
	
	@SuppressWarnings("unchecked")
	protected Map<String, String> getGroupRolesForUser(String userEid, GroupProvider provider) {
		try {
			return provider.getGroupRolesForUser(userEid);
		} catch (Throwable t) {
			log.warn("Provider " + provider + " threw an error on getGroupRolesForUser("+userEid+"): " + t.getMessage());
			return Collections.emptyMap();
		}
	}

	public String getRole(String arg0, String arg1) {
		log.error("\n------------------------------------------------------------------\n");
		log.error("THIS METHOD IS NEVER CALLED IN SAKAI.  WHAT HAPPENED???");
		log.error("\n------------------------------------------------------------------\n");
		return null;
	}

	public Map<String, String> getUserRolesForGroup(String id) {
		Map<String, String> rv = new HashMap<String, String>();
		for (GroupProvider provider : chain) {
			Map<String, String> userRolesForGroup = getUserRolesForGroup(id, provider);
			if (userRolesForGroup != null) {
				rv.putAll(userRolesForGroup);
			}
		}
		return rv;
	}

	@SuppressWarnings("unchecked")
	protected Map<String, String> getUserRolesForGroup(String id, GroupProvider provider) {
		try {
			return provider.getUserRolesForGroup(id);
		} catch (Throwable t) {
			log.warn("Provider " + provider + " threw an error on : getUserRolesForGroup("+id+")" + t.getMessage());
			return Collections.emptyMap();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String packId(String[] ids) {
		return defaultGroupProvider.packId(ids);
	}

	/**
	 * The last one in the chain that sends back date "wins"
	 */
	public String preferredRole(String role1, String role2) {
		String rv = "";
		for (GroupProvider provider : chain) {
			String found = provider.preferredRole(role1, role2);
			if (StringUtils.isNotBlank(found)) {
				rv = found;
				if (firstWins) {
					break;
				}
			}
		}
		return rv;
	}

	public void setChain(List<GroupProvider> chain) {
		this.chain = chain;
	}

	public void setDefaultGroupProvider(GroupProvider defaultGroupProvider) {
		this.defaultGroupProvider = defaultGroupProvider;
	}

	public void setFirstWins(boolean firstWins) {
		this.firstWins = firstWins;
	}

	public String[] unpackId(String id) {
		return defaultGroupProvider.unpackId(id);
	}

}
