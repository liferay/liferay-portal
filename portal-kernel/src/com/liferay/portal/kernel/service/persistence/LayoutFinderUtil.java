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
public class LayoutFinderUtil {

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
		findByScopeGroup(long groupId) {

		return getFinder().findByScopeGroup(groupId);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Layout>
		findByScopeGroup(long groupId, boolean privateLayout) {

		return getFinder().findByScopeGroup(groupId, privateLayout);
	}

	public static java.util.List
		<com.liferay.portal.kernel.model.LayoutReference> findByC_P_P(
			long companyId, String portletId, String preferencesKey,
			String preferencesValue) {

		return getFinder().findByC_P_P(
			companyId, portletId, preferencesKey, preferencesValue);
	}

	public static LayoutFinder getFinder() {
		if (_finder == null) {
			_finder = (LayoutFinder)PortalBeanLocatorUtil.locate(
				LayoutFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(LayoutFinder finder) {
		_finder = finder;
	}

	private static LayoutFinder _finder;

}
// LIFERAY-SERVICE-BUILDER-HASH:1540053891