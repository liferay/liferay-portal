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
public class PasswordPolicyFinderUtil {

	public static int filterCountByC_N(long companyId, String name) {
		return getFinder().filterCountByC_N(companyId, name);
	}

	public static java.util.List<com.liferay.portal.kernel.model.PasswordPolicy>
		filterFindByC_N(
			long companyId, String name, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.PasswordPolicy>
					orderByComparator) {

		return getFinder().filterFindByC_N(
			companyId, name, start, end, orderByComparator);
	}

	public static PasswordPolicyFinder getFinder() {
		if (_finder == null) {
			_finder = (PasswordPolicyFinder)PortalBeanLocatorUtil.locate(
				PasswordPolicyFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(PasswordPolicyFinder finder) {
		_finder = finder;
	}

	private static PasswordPolicyFinder _finder;

}
// LIFERAY-SERVICE-BUILDER-HASH:1555389263