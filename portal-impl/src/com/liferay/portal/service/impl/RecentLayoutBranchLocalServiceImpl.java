/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.RecentLayoutBranch;
import com.liferay.portal.kernel.service.persistence.LayoutBranchPersistence;
import com.liferay.portal.service.base.RecentLayoutBranchLocalServiceBaseImpl;

/**
 * @author Brian Wing Shun Chan
 * @author Preston Crary
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class RecentLayoutBranchLocalServiceImpl
	extends RecentLayoutBranchLocalServiceBaseImpl {

	@Override
	public RecentLayoutBranch addRecentLayoutBranch(
			long userId, long layoutBranchId, long layoutSetBranchId, long plid)
		throws PortalException {

		LayoutBranch layoutBranch = _layoutBranchPersistence.findByPrimaryKey(
			layoutBranchId);

		RecentLayoutBranch recentLayoutBranch =
			recentLayoutBranchPersistence.create(
				counterLocalService.increment());

		recentLayoutBranch.setGroupId(layoutBranch.getGroupId());
		recentLayoutBranch.setCompanyId(layoutBranch.getCompanyId());
		recentLayoutBranch.setUserId(userId);
		recentLayoutBranch.setLayoutBranchId(layoutBranchId);
		recentLayoutBranch.setLayoutSetBranchId(layoutSetBranchId);
		recentLayoutBranch.setPlid(plid);

		return recentLayoutBranchPersistence.update(recentLayoutBranch);
	}

	@Override
	public void deleteRecentLayoutBranches(long layoutBranchId) {
		recentLayoutBranchPersistence.removeByLayoutBranchId(layoutBranchId);
	}

	@Override
	public void deleteUserRecentLayoutBranches(long userId) {
		recentLayoutBranchPersistence.removeByUserId(userId);
	}

	@Override
	public RecentLayoutBranch fetchRecentLayoutBranch(
		long userId, long layoutSetBranchId, long plid) {

		return recentLayoutBranchPersistence.fetchByU_L_P(
			userId, layoutSetBranchId, plid);
	}

	@BeanReference(type = LayoutBranchPersistence.class)
	private LayoutBranchPersistence _layoutBranchPersistence;

}