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
package nl.edia.sakai.tool.util;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.tool.api.ToolSession;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.PreferencesService;
import org.sakaiproject.user.cover.UserDirectoryService;

public class SakaiUtils {

	/**
	 * The type string for this "application": should not change over time as it
	 * may be stored in various parts of persistent entities.
	 */
	public static final String APPLICATION_ID = "sakai:resourceloader";

	/** Preferences key for user's regional language locale */
	public static final String LOCALE_KEY = "locale";

	/** The special panel name for the main. */
	protected static final String MAIN_PANEL = "Main";

	/** The parameter for paneld. */
	public final static String PARAM_PANEL = "panel";

	/** ToolSession attribute name holding the helper id, if we are in helper mode. NOTE: promote to Tool -ggolden */
	protected static final String HELPER_ID = "sakai.tool.helper.id";

	/**
	 * <p>
	 * Gets the current active site.
	 * </p>
	 * 
	 * @return the current site, null if none found (highly unlikely)
	 */
	public static Site getCurrentSite() {
		Site mySite = null;
		String mySiteId = getCurrentSiteId();
		if (mySiteId != null) {
			try {
				mySite = org.sakaiproject.site.cover.SiteService.getSite(mySiteId);
			} catch (IdUnusedException e) {
				// Ignore
			}
		}

		return mySite;
	}

	/**
	 * <p>
	 * Get the current site identifier. Each unique site has an unique
	 * identifier.
	 * </p>
	 * 
	 * @return the current site id, null if none found (highly unlikely)
	 */
	public static String getCurrentSiteId() {
		String currentSiteId = null;

		Placement myCurrentPlacement = ToolManager.getCurrentPlacement();
		if (myCurrentPlacement != null) {
			currentSiteId = myCurrentPlacement.getContext();
		}
		return currentSiteId;
	}

	/**
	 * <p>
	 * The placement id is the unique identifier of the tool within a site. The
	 * same tool twice on the same site will result in a different placement id.
	 * </p>
	 * <p>
	 * A placement id is global unique within a sakai instance.
	 * </p>
	 * 
	 * @return placement id, null if none found (highly unlikely)
	 */
	public static String getCurrentPlacementId() {
		String placementId = null;
		Placement myCurrentPlacement = ToolManager.getCurrentPlacement();
		if (myCurrentPlacement != null) {
			placementId = myCurrentPlacement.getId();
		}
		return placementId;
	}

	/**
	 * <p>
	 * Gets the tool id, beware that this is not the placement id. The tool id
	 * is unique for a specific tool, but idential for all instances of the same
	 * tool over the different sites.
	 * </p>
	 * 
	 * @see #getCurrentPlacementId()
	 * @return the tool id, null if none found (highly unlikely)
	 */
	public static String getCurrentToolId() {
		String currentTool = null;
		Placement myCurrentPlacement = ToolManager.getCurrentPlacement();
		if (myCurrentPlacement != null) {
			currentTool = myCurrentPlacement.getToolId();
		}
		return currentTool;
	}

	/**
	 * @deprecated
	 * @see #getCurrentSiteId()
	 */
	public static String getContextSiteId() {
		return getCurrentSiteId();
	}

	/**
	 * <p>
	 * Returns the current user id.
	 * </p>
	 * 
	 * @return
	 */
	public static String getCurrentUserId() {
		return SessionManager.getCurrentSessionUserId();
	}

	/**
	 * @deprecated
	 * @see #getCurrentUserId()
	 */
	public static String getCurrentUserName() {
		return getCurrentUserId();
	}

	public static String getConfigValue(String key) {
		Placement myCurrentPlacement = ToolManager.getCurrentPlacement();
		if (myCurrentPlacement != null) {
			Properties myConfig = myCurrentPlacement.getConfig();
			return myConfig.getProperty(key);
		}
		return null;

	}

	public static boolean hasPermission(String permission) {
		Site myCurrentSite = getCurrentSite();
		String myReference = myCurrentSite.getReference();
		User myUser = getCurrentUser();
		if (myUser != null) {
			Boolean myCanView = SecurityService.unlock(myUser, permission, myReference);
			return myCanView;
		}
		return false;
	}

