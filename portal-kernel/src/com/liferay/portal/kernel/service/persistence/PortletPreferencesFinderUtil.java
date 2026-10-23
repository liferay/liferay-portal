/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PortletPreferencesFinderUtil {

	public static long countByO_O_P(
		long ownerId, int ownerType, String portletId,
		boolean excludeDefaultPreferences) {

		return getFinder().countByO_O_P(
			ownerId, ownerType, portletId, excludeDefaultPreferences);
	}

	public static long countByO_O_P_P_P(
		long ownerId, int ownerType, long plid, String portletId,
		boolean excludeDefaultPreferences) {

		return getFinder().countByO_O_P_P_P(
			ownerId, ownerType, plid, portletId, excludeDefaultPreferences);
	}

	public static java.util.List
		<com.liferay.portal.kernel.model.PortletPreferences> findByPortletId(
			String portletId) {

		return getFinder().findByPortletId(portletId);
	}

	public static java.util.List
		<com.liferay.portal.kernel.model.PortletPreferences> findByC_G_O_O_P_P(
			long companyId, long groupId, long ownerId, int ownerType,
			String portletId, boolean privateLayout) {

		return getFinder().findByC_G_O_O_P_P(
			companyId, groupId, ownerId, ownerType, portletId, privateLayout);
	}

	public static PortletPreferencesFinder getFinder() {
		if (_finder == null) {
			_finder = (PortletPreferencesFinder)PortalBeanLocatorUtil.locate(
				PortletPreferencesFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(PortletPreferencesFinder finder) {
		_finder = finder;
	}

	private static PortletPreferencesFinder _finder;

}
// LIFERAY-SERVICE-BUILDER-HASH:-151890310