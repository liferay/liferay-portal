/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.model.RecentLayoutSetBranch;
import com.liferay.portal.kernel.service.persistence.LayoutSetBranchPersistence;
import com.liferay.portal.service.base.RecentLayoutSetBranchLocalServiceBaseImpl;

/**
 * @author Brian Wing Shun Chan
 * @author Preston Crary
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class RecentLayoutSetBranchLocalServiceImpl
	extends RecentLayoutSetBranchLocalServiceBaseImpl {

	@Override
	public RecentLayoutSetBranch addRecentLayoutSetBranch(
			long userId, long layoutSetBranchId, long layoutSetId)
		throws PortalException {

		LayoutSetBranch layoutSetBranch =
			_layoutSetBranchPersistence.findByPrimaryKey(layoutSetBranchId);

		RecentLayoutSetBranch recentLayoutSetBranch =
			recentLayoutSetBranchPersistence.create(
				counterLocalService.increment());

		recentLayoutSetBranch.setGroupId(layoutSetBranch.getGroupId());
		recentLayoutSetBranch.setCompanyId(layoutSetBranch.getCompanyId());
		recentLayoutSetBranch.setUserId(userId);
		recentLayoutSetBranch.setLayoutSetBranchId(layoutSetBranchId);
		recentLayoutSetBranch.setLayoutSetId(layoutSetId);

		return recentLayoutSetBranchPersistence.update(recentLayoutSetBranch);
	}

	@Override
	public void deleteRecentLayoutSetBranches(long layoutSetBranchId) {
		recentLayoutSetBranchPersistence.removeByLayoutSetBranchId(
			layoutSetBranchId);
	}

	@Override
	public void deleteUserRecentLayoutSetBranches(long userId) {
		recentLayoutSetBranchPersistence.removeByUserId(userId);
	}

	@Override
	public RecentLayoutSetBranch fetchRecentLayoutSetBranch(
		long userId, long layoutSetId) {

		return recentLayoutSetBranchPersistence.fetchByU_L(userId, layoutSetId);
	}

	@BeanReference(type = LayoutSetBranchPersistence.class)
	private LayoutSetBranchPersistence _layoutSetBranchPersistence;

}