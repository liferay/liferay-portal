/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutSetBranchPermissionUtil;
import com.liferay.portal.kernel.util.comparator.LayoutSetBranchCreateDateComparator;
import com.liferay.portal.service.base.LayoutSetBranchServiceBaseImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Raymond Augé
 * @author Brian Wing Shun Chan
 */
public class LayoutSetBranchServiceImpl extends LayoutSetBranchServiceBaseImpl {

	@Override
	public LayoutSetBranch addLayoutSetBranch(
			long groupId, boolean privateLayout, String name,
			String description, boolean master, long copyLayoutSetBranchId,
			ServiceContext serviceContext)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.ADD_LAYOUT_SET_BRANCH);

		return layoutSetBranchLocalService.addLayoutSetBranch(
			getUserId(), groupId, privateLayout, name, description, master,
			copyLayoutSetBranchId, serviceContext);
	}

	@Override
	public void deleteLayoutSetBranch(long layoutSetBranchId)
		throws PortalException {

		LayoutSetBranchPermissionUtil.check(
			getPermissionChecker(), layoutSetBranchId, ActionKeys.DELETE);

		layoutSetBranchLocalService.deleteLayoutSetBranch(layoutSetBranchId);
	}

	@Override
	public void deleteLayoutSetBranch(
			long currentLayoutPlid, long layoutSetBranchId)
		throws PortalException {

		LayoutSetBranchPermissionUtil.check(
			getPermissionChecker(), layoutSetBranchId, ActionKeys.DELETE);

		layoutSetBranchLocalService.deleteLayoutSetBranch(
			currentLayoutPlid, layoutSetBranchId);
	}

	@Override
	public List<LayoutSetBranch> getLayoutSetBranches(
		long groupId, boolean privateLayout) {

		try {
			if (GroupPermissionUtil.contains(
					getPermissionChecker(), groupId, ActionKeys.VIEW_STAGING)) {

				return layoutSetBranchPersistence.findByG_P(
					groupId, privateLayout, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS,
					LayoutSetBranchCreateDateComparator.getInstance(true));
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to get layout set branches for group ", groupId,
						" with ", privateLayout ? "private" : "public",
						" layouts"),
					portalException);
			}
		}

		return new ArrayList<>();
	}

	@Override
	public LayoutSetBranch mergeLayoutSetBranch(
			long layoutSetBranchId, long mergeLayoutSetBranchId,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutSetBranchPermissionUtil.check(
			getPermissionChecker(), layoutSetBranchId, ActionKeys.UPDATE);

		return layoutSetBranchLocalService.mergeLayoutSetBranch(
			layoutSetBranchId, mergeLayoutSetBranchId, serviceContext);
	}

	@Override
	public LayoutSetBranch updateLayoutSetBranch(
			long groupId, long layoutSetBranchId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException {

		LayoutSetBranchPermissionUtil.check(
			getPermissionChecker(), layoutSetBranchId, ActionKeys.UPDATE);

		return layoutSetBranchLocalService.updateLayoutSetBranch(
			layoutSetBranchId, name, description, serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetBranchServiceImpl.class);

}