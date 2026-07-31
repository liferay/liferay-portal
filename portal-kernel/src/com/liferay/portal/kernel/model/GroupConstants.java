/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

/**
 * @author Brian Wing Shun Chan
 */
public class GroupConstants {

	public static final int ANY_PARENT_GROUP_ID = -1;

	public static final String APP = "App";

	public static final String CALENDAR = "Calendar";

	public static final String CALENDAR_FRIENDLY_URL = "/calendar";

	public static final String CMS = "CMS";

	public static final String CMS_FRIENDLY_URL = "/cms";

	public static final String CONTROL_PANEL = "Control Panel";

	public static final String CONTROL_PANEL_FRIENDLY_URL = "/control_panel";

	public static final String DEFAULT = "default";

	public static final long DEFAULT_LIVE_GROUP_ID = 0;

	public static final int DEFAULT_MEMBERSHIP_RESTRICTION = 0;

	public static final long DEFAULT_PARENT_GROUP_ID = 0;

	public static final String DSR = "DSR";

	public static final String FORMS = "Forms";

	public static final String FORMS_FRIENDLY_URL = "/forms";

	public static final String GLOBAL = "Global";

	public static final String GLOBAL_FRIENDLY_URL = "/global";

	public static final long GROUP_ID_ALL = -1L;

	public static final String GUEST = "Guest";

	public static final String GUEST_FRIENDLY_URL = "/guest";

	public static final int MEMBERSHIP_RESTRICTION_TO_PARENT_SITE_MEMBERS = 1;

	public static final String[] SYSTEM_GROUPS = {
		CALENDAR, CMS, CONTROL_PANEL, FORMS, GUEST,
		GroupConstants.USER_PERSONAL_SITE
	};

	public static final int TYPE_DEPOT = 5;

	public static final String TYPE_SETTINGS_KEY_INHERIT_LOCALES =
		"inheritLocales";

	public static final String TYPE_SETTINGS_KEY_LIFERAY_ANALYTICS_KEY =
		"liferayAnalyticsKey";

	public static final String TYPE_SETTINGS_KEY_MAINTENANCE_MODE =
		"maintenanceMode";

	public static final int TYPE_SITE_OPEN = 1;

	public static final String TYPE_SITE_OPEN_LABEL = "open";

	public static final int TYPE_SITE_PRIVATE = 3;

	public static final String TYPE_SITE_PRIVATE_LABEL = "private";

	public static final int TYPE_SITE_RESTRICTED = 2;

	public static final String TYPE_SITE_RESTRICTED_LABEL = "restricted";

	public static final String USER_PERSONAL_SITE = "User Personal Site";

	public static final String USER_PERSONAL_SITE_FRIENDLY_URL =
		"/personal_site";

	public static String getTypeLabel(int type) {
		if ((type == TYPE_DEPOT) || (type == TYPE_SITE_OPEN)) {
			return TYPE_SITE_OPEN_LABEL;
		}
		else if (type == TYPE_SITE_PRIVATE) {
			return TYPE_SITE_PRIVATE_LABEL;
		}

		return TYPE_SITE_RESTRICTED_LABEL;
	}

}