	public static User getCurrentUser() {
		return UserDirectoryService.getCurrentUser();
	}

	/**
	 * Sets a tool session attribute, with the given name.
	 * 
	 * @param name
	 *            of the attribute
	 * @param value
	 *            of the attribute, not null
	 * @see Session#setAttribute(String, Object)
	 */
	public static void setToolSessionAttribute(String name, Object value) {
		ToolSession mySession = getToolSession();
		if (mySession != null) {
			mySession.setAttribute(name, value);
		}
	}

	/**
	 * Gets a tool session attribute, null if not any attribute is set with this
	 * name.
	 * 
	 * @param name
	 *            name of the attribute
	 * @return Object if one set, else null.
	 * @see Session#getAttribute(String)
	 */
	public static Object getToolSessionAttribute(String name) {
		ToolSession mySession = getToolSession();
		if (mySession != null) {
			return mySession.getAttribute(name);
		}
		return null;
	}

	/**
	 * Gets and removes session attribute
	 * @param name
	 * @return
	 */
	public static Object popToolSessionAttribute(String name) {
		ToolSession mySession = getToolSession();
		if (mySession != null) {
			Object myAttribute = mySession.getAttribute(name);
			if (myAttribute != null) {
				removeToolSessionAttribute(name);
			}
			return myAttribute;
		}
		return null;
	}

	public static ToolSession getToolSession() {
		return SessionManager.getCurrentToolSession();
	}

	/**
	 * Removes a tool session attribute with the given name
	 * 
	 * @param name
	 * @see Session#removeAttribute(String)
	 */
	public static void removeToolSessionAttribute(String name) {
		ToolSession mySession = getToolSession();
		if (mySession != null) {
			mySession.removeAttribute(name);
		}
	}

	public static void checkPermission(String permission) throws PermissionException {
		Site myCurrentSite = getCurrentSite();
		String myReference = myCurrentSite.getReference();
		Boolean myCanView = SecurityService.unlock(permission, myReference);
		if (!myCanView) {
			throw new PermissionException(getCurrentUserName(), permission, myReference);
		}
	}

	/**
	 * <p>
	 * Creates a new event record. Events are defined by their string
	 * representaion of the action, usually this is the funtion, such as
	 * site.del. The resource is the identifier of the object that is being
	 * subjected. Usually the id.
	 * </p>
	 * 
	 * @param action
	 *            the representation of the action
	 * @param resource
	 *            the identification of the resource
	 */
	public static void createEvent(String action, String resource) {
		org.sakaiproject.event.api.EventTrackingService myEventTrackingService = EventTrackingService.getInstance();
		Event myEvent = myEventTrackingService.newEvent(action, resource, false);
		myEventTrackingService.post(myEvent);
	}

	/**
	 * @deprecated
	 * @see #createEvent(String, String)
	 */
	public static void spawnEvent(String action, String resource) {
		createEvent(action, resource);
	}

	/**
	 * <p>
	 * Creates a new event record. Events are defined by their string
	 * representaion of the action, usually this is the funtion, such as
	 * site.del. The resource is the identifier of the object that is being
	 * modified. Usually the id.
	 * </p>
	 * 
	 * @param action
	 *            the representation of the action
	 * @param resource
	 *            the identification of the resource
	 */

	public static void createModificationEvent(String action, String resource) {
		org.sakaiproject.event.api.EventTrackingService myEventTrackingService = EventTrackingService.getInstance();
		Event myEvent = myEventTrackingService.newEvent(action, resource, true);
		myEventTrackingService.post(myEvent);
	}

	/**
	 * @deprecated
	 * @see #createModificationEvent(String, String)
	 */
	public static void spawnModificationEvent(String action, String resource) {
		createModificationEvent(action, resource);
	}

