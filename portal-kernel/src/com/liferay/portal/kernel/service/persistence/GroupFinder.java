/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.service.persistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@ProviderType
public interface GroupFinder {

	public int countByLayouts(long companyId, long parentGroupId, boolean site);

	public int countByLayouts(
		long companyId, long parentGroupId, boolean site, Boolean active);

	public int countByG_U(long groupId, long userId, boolean inherit);

	public int countByC_C_PG_N_D(
		long companyId, long[] classNameIds, long parentGroupId, String[] names,
		String[] descriptions, java.util.LinkedHashMap<String, Object> params,
		boolean andOperator);

	public com.liferay.portal.kernel.model.Group fetchByC_GK(
			long companyId, String groupKey)
		throws com.liferay.portal.kernel.exception.NoSuchGroupException;

	public java.util.List<com.liferay.portal.kernel.model.Group>
		findByCompanyId(
			long companyId, java.util.LinkedHashMap<String, Object> params,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Group> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.Group> findByLayouts(
		long companyId, long parentGroupId, boolean site, Boolean active,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<com.liferay.portal.kernel.model.Group> orderByComparator);

	public java.util.List<com.liferay.portal.kernel.model.Group>
		findByLiveGroups();

	public java.util.List<Long> findByC_A(long companyId, boolean active);

	public java.util.List<com.liferay.portal.kernel.model.Group>
		findByL_TS_S_RSGC(
			long liveGroupId, String typeSettings, boolean site,
			int remoteStagingGroupCount);

	public java.util.List<com.liferay.portal.kernel.model.Group>
		findByC_C_PG_N_D(
			long companyId, long[] classNameIds, long parentGroupId,
			String[] names, String[] descriptions,
			java.util.LinkedHashMap<String, Object> params, boolean andOperator,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.Group> orderByComparator);

	public java.util.Map<Long, long[]> findByC_C_S_A_UserInheritedGroupIds(
		long companyId);

}
// LIFERAY-SERVICE-BUILDER-HASH:1824860953