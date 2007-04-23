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
import java.util.Properties;

import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.event.cover.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.Preferences;
import org.sakaiproject.user.cover.PreferencesService;

public class SakaiUtils {

	/**
	 * The type string for this "application": should not change over time as it
	 * may be stored in various parts of persistent entities.
	 */
	public static final String APPLICATION_ID = "sakai:resourceloader";

	/** Preferences key for user's regional language locale */
	public static final String LOCALE_KEY = "locale";


	/**
	 * <p>
	 * Gets the current active site.
	 * </p>
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
	 * @return the current site id, null if none found (highly unlikely)
	 */
	public static String getCurrentSiteId() {
		String currentSiteId = null;

		Placement thisPlacement = ToolManager.getCurrentPlacement();
		if (thisPlacement != null) {
			currentSiteId = thisPlacement.getContext();
		}
		return currentSiteId;
	}
	
	/**
	 * <p>
	 * The placement id is the unique identifier of the tool within a site.
	 * The same tool twice on the same site will result in a different
	 * placement id. 
	 * </p>
	 * <p>
	 * A placement id is global unique within a sakai instance.
	 * </p>
	 * @return placement id, null if none found (highly unlikely)
	 */
	public static String getCurrentPlacementId() {
		String placementId = null;
		Placement thisPlacement = ToolManager.getCurrentPlacement();
		if (placementId != null) {
			placementId = thisPlacement.getId();
		}
		return placementId;
	}
	
	/**
	 * <p>
	 * Gets the tool id, beware that this is not the placement
	 * id. The tool id is unique for a specific tool, but
	 * idential for all instances of the same tool over
	 * the different sites.
	 * </p>
	 * @see #getCurrentPlacementId()
	 * @return the tool id, null if none found (highly unlikely)
	 */
	public static String getCurrentToolId() {
		String currentTool = null;
		Placement thisPlacement = ToolManager.getCurrentPlacement();
		if (currentTool != null) {
			currentTool = thisPlacement.getToolId();
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
		Properties myConfig = myCurrentPlacement.getConfig();
		return myConfig.getProperty(key);
	}

	public static boolean hasPermission(String permission) {
		Site myCurrentSite = getCurrentSite();
		String myReference = myCurrentSite.getReference();
		Boolean myCanView = SecurityService.unlock(permission, myReference);
		return myCanView;
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
	 * Creates a new event record. Events are defined by 
	 * their string representaion of the action, usually
	 * this is the funtion, such as site.del. 
	 * The resource is the identifier of the object that is 
	 * being subjected. Usually the id.
	 * </p>
	 * @param action the representation of the action
	 * @param resource the identification of the resource
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
	 * Creates a new event record. Events are defined by 
	 * their string representaion of the action, usually
	 * this is the funtion, such as site.del. 
	 * The resource is the identifier of the object that is 
	 * being modified. Usually the id.
	 * </p>
	 * @param action the representation of the action
	 * @param resource the identification of the resource
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
	 * Return user's preferred locale 
	 * First: return locale from Sakai user preferences, if available 
	 * Second: return locale from user session, if available 
	 * Last: return system default locale
	 * 
	 * @return user's Locale object
	 */
	public static Locale getLocale() {
		Locale loc = null;

		// First: find locale from Sakai user preferences, if available
		try {
			String userId = SessionManager.getCurrentSessionUserId();
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
		} catch (Exception e) {
		} // ignore and continue

		// Second: find locale from user session, if available
		if (loc == null) {
			try {
				loc = (Locale) SessionManager.getCurrentSession().getAttribute(
						"locale");
			} catch (NullPointerException e) {
			} // ignore and continue
		}

		// Last: find system default locale
		if (loc == null) {
			// fallback to default.
			loc = Locale.getDefault();
		}

		return loc;
	}

}
