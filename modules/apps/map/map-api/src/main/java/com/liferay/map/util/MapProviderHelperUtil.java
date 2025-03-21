/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.util;

import com.liferay.map.constants.MapProviderWebKeys;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;

import jakarta.portlet.PortletPreferences;

/**
 * @author Jürgen Kappler
 */
public class MapProviderHelperUtil {

	public static String getMapProviderKey(
		GroupLocalService groupLocalService, long companyId, long groupId) {

		String companyMapProviderKey = getMapProviderKey(companyId);

		Group group = groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return companyMapProviderKey;
		}

		if (group.isStagingGroup()) {
			group = group.getLiveGroup();
		}

		return GetterUtil.getString(
			group.getTypeSettingsProperty(MapProviderWebKeys.MAP_PROVIDER_KEY),
			companyMapProviderKey);
	}

	public static String getMapProviderKey(long companyId) {
		PortletPreferences companyPortletPreferences =
			PrefsPropsUtil.getPreferences(companyId);

		return companyPortletPreferences.getValue(
			MapProviderWebKeys.MAP_PROVIDER_KEY, null);
	}

}