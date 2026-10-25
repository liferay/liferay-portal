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
public class GroupFinderUtil {

	public static int countByLayouts(
		long companyId, long parentGroupId, boolean site) {

		return getFinder().countByLayouts(companyId, parentGroupId, site);
	}

	public static int countByLayouts(
		long companyId, long parentGroupId, boolean site, Boolean active) {

		return getFinder().countByLayouts(
			companyId, parentGroupId, site, active);
	}

	public static int countByG_U(long groupId, long userId, boolean inherit) {
		return getFinder().countByG_U(groupId, userId, inherit);
	}

	public static int countByC_C_PG_N_D(
		long companyId, long[] classNameIds, long parentGroupId, String[] names,
		String[] descriptions, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator) {

		return getFinder().countByC_C_PG_N_D(
			companyId, classNameIds, parentGroupId, names, descriptions, params,
			andOperator);
	}

	public static com.liferay.portal.kernel.model.Group fetchByC_GK(
			long companyId, String groupKey)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException {

		return getFinder().fetchByC_GK(companyId, groupKey);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Group>
		findByCompanyId(
			long companyId, java.util.LinkedHashMap<String, Object> params,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Group> orderByComparator) {

		return getFinder().findByCompanyId(
			companyId, params, start, end, orderByComparator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Group>
		findByLayouts(
			long companyId, long parentGroupId, boolean site, Boolean active,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Group> orderByComparator) {

		return getFinder().findByLayouts(
			companyId, parentGroupId, site, active, start, end,
			orderByComparator);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Group>
		findByLiveGroups() {

		return getFinder().findByLiveGroups();
	}

	public static java.util.List<Long> findByC_A(
		long companyId, boolean active) {

		return getFinder().findByC_A(companyId, active);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Group>
		findByL_TS_S_RSGC(
			long liveGroupId, String typeSettings, boolean site,
			int remoteStagingGroupCount) {

		return getFinder().findByL_TS_S_RSGC(
			liveGroupId, typeSettings, site, remoteStagingGroupCount);
	}

	public static java.util.List<com.liferay.portal.kernel.model.Group>
		findByC_C_PG_N_D(
			long companyId, long[] classNameIds, long parentGroupId,
			String[] names, String[] descriptions,
			java.util.LinkedHashMap<String, Object> params, boolean andOperator,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Group> orderByComparator) {

		return getFinder().findByC_C_PG_N_D(
			companyId, classNameIds, parentGroupId, names, descriptions, params,
			andOperator, start, end, orderByComparator);
	}

	public static java.util.Map<Long, long[]>
		findByC_C_S_A_UserInheritedGroupIds(long companyId) {

		return getFinder().findByC_C_S_A_UserInheritedGroupIds(companyId);
	}

	public static GroupFinder getFinder() {
		if (_finder == null) {
			_finder = (GroupFinder)PortalBeanLocatorUtil.locate(
				GroupFinder.class.getName());
		}

		return _finder;
	}

	public void setFinder(GroupFinder finder) {
		_finder = finder;
	}

	private static GroupFinder _finder;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1059314770