/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.LayoutBranch;
import com.liferay.portal.kernel.model.LayoutRevision;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.service.permission.LayoutBranchPermissionUtil;
import com.liferay.portal.kernel.service.persistence.LayoutRevisionPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetBranchPersistence;
import com.liferay.portal.service.base.LayoutBranchServiceBaseImpl;

/**
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 * @deprecated As of Cavanaugh (7.4.x), with no direct replacement
 */
@Deprecated
public class LayoutBranchServiceImpl extends LayoutBranchServiceBaseImpl {

	@Override
	public LayoutBranch addLayoutBranch(
			long layoutRevisionId, String name, String description,
			boolean master, ServiceContext serviceContext)
		throws PortalException {

		FeatureFlagManagerUtil.checkEnabled(
			serviceContext.getCompanyId(), "LPD-105778");

		PermissionChecker permissionChecker = getPermissionChecker();

		GroupPermissionUtil.check(
			permissionChecker, serviceContext.getScopeGroupId(),
			ActionKeys.ADD_LAYOUT_BRANCH);

		LayoutRevision layoutRevision =
			_layoutRevisionPersistence.findByPrimaryKey(layoutRevisionId);

		LayoutSetBranch layoutSetBranch =
			_layoutSetBranchPersistence.findByPrimaryKey(
				layoutRevision.getLayoutSetBranchId());

		GroupPermissionUtil.check(
			permissionChecker, layoutSetBranch.getGroupId(),
			ActionKeys.ADD_LAYOUT_BRANCH);

		return layoutBranchLocalService.addLayoutBranch(
			layoutRevisionId, name, description, false, serviceContext);
	}

	@Override
	public void deleteLayoutBranch(long layoutBranchId) throws PortalException {
		LayoutBranchPermissionUtil.check(
			getPermissionChecker(), layoutBranchId, ActionKeys.DELETE);

		layoutBranchLocalService.deleteLayoutBranch(layoutBranchId);
	}

	@Override
	public LayoutBranch updateLayoutBranch(
			long layoutBranchId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException {

		LayoutBranchPermissionUtil.check(
			getPermissionChecker(), layoutBranchId, ActionKeys.UPDATE);

		return layoutBranchLocalService.updateLayoutBranch(
			layoutBranchId, name, description, serviceContext);
	}

	@BeanReference(type = LayoutRevisionPersistence.class)
	private LayoutRevisionPersistence _layoutRevisionPersistence;

	@BeanReference(type = LayoutSetBranchPersistence.class)
	private LayoutSetBranchPersistence _layoutSetBranchPersistence;

}