	/**
	 * Return user's preferred locale First: return locale from Sakai user
	 * preferences, if available Second: return locale from user session, if
	 * available Last: return system default locale
	 * 
	 * @return user's Locale object
	 */
	public static Locale getLocale() {
		Locale loc = null;
		// Oddly enough, the javadoc from org.sakaiproject.util.ResourceLoader says it loads
		// the user pref first, and than loads the locale from the session, but the code
		// works the other way around...
		
		// Second, ehh no, First: find locale from user session, if available
		if (loc == null) {
			try {
				loc = (Locale) SessionManager.getCurrentSession().getAttribute("locale");
			} catch (NullPointerException e) {
			} // ignore and continue
		}

		if (loc == null) {
			// First, no Second: find locale from Sakai user preferences, if available
			try {
				String userId = SessionManager.getCurrentSessionUserId();
				if (userId != null) {
					Preferences prefs = PreferencesService.getPreferences(userId);
					ResourceProperties locProps = prefs.getProperties(APPLICATION_ID);
	
					String localeString = locProps.getProperty(LOCALE_KEY);
					if (localeString != null) {
						String[] locValues = localeString.split("_");
						if (locValues.length > 1)
							loc = new Locale(locValues[0], locValues[1]); // language,
						// country
						else if (locValues.length == 1)
							loc = new Locale(locValues[0]); // just language
					}
				}
			} catch (Exception e) {
			} // ignore and continue
		}

		// Last: find system default locale
		if (loc == null) {
			// fallback to default.
			loc = Locale.getDefault();
		}

		return loc;
	}

	/**
	 * Return user's preferred TimeZone First: return TimeZone from Sakai user
	 * preferences, if available Second: return TimeZone from user session, if
	 * available Last: return system default TimeZone
	 * 
	 * @return user's TimeZone object
	 */
	public static TimeZone getTimeZone() {
		TimeZone myTimeZone = null;

		// First: find locale from Sakai user preferences, if available
		try {
			String userId = SessionManager.getCurrentSessionUserId();
			if (userId != null) {
				Preferences prefs = PreferencesService.getPreferences(userId);
				ResourceProperties tzProps = prefs.getProperties(TimeService.APPLICATION_ID);
				String timeZoneString  = tzProps.getProperty(TimeService.TIMEZONE_KEY);
				if (timeZoneString != null) {
					myTimeZone = TimeZone.getTimeZone(timeZoneString);
				}
			}
		} catch (Exception e) {
		} // ignore and continue

		// Second: find locale from user session, if available
		if (myTimeZone == null) {
			try {
				myTimeZone = (TimeZone) SessionManager.getCurrentSession().getAttribute(TimeService.TIMEZONE_KEY);
			} catch (NullPointerException e) {
			} // ignore and continue
		}

		// Last: find system default locale
		if (myTimeZone == null) {
			// fallback to default.
			myTimeZone = TimeZone.getDefault();
		}

		return myTimeZone;
	}

	public static void startHelper(HttpServletRequest req, String helperId) {
		startHelper(req, helperId, null, null);
	}

	public static void startHelper(HttpServletRequest req, String helperId, Map<String, String> extraAttributes) {
		startHelper(req, helperId, null, extraAttributes);
	}

	/**
	 * Setup for a helper tool - all subsequent requests will be directed there, till the tool is done.
	 * 
	 * @param helperId
	 *        The helper tool id.
	 */
	public static void startHelper(HttpServletRequest req, String helperId, String panel, Map<String, String> extraAttributes) {
		if (panel == null)
			panel = MAIN_PANEL;

		ToolSession toolSession = getToolSession();
		toolSession.setAttribute(HELPER_ID + panel, helperId);

		// the done URL - this url and the extra parameter to indicate done
		// also make sure the panel is indicated - assume that it needs to be main, assuming that helpers are taking over the entire tool response
		String doneUrl = req.getContextPath() + req.getServletPath() + (req.getPathInfo() == null ? "" : req.getPathInfo()) + "?" + HELPER_ID + panel + "=done" + "&" + PARAM_PANEL + "=" + panel;
		if (extraAttributes != null) {
			for (String myKey : extraAttributes.keySet()) {
				doneUrl += "&" + myKey + "=" + extraAttributes.get(myKey);
			}
		}
		toolSession.setAttribute(helperId + Tool.HELPER_DONE_URL, doneUrl);
	}

}
