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
public class UserGroupRoleFinderUtil {

	public static java.util.List<com.liferay.portal.kernel.model.UserGroupRole>
		findByGroupRoleType(long groupId, int roleType) {

		return getFinder().findByGroupRoleType(groupId, roleType);
	}

	public static UserGroupRoleFinder getFinder() {
		if (_finder == null) {
			_finder = (UserGroupRoleFinder)PortalBeanLocatorUtil.locate(
				UserGroupRoleFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(UserGroupRoleFinder finder) {
		_finder = finder;
	}

	private static UserGroupRoleFinder _finder;

}
// LIFERAY-SERVICE-BUILDER-HASH:-42836068