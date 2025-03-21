/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mentions.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;

import jakarta.portlet.PortletPreferences;

/**
 * @author Sergio González
 */
public class MentionsUtil {

	public static boolean isMentionsEnabled(long siteGroupId)
		throws PortalException {

		Group group = GroupLocalServiceUtil.getGroup(siteGroupId);

		PortletPreferences companyPortletPreferences =
			PrefsPropsUtil.getPreferences(group.getCompanyId());

		boolean companyMentionsEnabled = GetterUtil.getBoolean(
			companyPortletPreferences.getValue("mentionsEnabled", null), true);

		if (!companyMentionsEnabled) {
			return false;
		}

		return GetterUtil.getBoolean(
			group.getLiveParentTypeSettingsProperty("mentionsEnabled"), true);
	}

}